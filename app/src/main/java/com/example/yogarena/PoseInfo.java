package com.example.yogarena;

public class PoseInfo {
    String poseId;       // The unique ID, e.g., "Tree_Pose_or_Vrksasana_"
    String simpleName;     // The display name, e.g., "Tree Pose"
    int imageResourceId; // e.g., R.drawable.tree_pose_image
    String category;

    public PoseInfo(String poseId, String simpleName, int imageResourceId, String category) {
        this.poseId = poseId;
        this.simpleName = simpleName;
        this.imageResourceId = imageResourceId;
        this.category = category;
    }

    // --- Getters ---
    public String getPoseId() { return poseId; }
    public String getSimpleName() { return simpleName; }
    public int getImageResourceId() { return imageResourceId; }
    public String getCategory() { return category; }
}