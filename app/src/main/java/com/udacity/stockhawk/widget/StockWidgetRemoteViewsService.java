package com.udacity.stockhawk.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Binder;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.PrefUtils;

import java.text.DecimalFormat;
import java.util.Locale;

/**
 * Created by morals on 24/03/17.
 */



public class StockWidgetRemoteViewsService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            Cursor mCursor = null;
            public final String SymbolNameKey = "SYMBOL_NAME";


            private final String[] StockProjection = {
                    Contract.Quote._ID,
                    Contract.Quote.COLUMN_SYMBOL,
                    Contract.Quote.COLUMN_PRICE,
                    Contract.Quote.COLUMN_HISTORY,
                    Contract.Quote.COLUMN_ABSOLUTE_CHANGE,
                    Contract.Quote.COLUMN_PERCENTAGE_CHANGE
            };

            static final int ID = 0;
            static final int SYMBOL =1;
            static final int PRICE=2;
            static final int HISTORY=3;
            static final int ABSOLUTE_CHANGE = 4;
            static final int PERCENTAGE_CHANGE = 5;
            private DecimalFormat dollarFormatWithPlus;
            private DecimalFormat dollarFormat;
            private DecimalFormat percentageFormat;

            @Override
            public void onCreate() {

                dollarFormat = (DecimalFormat) java.text.NumberFormat.getCurrencyInstance(Locale.US);
                dollarFormatWithPlus = (DecimalFormat) java.text.NumberFormat.getCurrencyInstance(Locale.US);
                dollarFormatWithPlus.setPositivePrefix("+$");
                percentageFormat = (DecimalFormat) java.text.NumberFormat.getPercentInstance(Locale.getDefault());
                percentageFormat.setMaximumFractionDigits(2);
                percentageFormat.setMinimumFractionDigits(2);
                percentageFormat.setPositivePrefix("+");
            }

            @Override
            public void onDataSetChanged() {

                Uri query = Contract.Quote.URI;
                final long identityToken = Binder.clearCallingIdentity();
                mCursor = getContentResolver().query(query, StockProjection, null, null, Contract.Quote.COLUMN_SYMBOL);
                Binder.restoreCallingIdentity(identityToken);

            }

            @Override
            public void onDestroy() {

            }

            @Override
            public int getCount() {
                return mCursor.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {

                RemoteViews remoteView = null;
                if( mCursor == null || mCursor.moveToFirst() == false)
                    return null;
                mCursor.moveToPosition(position);
                remoteView = new RemoteViews(getPackageName(), R.layout.list_item_quote);
                remoteView.setTextViewText(R.id.symbol, mCursor.getString(SYMBOL));
                remoteView.setTextViewText(R.id.price,dollarFormat.format(mCursor.getFloat(Contract.Quote.POSITION_PRICE)));
                Double rawAbsoluteChange = mCursor.getDouble(ABSOLUTE_CHANGE);
                Double percentageChange  = mCursor.getDouble(PERCENTAGE_CHANGE);

                String change = dollarFormatWithPlus.format(rawAbsoluteChange);
                String percentage = percentageFormat.format(percentageChange / 100);

                if (PrefUtils.getDisplayMode(getBaseContext())
                        .equals(getBaseContext().getString(R.string.pref_display_mode_absolute_key))) {
                    remoteView.setTextViewText(R.id.change, change);
                } else {
                    remoteView.setTextViewText(R.id.change, percentage);
                }

                Intent fillInIntent = new Intent();
                fillInIntent.putExtra(Intent.EXTRA_TITLE, mCursor.getString(SYMBOL));
                remoteView.setOnClickFillInIntent(R.id.widget_main, fillInIntent);
                return remoteView;
            }


            @Override
            public RemoteViews getLoadingView() {
                return null;
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return false;
            }
        };
    }
}