package com.udacity.stockhawk.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.sync.QuoteSyncJob;
import com.udacity.stockhawk.ui.DetailActivity;

/**
 * Created by morals on 20/3/17.
 */

public class StockWidgetProvider extends AppWidgetProvider {
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
}
