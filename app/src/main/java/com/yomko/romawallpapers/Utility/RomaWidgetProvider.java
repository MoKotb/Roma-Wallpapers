package com.yomko.romawallpapers.Utility;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.RemoteViews;

import com.yomko.romawallpapers.Home.HomeActivity;
import com.yomko.romawallpapers.R;

import java.io.IOException;
import java.net.URL;

public class RomaWidgetProvider extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId, String imageURL, String imageName) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.roma_widget_provider);

        Intent intent = new Intent(context, HomeActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        views.setTextViewText(R.id.widgetText, imageName);
        URL url = null;
        try {
            url = new URL(imageURL);
            Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            views.setImageViewBitmap(R.id.widgetImage, bmp);
        } catch (IOException e) {
            e.printStackTrace();
        }
        views.setOnClickPendingIntent(R.id.widgetImage, pendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    public static void UpdateAll(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds, String imageURL, String imageName) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, imageURL, imageName);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        WidgetService.startAction(context);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

