package com.example.yogarena;

import android.Manifest; // Required for camera permission checks
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Canvas; // Needed for padBitmap
import android.graphics.Color; // Needed for padBitmap
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler; // For timer/progress bar updates
import android.os.Looper;  // For timer/progress bar updates
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton; // If you use pause_button
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment; // For navigation
import com.google.android.material.progressindicator.LinearProgressIndicator; // Import Progress Bar
import com.google.common.util.concurrent.ListenableFuture;
import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder; // Needed for ByteBuffer
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InGameScreen extends Fragment {

    // Views from XML (ingame_screen_main.xml)
    private PreviewView miniPreviewView;
    private ImageView targetPoseImageView;
    private LinearProgressIndicator sessionProgressBar;
    private ImageButton pauseButton; // Optional, find if needed

    // Camera & ML
    private Interpreter poseDetector;        // MoveNet
    private Interpreter imageClassifier;    // Your image classification model
    private YogaPoseClassifier yogaPoseClassifier; // Classifier helper class
    private ExecutorService cameraExecutor;
    private YuvToRgbConverter converter;
    private TextView accuracyTextView;// Assumes you have this class

    // Routine Data
    private List<String> routinePoseIds;
    private int currentPoseIndex = 0;
    private List<String> allPoseLabels; // Full list for the classifier

    // State & Constants
    private static final int MOVENET_INPUT_SIZE = 192;
    private static final int MOBILENET_INPUT_SIZE_H = 224;
    private static final int MOBILENET_INPUT_SIZE_W = 224;
    private static final String TAG = "InGameScreen"; // Changed TAG
    private static final float CLASSIFICATION_THRESHOLD = 0.7f;
    private static final long REQUIRED_POSE_HOLD_MS = 3000;
    private long poseStartTime = 0;
    private final Handler progressHandler = new Handler(Looper.getMainLooper());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this screen
        return inflater.inflate(R.layout.ingame_screen_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Initialize converter and executor
        converter = new YuvToRgbConverter(requireContext());
        cameraExecutor = Executors.newSingleThreadExecutor();

        // --- Find Views ---
        miniPreviewView = view.findViewById(R.id.mini_preview_view);
        targetPoseImageView = view.findViewById(R.id.sample_image);
        sessionProgressBar = view.findViewById(R.id.session_progress_bar);
        pauseButton = view.findViewById(R.id.pause_button); // Find the pause button
        accuracyTextView = view.findViewById(R.id.accuracy_text_view);

        // --- Basic View Setup ---
        if (miniPreviewView != null) {
            miniPreviewView.setScaleType(PreviewView.ScaleType.FIT_CENTER);
        } else {
            Log.e(TAG, "mini_preview_view not found in layout!");
        }
        if (sessionProgressBar != null) {
            sessionProgressBar.setMax((int) REQUIRED_POSE_HOLD_MS);
            sessionProgressBar.setProgress(0);
        } else {
            Log.e(TAG, "session_progress_bar not found in layout!");
        }

        // Pause button listener (example)
        if(pauseButton != null) {
            pauseButton.setOnClickListener(v -> {
                // Implement pause logic here
                Toast.makeText(getContext(), "Pause Clicked", Toast.LENGTH_SHORT).show();
                // Maybe stop cameraExecutor or pause analysis?
            });
        }

        // --- Load Models ---
        try {
            poseDetector = new Interpreter(loadModelFile(requireContext(), "movenet.tflite"));
            imageClassifier = new Interpreter(loadModelFile(requireContext(), "pose_classifier.tflite"));
            initializePoseLabels(); // Load all labels

            // Initialize the classifier class
            if (imageClassifier != null && allPoseLabels != null && !allPoseLabels.isEmpty()) {
                yogaPoseClassifier = new YogaPoseClassifier(imageClassifier, allPoseLabels, MOBILENET_INPUT_SIZE_W, MOBILENET_INPUT_SIZE_H);
                Log.d(TAG, "YogaPoseClassifier initialized.");
            } else {
                Log.e(TAG, "Failed to initialize YogaPoseClassifier.");
                Toast.makeText(getContext(), "Classifier init failed.", Toast.LENGTH_SHORT).show();
            }

        } catch (IOException e) {
            Log.e(TAG, "Failed to load models", e);
            Toast.makeText(getContext(), "Error loading ML models.", Toast.LENGTH_SHORT).show();
            return; // Exit if models fail
        }
        // --- End Load Models ---


        // --- Get Routine from Arguments ---
        loadRoutineFromArguments();
        // --- End Get Routine ---

        // Start camera only if permission is granted
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            startCameraFront(); // Binds preview and starts analysis
        } else {
            Log.e(TAG, "Camera permission not granted!");
            Toast.makeText(getContext(), "Camera permission needed!", Toast.LENGTH_LONG).show();
            // Consider requesting permission or navigating back
        }

    }

    // --- Load Routine Helper ---
    private void loadRoutineFromArguments() {
        if (getArguments() != null) {
            // *** FIX: Use getStringArray and convert to List ***
            String[] selectedIdsArray = getArguments().getStringArray("selectedPoseIds");
            if (selectedIdsArray == null || selectedIdsArray.length == 0) {
                Log.e(TAG, "Routine is empty or null!");
                routinePoseIds = new ArrayList<>();
                Toast.makeText(getContext(),"Error: No routine loaded.", Toast.LENGTH_SHORT).show();
            } else {
                // Convert array to ArrayList
                routinePoseIds = new ArrayList<>(Arrays.asList(selectedIdsArray));
                Log.d(TAG, "Starting routine with " + routinePoseIds.size() + " poses.");
                currentPoseIndex = 0;
                updateTargetPoseUI();
            }
        } else {
            Log.e(TAG, "No routine arguments received!");
            routinePoseIds = new ArrayList<>();
            Toast.makeText(getContext(),"Error: No routine data.", Toast.LENGTH_SHORT).show();
        }
    }

    // --- Helper to load all pose labels ---
    private void initializePoseLabels() {
        // You MUST load the exact list of labels your imageClassifier was trained with, in the correct order.
        allPoseLabels = Arrays.asList( /* PASTE YOUR FULL LIST OF 80+ LABELS HERE */
                "Akarna_Dhanurasana", "Bharadvaja's_Twist_pose_or_Bharadvajasana_I_",
                "Boat_Pose_or_Paripurna_Navasana_", "Bound_Angle_Pose_or_Baddha_Konasana_",
                "Bow_Pose_or_Dhanurasana_", "Bridge_Pose_or_Setu_Bandha_Sarvangasana_",
                "Camel_Pose_or_Ustrasana_", "Cat_Cow_Pose_or_Marjaryasana_",
                "Chair_Pose_or_Utkatasana_", "Child_Pose_or_Balasana_",
                "Cobra_Pose_or_Bhujangasana_", "Cockerel_Pose",
                "Corpse_Pose_or_Savasana_", "Cow_Face_Pose_or_Gomukhasana_",
                "Crane_(Crow)_Pose_or_Bakasana_", "Dolphin_Plank_Pose_or_Makara_Adho_Mukha_Svanasana_",
                "Dolphin_Pose_or_Ardha_Pincha_Mayurasana_",
                "Downward-Facing_Dog_pose_or_Adho_Mukha_Svanasana_", "Eagle_Pose_or_Garudasana_",
                "Eight-Angle_Pose_or_Astavakrasana_", "Extended_Puppy_Pose_or_Uttana_Shishosana_",
                "Extended_Revolved_Side_Angle_Pose_or_Utthita_Parsvakonasana_",
                "Extended_Revolved_Triangle_Pose_or_Utthita_Trikonasana_",
                "Feathered_Peacock_Pose_or_Pincha_Mayurasana_", "Firefly_Pose_or_Tittibhasana_",
                "Fish_Pose_or_Matsyasana_", "Four-Limbed_Staff_Pose_or_Chaturanga_Dandasana_",
                "Frog_Pose_or_Bhekasana", "Garland_Pose_or_Malasana_", "Gate_Pose_or_Parighasana_",
                "Half_Lord_of_the_Fishes_Pose_or_Ardha_Matsyendrasana_",
                "Half_Moon_Pose_or_Ardha_Chandrasana_", "Handstand_pose_or_Adho_Mukha_Vrksasana_",
                "Happy_Baby_Pose_or_Ananda_Balasana_", "Head-to-Knee_Forward_Bend_pose_or_Janu_Sirsasana_",
                "Heron_Pose_or_Krounchasana_", "Intense_Side_Stretch_Pose_or_Parsvottanasana_",
                "Legs-Up-the-Wall_Pose_or_Viparita_Karani_", "Locust_Pose_or_Salabhasana_",
                "Lord_of_the_Dance_Pose_or_Natarajasana_", "Low_Lunge_pose_or_Anjaneyasana_",
                "Noose_Pose_or_Pasasana_", "Peacock_Pose_or_Mayurasana_",
                "Pigeon_Pose_or_Kapotasana_", "Plank_Pose_or_Kumbhakasana_", "Plow_Pose_or_Halasana_",
                "Pose_Dedicated_to_the_Sage_Koundinya_or_Eka_Pada_Koundinyanasana_I_and_II",
                "Rajakapotasana", "Reclining_Hand-to-Big-Toe_Pose_or_Supta_Padangusthasana_",
                "Revolved_Head-to-Knee_Pose_or_Parivrtta_Janu_Sirsasana_",
                "Scale_Pose_or_Tolasana_", "Scorpion_pose_or_vrischikasana",
                "Seated_Forward_Bend_pose_or_Paschimottanasana_",
                "Shoulder-Pressing_Pose_or_Bhujapidasana_", "Side-Reclining_Leg_Lift_pose_or_Anantasana_",
                "Side_Crane_(Crow)_Pose_or_Parsva_Bakasana_", "Side_Plank_Pose_or_Vasisthasana_",
                "Sitting_pose_1_(normal)", "Split_pose", "Staff_Pose_or_Dandasana_",
                "Standing_Forward_Bend_pose_or_Uttanasana_",
                "Standing_Split_pose_or_Urdhva_Prasarita_Eka_Padasana_",
                "Standing_big_toe_hold_pose_or_Utthita_Padangusthasana",
                "Supported_Headstand_pose_or_Salamba_Sirsasana_",
                "Supported_Shoulderstand_pose_or_Salamba_Sarvangasana_", "Supta_Baddha_Konasana_",
                "Supta_Virasana_Vajrasana", "Tortoise_Pose", "Tree_Pose_or_Vrksasana_",
                "Upward_Bow_(Wheel)_Pose_or_Urdhva_Dhanurasana_",
                "Upward_Facing_Two-Foot_Staff_Pose_or_Dwi_Pada_Viparita_Dandasana_",
                "Upward_Plank_Pose_or_Purvottanasana_", "Virasana_or_Vajrasana",
                "Warrior_III_Pose_or_Virabhadrasana_III_", "Warrior_II_Pose_or_Virabhadrasana_II_",
                "Warrior_I_Pose_or_Virabhadrasana_I_",
                "Wide-Angle_Seated_Forward_Bend_pose_or_Upavistha_Konasana_",
                "Wide-Legged_Forward_Bend_pose_or_Prasarita_Padottanasana_",
                "Wild_Thing_pose_or_Camatkarasana_", "Wind_Relieving_pose_or_Pawanmuktasana",
                "Yogic_sleep_pose", "viparita_virabhadrasana_or_reverse_warrior_pose"
        );
        // Optional verification logic can stay here
        if (imageClassifier != null) {
            try {
                int[] outputShape = imageClassifier.getOutputTensor(0).shape();
                if (outputShape.length < 2 || outputShape[1] != allPoseLabels.size()) {
                    Log.e(TAG, "Model output size (" + (outputShape.length > 1 ? outputShape[1] : 0) + ") doesn't match label count (" + allPoseLabels.size() + ")");
                    Toast.makeText(getContext(), "Classifier/Label Mismatch!", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e){
                Log.e(TAG, "Error getting classifier output shape", e);
            }
        }
    }

    // --- Camera Setup ---
    private void startCameraFront() {
        if (miniPreviewView == null) { Log.e(TAG, "Cannot start camera, miniPreviewView is null."); return; }
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext());
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                Preview preview = new Preview.Builder().setTargetAspectRatio(AspectRatio.RATIO_4_3).build();
                preview.setSurfaceProvider(miniPreviewView.getSurfaceProvider()); // Bind to mini view
                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();
                imageAnalysis.setAnalyzer(cameraExecutor, this::analyzeImage); // Set analyzer
                CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build();
                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(getViewLifecycleOwner(), cameraSelector, preview, imageAnalysis);
                Log.d(TAG, "Camera started successfully.");
            } catch (Exception e) { Log.e(TAG, "Camera binding failed", e); }
        }, ContextCompat.getMainExecutor(requireContext()));
    }


    // --- Image Analysis ---
    private void analyzeImage(@NonNull ImageProxy imageProxy) {
        Bitmap bitmap = converter.yuvToRgb(imageProxy);
        if (bitmap == null) { imageProxy.close(); return; }

        Bitmap rotatedBitmap = rotateBitmapIfNeeded(bitmap, imageProxy.getImageInfo().getRotationDegrees());
        Bitmap processedBitmap = flipBitmap(rotatedBitmap); // Assume front camera

        // --- CLASSIFICATION ---
        if (yogaPoseClassifier != null && yogaPoseClassifier.isReady() && routinePoseIds != null && currentPoseIndex < routinePoseIds.size()) {
            String targetPoseId = routinePoseIds.get(currentPoseIndex);

            // Classify the current frame against the target pose
            float confidence = yogaPoseClassifier.classify(processedBitmap, targetPoseId);

            handleClassificationResult(confidence, targetPoseId);

        } else if (routinePoseIds == null || routinePoseIds.isEmpty()) {
            Log.d(TAG, "No routine loaded, skipping classification.");
        }
        // --- END CLASSIFICATION ---

        imageProxy.close();
    }


    // --- Handle Classification Result & Timer ---
    // --- Handle Classification Result & Timer ---
    private void handleClassificationResult(float confidence, String targetPoseId) {
        final String simpleTargetName = getSimplePoseName(targetPoseId);
        int progress = 0;
        final String accuracyText; // Declare here

        if (confidence < 0) { // Error case
            poseStartTime = 0;
            progress = 0;
            accuracyText = "Accuracy: Error"; // Assign error text
            Log.w(TAG, "Classification returned error for " + targetPoseId);
        } else {
            // *** FIX: Assign the formatted text here for all non-error cases ***
            accuracyText = String.format("Accuracy: %.1f%%", confidence * 100);

            // Now handle the timer/progress logic based on the threshold
            if (confidence > CLASSIFICATION_THRESHOLD) {
                if (poseStartTime == 0) { // Start timer
                    poseStartTime = System.currentTimeMillis();
                    progress = 0;
                    Log.d(TAG, "Pose match started: " + simpleTargetName);
                } else { // Timer already running
                    long timeHeld = System.currentTimeMillis() - poseStartTime;
                    progress = (int) Math.min(timeHeld, REQUIRED_POSE_HOLD_MS);
                    if (timeHeld >= REQUIRED_POSE_HOLD_MS) {
                        Log.d(TAG, "Pose held successfully: " + simpleTargetName);
                        advanceToNextPose();
                        // Resetting timer and progress happens in updateTargetPoseUI
                    }
                }
            } else { // No match or confidence too low
                poseStartTime = 0;
                progress = 0;
            }
        }

        // Update Progress Bar AND Accuracy Text
        final int finalProgress = progress;
        progressHandler.post(() -> {
            if (sessionProgressBar != null) {
                sessionProgressBar.setProgress(finalProgress, true); // Animate progress
            }
            // --- SET ACCURACY TEXT ---
            if (accuracyTextView != null) {
                // Now accuracyText is guaranteed to have a value
                accuracyTextView.setText(accuracyText);
            }
            // --- END SET ACCURACY TEXT ---
        });
    }


    // --- Routine Advancement Logic ---
    private void advanceToNextPose() {
        currentPoseIndex++;
        if (currentPoseIndex >= routinePoseIds.size()) {
            // Routine finished!
            if (!isStateSaved() && isAdded() && getActivity() != null) { // Check fragment state
                getActivity().runOnUiThread(() -> { // Ensure UI ops on main thread
                    Log.d(TAG, "Routine finished!");
                    Toast.makeText(getContext(), "Routine Complete!", Toast.LENGTH_LONG).show();
                    try {
                        NavHostFragment.findNavController(InGameScreen.this).popBackStack(); // Example: Go back
                    } catch (IllegalStateException e) {
                        Log.e(TAG, "Error navigating back after routine completion", e);
                    }
                });
            }
            // Reset to loop or stop?
            // currentPoseIndex = 0; // Loop for now

        } else {
            Log.d(TAG, "Advanced to pose index: " + currentPoseIndex + " (" + routinePoseIds.get(currentPoseIndex) + ")");
            updateTargetPoseUI(); // Update image and reset timer/progress
        }
    }

    // --- Update Target Pose Image ---
    private void updateTargetPoseUI() {
        if (routinePoseIds == null || currentPoseIndex >= routinePoseIds.size()) return;

        String targetPoseId = routinePoseIds.get(currentPoseIndex);
        PoseInfo targetPoseInfo = PoseMasterList.getInfo(targetPoseId);

        if (getActivity() != null) { // Check if fragment is attached
            getActivity().runOnUiThread(() -> {
                if (targetPoseImageView != null && targetPoseInfo != null) {
                    targetPoseImageView.setImageResource(targetPoseInfo.getImageResourceId());
                    Log.d(TAG, "Set target image for: " + targetPoseInfo.getSimpleName());
                } else if (targetPoseImageView != null){
                    Log.w(TAG, "PoseInfo not found for ID: " + targetPoseId + ", cannot set image.");
                    // targetPoseImageView.setImageResource(R.drawable.ic_placeholder); // Set a placeholder if you have one
                }

                // Reset timer and progress bar for the new pose
                poseStartTime = 0;
                if (sessionProgressBar != null) {
                    sessionProgressBar.setProgress(0, false); // Reset progress without animation
                }
                if (accuracyTextView != null) {
                    accuracyTextView.setText("Accuracy: --%"); // Reset text for new pose
                }
                Toast.makeText(getContext(), "Next: " + getSimplePoseName(targetPoseId), Toast.LENGTH_SHORT).show();
            });
        }
    }

    // --- Utility Methods ---
    // Make sure these helpers are included and correct
    private static MappedByteBuffer loadModelFile(Context context, String modelPath) throws IOException {
        AssetFileDescriptor fileDescriptor = context.getAssets().openFd(modelPath);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        MappedByteBuffer buffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
        // IMPORTANT: Close resources properly
        fileChannel.close();
        inputStream.close();
        fileDescriptor.close();
        return buffer;
    }

    private Bitmap rotateBitmapIfNeeded(Bitmap bitmap, int rotationDegrees) {
        if (bitmap == null) return null; // Add null check
        if (rotationDegrees == 0) return bitmap;
        Matrix matrix = new Matrix();
        matrix.postRotate(rotationDegrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    private Bitmap flipBitmap(Bitmap src) {
        if (src == null) return null; // Add null check
        Matrix matrix = new Matrix();
        matrix.preScale(-1.0f, 1.0f);
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
    }

    // Include padBitmap, unpadKeypoints if using MoveNet results later
    // Include convertBitmapToByteBufferMovenet if needed by padBitmap

    private String getSimplePoseName(String poseId){
        if (poseId == null) return "Unknown";
        PoseInfo info = PoseMasterList.getInfo(poseId);
        return info != null ? info.getSimpleName() : poseId; // Fallback to ID
    }


    // --- Lifecycle ---
    @Override
    public void onDestroyView() { // Use onDestroyView for fragment view cleanup
        super.onDestroyView();
        // Shut down executor first
        if (cameraExecutor != null) {
            cameraExecutor.shutdown();
            cameraExecutor = null;
        }
        // Close interpreters
        if (poseDetector != null) {
            poseDetector.close();
            poseDetector = null;
        }
        if (imageClassifier != null) {
            imageClassifier.close();
            imageClassifier = null;
        }
        // Clean up handler
        progressHandler.removeCallbacksAndMessages(null);
        Log.d(TAG, "Cleaned up resources in onDestroyView.");
    }

}