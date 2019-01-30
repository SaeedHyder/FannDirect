package com.app.fandirect.ui.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.fandirect.R;
import com.app.fandirect.entities.Category;
import com.app.fandirect.entities.GetMyFannsEnt;

import java.util.ArrayList;
import java.util.List;

public class CategoryDropdownAdapter extends RecyclerView.Adapter<CategoryDropdownAdapter.CategoryViewHolder> {

    //  private List<Category> categories;
    private ArrayList<GetMyFannsEnt> collection = new ArrayList<>();
    private CategorySelectedListener categorySelectedListener;
    private String UserId = "";

    public CategoryDropdownAdapter(ArrayList<GetMyFannsEnt> collection, String UserId) {
        super();
        this.collection = collection;
        this.UserId = UserId;
    }


    public void setCategorySelectedListener(CategoryDropdownAdapter.CategorySelectedListener categorySelectedListener) {
        this.categorySelectedListener = categorySelectedListener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CategoryViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, final int position) {
        final GetMyFannsEnt entity = collection.get(position);

        if (entity.getSenderDetail().getId().equals(String.valueOf(UserId))) {
            if (entity.getReceiverDetail() != null) {
                holder.tvCategory.setText(entity.getReceiverDetail().getUserName() + "");

            }
        } else if (entity.getSenderDetail() != null) {
            holder.tvCategory.setText(entity.getSenderDetail().getUserName() + "");
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (categorySelectedListener != null) {
                    categorySelectedListener.onCategorySelected(position, entity);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return collection.size();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategory;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            tvCategory = itemView.findViewById(R.id.name);

        }
    }

    public interface CategorySelectedListener {
        void onCategorySelected(int position, GetMyFannsEnt category);
    }
}

