package com.yomko.romawallpapers.Utility;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.yomko.romawallpapers.R;

public class WidgetService extends IntentService {

    public static final String ACTION_IMAGE = "com.yomko.romawallpapers.action.roma";

    public WidgetService() {
        super("WidgetService");
    }

    public static void startAction(Context context) {
        Intent intent = new Intent(context, WidgetService.class);
        intent.setAction(ACTION_IMAGE);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_IMAGE.equals(action)) {
                handleActionImage();
            }
        }
    }

    private void handleActionImage() {
        SharedPreferences sharedPreferences = getSharedPreferences(getResources().getString(R.string.SaveImage), MODE_PRIVATE);
        String imageURL = sharedPreferences.getString(getResources().getString(R.string.imageURL), null);
        String imageName = sharedPreferences.getString(getResources().getString(R.string.imageName), null);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int appIDs[] = appWidgetManager.getAppWidgetIds(new ComponentName(this, RomaWidgetProvider.class));
        RomaWidgetProvider.UpdateAll(this, appWidgetManager, appIDs, imageURL, imageName);
    }
}