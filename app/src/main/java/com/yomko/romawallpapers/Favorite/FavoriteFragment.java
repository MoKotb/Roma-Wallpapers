package com.yomko.romawallpapers.Favorite;


import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yomko.romawallpapers.Details.DetailsActivity;
import com.yomko.romawallpapers.Model.Image;
import com.yomko.romawallpapers.Model.User;
import com.yomko.romawallpapers.R;
import com.yomko.romawallpapers.Recent.RecentAdapter;
import com.yomko.romawallpapers.Utility.Database.DatabaseContract;
import com.yomko.romawallpapers.Utility.Database.DatabaseHelper;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.MODE_PRIVATE;

public class FavoriteFragment extends Fragment implements RecentAdapter.OnItemClick {

    @BindView(R.id.favoriteRecycler)
    RecyclerView favoriteRecycler;
    @BindView(R.id.offlineText)
    TextView offlineText;
    @BindView(R.id.offlineImage)
    ImageView offlineImage;

    private RecentAdapter adapter;
    private GridLayoutManager gridLayoutManager;
    private String message;
    private DatabaseHelper helper;
    private SQLiteDatabase database;
    private ArrayList<Image> imageArrayList;
    private User newUser;

    public FavoriteFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.favorite));
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        helper = new DatabaseHelper(getActivity());
        ButterKnife.bind(this, view);
        checkRecycler();
        imageArrayList = new ArrayList<>();
        getImages();
        if (imageArrayList.size() == 0) {
            setOfflineMode();
        } else {
            adapter.setImagesArrayList(imageArrayList);
        }

        return view;
    }

    private void checkRecycler() {
        adapter = new RecentAdapter(getActivity(), this);
        gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        favoriteRecycler.setAdapter(adapter);
        favoriteRecycler.setLayoutManager(gridLayoutManager);
        favoriteRecycler.setNestedScrollingEnabled(false);
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(getActivity(), DetailsActivity.class);
        intent.putExtra(getResources().getString(R.string.Image), imageArrayList.get(position));
        startActivity(intent);
    }

    private void setOfflineMode() {
        message = getResources().getString(R.string.offlineText);
        offlineText.setText(message);
        offlineText.setVisibility(View.VISIBLE);
        offlineImage.setVisibility(View.VISIBLE);
    }

    private void getImages() {
        database = helper.getWritableDatabase();
        ArrayList<Image> imageArrayList = new ArrayList<>();
        Image image;
        String[] columns = {DatabaseContract.DatabaseEntry.IMAGE_ID, DatabaseContract.DatabaseEntry.IMAGE_NAME
                , DatabaseContract.DatabaseEntry.IMAGE_URL};
        String selection = DatabaseContract.DatabaseEntry.USER_ID + " =?";
        String[] selectionArgs = {getUserData().getUserID()};
        Cursor cursor = database.query(DatabaseContract.DatabaseEntry.IMAGE_TABLE_NAME, columns, selection, selectionArgs, null, null, null, null);
        while (cursor.moveToNext()) {
            image = new Image();
            int index0 = cursor.getColumnIndex(DatabaseContract.DatabaseEntry.IMAGE_ID);
            int index1 = cursor.getColumnIndex(DatabaseContract.DatabaseEntry.IMAGE_NAME);
            int index2 = cursor.getColumnIndex(DatabaseContract.DatabaseEntry.IMAGE_URL);

            image.setImageID(cursor.getString(index0));
            image.setImageName(cursor.getString(index1));
            image.setImageURL(cursor.getString(index2));
            imageArrayList.add(image);
        }
        this.imageArrayList = imageArrayList;
    }

    private User getUserData() {
        User user = new User();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(getResources().getString(R.string.SaveUser), MODE_PRIVATE);
        user.setUserID(sharedPreferences.getString(getResources().getString(R.string.userID), null));
        user.setUserEmail(sharedPreferences.getString(getResources().getString(R.string.userEmail), null));
        return user;
    }

}
