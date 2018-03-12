package com.lucastrestka.stockwatch;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.lucastrestka.stockwatch.MainActivity;
import com.lucastrestka.stockwatch.Stock;
import com.lucastrestka.stockwatch.searchResult;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by trest on 3/3/2018.
 */

public class asynchRefresh extends AsyncTask<String, Void, String> {
    private static final String TAG = "AsynchAdd";
    private MainActivity mainActivity;
    private String toSearch;
    private Stock stockToAdd;
    private final String stocksURL = "https://api.iextrading.com/1.0/stock/";

    public asynchRefresh(MainActivity ma) {
        mainActivity = ma;
    }

    @Override
    protected void onPostExecute(String x) {
        Log.d(TAG, "doInBackgroung: stock refreshed");
        mainActivity.refreshStock(stockToAdd);
    }

    @Override
    protected String doInBackground(String... params) {    // Builds url and captures json string,
        // Passes string to parseJSON for parsing
        Log.d(TAG, "doInBackground: "+ params[0]);
        Uri.Builder buildURL = Uri.parse(stocksURL).buildUpon();
        buildURL.appendPath(params[0]);
        buildURL.appendPath("quote");
        String URLtoSearch = buildURL.build().toString();
        Log.d(TAG, "doInBackground2: searched" + URLtoSearch);

        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(URLtoSearch);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        }catch (Exception e) {
            Log.e(TAG, "doInBackground: exception", e);
            return null;
        }
        parseJSON(sb.toString());

        return null;
    }

    private Stock parseJSON(String s) { // Takes json string from url and converts into a Stock object
        // created object is returned to MainActivity to add to stockList
        String Symbol, Name, latestPrice, Change, changePercent, arrow;
        try {
            JSONObject object = new JSONObject(s);
            Symbol = object.get("symbol").toString();
            Log.d(TAG, "parseJSON: "+ Symbol);
            Name = object.get("companyName").toString();
            Change = object.get("change").toString();
            latestPrice = object.get("latestPrice").toString();
            changePercent = object.get("changePercent").toString();
            Log.d(TAG, "parseJSON: new stock info:" + Symbol + ", " + Change + ", " +changePercent);
            // Convert change percent to a real percent value
            double cp = Double.parseDouble(changePercent);
            cp = cp*100;
            if (cp <0){
                arrow = "▼";
            }
            else{
                arrow = "▲";
            }
            NumberFormat nf = new DecimalFormat("##.##");
            changePercent = "(" + nf.format(cp) + "%)";

            Log.d(TAG, "parseJSON: found stock info");
            stockToAdd = new Stock(Symbol, Name, latestPrice, Change, changePercent, arrow);
            return stockToAdd;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
