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
import android.support.v7.app.AlertDialog;
import android.text.Layout;
import android.util.Log;
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


public class LMtoLMDetails extends Fragment implements View.OnClickListener {
    SharedPreferences prefs;
    SharedPreferences prefs1;
    TextView tvProduct, tvWaybillNo, tvLMDId2, tvLmdName2, tvDate1, tvDate2, tvWaybillText;
    EditText etUnit;
    EditText etUnit2;
    Button btnDate1, btnDate2, btnDeliver, btnHome, btnCancel, btnWaybillDetails, btnLMD2Scan;
    String check;

    SmartUpdateAccess smartUpdateAccess;

    public LMtoLMDetails() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_lmto_lmdetails, container, false);
        /*prefs1 = getActivity().getSharedPreferences("Preferences1", Context.MODE_PRIVATE);*/
        prefs = getActivity().getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        smartUpdateAccess = new SmartUpdateAccess(getActivity());

        tvProduct = v.findViewById(R.id.tvProduct);
        tvWaybillNo = v.findViewById(R.id.tvWaybillNo);
        tvLMDId2 = v.findViewById(R.id.tvLmdId2);
        tvLmdName2 = v.findViewById(R.id.tvLmdName2);
        tvDate1 = v.findViewById(R.id.tvDate1);
        tvDate2 = v.findViewById(R.id.tvDate2);
        etUnit = v.findViewById(R.id.etUnit);
        etUnit2 = v.findViewById(R.id.etUnit2);

        tvWaybillText = v.findViewById(R.id.textView4);

        tvProduct.setText(prefs.getString("product", ""));
        tvWaybillNo.setText(prefs.getString("waybill", "0"));
        tvLMDId2.setText(prefs.getString("lmdid2", ""));
        tvLmdName2.setText(prefs.getString("lmdname2", ""));
        tvDate1.setText(prefs.getString("date1",""));
        tvDate2.setText(prefs.getString("date2",""));
        etUnit.setText(prefs.getString("quantity", ""));
        etUnit2.setText("");

        btnDate1 = v.findViewById(R.id.btnDate1Scan);
        btnDate2 = v.findViewById(R.id.btnDate2Scan);
        btnDeliver = v.findViewById(R.id.btnDeliver);
        btnHome = v.findViewById(R.id.btnHome);
        btnCancel = v.findViewById(R.id.btnCancel);
        btnLMD2Scan = v.findViewById(R.id.btnLMD2Scan);
        btnWaybillDetails = v.findViewById(R.id.btnWaybillDetails);
        btnDate1.setOnClickListener(this);
        btnDate2.setOnClickListener(this);
        btnDeliver.setOnClickListener(this);
        btnHome.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnLMD2Scan.setOnClickListener(this);
        btnWaybillDetails.setOnClickListener(this);

        if(prefs.getString("transtype", "").equals("Delivery to Team Member")){
            tvWaybillNo.setVisibility(View.GONE);
            tvWaybillText.setVisibility(View.GONE);
        }

        check = "no";

        return v;
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
            case R.id.btnWaybillDetails:
                prefs.edit().putString("scannerheading", "Scan Waybill details")
                        .putString("datatobescanned", "details").commit();
                startActivity(new Intent(getActivity(), LMDScan.class));
                break;
            case R.id.btnLMD2Scan:
                prefs.edit().putString("scannerheading", "Scan Destination details")
                         .putString("datatobescanned", "LMD2").commit();
                startActivity(new Intent(getActivity(), LMDScan.class));
                break;
            case R.id.btnCancel:
                getFragmentManager().beginTransaction().remove(LMtoLMDetails.this).commit();      //removes the fragment from view
                //clears the product fragment
                prefs.edit().remove("product").commit();
                prefs.edit().remove("productid").commit();
                prefs.edit().remove("waybill").commit();
                prefs.edit().remove("lmdname2").commit();
                prefs.edit().remove("lmdid2").commit();
                prefs.edit().remove("date1").commit();
                prefs.edit().remove("date2").commit();
                prefs.edit().remove("quantity").commit();
                prefs.edit().putBoolean("loadfrag", false).commit();
                break;
            case R.id.btnHome:
                //clears the preferences
                prefs.edit().clear().commit();
                Intent intent = new Intent(getActivity(), Operations.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;
            case R.id.btnDeliver:
                if (etUnit.getText().toString().equals(etUnit2.getText().toString()) && !etUnit.getText().toString().equals("")
                        && !tvProduct.getText().toString().equals("") && !tvWaybillNo.getText().toString().equals("")
                        && tvDate1.getText().toString().equals(tvDate2.getText().toString()) && !tvDate1.getText().equals("")) {
                    switch (prefs.getString("transtype", "XXXX")) {
                        case "Internal Transfer In":
                            new AlertDialog.Builder(getActivity())
                                    .setTitle("Internal Transfer In")
                                    .setMessage("Are you sure you want to move " + etUnit.getText().toString() + " of " + tvProduct.getText().toString() + " from " + prefs.getString("lmdid", "") + " to " + prefs.getString("lmdid2", "") + "?")
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

                                            details.setText(details.getText()+""+"Waybill No: "+(tvWaybillNo.getText().toString())+"\n"+"Product ID: "+prefs.getString("productid", "")+"\n"+"Product: "+prefs.getString("product", "")+"\n"+"Unit: "+etUnit.getText().toString()+"\n"+"In: "+prefs.getString("lmdid2", "")+"\n"+"Out: "+prefs.getString("lmdid", "")+"\n"+"Type: Inv"+"\n"+"Unit Price: 0"+"\n"+"Suggested Unit Price: 0"+"\n"+"LMD Hub: "+prefs.getString("lmdhub2","")+"\n"+"Date: "+tvDate1.getText().toString());

                                            //details2.setText(details2.getText()+"\n"+(tvWaybillNo.getText().toString())+"\n"+prefs.getString("productid", "")+"\n"+prefs.getString("product", "")+"\n"+etUnit.getText().toString()+"\n"+prefs.getString("lmdid2", "")+"\n"+prefs.getString("lmdid", "")+"\n"+"Inv"+"\n"+"0"+"\n"+"0"+"\n"+prefs.getString("lmdhub","")+"\n"+tvDate1.getText().toString());

                                            final AlertDialog ad = alertadd.show();
                                            submit.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {

                                                    submit.setEnabled(false);
                                                     InventoryTDBhandler db1 = new InventoryTDBhandler(getActivity());
                                                    if (!db1.onAdd(new Transaction(tvWaybillNo.getText().toString(), prefs.getString("productid", ""),
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
                                                    prefs.edit().remove("lmdname2").commit();
                                                    prefs.edit().remove("lmdid2").commit();
                                                    prefs.edit().remove("quantity").commit();
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

                        case "Delivery to LMD":
                            //Smart checks commented out here
                            /*smartUpdateAccess.open();
                            Integer upperLimit2 = smartUpdateAccess.CheckUpperLimit(prefs.getString("productid", ""));
                            Integer currentUnit2 = Integer.parseInt(etUnit.getText().toString());
                            if(currentUnit2 > upperLimit2 && check.equals("no")){
                                Snackbar snackbar = Snackbar.make(getActivity().findViewById(android.R.id.content), "Please Confirm Product Quantity again.", Snackbar.LENGTH_LONG);
                                etUnit.setText("");
                                etUnit2.setText("");
                                snackbar.show();
                                check = "yes";
                                break;
                            }*/

                            new AlertDialog.Builder(getActivity())
                                    .setTitle("Delivery to LMD")
                                    .setMessage("Are you sure you want to move " + etUnit.getText().toString() + " of " + tvProduct.getText().toString() + " from " + prefs.getString("lmdid", "") + " to " + prefs.getString("lmdid2", "") + "?")
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

                                            details.setText(details.getText()+""+"Waybill No: "+(tvWaybillNo.getText().toString())+"\n"+"Product ID: "+prefs.getString("productid", "")+"\n"+"Product: "+prefs.getString("product", "")+"\n"+"Unit: "+etUnit.getText().toString()+"\n"+"In: "+prefs.getString("lmdid2", "")+"\n"+"Out: "+prefs.getString("lmdid", "")+"\n"+"Type: Inv"+"\n"+"Unit Price: 0"+"\n"+"Suggested Unit Price: 0"+"\n"+"LMD Hub: "+prefs.getString("lmdhub2","")+"\n"+"Date: "+tvDate1.getText().toString());

                                            //details2.setText(details2.getText()+"\n"+(tvWaybillNo.getText().toString())+"\n"+prefs.getString("productid", "")+"\n"+prefs.getString("product", "")+"\n"+etUnit.getText().toString()+"\n"+prefs.getString("lmdid2", "")+"\n"+prefs.getString("lmdid", "")+"\n"+"Inv"+"\n"+"0"+"\n"+"0"+"\n"+prefs.getString("lmdhub","")+"\n"+tvDate1.getText().toString());

                                            final AlertDialog ad = alertadd.show();
                                            submit.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {

                                                    submit.setEnabled(false);
                                                    InventoryTDBhandler db1 = new InventoryTDBhandler(getActivity());
                                                    if (!db1.onAdd(new Transaction(tvWaybillNo.getText().toString(), prefs.getString("productid", ""),
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
                                                    prefs.edit().remove("lmdname2").commit();
                                                    prefs.edit().remove("lmdid2").commit();
                                                    prefs.edit().remove("quantity").commit();
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

                        case "Delivery to LMR":
                            //Smart checks commented out here
                            //Check if product is an LMR product.
                            /*smartUpdateAccess.open();
                            if(smartUpdateAccess.checkLMRStatus(prefs.getString("productid", "")) && check.equals("no")){
                                Toast.makeText(getActivity(), "Please Confirm the product again", Toast.LENGTH_LONG).show();
                                check = "yes";
                                break;

                            }

                                //Check for Invoice Quantity 75% of dataset.
                                Integer invoiceQty = smartUpdateAccess.CheckInvoiceQty(prefs.getString("productid", ""));
                                Integer currentInvoiceQty = Integer.parseInt(etUnit.getText().toString());

                                if(currentInvoiceQty < invoiceQty && check.equals("no")){
                                    Snackbar snackbar = Snackbar.make(getActivity().findViewById(android.R.id.content), "Please Confirm Product Quantity again.", Snackbar.LENGTH_LONG);
                                    etUnit.setText("");
                                    etUnit2.setText("");
                                    snackbar.show();
                                    check = "yes";
                                    break;
                                }*/

                                new AlertDialog.Builder(getActivity())
                                        .setTitle("Delivery to LMR")
                                        .setMessage("Are you sure you want to move " + etUnit.getText().toString() + " of " + tvProduct.getText().toString() + " from " + prefs.getString("lmdid", "") + " to " + prefs.getString("lmdid2", "") + "?")
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

                                                details.setText(details.getText()+""+"Waybill No: "+(tvWaybillNo.getText().toString())+"\n"+"Product ID: "+prefs.getString("productid", "")+"\n"+"Product: "+prefs.getString("product", "")+"\n"+"Unit: "+etUnit.getText().toString()+"\n"+"In: "+prefs.getString("lmdid2", "")+"\n"+"Out: "+prefs.getString("lmdid", "")+"\n"+"Type: Inv"+"\n"+"Unit Price: 0"+"\n"+"Suggested Unit Price: 0"+"\n"+"LMD Hub: "+prefs.getString("lmdhub2","")+"\n"+"Date: "+tvDate1.getText().toString());

                                                //details2.setText(details2.getText()+"\n"+(tvWaybillNo.getText().toString())+"\n"+prefs.getString("productid", "")+"\n"+prefs.getString("product", "")+"\n"+etUnit.getText().toString()+"\n"+prefs.getString("lmdid2", "")+"\n"+prefs.getString("lmdid", "")+"\n"+"Inv"+"\n"+"0"+"\n"+"0"+"\n"+prefs.getString("lmdhub","")+"\n"+tvDate1.getText().toString());

                                                final AlertDialog ad = alertadd.show();
                                                submit.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {

                                                        submit.setEnabled(false);
                                                        InventoryTDBhandler db1 = new InventoryTDBhandler(getActivity());
                                                        if (!db1.onAdd(new Transaction(tvWaybillNo.getText().toString(), prefs.getString("productid", ""),
                                                                prefs.getString("product", ""), etUnit.getText().toString(), prefs.getString("lmdid2", ""),
                                                                prefs.getString("lmdid", ""), "Inv", "0", "0", prefs.getString("lmdhub2", ""), tvDate1.getText().toString()))) {

                                                            Toast.makeText(getActivity(), "Transaction already exists in the database", Toast.LENGTH_LONG).show();

                                                        }else{
                                                            db1.onAdd(new Transaction(" ", prefs.getString("productid", ""),
                                                                prefs.getString("product", ""), etUnit.getText().toString(), "Sale",
                                                                prefs.getString("lmdid2", ""), "Sale", "0", "0", prefs.getString("lmdhub", ""), tvDate1.getText().toString()));
                                                        }
                                                        //clears the product fragment
                                                        prefs.edit().remove("product").commit();
                                                        prefs.edit().remove("productid").commit();
                                                        prefs.edit().remove("waybill").commit();
                                                        prefs.edit().remove("date1").commit();
                                                        prefs.edit().remove("date2").commit();
                                                        prefs.edit().remove("lmdname2").commit();
                                                        prefs.edit().remove("lmdid2").commit();
                                                        prefs.edit().remove("quantity").commit();
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
                        case "Internal Transfer Out":
                            //Smart checks commented out here
                            /*//Check for known quantity of product.
                            smartUpdateAccess.open();
                            Integer knownQty2 = smartUpdateAccess.CheckKnownQty(prefs.getString("productid", ""));
                            Integer unit2 = Integer.parseInt(etUnit.getText().toString());

                            if(knownQty2 > unit2 && check.equals("no")){
                                Snackbar snackbar = Snackbar.make(getActivity().findViewById(android.R.id.content), "Please Confirm Product Quantity again.", Snackbar.LENGTH_LONG);
                                etUnit.setText("");
                                etUnit2.setText("");
                                snackbar.show();
                                check = "yes";
                                break;

                            }

                            smartUpdateAccess.open();
                            Integer cartonNo2 = smartUpdateAccess.CheckCartonNo(prefs.getString("productid", ""));
                            if((unit2 % cartonNo2) != 0 && check.equals("no")){
                                Snackbar snackbar = Snackbar.make(getActivity().findViewById(android.R.id.content), "Please Confirm Quantity again.", Snackbar.LENGTH_LONG);
                                etUnit.setText("");
                                etUnit2.setText("");
                                snackbar.show();
                                check = "yes";
                                break;
                            }*/

                            new AlertDialog.Builder(getActivity())
                                    .setTitle("Internal Transfer Out")
                                    .setMessage("Are you sure you want to move " + etUnit.getText().toString() + " of " + tvProduct.getText().toString() + " from " + prefs.getString("lmdid", "") + " to " + prefs.getString("lmdid2", "") + "?")
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


                                            details.setText(details.getText()+""+"Waybill No: "+(tvWaybillNo.getText().toString())+"\n"+"Product ID: "+prefs.getString("productid", "")+"\n"+"Product: "+prefs.getString("product", "")+"\n"+"Unit: "+etUnit.getText().toString()+"\n"+"In: "+prefs.getString("lmdid2", "")+"\n"+"Out: "+prefs.getString("lmdid", "")+"\n"+"Type: Inv"+"\n"+"Unit Price: 0"+"\n"+"Suggested Unit Price: 0"+"\n"+"LMD Hub: "+prefs.getString("lmdhub2","")+"\n"+"Date: "+tvDate1.getText().toString());

                                            //details.setText(details.getText()+""+"Waybill No: "+"\n"+"Product ID: "+"\n"+"Product: "+"\n"+"Unit: "+"\n"+"In: "+"\n"+"Out: "+"\n"+"Type: "+"\n"+"Unit Price: "+"\n"+"Suggested Unit Price: "+"\n"+"LMD Hub: "+"\n"+"Date: ");

                                            //details2.setText(details2.getText()+"\n"+(tvWaybillNo.getText().toString())+"\n"+prefs.getString("productid", "")+"\n"+prefs.getString("product", "")+"\n"+etUnit.getText().toString()+"\n"+prefs.getString("lmdid2", "")+"\n"+prefs.getString("lmdid", "")+"\n"+"Inv"+"\n"+"0"+"\n"+"0"+"\n"+prefs.getString("lmdhub","")+"\n"+tvDate1.getText().toString());

                                            final AlertDialog ad = alertadd.show();
                                            submit.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {

                                                    submit.setEnabled(false);
                                                    InventoryTDBhandler db1 = new InventoryTDBhandler(getActivity());
                                                    if (!db1.onAdd(new Transaction(tvWaybillNo.getText().toString(), prefs.getString("productid", ""),
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
                                                    prefs.edit().remove("lmdname2").commit();
                                                    prefs.edit().remove("lmdid2").commit();
                                                    prefs.edit().remove("quantity").commit();
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

                        case "Pickup from LMD":
                                //Smart checks commented out here
                                /*smartUpdateAccess.open();
                                Integer upperLimit = smartUpdateAccess.CheckUpperLimit(prefs.getString("productid", ""));
                                Integer currentUnit = Integer.parseInt(etUnit.getText().toString());
                                if(currentUnit > upperLimit && check.equals("no")){
                                    Snackbar snackbar = Snackbar.make(getActivity().findViewById(android.R.id.content), "Please Confirm Product Quantity again.", Snackbar.LENGTH_LONG);
                                    etUnit.setText("");
                                    etUnit2.setText("");
                                    snackbar.show();
                                    check = "yes";
                                    break;

                                }*/

                                new AlertDialog.Builder(getActivity())
                                    .setTitle("Pickup from LMD")
                                    .setMessage("Are you sure you want to move " + etUnit.getText().toString() + " of " + tvProduct.getText().toString() + " from " + prefs.getString("lmdid", "") + " to " + prefs.getString("lmdid2", "") + "?")
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


                                            details.setText(details.getText()+""+"Waybill No: "+(tvWaybillNo.getText().toString())+"\n"+"Product ID: "+prefs.getString("productid", "")+"\n"+"Product: "+prefs.getString("product", "")+"\n"+"Unit: "+etUnit.getText().toString()+"\n"+"In: "+prefs.getString("lmdid2", "")+"\n"+"Out: "+prefs.getString("lmdid", "")+"\n"+"Type: Inv"+"\n"+"Unit Price: 0"+"\n"+"Suggested Unit Price: 0"+"\n"+"LMD Hub: "+prefs.getString("lmdhub2","")+"\n"+"Date: "+tvDate1.getText().toString());

                                            //details.setText(details.getText()+""+"Waybill No: "+"\n"+"Product ID: "+"\n"+"Product: "+"\n"+"Unit: "+"\n"+"In: "+"\n"+"Out: "+"\n"+"Type: "+"\n"+"Unit Price: "+"\n"+"Suggested Unit Price: "+"\n"+"LMD Hub: "+"\n"+"Date: ");

                                            //details2.setText(details2.getText()+"\n"+(tvWaybillNo.getText().toString())+"\n"+prefs.getString("productid", "")+"\n"+prefs.getString("product", "")+"\n"+etUnit.getText().toString()+"\n"+prefs.getString("lmdid2", "")+"\n"+prefs.getString("lmdid", "")+"\n"+"Inv"+"\n"+"0"+"\n"+"0"+"\n"+prefs.getString("lmdhub","")+"\n"+tvDate1.getText().toString());

                                            final AlertDialog ad = alertadd.show();
                                            submit.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {

                                                    submit.setEnabled(false);
                                                    InventoryTDBhandler db1 = new InventoryTDBhandler(getActivity());
                                                    if (!db1.onAdd(new Transaction(tvWaybillNo.getText().toString(), prefs.getString("productid", ""),
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
                                                    prefs.edit().remove("lmdname2").commit();
                                                    prefs.edit().remove("lmdid2").commit();
                                                    prefs.edit().remove("quantity").commit();
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

                        case "Pickup from LMR":
                            new AlertDialog.Builder(getActivity())
                                    .setTitle("Pickup from LMR")
                                    .setMessage("Are you sure you want to move " + etUnit.getText().toString() + " of " + tvProduct.getText().toString() + " from " + prefs.getString("lmdid", "") + " to " + prefs.getString("lmdid2", "") + "?")
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

                                            details.setText(details.getText()+""+"Waybill No: "+(tvWaybillNo.getText().toString())+"\n"+"Product ID: "+prefs.getString("productid", "")+"\n"+"Product: "+prefs.getString("product", "")+"\n"+"Unit: "+etUnit.getText().toString()+"\n"+"In: "+prefs.getString("lmdid2", "")+"\n"+"Out: "+prefs.getString("lmdid", "")+"\n"+"Type: Inv"+"\n"+"Unit Price: 0"+"\n"+"Suggested Unit Price: 0"+"\n"+"LMD Hub: "+prefs.getString("lmdhub2","")+"\n"+"Date: "+tvDate1.getText().toString());

                                            //details2.setText(details2.getText()+"\n"+(tvWaybillNo.getText().toString())+"\n"+prefs.getString("productid", "")+"\n"+prefs.getString("product", "")+"\n"+etUnit.getText().toString()+"\n"+prefs.getString("lmdid2", "")+"\n"+prefs.getString("lmdid", "")+"\n"+"Inv"+"\n"+"0"+"\n"+"0"+"\n"+prefs.getString("lmdhub","")+"\n"+tvDate1.getText().toString());

                                            final AlertDialog ad = alertadd.show();
                                            submit.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {

                                                    submit.setEnabled(false);
                                                    InventoryTDBhandler db1 = new InventoryTDBhandler(getActivity());
                                                    if (!db1.onAdd(new Transaction(tvWaybillNo.getText().toString(), prefs.getString("productid", ""),
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
                                                    prefs.edit().remove("lmdname2").commit();
                                                    prefs.edit().remove("lmdid2").commit();
                                                    prefs.edit().remove("quantity").commit();
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


                        case "Delivery to Customer":
                            new AlertDialog.Builder(getActivity())
                                    .setTitle("Delivery to Customer")
                                    .setMessage("Are you sure you want to move " + etUnit.getText().toString() + " of " + tvProduct.getText().toString() + " from " + prefs.getString("lmdid", "") + " to " + prefs.getString("lmdid2", "") + "?")
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

                                            details.setText(details.getText()+""+"Waybill No: "+(tvWaybillNo.getText().toString())+"\n"+"Product ID: "+prefs.getString("productid", "")+"\n"+"Product: "+prefs.getString("product", "")+"\n"+"Unit: "+etUnit.getText().toString()+"\n"+"In: "+prefs.getString("lmdid2", "")+"\n"+"Out: "+prefs.getString("lmdid", "")+"\n"+"Type: Inv"+"\n"+"Unit Price: 0"+"\n"+"Suggested Unit Price: 0"+"\n"+"LMD Hub: "+prefs.getString("lmdhub2","")+"\n"+"Date: "+tvDate1.getText().toString());

                                            //details2.setText(details2.getText()+"\n"+(tvWaybillNo.getText().toString())+"\n"+prefs.getString("productid", "")+"\n"+prefs.getString("product", "")+"\n"+etUnit.getText().toString()+"\n"+prefs.getString("lmdid2", "")+"\n"+prefs.getString("lmdid", "")+"\n"+"Inv"+"\n"+"0"+"\n"+"0"+"\n"+prefs.getString("lmdhub","")+"\n"+tvDate1.getText().toString());

                                            final AlertDialog ad = alertadd.show();
                                            submit.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {

                                                    submit.setEnabled(false);
                                                    InventoryTDBhandler db1 = new InventoryTDBhandler(getActivity());
                                                    //Inv type version of inserting first.
                                                    if (!db1.onAdd(new Transaction(tvWaybillNo.getText().toString(), prefs.getString("productid", ""),
                                                            prefs.getString("product", ""), etUnit.getText().toString(), prefs.getString("lmdid2", ""),
                                                            prefs.getString("lmdid", ""), "Inv", "0", "0", prefs.getString("lmdhub2", ""), tvDate1.getText().toString()))) {
                                                        Toast.makeText(getActivity(), "Transaction already exists in the database", Toast.LENGTH_LONG).show();
                                                    }else{
                                                        //Sale type version of inserting next.
                                                        db1.onAdd(new Transaction(" ", prefs.getString("productid", ""),
                                                                prefs.getString("product", ""), etUnit.getText().toString(), "Sale",
                                                                prefs.getString("lmdid2", ""), "Sale", "0", "0", prefs.getString("lmdhub", ""), tvDate1.getText().toString()));
                                                    }           //Note to self: Since in this sale version, How does the Web know the out of the customer, Unless there's actually an Out for the customer.
                                                    //clears the product fragment
                                                    prefs.edit().remove("product").commit();
                                                    prefs.edit().remove("productid").commit();
                                                    prefs.edit().remove("waybill").commit();
                                                    prefs.edit().remove("date1").commit();
                                                    prefs.edit().remove("date2").commit();
                                                    prefs.edit().remove("lmdname2").commit();
                                                    prefs.edit().remove("lmdid2").commit();
                                                    prefs.edit().remove("quantity").commit();
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


                        case "Delivery to Team Member":
                            new AlertDialog.Builder(getActivity())
                                    .setTitle("Delivery to Team Member")
                                    .setMessage("Are you sure you want to move " + etUnit.getText().toString() + " of " + tvProduct.getText().toString() + " from " + prefs.getString("lmdid", "") + " to " + prefs.getString("lmdid2", "") + "?")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id) {

                                            final AlertDialog.Builder alertadd = new AlertDialog.Builder(getActivity());
                                            LayoutInflater factory = LayoutInflater.from(getActivity());
                                            alertadd.setCancelable(true);
                                            final View views = factory.inflate(R.layout.custom_layout, null);
                                            final TextView details = views.findViewById(R.id.display);
                                            final Button submit = views.findViewById(R.id.positive_button);
                                            alertadd.setView(views);

                                            final Button cancel = views.findViewById(R.id.negative_button);

                                            details.setText(details.getText()+""+"Waybill No: N/A \n"+"Product ID: "+prefs.getString("productid", "")+"\n"+"Product: "+prefs.getString("product", "")+"\n"+"Unit: "+etUnit.getText().toString()+"\n"+"In: "+prefs.getString("lmdid2", "")+"\n"+"Out: "+prefs.getString("lmdid", "")+"\n"+"Type: Inv"+"\n"+"Unit Price: 0"+"\n"+"Suggested Unit Price: 0"+"\n"+"LMD Hub: "+prefs.getString("lmdhub2","")+"\n"+"Date: "+tvDate1.getText().toString());

                                            final AlertDialog ad = alertadd.show();
                                            submit.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {

                                                    submit.setEnabled(false);
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
                                                        prefs.edit().remove("lmdname2").commit();
                                                        prefs.edit().remove("lmdid2").commit();
                                                        prefs.edit().remove("quantity").commit();

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
