package com.example.bg10.qrfinal;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Delivery extends Activity implements View.OnClickListener {
    SharedPreferences prefs1;
    SharedPreferences prefs;
    Button btnITIn;
    Button btnLMDDelivery;
    Button btnLMRDelivery;
    Button btnSupplierDelivery;
    Button btnCustomerDelivery;
    Button btnStoreDelivery;
    Button btnTeamMemberDelivery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery);
        prefs1 = getSharedPreferences("Preferences1", MODE_PRIVATE);
        prefs = getSharedPreferences("Preferences", MODE_PRIVATE);

        btnITIn = findViewById(R.id.btnITIn);
        btnLMDDelivery = findViewById(R.id.btnLMDDelivery);
        btnLMRDelivery = findViewById(R.id.btnLMRDelivery);
        btnSupplierDelivery = findViewById(R.id.btnSupplierDelivery);
        btnCustomerDelivery = findViewById(R.id.btnCustomerDelivery);
        btnStoreDelivery = findViewById(R.id.btnStoreDelivery);
        btnTeamMemberDelivery = findViewById(R.id.btnTeamMemberDelivery);


        btnITIn.setOnClickListener(this);
        btnLMDDelivery.setOnClickListener(this);
        btnLMRDelivery.setOnClickListener(this);
        btnSupplierDelivery.setOnClickListener(this);
        btnCustomerDelivery.setOnClickListener(this);
        btnStoreDelivery.setOnClickListener(this);
        btnTeamMemberDelivery.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btnITIn:
                startActivity(new Intent(Delivery.this, LMDScan.class));
                prefs.edit().putString("fragment", "Stocking2")
                        .putString("transtype", "Internal Transfer In")
                        .putString("datatobescanned", "LMD")
                        .putString("scannerheading", "Scan Team Member Details")
                        .commit();
                break;
            case R.id.btnLMDDelivery:
                startActivity(new Intent(Delivery.this, LMDScan.class));
                prefs.edit().putString("fragment", "Stocking2")
                        .putString("transtype", "Delivery to LMD")
                        .putString("datatobescanned", "LMD")
                        .putString("scannerheading", "Scan Team Member Details")
                        .commit();
                break;
            case R.id.btnLMRDelivery:
                startActivity(new Intent(Delivery.this, LMDScan.class));
                prefs.edit().putString("fragment", "Stocking2")
                        .putString("transtype", "Delivery to LMR")
                        .putString("datatobescanned", "LMD")
                        .putString("scannerheading", "Scan LMD Details")
                        .commit();
                break;
            case R.id.btnSupplierDelivery:
                startActivity(new Intent(Delivery.this, LMDScan.class));
                prefs.edit().putString("fragment", "Stocking")
                        .putString("transtype", "Receiving")
                        .putString("datatobescanned", "LMD")
                        .putString("scannerheading", "Scan Warehouse details")
                        .commit();
                break;

            case R.id.btnCustomerDelivery:
                startActivity(new Intent(Delivery.this, LMDScan.class));
                prefs.edit().putString("fragment", "Stocking2")
                        .putString("transtype", "Delivery to Customer")
                        .putString("datatobescanned", "LMD")
                        .putString("scannerheading", "Scan Team Member Details")
                        .commit();
                break;

            case R.id.btnStoreDelivery:
                startActivity(new Intent(Delivery.this, LMDScan.class));
                prefs.edit().putString("fragment", "Contract")
                        .putString("transtype", "Delivery to Warehouse")
                        .putString("datatobescanned", "LMD")
                        .putString("scannerheading", "Scan LMD Details")
                        .commit();
                break;

            case R.id.btnTeamMemberDelivery:
                startActivity(new Intent(Delivery.this, LMDScan.class));
                prefs.edit().putString("fragment", "Stocking2")
                        .putString("transtype", "Delivery to Team Member")
                        .putString("datatobescanned", "LMD")
                        .putString("scannerheading", "Scan Team Member Details")
                        .commit();
                break;
        }
    }
}


