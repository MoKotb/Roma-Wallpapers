package com.yomko.romawallpapers.Admin.Categories;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.shashank.sony.fancydialoglib.Animation;
import com.shashank.sony.fancydialoglib.FancyAlertDialog;
import com.shashank.sony.fancydialoglib.FancyAlertDialogListener;
import com.shashank.sony.fancydialoglib.Icon;
import com.tuyenmonkey.mkloader.MKLoader;
import com.yomko.romawallpapers.Model.Category;
import com.yomko.romawallpapers.R;
import com.yomko.romawallpapers.Utility.Validation;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CategoriesActivity extends AppCompatActivity {

    @BindView(R.id.categoriesImage)
    ImageView categoriesImage;
    @BindView(R.id.nameEdt)
    EditText nameEdt;
    @BindView(R.id.AddBtn)
    Button AddBtn;
    @BindView(R.id.categoriesView)
    NestedScrollView categoriesView;
    @BindView(R.id.loading)
    MKLoader loading;

    private int REQUEST_GALLERY = 123;
    private final int MY_PERMISSIONS_REQUEST = 111;
    private Snackbar snackbar;
    private String message;
    private Category category;
    private FirebaseDatabase database;
    private StorageReference storage;
    private Uri filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        setTitle(getResources().getString(R.string.categoryTitle));

        ButterKnife.bind(this);
        storage = FirebaseStorage.getInstance().getReference();
        database = FirebaseDatabase.getInstance();
        category = new Category();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.MEDIA_CONTENT_CONTROL)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.MEDIA_CONTENT_CONTROL}, MY_PERMISSIONS_REQUEST);

        }

        categoriesImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(CategoriesActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(CategoriesActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(CategoriesActivity.this, Manifest.permission.MEDIA_CONTENT_CONTROL)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(CategoriesActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.MEDIA_CONTENT_CONTROL}, MY_PERMISSIONS_REQUEST);
                } else {
                    SelectImage();
                }
            }
        });

        AddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCategory();
            }
        });
    }

    private void addCategory() {
        if (Validation.validateName(nameEdt)) {
            if (filePath != null) {
                loading.setVisibility(View.VISIBLE);
                final Category[] newCategory = new Category[1];
                StorageReference reference = storage.child(getResources().getString(R.string.Photos) + filePath.getLastPathSegment());
                reference.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        loading.setVisibility(View.GONE);
                        message = nameEdt.getText().toString() + " " + getResources().getString(R.string.addSuccessfully);
                        newCategory[0] = new Category(nameEdt.getText().toString(), taskSnapshot.getDownloadUrl().toString());
                        DatabaseReference reference = database.getReference().child(getResources().getString(R.string.Categories));
                        reference.push().setValue(newCategory[0]);
                        Toast.makeText(CategoriesActivity.this, "" + message, Toast.LENGTH_LONG).show();
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loading.setVisibility(View.GONE);
                        Toast.makeText(CategoriesActivity.this, getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
                    }
                })
                ;
            } else {
                message = getResources().getString(R.string.invalidCategoryImage);
                showMessage(message);
            }
        } else {
            message = getResources().getString(R.string.invalidCategoryName);
            showMessage(message);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    SelectImage();
                }
                return;
            }
        }
    }

    private void SelectImage() {
        new FancyAlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.CategoryImage))
                .setBackgroundColor(getResources().getColor(R.color.colorRad))  //Don't pass R.color.colorvalue
                .setNegativeBtnText(getResources().getString(R.string.Gallery))
                .setNegativeBtnBackground(getResources().getColor(R.color.colorRad))  //Don't pass R.color.colorvalue
                .setPositiveBtnText(getResources().getString(R.string.Cancel))
                .setPositiveBtnBackground(getResources().getColor(R.color.colorRad))  //Don't pass R.color.colorvalue
                .setAnimation(Animation.POP)
                .isCancellable(true)
                .setIcon(R.drawable.camera, Icon.Visible)
                .OnNegativeClicked(new FancyAlertDialogListener() {
                    @Override
                    public void OnClick() {
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType(getResources().getString(R.string.imageType));
                        startActivityForResult(intent.createChooser(intent, getResources().getString(R.string.SelectFile)), REQUEST_GALLERY);
                    }
                })
                .build();
    }

    private void showMessage(String message) {
        snackbar = Snackbar.make(categoriesView, message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_GALLERY) {
                filePath = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                    categoriesImage.setImageBitmap(bitmap);
                    category.setCategoryImage(filePath.getLastPathSegment());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
