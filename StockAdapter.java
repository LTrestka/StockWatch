package com.lucastrestka.stockwatch;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.List;

/**
 * Created by trest on 2/18/2018.
 */

public class StockAdapter extends RecyclerView.Adapter<MyViewHolder> {

    private static final String TAG = "StockAdapter";
    private List<Stock> stockList;
    private MainActivity mainActivity;

    public StockAdapter(List<Stock> sList, MainActivity ma){    // Viewholder adaptor for displaying
        this.stockList = sList;                                 ////List of stocks
        this.mainActivity = ma;
    }

    @Override
    public MyViewHolder onCreateViewHolder(final ViewGroup parent, int viewType){
        Log.d(TAG, "onCreateViewHolder:  Making new");
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row, parent, false);

        itemView.setOnClickListener(mainActivity);
        itemView.setOnLongClickListener(mainActivity);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder hold, int position){
        Stock stock = stockList.get(position);
        if(stock.getNASDAQ()!= null) {
            hold.NASDAQ.setText(stock.getNASDAQ());
            hold.companyName.setText(stock.getCompanyName());
            hold.currentPrice.setText(stock.getCurrentPrice());
            hold.change.setText(stock.getDifference());
            hold.changePercent.setText(stock.getChangePercent());
            hold.arrow.setText(stock.getArrow());
            String s = hold.arrow.getText().toString();
            if (s.contains("▼")){                           // Turns all text fields red if change is negative
                hold.NASDAQ.setTextColor(Color.RED);
                hold.companyName.setTextColor(Color.RED);
                hold.change.setTextColor(Color.RED);
                hold.currentPrice.setTextColor(Color.RED);
                hold.changePercent.setTextColor(Color.RED);
                hold.arrow.setTextColor(Color.RED);
            }
            else if(s.contains("▲")){
                hold.NASDAQ.setTextColor(Color.GREEN);
                hold.companyName.setTextColor(Color.GREEN);
                hold.change.setTextColor(Color.GREEN);
                hold.currentPrice.setTextColor(Color.GREEN);
                hold.changePercent.setTextColor(Color.GREEN);
                hold.arrow.setTextColor(Color.GREEN);
            }
        }

    }

    @Override
    public int getItemCount() {
        return stockList.size();
    }

}
