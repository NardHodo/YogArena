package com.example.yogarena; // Ensure this matches your package

import java.util.List;
import java.util.Objects; // Needed for equals() and hashCode()

/**
 * Represents a user-created custom yoga routine.
 * Stores the routine's name and an ordered list of pose IDs.
 */
public class CustomRoutine {

    String routineName;
    List<String> poseNames; // Just the list of pose IDs, e.g., ["Tree_Pose_or_Vrksasana_", "Cobra_Pose_or_Bhujangasana_"]

    /**
     * Constructor for creating a new CustomRoutine.
     * @param routineName The name given to the routine by the user.
     * @param poseNames The ordered list of pose IDs included in the routine.
     */
    public CustomRoutine(String routineName, List<String> poseNames) {
        this.routineName = routineName;
        this.poseNames = poseNames;
    }

    // --- Getters ---
    public String getRoutineName() {
        return routineName;
    }

    public List<String> getPoseNames() {
        return poseNames;
    }

    // --- Optional but recommended: equals() and hashCode() ---
    // This helps if you want to use methods like List.remove(object) later
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomRoutine that = (CustomRoutine) o;
        // Two routines are considered equal if they have the same name
        return Objects.equals(routineName, that.routineName);
    }

    @Override
    public int hashCode() {
        // Use the routineName for the hash code
        return Objects.hash(routineName);
    }
}