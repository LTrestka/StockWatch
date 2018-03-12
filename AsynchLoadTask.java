package com.lucastrestka.stockwatch;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by trest on 3/1/2018.
 */

public class AsynchLoadTask extends AsyncTask<String, Void, String> {

    private static final String TAG = "AsynchLoadTask";
    private MainActivity mainActivity;
    private String toSearch;
    List<searchResult> stockListToReturn = new ArrayList<>();

    private final String stocksURL = "http://d.yimg.com/aq/autoc?region=US&lang=en-US";


    public AsynchLoadTask(MainActivity ma) {
        mainActivity = ma;
    }

    @Override
    protected void onPostExecute(String x) {
        Log.d(TAG, "onPostExecute: searched stock list generated");
        mainActivity.stockSearch(stockListToReturn);
    }

    @Override
    protected String doInBackground(String... params) {
        Log.d(TAG, "doInBackground: "+ params[0]);
        Uri.Builder buildURL = Uri.parse(stocksURL).buildUpon();
        buildURL.appendQueryParameter("query", params[0]);

        String URLtoSearch = buildURL.build().toString();
        Log.d(TAG, "doInBackground: searched" + URLtoSearch);

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
        Log.d(TAG, "doInBackground: parsed" + sb.toString());
        stockListToReturn = parseJSON(sb.toString());

        for (int i = 0; i < stockListToReturn.size(); i++){
            Log.d(TAG, "doInBackground: stocks found" + stockListToReturn.get(i).getName().toString());
        }


        return null;
    }

    private List<searchResult> parseJSON(String s) {
        List<searchResult> stocksFound = new ArrayList<>();
        try {

            JSONObject object = new JSONObject(s);
            JSONObject searchResults = object.getJSONObject("ResultSet");
            JSONArray stockList = searchResults.getJSONArray("Result");
            Log.d(TAG, "parseJSON: found array");
            int i = 0;
            int k = 0;
            while(i<10 || stockList.get(k) != null){
                Log.d(TAG, "parseJSON: in loop");
                if(stockList.get(k) != null) {
                    Log.d(TAG, "parseJSON: found object");
                    JSONObject stock = stockList.getJSONObject(k);
                    if(!stock.getString("symbol").contains(".") && stock.getString("symbol").length() <= 4  ) {
                        searchResult result = new searchResult(stock.getString("name"), stock.getString("symbol"), stock.getString("type"));
                        stocksFound.add(result);
                        Log.d(TAG, "parseJSON: added object");
                        i++;
                        k++;
                    }
                    else {
                        k++;
                    }
                }
                else{
                    break;
                }
            }
            return stocksFound;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return stocksFound;
    }

}
