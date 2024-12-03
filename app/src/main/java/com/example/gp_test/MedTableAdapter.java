package com.example.gp_test;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MedTableAdapter extends RecyclerView.Adapter<MedTableAdapter.MedTableViewHolder> {

    private final List<MedTable> medTableList;
    private final OnDeleteClickListener onDeleteClickListener;

    public MedTableAdapter(List<MedTable> medTableList, OnDeleteClickListener onDeleteClickListener) {
        this.medTableList = medTableList;
        this.onDeleteClickListener = onDeleteClickListener;
    }

    @NonNull
    @Override
    public MedTableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_med_table, parent, false);
        return new MedTableViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedTableViewHolder holder, int position) {
        MedTable medTable = medTableList.get(position);

        if (medTable != null) {
            holder.tvMedTableName.setText(medTable.getName());
            holder.tvMedTableCode.setText(medTable.getCode());

            // Delete button functionality
            holder.deleteButton.setOnClickListener(v -> {
                if (onDeleteClickListener != null) {
                    onDeleteClickListener.onDeleteClick(position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return medTableList.size();
    }

    public static class MedTableViewHolder extends RecyclerView.ViewHolder {
        TextView tvMedTableName, tvMedTableCode, deleteButton;

        public MedTableViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMedTableName = itemView.findViewById(R.id.tvMedTableName);
            tvMedTableCode = itemView.findViewById(R.id.tvMedTableCode);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(int position);
    }
}
