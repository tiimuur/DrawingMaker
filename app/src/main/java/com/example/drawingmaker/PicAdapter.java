package com.example.drawingmaker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PicAdapter extends RecyclerView.Adapter<PicAdapter.ViewHolder> {

    Context context;
    private final List<ListItem> list;
    private final LayoutInflater layoutInflater;

    public PicAdapter(Context context, List<ListItem> list) {
        this.context = context;
        this.list = list;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.pic_item, parent, false);
        return new ViewHolder(view, context, list);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ListItem item = list.get(position);
        holder.picName.setText(item.getTitle());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateAdapter(List<ListItem> someList){
        list.clear();
        list.addAll(someList);
        notifyDataSetChanged();
    }

    public void  removeItem(int pos, DBManager dbManager){
        dbManager.openDB();
        dbManager.deleteInDB(list.get(pos).getId());
        System.out.println(list.get(pos).getId());
        list.remove(pos);
        notifyItemRangeChanged(0, list.size());
        notifyItemRemoved(pos);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        final TextView picName;
        private final Context context;
        private final List<ListItem> list;

        public ViewHolder(@NonNull View itemView, Context context, List<ListItem> list) {
            super(itemView);
            this.context = context;
            this.list = list;
            picName = itemView.findViewById(R.id.pic_name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, PicActivity.class);
            intent.putExtra(Constants.LIST_ITEM_KEY, list.get(getAdapterPosition()));
            intent.putExtra(Constants.NEW_KEY, false);
            context.startActivity(intent);
        }
    }
}
