package com.udacity.stockhawk.ui;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.EntryXComparator;
import com.udacity.stockhawk.R;

import java.io.IOException;
import java.io.StringReader;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import au.com.bytecode.opencsv.CSVReader;
import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class StockDetail extends AppCompatActivity {
    @BindView(R.id.tv_detail_history)
    TextView mHistoryDetail;
    @BindView(R.id.lc_stock_history)
    LineChart mStockHistoryGraph;

    List<Entry> entries = new ArrayList<Entry>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_detail);
        ButterKnife.bind(this);

        String stockHistory = "";
        // Get the intent
        Intent intentThatStartedThisActivity = getIntent();

        if (intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)) { // Do we have data?
            stockHistory = intentThatStartedThisActivity.getStringExtra(Intent.EXTRA_TEXT);
            Timber.d(stockHistory);
//            mHistoryDetail.setText(stockHistory);
        }
        CSVReader reader = new CSVReader(new StringReader(stockHistory));
        String[] nextLine;
        try {
            while ((nextLine = reader.readNext()) != null) {
                // nextLine[] is an array of values from the line
                Long rawDate = Long.valueOf(nextLine[0]);
                Date date = new Date(rawDate);
                float value = Float.valueOf(nextLine[1]);

                entries.add(new Entry((float) rawDate,  value));

                mHistoryDetail.append(date.toString() + " --- " + value + "\n");
            }
        } catch (IOException exception) {
            // This should not happen as we are reading from a string
        }

        Collections.sort(entries, new EntryXComparator());

        LineDataSet dataSet = new LineDataSet(entries, "Stock History"); // add entries to dataset
        dataSet.setColor(Color.YELLOW);
        dataSet.setValueTextColor(Color.RED); // styling, ...
        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

        LineData lineData = new LineData(dataSet);
        mStockHistoryGraph.setData(lineData);
        mStockHistoryGraph.invalidate();


    }
}