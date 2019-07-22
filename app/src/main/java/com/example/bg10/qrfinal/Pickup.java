package com.example.bg10.qrfinal;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

public class Pickup extends AppCompatActivity implements View.OnClickListener{
    SharedPreferences prefs1;
    SharedPreferences prefs;
    Button btnITOut;
    Button btnLMDPickup;
    Button btnlMRPickup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pickup);
        prefs1 = getSharedPreferences("Preferences1", MODE_PRIVATE);
        prefs = getSharedPreferences("Preferences", MODE_PRIVATE);

        btnITOut = findViewById(R.id.btnITOut);
        btnLMDPickup = findViewById(R.id.btnLMDPickup);
        btnlMRPickup = findViewById(R.id.btnLMRPickup);

        btnITOut.setOnClickListener(this);
        btnLMDPickup.setOnClickListener(this);
        btnlMRPickup.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btnITOut:
                startActivity(new Intent(Pickup.this, LMDScan.class));
                prefs.edit().putString("fragment", "Stocking2")
                        .putString("transtype", "Internal Transfer Out")
                        .putString("datatobescanned", "LMD")
                        .putString("scannerheading", "Scan Warehouse Details")
                        .commit();
                break;
            case R.id.btnLMDPickup:
                startActivity(new Intent(Pickup.this, LMDScan.class));
                prefs.edit().putString("fragment", "Stocking2")
                        .putString("transtype", "Pickup from LMD")
                        .putString("datatobescanned", "LMD")
                        .putString("scannerheading", "Scan LMD Details")
                        .commit();
                break;
            case R.id.btnLMRPickup:
                AlertDialog.Builder choose = new AlertDialog.Builder(Pickup.this);
                LayoutInflater factory = LayoutInflater.from(Pickup.this);
                choose.setCancelable(true);
                final View views = factory.inflate(R.layout.choose_layout, null);
                final Button btnEnterID = views.findViewById(R.id.enterID);
                final Button btnScanID = views.findViewById(R.id.scanID);
                choose.setView(views);

                final AlertDialog ad = choose.show();

                btnEnterID.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        prefs.edit().putString("fragment", "Stocking2")
                                .putString("transtype", "Pickup from LMR")
                                .putString("datatobescanned", "LMD")
                                .putString("scannerheading", "Scan LMR Details")
                                .commit();
                        Intent intent = new Intent(Pickup.this, enterLMR.class);
                        startActivity(intent);
                    }
                });

                btnScanID.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(Pickup.this, LMDScan.class));
                        prefs.edit().putString("fragment", "Stocking2")
                                .putString("transtype", "Pickup from LMR")
                                .putString("datatobescanned", "LMD")
                                .putString("scannerheading", "Scan LMR Details")
                                .commit();
                    }
                });
                break;
        }
    }
}
