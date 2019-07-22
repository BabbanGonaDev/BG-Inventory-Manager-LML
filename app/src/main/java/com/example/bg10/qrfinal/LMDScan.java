package com.example.bg10.qrfinal;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.widget.Toast;
import com.google.zxing.Result;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.zip.DataFormatException;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import static android.text.TextUtils.isDigitsOnly;

/**
From Henceforth, Update from Kola. Just scan one QR code to enter everything.
*/

public class LMDScan extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView scannerView;
    SharedPreferences prefs;
    SharedPreferences pref1;
    SharedPreferences.Editor prefsEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);
        prefs = getSharedPreferences("Preferences", MODE_PRIVATE);
        pref1 = getSharedPreferences("Preferences1", MODE_PRIVATE);
        prefsEdit = prefs.edit();

        /*scannerView.setResultHandler(this);
        scannerView.startCamera();
        setTitle(prefs.getString("scannerheading", "SCAN")); //sets the header of the scanner activity*/
    }

    @Override
    public void onResume() {
        super.onResume();
        scannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        scannerView.startCamera();          // Start camera on resume
        setTitle(prefs.getString("scannerheading", "SCAN")); //sets the header of the scanner activity

    }

    @Override
    public void onPause() {
        super.onPause();
        scannerView.stopCamera();
        // Stop camera on pause
    }

    @Override
    public void handleResult(final Result result) {
        String cleanedData[] = result.getText().split("\\*"); //gets the scanned data without the * after
        String data[] = cleanedData[0].split(",");
        //switch block for handling all calls to the scanner activity with intent extras that determine what to do with the scanned data

        switch (prefs.getString("fragment", "")) {
            case "Stocking":
                switch (prefs.getString("transtype", "")) {
                    case "Receiving":
                        switch (prefs.getString("datatobescanned", "")) {
                            case "LMD":
                                try {
                                    prefsEdit.putString("lmdname", data[0]);
                                    prefsEdit.putString("lmdid", data[1]);
                                    prefsEdit.putString("lmdhub", data[2]);
                                    prefsEdit.commit();
                                    //checking if it is truly a warehouse
                                    if (data[1].charAt(0) != 'N') {
                                        Toast.makeText(getApplicationContext(), "Please scan a BG warehouse QR code to proceed", Toast.LENGTH_LONG).show();
                                        finish();
                                    }
                                    new AlertDialog.Builder(this)
                                            .setTitle("Begin Stocking")
                                            .setMessage("Are you sure you want to begin stocking " + data[1] + "?")
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int id) {
                                                    startActivity(new Intent(LMDScan.this, Transactions.class)
                                                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                                }
                                            })

                                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int id) {
                                                    // takes them back to the activity before the zxing activity
                                                    finish();
                                                }
                                            })
                                            .show();
                                } catch (ArrayIndexOutOfBoundsException exception) {
                                    Toast.makeText(getApplicationContext(), "Please scan the appropriate BG warehouse QR code to proceed", Toast.LENGTH_LONG).show();
                                    finish();
                                } catch (StringIndexOutOfBoundsException e) {
                                    Toast.makeText(getApplicationContext(), "Please scan an the appropriate BG warehouse QR code to proceed", Toast.LENGTH_LONG).show();
                                    finish();
                                }

                                break;
                            case "product":
                                try {
                                    prefsEdit.putString("product", data[0]);
                                    prefsEdit.putString("productid", data[1]);
                                    prefsEdit.commit();
                                    //check if the product id is a number of length 9
                                    if ((data[1].length() != 9) || (!isDigitsOnly(data[1]))) {
                                        Toast.makeText(getApplicationContext(), "Please scan a BG product QR code to proceed", Toast.LENGTH_LONG).show();
                                        finish();
                                    }
                                    new AlertDialog.Builder(this)
                                            .setTitle("Begin stocking Product")
                                            .setMessage("Are you sure you want to begin stocking " + data[0])
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int id) {
                                                    prefsEdit.putBoolean("loadfrag", true).commit();      //this extra tells the Stocking activity whether to load the stocking details fragment automatically or not
                                                    startActivity(new Intent(LMDScan.this, Transactions.class)
                                                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                                }
                                            })

                                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int id) {
                                                    // takes them back to the activity before the zxing activity
                                                    finish();
                                                }
                                            })
                                            .show();
                                } catch (ArrayIndexOutOfBoundsException exception) {
                                    Toast.makeText(getApplicationContext(), "Please scan the appropriate QR code to proceed", Toast.LENGTH_LONG).show();
                                    finish();
                                } catch (StringIndexOutOfBoundsException e) {
                                    Toast.makeText(getApplicationContext(), "Please scan an the appropriate QR code to proceed", Toast.LENGTH_LONG).show();
                                    finish();
                                }
                                break;

                            case "receipt":
                                try {
                                    prefsEdit.putString("waybill", data[0])
                                            .putBoolean("loadfrag", true).commit();

                                    if (data[0].charAt(0) != 'W') {
                                        Toast.makeText(getApplicationContext(), "Please scan a valid waybill to proceed", Toast.LENGTH_LONG).show();
                                        prefsEdit.putString("waybill", "");
                                        prefsEdit.commit();
                                        finish();
                                    }


                                    startActivity(new Intent(LMDScan.this, Transactions.class)
                                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                } catch (ArrayIndexOutOfBoundsException exception) {
                                    Toast.makeText(getApplicationContext(), "Please scan the appropriate QR code to proceed", Toast.LENGTH_LONG).show();
                                    finish();
                                } catch (StringIndexOutOfBoundsException e) {
                                    Toast.makeText(getApplicationContext(), "Please scan an the appropriate QR code to proceed", Toast.LENGTH_LONG).show();
                                    finish();
                                }
                                break;

                            case "supplier":
                                try {
                                    if (data[1].length() == 18 && data[1].charAt(0) == 'V') {
                                        prefsEdit.putString("supplier name", data[0]);
                                        prefsEdit.putString("supplier ID", data[1]);
                                        prefsEdit.commit();
                                        prefsEdit.putBoolean("loadfrag", true).commit();
                                        startActivity(new Intent(LMDScan.this, Transactions.class)
                                                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Please scan the appropriate Supplier's QR code to proceed", Toast.LENGTH_LONG).show();
                                    }
                                } catch (ArrayIndexOutOfBoundsException exception) {
                                    Toast.makeText(getApplicationContext(), "Please scan the appropriate Supplier's QR code to proceed", Toast.LENGTH_LONG).show();
                                    finish();
                                } catch (StringIndexOutOfBoundsException e) {
                                    Toast.makeText(getApplicationContext(), "Please scan an the appropriate Supplier's QR code to proceed", Toast.LENGTH_LONG).show();
                                    finish();
                                }
                                break;
                        }
                        break;
                }
                break;
            case "Stocking2":
                switch (prefs.getString("transtype", "")) {
                    /**
                     * Delivery Transactions below:
                     */
                    case "Internal Transfer In":
                        //TODO Updated but not checked.
                        switch (prefs.getString("datatobescanned", "")) {
                            case "LMD":
                                try {
                                    prefsEdit.putString("lmdname", data[0]);
                                    prefsEdit.putString("lmdid", data[1]);
                                    prefsEdit.putString("lmdhub", data[2]);
                                    prefsEdit.commit();
                                    if (data[1].charAt(0) != 'T') {
                                        Toast.makeText(getApplicationContext(), "Please scan a BG Team Member's QR code to proceed", Toast.LENGTH_LONG).show();
                                        finish();
                                    }
                                    new AlertDialog.Builder(this)
                                            .setTitle("Internal Transfer In")
                                            .setMessage("Are you sure you want to begin transfer from " + data[0] + "?")
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int id) {
                                                    startActivity(new Intent(LMDScan.this, Transactions.class)
                                                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                                }
                                            })

                                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int id) {
                                                    // takes them back to the activity before the zxing activity
                                                    finish();
                                                }
                                            })
                                            .show();
                                } catch (ArrayIndexOutOfBoundsException exception) {
                                    Toast.makeText(getApplicationContext(), "Please scan the appropriate QR code to proceed", Toast.LENGTH_LONG).show();
                                    finish();
                                } catch (StringIndexOutOfBoundsException e) {
                                    Toast.makeText(getApplicationContext(), "Please scan an the appropriate QR code to proceed", Toast.LENGTH_LONG).show();
                                    finish();
                                }
                                break;

                            case "LMD2":
                                try {
                                    prefsEdit.putString("lmdname2", data[0]);
                                    prefsEdit.putString("lmdid2", data[1]);
                                    prefsEdit.putString("lmdhub2", data[2]);
                                    prefsEdit.commit();
                                    if (data[1].charAt(0) != 'N') {
                                        Toast.makeText(getApplicationContext(), "Please scan a BG warehouse QR code to proceed", Toast.LENGTH_LONG).show();
                                        finish();
                                    }
                                    new AlertDialog.Builder(this)
                                            .setTitle("Internal Transfer In")
                                            .setMessage("Are you sure you want to begin stocking warehouse " + data[0] + "?")
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int id) {
                                                    prefsEdit.putBoolean("loadfrag", true).commit();
                                                    startActivity(new Intent(LMDScan.this, Transactions.class)
                                                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                                }
                                            })

                                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int id) {
                                                    // takes them back to the activity before the zxing activity
                                                    finish();
                                                }
                                            })
                                            .show();
                                } catch (ArrayIndexOutOfBoundsException exception) {
                                    Toast.makeText(getApplicationContext(), "Please scan the appropriate QR code to proceed", Toast.LENGTH_LONG).show();
                                    finish();
                                } catch (StringIndexOutOfBoundsException e) {
                                    Toast.makeText(getApplicationContext(), "Please scan an the appropriate QR code to proceed", Toast.LENGTH_LONG).show();
                                    finish();
                                }

                                break;

                            case "details":
                                try{
                                    Log.d("QR-Result", cleanedData[0]);
                                    Log.d("QR-Result-Data4", data[4]);
                                    Log.d("QR-Result-LMDID", prefs.getString("lmdid", ""));
                                    prefsEdit.putString("waybill", data[0]);    /*Waybill ID*/
                                    prefsEdit.putString("productid", data[1]);  /*Item ID*/
                                    prefsEdit.putString("product", data[2]);    /*Item Name*/
                                    prefsEdit.putString("quantity", data[7]);   /*The Quantity*/
                                    prefsEdit.commit();

                                    /*if(!data[4].matches(prefs.getString("lmdid", ""))  || data[5].charAt(0) != 'N' || (data[1].length() != 9) || (!isDigitsOnly(data[1])) || data[0].charAt(0) != 'W'){
                                        //First check if "out" in qr is different from "out" selected, ......, then confirm item id, then confirm if its a waybill id.
                                        Toast.makeText(getApplicationContext(), "Kindly Scan the right Waybill for this Transaction.", Toast.LENGTH_LONG).show();
                                        finish();
                                    }*/
                                    if(isWaybillExpired(data[8])){
                                        Toast.makeText(getApplicationContext(), "Waybill has Expired after 14 days.", Toast.LENGTH_LONG).show();
                                        emptySharedPref();
                                        finish();
                                    }else if(!data[5].matches(prefs.getString("lmdid2", ""))){
                                        Toast.makeText(getApplicationContext(), "Warehouse Destination doesn't match, Kindly Check Waybill.", Toast.LENGTH_LONG).show();
                                        emptySharedPref();
                                        finish();
                                    }else if(data[5].charAt(0) != 'N'){
                                        Toast.makeText(getApplicationContext(), "Incorrect Destination Warehouse, Kindly Check Waybill.", Toast.LENGTH_LONG).show();
                                        emptySharedPref();
                                        finish();
                                    }else if((data[1].length() != 9) || (!isDigitsOnly(data[1]))){
                                        Toast.makeText(getApplicationContext(), "Incorrect Item ID, Kindly Check Waybill.", Toast.LENGTH_LONG).show();
                                        emptySharedPref();
                                        finish();
                                    }else if(data[0].charAt(0) != 'W'){
                                        Toast.makeText(getApplicationContext(), "Incorrect Waybill ID, Kindly Check Waybill.", Toast.LENGTH_LONG).show();
                                        emptySharedPref();
                                        finish();
                                    }

                                    prefsEdit.putBoolean("loadfrag", true).commit();
                                    startActivity(new Intent(LMDScan.this, Transactions.class)
                                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                } catch (ArrayIndexOutOfBoundsException exception) {
                                    Toast.makeText(getApplicationContext(), "Please scan the appropriate QR code to proceed", Toast.LENGTH_LONG).show();
                                    finish();
                                } catch (StringIndexOutOfBoundsException e) {
                                    Toast.makeText(getApplicationContext(), "Please scan an the appropriate QR code to proceed", Toast.LENGTH_LONG).show();
                                    finish();
                                }
                                break;
                        }
                        break;
                    case "Delivery to LMD":
                        //TODO Updated but not checked.
                        switch (prefs.getString("datatobescanned", "")) {
                            case "LMD":
                                try {
                                    prefsEdit.putString("lmdname", data[0]);
                                    prefsEdit.putString("lmdid", data[1]);
                                    prefsEdit.putString("lmdhub", data[2]);
                                    prefsEdit.commit();
                                    if (data[1].charAt(0) != 'T') {
                                        Toast.makeText(getApplicationContext(), "Please scan a BG Team Member's QR code to proceed", Toast.LENGTH_LONG).show();
                                        finish();
                                    }
                                    new AlertDialog.Builder(this)
                                            .setTitle("Delivery to LMD")
                                            .setMessage("Are you sure you want to begin transfer from " + data[0] + "?")
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int id) {
                                                    startActivity(new Intent(LMDScan.this, Transactions.class)
                                                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                                }
                                            })

                                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int id) {
                                                    // takes them back to the activity before the zxing activity
                                                    finish();
                                                }
                                            })
                                            .show();
                                } catch (ArrayIndexOutOfBoundsException exception) {
                                    Toast.makeText(getApplicationContext(), "Please scan the appropriate QR code to proceed", Toast.LENGTH_LONG).show();
                                    finish();
                                } catch (StringIndexOutOfBoundsException e) {
                                    Toast.makeText(getApplicationContext(), "Please scan an the appropriate QR code to proceed", Toast.LENGTH_LONG).show();
                                    finish();
                                }

                                break;

                            case "LMD2":
                                try {
                                    prefsEdit.putString("lmdname2", data[0]);
                                    prefsEdit.putString("lmdid2", data[1]);
                                    prefsEdit.putString("lmdhub2", data[2]);
                                    prefsEdit.commit();
                                    if ((data[1].charAt(0) != 'R') || (data[1].charAt(16) != '9')) {
                                        Toast.makeText(getApplicationContext(), "Please scan an LMD's details to proceed", Toast.LENGTH_LONG).show();
                                        finish();
                                    }
                                    new AlertDialog.Builder(this)
                                            .setTitle("Delivery to LMD")
                                            .setMessage("Are you sure you want to begin stocking LMD " + data[0] + "?")
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int id) {
                                                    prefsEdit.putBoolean("loadfrag", true).commit();
                                                    startActivity(new Intent(LMDScan.this, Transactions.class)
                                                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                                }
                                            })

                                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int id) {
                                                    // takes them back to the activity before the zxing activity
                                                    finish();
                                                }
                                            })
                                            .show();
                                } catch (ArrayIndexOutOfBoundsException exception) {
                                    Toast.makeText(getApplicationContext(), "Please scan the appropriate QR code to proceed", Toast.LENGTH_LONG).show();
                                    finish();
                                } catch (StringIndexOutOfBoundsException e) {
                                    Toast.makeText(getApplicationContext(), "Please scan an the appropriate QR code to proceed", Toast.LENGTH_LONG).show();
                                    finish();
                                }

                                break;

                            case "details":
                                try{
                                    Log.d("QR-Result", cleanedData[0]);
                                    Log.d("QR-Result-Data4", data[4]);
                                    Log.d("QR-Result-LMDID", prefs.getString("lmdid", ""));
                                    prefsEdit.putString("waybill", data[0]);    /*Waybill ID*/
                                    prefsEdit.putString("productid", data[1]);  /*Item ID*/
                                    prefsEdit.putString("product", data[2]);    /*Item Name*/
                                    prefsEdit.putString("quantity", data[7]);   /*The Quantity*/
                                    prefsEdit.commit();

                                    /*if(!data[4].matches(prefs.getString("lmdid", ""))  || data[5].charAt(0) != 'R' || data[5].charAt(16) != '9' || data[1].length() != 9 || !isDigitsOnly(data[1]) || data[0].charAt(0) != 'W'){
                                        //First check if "out" in qr is different from "out" selected, ......, then confirm item id, then confirm if its a waybill id.
                                        Toast.makeText(getApplicationContext(), "Kindly Scan the right Waybill for this Transaction.", Toast.LENGTH_LONG).show();
                                        finish();
                                    }*/
                                    if(isWaybillExpired(data[8])) {
                                        Toast.makeText(getApplicationContext(), "Waybill has Expired after 14 days.", Toast.LENGTH_LONG).show();
                                        emptySharedPref();
                                        finish();
                                    }else if(!data[5].matches(prefs.getString("lmdid2", ""))){
                                        Toast.makeText(getApplicationContext(), "LMD Destination doesn't match, Kindly Check Waybill.", Toast.LENGTH_LONG).show();
                                        emptySharedPref();
                                        finish();
                                    }else if(data[5].charAt(0) != 'R' || data[5].charAt(16) != '9'){
                                        Toast.makeText(getApplicationContext(), "Incorrect LMD Destination, Kindly Check Waybill.", Toast.LENGTH_LONG).show();
                                        emptySharedPref();
                                        finish();
                                    }else if((data[1].length() != 9) || (!isDigitsOnly(data[1]))){
                                        Toast.makeText(getApplicationContext(), "Incorrect Item ID, Kindly Check Waybill.", Toast.LENGTH_LONG).show();
                                        emptySharedPref();
                                        finish();
                                    }else if(data[0].charAt(0) != 'W'){
                                        Toast.makeText(getApplicationContext(), "Incorrect Waybill ID, Kindly Check Waybill.", Toast.LENGTH_LONG).show();
                                        emptySharedPref();
                                        finish();
                                    }

                                    prefsEdit.putBoolean("loadfrag", true).commit();
                                    startActivity(new Intent(LMDScan.this, Transactions.class)
                                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                } catch (ArrayIndexOutOfBoundsException exception) {
                                    Toast.makeText(getApplicationContext(), "Please scan the appropriate QR code to proceed", Toast.LENGTH_LONG).show();
                                    finish();
                                } catch (StringIndexOutOfBoundsException e) {
                                    Toast.makeText(getApplicationContext(), "Please scan an the appropriate QR code to proceed", Toast.LENGTH_LONG).show();
                                    finish();
                                }
                                break;
                        }
                        break;

                    /**
                     * Pickup Transactions below:
                      */
                    case "Internal Transfer Out":
                        //TODO Updated but not checked.
                        switch (prefs.getString("datatobescanned", "")) {
                            case "LMD":
                                try {
                                    prefsEdit.putString("lmdname", data[0]);
                                    prefsEdit.putString("lmdid", data[1]);
                                    prefsEdit.putString("lmdhub", data[2]);
                                    prefsEdit.commit();
                                    if (data[1].charAt(0) != 'N') {
                                        Toast.makeText(getApplicationContext(), "Please scan a BG warehouse QR code to proceed", Toast.LENGTH_LONG).show();
                                        finish();
                                    }
                                    new AlertDialog.Builder(this)
                                            .setTitle("Internal Transfer Out")
                                            .setMessage("Are you sure you want to begin internal transfer from " + data[0] + "?")
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int id) {
                                                    startActivity(new Intent(LMDScan.this, Transactions.class)
                                                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                                }
                                            })

                                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int id) {
                                                    // takes them back to the activity before the zxing activity
                                                    finish();
                                                }
                                            })
                                            .show();
                                } catch (ArrayIndexOutOfBoundsException exception) {
                                    Toast.makeText(getApplicationContext(), "Please scan the appropriate QR code to proceed", Toast.LENGTH_LONG).show();
                                    finish();
                                } catch (StringIndexOutOfBoundsException e) {
                                    Toast.makeText(getApplicationContext(), "Please scan an the appropriate QR code to proceed", Toast.LENGTH_LONG).show();
                                    finish();
                                }

                                break;

                            case "LMD2":
                                try {
                                    prefsEdit.putString("lmdname2", data[0]);
                                    prefsEdit.putString("lmdid2", data[1]);
                                    prefsEdit.putString("lmdhub2", data[2]);
                                    prefsEdit.commit();
                                    if (data[1].charAt(0) != 'T') {
                                        Toast.makeText(getApplicationContext(), "Please scan a BG Team Member QR code to proceed", Toast.LENGTH_LONG).show();
                                        finish();
                                    }
                                    new AlertDialog.Builder(this)
                                            .setTitle("Internal Transfer Out")
                                            .setMessage("Are you sure you want to begin internal transfer to " + data[0] + "?")
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int id) {
                                                    prefsEdit.putBoolean("loadfrag", true).commit();
                                                    startActivity(new Intent(LMDScan.this, Transactions.class)
                                                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                                }
                                            })

                                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int id) {
                                                    // takes them back to the activity before the zxing activity
                                                    finish();
                                                }
                                            })
                                            .show();
                                } catch (ArrayIndexOutOfBoundsException exception) {
                                    Toast.makeText(getApplicationContext(), "Please scan the appropriate QR code to proceed", Toast.LENGTH_LONG).show();
                                    finish();
                                } catch (StringIndexOutOfBoundsException e) {
                                    Toast.makeText(getApplicationContext(), "Please scan an the appropriate QR code to proceed", Toast.LENGTH_LONG).show();
                                    finish();
                                }

                                break;

                            case "details":
                                try{
                                    prefsEdit.putString("waybill", data[0]);    /*Waybill ID*/
                                    prefsEdit.putString("productid", data[1]);  /*Item ID*/
                                    prefsEdit.putString("product", data[2]);    /*Item Name*/
                                    prefsEdit.putString("quantity", data[7]);   /*The Quantity*/
                                    prefsEdit.commit();

                                    /*if(!data[3].matches(prefs.getString("lmdid", ""))  || data[4].charAt(0) != 'T' || (data[1].length() != 9) || (!isDigitsOnly(data[1])) || data[0].charAt(0) != 'W'){
                                        //First check if "out" in qr is different from "out" selected, then check if its a warehouse, and then confirm item id, then confirm if its a waybill id.
                                        Toast.makeText(getApplicationContext(), "Kindly Scan the right Waybill for this Transaction.", Toast.LENGTH_LONG).show();
                                        finish();
                                    }*/
                                    if(isWaybillExpired(data[8])){
                                        Toast.makeText(getApplicationContext(), "Waybill has Expired after 14 days.", Toast.LENGTH_LONG).show();
                                        emptySharedPref();
                                        finish();
                                    }else if(!data[3].matches(prefs.getString("lmdid", ""))){
                                        Toast.makeText(getApplicationContext(), "Warehouse Out locations don't match, Kindly Check Waybill.", Toast.LENGTH_LONG).show();
                                        emptySharedPref();
                                        finish();
                                    }else if((data[1].length() != 9) || (!isDigitsOnly(data[1]))){
                                        Toast.makeText(getApplicationContext(), "Incorrect Item ID, Kindly Check Waybill.", Toast.LENGTH_LONG).show();
                                        emptySharedPref();
                                        finish();
                                    }else if(data[0].charAt(0) != 'W'){
                                        Toast.makeText(getApplicationContext(), "Incorrect Waybill ID, Kindly Check Waybill.", Toast.LENGTH_LONG).show();
                                        emptySharedPref();
                                        finish();
                                    }

                                    prefsEdit.putBoolean("loadfrag", true).commit();
                                    startActivity(new Intent(LMDScan.this, Transactions.class)
                                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                } catch (ArrayIndexOutOfBoundsException exception) {
                                    Toast.makeText(getApplicationContext(), "Please scan the appropriate QR code to proceed", Toast.LENGTH_LONG).show();
                                    finish();
                                } catch (StringIndexOutOfBoundsException e) {
                                    Toast.makeText(getApplicationContext(), "Please scan an the appropriate QR code to proceed", Toast.LENGTH_LONG).show();
                                    finish();
                                }
                                break;
                        }
                        break;
                    case "Pickup from LMD":
                        //TODO Updated but not checked.
                        switch (prefs.getString("datatobescanned", "")) {
                            case "LMD":
                                try {
                                    prefsEdit.putString("lmdname", data[0]);
                                    prefsEdit.putString("lmdid", data[1]);
                                    prefsEdit.putString("lmdhub", data[2]);
                                    prefsEdit.commit();
                                    if ((data[1].charAt(0) != 'R') || (data[1].charAt(16) != '9')) {
                                        Toast.makeText(getApplicationContext(), "Please scan an LMD's details to proceed", Toast.LENGTH_LONG).show();
                                        finish();
                                    }
                                    new AlertDialog.Builder(this)
                                            .setTitle("Pickup from LMD")
                                            .setMessage("Are you sure you want to begin pickup from " + data[0] + "?")
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int id) {
                                                    startActivity(new Intent(LMDScan.this, Transactions.class)
                                                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                                }
                                            })

                                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int id) {
                                                    // takes them back to the activity before the zxing activity
                                                    finish();
                                                }
                                            })
                                            .show();
                                } catch (ArrayIndexOutOfBoundsException exception) {
                                    Toast.makeText(getApplicationContext(), "Please scan the appropriate QR code to proceed", Toast.LENGTH_LONG).show();
                                    finish();
                                } catch (StringIndexOutOfBoundsException e) {
                                    Toast.makeText(getApplicationContext(), "Please scan an the appropriate QR code to proceed", Toast.LENGTH_LONG).show();
                                    finish();
                                }

                                break;

                            case "LMD2":
                                try {
                                    prefsEdit.putString("lmdname2", data[0]);
                                    prefsEdit.putString("lmdid2", data[1]);
                                    prefsEdit.putString("lmdhub2", data[2]);
                                    prefsEdit.commit();
                                    if (data[1].charAt(0) != 'T') {
                                        Toast.makeText(getApplicationContext(), "Please scan a BG team member QR code to proceed", Toast.LENGTH_LONG).show();
                                        finish();
                                    }
                                    new AlertDialog.Builder(this)
                                            .setTitle("Pickup from LMD")
                                            .setMessage("Are you sure you want to begin pickup by " + data[0] + "?")
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int id) {
                                                    prefsEdit.putBoolean("loadfrag", true).commit();
                                                    startActivity(new Intent(LMDScan.this, Transactions.class)
                                                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                                }
                                            })

                                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int id) {
                                                    // takes them back to the activity before the zxing activity
                                                    finish();
                                                }
                                            })
                                            .show();
                                } catch (ArrayIndexOutOfBoundsException exception) {
                                    Toast.makeText(getApplicationContext(), "Please scan the appropriate QR code to proceed", Toast.LENGTH_LONG).show();
                                    finish();
                                } catch (StringIndexOutOfBoundsException e) {
                                    Toast.makeText(getApplicationContext(), "Please scan an the appropriate QR code to proceed", Toast.LENGTH_LONG).show();
                                    finish();
                                }

                                break;

                            case "details":
                                try{
                                    prefsEdit.putString("waybill", data[0]);    /*Waybill ID*/
                                    prefsEdit.putString("productid", data[1]);  /*Item ID*/
                                    prefsEdit.putString("product", data[2]);    /*Item Name*/
                                    prefsEdit.putString("quantity", data[7]);   /*The Quantity*/
                                    prefsEdit.commit();

                                    /*if(!data[3].matches(prefs.getString("lmdid", ""))  || data[4].charAt(0) != 'T' || (data[1].length() != 9) || (!isDigitsOnly(data[1])) || data[0].charAt(0) != 'W'){
                                        //First check if "out" in qr is different from "out" selected, then check if its a warehouse, and then confirm item id, then confirm if its a waybill id.
                                        Toast.makeText(getApplicationContext(), "Kindly Scan the right Waybill for this Transaction.", Toast.LENGTH_LONG).show();
                                        finish();
                                    }*/
                                    if(isWaybillExpired(data[8])){
                                        Toast.makeText(getApplicationContext(), "Waybill has Expired after 14 days.", Toast.LENGTH_LONG).show();
                                        emptySharedPref();
                                        finish();
                                    }else if(!data[3].matches(prefs.getString("lmdid", ""))){
                                        Toast.makeText(getApplicationContext(), "LMD Out locations don't match, Kindly Check Waybill.", Toast.LENGTH_LONG).show();
                                        emptySharedPref();
                                        finish();
                                    }else if((data[1].length() != 9) || (!isDigitsOnly(data[1]))){
                                        Toast.makeText(getApplicationContext(), "Incorrect Item ID, Kindly Check Waybill.", Toast.LENGTH_LONG).show();
                                        emptySharedPref();
                                        finish();
                                    }else if(data[0].charAt(0) != 'W'){
                                        Toast.makeText(getApplicationContext(), "Incorrect Waybill ID, Kindly Check Waybill.", Toast.LENGTH_LONG).show();
                                        emptySharedPref();
                                        finish();
                                    }

                                    prefsEdit.putBoolean("loadfrag", true).commit();
                                    startActivity(new Intent(LMDScan.this, Transactions.class)
                                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                } catch (ArrayIndexOutOfBoundsException exception) {
                                    Toast.makeText(getApplicationContext(), "Please scan the appropriate QR code to proceed", Toast.LENGTH_LONG).show();
                                    finish();
                                } catch (StringIndexOutOfBoundsException e) {
                                    Toast.makeText(getApplicationContext(), "Please scan an the appropriate QR code to proceed", Toast.LENGTH_LONG).show();
                                    finish();
                                }
                                break;
                        }
                        break;

                    /**
                     * More Delivery Transactions below:
                      */
                    case "Delivery to Customer":
                        //TODO Updated but not checked.
                        switch (prefs.getString("datatobescanned", "")) {
                            case "LMD":
                                try {
                                    prefsEdit.putString("lmdname", data[0]);
                                    prefsEdit.putString("lmdid", data[1]);
                                    prefsEdit.putString("lmdhub", data[2]);
                                    prefsEdit.commit();
                                    if (data[1].charAt(0) != 'T') {
                                        Toast.makeText(getApplicationContext(), "Please scan a BG Team Member's QR code to proceed", Toast.LENGTH_LONG).show();
                                        finish();
                                    }
                                    new AlertDialog.Builder(this)
                                            .setTitle("Delivery to Customer")
                                            .setMessage("Are you sure you want to begin transfer from " + data[0] + "?")
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int id) {
                                                    startActivity(new Intent(LMDScan.this, Transactions.class)
                                                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                                }
                                            })

                                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int id) {
                                                    // takes them back to the activity before the zxing activity
                                                    finish();
                                                }
                                            })
                                            .show();
                                } catch (ArrayIndexOutOfBoundsException exception) {
                                    Toast.makeText(getApplicationContext(), "Please scan the appropriate QR code to proceed", Toast.LENGTH_LONG).show();
                                    finish();
                                } catch (StringIndexOutOfBoundsException e) {
                                    Toast.makeText(getApplicationContext(), "Please scan an the appropriate QR code to proceed", Toast.LENGTH_LONG).show();
                                    finish();
                                }
                                break;

                            case "LMD2":
                                try {
                                    prefsEdit.putString("lmdname2", data[0]);
                                    prefsEdit.putString("lmdid2", data[1]);
                                    prefsEdit.putString("lmdhub2", data[2]);
                                    prefsEdit.commit();
                                    if ((data[1].charAt(0) != 'C') || (data[1].length() != 18)) {
                                        Toast.makeText(getApplicationContext(), "Please scan a Customer's details to proceed", Toast.LENGTH_LONG).show();
                                        finish();
                                    }
                                    new AlertDialog.Builder(this)
                                            .setTitle("Delivery to Customer")
                                            .setMessage("Are you sure you want to begin stocking Customer " + data[0] + "?")
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int id) {
                                                    prefsEdit.putBoolean("loadfrag", true).commit();
                                                    startActivity(new Intent(LMDScan.this, Transactions.class)
                                                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                                }
                                            })

                                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int id) {
                                                    // takes them back to the activity before the zxing activity
                                                    finish();
                                                }
                                            })
                                            .show();
                                } catch (ArrayIndexOutOfBoundsException exception) {
                                    Toast.makeText(getApplicationContext(), "Please scan the appropriate QR code to proceed", Toast.LENGTH_LONG).show();
                                    finish();
                                } catch (StringIndexOutOfBoundsException e) {
                                    Toast.makeText(getApplicationContext(), "Please scan an the appropriate QR code to proceed", Toast.LENGTH_LONG).show();
                                    finish();
                                }

                                break;

                            case "details":
                                try{
                                    Log.d("QR-Result", cleanedData[0]);
                                    Log.d("QR-Result-Data4", data[4]);
                                    Log.d("QR-Result-LMDID", prefs.getString("lmdid", ""));
                                    prefsEdit.putString("waybill", data[0]);    /*Waybill ID*/
                                    prefsEdit.putString("productid", data[1]);  /*Item ID*/
                                    prefsEdit.putString("product", data[2]);    /*Item Name*/
                                    prefsEdit.putString("quantity", data[7]);   /*The Quantity*/
                                    prefsEdit.commit();

                                    /*if(!data[4].matches(prefs.getString("lmdid", ""))  || data[5].charAt(0) != 'C' || data[5].length() != 18 || data[1].length() != 9 || !isDigitsOnly(data[1]) || data[0].charAt(0) != 'W'){
                                        //First check if "out" in qr is different from "out" selected, ......, then confirm item id, then confirm if its a waybill id.
                                        Toast.makeText(getApplicationContext(), "Kindly Scan the right Waybill for this Transaction.", Toast.LENGTH_LONG).show();
                                        finish();
                                    }*/
                                    if(isWaybillExpired(data[8])){
                                        Toast.makeText(getApplicationContext(), "Waybill has Expired after 14 days.", Toast.LENGTH_LONG).show();
                                        emptySharedPref();
                                        finish();
                                    }else if(!data[5].matches(prefs.getString("lmdid2", ""))){
                                        Toast.makeText(getApplicationContext(), "Customer destinations don't match, Kindly Check Waybill.", Toast.LENGTH_LONG).show();
                                        emptySharedPref();
                                        finish();
                                    }else if(data[5].charAt(0) != 'C' || data[5].length() != 18){
                                        Toast.makeText(getApplicationContext(), "Incorrect Customer Destination, Kindly Check Waybill.", Toast.LENGTH_LONG).show();
                                        emptySharedPref();
                                        finish();
                                    }else if((data[1].length() != 9) || (!isDigitsOnly(data[1]))){
                                        Toast.makeText(getApplicationContext(), "Incorrect Item ID, Kindly Check Waybill.", Toast.LENGTH_LONG).show();
                                        emptySharedPref();
                                        finish();
                                    }else if(data[0].charAt(0) != 'W'){
                                        Toast.makeText(getApplicationContext(), "Incorrect Waybill ID, Kindly Check Waybill.", Toast.LENGTH_LONG).show();
                                        emptySharedPref();
                                        finish();
                                    }

                                    prefsEdit.putBoolean("loadfrag", true).commit();
                                    startActivity(new Intent(LMDScan.this, Transactions.class)
                                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                } catch (ArrayIndexOutOfBoundsException exception) {
                                    Toast.makeText(getApplicationContext(), "Please scan the appropriate QR code to proceed", Toast.LENGTH_LONG).show();
                                    finish();
                                } catch (StringIndexOutOfBoundsException e) {
                                    Toast.makeText(getApplicationContext(), "Please scan an the appropriate QR code to proceed", Toast.LENGTH_LONG).show();
                                    finish();
                                }
                                break;
                        }
                        break;
                    case "Delivery to Team Member":
                        //TODO Updated but not checked.
                        switch (prefs.getString("datatobescanned", "")) {
                            case "LMD":
                                try {
                                    prefsEdit.putString("lmdname", data[0]);
                                    prefsEdit.putString("lmdid", data[1]);
                                    prefsEdit.putString("lmdhub", data[2]);
                                    prefsEdit.commit();
                                    if (data[1].charAt(0) != 'T') {
                                        Toast.makeText(getApplicationContext(), "Please scan a BG Team Member's QR code to proceed", Toast.LENGTH_LONG).show();
                                        finish();
                                    }
                                    new AlertDialog.Builder(this)
                                            .setTitle("Delivery to Team Member")
                                            .setMessage("Are you sure you want to begin transfer from " + data[0] + "?")
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int id) {
                                                    startActivity(new Intent(LMDScan.this, Transactions.class)
                                                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                                }
                                            })

                                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int id) {
                                                    // takes them back to the activity before the zxing activity
                                                    finish();
                                                }
                                            })
                                            .show();
                                } catch (ArrayIndexOutOfBoundsException exception) {
                                    Toast.makeText(getApplicationContext(), "Please scan the appropriate QR code to proceed", Toast.LENGTH_LONG).show();
                                    finish();
                                } catch (StringIndexOutOfBoundsException e) {
                                    Toast.makeText(getApplicationContext(), "Please scan an the appropriate QR code to proceed", Toast.LENGTH_LONG).show();
                                    finish();
                                }
                                break;

                            case "LMD2":
                                try {
                                    prefsEdit.putString("lmdname2", data[0]);
                                    prefsEdit.putString("lmdid2", data[1]);
                                    prefsEdit.putString("lmdhub2", data[2]);
                                    prefsEdit.commit();
                                    if (data[1].charAt(0) != 'T') {
                                        Toast.makeText(getApplicationContext(), "Please scan a BG Team Member's QR code to proceed", Toast.LENGTH_LONG).show();
                                        finish();
                                    }
                                    new AlertDialog.Builder(this)
                                            .setTitle("Delivery to Team Member")
                                            .setMessage("Are you sure you want to begin transfer to Team Member " + data[0] + "?")
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int id) {
                                                    prefsEdit.putBoolean("loadfrag", true).commit();
                                                    startActivity(new Intent(LMDScan.this, Transactions.class)
                                                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                                }
                                            })

                                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int id) {
                                                    // takes them back to the activity before the zxing activity
                                                    finish();
                                                }
                                            })
                                            .show();
                                } catch (ArrayIndexOutOfBoundsException exception) {
                                    Toast.makeText(getApplicationContext(), "Please scan the appropriate QR code to proceed", Toast.LENGTH_LONG).show();
                                    finish();
                                } catch (StringIndexOutOfBoundsException e) {
                                    Toast.makeText(getApplicationContext(), "Please scan an the appropriate QR code to proceed", Toast.LENGTH_LONG).show();
                                    finish();
                                }

                                break;

                            case "details":
                                try{
                                    Log.d("QR-Result", cleanedData[0]);
                                    Log.d("QR-Result-Data4", data[4]);
                                    Log.d("QR-Result-LMDID", prefs.getString("lmdid", ""));
                                    prefsEdit.putString("waybill", data[0]);    /*Waybill ID*/
                                    prefsEdit.putString("productid", data[1]);  /*Item ID*/
                                    prefsEdit.putString("product", data[2]);    /*Item Name*/
                                    prefsEdit.putString("quantity", data[7]);   /*The Quantity*/
                                    prefsEdit.commit();

                                    /*if(!data[4].matches(prefs.getString("lmdid", ""))  || data[5].charAt(0) != 'T' || data[1].length() != 9 || !isDigitsOnly(data[1]) || data[0].charAt(0) != 'W'){
                                        //First check if "out" in qr is different from "out" selected, ......, then confirm item id, then confirm if its a waybill id.
                                        Toast.makeText(getApplicationContext(), "Kindly Scan the right Waybill for this Transaction.", Toast.LENGTH_LONG).show();
                                        finish();
                                    }*/
                                    if(isWaybillExpired(data[8])){
                                        Toast.makeText(getApplicationContext(), "Waybill has Expired after 14 days.", Toast.LENGTH_LONG).show();
                                        emptySharedPref();
                                        finish();
                                    }else if(!data[5].matches(prefs.getString("lmdid2", ""))){
                                        Toast.makeText(getApplicationContext(), "Team member destinations don't match, Kindly Check Waybill.", Toast.LENGTH_LONG).show();
                                        emptySharedPref();
                                        finish();
                                    }else if(data[5].charAt(0) != 'T'){
                                        Toast.makeText(getApplicationContext(), "Incorrect Team Member Destination, Kindly Check Waybill.", Toast.LENGTH_LONG).show();
                                        emptySharedPref();
                                        finish();
                                    }else if((data[1].length() != 9) || (!isDigitsOnly(data[1]))){
                                        Toast.makeText(getApplicationContext(), "Incorrect Item ID, Kindly Check Waybill.", Toast.LENGTH_LONG).show();
                                        emptySharedPref();
                                        finish();
                                    }else if(data[0].charAt(0) != 'W'){
                                        Toast.makeText(getApplicationContext(), "Incorrect Waybill ID, Kindly Check Waybill.", Toast.LENGTH_LONG).show();
                                        emptySharedPref();
                                        finish();
                                    }

                                    prefsEdit.putBoolean("loadfrag", true).commit();
                                    startActivity(new Intent(LMDScan.this, Transactions.class)
                                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                } catch (ArrayIndexOutOfBoundsException exception) {
                                    Toast.makeText(getApplicationContext(), "Please scan the appropriate QR code to proceed", Toast.LENGTH_LONG).show();
                                    finish();
                                } catch (StringIndexOutOfBoundsException e) {
                                    Toast.makeText(getApplicationContext(), "Please scan an the appropriate QR code to proceed", Toast.LENGTH_LONG).show();
                                    finish();
                                }
                                break;
                        }
                        break;
                }
                break;
            case "Contract":
                switch (prefs.getString("transtype", "")) {
                    case "Delivery to Warehouse":
                        switch (prefs.getString("datatobescanned", "")) {
                            case "LMD":
                                try {
                                    prefsEdit.putString("lmdname", data[0]);
                                    prefsEdit.putString("lmdid", data[1]);
                                    prefsEdit.putString("lmdhub", data[2]);
                                    prefsEdit.commit();
                                    if ((data[1].charAt(0) != 'R') || (data[1].charAt(16) != '9')) {
                                        Toast.makeText(getApplicationContext(), "Please scan an LMD's QR code to proceed", Toast.LENGTH_LONG).show();
                                        finish();
                                    }
                                    new AlertDialog.Builder(this)
                                            .setTitle("Delivery to Warehouse")
                                            .setMessage("Are you sure you want to begin transfer from " + data[0] + "?")
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int id) {
                                                    startActivity(new Intent(LMDScan.this, Transactions.class)
                                                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                                }
                                            })

                                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int id) {
                                                    // takes them back to the activity before the zxing activity
                                                    finish();
                                                }
                                            })
                                            .show();
                                } catch (ArrayIndexOutOfBoundsException exception) {
                                    Toast.makeText(getApplicationContext(), "Please scan the appropriate QR code to proceed", Toast.LENGTH_LONG).show();
                                    finish();
                                } catch (StringIndexOutOfBoundsException e) {
                                    Toast.makeText(getApplicationContext(), "Please scan an the appropriate QR code to proceed", Toast.LENGTH_LONG).show();
                                    finish();
                                }

                                break;

                            case "LMD2":
                                try {
                                    prefsEdit.putString("lmdname2", data[0]);
                                    prefsEdit.putString("lmdid2", data[1]);
                                    prefsEdit.putString("lmdhub2", data[2]);
                                    prefsEdit.commit();
                                    if (data[1].charAt(0) != 'T') {
                                        Toast.makeText(getApplicationContext(), "Please scan a BG Team Member's QR code to proceed", Toast.LENGTH_LONG).show();
                                        finish();
                                    }
                                    new AlertDialog.Builder(this)
                                            .setTitle("Delivery to Warehouse")
                                            .setMessage("Are you sure you want to begin stocking " + data[0] + "?")
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int id) {
                                                    prefsEdit.putBoolean("loadfrag", true).commit();
                                                    startActivity(new Intent(LMDScan.this, Transactions.class)
                                                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                                }
                                            })

                                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int id) {
                                                    // takes them back to the activity before the zxing activity
                                                    finish();
                                                }
                                            })
                                            .show();
                                } catch (ArrayIndexOutOfBoundsException exception) {
                                    Toast.makeText(getApplicationContext(), "Please scan the appropriate QR code to proceed", Toast.LENGTH_LONG).show();
                                    finish();
                                } catch (StringIndexOutOfBoundsException e) {
                                    Toast.makeText(getApplicationContext(), "Please scan an the appropriate QR code to proceed", Toast.LENGTH_LONG).show();
                                    finish();
                                }

                                break;
                            case "product":
                                try {
                                    prefsEdit.putString("product", data[0]);
                                    prefsEdit.putString("productid", data[1]);
                                    prefsEdit.commit();
                                    if ((data[1].length() != 9) || (!isDigitsOnly(data[1]))) {
                                        Toast.makeText(getApplicationContext(), "Please scan a BG product QR code to proceed", Toast.LENGTH_LONG).show();
                                        finish();
                                    }
                                    new AlertDialog.Builder(this)
                                            .setTitle("Delivery to Warehouse")
                                            .setMessage("Are you sure you want to begin transferring " + data[0])
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int id) {
                                                    prefsEdit.putBoolean("loadfrag", true).commit();
                                                    startActivity(new Intent(LMDScan.this, Transactions.class)
                                                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                                }
                                            })

                                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int id) {
                                                    // takes them back to the activity before the zxing activity
                                                    finish();
                                                }
                                            })
                                            .show();
                                } catch (ArrayIndexOutOfBoundsException exception) {
                                    Toast.makeText(getApplicationContext(), "Please scan the appropriate QR code to proceed", Toast.LENGTH_LONG).show();
                                    finish();
                                } catch (StringIndexOutOfBoundsException e) {
                                    Toast.makeText(getApplicationContext(), "Please scan an the appropriate QR code to proceed", Toast.LENGTH_LONG).show();
                                    finish();
                                }
                                break;
                            case "receipt":
                                try {
                                    prefsEdit.putString("waybill", data[0]);
                                    prefsEdit.commit();
                                    prefsEdit.putBoolean("loadfrag", true).commit();

                                    //checking if it is truly a Waybill number
                                    if (data[0].charAt(0) != 'W') {
                                        Toast.makeText(getApplicationContext(), "Please scan a valid waybill to proceed", Toast.LENGTH_LONG).show();
                                        prefsEdit.putString("waybill", "");
                                        prefsEdit.commit();
                                        finish();
                                    }


                                    startActivity(new Intent(LMDScan.this, Transactions.class)
                                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                } catch (ArrayIndexOutOfBoundsException exception) {
                                    Toast.makeText(getApplicationContext(), "Please scan the appropriate QR code to proceed", Toast.LENGTH_LONG).show();
                                    exception.printStackTrace();
                                    finish();
                                } catch (StringIndexOutOfBoundsException e) {
                                    Toast.makeText(getApplicationContext(), "Please scan an the appropriate QR code to proceed", Toast.LENGTH_LONG).show();
                                    finish();
                                }
                                break;
                        }
                        break;
                }
                break;
            case "Return":
                switch (prefs.getString("transtype", "")) {
                    case "Return From LMR":
                        switch (prefs.getString("datatobescanned", "")) {
                            case "LMD":
                                try {
                                    prefsEdit.putString("lmdname", data[0]);
                                    prefsEdit.putString("lmdid", data[1]);
                                    prefsEdit.putString("lmdhub", data[2]);
                                    prefsEdit.commit();
                                    if ((data[1].charAt(0) != 'R') || (data[1].charAt(16) != '7')) {
                                        Toast.makeText(getApplicationContext(), "Please scan an LMR's QR code to proceed", Toast.LENGTH_LONG).show();
                                        finish();
                                    }
                                    new AlertDialog.Builder(this)
                                            .setTitle("Return From LMR")
                                            .setMessage("Are you sure you want to begin return from " + data[0] + "?")
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int id) {
                                                    startActivity(new Intent(LMDScan.this, Transactions.class)
                                                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                                }
                                            })

                                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int id) {
                                                    // takes them back to the activity before the zxing activity
                                                    finish();
                                                }
                                            })
                                            .show();
                                } catch (ArrayIndexOutOfBoundsException exception) {
                                    Toast.makeText(getApplicationContext(), "Please scan the appropriate QR code to proceed", Toast.LENGTH_LONG).show();
                                    finish();
                                } catch (StringIndexOutOfBoundsException e) {
                                    Toast.makeText(getApplicationContext(), "Please scan an the appropriate QR code to proceed", Toast.LENGTH_LONG).show();
                                    finish();
                                }
                                break;

                            case "LMD2":
                                try {
                                    prefsEdit.putString("lmdname2", data[0]);
                                    prefsEdit.putString("lmdid2", data[1]);
                                    prefsEdit.putString("lmdhub2", data[2]);
                                    prefsEdit.commit();

                                    if (data[1].charAt(0) == 'N') {
                                        Toast.makeText(getApplicationContext(), "Kindly scan a BG Team Member or Customer QR code to proceed", Toast.LENGTH_LONG).show();
                                        finish();
                                    }
                                    new AlertDialog.Builder(this)
                                            .setTitle("Return From LMR")
                                            .setMessage("Are you sure you want to begin stocking " + data[0] + "?")
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int id) {
                                                    prefsEdit.putBoolean("loadfrag", true).commit();
                                                    startActivity(new Intent(LMDScan.this, Transactions.class)
                                                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                                }
                                            })

                                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int id) {
                                                    // takes them back to the activity before the zxing activity
                                                    finish();
                                                }
                                            })
                                            .show();
                                } catch (ArrayIndexOutOfBoundsException exception) {
                                    Toast.makeText(getApplicationContext(), "Please scan the appropriate QR code to proceed", Toast.LENGTH_LONG).show();
                                    finish();
                                } catch (StringIndexOutOfBoundsException e) {
                                    Toast.makeText(getApplicationContext(), "Please scan an the appropriate QR code to proceed", Toast.LENGTH_LONG).show();
                                    finish();
                                }
                                break;
                            case "product":
                                try {
                                    prefsEdit.putString("product", data[0]);
                                    prefsEdit.putString("productid", data[1]);
                                    prefsEdit.commit();
                                    if ((data[1].length() != 9) || (!isDigitsOnly(data[1]))) {
                                        Toast.makeText(getApplicationContext(), "Please scan a BG product QR code to proceed", Toast.LENGTH_LONG).show();
                                        finish();
                                    }
                                    new AlertDialog.Builder(this)
                                            .setTitle("Return From LMR")
                                            .setMessage("Are you sure you want to begin transferring " + data[0])
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int id) {
                                                    prefsEdit.putBoolean("loadfrag", true).commit();
                                                    startActivity(new Intent(LMDScan.this, Transactions.class)
                                                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                                }
                                            })

                                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int id) {
                                                    // takes them back to the activity before the zxing activity
                                                    finish();
                                                }
                                            })
                                            .show();
                                } catch (ArrayIndexOutOfBoundsException exception) {
                                    Toast.makeText(getApplicationContext(), "Please scan the appropriate QR code to proceed", Toast.LENGTH_LONG).show();
                                    finish();
                                } catch (StringIndexOutOfBoundsException e) {
                                    Toast.makeText(getApplicationContext(), "Please scan an the appropriate QR code to proceed", Toast.LENGTH_LONG).show();
                                    finish();
                                }
                                break;
                        }
                        break;
                }
                break;
                //The Next Case of Fragment Type goes here.
        }

    }

    public void emptySharedPref(){
        //This function empties the shared preferences in case of an error.
        prefs.edit().remove("product").commit();
        prefs.edit().remove("productid").commit();
        prefs.edit().remove("waybill").commit();
        prefs.edit().remove("date1").commit();
        prefs.edit().remove("date2").commit();
        prefs.edit().remove("quantity").commit();
    }

    public boolean isWaybillExpired(String qrdate){
        //This function uses the shared pref to check if the waybill has expired.
        DateFormat format  = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date created = null;
        try {
            created = format.parse(qrdate);
            long diff = new Date().getTime() - created.getTime();
            long day = diff / (1000 * 60 *60 * 24);
            Log.d("Day difference", String.valueOf(day));
            if(day >= 0 && day <= 14){
                return false;
            }
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }


        return true;
    }
}
