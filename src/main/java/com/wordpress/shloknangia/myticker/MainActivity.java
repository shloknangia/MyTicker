package com.wordpress.shloknangia.myticker;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private static final String STOCK_URL = "http://finance.google.com/finance/info?q=NASDAQ%3a";

    private static final String SYMBOL = "symbol";
    private static final String EXCHANGE = "exchange";
    private static final String LAST_TRADE = "last_trade";
    private static final String CHANGE = "change";
    private static final String PERC_CHANGE = "perc_change";

    private HashMap<String, String> hmStockData;

    private EditText edSymbol = null;
    TextView tvlast;
    private Button bnRetrieve = null;
    private String symbol = "";


    private static final String TAG = "myAct";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_price);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        bnRetrieve = (Button) findViewById(R.id.bn_retrieve);

        edSymbol = (EditText) findViewById(R.id.edit_symbol);
        tvlast = (TextView) findViewById(R.id.tv_last);

        edSymbol.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {

                symbol = edSymbol.getText().toString().trim();
                bnRetrieve.setEnabled(symbol.length() > 0);

            }

        });
    }
    public void addstock(View view){
        String rate = tvlast.getText().toString();
        Intent i = new Intent(MainActivity.this, AddStock.class);
        i.putExtra("symbol", symbol);
        i.putExtra("rate", rate);
        startActivity(i);
    }

    public void retrieveQuote(View vw) {
        String request = STOCK_URL + symbol;

        if (checkInternet()) {
            new StockRetriveTask() {
                @Override
                protected void onPreExecute() {
                    Log.i(TAG, "onPreExecute");
                    super.onPreExecute();
                }

                @Override
                protected void onPostExecute(String response) {
                    super.onPostExecute(response);
                    Log.d(TAG, response);
                    readJsonResponse(response);
                    displayResponse();
                }

            }.execute(request);
        }
    }

    private boolean checkInternet() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni != null && ni.isConnected()) {
            return true;
        } else {
            Toast.makeText(MainActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void readJsonResponse(String response) {
        response = response.substring(2);
        hmStockData = new HashMap<>();
        try {
            Toast.makeText(MainActivity.this, "Success !", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "readJsonResponse: " + response);
            JSONArray jArr = new JSONArray(response);
            Log.d(TAG, "jArr to string : " + jArr.toString());

            for(int i=0;i<=jArr.length();i++) {
                JSONObject jObj = jArr.getJSONObject(0);

                hmStockData.put(SYMBOL, jObj.getString("t"));
                hmStockData.put(EXCHANGE, jObj.getString("e"));
                hmStockData.put(LAST_TRADE, jObj.getString("l"));
                hmStockData.put(CHANGE, jObj.getString("c"));
                hmStockData.put(PERC_CHANGE, jObj.getString("cp"));

                Log.d(TAG, "ticker: " + jObj.getString("t"));
                Log.d(TAG, "exchange: " + jObj.getString("e"));
                Log.d(TAG, "price: " + jObj.getString("l"));

            }

        } catch (JSONException e) {
            Log.d(TAG, "error in json: ");
            e.printStackTrace();
        }
    }

    private void updateTextView(int id, String name) {

        TextView tvTarget = (TextView) findViewById(id);

        if (tvTarget == null) return;

        tvTarget.setText(hmStockData.containsKey(name) ? hmStockData.get(name) : "");

    }

    private void displayResponse() {

        Log.i(TAG, "displayResponse");

        updateTextView(R.id.tv_symbol, SYMBOL);
        updateTextView(R.id.tv_exchange, EXCHANGE);
        updateTextView(R.id.tv_last, LAST_TRADE);
        updateTextView(R.id.tv_change, CHANGE);
        updateTextView(R.id.tv_perc_change, PERC_CHANGE);
    }
    public void googleSearch(View v) {
        Intent i = new Intent(MainActivity.this, GoogleSearchActivity.class);
        i.putExtra("keyword", symbol);
        startActivity(i);
    }

    String stockPrice = "";
    boolean flag=false;

    public String findPrice(String s) {
        String request = STOCK_URL + s;

//        if (checkInternet()) {
            new StockRetriveTask() {
                @Override
                protected void onPreExecute() {
                    Log.i(TAG, "onPreExecute");
                    super.onPreExecute();
                }

                @Override
                protected void onPostExecute(String response) {
                    super.onPostExecute(response);
                    Log.d(TAG, response);
                    readAgain(response);
                }

            }.execute(request);
//        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
            }
        }, 5000);

        Log.d(TAG, "Stockprice last : " + stockPrice);
        return stockPrice;
    }

    public void readAgain(String response) {
        response = response.substring(2);
        try {
//            Toast.makeText(MainActivity.this, "Success !", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "readJsonResponse: " + response);
            JSONArray jArr = new JSONArray(response);
            Log.d(TAG, "jArr to string : " + jArr.toString());

            for(int i=0;i<=jArr.length();i++) {
                JSONObject jObj = jArr.getJSONObject(0);

                stockPrice = jObj.getString("l");
                Log.d(TAG, "Stock : " + stockPrice);
            }

        } catch (JSONException e) {
            Log.d(TAG, "error in json: ");
            e.printStackTrace();
        }
        flag=true;
    }

}