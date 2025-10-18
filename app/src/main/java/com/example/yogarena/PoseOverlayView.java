package com.example.yogarena;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import androidx.camera.view.PreviewView;

public class PoseOverlayView extends View {

    private float[][] keypoints;
    private PreviewView previewView;
    private float analysisAspectRatio = 1.0f; // Aspect ratio of the analysis image

    // --- NEW: Paint for the bounding box ---
    private Paint boxPaint;

    private Paint pointPaint;
    private Paint linePaint;

    public PoseOverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaints();
    }

    private void initPaints() {
        pointPaint = new Paint();
        pointPaint.setColor(Color.RED);
        pointPaint.setStyle(Paint.Style.FILL);
        pointPaint.setStrokeWidth(8f);

        linePaint = new Paint();
        linePaint.setColor(Color.GREEN);
        linePaint.setStrokeWidth(6f);

        // --- NEW: Initialize the box paint ---
        boxPaint = new Paint();
        boxPaint.setColor(Color.YELLOW); // Yellow stands out
        boxPaint.setStyle(Paint.Style.STROKE);
        boxPaint.setStrokeWidth(5f);
    }

    public void setPreviewView(PreviewView view) {
        this.previewView = view;
    }

    public void setKeypoints(float[][] keypoints, float analysisAspectRatio) {
        this.keypoints = keypoints;
        this.analysisAspectRatio = analysisAspectRatio;
        invalidate(); // Request a redraw
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (keypoints == null || previewView == null) return;

        // --- NEW: Refactored drawing logic ---

        // 1. Initialize boundary variables and a place to store screen points
        float minX = Float.MAX_VALUE;
        float minY = Float.MAX_VALUE;
        float maxX = -Float.MAX_VALUE;
        float maxY = -Float.MAX_VALUE;
        boolean personVisible = false;

        float[][] screenKeypoints = new float[17][2];

        // 2. Loop once to map all points and find boundaries
        for (int i = 0; i < 17; i++) {
            if (keypoints[i][2] > 0.1) { // If keypoint is visible
                float[] xy = mapKeypointToPreview(keypoints[i]);
                screenKeypoints[i] = xy; // Store the mapped point

                // Update boundaries
                minX = Math.min(minX, xy[0]);
                maxX = Math.max(maxX, xy[0]);
                minY = Math.min(minY, xy[1]);
                maxY = Math.max(maxY, xy[1]);

                personVisible = true;
            }
        }

        // 3. If a person was detected, draw everything
        if (personVisible) {
            int[][] connections = {
                    {0, 1}, {0, 2}, {1, 3}, {2, 4}, {5, 6}, {5, 7}, {7, 9}, {6, 8}, {8, 10},
                    {5, 11}, {6, 12}, {11, 12}, {11, 13}, {13, 15}, {12, 14}, {14, 16}
            };

            // Draw connections
            for (int[] conn : connections) {
                int start = conn[0];
                int end = conn[1];
                // Check if *both* points were visible
                if (keypoints[start][2] > 0.1 && keypoints[end][2] > 0.1) {
                    float[] startPos = screenKeypoints[start]; // Get from storage
                    float[] endPos = screenKeypoints[end];   // Get from storage
                    canvas.drawLine(startPos[0], startPos[1], endPos[0], endPos[1], linePaint);
                }
            }

            // Draw keypoints
            for (int i = 0; i < 17; i++) {
                if (keypoints[i][2] > 0.1) {
                    float[] xy = screenKeypoints[i]; // Get from storage
                    canvas.drawCircle(xy[0], xy[1], 10, pointPaint);
                }
            }

            // --- 4. NEW: Draw the bounding box ---
            // Add a small padding for visual comfort
            float padding = 10f;
            canvas.drawRect(minX - padding, minY - padding, maxX + padding, maxY + padding, boxPaint);
        }
    }

    /**
     * Maps the normalized keypoint coordinates [0..1] to the screen coordinates
     * of the PreviewView, accounting for aspect ratio differences and mirroring.
     */
    private float[] mapKeypointToPreview(float[] kp) {
        // kp[0] = y, kp[1] = x in normalized coordinates [0..1]

        int viewWidth = previewView.getWidth();
        int viewHeight = previewView.getHeight();

        // This is the aspect ratio of the PreviewView (the screen)
        float ratioPreview = (float) viewWidth / viewHeight;

        // This is the aspect ratio of the analysis bitmap (e.GET_IMAGE_FROM_QUERY
        float ratioCamera = this.analysisAspectRatio;

        float contentWidth;
        float contentHeight;
        float offsetX = 0f;
        float offsetY = 0f;

        if (ratioPreview > ratioCamera) {
            // Preview is wider than analysis image (letterboxing on left/right)
            contentHeight = viewHeight;
            contentWidth = viewHeight * ratioCamera;
            offsetX = (viewWidth - contentWidth) / 2f;
        } else {
            // Preview is taller than analysis image (letterboxing on top/bottom)
            contentWidth = viewWidth;
            contentHeight = viewWidth / ratioCamera;
            offsetY = (viewHeight - contentHeight) / 2f;
        }

        // Map normalized keypoints to the scaled content area
        float x = kp[1] * contentWidth + offsetX;
        float y = kp[0] * contentHeight + offsetY;

        // Flip horizontally for front camera (mirroring)
        x = viewWidth - x;

        return new float[]{x, y};
    }
}