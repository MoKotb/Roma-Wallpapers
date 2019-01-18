package com.yomko.romawallpapers.Recent;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tuyenmonkey.mkloader.MKLoader;
import com.yomko.romawallpapers.Details.DetailsActivity;
import com.yomko.romawallpapers.Model.Category;
import com.yomko.romawallpapers.Model.Image;
import com.yomko.romawallpapers.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecentFragment extends Fragment implements RecentAdapter.OnItemClick {

    @BindView(R.id.recentRecycler)
    RecyclerView recentRecycler;
    @BindView(R.id.loading)
    MKLoader loading;
    @BindView(R.id.offlineText)
    TextView offlineText;
    @BindView(R.id.offlineImage)
    ImageView offlineImage;

    private View root;
    private RecentAdapter adapter;
    private GridLayoutManager gridLayoutManager;
    private String message;
    private DatabaseReference databaseReference, databaseReference2;
    private FirebaseDatabase database;
    private ArrayList<Image> imageArrayList;
    private ArrayList<Category> categoryArrayList;

    public RecentFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_recent, container, false);

        ButterKnife.bind(this, root);
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference(getResources().getString(R.string.Images));
        databaseReference2 = database.getReference(getResources().getString(R.string.Categories));

        checkRecycler();
        if (getArguments() != null) {
            String type = getArguments().getString(getResources().getString(R.string.CategoryType));
            downloadCategories(type);
        } else {
            downloadCategories(getResources().getString(R.string.Recent));
        }
        return root;
    }

    private void checkRecycler() {
        adapter = new RecentAdapter(getActivity(), this);
        gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        recentRecycler.setAdapter(adapter);
        recentRecycler.setLayoutManager(gridLayoutManager);
        recentRecycler.setNestedScrollingEnabled(false);
    }


    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(getActivity(), DetailsActivity.class);
        intent.putExtra(getResources().getString(R.string.Image), imageArrayList.get(position));
        startActivity(intent);
    }

    private void downloadImages(final String type) {
        imageArrayList = new ArrayList<>();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Image image = snapshot.getValue(Image.class);
                    for (int i = 0; i < categoryArrayList.size(); i++) {
                        if (categoryArrayList.get(i).getCategoryName().equals(type)) {
                            if (image.getCategoryID().equals(categoryArrayList.get(i).getCategoryID())) {
                                image.setImageID(snapshot.getKey());
                                imageArrayList.add(image);
                            }
                        }
                    }
                }
                adapter.setImagesArrayList(imageArrayList);
                loading.setVisibility(View.GONE);
                if (imageArrayList.size() == 0) {
                    setOfflineMode();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }

    private void downloadCategories(final String type) {
        categoryArrayList = new ArrayList<>();
        loading.setVisibility(View.VISIBLE);
        databaseReference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Category category = snapshot.getValue(Category.class);
                    category.setCategoryID(snapshot.getKey());
                    categoryArrayList.add(category);
                }
                downloadImages(type);
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }

    private void setOfflineMode() {
        message = getResources().getString(R.string.offlineText);
        offlineText.setText(message);
        offlineText.setVisibility(View.VISIBLE);
        offlineImage.setVisibility(View.VISIBLE);
    }
}
