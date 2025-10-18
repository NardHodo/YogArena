package com.example.yogarena;

import java.util.HashMap;
import java.util.Map;

public class PoseMasterList {

    // This Map is your "static database"
    public static final Map<String, PoseInfo> ALL_POSES = new HashMap<>();

    // This 'static' block runs once when your app starts
    static {
        // Manually add all your poses here.
        // The first string MUST match your classifier's label (the poseId).

        // Format:
        // ALL_POSES.put("classifier_label_name",
        //     new PoseInfo("classifier_label_name", "Simple Name", R.drawable.your_image, "Category Name"));

        ALL_POSES.put("Tree_Pose_or_Vrksasana_",
                new PoseInfo("Tree_Pose_or_Vrksasana_", "Tree Pose", R.drawable.yoga_pose_1, "Standing Poses"));

        ALL_POSES.put("Cobra_Pose_or_Bhujangasana_",
                new PoseInfo("Cobra_Pose_or_Bhujangasana_", "Cobra Pose", R.drawable.yoga_pose_1, "Backbends"));

        ALL_POSES.put("Downward-Facing_Dog_pose_or_Adho_Mukha_Svanasana_",
                new PoseInfo("Downward-Facing_Dog_pose_or_Adho_Mukha_Svanasana_", "Downward-Facing Dog", R.drawable.yoga_pose_1, "Inversions"));

        ALL_POSES.put("Warrior_I_Pose_or_Virabhadrasana_I_",
                new PoseInfo("Warrior_I_Pose_or_Virabhadrasana_I_", "Warrior I", R.drawable.yoga_pose_1, "Standing Poses"));

        ALL_POSES.put("Warrior_II_Pose_or_Virabhadrasana_II_",
                new PoseInfo("Warrior_II_Pose_or_Virabhadrasana_II_", "Warrior II", R.drawable.yoga_pose_1, "Standing Poses"));

        // ...
        // ... YOU MUST ADD ALL YOUR OTHER 80+ POSES HERE
        // ...
    }

    // A helper to easily get info for any pose by its ID
    public static PoseInfo getInfo(String poseId) {
        return ALL_POSES.get(poseId);
    }
}