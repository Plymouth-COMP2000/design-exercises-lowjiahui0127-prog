package com.example.mal2017restaurantmanagementapplication;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.util.List;

public class StaffMenuAdapter extends RecyclerView.Adapter<StaffMenuAdapter.ViewHolder> {

    private Context context;
    private List<MenuItem> menuItems;
    private OnMenuItemClickListener listener;

    public interface OnMenuItemClickListener {
        void onEditClick(MenuItem menuItem);
        void onDeleteClick(MenuItem menuItem);
    }

    public StaffMenuAdapter(Context context, List<MenuItem> menuItems) {
        this.context = context;
        this.menuItems = menuItems;
    }

    public void setOnMenuItemClickListener(OnMenuItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_menu_card_staff, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MenuItem menuItem = menuItems.get(position);

        String imagePath = menuItem.getImagePath();
        if (imagePath != null && !imagePath.isEmpty()) {
            if (imagePath.contains("/") || imagePath.contains(".")) {
                try {
                    File imgFile = new File(imagePath);
                    if (imgFile.exists()) {
                        holder.ivDish.setImageURI(Uri.fromFile(imgFile));
                    } else {
                        loadDrawableResource(holder.ivDish, imagePath);
                    }
                } catch (Exception e) {
                    loadDrawableResource(holder.ivDish, imagePath);
                }
            } else {
                loadDrawableResource(holder.ivDish, imagePath);
            }
        } else {
            holder.ivDish.setImageResource(R.drawable.pizza1);
        }

        holder.tvTitle.setText(menuItem.getName());
        holder.tvCategory.setText(menuItem.getCategory());
        holder.tvPrice.setText(menuItem.getPrice());
        holder.tvDescription.setText(menuItem.getDescription());

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditClick(menuItem);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(menuItem);
            }
        });
    }

    private void loadDrawableResource(ImageView imageView, String imageName) {
        try {
            int imageResId = context.getResources().getIdentifier(
                    imageName,
                    "drawable",
                    context.getPackageName()
            );

            if (imageResId != 0) {
                imageView.setImageResource(imageResId);
            } else {
                imageView.setImageResource(R.drawable.pizza1);
            }
        } catch (Exception e) {
            imageView.setImageResource(R.drawable.pizza1);
        }
    }

    @Override
    public int getItemCount() {
        return menuItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivDish;
        TextView tvTitle, tvCategory, tvPrice, tvDescription;
        MaterialButton btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivDish = itemView.findViewById(R.id.iv_dish);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvCategory = itemView.findViewById(R.id.tv_category);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvDescription = itemView.findViewById(R.id.tv_description);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}