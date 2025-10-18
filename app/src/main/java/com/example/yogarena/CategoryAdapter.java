package com.example.yogarena;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<CategoryGroup> categoryList;
    private OnPoseSelectedListener listener;
    private Set<String> selectedPoseIds;
    private Context context;

    public CategoryAdapter(Context context, List<CategoryGroup> categoryList, Set<String> selectedPoseIds, OnPoseSelectedListener listener) {
        this.context = context;
        this.categoryList = categoryList;
        this.selectedPoseIds = selectedPoseIds;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.temp_item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        CategoryGroup group = categoryList.get(position);
        holder.bind(group);
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView categoryNameText;
        ImageView expandArrow;
        RecyclerView posesRecyclerView;
        View headerLayout;

        CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            headerLayout = itemView.findViewById(R.id.header_layout);
            categoryNameText = itemView.findViewById(R.id.category_name_text);
            expandArrow = itemView.findViewById(R.id.expand_arrow);
            posesRecyclerView = itemView.findViewById(R.id.poses_recyclerview);
        }

        void bind(final CategoryGroup group) {
            categoryNameText.setText(group.getCategoryName());

            // Set up the nested adapter
            PoseAdapter poseAdapter = new PoseAdapter(group.getPoses(), selectedPoseIds, listener);
            posesRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            posesRecyclerView.setAdapter(poseAdapter);

            // Set the visibility and arrow based on the state
            boolean isExpanded = group.isExpanded();
            posesRecyclerView.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
            expandArrow.setImageResource(isExpanded ? R.drawable.back_button_dark : R.drawable.back_button_dark);

            // Set the click listener to toggle the state
            headerLayout.setOnClickListener(v -> {
                group.setExpanded(!isExpanded);
                // Notify this item has changed to re-bind it
                notifyItemChanged(getAdapterPosition());
            });
        }
    }
}