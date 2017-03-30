package com.udacity.stockhawk.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.PrefUtils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by morals on 24/03/17.
 */

public class StockWidgetFactory implements RemoteViewsService.RemoteViewsFactory {
    private Cursor mCursor;
    private Context mContext;

    static final int ID = 0;
    static final int SYMBOL =1;
    static final int PRICE=2;
    static final int ABSOLUTE_CHANGE = 3;
    static final int PERCENTAGE_CHANGE = 4;
    static final int HISTORY=5;

    private final DecimalFormat dollarFormatWithPlus;
    private final DecimalFormat dollarFormat;
    private final DecimalFormat percentageFormat;

    public StockWidgetFactory(Context context, Intent intent) {
        mContext = context;

        dollarFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        dollarFormatWithPlus = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        dollarFormatWithPlus.setPositivePrefix("+$");
        percentageFormat = (DecimalFormat) NumberFormat.getPercentInstance(Locale.getDefault());
        percentageFormat.setMaximumFractionDigits(2);
        percentageFormat.setMinimumFractionDigits(2);
        percentageFormat.setPositivePrefix("+");

    }

    private void getCursorData(){

        if (mCursor != null) {
            mCursor.close();
            mCursor = null;
        }

        final long identityToken = Binder.clearCallingIdentity();

        mCursor = mContext.getContentResolver().query(
                        Contract.Quote.URI,
                        Contract.Quote.QUOTE_COLUMNS.toArray(new String[]{}),
                        null,
                        null,
                        Contract.Quote.COLUMN_SYMBOL
                );

        Binder.restoreCallingIdentity(identityToken);
    }

    @Override
    public void onCreate() {
        getCursorData();
    }

    @Override
    public void onDataSetChanged() {
        getCursorData();
    }

    @Override
    public void onDestroy() {
        if (mCursor != null) {
            mCursor.close();
        }
        mCursor = null;
    }

    @Override
    public int getCount() {
        if (mCursor != null) {
            return mCursor.getCount();
        } else {
            return 0;
        }
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (mCursor == null || !mCursor.moveToFirst()) {
            return null;
        }
        mCursor.moveToPosition(position);
        RemoteViews remoteView = new RemoteViews(mContext.getPackageName(), R.layout.list_item_quote);
        remoteView.setTextViewText(R.id.symbol, mCursor.getString(SYMBOL));
        remoteView.setTextViewText(R.id.price,dollarFormat.format(mCursor.getFloat(Contract.Quote.POSITION_PRICE)));
        Double rawAbsoluteChange = mCursor.getDouble(ABSOLUTE_CHANGE);
        Double percentageChange  = mCursor.getDouble(PERCENTAGE_CHANGE);

        String change = dollarFormatWithPlus.format(rawAbsoluteChange);
        String percentage = percentageFormat.format(percentageChange / 100);

        if (PrefUtils.getDisplayMode(mContext)
                .equals(mContext.getString(R.string.pref_display_mode_absolute_key))) {
            remoteView.setTextViewText(R.id.change, change);
        } else {
            remoteView.setTextViewText(R.id.change, percentage);
        }

        if (rawAbsoluteChange > 0) {
            remoteView.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_green);
        } else {
            remoteView.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_red);
        }


        Intent fillInIntent = new Intent();
        fillInIntent.putExtra(Intent.EXTRA_TEXT, mCursor.getString(HISTORY));
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
        if (mCursor.moveToPosition(position)) {
            return mCursor.getLong(Contract.Quote.POSITION_ID);
        } else {
            return position;
        }
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}