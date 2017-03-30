package com.udacity.stockhawk.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.widget.RemoteViews;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.sync.QuoteSyncJob;
import com.udacity.stockhawk.ui.MainActivity;
import com.udacity.stockhawk.ui.StockDetail;

/**
 * Created by morals on 20/3/17.
 */

public class StockWidgetProvider extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.stock_widget);
        setRemoteAdapter(context, remoteViews);

        // intent to launch main Activity
        Intent mainActivityIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                mainActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        // intent to launch stockDetail
        remoteViews.setOnClickPendingIntent(R.id.tv_widget_title, pendingIntent);


        Intent detailActivityIntent = new Intent(context, StockDetail.class);
        PendingIntent pendingIntentDetail = PendingIntent.getActivity(
                context,
                0,
                detailActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        remoteViews.setPendingIntentTemplate(R.id.lv_stock_widget, pendingIntentDetail);

        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.lv_stock_widget);
    }


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int i=0; i< appWidgetIds.length; i++)
        {
            updateAppWidget(context,appWidgetManager,appWidgetIds[i]);
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

    private static void setRemoteAdapter(Context context, @NonNull final RemoteViews remoteViews){
        remoteViews.setRemoteAdapter(
                R.id.lv_stock_widget,
                new Intent(context, StockWidgetRemoteViewsService.class)
        );
    }

}
