package com.babbangona.inventory.msb;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class Operations extends AppCompatActivity implements View.OnClickListener {

    SharedPreferences prefs;
    SharedPreferences prefs1;
    Button btnDeliver;
    Button btnPickup;
    Button btnViewAllTransactions;
    Button btnReturn;
    Button btnExportInvT;
    TextView tvAppVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operations);
        prefs1 = getSharedPreferences("Preferences1", MODE_PRIVATE);
        prefs = getSharedPreferences("Preferences", MODE_PRIVATE);
        btnDeliver = findViewById(R.id.btnDeliver);
        btnPickup = findViewById(R.id.btnPickup);
        btnViewAllTransactions = findViewById(R.id.btnViewAllTransactions);
        btnExportInvT = findViewById(R.id.btnExportInvT);
        btnReturn = findViewById(R.id.btnReturn);
        tvAppVersion = findViewById(R.id.tvAppVersion);


        btnDeliver.setOnClickListener(this);
        btnPickup.setOnClickListener(this);
        btnViewAllTransactions.setOnClickListener(this);
        btnReturn.setOnClickListener(this);
        btnExportInvT.setOnClickListener(this);

        tvAppVersion.setText("(C) BG MSB Inventory " + " " +  BuildConfig.VERSION_NAME);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        if (permissionGranted()) {
            switch (v.getId())
            {
                case R.id.btnDeliver:
                    startActivity(new Intent(Operations.this, Delivery.class));
                    break;
                case R.id.btnPickup:
                    startActivity(new Intent(Operations.this, Pickup.class));
                    break;
                case R.id.btnReturn:
                    startActivity(new Intent(Operations.this, LMDScan.class));
                    prefs.edit().putString("fragment", "Return")
                            .putString("transtype", "Return From LMR")
                            .putString("datatobescanned", "LMD")
                            .putString("scannerheading", "Scan LMR Details")
                            .commit();
                    break;
                case R.id.btnViewAllTransactions:
                    startActivity(new Intent(getApplicationContext(),ViewAllTransactions.class));
                    break;
                case R.id.btnExportInvT:
                    ExportInventoryT();
                    break;

            }

        } else {
            ActivityCompat.requestPermissions(Operations.this, new String[]{Manifest.permission.CAMERA}, 1);
        }

    }

    public boolean permissionGranted() {
        return (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setComponent(new ComponentName("com.babbangona.accesscontrol","com.babbangona.accesscontrol.MainActivity"));
        startActivity(intent);
    }

    public void ExportInventoryT(){

       ActivityCompat.requestPermissions(Operations.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 5);

        File dir = new File(Environment.getExternalStorageDirectory().getPath(), "Exports");
        try {
            if (!dir.exists()) {
                dir.mkdir();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        String directory_path = Environment.getExternalStorageDirectory().getPath();
        Log.d("directory", "" + directory_path);
        SQLiteToExcel sqliteToExcel;
        final int[] count = {0};

        sqliteToExcel = new SQLiteToExcel(getApplicationContext(), "InventoryT.db", directory_path + "/Exports");
        try {
            sqliteToExcel.exportAllTables("InventoryT.xls", new SQLiteToExcel.ExportListener() {
                @Override
                public void onStart() {
                }

                @Override
                public void onCompleted(String filePath) {
                    count[0]++;
                    Log.d("count", "" + count[0]);
                    Toast.makeText(getApplicationContext(), "InventoryT Exported Successfully", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(Exception e) {
                    Log.d("e", "" + e);
                }
            }, getApplicationContext());
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Export failed. Have you made any entries into the database ?",
                    Toast.LENGTH_SHORT).show();
        }
    }

}
