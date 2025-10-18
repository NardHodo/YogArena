package com.example.yogarena;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class SelectPoseFragment extends Fragment implements OnPoseSelectedListener {

    private RecyclerView categoryRecyclerView;
    private CategoryAdapter categoryAdapter;
    private List<CategoryGroup> categoryGroups;

    private HashSet<String> selectedPoseIds = new HashSet<>();
    private TextView selectedCounterText;
    private Button nextButton, cancelButton;

    private final int MAX_SELECTION = 8;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_temp_select_pose, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Find views
        categoryRecyclerView = view.findViewById(R.id.category_recyclerview);
        selectedCounterText = view.findViewById(R.id.selected_counter_text);
        nextButton = view.findViewById(R.id.next_button);
        cancelButton = view.findViewById(R.id.cancel_button);

        // 1. Prepare the data
        prepareData();

        // 2. Set up the main adapter
        categoryAdapter = new CategoryAdapter(getContext(), categoryGroups, selectedPoseIds, this);
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        categoryRecyclerView.setAdapter(categoryAdapter);

        // 3. Set click listeners
        cancelButton.setOnClickListener(v -> {
            // Navigate back
            NavHostFragment.findNavController(this).popBackStack();
        });

        nextButton.setOnClickListener(v -> {
            if (selectedPoseIds.isEmpty()) {
                Toast.makeText(getContext(), "Please select at least one pose.", Toast.LENGTH_SHORT).show();
            } else {
                ArrayList<String> selectedList = new ArrayList<>(selectedPoseIds);

                // --- FIX: Convert ArrayList to String array ---
                String[] selectedArray = selectedList.toArray(new String[0]); // Convert to String[]

                Bundle args = new Bundle();
                // Use putStringArray with the converted array
                args.putStringArray("selectedPoseIds", selectedArray); // Pass the String[]

                try {
                    NavHostFragment.findNavController(SelectPoseFragment.this)
                            .navigate(R.id.action_selectPoseFragment_to_reorderFragment, args);
                } catch (Exception e) {
                    android.util.Log.e("SelectPoseFragment", "Navigation failed", e);
                    Toast.makeText(getContext(), "Error navigating.", Toast.LENGTH_SHORT).show();
                }
                // --- END FIX ---
            }
        });

        updateCounter();
    }

    private void prepareData() {
        // This is where you convert your PoseMasterList into grouped categories
        categoryGroups = new ArrayList<>();

        // You would get this from your actual PoseMasterList
        Map<String, PoseInfo> masterPoseList = PoseMasterList.ALL_POSES;

        // Group poses by category
        Map<String, List<PoseInfo>> posesByCategory = new HashMap<>();
        for (PoseInfo pose : masterPoseList.values()) {
            String category = pose.getCategory();
            if (!posesByCategory.containsKey(category)) {
                posesByCategory.put(category, new ArrayList<>());
            }
            posesByCategory.get(category).add(pose);
        }

        // Create the CategoryGroup objects
        for (Map.Entry<String, List<PoseInfo>> entry : posesByCategory.entrySet()) {
            categoryGroups.add(new CategoryGroup(entry.getKey(), entry.getValue()));
        }

        // (Optional) Sort categories alphabetically
        // categoryGroups.sort(Comparator.comparing(CategoryGroup::getCategoryName));
    }

    @Override
    public void onPoseSelected(PoseInfo poseInfo) {
        if (selectedPoseIds.size() < MAX_SELECTION) {
            selectedPoseIds.add(poseInfo.getPoseId());
            updateCounter();
            categoryAdapter.notifyDataSetChanged(); // Refresh all to show selection
        } else {
            Toast.makeText(getContext(), "You can only select up to " + MAX_SELECTION + " poses.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPoseDeselected(PoseInfo poseInfo) {
        selectedPoseIds.remove(poseInfo.getPoseId());
        updateCounter();
        categoryAdapter.notifyDataSetChanged(); // Refresh all to show selection
    }

    private void updateCounter() {
        selectedCounterText.setText("Selected: " + selectedPoseIds.size() + "/" + MAX_SELECTION);
    }
}