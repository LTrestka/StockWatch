package com.lucastrestka.stockwatch;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.text.InputType.TYPE_CLASS_TEXT;
import static android.text.InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS;

// To Do: Display stocks in alphabetical order

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener{

    private RecyclerView recyclerView;
    private StockAdapter stockAdapter;
    private List<Stock> stockList = new ArrayList<>();
    private List<searchResult> searchResults;
    private Menu menu;
    private SwipeRefreshLayout swiper;
    private stocksDB database;
    private String stockSelected;
    private static final String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: ");
        swiper = findViewById(R.id.swiper);
        swiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if(doNetCheck()){
                    // do refresh code
                    for (int i = 0; i < stockList.size(); i++){
                        new asynchRefresh(MainActivity.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                                stockList.get(i).getNASDAQ());      // Starts Asynchronous task that gets JSON data
                    }
                    onResume();
                }
                else{
                    // shows alert in the event that there is no network connection
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("No network Connection");
                    builder.setMessage("Stocks cannot be updated without a network connection");
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                swiper.setRefreshing(false);
            }
        });                                                             // Refreshes adapter to display
        recyclerView = (RecyclerView) findViewById(R.id.recycler);      // All watched stocks
        stockAdapter = new StockAdapter(stockList, this);
        recyclerView.setAdapter(stockAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        database = new stocksDB(this);
    }

    @Override
    protected void onResume() {
        database.dumpDbToLog();
        List<Stock> list = database.loadStocks();
        stockList.clear();
        stockList.addAll(list);
        Log.d(TAG, "onResume: " + list);
        stockAdapter.notifyDataSetChanged();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        database.shutDown();
        super.onDestroy();
    }



    @Override
    public void onClick(View view){
        int pos = recyclerView.getChildLayoutPosition(view);
        Stock s = stockList.get(pos);
        String symbol = s.getNASDAQ();
        String url = "https://www.marketwatch.com/investing/stock/"+symbol; // go to website to view more
        Intent i = new Intent(Intent.ACTION_VIEW);                          // details on selected stock
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    @Override
    public boolean onLongClick(View view){                                  // Long click to give option
                                                                            // to delete selected stock
        int pos = recyclerView.getChildLayoutPosition(view);
        final Stock s = stockList.get(pos);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Stock");
        builder.setMessage("Are you sure you want to delete " + s.getNASDAQ() + " from" +
                "the list?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                database.deleteStock(s.getNASDAQ());
                onResume();
            }
        });
        builder.setNegativeButton("Nevermind", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu m){                             // menu for stock add button
        getMenuInflater().inflate(R.menu.main_menu, m);
        menu = m;
        menu.findItem(R.id.addStock_button).setVisible(true);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem m){
        switch (m.getItemId()){
            case R.id.addStock_button:

                if(doNetCheck()) {  // tests network connection
                    stockInput();   // opens dialog to search for stock, which opens another dialog to select
                                    // a stock from a list, adds selected stock to stockList or cancels everything
                }
                else{
                    // shows alert in the event that there is no network connection
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("No network Connection");
                    builder.setMessage("Stocks cannot be added without a network connection");
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(m);
        }
    }

    public boolean doNetCheck(){                                 // Tests network connection
                                                                // returns boolean value accordingly
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = cm.getActiveNetworkInfo();
        if(netinfo != null && netinfo.isConnectedOrConnecting()){
            return true;
        }
        else {
            return false;
        }
    }

    public void stockSearch(List<searchResult> stocksToDisplay){    // Gets results generated
        Log.d(TAG, "stockSearch: im in");                       // By stock search and passes them
        searchResults = stocksToDisplay;                             // to choices() to display and select
        if (searchResults.size() == 0){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Symbol not found: "+ stockSelected);
                builder.setMessage("Search results came up empty");
                AlertDialog dialog = builder.create();

                dialog.show();
            }
        else {
            choices(findViewById(R.id.textThing));
        }
    }

    public void stockInput() {                                              // Dialog for searching for stock
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final EditText et = new EditText(this);
        et.setInputType(TYPE_CLASS_TEXT |TYPE_TEXT_FLAG_CAP_CHARACTERS);
        et.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        //et.setInputType(TYPE_CLASS_TEXT);
        et.setGravity(Gravity.CENTER_HORIZONTAL);
        builder.setTitle("Add stock");
        builder.setMessage("Enter the name or NASDAQ code to search for:");
        builder.setView(et);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                new AsynchLoadTask(MainActivity.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                        et.getText().toString());      // Starts Asynchronous task that gets JSON data
           stockSelected = et.getText().toString(); }                                          // and generates a list of stock options to choose from
        });

        builder.setNegativeButton("NO WAY", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Generates list of choices from stock search
    public void choices(final View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Make a selection");
        ArrayList<String> array = new ArrayList<>();
        int i = 0;
        while (searchResults.get(i) != null){   // gets first ten options
            //sArray[i] =searchResults.get(i).getSymbol() + " - " + searchResults.get(i).getName();
            array.add(searchResults.get(i).getSymbol() + " - " + searchResults.get(i).getName());
            i++;
            if(i == searchResults.size()){
                break;
            }
        }
        final CharSequence[] sArray = new CharSequence[i];
        for (int k = 0; k < i; k++){
            sArray[k] = array.get(k);
        }
        if(i ==1){
            new AsynchAdd(MainActivity.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                    searchResults.get(0).getSymbol().toString());
            return;
        }
        Log.d(TAG, "choices: build?");
        builder.setItems(sArray, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                TextView et = new TextView(MainActivity.this);
                new AsynchAdd(MainActivity.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                        searchResults.get(which).getSymbol().toString());   // Starts Asynch task that loads
            }                                                               // Json data from selected stock
                                                                            // Creates object for insertion
                                                                            // into stockList
        });

        builder.setNegativeButton("Nevermind", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        AlertDialog dialog = builder.create();

        dialog.show();
    }


    public void addStock(Stock stock_to_add) {   // Adds stock that was created upon selection from
        // Search results and refreshes Viewholder adapter
        if (stock_to_add != null) {

            int pass = stockList.size();
            for (int i = 0; i < pass; i++) {
                if (stock_to_add.getNASDAQ().equals(stockList.get(i).getNASDAQ())) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Duplicate Stock");
                    builder.setMessage("Stock symbol " + stock_to_add.getNASDAQ() + " is already displayed");
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return;
                }
            }
            database.addStock(stock_to_add);
            onResume();
            if (stockList.size() == pass) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Error");
                builder.setMessage("Stock not added due to server issues");
                AlertDialog dialog = builder.create();
                dialog.show();
            } else {
                onResume();
            }
        }
        else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Error");
            builder.setMessage("Stock not added due to server issues");
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    public void refreshStock(Stock stock_to_add){
        database.updateStock(stock_to_add);
        stockAdapter.notifyDataSetChanged();
    }
}


