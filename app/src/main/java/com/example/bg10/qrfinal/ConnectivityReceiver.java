package com.example.bg10.qrfinal;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

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

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by ciani on 30/04/2018.
 */

public class ConnectivityReceiver extends AsyncTask<Void,Void,String> {
    SharedPreferences prefs1;
    Context context;
    DBHandler db;

    public ConnectivityReceiver(Context context) {
        this.context = context;
        this.prefs1 = context.getSharedPreferences("Preferences1", MODE_PRIVATE);
        db = new DBHandler(context);
    }

    @Override
    protected String doInBackground(Void... voids) {
        HttpURLConnection httpURLConnection = null;
        if(!db.synced(prefs1.getString("username"," "), prefs1.getString("staff_id"," "))) {
            try {
                // application to Database connection block Connection block

                URL url = new URL("http://apps.babbangona.com/qrcode/loginsync.php");
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");

                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);


                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String data_string = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(prefs1.getString("username", " "), "UTF-8") + "&" +
                        URLEncoder.encode("app_name", "UTF-8") + "=" + URLEncoder.encode("LML Inventory Manager", "UTF-8");

                bufferedWriter.write(data_string); // writing information to Database
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                httpURLConnection.connect();
                db.updateSynced(prefs1.getString("username"," "), prefs1.getString("staff_id"," "));

            } catch (IOException e) {
                e.printStackTrace();
            }
            try {

                int response_code = httpURLConnection.getResponseCode();
                if (response_code == HttpURLConnection.HTTP_OK) {
                    InputStream input = httpURLConnection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {

                        result.append(line);
                        if (result.toString().equals("synced")) {
                            db.updateSynced(prefs1.getString("username", " "), prefs1.getString("staff_id", " "));
                        }
                        Log.i("PASS", result.toString());
                    }
                    return (result.toString());
                } else {

                    return ("connection error");
                }

            } catch (IOException e) {
                e.printStackTrace();
                return e.toString();
            } finally {
                httpURLConnection.disconnect();
            }

        }

        return "Already synced";

    }
}

