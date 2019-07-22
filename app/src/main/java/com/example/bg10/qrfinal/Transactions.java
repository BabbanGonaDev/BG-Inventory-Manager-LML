package com.example.bg10.qrfinal;

import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

public class Transactions extends AppCompatActivity {
    SharedPreferences prefs;
    TextView tvLmdName;
    TextView tvLmdid;
    RecyclerView recyclerView;
    AllTransAdapter adapter;
    ArrayList<Transaction> tsns;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);

        prefs = getSharedPreferences("Preferences", MODE_PRIVATE);
        setTitle(prefs.getString("transtype","XXXX"));
        tvLmdName = findViewById(R.id.tvLmdName);
        tvLmdid = findViewById(R.id.tvLmdId);
        tvLmdName.setText(prefs.getString("lmdname",""));
        tvLmdid.setText(prefs.getString("lmdid",""));

        InventoryTDBhandler db = new InventoryTDBhandler(getApplicationContext());

        tsns = db.displayTransactions();
        //this switch block loads the category of transactions to be viewed to the transactions list
        switch (prefs.getString("transtype","XXXX"))
        {

            case "Receiving":
                tsns = db.displaySupplierTransactions();
                break;
            case "Internal Transfer In":
                tsns = db.displayITInTransactions();
                break;
            case "Delivery to LMD":
                tsns = db.displayLMDDeliveryTransactions();
                break;
            case "Delivery to LMR":
                tsns = db.displayLMRDeliveryTransactions();
                break;
            case "Internal Transfer Out":
                tsns = db.displayITOutTransactions();
                break;
            case "Pickup from LMD":
                tsns = db.displayITOutTransactions();   //it's the same as above because the ins are T and the outs are in the prefs variable
                break;
            case "Pickup from LMR":
                tsns = db.displayLMRPickupTransactions();
                break;

                //Put the ones for Delivery to Delivery to customer. (And maybe add collect LMR Payment.)
        }

        recyclerView = findViewById(R.id.rv);
        adapter = new AllTransAdapter(getApplicationContext(), tsns, new AllTransAdapter.OnItemClickListener() {
            @Override
            public void onClick(Transaction trans) {
                View v = LayoutInflater.from(getApplicationContext()).inflate(R.layout.trans_details,null, false);
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

                new AlertDialog.Builder(Transactions.this).setView(v)
                        .setTitle("Transaction Details")
                        .show();

            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(adapter);


        //this switch block automatically loads the appropriate fragment if the activity was loaded from the LMDScan activity
        if(prefs.getBoolean("loadfrag",false))
        {
            FragmentManager fragmentManager = getFragmentManager();
            switch (prefs.getString("fragment",""))
            {
                case "Stocking":
                    fragmentManager.beginTransaction().replace(R.id.frag_container, new StockingDetails()).commit();
                    break;
                case "Stocking2":
                    fragmentManager.beginTransaction().replace(R.id.frag_container, new LMtoLMDetails()).commit();
                    break;
                case "Contract":
                    fragmentManager.beginTransaction().replace(R.id.frag_container, new ContractDetails()).commit();
                    break;
                case "Return":
                    fragmentManager.beginTransaction().replace(R.id.frag_container, new ReturnDetails()).commit();
                    break;
            }
        }

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Load fragments based on shared preferences
                FragmentManager fragmentManager = getFragmentManager();
                switch (prefs.getString("fragment",""))
                {
                    case "Stocking":
                        fragmentManager.beginTransaction().replace(R.id.frag_container, new StockingDetails()).commit();
                        break;
                    case "Stocking2":
                        fragmentManager.beginTransaction().replace(R.id.frag_container, new LMtoLMDetails()).commit();
                        break;
                    case "Contract":
                        fragmentManager.beginTransaction().replace(R.id.frag_container, new ContractDetails()).commit();
                        break;
                    case "Return":
                        fragmentManager.beginTransaction().replace(R.id.frag_container, new ReturnDetails()).commit();
                        break;
                }
            }
        });


    }

    @Override
    public void onBackPressed() {
        //on back pressed launches the zxing activity which closes the app straight away so we have to launch it explicitly
        //clears the preferences leaving the fragment to load
        prefs.edit().remove("product").commit();
        prefs.edit().remove("productid").commit();
        prefs.edit().remove("waybill").commit();
        prefs.edit().remove("loadfrag").commit();
        prefs.edit().remove("date1").commit();
        prefs.edit().remove("date2").commit();

        //edit 23/07/2018: disable the back button as per Tobi's request
//        Intent intent = new Intent(getApplicationContext(), Operations.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        startActivity(intent);
    }
}
