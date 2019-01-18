package com.yomko.romawallpapers.Category;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
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
import com.yomko.romawallpapers.Model.Category;
import com.yomko.romawallpapers.R;
import com.yomko.romawallpapers.Recent.RecentFragment;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CategoryFragment extends Fragment implements CategoryAdapter.OnItemClick {

    @BindView(R.id.categoryRecycler)
    RecyclerView categoryRecycler;
    @BindView(R.id.loading)
    MKLoader loading;
    @BindView(R.id.offlineText)
    TextView offlineText;
    @BindView(R.id.offlineImage)
    ImageView offlineImage;

    private View root;
    private CategoryAdapter adapter;
    private LinearLayoutManager linearLayoutManager;
    private String message;
    private DatabaseReference databaseReference;
    private FirebaseDatabase database;
    private ArrayList<Category> categoryArrayList;

    public CategoryFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_category, container, false);

        ButterKnife.bind(this, root);

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference(getResources().getString(R.string.Categories));

        checkRecycler();

        downloadCategories();

        return root;
    }

    private void checkRecycler() {
        adapter = new CategoryAdapter(getActivity(), this);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        categoryRecycler.setAdapter(adapter);
        categoryRecycler.setLayoutManager(linearLayoutManager);
        categoryRecycler.setNestedScrollingEnabled(false);
    }


    @Override
    public void onItemClick(int position) {
        RecentFragment recentFragment = new RecentFragment();
        FragmentManager manager = getFragmentManager();
        Bundle bundle = new Bundle();
        bundle.putString(getResources().getString(R.string.CategoryType), categoryArrayList.get(position).getCategoryName());
        recentFragment.setArguments(bundle);
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.categoryContainer, recentFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void downloadCategories() {
        categoryArrayList = new ArrayList<>();
        loading.setVisibility(View.VISIBLE);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Category category = snapshot.getValue(Category.class);
                    category.setCategoryID(snapshot.getKey());
                    if (!category.getCategoryName().equals(getResources().getString(R.string.Recent))) {
                        categoryArrayList.add(category);
                    }
                }
                adapter.setCategoryArrayList(categoryArrayList);
                if (categoryArrayList.size() == 0) {
                    setOfflineMode();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
        loading.setVisibility(View.GONE);
    }

    private void setOfflineMode() {
        message = getResources().getString(R.string.offlineText);
        offlineText.setText(message);
        offlineText.setVisibility(View.VISIBLE);
        offlineImage.setVisibility(View.VISIBLE);
    }

}
