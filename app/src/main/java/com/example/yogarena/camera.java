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

import androidx.annotation.NonNull;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.common.util.concurrent.ListenableFuture;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class camera extends Fragment {

    private PreviewView viewFinder;

    private ImageView overlay;
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

                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();
                imageAnalysis.setAnalyzer(cameraExecutor, this::analyzeImage);

                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                        .build();

                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(getViewLifecycleOwner(), cameraSelector, imageAnalysis);

            } catch (Exception e) {
                Log.e(TAG, "Camera binding failed", e);
            }
        }, ContextCompat.getMainExecutor(requireContext()));
    }

    private void analyzeImage(ImageProxy imageProxy) {
        Bitmap bitmap = converter.yuvToRgb(imageProxy);
        if (bitmap == null) {
            imageProxy.close();
            return;
        }

        Bitmap rotatedBitmap = rotateBitmapIfNeeded(bitmap, imageProxy.getImageInfo().getRotationDegrees());

        Bitmap inputForModel = Bitmap.createScaledBitmap(rotatedBitmap, MOVENET_INPUT_SIZE, MOVENET_INPUT_SIZE, true);
        ByteBuffer buffer = convertBitmapToByteBuffer(inputForModel);

        float[][][][] output = new float[1][1][17][3];
        poseDetector.run(buffer, output);

        float[][] keypoints = output[0][0];

        Bitmap overlayBitmap = rotatedBitmap.copy(Bitmap.Config.ARGB_8888, true);
        drawKeypoints(overlayBitmap, keypoints);

        requireActivity().runOnUiThread(() -> {
            overlay.setImageBitmap(overlayBitmap);

            FeedbackState alignment = checkAlignment(keypoints);
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

    private void drawKeypoints(Bitmap bitmap, float[][] keypoints) {
        Canvas canvas = new Canvas(bitmap);
        Paint pointPaint = new Paint();
        pointPaint.setColor(Color.RED);
        pointPaint.setStyle(Paint.Style.FILL);
        pointPaint.setStrokeWidth(8f);

        Paint linePaint = new Paint();
        linePaint.setColor(Color.GREEN);
        linePaint.setStrokeWidth(6f);

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int[][] connections = {
                {0, 1}, {0, 2}, {1, 3}, {2, 4}, {5, 6}, {5, 7}, {7, 9}, {6, 8}, {8, 10},
                {5, 11}, {6, 12}, {11, 12}, {11, 13}, {13, 15}, {12, 14}, {14, 16}
        };

        for (int[] conn : connections) {
            int start = conn[0];
            int end = conn[1];
            if (keypoints[start][2] > 0.1 && keypoints[end][2] > 0.1) {
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
        if (personHeight > MAX_HEIGHT_RATIO) return FeedbackState.TOO_NEAR;

        float personCenterY = minY + personHeight / 2f;
        if (personCenterY > MAX_Y_OFFSET) return FeedbackState.TOO_LOW;

        return FeedbackState.GOOD;
    }

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
                    text = "CAMERA AND SUBJECT SETUP IS GOOD";
                    colorRes = R.color.feedback_good_green;
                    break;
                case TOO_NEAR:
                    text = "SUBJECT IS TOO NEAR THE CAMERA";
                    colorRes = R.color.feedback_warning_color;
                    break;
                case TOO_LOW:
                    text = "CAMERA PLACEMENT IS WAY TOO LOW";
                    colorRes = R.color.feedback_warning_color;
                    break;
                case WEAK_DETECTION:
                    text = "DETECTION IS WEAK. MOVE TO A BRIGHTER AREA";
                    colorRes = R.color.feedback_warning_color;
                    break;
                default:
                    text = "";
                    colorRes = android.R.color.transparent;
            }

            feedbackText.setText(text);
            feedbackBar.setBackgroundColor(ContextCompat.getColor(requireActivity(), colorRes));
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (poseDetector != null) poseDetector.close();
        if (cameraExecutor != null) cameraExecutor.shutdown();
    }
    public enum FeedbackState { NONE, GOOD, TOO_NEAR, TOO_LOW, WEAK_DETECTION }

}
