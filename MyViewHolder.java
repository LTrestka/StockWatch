package com.lucastrestka.stockwatch;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by trest on 2/18/2018.
 */

public class MyViewHolder extends RecyclerView.ViewHolder {

    public TextView NASDAQ;
    public TextView companyName;
    public TextView change;
    public TextView currentPrice;
    public TextView changePercent;
    public TextView arrow;

    public MyViewHolder (View v){
        super(v);
        NASDAQ = (TextView) v.findViewById(R.id.NASDAQ);
        companyName = (TextView) v.findViewById(R.id.companyName);
        change = (TextView) v.findViewById(R.id.change);
        currentPrice = (TextView) v.findViewById(R.id.currentPrice);
        changePercent = (TextView) v.findViewById(R.id.changePercent);
        arrow = (TextView) v.findViewById(R.id.arrow);
    }

}