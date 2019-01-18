package com.yomko.romawallpapers.Category;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yomko.romawallpapers.Model.Category;
import com.yomko.romawallpapers.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private ArrayList<Category> categoryArrayList;
    private Context context;
    private OnItemClick listener;

    public CategoryAdapter(Context context, OnItemClick listener) {
        this.context = context;
        this.listener = listener;
        categoryArrayList = new ArrayList<>();
    }

    public interface OnItemClick {
        void onItemClick(int position);
    }

    public void setCategoryArrayList(ArrayList<Category> arrayList) {
        this.categoryArrayList.addAll(arrayList);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Picasso.get().load(categoryArrayList.get(position).getCategoryImage()).placeholder(R.drawable.roma_background).error(R.drawable.roma_background).into(holder.categoryImage);
        holder.categoryText.setText(categoryArrayList.get(position).getCategoryName());
    }

    @Override
    public int getItemCount() {
        if (categoryArrayList == null) {
            return 0;
        }
        return categoryArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private CircleImageView categoryImage;
        private TextView categoryText;
        private ConstraintLayout categoryItem;

        public ViewHolder(final View view) {
            super(view);
            categoryImage = view.findViewById(R.id.categoryImage);
            categoryText = view.findViewById(R.id.categoryText);
            categoryItem = view.findViewById(R.id.categoryItem);
            categoryItem.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onItemClick(getAdapterPosition());
        }
    }
}
