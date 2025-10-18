package com.example.yogarena;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ReorderAdapter extends RecyclerView.Adapter<ReorderAdapter.ReorderViewHolder> {

    private final List<PoseInfo> poseList;

    public ReorderAdapter(List<PoseInfo> poseList) {
        this.poseList = poseList;
    }

    @NonNull
    @Override
    public ReorderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_reorder_card, parent, false);
        return new ReorderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReorderViewHolder holder, int position) {
        PoseInfo pose = poseList.get(position);
        holder.bind(pose);
    }

    @Override
    public int getItemCount() {
        return poseList.size();
    }

    class ReorderViewHolder extends RecyclerView.ViewHolder {
        ImageView poseImage;
        TextView poseNameText;

        ReorderViewHolder(@NonNull View itemView) {
            super(itemView);
            poseImage = itemView.findViewById(R.id.pose_image);
            poseNameText = itemView.findViewById(R.id.pose_name_text);
        }

        void bind(PoseInfo pose) {
            poseImage.setImageResource(pose.getImageResourceId());
            poseNameText.setText(pose.getSimpleName());
        }
    }
}