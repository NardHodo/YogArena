package com.example.yogarena;

import org.tensorflow.lite.Interpreter; // Add this import
import java.util.List;               // Add this import
import android.util.Log;               // Add this import
import android.graphics.Bitmap;        // Add this import
import android.graphics.Color;         // Add this import
import java.nio.ByteBuffer;          // Add this import
import java.nio.ByteOrder;           // Add this import


public class YogaPoseClassifier {

    // --- ADD THESE MEMBER VARIABLES ---
    private static final String TAG = "YogaPoseClassifier";
    private final Interpreter imageClassifier;
    private final List<String> allPoseLabels;
    private final int inputImageWidth;
    private final int inputImageHeight;
    // --- END MEMBER VARIABLES ---

    // --- ADD THIS CONSTRUCTOR ---
    /**
     * Constructor for the YogaPoseClassifier.
     * @param interpreter The initialized TFLite Interpreter for the classification model.
     * @param labels The complete list of pose labels corresponding to the model's output order.
     * @param inputWidth The expected width of the input image for the model (e.g., 224).
     * @param inputHeight The expected height of the input image for the model (e.g., 224).
     */
    public YogaPoseClassifier(Interpreter interpreter, List<String> labels, int inputWidth, int inputHeight) {
        this.imageClassifier = interpreter;
        this.allPoseLabels = labels;
        this.inputImageWidth = inputWidth;
        this.inputImageHeight = inputHeight;

        // Optional: Verification logic
        verifyModelLabels();
    }
    // --- END CONSTRUCTOR ---

    // --- ADD THE REST OF THE CLASS METHODS (classify, preprocessImage, etc.) ---
    // (Paste the methods from the previous answer here)
    /**
     * Classifies the bitmap against a target pose ID.
     * @param cameraFrameBitmap Bitmap from the camera (correctly oriented).
     * @param targetPoseId The ID/label of the pose to check for.
     * @return Confidence score (0.0 to 1.0) for the target pose, or -1.0f on error.
     */
    public float classify(Bitmap cameraFrameBitmap, String targetPoseId) {
        if (!isReady() || cameraFrameBitmap == null) {
            Log.e(TAG, "Classifier not ready or invalid input bitmap.");
            return -1.0f;
        }

        int targetClassifierIndex = allPoseLabels.indexOf(targetPoseId);
        if (targetClassifierIndex == -1) {
            Log.e(TAG, "Target pose ID '" + targetPoseId + "' not found in classifier labels!");
            return -1.0f; // Target pose is unknown to the model
        }

        try {
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(cameraFrameBitmap, inputImageWidth, inputImageHeight, true);
            ByteBuffer inputBuffer = preprocessImage(resizedBitmap);
            float[][] classifierOutput = new float[1][allPoseLabels.size()];

            imageClassifier.run(inputBuffer, classifierOutput);

            float confidence = classifierOutput[0][targetClassifierIndex];
            return Math.max(0.0f, Math.min(1.0f, confidence)); // Clamp between 0 and 1

        } catch (Exception e) {
            Log.e(TAG, "Error during classification for target '" + targetPoseId + "'", e);
            return -1.0f;
        }
    }

    /**
     * Checks if the classifier is properly initialized.
     */
    public boolean isReady() {
        return imageClassifier != null && allPoseLabels != null && !allPoseLabels.isEmpty();
    }

    /**
     * Preprocesses the bitmap for the image classifier model.
     * (Assumes MobileNet-style [-1, 1] normalization - adjust if needed).
     */
    private ByteBuffer preprocessImage(Bitmap bitmap) {
        ByteBuffer buffer = ByteBuffer.allocateDirect(4 * inputImageWidth * inputImageHeight * 3)
                .order(ByteOrder.nativeOrder());
        buffer.rewind();

        int[] pixels = new int[inputImageWidth * inputImageHeight];
        bitmap.getPixels(pixels, 0, inputImageWidth, 0, 0, inputImageWidth, inputImageHeight);

        for (int pixelValue : pixels) {
            float r = Color.red(pixelValue);
            float g = Color.green(pixelValue);
            float b = Color.blue(pixelValue);

            // Normalize to [-1, 1]
            float rf = (r - 127.5f) / 127.5f;
            float gf = (g - 127.5f) / 127.5f;
            float bf = (b - 127.5f) / 127.5f;

            buffer.putFloat(rf);
            buffer.putFloat(gf);
            buffer.putFloat(bf);
        }
        buffer.rewind();
        return buffer;
    }

    // Optional verification method
    private void verifyModelLabels() {
        if (imageClassifier != null && allPoseLabels != null && !allPoseLabels.isEmpty()) {
            try {
                int[] outputShape = imageClassifier.getOutputTensor(0).shape();
                if (outputShape.length < 2 || outputShape[1] != allPoseLabels.size()) {
                    Log.e(TAG, "Model output size (" + (outputShape.length > 1 ? outputShape[1] : 0)
                            + ") doesn't match label count (" + allPoseLabels.size() + ").");
                } else {
                    Log.d(TAG, "Classifier labels match model output size.");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error verifying classifier output shape", e);
            }
        } else {
            Log.w(TAG, "Cannot verify model labels: Interpreter or labels list is null/empty.");
        }
    }
    // --- END CLASS METHODS ---
}