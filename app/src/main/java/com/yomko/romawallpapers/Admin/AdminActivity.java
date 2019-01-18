package com.yomko.romawallpapers.Admin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.tuyenmonkey.mkloader.MKLoader;
import com.yomko.romawallpapers.Admin.Administration.AdministrationActivity;
import com.yomko.romawallpapers.Admin.Categories.CategoriesActivity;
import com.yomko.romawallpapers.Admin.Images.ImagesActivity;
import com.yomko.romawallpapers.Login.LoginActivity;
import com.yomko.romawallpapers.Model.AdminView;
import com.yomko.romawallpapers.Model.User;
import com.yomko.romawallpapers.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AdminActivity extends AppCompatActivity implements AdminAdapter.OnItemClick {

    @BindView(R.id.adminRecycler)
    RecyclerView adminRecycler;
    @BindView(R.id.loading)
    MKLoader loading;
    @BindView(R.id.offlineText)
    TextView offlineText;

    private AdminAdapter adapter;
    private GridLayoutManager gridLayoutManager;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        ButterKnife.bind(this);

        setTitle(getUserData().getUserName());

        checkRecycler();
    }

    private void checkRecycler() {
        adapter = new AdminAdapter(this, this);
        gridLayoutManager = new GridLayoutManager(this, 2);
        adminRecycler.setAdapter(adapter);
        adminRecycler.setLayoutManager(gridLayoutManager);
        adminRecycler.setNestedScrollingEnabled(false);
        ArrayList<AdminView> views = new ArrayList<>();
        views.add(new AdminView(getResources().getString(R.string.Administration), R.drawable.admin_icon));
        views.add(new AdminView(getResources().getString(R.string.Categories), R.drawable.categories_icon));
        views.add(new AdminView(getResources().getString(R.string.Images), R.drawable.images_icon));
        adapter.setAdminArrayList(views);
    }

    @Override
    public void onItemClick(AdminView adminView) {
        String type = adminView.getTitle();
        if (type.equals(getResources().getString(R.string.Administration))) {
            intent = new Intent(this, AdministrationActivity.class);
            startActivity(intent);
        } else if (type.equals(getResources().getString(R.string.Categories))) {
            intent = new Intent(this, CategoriesActivity.class);
            startActivity(intent);
        } else if (type.equals(getResources().getString(R.string.Images))) {
            intent = new Intent(this, ImagesActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.admin_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private User getUserData() {
        User user = new User();
        SharedPreferences sharedPreferences = getSharedPreferences(getResources().getString(R.string.SaveUser), MODE_PRIVATE);
        user.setUserID(sharedPreferences.getString(getResources().getString(R.string.userID), null));
        user.setUserEmail(sharedPreferences.getString(getResources().getString(R.string.userEmail), null));
        user.setUserName(sharedPreferences.getString(getResources().getString(R.string.userName), null));
        return user;
    }
}
