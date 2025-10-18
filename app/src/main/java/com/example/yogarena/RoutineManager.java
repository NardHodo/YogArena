package com.example.yogarena; // Make sure this matches your package

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages saving and loading CustomRoutine objects using SharedPreferences and Gson.
 */
public class RoutineManager {

    private static final String PREFS_NAME = "YogaAppPrefs"; // Name of the SharedPreferences file
    private static final String KEY_ROUTINES = "CustomRoutines"; // Key for storing the JSON string

    private SharedPreferences sharedPreferences;
    private Gson gson;

    /**
     * Constructor. Initializes SharedPreferences and Gson.
     * @param context The application context.
     */
    public RoutineManager(Context context) {
        // Get SharedPreferences instance
        sharedPreferences = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        // Create a Gson instance for JSON conversion
        gson = new Gson();
    }

    /**
     * Loads all saved custom routines from SharedPreferences.
     * @return A List of CustomRoutine objects. Returns an empty list if none are saved.
     */
    public List<CustomRoutine> loadRoutines() {
        // Retrieve the JSON string from SharedPreferences
        String jsonString = sharedPreferences.getString(KEY_ROUTINES, null);

        // If no string is found, return an empty list
        if (jsonString == null) {
            return new ArrayList<>();
        }

        // Define the type for Gson to deserialize into (a List of CustomRoutine)
        Type type = new TypeToken<ArrayList<CustomRoutine>>(){}.getType();

        // Convert the JSON string back into a List of CustomRoutine objects
        List<CustomRoutine> routines = gson.fromJson(jsonString, type);

        // Ensure we always return a non-null list
        return routines != null ? routines : new ArrayList<>();
    }

    /**
     * Saves the entire list of custom routines to SharedPreferences.
     * This overwrites any previously saved list.
     * @param routines The List of CustomRoutine objects to save.
     */
    private void saveRoutines(List<CustomRoutine> routines) {
        // Convert the List of CustomRoutine objects into a JSON string
        String jsonString = gson.toJson(routines);

        // Get an editor to modify SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Put the JSON string into SharedPreferences under the defined key
        editor.putString(KEY_ROUTINES, jsonString);

        // Apply the changes asynchronously
        editor.apply();
    }

    /**
     * Adds a new custom routine to the saved list.
     * It loads the existing list, adds the new routine, and saves the updated list.
     * @param newRoutine The CustomRoutine object to add.
     */
    public void addRoutine(CustomRoutine newRoutine) {
        // Load the current list of routines
        List<CustomRoutine> routines = loadRoutines();
        // Add the new routine to the list
        routines.add(newRoutine);
        // Save the updated list back to SharedPreferences
        saveRoutines(routines);
    }

    /**
     * (Optional) Deletes a specific custom routine from the saved list.
     * Note: This requires the CustomRoutine class to properly implement equals() and hashCode()
     * or you need to find the routine by its name or ID.
     * @param routineToRemove The CustomRoutine object to remove.
     */
    public void deleteRoutine(CustomRoutine routineToRemove) {
        List<CustomRoutine> routines = loadRoutines();
        // Attempt to remove the routine (relies on equals method)
        if (routines.remove(routineToRemove)) {
            // Save the list only if removal was successful
            saveRoutines(routines);
        }
    }

    // You could add more methods here, like updateRoutine, getRoutineByName, etc.
}