package com.example.drawingmaker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PicAdapter extends RecyclerView.Adapter<PicAdapter.ViewHolder> {

    private final List<String> list;
    private final LayoutInflater layoutInflater;
    public final OnItemClick onItemClick;

    public PicAdapter(Context context, List<String> list, OnItemClick onItemClick) {
        this.list = list;
        this.layoutInflater = LayoutInflater.from(context);
        this.onItemClick = onItemClick;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.pic_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String item = list.get(position);
        holder.picName.setText(item);
        holder.itemView.setOnClickListener(v -> {
            onItemClick.onClick(item);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public interface OnItemClick{
        void onClick (String item);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{
        final TextView picName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            picName = itemView.findViewById(R.id.pic_name);
        }
    }
}
