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

import com.jldevtech.mactracker.ControllerFolder.AddFoodFromSearch;
import com.jldevtech.mactracker.R;
import com.fatsecret.platform.model.CompactFood;

import java.util.ArrayList;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.FoodViewHolder>{
    private ArrayList<CompactFood> foodsList;
    private final Context context;
    private final LayoutInflater mInflater;

    public SearchAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        this.context = context;
    }

    public class FoodViewHolder extends RecyclerView.ViewHolder {
        private TextView nameText;
        private TextView descriptionText;

        public FoodViewHolder(final View view) {
            super(view);
            nameText = view.findViewById(R.id.nameFood);
            descriptionText = view.findViewById(R.id.description);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    final CompactFood current = foodsList.get(position);
                    Intent intent = new Intent(context, AddFoodFromSearch.class);
                    System.out.println(current.getId());
                    intent.putExtra("foodId", current.getId());
                    intent.putExtra("foodName", current.getName());
                    intent.putExtra("foodDescription" , current.getDescription());
                    context.startActivity(intent);
                    ((Activity)context).finish();
                }
            });
        }
    }

    @NonNull
    @Override
    public SearchAdapter.FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_rows_titles_food_search, parent, false);
        return new FoodViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.FoodViewHolder holder, int position) {
        if (foodsList != null) {
            final CompactFood current = foodsList.get(position);
            holder.nameText.setText(current.getName());
            holder.descriptionText.setText(current.getDescription());
        } else {
            holder.nameText.setText("No Value");
        }
    }

    @Override
    public int getItemCount() {
        return foodsList.size();
    }

    public void setTerms(ArrayList<CompactFood> terms) {
        foodsList = terms;
        notifyDataSetChanged();
    }

}
