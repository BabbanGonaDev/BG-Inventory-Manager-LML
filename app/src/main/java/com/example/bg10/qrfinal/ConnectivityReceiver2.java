package com.example.bg10.qrfinal;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by ciani on 30/04/2018.
 */

public class ConnectivityReceiver2 extends AsyncTask<Void,Void,String> {
    SharedPreferences prefs1;
    Context context;
    DBHandler db;

    public ConnectivityReceiver2(Context context) {
        this.context = context;
        this.prefs1 = context.getSharedPreferences("Preferences1", MODE_PRIVATE);
        db = new DBHandler(context);
    }

    @Override
    protected String doInBackground(Void... voids) {

        //inventoryTUpload();
        return "s";

    }

    public void inventoryTUpload() {
        final InventoryTDBhandler db = new InventoryTDBhandler(context);
        ArrayList<HashMap<String, String>> logs = db.getAllTrans();
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        if (logs.size() != 0) {
            if (db.dbSyncCount() != 0) {
                params.put("unsyncedJSON", db.composeJSONfromSQLite());
                client.post("http://apps.babbangona.com/qrcode/inventorytuploadagain.php", params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String response) {
                        try {
                            JSONArray arr = new JSONArray(response);
                            for (int i = 0; i < arr.length(); i++) {
                                JSONObject obj = (JSONObject) arr.get(i);
                                db.updateSyncStatus(obj.get("idonline").toString(), obj.get("sync").toString());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Throwable error, String content) {
                        if (statusCode == 404) {
                           // Toast.makeText(context, "Requested resource not found", Toast.LENGTH_LONG).show();
                        } else if (statusCode == 500) {
                            //Toast.makeText(context, "Something went wrong at server side", Toast.LENGTH_LONG).show();
                        } else {
                            //Toast.makeText(context, "Unexpected error occurred! [Most common Error: Device might not be connected ]", Toast.LENGTH_LONG).show();
                        }
                    }

                });

            } else {  //Toast.makeText(getApplicationContext(), "SQLite and Remote MySQL DBs are in Sync!", Toast.LENGTH_LONG).show();
            }

        } else { //Toast.makeText(getApplicationContext(), "There are no sub transactions in local db!", Toast.LENGTH_LONG).show();
        }
    }

}

