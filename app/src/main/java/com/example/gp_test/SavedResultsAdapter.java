package com.example.gp_test;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;

public class SavedResultsAdapter extends RecyclerView.Adapter<SavedResultsAdapter.ViewHolder> {

    private final ArrayList<HashMap<String, String>> savedResults;
    private final Context context;

    public SavedResultsAdapter(Context context, ArrayList<HashMap<String, String>> savedResults, DisplayListsSc displayListsSc) {
        this.context = context;
        this.savedResults = savedResults;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_saved_result, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HashMap<String, String> result = savedResults.get(position);

        // Bind the data to the views
        holder.resultTitle.setText(result.get("name"));
        holder.resultCode.setText(result.get("code"));

        // Set click listener for the item
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, TestResultSc.class);
            intent.putExtra("name", result.get("name"));
            intent.putExtra("code", result.get("code"));
            intent.putExtra("ocrData", result.get("ocrData")); // Pass OCR data if available
            context.startActivity(intent);
        });

        // Sharing functionality for "مشاركة" button
        holder.shareButton.setOnClickListener(v -> {
            String shareText = "Result Name: " + result.get("name") +
                    "\nCode: " + result.get("code");


            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);

            context.startActivity(Intent.createChooser(shareIntent, "Share via"));
        });
    }

    @Override
    public int getItemCount() {
        return savedResults.size();
    }

    public interface OnItemClickListener {
        void onItemClick(String selectedKey);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView resultTitle, resultCode, resultDate, shareButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            resultTitle = itemView.findViewById(R.id.resultTitle);
            resultCode = itemView.findViewById(R.id.resultCode);
            resultDate = itemView.findViewById(R.id.resultDate);
            shareButton = itemView.findViewById(R.id.shareButton);
        }
    }
}
