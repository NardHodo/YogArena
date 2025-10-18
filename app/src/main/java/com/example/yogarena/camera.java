package com.example.yogarena;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.common.util.concurrent.ListenableFuture;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class camera extends Fragment {

    private PreviewView viewFinder;

    private PoseOverlayView overlay;

    private androidx.camera.view.PreviewView previewView;
    private LinearLayout feedbackBar;
    private TextView feedbackText;
    private ImageView backButton;
    private Interpreter poseDetector;
    private ExecutorService cameraExecutor;

    private static final int MOVENET_INPUT_SIZE = 192;
    private static final String TAG = "CameraFragment";

    private static final float MIN_CONFIDENCE = 0.3f;
    private static final float MAX_HEIGHT_RATIO = 0.80f;
    private static final float MAX_Y_OFFSET = 0.7f;


    private static final float MIN_HEIGHT_RATIO = 0.45f;  // Too far if smaller than this
    //private static final float MAX_HEIGHT_RATIO = 0.80f;  // Too near if bigger than this

    private YuvToRgbConverter converter;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_camera, container, false);
        previewView = root.findViewById(R.id.viewFinder);
        overlay = root.findViewById(R.id.overlay);


        feedbackBar = root.findViewById(R.id.feedbackBar);
        feedbackText = root.findViewById(R.id.feedbackText);
        backButton = root.findViewById(R.id.back_Button);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        converter = new YuvToRgbConverter(requireContext());
        cameraExecutor = Executors.newSingleThreadExecutor();

        previewView.setScaleType(PreviewView.ScaleType.FIT_CENTER);
        overlay.setPreviewView(previewView);
        try {
            poseDetector = new Interpreter(loadModelFile("movenet.tflite"));
        } catch (IOException e) {
            Log.e(TAG, "Failed to load MoveNet model", e);
        }

        startCameraFront();
    }

    private MappedByteBuffer loadModelFile(String modelPath) throws IOException {
        AssetFileDescriptor fileDescriptor = requireContext().getAssets().openFd(modelPath);
        try (FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor())) {
            FileChannel fileChannel = inputStream.getChannel();
            long startOffset = fileDescriptor.getStartOffset();
            long declaredLength = fileDescriptor.getDeclaredLength();
            return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
        } finally {
            fileDescriptor.close();
        }
    }

    private void startCameraFront() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(requireContext());

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                Preview preview = new Preview.Builder()
                        // !!! --- ADD THIS LINE --- !!!
                        .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                        .build();


                preview.setSurfaceProvider(previewView.getSurfaceProvider());



                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();
                imageAnalysis.setAnalyzer(cameraExecutor, this::analyzeImage);

                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                        .build();

                cameraProvider.unbindAll();

                // 4. BIND *BOTH* USE CASES
                // The 'preview' will show the camera, the 'imageAnalysis' will detect the pose.
                cameraProvider.bindToLifecycle(
                        getViewLifecycleOwner(),
                        cameraSelector,
                        preview,  // Add this
                        imageAnalysis
                );

            } catch (Exception e) {
                Log.e(TAG, "Camera binding failed", e);
            }
        }, ContextCompat.getMainExecutor(requireContext()));
    }

    // in camera.java
    private void analyzeImage(ImageProxy imageProxy) {
        Bitmap bitmap = converter.yuvToRgb(imageProxy);
        if (bitmap == null) {
            imageProxy.close();
            return;
        }

        Bitmap rotatedBitmap = rotateBitmapIfNeeded(bitmap, imageProxy.getImageInfo().getRotationDegrees()); // This is 4:3 (e.g., 640x480)

        float sourceWidth = rotatedBitmap.getWidth();
        float sourceHeight = rotatedBitmap.getHeight();
        float ratio = sourceWidth / sourceHeight; // e.g., 640/480 = 1.33 (4:3 Landscape)

        // --- 1. PAD THE IMAGE TO A 1:1 SQUARE (INSTEAD OF SQUASHING) ---

        // Create a new blank 192x192 bitmap and fill it with black
        Bitmap inputForModel = Bitmap.createBitmap(MOVENET_INPUT_SIZE, MOVENET_INPUT_SIZE, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(inputForModel);
        canvas.drawColor(Color.BLACK);

        int destWidth;
        int destHeight;
        int xOffset;
        int yOffset;

        // Calculate the new dimensions to fit the 4:3 image inside the 1:1 square
        if (ratio > 1.0) { // Landscape (this is your case: 4:3)
            destWidth = MOVENET_INPUT_SIZE;
            destHeight = (int) (MOVENET_INPUT_SIZE / ratio);
            xOffset = 0;
            yOffset = (MOVENET_INPUT_SIZE - destHeight) / 2;
        } else { // Portrait
            destHeight = MOVENET_INPUT_SIZE;
            destWidth = (int) (MOVENET_INPUT_SIZE * ratio);
            xOffset = (MOVENET_INPUT_SIZE - destWidth) / 2;
            yOffset = 0;
        }

        // Create a matrix to scale and translate the image
        Matrix matrix = new Matrix();
        matrix.postScale((float) destWidth / sourceWidth, (float) destHeight / sourceHeight);
        matrix.postTranslate(xOffset, yOffset);

        // Draw the 4:3 bitmap onto the 1:1 black canvas
        canvas.drawBitmap(rotatedBitmap, matrix, null);

        // --- END PADDING FIX ---

        // --- 2. RUN THE MODEL (on the padded image) ---
        ByteBuffer buffer = convertBitmapToByteBuffer(inputForModel);
        float[][][][] output = new float[1][1][17][3];
        poseDetector.run(buffer, output);
        float[][] keypoints = output[0][0]; // These keypoints are relative to the 1:1 padded square

        // --- 3. UN-PAD THE KEYPOINTS ---
        // Convert the 1:1 padded coordinates back to the original 4:3 coordinates

        // Calculate the normalized padding values
        float normXOffset = (float)xOffset / MOVENET_INPUT_SIZE;
        float normYOffset = (float)yOffset / MOVENET_INPUT_SIZE;
        float normWidth = (float)destWidth / MOVENET_INPUT_SIZE;
        float normHeight = (float)destHeight / MOVENET_INPUT_SIZE;

        float[][] unpaddedKeypoints = new float[17][3];
        for (int i = 0; i < 17; i++) {
            // [0] = Y, [1] = X
            unpaddedKeypoints[i][0] = (keypoints[i][0] - normYOffset) / normHeight;
            unpaddedKeypoints[i][1] = (keypoints[i][1] - normXOffset) / normWidth;
            unpaddedKeypoints[i][2] = keypoints[i][2];
        }
        // --- END UN-PADDING ---


        // --- 4. PASS UN-PADDED DATA TO OVERLAY ---
        // We pass the original 4:3 aspect ratio
        //float analysisAspectRatio = (float) rotatedBitmap.getWidth() / (float) rotatedBitmap.getHeight();

        requireActivity().runOnUiThread(() -> {
            // Pass the 4:3 un-padded keypoints and 4:3 aspect ratio
            overlay.setKeypoints(unpaddedKeypoints, ratio);

            // Use the un-padded keypoints for alignment checks
            FeedbackState alignment = checkAlignment(unpaddedKeypoints);
            updateFeedbackUI(alignment);
        });

        imageProxy.close();
    }

    private Bitmap rotateBitmapIfNeeded(Bitmap bitmap, int rotationDegrees) {
        if (rotationDegrees == 0) return bitmap;
        Matrix matrix = new Matrix();
        matrix.postRotate(rotationDegrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    // Maps model keypoints to camera preview coordinates
    private float[] mapKeypointToPreview(float y, float x) {
        float scaledX = x * previewView.getWidth();
        float scaledY = y * previewView.getHeight();
        // Flip horizontally for front camera
        scaledX = previewView.getWidth() - scaledX;
        return new float[]{scaledX, scaledY};
    }

    // Draw skeleton and bounding box on the bitmap
    private void drawKeypointsOnBitmap(Canvas canvas, float[][] keypoints) {
        Paint pointPaint = new Paint();
        pointPaint.setColor(Color.RED);
        pointPaint.setStyle(Paint.Style.FILL);
        pointPaint.setStrokeWidth(8f);

        Paint linePaint = new Paint();
        linePaint.setColor(Color.GREEN);
        linePaint.setStrokeWidth(6f);

        Paint boxPaint = new Paint();
        boxPaint.setColor(Color.YELLOW);
        boxPaint.setStyle(Paint.Style.STROKE);
        boxPaint.setStrokeWidth(5f);

        int[][] connections = {
                {0, 1}, {0, 2}, {1, 3}, {2, 4}, {5, 6}, {5, 7}, {7, 9}, {6, 8}, {8, 10},
                {5, 11}, {6, 12}, {11, 12}, {11, 13}, {13, 15}, {12, 14}, {14, 16}
        };

        float minX = canvas.getWidth(), minY = canvas.getHeight(), maxX = 0, maxY = 0;

        // Draw skeleton lines and update bounding box coordinates
        for (int[] conn : connections) {
            int start = conn[0], end = conn[1];
            if (keypoints[start][2] > 0.1 && keypoints[end][2] > 0.1) {
                float[] startPos = mapKeypointToPreview(keypoints[start][0], keypoints[start][1]);
                float[] endPos = mapKeypointToPreview(keypoints[end][0], keypoints[end][1]);

                canvas.drawLine(startPos[0], startPos[1], endPos[0], endPos[1], linePaint);

                minX = Math.min(minX, Math.min(startPos[0], endPos[0]));
                minY = Math.min(minY, Math.min(startPos[1], endPos[1]));
                maxX = Math.max(maxX, Math.max(startPos[0], endPos[0]));
                maxY = Math.max(maxY, Math.max(startPos[1], endPos[1]));
            }
        }

        // Draw keypoints and update bounding box coordinates
        for (int i = 0; i < 17; i++) {
            if (keypoints[i][2] > 0.1) {
                float[] pos = mapKeypointToPreview(keypoints[i][0], keypoints[i][1]);
                canvas.drawCircle(pos[0], pos[1], 10, pointPaint);

                minX = Math.min(minX, pos[0]);
                minY = Math.min(minY, pos[1]);
                maxX = Math.max(maxX, pos[0]);
                maxY = Math.max(maxY, pos[1]);
            }
        }

        // Draw bounding box if keypoints exist
        if (minX < maxX && minY < maxY) {
            canvas.drawRect(minX, minY, maxX, maxY, boxPaint);
        }
    }



    // Notice the new function arguments: Canvas, keypoints, width, height
    private void drawKeypoints(Canvas canvas, float[][] keypoints, int width, int height) {
        Paint pointPaint = new Paint();
        pointPaint.setColor(Color.RED);
        pointPaint.setStyle(Paint.Style.FILL);
        pointPaint.setStrokeWidth(8f);

        Paint linePaint = new Paint();
        linePaint.setColor(Color.GREEN);
        linePaint.setStrokeWidth(6f);

        int[][] connections = {
                {0, 1}, {0, 2}, {1, 3}, {2, 4}, {5, 6}, {5, 7}, {7, 9}, {6, 8}, {8, 10},
                {5, 11}, {6, 12}, {11, 12}, {11, 13}, {13, 15}, {12, 14}, {14, 16}
        };

        for (int[] conn : connections) {
            int start = conn[0];
            int end = conn[1];
            if (keypoints[start][2] > 0.1 && keypoints[end][2] > 0.1) {
                // keypoints[...][0] is Y, keypoints[...][1] is X
                float startX = keypoints[start][1] * width;
                float startY = keypoints[start][0] * height;
                float endX = keypoints[end][1] * width;
                float endY = keypoints[end][0] * height;
                canvas.drawLine(startX, startY, endX, endY, linePaint);
            }
        }

        for (int i = 0; i < 17; i++) {
            if (keypoints[i][2] > 0.1) {
                float x = keypoints[i][1] * width;
                float y = keypoints[i][0] * height;
                canvas.drawCircle(x, y, 10, pointPaint);
            }
        }
    }

    private ByteBuffer convertBitmapToByteBuffer(Bitmap bitmap) {
        ByteBuffer buffer = ByteBuffer.allocateDirect(MOVENET_INPUT_SIZE * MOVENET_INPUT_SIZE * 3)
                .order(ByteOrder.nativeOrder());

        int[] pixels = new int[MOVENET_INPUT_SIZE * MOVENET_INPUT_SIZE];
        bitmap.getPixels(pixels, 0, MOVENET_INPUT_SIZE, 0, 0, MOVENET_INPUT_SIZE, MOVENET_INPUT_SIZE);

        for (int pixel : pixels) {
            buffer.put((byte) Color.red(pixel));
            buffer.put((byte) Color.green(pixel));
            buffer.put((byte) Color.blue(pixel));
        }

        buffer.rewind();
        return buffer;
    }

    private FeedbackState checkAlignment(float[][] keypoints) {
        float totalConfidence = 0f;
        int visibleKeypoints = 0;
        float minY = 1f, maxY = 0f;

        for (float[] kp : keypoints) {
            if (kp[2] > 0.1f) {
                visibleKeypoints++;
                totalConfidence += kp[2];
                minY = Math.min(minY, kp[0]);
                maxY = Math.max(maxY, kp[0]);
            }
        }

        if (visibleKeypoints < 5 || (totalConfidence / visibleKeypoints) < MIN_CONFIDENCE) {
            return FeedbackState.WEAK_DETECTION;
        }

        float personHeight = maxY - minY;

        // Distance-based conditions
        if (personHeight > MAX_HEIGHT_RATIO) return FeedbackState.TOO_NEAR;
        if (personHeight < MIN_HEIGHT_RATIO) return FeedbackState.TOO_FAR;

        // Vertical alignment
        float personCenterY = minY + personHeight / 2f;
        if (personCenterY > MAX_Y_OFFSET) return FeedbackState.TOO_LOW;

        return FeedbackState.GOOD;
    }


    private int goodFrameCount = 0;
    private static final int REQUIRED_GOOD_FRAMES = 75; // ~10 frames = about 0.3 sec
    private boolean hasTransitioned = false;

    private void updateFeedbackUI(FeedbackState state) {
        if (getActivity() == null) return;

        requireActivity().runOnUiThread(() -> {
            if (state == FeedbackState.NONE) {
                feedbackBar.setVisibility(View.GONE);
                return;
            }

            feedbackBar.setVisibility(View.VISIBLE);
            int colorRes;
            String text;

            switch (state) {
                case GOOD:
                    text = "PERFECT DISTANCE AND CAMERA ALIGNMENT";
                    colorRes = R.color.feedback_good_green;
                    goodFrameCount++;
                    break;
                case TOO_NEAR:
                    text = "MOVE BACK A BIT FROM THE CAMERA";
                    colorRes = R.color.feedback_warning_color;
                    goodFrameCount = 0;
                    break;
                case TOO_FAR:
                    text = "MOVE CLOSER TO THE CAMERA";
                    colorRes = R.color.feedback_warning_color;
                    goodFrameCount = 0;
                    break;
                case TOO_LOW:
                    text = "CAMERA IS TOO LOW — RAISE IT TO CHEST LEVEL";
                    colorRes = R.color.feedback_warning_color;
                    goodFrameCount = 0;
                    break;
                case WEAK_DETECTION:
                    text = "DETECTION IS WEAK — TRY BETTER LIGHTING";
                    colorRes = R.color.feedback_warning_color;
                    goodFrameCount = 0;
                    break;
                default:
                    text = "";
                    colorRes = android.R.color.transparent;
            }


            feedbackText.setText(text);
            feedbackBar.setBackgroundColor(ContextCompat.getColor(requireActivity(), colorRes));

            // Transition after enough consecutive GOOD frames
            if (goodFrameCount >= REQUIRED_GOOD_FRAMES && !hasTransitioned) {
                hasTransitioned = true;
                navigateToPreLobby();
            }
        });

    }

    private void navigateToPreLobby() {
        Log.d("CameraFragment", "Attempting to navigate to InGameScreen..."); // Use a consistent TAG

        // 1. Get the arguments bundle that THIS (camera) fragment received
        Bundle receivedArgs = getArguments();

        // 2. Prepare the arguments bundle to pass FORWARD to InGameScreen
        Bundle argsToPass = new Bundle();
        if (receivedArgs != null) {
            // Retrieve the pose IDs array (make sure the key "selectedPoseIds" is correct)
            String[] poseIds = receivedArgs.getStringArray("selectedPoseIds");
            if (poseIds != null) {
                // Put the array into the bundle we will pass
                argsToPass.putStringArray("selectedPoseIds", poseIds);
                Log.d("CameraFragment", "Passing " + poseIds.length + " pose IDs to InGameScreen.");
            } else {
                Log.w("CameraFragment", "selectedPoseIds was null in received arguments when navigating to InGameScreen.");
                // Handle error: Maybe don't navigate or pass an empty array?
                // argsToPass.putStringArray("selectedPoseIds", new String[0]); // Option: pass empty
            }
        } else {
            Log.w("CameraFragment", "getArguments() returned null when navigating to InGameScreen.");
            // Handle error: Maybe don't navigate?
        }

        // 3. Perform the navigation using the action ID from your nav_graph.xml
        try {
            // Use the action ID you defined that goes from cameraFragment to inGameScreenFragment
            NavHostFragment.findNavController(this) // Use 'this' (the Fragment instance)
                    .navigate(R.id.action_camera_to_inGameScreen, argsToPass);
            Log.d("CameraFragment", "Navigation to InGameScreen initiated.");

        } catch (Exception e) { // Catch potential navigation errors (e.g., ID not found)
            Log.e("CameraFragment", "Navigation to InGameScreen failed", e);
            // Show an error message to the user if needed
            Toast.makeText(getContext(), "Error starting routine.", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (poseDetector != null) poseDetector.close();
        if (cameraExecutor != null) cameraExecutor.shutdown();
    }
    public enum FeedbackState { NONE, GOOD, TOO_NEAR, TOO_FAR, TOO_LOW, WEAK_DETECTION }


}
