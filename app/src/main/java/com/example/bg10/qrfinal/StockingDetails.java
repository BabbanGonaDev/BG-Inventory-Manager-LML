package com.example.bg10.qrfinal;


import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;


public class StockingDetails extends Fragment implements View.OnClickListener {
    SharedPreferences prefs;
    TextView tvProduct;
    TextView tvWaybillNo;
    TextView tvDate1;
    TextView tvDate2;
    EditText etUnit;
    EditText etUnit2;
    TextView tvSupplierName;
    Button btnProdScan;
    Button btnReceiptScan;
    Button btnDate1;
    Button btnDate2;
    Button btnDeliver;
    Button btnHome;
    Button btnCancel;
    Button btnSupplierScan;
    String check;

    SmartUpdateAccess smartUpdateAccess;

    public StockingDetails() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_stocking_details, container, false);
        prefs = getActivity().getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        smartUpdateAccess = new SmartUpdateAccess(getActivity());

        tvProduct = v.findViewById(R.id.tvProduct);
        tvWaybillNo = v.findViewById(R.id.tvWaybillNo);
        tvDate1 = v.findViewById(R.id.tvDate1);
        tvDate2 = v.findViewById(R.id.tvDate2);
        etUnit = v.findViewById(R.id.etUnit);
        etUnit2 = v.findViewById(R.id.etUnit2);
        tvSupplierName = v.findViewById(R.id.tvSupplierName);

        tvProduct.setText(prefs.getString("product", ""));
        tvWaybillNo.setText(prefs.getString("waybill", ""));
        tvDate1.setText(prefs.getString("date1",""));
        tvDate2.setText(prefs.getString("date2",""));
        tvSupplierName.setText(prefs.getString("supplier ID", ""));

        btnProdScan = v.findViewById(R.id.btnProdScan);
        btnReceiptScan = v.findViewById(R.id.btnReceiptScan);
        btnDate1 = v.findViewById(R.id.btnDate1Scan);
        btnDate2 = v.findViewById(R.id.btnDate2Scan);
        btnDeliver = v.findViewById(R.id.btnDeliver);
        btnHome = v.findViewById(R.id.btnHome);
        btnCancel = v.findViewById(R.id.btnCancel);
        btnSupplierScan = v.findViewById(R.id.btnSupplierScan);
        btnProdScan.setOnClickListener(this);
        btnReceiptScan.setOnClickListener(this);
        btnDate1.setOnClickListener(this);
        btnDate2.setOnClickListener(this);
        btnDeliver.setOnClickListener(this);
        btnHome.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnSupplierScan.setOnClickListener(this);

        check = "no";

        return v;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnDate1Scan:
                Calendar c = Calendar.getInstance();
                final int y = c.get(Calendar.YEAR);
                final int m = c.get(Calendar.MONTH);
                final int d = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(getActivity(), android.R.style.Theme_Holo_Light_Dialog_MinWidth, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        //checks for the validity of the date
                        if((year > y) || ((year <= y)&&(month > m)) || ((year <= y)&&(month <= m)&&(dayOfMonth > d)))
                        {
                            //if date is invalid, do nothing
                            Toast.makeText(getActivity(),"Please enter a valid date", Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            prefs.edit().putString("date1", year + "-" + String.format("%02d", month + 1) +"-"+ dayOfMonth).commit();
                            tvDate1.setText(prefs.getString("date1",""));
                        }
                    }
                }, y,m,d);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
                break;
            case R.id.btnDate2Scan:
                Calendar c2 = Calendar.getInstance();
                final int y2 = c2.get(Calendar.YEAR);
                final int m2 = c2.get(Calendar.MONTH);
                final int d2 = c2.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog2 = new DatePickerDialog(getActivity(), android.R.style.Theme_Holo_Light_Dialog_MinWidth, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        //checks for the validity of the date
                        if((year > y2) || ((year <= y2)&&(month > m2)) || ((year <= y2)&&(month <= m2)&&(dayOfMonth > d2)))
                        {
                            //if date is invalid, do nothing
                            Toast.makeText(getActivity(),"Please enter a valid date", Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            prefs.edit().putString("date2", year + "-" + String.format("%02d", month + 1) +"-"+ dayOfMonth).commit();
                            tvDate2.setText(prefs.getString("date2",""));
                        }
                    }
                }, y2,m2,d2);
                dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog2.show();
                break;
            case R.id.btnSupplierScan:
                AlertDialog.Builder choose = new AlertDialog.Builder(getActivity());
                LayoutInflater factory = LayoutInflater.from(getActivity());
                choose.setCancelable(true);
                final View views = factory.inflate(R.layout.select_supplier_layout, null);
                final Button btnEnterSupplierID = views.findViewById(R.id.enterSupplierID);
                final Button btnScanSupplierID = views.findViewById(R.id.scanSupplierID);
                choose.setView(views);

                final AlertDialog ad = choose.show();

                btnEnterSupplierID.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), enterSupplier.class);
                        startActivity(intent);
                    }
                });

                btnScanSupplierID.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        prefs.edit().putString("scannerheading", "Scan Supplier's Details")
                                .putString("datatobescanned", "supplier").commit();
                        startActivity(new Intent(getActivity(), LMDScan.class));
                    }
                });
                break;

            case R.id.btnProdScan:
                prefs.edit().putString("scannerheading", "Scan Product Details")
                        .putString("datatobescanned", "product").commit();
                startActivity(new Intent(getActivity(), LMDScan.class));
                break;
            case R.id.btnReceiptScan:
                prefs.edit().putString("scannerheading", "Scan Waybill Details")
                        .putString("datatobescanned", "receipt").commit();
                startActivity(new Intent(getActivity(), LMDScan.class));
                break;
            case R.id.btnCancel:
                getFragmentManager().beginTransaction().remove(StockingDetails.this).commit();      //removes the fragment from view
                //clears the product fragment
                prefs.edit().remove("product").commit();
                prefs.edit().remove("productid").commit();
                prefs.edit().remove("waybill").commit();
                prefs.edit().remove("date1").commit();
                prefs.edit().remove("date2").commit();
                prefs.edit().putBoolean("loadfrag", false).commit();
                break;
            case R.id.btnHome:
                //clears the preferences
                prefs.edit().clear().commit();
                //takes them to the home page
                Intent intent = new Intent(getActivity(), Operations.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;
            case R.id.btnDeliver:
                if (etUnit.getText().toString().equals(etUnit2.getText().toString()) && !etUnit.getText().toString().equals("")
                        && !tvProduct.getText().toString().equals("") && !tvWaybillNo.getText().toString().equals("")
                        && tvDate1.getText().toString().equals(tvDate2.getText().toString()) && !tvDate1.getText().equals("")) {

                    switch (prefs.getString("transtype","XXXX")) {
                        case "Receiving":

                            //SmartUpdateAccess smartUpdateAccess = new SmartUpdateAccess(getActivity());
                            smartUpdateAccess.open();
                            Integer stockingQty = smartUpdateAccess.CheckStockingQty(prefs.getString("productid", ""));
                            Integer unit = Integer.parseInt(etUnit.getText().toString());
                            if(unit > stockingQty && check.equals("no")){
                                Snackbar snackbar = Snackbar.make(getActivity().findViewById(android.R.id.content), "Please Confirm Stocking Quantity again.", Snackbar.LENGTH_LONG);
                                etUnit.setText("");
                                etUnit2.setText("");
                                snackbar.show();
                                check = "yes";
                                break;
                            }

                            String supplierName = smartUpdateAccess.CheckSupplier(prefs.getString("productid", ""));
                            if(supplierName != prefs.getString("supplier name", "") && check.equals("no")){
                                Snackbar snackbar = Snackbar.make(getActivity().findViewById(android.R.id.content), "Please Confirm Supplier again.", Snackbar.LENGTH_LONG);
                                //tvSupplierName.setText("");
                                snackbar.show();
                                check = "yes";
                                break;
                            }

                            Integer cartonNo = smartUpdateAccess.CheckCartonNo(prefs.getString("productid", ""));
                            if(unit % cartonNo != 0 && check.equals("no")){
                                Snackbar snackbar = Snackbar.make(getActivity().findViewById(android.R.id.content), "Please Confirm Quantity again.", Snackbar.LENGTH_LONG);
                                etUnit.setText("");
                                etUnit2.setText("");
                                snackbar.show();
                                check = "yes";
                                break;
                            }

                            new AlertDialog.Builder(getActivity())
                                    .setTitle("Begin stocking warehouse")
                                    .setMessage("Are you sure you want to begin stocking " + etUnit.getText().toString() + " of " + tvProduct.getText().toString() + "?")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id) {

                                            final AlertDialog.Builder alertadd = new AlertDialog.Builder(getActivity());
                                            LayoutInflater factory = LayoutInflater.from(getActivity());
                                            alertadd.setCancelable(true);
                                            final View views = factory.inflate(R.layout.custom_layout, null);
                                            final TextView details = views.findViewById(R.id.display);
                                            final TextView details2 = views.findViewById(R.id.display2);
                                            final Button submit = views.findViewById(R.id.positive_button);
                                            alertadd.setView(views);

                                            final Button cancel = views.findViewById(R.id.negative_button);

                                            details.setText(details.getText()+""+"Waybill No: "+(tvWaybillNo.getText().toString())+"\n"+"Product ID: "+prefs.getString("productid", "")+"\n"+"Product: "+prefs.getString("product", "")+"\n"+"Unit: "+etUnit.getText().toString()+"\n"+"In: "+prefs.getString("lmdid", "")+"\n"+"Out: "+prefs.getString("supplier ID", "")+"\n"+"Type: Inv"+"\n"+"Unit Price: 0"+"\n"+"Suggested Unit Price: 0"+"\n"+"LMD Hub: "+prefs.getString("lmdhub","")+"\n"+"Date: "+tvDate1.getText().toString());

                                            //details.setText(details.getText()+""+"Waybill No: "+"\n"+"Product ID: "+"\n"+"Product: "+"\n"+"Unit: "+"\n"+"In: "+"\n"+"Out: "+"\n"+"Type: "+"\n"+"Unit Price: "+"\n"+"Suggested Unit Price: "+"\n"+"LMD Hub: "+"\n"+"Date: ");

                                            //details2.setText(details2.getText()+"\n"+(tvWaybillNo.getText().toString())+"\n"+prefs.getString("productid", "")+"\n"+prefs.getString("product", "")+"\n"+etUnit.getText().toString()+"\n"+"\n"+"GIT-In"+"\n"+"Inv"+"\n"+"0"+"\n"+"0"+"\n"+prefs.getString("lmdhub","")+"\n"+tvDate1.getText().toString());

                                            final AlertDialog ad = alertadd.show();
                                            submit.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    InventoryTDBhandler db1 = new InventoryTDBhandler(getActivity());
                                                    if (!db1.onAdd(new Transaction(tvWaybillNo.getText().toString(), prefs.getString("productid", ""),
                                                            prefs.getString("product", ""), etUnit.getText().toString(), prefs.getString("lmdid", ""),
                                                            prefs.getString("supplier ID", ""), "Inv", "0","0", prefs.getString("lmdhub",""), tvDate1.getText().toString()))) {
                                                        Toast.makeText(getActivity(), "Transaction already exists in the database", Toast.LENGTH_LONG).show();
                                                    }
                                                    //Old version
                                                    /*if (!db1.onAdd(new Transaction(tvWaybillNo.getText().toString(), prefs.getString("productid", ""),
                                                            prefs.getString("product", ""), etUnit.getText().toString(), prefs.getString("lmdid", ""),
                                                            "GIT-In", "Inv", "0","0", prefs.getString("lmdhub",""), tvDate1.getText().toString()))) {
                                                        Toast.makeText(getActivity(), "Transaction already exists in the database", Toast.LENGTH_LONG).show();
                                                    }*/
                                                    //clears the product fragment
                                                    prefs.edit().remove("product").commit();
                                                    prefs.edit().remove("productid").commit();
                                                    prefs.edit().remove("waybill").commit();
                                                    prefs.edit().remove("date1").commit();
                                                    prefs.edit().remove("date2").commit();
                                                    prefs.edit().putBoolean("loadfrag", false).commit();
                                                    startActivity(new Intent(getActivity(), Transactions.class)
                                                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));

                                                }
                                            });

                                            cancel.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    ad.dismiss();
                                                }
                                            });


                                        }
                                    })

                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id) {
                                            // takes them back to the activity before the zxing activity

                                        }
                                    })
                                    .show();
                            break;
                    }

                } else {
                    Toast.makeText(getActivity(), "Make sure the input fields are correctly filled", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }
}
