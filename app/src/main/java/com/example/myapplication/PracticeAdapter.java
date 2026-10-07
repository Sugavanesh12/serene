package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PracticeAdapter extends RecyclerView.Adapter<PracticeAdapter.PracticeViewHolder> {

    private final List<PracticeItem> practiceList;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(PracticeItem item);
    }

    public PracticeAdapter(List<PracticeItem> practiceList, OnItemClickListener listener) {
        this.practiceList = practiceList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PracticeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_practice, parent, false);
        return new PracticeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PracticeViewHolder holder, int position) {
        PracticeItem item = practiceList.get(position);
        holder.tvTitle.setText(item.getTitle());
        holder.tvDescription.setText(item.getDescription());
        holder.tvDuration.setText(item.getDuration());

        if (item.isDownloaded()) {
            holder.ivAction.setImageResource(android.R.drawable.stat_sys_download_done);
        } else {
            holder.ivAction.setImageResource(android.R.drawable.stat_sys_download);
        }

        holder.ivAction.setOnClickListener(v -> {
            boolean newState = !item.isDownloaded();
            // Update downloaded status simulation
            Toast.makeText(v.getContext(), item.getTitle() + (newState ? " downloaded for offline" : " removed from downloads"), Toast.LENGTH_SHORT).show();
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return practiceList.size();
    }

    static class PracticeViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDescription, tvDuration;
        ImageView ivAction;

        public PracticeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvDuration = itemView.findViewById(R.id.tvDuration);
            ivAction = itemView.findViewById(R.id.ivAction);
        }
    }
}
