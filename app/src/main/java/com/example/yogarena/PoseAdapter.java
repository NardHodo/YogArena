package com.example.yogarena;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yogarena.R;

import java.util.HashSet;
import java.util.List;

public class PoseAdapter extends RecyclerView.Adapter<PoseAdapter.PoseViewHolder> {

    private List<PoseInfo> poseList;
    private OnPoseSelectedListener listener;
    private HashSet<String> selectedPoseIds; // To track selection state

    public PoseAdapter(List<PoseInfo> poseList, HashSet<String> selectedPoseIds, OnPoseSelectedListener listener) {
        this.poseList = poseList;
        this.selectedPoseIds = selectedPoseIds;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PoseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.temp_item_pose_card, parent, false);
        return new PoseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PoseViewHolder holder, int position) {
        PoseInfo poseInfo = poseList.get(position);
        holder.bind(poseInfo);
    }

    @Override
    public int getItemCount() {
        return poseList.size();
    }

    class PoseViewHolder extends RecyclerView.ViewHolder {
        ImageView poseImage;
        View selectionOverlay;

        PoseViewHolder(@NonNull View itemView) {
            super(itemView);
            poseImage = itemView.findViewById(R.id.pose_image);
            selectionOverlay = itemView.findViewById(R.id.selection_overlay);
        }

        void bind(final PoseInfo poseInfo) {
            poseImage.setImageResource(poseInfo.getImageResourceId());

            // Set the selection state
            boolean isSelected = selectedPoseIds.contains(poseInfo.getPoseId());
            selectionOverlay.setVisibility(isSelected ? View.VISIBLE : View.GONE);

            itemView.setOnClickListener(v -> {
                if (selectedPoseIds.contains(poseInfo.getPoseId())) {
                    // It was selected, now deselect it
                    listener.onPoseDeselected(poseInfo);
                } else {
                    // It was not selected, now select it
                    listener.onPoseSelected(poseInfo);
                }
                // We just notify the listener. The Fragment will update the HashSet
                // and tell the *main* adapter to refresh, which will recreate this adapter.
            });
        }
    }
}