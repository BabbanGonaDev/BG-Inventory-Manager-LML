package com.example.bg10.qrfinal;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class ViewAllTransactions extends AppCompatActivity {

    SharedPreferences prefs2;
    SharedPreferences.Editor prefs2Edit;
    RecyclerView recyclerView;
    AllTransAdapter adapter;
    ArrayList<Transaction> tsns;
    String todaysDate;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_transactions);
        prefs2 = getSharedPreferences("Preferences2", MODE_PRIVATE);    //preferences for holding the transaction viewing date range
        prefs2Edit = prefs2.edit();


        todaysDate =  new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new java.util.Date());
        prefs2Edit.putString("startdate", "2018-06-17" ).commit();
        prefs2Edit.putString("enddate", todaysDate ).commit();

        InventoryTDBhandler db = new InventoryTDBhandler(getApplicationContext());
        tsns = db.displayTransactions();    //gets the list of transactions

        Log.d("CHECK", String.valueOf(tsns));

        recyclerView = findViewById(R.id.rv);
        adapter = new AllTransAdapter(ViewAllTransactions.this, tsns, new AllTransAdapter.OnItemClickListener() {
            @Override
            public void onClick(Transaction trans) {
                setTransViews(trans);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(ViewAllTransactions.this));
        recyclerView.setAdapter(adapter);
    }

    public void setTransViews(Transaction trans) {
        LayoutInflater inflater = LayoutInflater.from(ViewAllTransactions.this);
        View v = inflater.inflate(R.layout.trans_details, null,false);
        TextView tvTransID = v.findViewById(R.id.tvTransID);
        TextView tvIn = v.findViewById(R.id.tvIn);
        TextView tvOut = v.findViewById(R.id.tvOut);
        TextView tvItemID = v.findViewById(R.id.tvItemID);
        TextView tvItemName = v.findViewById(R.id.tvItemName);
        TextView tvUnit = v.findViewById(R.id.tvUnit);
        TextView tvUnitPrice = v.findViewById(R.id.tvUnitPrice);
        TextView tvSuggUnitPrice = v.findViewById(R.id.tvSuggUnitPrice);
        TextView tvType = v.findViewById(R.id.tvType);
        TextView tvHub = v.findViewById(R.id.tvHub);
        TextView tvSync = v.findViewById(R.id.tvSync);
        TextView tvDate = v.findViewById(R.id.tvDate);
        tvTransID.setText(trans.getId());
        tvIn.setText(trans.getIn());
        tvOut.setText(trans.getOut());
        tvItemID.setText(trans.getItemid());
        tvItemName.setText(trans.getItemName());
        tvUnit.setText(trans.getUnit());
        tvUnitPrice.setText(trans.getUnitPrice());
        tvSuggUnitPrice.setText(trans.getSuggUnitPrice());
        tvType.setText(trans.getType());
        tvHub.setText(trans.getHub());
        tvSync.setText(trans.getSync());
        tvDate.setText(trans.getEnteredDate());

        new AlertDialog.Builder(ViewAllTransactions.this).setView(v)
                .setTitle("Transaction Details")
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_view, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        todaysDate =  new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new java.util.Date());
        menu.findItem(R.id.itmStartDate).setTitle(prefs2.getString("startdate","2018-06-20"));  //sets the item text for start date
        menu.findItem(R.id.itmEndDate).setTitle(prefs2.getString("enddate",todaysDate));      //sets the item text for end date
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        InventoryTDBhandler db = new InventoryTDBhandler(getApplicationContext());
        item.setChecked(true);
        switch (item.getItemId()) {
            case R.id.itmStartDate:
                Calendar c = Calendar.getInstance();
                final int y = c.get(Calendar.YEAR);
                final int m = c.get(Calendar.MONTH);
                final int d = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(ViewAllTransactions.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        //checks for the validity of the date
                        if((year > y) || ((year <= y)&&(month > m)) || ((year <= y)&&(month <= m)&&(dayOfMonth > d)))
                        {
                            //if date is invalid, do nothing
                            Toast.makeText(getApplicationContext(),"Please enter a valid date", Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            //if date is valid, change the name and edit the preferences
                            prefs2Edit.putString("startdate", year + "-" + String.format("%02d", month + 1) +"-"+ dayOfMonth );
                            prefs2Edit.commit();
                            finish();
                            startActivity(new Intent(ViewAllTransactions.this,ViewAllTransactions.class));
                        }
                    }
                }, y,m,d);

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
                break;
            case R.id.itmEndDate:
                Calendar c2 = Calendar.getInstance();
                final int y2 = c2.get(Calendar.YEAR);
                final int m2 = c2.get(Calendar.MONTH);
                final int d2 = c2.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog2 = new DatePickerDialog(ViewAllTransactions.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        //checks for the validity of the date
                        if((year > y2) || ((year <= y2)&&(month > m2)) || ((year <= y2)&&(month <= m2)&&(dayOfMonth > d2)))
                        {
                            //if date is invalid, do nothing
                            Toast.makeText(getApplicationContext(),"Please enter a valid date", Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            //if date is valid, change the name and edit the preferences
                            prefs2Edit.putString("enddate", year + "-" + String.format("%02d", month + 1) +"-"+ dayOfMonth );
                            prefs2Edit.commit();
                            finish();
                            startActivity(new Intent(ViewAllTransactions.this,ViewAllTransactions.class));
                        }
                    }
                }, y2,m2,d2);

                dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog2.show();
                break;
            case R.id.itmSync:
                pricesUpdate();
                inventoryTUpload();

                /**
                 * AM commenting out the smartProductUpdate because i don't think we are using it really. - Rehoboth(17/04/19)
                 **/
                //smartProductUpdate();
                //inventory02TUpload();

                prefs2Edit.putString("startdate", "2018-06-17" );
                prefs2Edit.putString("enddate", todaysDate );
                prefs2Edit.commit();
                adapter = new AllTransAdapter(getApplicationContext(), db.displayTransactions(), new AllTransAdapter.OnItemClickListener() {
                    @Override
                    public void onClick(Transaction trans) {
                        setTransViews(trans);
                    }
                });
                recyclerView.setAdapter(adapter);
                break;
            case R.id.itmITIn:
                adapter = new AllTransAdapter(getApplicationContext(), db.displayAllITInTransactions(), new AllTransAdapter.OnItemClickListener() {
                    @Override
                    public void onClick(Transaction trans) {
                        setTransViews(trans);
                    }
                });
                recyclerView.setAdapter(adapter);
                break;
            case R.id.itmITOut:
                adapter = new AllTransAdapter(getApplicationContext(), db.displayAllITOutTransactions(), new AllTransAdapter.OnItemClickListener() {
                    @Override
                    public void onClick(Transaction trans) {
                        setTransViews(trans);
                    }
                });
                recyclerView.setAdapter(adapter);
                break;
            case R.id.itmSale:
                adapter = new AllTransAdapter(getApplicationContext(), db.displayAllSaleTransactions(), new AllTransAdapter.OnItemClickListener() {
                    @Override
                    public void onClick(Transaction trans) {
                        setTransViews(trans);
                    }
                });
                recyclerView.setAdapter(adapter);
                break;
            case R.id.itmLMRDelivery:
                adapter = new AllTransAdapter(getApplicationContext(),db.displayAllLMRDeliveryTransactions() , new AllTransAdapter.OnItemClickListener() {
                    @Override
                    public void onClick(Transaction trans) {
                        setTransViews(trans);
                    }
                });
                recyclerView.setAdapter(adapter);
                break;
            case R.id.itmLMDDelivery:
                adapter = new AllTransAdapter(getApplicationContext(), db.displayAllLMDDeliveryTransactions(), new AllTransAdapter.OnItemClickListener() {
                    @Override
                    public void onClick(Transaction trans) {
                        setTransViews(trans);
                    }
                });
                recyclerView.setAdapter(adapter);
                break;
            case R.id.itmLMRPickup:
                adapter = new AllTransAdapter(getApplicationContext(), db.displayAllLMRPickupTransactions(), new AllTransAdapter.OnItemClickListener() {
                    @Override
                    public void onClick(Transaction trans) {
                        setTransViews(trans);
                    }
                });
                recyclerView.setAdapter(adapter);
                break;
            case R.id.itmLMDPickup:
                adapter = new AllTransAdapter(getApplicationContext(), db.displayAllLMDPickupTransactions(), new AllTransAdapter.OnItemClickListener() {
                    @Override
                    public void onClick(Transaction trans) {
                        setTransViews(trans);
                    }
                });
                recyclerView.setAdapter(adapter);
                break;
            case R.id.itmSupplierDelivery:
                adapter = new AllTransAdapter(getApplicationContext(), db.displayAllSupplierTransactions(), new AllTransAdapter.OnItemClickListener() {
                    @Override
                    public void onClick(Transaction trans) {
                        setTransViews(trans);
                    }
                });
                recyclerView.setAdapter(adapter);
                break;
            case R.id.itmAll:
                adapter = new AllTransAdapter(getApplicationContext(), db.displayTransactions(), new AllTransAdapter.OnItemClickListener() {
                    @Override
                    public void onClick(Transaction trans) {
                        setTransViews(trans);
                    }
                });
                recyclerView.setAdapter(adapter);
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    //Due to time constraint, I am putting the Async Task for syncing inside the activity page here.
    public void pricesUpdate() {
        SyncPriceUpdateAsyncTask priceSync = new SyncPriceUpdateAsyncTask();
        priceSync.execute();
    }

    public void inventoryTUpload() {
        SyncInventoryTAsyncTask inventoryTsync = new SyncInventoryTAsyncTask();
        inventoryTsync.execute();
    }

    public class SyncInventoryTAsyncTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Void... voids){

            final InventoryTDBhandler db = new InventoryTDBhandler(getApplicationContext());
            ArrayList<HashMap<String, String>> logs = db.getAllTrans();
            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();
            if (logs.size() != 0) {
                if (db.dbSyncCount() != 0) {
                    prefs = getSharedPreferences("Preferences1", MODE_PRIVATE);
                    String staffID = prefs.getString("staff_id","");
                    params.put("unsyncedJSON", db.composeJSONfromSQLite());
                    params.put("staff_id", staffID);
                    client.post("http://apps.babbangona.com/qrcode/inventorytmsbupload.php", params, new AsyncHttpResponseHandler() {

                        @Override
                        public void onSuccess(String response) {

                            try {
                                Log.d("MSG1", "We have gotten inside");
                                Log.d("TAG Sync Up response: ", response);
                                JSONArray arr = new JSONArray(response);
                                for (int i = 0; i < arr.length(); i++) {
                                    JSONObject obj = (JSONObject) arr.get(i);
                                    Log.d("MSG2", "Executing QUERY");
                                    db.updateSyncStatus(obj.get("UniqueID").toString(), obj.get("sync").toString());
                                }
                                finish();
                                startActivity(new Intent(getApplicationContext(), ViewAllTransactions.class));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Throwable error, String content) {
                            if (statusCode == 404) {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "Requested resource not found (ITU)", Toast.LENGTH_LONG).show();
                                    }
                                });

                            } else if (statusCode == 500) {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "Something went wrong at server side (ITU)", Toast.LENGTH_LONG).show();
                                    }
                                });

                            } else {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "Unexpected error occurred! [Most common Error: Device might not be connected (ITU)", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }

                    });

                } else {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "SQLite and Remote MySQL DBs are in Sync!", Toast.LENGTH_LONG).show();
                        }
                    });
                }

            } else {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "There are no sub transactions in local db!", Toast.LENGTH_LONG).show();
                    }
                });
            }
            return null;
        }
    }

    public class SyncPriceUpdateAsyncTask extends AsyncTask<Void, Void, Void>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Void... voids) {

            final ProductDBHelper db = new ProductDBHelper(getApplicationContext());

            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();
            params.put("lastPriceUpdate", db.latestPriceUpdate());
            client.post("http://apps.babbangona.com/qrcode/priceupdate.php", params, new AsyncHttpResponseHandler(){

                @Override
                public void onSuccess(String response) {
                    try {
                        Log.d("TAG Price Response:",response);
                        JSONArray arr = new JSONArray(response);
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = (JSONObject) arr.get(i);
                            db.pricesAddUpdate(obj.get("itemid").toString(), obj.get("itemname").toString(), obj.get("price").toString(), obj.get("timestamp").toString());
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Error caught in price update!", Toast.LENGTH_LONG).show();
                            }
                        });
                        e.printStackTrace();
                    }
                }
                @Override
                public void onFailure(int statusCode, Throwable error, String content) {

                    if (statusCode == 404) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Requested resource not found (PU)", Toast.LENGTH_LONG).show();
                            }
                        });

                    }else if (statusCode == 500) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Something went wrong at server side (PU)", Toast.LENGTH_LONG).show();
                            }
                        });

                    }else{

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Unexpected error occurred! [Most common Error: Device might not be connected (PU)", Toast.LENGTH_LONG).show();
                            }
                        });

                    }
                }

            });
            return null;
        }
    }

   /* public void smartProductUpdate(){
        final SmartUpdateAccess smartUpdateAccess = new SmartUpdateAccess(ViewAllTransactions.this);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        client.post("http://apps.babbangona.com/qrcode/smartupdate.php", params, new AsyncHttpResponseHandler(){
            @Override
            public void onSuccess(String response) {
                try {
                    JSONArray arr = new JSONArray(response);
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject obj = (JSONObject) arr.get(i);
                        Log.d("TAG","Update in progress");
                        *//*Log.d("Product", obj.get("product_id").toString());
                        Log.d("expectedUnits", obj.get("expectedUnits").toString());
                        Log.d("unitsPerCarton", obj.get("unitsPerCarton").toString());
                        Log.d("LMRStatus", obj.get("LMRStatus").toString());*//*
                        smartUpdateAccess.open();
                        smartUpdateAccess.updateSmartUpdate(obj.get("product_id").toString(), obj.get("unitsPerCarton").toString(), obj.get("expectedUnits").toString(), obj.get("LMRStatus").toString());
                    }

                } catch (JSONException e) {
                    Log.i("Error:", "Error caught at smart product update");
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Throwable error, String content) {

                if (statusCode == 404) {Toast.makeText(getApplicationContext(), "Requested resource not found (SU)", Toast.LENGTH_LONG).show();}
                else if (statusCode == 500) {Toast.makeText(getApplicationContext(), "Something went wrong at server side (SU)", Toast.LENGTH_LONG).show();}
                else {Toast.makeText(getApplicationContext(), "Unexpected error occurred! [Most common Error: Device might not be connected (SU)", Toast.LENGTH_LONG).show();}
            }
        });
    }*/


}
