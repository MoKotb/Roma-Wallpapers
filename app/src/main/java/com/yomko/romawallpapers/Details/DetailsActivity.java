package com.yomko.romawallpapers.Details;

import android.Manifest;
import android.app.WallpaperManager;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.squareup.picasso.Picasso;
import com.tuyenmonkey.mkloader.MKLoader;
import com.yomko.romawallpapers.Model.Image;
import com.yomko.romawallpapers.Model.User;
import com.yomko.romawallpapers.R;
import com.yomko.romawallpapers.Utility.Database.DatabaseContract;
import com.yomko.romawallpapers.Utility.Database.DatabaseHelper;
import com.yomko.romawallpapers.Utility.WidgetService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailsActivity extends AppCompatActivity {

    private final int MY_PERMISSIONS_REQUEST = 156;
    @BindView(R.id.detailsImage)
    ImageView detailsImage;
    @BindView(R.id.saveFab)
    FloatingActionButton saveFab;
    @BindView(R.id.shareFab)
    FloatingActionButton shareFab;
    @BindView(R.id.wallpaperFab)
    FloatingActionButton wallpaperFab;
    @BindView(R.id.loading)
    MKLoader loading;
    Bitmap bitmap1, bitmap2;
    DisplayMetrics displayMetrics;
    int width, height;
    BitmapDrawable bitmapDrawable;
    private Image imageItem;
    private DatabaseHelper helper;
    private SQLiteDatabase database;
    private User newUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        ButterKnife.bind(this);
        Bundle extras = getIntent().getExtras();
        helper = new DatabaseHelper(this);
        if (extras != null) {
            imageItem = extras.getParcelable(getResources().getString(R.string.Image));
            Picasso.get().load(imageItem.getImageURL()).placeholder(R.drawable.roma_background).error(R.drawable.roma_background).into(detailsImage);
            setTitle(imageItem.getImageName());
            SaveUserData(imageItem.getImageURL(), imageItem.getImageName());
        }

        saveFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(DetailsActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(DetailsActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(DetailsActivity.this, Manifest.permission.MEDIA_CONTENT_CONTROL)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(DetailsActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.MEDIA_CONTENT_CONTROL}, MY_PERMISSIONS_REQUEST);
                    saveImage();
                } else {
                    saveImage();
                }
            }
        });

        shareFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(DetailsActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(DetailsActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(DetailsActivity.this, Manifest.permission.MEDIA_CONTENT_CONTROL)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(DetailsActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.MEDIA_CONTENT_CONTROL}, MY_PERMISSIONS_REQUEST);
                    shareImage();
                } else {
                    shareImage();
                }
            }
        });

        wallpaperFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setWallpaper();
            }
        });

    }

    private void SaveUserData(String imageURL, String imageName) {
        SharedPreferences sharedpreferences = getSharedPreferences(getResources().getString(R.string.SaveImage), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(getResources().getString(R.string.imageURL), imageURL);
        editor.putString(getResources().getString(R.string.imageName), imageName);
        editor.apply();
        WidgetService.startAction(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.details_menu, menu);
        if (getImage(imageItem.getImageID()) > 0) {
            menu.getItem(0).setIcon(R.drawable.favorite);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.favoriteItem:
                if (getImage(imageItem.getImageID()) > 0) {
                    deleteFromFavorite(imageItem.getImageID());
                    item.setIcon(R.drawable.unfavorite);
                } else {
                    addToFavorite(imageItem);
                    item.setIcon(R.drawable.favorite);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setWallpaper() {
        loading.setVisibility(View.VISIBLE);
        bitmapDrawable = (BitmapDrawable) detailsImage.getDrawable();
        bitmap1 = bitmapDrawable.getBitmap();
        GetScreenWidthHeight();
        SetBitmapSize();
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
        try {
            wallpaperManager.setBitmap(bitmap2);
            wallpaperManager.suggestDesiredDimensions(width, height);
            Toast.makeText(this, getResources().getString(R.string.WallpaperSet), Toast.LENGTH_LONG).show();
            loading.setVisibility(View.GONE);
        } catch (IOException e) {
            e.printStackTrace();
            loading.setVisibility(View.GONE);
            Toast.makeText(this, getResources().getString(R.string.WallpaperNotSet), Toast.LENGTH_LONG).show();
        }
    }

    public void GetScreenWidthHeight() {
        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        width = displayMetrics.widthPixels;
        height = displayMetrics.heightPixels;
    }

    public void SetBitmapSize() {
        bitmap2 = Bitmap.createScaledBitmap(bitmap1, width, height, false);
    }

    private void shareImage() {
        loading.setVisibility(View.VISIBLE);
        Bitmap bitmap = getBitmapFromView(detailsImage);
        try {
            File file = new File(this.getExternalCacheDir(), getResources().getString(R.string.logicchippng));
            FileOutputStream fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
            file.setReadable(true, false);
            final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            intent.setType(getResources().getString(R.string.imagepng));
            startActivity(Intent.createChooser(intent, getResources().getString(R.string.Shareimagevia)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        loading.setVisibility(View.GONE);
    }

    private Bitmap getBitmapFromView(View view) {
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null) {
            bgDrawable.draw(canvas);
        } else {
            canvas.drawColor(Color.WHITE);
        }
        view.draw(canvas);
        return returnedBitmap;
    }

    private void saveImage() {
        BitmapDrawable draw = (BitmapDrawable) detailsImage.getDrawable();
        Bitmap bitmap = draw.getBitmap();

        FileOutputStream outStream = null;
        File sdCard = Environment.getExternalStorageDirectory();
        File dir = new File(sdCard.getAbsolutePath() + "/" + getResources().getString(R.string.app_name));
        dir.mkdirs();
        String fileName = String.format("%d.jpg", System.currentTimeMillis());
        File outFile = new File(dir, fileName);
        try {
            outStream = new FileOutputStream(outFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.flush();
            outStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(outFile));
        sendBroadcast(intent);
        Toast.makeText(this, getResources().getString(R.string.ImageSavedSuccessfully), Toast.LENGTH_SHORT).show();
    }

    private void addToFavorite(Image image) {
        database = helper.getWritableDatabase();
        ContentValues insertValues = new ContentValues();
        insertValues.put(DatabaseContract.DatabaseEntry.IMAGE_ID, image.getImageID());
        insertValues.put(DatabaseContract.DatabaseEntry.IMAGE_NAME, image.getImageName());
        insertValues.put(DatabaseContract.DatabaseEntry.IMAGE_URL, image.getImageURL());
        insertValues.put(DatabaseContract.DatabaseEntry.USER_ID, getUserData().getUserID());
        long id = database.insert(DatabaseContract.DatabaseEntry.IMAGE_TABLE_NAME, null, insertValues);
        Toast.makeText(this, getResources().getString(R.string.addToFav), Toast.LENGTH_LONG).show();
    }

    private void deleteFromFavorite(String imageID) {
        String selection = DatabaseContract.DatabaseEntry.USER_ID + " =? AND " + DatabaseContract.DatabaseEntry.IMAGE_ID + " =?";
        String[] selectionArgs = {getUserData().getUserID(), imageID};
        database.delete(DatabaseContract.DatabaseEntry.IMAGE_TABLE_NAME, selection, selectionArgs);
        Toast.makeText(this, getResources().getString(R.string.removeFromFav), Toast.LENGTH_LONG).show();
    }

    private User getUserData() {
        User user = new User();
        SharedPreferences sharedPreferences = getSharedPreferences(getResources().getString(R.string.SaveUser), MODE_PRIVATE);
        user.setUserID(sharedPreferences.getString(getResources().getString(R.string.userID), null));
        user.setUserEmail(sharedPreferences.getString(getResources().getString(R.string.userEmail), null));
        return user;
    }

    private int getImage(String imageID) {
        database = helper.getWritableDatabase();
        String[] columns = {DatabaseContract.DatabaseEntry.IMAGE_ID, DatabaseContract.DatabaseEntry.IMAGE_NAME
                , DatabaseContract.DatabaseEntry.IMAGE_URL};
        String selection = DatabaseContract.DatabaseEntry.USER_ID + " =? AND " + DatabaseContract.DatabaseEntry.IMAGE_ID + " =?";
        String[] selectionArgs = {getUserData().getUserID(), imageID};
        Cursor cursor = database.query(DatabaseContract.DatabaseEntry.IMAGE_TABLE_NAME, columns, selection, selectionArgs, null, null, null, null);
        int count = 0;
        while (cursor.moveToNext()) {
            count++;
        }
        return count;
    }
}
