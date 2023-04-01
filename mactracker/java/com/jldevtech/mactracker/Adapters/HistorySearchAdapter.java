package com.jldevtech.mactracker.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jldevtech.mactracker.ControllerFolder.AddFoodFromHistory;
import com.jldevtech.mactracker.R;
import com.jldevtech.mactracker.entities.FoodEntity;

import java.util.ArrayList;

public class HistorySearchAdapter extends RecyclerView.Adapter<HistorySearchAdapter.FoodViewHolder>{
    private ArrayList<FoodEntity> foodsList;
    private final Context context;
    private final LayoutInflater mInflater;

    public HistorySearchAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        this.context = context;
    }

    public class FoodViewHolder extends RecyclerView.ViewHolder {
        private TextView nameText;
        private TextView proteinText;
        private TextView carbsText;
        private TextView fatText;

        public FoodViewHolder(final View view) {
            super(view);
            nameText = view.findViewById(R.id.nameFood);
            proteinText = view.findViewById(R.id.proteinText);
            fatText = view.findViewById(R.id.fatText);
            carbsText = view.findViewById(R.id.carbsText);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    final FoodEntity current = foodsList.get(position);
                    Intent intent = new Intent(context, AddFoodFromHistory.class);
                    intent.putExtra("foodName", current.getFoodName());
                    intent.putExtra("foodCarbs", current.getCarbs());
                    intent.putExtra("foodFat", current.getFat());
                    intent.putExtra("foodProtein", current.getProtein());
                    intent.putExtra("foodDate", current.getDate());
                    intent.putExtra("foodId", current.getFid());
                    intent.putExtra("username" , current.getUsername());
                    intent.putExtra("position", position);
                    context.startActivity(intent);
                    ((Activity)context).finish();
                }
            });
        }
    }

    @NonNull
    @Override
    public HistorySearchAdapter.FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_rows_titles, parent, false);
        return new FoodViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull HistorySearchAdapter.FoodViewHolder holder, int position) {
        if (foodsList != null) {
            final FoodEntity current = foodsList.get(position);
            holder.nameText.setText(current.getFoodName());
            holder.proteinText.setText(current.getProtein());
            holder.fatText.setText(current.getFat());
            holder.carbsText.setText(current.getCarbs());
        } else {
            holder.nameText.setText("No Value");
        }
    }

    @Override
    public int getItemCount() {
        return foodsList.size();
    }

    public void setTerms(ArrayList<FoodEntity> terms) {
        foodsList = terms;
        notifyDataSetChanged();
    }
}