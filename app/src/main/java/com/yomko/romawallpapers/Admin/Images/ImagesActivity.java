package com.yomko.romawallpapers.Admin.Images;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.shashank.sony.fancydialoglib.Animation;
import com.shashank.sony.fancydialoglib.FancyAlertDialog;
import com.shashank.sony.fancydialoglib.FancyAlertDialogListener;
import com.shashank.sony.fancydialoglib.Icon;
import com.tuyenmonkey.mkloader.MKLoader;
import com.yomko.romawallpapers.Model.Category;
import com.yomko.romawallpapers.Model.Image;
import com.yomko.romawallpapers.R;
import com.yomko.romawallpapers.Utility.Validation;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.ganfra.materialspinner.MaterialSpinner;

public class ImagesActivity extends AppCompatActivity {

    @BindView(R.id.newImage)
    ImageView newImage;
    @BindView(R.id.nameEdt)
    EditText nameEdt;
    @BindView(R.id.AddBtn)
    Button AddBtn;
    @BindView(R.id.spinner)
    MaterialSpinner spinner;
    @BindView(R.id.imagesView)
    NestedScrollView imagesView;
    @BindView(R.id.loading)
    MKLoader loading;

    private int REQUEST_GALLERY = 123;
    private final int MY_PERMISSIONS_REQUEST = 111;
    private Snackbar snackbar;
    private String message;
    private Image image;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private StorageReference storage;
    private Uri filePath;
    private ArrayList<Category> categoryArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);
        setTitle(getResources().getString(R.string.ImageTitle));
        ButterKnife.bind(this);
        storage = FirebaseStorage.getInstance().getReference();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference(getResources().getString(R.string.Categories));
        categoryArrayList = new ArrayList<>();
        image = new Image();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.MEDIA_CONTENT_CONTROL)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.MEDIA_CONTENT_CONTROL}, MY_PERMISSIONS_REQUEST);

        }

        newImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(ImagesActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(ImagesActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(ImagesActivity.this, Manifest.permission.MEDIA_CONTENT_CONTROL)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ImagesActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.MEDIA_CONTENT_CONTROL}, MY_PERMISSIONS_REQUEST);
                } else {
                    SelectImage();
                }
            }
        });

        AddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewImage();
            }
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                loading.setVisibility(View.VISIBLE);
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Category category = snapshot.getValue(Category.class);
                    if (snapshot.getKey() != null)
                        category.setCategoryID(snapshot.getKey());
                    categoryArrayList.add(category);
                }
                setCategories(categoryArrayList);
                loading.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

    }

    private void addNewImage() {
        if (Validation.validateName(nameEdt)) {
            if (filePath != null) {
                if (spinner.getSelectedItem() != null) {
                    loading.setVisibility(View.VISIBLE);
                    final Image[] images = new Image[1];
                    StorageReference reference = storage.child(getResources().getString(R.string.Photos) + filePath.getLastPathSegment());
                    reference.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            loading.setVisibility(View.GONE);
                            message = nameEdt.getText().toString() + " " + getResources().getString(R.string.addSuccessfully);
                            images[0] = new Image(nameEdt.getText().toString(), taskSnapshot.getDownloadUrl().toString(), categoryArrayList.get(spinner.getSelectedItemPosition() - 1).getCategoryID());
                            DatabaseReference reference = database.getReference().child(getResources().getString(R.string.Images));
                            reference.push().setValue(images[0]);
                            Toast.makeText(ImagesActivity.this, "" + message, Toast.LENGTH_LONG).show();
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            loading.setVisibility(View.GONE);
                            Toast.makeText(ImagesActivity.this, getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    message = getResources().getString(R.string.invalidCategory);
                    showMessage(message);
                }
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
                .setTitle(getResources().getString(R.string.AddNewImage))
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
        snackbar = Snackbar.make(imagesView, message, Snackbar.LENGTH_LONG);
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
                    newImage.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setCategories(ArrayList<Category> categories) {
        ArrayList<String> strings = new ArrayList<>();
        for (int i = 0; i < categories.size(); i++) {
            strings.add(categories.get(i).getCategoryName());
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, strings);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
    }

}
