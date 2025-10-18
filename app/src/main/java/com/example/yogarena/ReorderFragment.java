
package com.example.yogarena;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton; // Corrected import
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager; // Add this import
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ReorderFragment extends Fragment {

    private RecyclerView reorderRecyclerView;
    private ReorderAdapter adapter;
    private List<PoseInfo> selectedPoseInfoList = new ArrayList<>();
    private ArrayList<String> selectedIds; // To store received IDs

    // TODO: Get the actual routine name from a ViewModel or NavArgs
    private String routineName = "My New Routine";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reorder, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // --- 1. Get the data from the previous fragment ---
        String[] selectedIdsArray = null; // Variable to hold the String array
        if (getArguments() != null) {
            // *** FIX: Use getStringArray() ***
            selectedIdsArray = getArguments().getStringArray("selectedPoseIds");
            if (selectedIdsArray == null) {
                Log.e("ReorderFragment", "Received null or wrong type for selectedPoseIds!");
                selectedIdsArray = new String[0]; // Initialize as empty array to prevent crash
            } else {
                Log.d("ReorderFragment", "Received IDs array size: " + selectedIdsArray.length); // Add log
            }
        } else {
            selectedIdsArray = new String[0];
            Log.e("ReorderFragment", "Arguments bundle is null!");
        }

        // --- 2. Convert IDs array to full PoseInfo objects ---
        selectedPoseInfoList.clear();
        for (String id : selectedIdsArray) { // Loop through the String array
            PoseInfo info = PoseMasterList.getInfo(id);
            if (info != null) {
                selectedPoseInfoList.add(info);
            } else {
                Log.w("ReorderFragment", "Could not find PoseInfo for ID: " + id);
            }
        }
        Log.d("ReorderFragment", "Converted PoseInfo list size: " + selectedPoseInfoList.size()); // Add log


        // --- 3. Set up the RecyclerView ---
        reorderRecyclerView = view.findViewById(R.id.reorder_recyclerview);
        adapter = new ReorderAdapter(selectedPoseInfoList);
        reorderRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        reorderRecyclerView.setAdapter(adapter);
        Log.d("ReorderFragment", "Adapter item count: " + adapter.getItemCount()); // Add log



        // --- 4. Set up buttons ---
        ImageButton backButton = view.findViewById(R.id.back_button); // Use ImageButton if it's an ImageButton
        Button finishButton = view.findViewById(R.id.finish_button);

        backButton.setOnClickListener(v -> {
            // Go back to the selection screen
            NavHostFragment.findNavController(this).popBackStack();
        });

        finishButton.setOnClickListener(v -> {
            saveRoutineAndShowDialog();
        });
    }

    private void saveRoutineAndShowDialog() {
        // --- 1. Get the final list of pose names (IDs) ---
        // The order is already correct from selectedPoseInfoList
        List<String> finalPoseNames = new ArrayList<>();
        for (PoseInfo poseInfo : selectedPoseInfoList) {
            finalPoseNames.add(poseInfo.getPoseId());
        }

        // --- 2. Create the CustomRoutine object ---
        // TODO: Get the real routine name (from ViewModel or previous fragment)
        CustomRoutine newRoutine = new CustomRoutine(routineName, finalPoseNames);

        // --- 3. Save it using your RoutineManager ---
        RoutineManager routineManager = new RoutineManager(requireContext()); // Use requireContext()
        routineManager.addRoutine(newRoutine);

        // --- 4. Show the success dialog ---
        showSuccessDialog();
    }

    private void showSuccessDialog() {
        // Inflate the custom layout
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_routine_created, null);

        // Build the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext()); // Use requireContext()
        builder.setView(dialogView);
        // Optional: Make dialog non-cancelable by tapping outside
        // builder.setCancelable(false);

        final AlertDialog dialog = builder.create();

        // Optional: Make dialog background transparent if needed
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        // Get views from the custom layout
        TextView messageText = dialogView.findViewById(R.id.dialog_message);
        Button closeButton = dialogView.findViewById(R.id.close_button);

        // Set the message dynamically
        messageText.setText("Routine: '" + routineName + "' successfully created");

        // Set the close button action
        // Inside ReorderFragment.java -> showSuccessDialog -> closeButton.setOnClickListener

        closeButton.setOnClickListener(v -> {
            dialog.dismiss();
            try {
                // --- PASS ARGUMENTS ---
                // 1. Get the final list (already done in saveRoutineAndShowDialog, but we need it here)
                List<String> finalPoseNames = new ArrayList<>();
                for (PoseInfo poseInfo : selectedPoseInfoList) {
                    finalPoseNames.add(poseInfo.getPoseId());
                }

                // 2. Convert to String array
                String[] finalPoseArray = finalPoseNames.toArray(new String[0]);

                // 3. Create Bundle and add the array
                Bundle args = new Bundle();
                args.putStringArray("selectedPoseIds", finalPoseArray); // Use the key defined in nav_graph

                // 4. Navigate WITH the arguments bundle
                NavHostFragment.findNavController(ReorderFragment.this) // Use 'this'
                        .navigate(R.id.action_reorderFragment_to_cameraFragment, args); // Pass args
                // --- END PASS ARGUMENTS ---

            } catch (Exception e) {
                Log.e("ReorderFragment", "Navigation failed after dialog close", e);
                // Fallback: Just pop back stack if action fails
                NavHostFragment.findNavController(ReorderFragment.this).popBackStack(R.id.selectPoseFragment, true); // Pop back to selection
            }
        });
        dialog.show();
    }

    // You need CustomRoutine.java created as well
    // Make sure RoutineManager.java is also created
}