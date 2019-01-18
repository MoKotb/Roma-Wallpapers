package com.yomko.romawallpapers.Recent;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.yomko.romawallpapers.Model.Image;
import com.yomko.romawallpapers.R;

import java.util.ArrayList;

public class RecentAdapter extends RecyclerView.Adapter<RecentAdapter.ViewHolder> {

    private ArrayList<Image> imagesArrayList;
    private Context context;
    private OnItemClick listener;

    public RecentAdapter(Context context, OnItemClick listener) {
        this.context = context;
        this.listener = listener;
        imagesArrayList = new ArrayList<>();
    }

    public interface OnItemClick {
        void onItemClick(int position);
    }

    public void setImagesArrayList(ArrayList<Image> arrayList) {
        this.imagesArrayList.addAll(arrayList);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recent_item, parent, false);
        return new RecentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Picasso.get().load(imagesArrayList.get(position).getImageURL()).placeholder(R.drawable.roma_background).error(R.drawable.roma_background).into(holder.imageItem);
    }

    @Override
    public int getItemCount() {
        if (imagesArrayList == null) {
            return 0;
        }
        return imagesArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView imageItem;

        public ViewHolder(final View view) {
            super(view);
            imageItem = view.findViewById(R.id.imageItem);
            imageItem.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onItemClick(getAdapterPosition());
        }
    }
}
