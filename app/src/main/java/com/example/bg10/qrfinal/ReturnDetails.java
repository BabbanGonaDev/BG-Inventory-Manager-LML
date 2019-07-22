package com.example.bg10.qrfinal;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class ReturnDetails extends Fragment implements View.OnClickListener {
    SharedPreferences prefs;
    TextView tvDate1;
    TextView tvDate2;
    TextView tvLMDName2;
    TextView tvLMDId2;
    TextView tvProduct;
    EditText etUnit;
    EditText etUnit2;
    Button btnDate1;
    Button btnDate2;
    Button btnDeliver;
    Button btnLMD2Scan;
    Button btnProdScan;
    Button btnHome;
    Button btnCancel;

    public ReturnDetails(){
        //Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_return_details, container, false);
        prefs = getActivity().getSharedPreferences("Preferences", Context.MODE_PRIVATE);

        tvDate1 = v.findViewById(R.id.tvDate1);
        tvDate2 = v.findViewById(R.id.tvDate2);
        tvLMDName2 = v.findViewById(R.id.tvLmdName2);
        tvLMDId2 = v.findViewById(R.id.tvLmdId2);
        tvProduct = v.findViewById(R.id.tvProduct);
        etUnit = v.findViewById(R.id.etUnit);
        etUnit2 = v.findViewById(R.id.etUnit2);
        tvLMDName2.setText(prefs.getString("lmdname2", ""));
        tvLMDId2.setText(prefs.getString("lmdid2", ""));
        tvProduct.setText(prefs.getString("product", ""));
        tvDate1.setText(prefs.getString("date1", ""));
        tvDate2.setText(prefs.getString("date2", ""));

        btnDate1 = v.findViewById(R.id.btnDate1Scan);
        btnDate2 = v.findViewById(R.id.btnDate2Scan);
        btnLMD2Scan = v.findViewById(R.id.btnLMD2Scan);
        btnProdScan = v.findViewById(R.id.btnProdScan);
        btnDeliver = v.findViewById(R.id.btnDeliver);
        btnHome = v.findViewById(R.id.btnHome);
        btnCancel = v.findViewById(R.id.btnCancel);

        btnDate1.setOnClickListener(this);
        btnDate2.setOnClickListener(this);
        btnLMD2Scan.setOnClickListener(this);
        btnProdScan.setOnClickListener(this);
        btnDeliver.setOnClickListener(this);
        btnHome.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        return v;
    }


    @Override
    public void onClick(View v){
        switch(v.getId()) {
            case R.id.btnDate1Scan:
                Calendar c = Calendar.getInstance();
                final int y = c.get(Calendar.YEAR);
                final int m = c.get(Calendar.MONTH);
                final int d = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(getActivity(), android.R.style.Theme_Holo_Light_Dialog_MinWidth, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        //checks for the validity of the date
                        if ((year > y) || ((year <= y) && (month > m)) || ((year <= y) && (month <= m) && (dayOfMonth > d))) {
                            //if date is invalid, do nothing
                            Toast.makeText(getActivity(), "Please enter a valid date", Toast.LENGTH_LONG).show();
                        } else {
                            prefs.edit().putString("date1", year + "-" + String.format("%02d", month + 1) + "-" + dayOfMonth).commit();
                            tvDate1.setText(prefs.getString("date1", ""));
                        }
                    }
                }, y, m, d);
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
                        if ((year > y2) || ((year <= y2) && (month > m2)) || ((year <= y2) && (month <= m2) && (dayOfMonth > d2))) {
                            //if date is invalid, do nothing
                            Toast.makeText(getActivity(), "Please enter a valid date", Toast.LENGTH_LONG).show();
                        } else {
                            prefs.edit().putString("date2", year + "-" + String.format("%02d", month + 1) + "-" + dayOfMonth).commit();
                            tvDate2.setText(prefs.getString("date2", ""));
                        }
                    }
                }, y2, m2, d2);
                dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog2.show();
                break;
            case R.id.btnCancel:
                getFragmentManager().beginTransaction().remove(ReturnDetails.this).commit();      //removes the fragment from view
                //clears the product fragment
                prefs.edit().remove("product").commit();
                prefs.edit().remove("productid").commit();
                prefs.edit().remove("waybill").commit();
                prefs.edit().remove("unitprice").commit();
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
            case R.id.btnLMD2Scan:
                prefs.edit().putString("scannerheading", "Scan Destination details")
                        .putString("datatobescanned", "LMD2").commit();
                startActivity(new Intent(getActivity(), LMDScan.class));
                break;
            case R.id.btnProdScan:
                prefs.edit().putString("scannerheading", "Scan Product Details")
                        .putString("datatobescanned", "product").commit();
                startActivity(new Intent(getActivity(), LMDScan.class));
                break;
            case R.id.btnDeliver:
                if(etUnit.getText().toString().equals(etUnit2.getText().toString()) && !tvLMDName2.getText().toString().equals("") && !etUnit.getText().toString().equals("")
                        && !tvProduct.getText().toString().equals("") && tvDate1.getText().toString().equals(tvDate2.getText().toString()) && !tvDate1.getText().equals("")) {


                    new AlertDialog.Builder(getActivity())
                            .setTitle("Return From LMR")
                            .setMessage("Are you sure you want to return " + etUnit.getText().toString() + " of " + tvProduct.getText().toString() + " to " + tvLMDName2.getText().toString())
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    final android.support.v7.app.AlertDialog.Builder alertadd = new android.support.v7.app.AlertDialog.Builder(getActivity());
                                    LayoutInflater factory = LayoutInflater.from(getActivity());
                                    alertadd.setCancelable(true);
                                    final View views = factory.inflate(R.layout.custom_layout, null);
                                    final TextView details = views.findViewById(R.id.display);
                                    final TextView details2 = views.findViewById(R.id.display2);
                                    final Button submit = views.findViewById(R.id.positive_button);
                                    alertadd.setView(views);

                                    final Button cancel = views.findViewById(R.id.negative_button);

                                    details.setText(details.getText()+""+"Product ID: "+prefs.getString("productid", "")+"\n"+"Product: "+prefs.getString("product", "")+"\n"+"Unit: "+etUnit.getText().toString()+"\n"+"In: "+prefs.getString("lmdid2", "")+"\n"+"Out: "+prefs.getString("lmdid", "")+"\n"+"Type: Inv"+"\n"+"Unit Price: 0"+"\n"+"Suggested Unit Price: 0"+"\n"+"LMD Hub: "+prefs.getString("lmdhub2","")+"\n"+"Date: "+tvDate1.getText().toString());

                                    final android.support.v7.app.AlertDialog ad = alertadd.show();
                                    submit.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            InventoryTDBhandler db1 = new InventoryTDBhandler(getActivity());
                                            if (!db1.onAdd(new Transaction("N/A", prefs.getString("productid", ""),
                                                    prefs.getString("product", ""), etUnit.getText().toString(), prefs.getString("lmdid2", ""),
                                                    prefs.getString("lmdid", ""), "Inv", "0", "0", prefs.getString("lmdhub2", ""), tvDate1.getText().toString()))) {
                                                Toast.makeText(getActivity(), "Transaction already exists in the database", Toast.LENGTH_LONG).show();
                                            }
                                            //clears the product fragment
                                            prefs.edit().remove("product").commit();
                                            prefs.edit().remove("productid").commit();
                                            prefs.edit().remove("waybill").commit();
                                            prefs.edit().remove("date1").commit();
                                            prefs.edit().remove("date2").commit();
                                            //prefs.edit().remove("lmdname2").commit();     //so that the lmd details don't need to be scanned
                                            //prefs.edit().remove("lmdid2").commit();
                                            startActivity(new Intent(getActivity(), Transactions.class)
                                                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                        }
                                    });

                                    cancel.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            ad.dismiss();
                                        }
                                    });


                                }
                            })

                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            })
                            .show();
                }
        }
    }


}
