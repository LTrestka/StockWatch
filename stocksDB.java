package com.lucastrestka.stockwatch;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by trest on 3/3/2018.
 */

public class stocksDB extends SQLiteOpenHelper {

    private static final String TAG = "stocksDB";

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    // DB Name
    private static final String DATABASE_NAME = "stocksDB";

    // DB Table Name
    private static final String TABLE_NAME = "stockList";

    ///DB Columns
    private static final String SYMBOL = "Symbol";
    private static final String COMPANY_NAME = "companyName";
    private static final String CURRENT_PRICE = "currentPrice";
    private static final String CHANGE = "Change";
    private static final String CHANGE_PERCENT = "changePercent";
    private static final String ARROW = "arrow";

    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    SYMBOL + " TEXT not null unique," +
                    COMPANY_NAME + " TEXT not null, " +
                    CURRENT_PRICE + " TEXT not null, " +
                    CHANGE+ " TEXT not null, " +
                    CHANGE_PERCENT + " TEXT not null, "+
                    ARROW + " TEXT not null)";

    private SQLiteDatabase db;

    public stocksDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        db = getWritableDatabase(); // Inherited from SQLiteOpenHelper
        Log.d(TAG, "stocksDB: Constructor done");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    @Override
    public void onCreate(SQLiteDatabase db) {       // Bring the database into existence, you
        Log.d(TAG, "onCreate: Making New DB");  // are the creator, the almighty. Without you
        db.execSQL(SQL_CREATE_TABLE);               // having this function called, this database
                                                    // could not exist. Praise be to you.
    }

    public void addStock(Stock stock) {
        ContentValues values = new ContentValues();
        if (stock!=null) {
            values.put(SYMBOL, stock.getNASDAQ());
            values.put(COMPANY_NAME, stock.getCompanyName());
            values.put(CURRENT_PRICE, stock.getCurrentPrice());
            values.put(CHANGE, stock.getDifference());
            values.put(CHANGE_PERCENT, stock.getChangePercent());
            values.put(ARROW, stock.getArrow());

            long key = db.insert(TABLE_NAME, null, values);
            Log.d(TAG, "addStock: " + key);
        }
    }

    public List<Stock> loadStocks() {

        // Load countries - return ArrayList of loaded countries
        Log.d(TAG, "loadStocks: START");
        List<Stock> stocks = new ArrayList<>();

        Cursor cursor = db.query(
                TABLE_NAME,  // The table to query
                new String[]{SYMBOL, COMPANY_NAME, CURRENT_PRICE, CHANGE, CHANGE_PERCENT, ARROW}, // The columns to return
                null, // The columns for the WHERE clause
                null, // The values for the WHERE clause
                null, // don't group the rows
                null, // don't filter by row groups
                SYMBOL); // The sort order

        if (cursor != null) {
            cursor.moveToFirst();

            for (int i = 0; i < cursor.getCount(); i++) {
                String symbol = cursor.getString(0);
                String company_name = cursor.getString(1);
                String currentPrice = cursor.getString(2);
                String change = cursor.getString(3);
                String change_percent = cursor.getString(4);
                String arrow = cursor.getString(5);
                Stock stock = new Stock(symbol, company_name, currentPrice, change, change_percent, arrow);
                stocks.add(stock);
                cursor.moveToNext();
            }
            cursor.close();
        }
        Log.d(TAG, "loadStocks: DONE");

        return stocks;
    }

    public void updateStock(Stock stock) {
        ContentValues values = new ContentValues();

        values.put(SYMBOL, stock.getNASDAQ());
        values.put(COMPANY_NAME, stock.getCompanyName());
        values.put(CURRENT_PRICE, stock.getCurrentPrice());
        values.put(CHANGE, stock.getDifference());
        values.put(CHANGE_PERCENT, stock.getChangePercent());
        values.put(ARROW, stock.getArrow());

        long key = db.update(TABLE_NAME, values, SYMBOL + " = ?", new String[]{stock.getNASDAQ()});
        Log.d(TAG, "addStock: " + key);
    }

    public void deleteStock(String name) {
        Log.d(TAG, "deleteStock: " + name);

        int cnt = db.delete(TABLE_NAME, SYMBOL + " = ?", new String[]{name});

        Log.d(TAG, "deleteSTOCK: " + cnt);
    }

    public void dumpDbToLog() {
        Cursor cursor = db.rawQuery("select * from " + TABLE_NAME, null);
        if (cursor != null) {
            cursor.moveToFirst();

            Log.d(TAG, "dumpDbToLog: dumping to log");
            for (int i = 0; i < cursor.getCount(); i++) {
                String symbol = cursor.getString(0);
                String company_name = cursor.getString(1);
                String current_price = cursor.getString(2);
                String change = cursor.getString(3);
                String change_percent = cursor.getString(4);
                String arrow = cursor.getString(5);

                Log.d(TAG, "dumpDbToLog: " +
                        SYMBOL + ":"+ symbol + "\n" +
                        COMPANY_NAME + ":"+ company_name + "\n" +
                        CURRENT_PRICE + ":"+ current_price + "\n" +
                        CHANGE + ":"+ change + "\n" +
                        CHANGE_PERCENT + ":"+ change_percent+ "\n" +
                        ARROW + ":" + arrow);
                cursor.moveToNext();
            }
            cursor.close();
        }

        Log.d(TAG, "dumpDbToLog: Log dumped");
    }



    public void shutDown() {
        db.close();
    }


}
