package com.example.yogarena;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yogarena.R;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PoseAdapter extends RecyclerView.Adapter<PoseAdapter.PoseViewHolder> {

    private List<PoseInfo> poseList;
    private OnPoseSelectedListener listener;
    private Set<String> selectedPoseIds; // To track selection state

    public PoseAdapter(List<PoseInfo> poseList, Set<String> selectedPoseIds, OnPoseSelectedListener listener) {
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
        TextView pose_name_text;


        PoseViewHolder(@NonNull View itemView) {
            super(itemView);
            poseImage = itemView.findViewById(R.id.pose_image);
            selectionOverlay = itemView.findViewById(R.id.selection_overlay);
            pose_name_text = itemView.findViewById(R.id.pose_name_text);
        }

        void bind(final PoseInfo poseInfo) {
            poseImage.setImageResource(poseInfo.getImageResourceId());
            pose_name_text.setText(poseInfo.getSimpleName());


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