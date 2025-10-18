package com.example.yogarena;

import java.util.List;

public class CategoryGroup {
    String categoryName;
    List<PoseInfo> poses;
    boolean isExpanded; // Tracks if the list is "open" or "closed"

    public CategoryGroup(String categoryName, List<PoseInfo> poses) {
        this.categoryName = categoryName;
        this.poses = poses;
        this.isExpanded = false; // Default to collapsed
    }

    // --- Getters and Setters ---
    public String getCategoryName() { return categoryName; }
    public List<PoseInfo> getPoses() { return poses; }
    public boolean isExpanded() { return isExpanded; }
    public void setExpanded(boolean expanded) { isExpanded = expanded; }
}