package com.udacity.stockhawk.widget;


import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by morals on 24/03/17.
 */


public class StockWidgetRemoteViewsService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new StockWidgetFactory(this, intent);
    }


}

