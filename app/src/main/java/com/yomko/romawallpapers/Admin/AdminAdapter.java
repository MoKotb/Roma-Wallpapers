package com.yomko.romawallpapers.Admin;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yomko.romawallpapers.Model.AdminView;
import com.yomko.romawallpapers.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdminAdapter extends RecyclerView.Adapter<AdminAdapter.ViewHolder> {

    private ArrayList<AdminView> adminArrayList;
    private Context context;
    private OnItemClick listener;

    public AdminAdapter(Context context, OnItemClick listener) {
        this.context = context;
        this.listener = listener;
        adminArrayList = new ArrayList<>();
    }

    public interface OnItemClick {
        void onItemClick(AdminView adminView);
    }

    public void setAdminArrayList(ArrayList<AdminView> arrayList) {
        this.adminArrayList.addAll(arrayList);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_item, parent, false);
        return new AdminAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Picasso.get().load(adminArrayList.get(position).getImage()).placeholder(R.drawable.roma_background).error(R.drawable.roma_background).into(holder.adminItemImage);
        holder.adminItemText.setText(adminArrayList.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        if (adminArrayList == null) {
            return 0;
        }
        return adminArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private CardView adminItemCard;
        private CircleImageView adminItemImage;
        private TextView adminItemText;

        public ViewHolder(final View view) {
            super(view);
            adminItemCard = view.findViewById(R.id.adminItemCard);
            adminItemImage = view.findViewById(R.id.adminItemImage);
            adminItemText = view.findViewById(R.id.adminItemText);
            adminItemCard.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onItemClick(adminArrayList.get(getAdapterPosition()));
        }
    }
}
