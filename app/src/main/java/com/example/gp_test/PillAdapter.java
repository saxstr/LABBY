package com.example.gp_test;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PillAdapter extends RecyclerView.Adapter<PillAdapter.PillViewHolder> {

    private final List<AddPillSc.Medication> pills;
    private final OnPillDeleteListener deleteListener;

    public PillAdapter(List<AddPillSc.Medication> pills, OnPillDeleteListener deleteListener) {
        this.pills = pills;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public PillViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pill, parent, false);
        return new PillViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PillViewHolder holder, int position) {
        AddPillSc.Medication pill = pills.get(position);
        holder.pillName.setText("اسم: " + pill.name);
        holder.pillDosage.setText("جرعة: " + pill.dosage);
        holder.pillTime.setText("وقت: " + pill.time);

        holder.deleteButton.setOnClickListener(v -> deleteListener.onDelete(position));
    }

    @Override
    public int getItemCount() {
        return pills.size();
    }

    public interface OnPillDeleteListener {
        void onDelete(int position);
    }

    static class PillViewHolder extends RecyclerView.ViewHolder {
        TextView pillName, pillDosage, pillTime;
        Button deleteButton;

        public PillViewHolder(@NonNull View itemView) {
            super(itemView);
            pillName = itemView.findViewById(R.id.pillName);
            pillDosage = itemView.findViewById(R.id.pillDosage);
            pillTime = itemView.findViewById(R.id.pillTime);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}
