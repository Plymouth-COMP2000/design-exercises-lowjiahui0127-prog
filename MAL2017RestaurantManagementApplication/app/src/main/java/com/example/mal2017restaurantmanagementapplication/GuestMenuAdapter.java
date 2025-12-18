package com.example.mal2017restaurantmanagementapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class GuestMenuAdapter extends RecyclerView.Adapter<GuestMenuAdapter.ViewHolder> {

    private Context context;
    private List<MenuItem> menuItems;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(MenuItem menuItem);
    }

    public GuestMenuAdapter(Context context, List<MenuItem> menuItems) {
        this.context = context;
        this.menuItems = menuItems;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_menu_card_guest, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MenuItem menuItem = menuItems.get(position);

        // Get image resource ID from image path
        int imageResId = context.getResources().getIdentifier(
                menuItem.getImagePath(),
                "drawable",
                context.getPackageName()
        );

        if (imageResId != 0) {
            holder.ivDish.setImageResource(imageResId);
        }

        holder.tvTag.setText(menuItem.getCategory());
        holder.tvTitle.setText(menuItem.getName());
        holder.tvDesc.setText(menuItem.getDescription());
        holder.tvPrice.setText(menuItem.getPrice());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(menuItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return menuItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivDish;
        TextView tvTag, tvTitle, tvDesc, tvPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivDish = itemView.findViewById(R.id.iv_dish);
            tvTag = itemView.findViewById(R.id.tv_tag);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvDesc = itemView.findViewById(R.id.tv_desc);
            tvPrice = itemView.findViewById(R.id.tv_price);
        }
    }
}