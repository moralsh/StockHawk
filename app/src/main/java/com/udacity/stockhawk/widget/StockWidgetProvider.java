package com.udacity.stockhawk.widget;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.RemoteViews;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.sync.QuoteSyncJob;
import com.udacity.stockhawk.ui.DetailActivity;
import com.udacity.stockhawk.ui.MainActivity;

/**
 * Created by morals on 20/3/17.
 */

public class StockWidgetProvider extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.stock_widget);

        //  Setting up the adapter
        setRemoteAdapter(context, views);


        //  Create Intent to launch TrendActivity
        Intent mainActivityIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                mainActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        views.setOnClickPendingIntent(R.id.tv_widget_title, pendingIntent);


        Intent detailActivityIntent = new Intent(context, DetailActivity.class);
        PendingIntent pendingIntentDetail = PendingIntent.getActivity(
                context,
                0,
                detailActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        views.setPendingIntentTemplate(R.id.lv_stock_widget, pendingIntentDetail);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.lv_stock_widget);
    }


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int i=0; i< appWidgetIds.length; i++)
        {
            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.stock_widget);
            rv.setRemoteAdapter(R.id.lv_stock_widget, new Intent(context, StockWidgetRemoteViewsService.class));

            Intent detailIntent = new Intent(context, DetailActivity.class);
            PendingIntent pendingTemplateIntent = PendingIntent.getActivity(context, 0 , detailIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            rv.setPendingIntentTemplate(R.id.lv_stock_widget, pendingTemplateIntent);
            appWidgetManager.updateAppWidget(appWidgetIds[i], rv);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(QuoteSyncJob.ACTION_DATA_UPDATED))
        {
            AppWidgetManager am = AppWidgetManager.getInstance(context);
            int [] appWidgetIds = am.getAppWidgetIds(new ComponentName(context, StockWidgetProvider.class));
            am.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.lv_stock_widget);

        }
        super.onReceive(context, intent);

    }

    private static void setRemoteAdapter(Context context, @NonNull final RemoteViews views){
        views.setRemoteAdapter(
                R.id.lv_stock_widget,
                new Intent(context, StockWidgetRemoteViewsService.class)
        );
    }

}
