package com.example.mal2017restaurantmanagementapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {

    private List<MenuItemModel> list;

    public MenuAdapter(List<MenuItemModel> list) {
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_menu_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MenuItemModel item = list.get(position);

        holder.ivDish.setImageResource(item.imageRes);
        holder.tvTag.setText(item.tag);
        holder.tvTitle.setText(item.title);
        holder.tvDesc.setText(item.desc);
        holder.tvPrice.setText(item.price);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivDish;
        TextView tvTag, tvTitle, tvDesc, tvPrice;

        public ViewHolder(View itemView) {
            super(itemView);

            ivDish = itemView.findViewById(R.id.iv_dish);
            tvTag = itemView.findViewById(R.id.tv_tag);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvDesc = itemView.findViewById(R.id.tv_desc);
            tvPrice = itemView.findViewById(R.id.tv_price);
        }
    }
}
