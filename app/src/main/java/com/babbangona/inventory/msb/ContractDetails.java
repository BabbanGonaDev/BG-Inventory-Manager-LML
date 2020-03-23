package com.babbangona.inventory.msb;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class ContractDetails extends Fragment implements View.OnClickListener {

    SharedPreferences prefs;
    TextView tvDate1Contract;
    TextView tvDate2Contract;
    TextView tvLMDNameContract;
    TextView tvLMDIDContract;
    TextView tvProductContract;
    Button btnDate1ScanContract;
    Button btnDate2ScanContract;
    Button btnLMD2ScanContract;
    Button btnProdScanContract;
    EditText etContractFormNo;
    EditText etContractFormNo2;
    EditText etUnitContract;
    EditText etUnit2Contract;
    Button btnDeliverContract;
    Button btnHomeContract;
    Button btnCancelContract;

    public ContractDetails(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_contract_details, container, false);
        prefs = getActivity().getSharedPreferences("Preferences", Context.MODE_PRIVATE);

        tvProductContract = v.findViewById(R.id.tvProductContract);
        tvLMDIDContract = v.findViewById(R.id.tvLMDIDContract);
        tvLMDNameContract = v.findViewById(R.id.tvLMDNameContract);
        tvDate1Contract = v.findViewById(R.id.tvDate1Contract);
        tvDate2Contract = v.findViewById(R.id.tvDate2Contract);
        etContractFormNo = v.findViewById(R.id.etContractFormNo);
        etContractFormNo2 = v.findViewById(R.id.etContractFormNo2);
        etUnitContract = v.findViewById(R.id.etUnitContract);
        etUnit2Contract = v.findViewById(R.id.etUnit2Contract);

        tvProductContract.setText(prefs.getString("product", ""));
        tvDate1Contract.setText(prefs.getString("date1", ""));
        tvDate2Contract.setText(prefs.getString("date2", ""));
        tvLMDNameContract.setText(prefs.getString("lmdname2", ""));
        tvLMDIDContract.setText(prefs.getString("lmdid2", ""));

        btnDate1ScanContract = v.findViewById(R.id.btnDate1ScanContract);
        btnDate2ScanContract = v.findViewById(R.id.btnDate2ScanContract);
        btnLMD2ScanContract = v.findViewById(R.id.btnLMD2ScanContract);
        btnProdScanContract = v.findViewById(R.id.btnProdScanContract);
        btnDeliverContract = v.findViewById(R.id.btnDeliverContract);
        btnHomeContract = v.findViewById(R.id.btnHomeContract);
        btnCancelContract = v.findViewById(R.id.btnCancelContract);

        btnDate1ScanContract.setOnClickListener(this);
        btnDate2ScanContract.setOnClickListener(this);
        btnLMD2ScanContract.setOnClickListener(this);
        btnProdScanContract.setOnClickListener(this);
        btnDeliverContract.setOnClickListener(this);
        btnHomeContract.setOnClickListener(this);
        btnCancelContract.setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btnDate1ScanContract:
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
                            tvDate1Contract.setText(prefs.getString("date1",""));
                        }
                    }
                }, y,m,d);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
                break;
            case R.id.btnDate2ScanContract:
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
                            tvDate2Contract.setText(prefs.getString("date2",""));
                        }
                    }
                }, y2,m2,d2);
                dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog2.show();
                break;
            case R.id.btnLMD2ScanContract:
                prefs.edit().putString("scannerheading", "Scan Destination details")
                        .putString("datatobescanned", "LMD2").commit();
                startActivity(new Intent(getActivity(), LMDScan.class));
                break;
            case R.id.btnProdScanContract:
                prefs.edit().putString("scannerheading", "Scan Product details")
                        .putString("datatobescanned", "product").commit();
                startActivity(new Intent(getActivity(), LMDScan.class));
                break;
            case R.id.btnCancelContract:
                getFragmentManager().beginTransaction().remove(ContractDetails.this).commit();      //removes the fragment from view
                //clears the product fragment
                prefs.edit().remove("product").commit();
                prefs.edit().remove("productid").commit();
                prefs.edit().remove("waybill").commit();
                prefs.edit().remove("lmdname2").commit();
                prefs.edit().remove("lmdid2").commit();
                prefs.edit().remove("date1").commit();
                prefs.edit().remove("date2").commit();
                prefs.edit().putBoolean("loadfrag", false).commit();
                break;
            case R.id.btnHomeContract:
                //clears the preferences
                prefs.edit().clear().commit();
                Intent intent = new Intent(getActivity(), Operations.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;
            case R.id.btnDeliverContract:
                if(etUnit2Contract.getText().toString().equals(etUnitContract.getText().toString()) && !etUnitContract.getText().toString().equals("") && etContractFormNo2.getText().toString().equals(etContractFormNo.getText().toString()) &&
                        !etContractFormNo.getText().toString().equals("") && tvDate1Contract.getText().toString().equals(tvDate2Contract.getText()) && !tvDate2Contract.getText().toString().equals("") &&
                        !tvProductContract.getText().toString().equals("") && !tvLMDNameContract.getText().toString().equals("") && !tvLMDIDContract.getText().toString().equals("")){

                    new AlertDialog.Builder(getActivity())
                            .setTitle("Delivery to Warehouse")
                            .setMessage("Are you sure you want to move " + etUnit2Contract.getText().toString() + " of " + tvProductContract.getText().toString() + " to " + tvLMDNameContract.getText().toString() + " ?")
                            .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    final AlertDialog.Builder alertadd = new AlertDialog.Builder(getActivity());
                                    LayoutInflater factory = LayoutInflater.from(getActivity());
                                    alertadd.setCancelable(true);
                                    final View views = factory.inflate(R.layout.custom_layout, null);
                                    final TextView details = views.findViewById(R.id.display);
                                    //final TextView details2 = views.findViewById(R.id.display2);
                                    final Button submit = views.findViewById(R.id.positive_button);
                                    alertadd.setView(views);

                                    final Button cancel = views.findViewById(R.id.negative_button);


                                    details.setText(details.getText()+""+"Waybill No: "+ etContractFormNo.getText().toString() +"\n"+"Product ID: "+prefs.getString("productid", "")+"\n"+"Product: "+prefs.getString("product", "")+"\n"+"Unit: "+etUnitContract.getText().toString()+"\n"+"In: "+prefs.getString("lmdid2", "")+"\n"+"Out: "+prefs.getString("lmdid", "")+"\n"+"Type: Inv"+"\n"+"LMD Hub: "+prefs.getString("lmdhub2","")+"\n"+"Date: "+tvDate1Contract.getText().toString());

                                    final AlertDialog ad = alertadd.show();
                                    submit.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Log.d("Location", "We are inside the Submit click listener");
                                            InventoryTDBhandler db1 = new InventoryTDBhandler(getActivity());
                                            if(!db1.onAdd(new Transaction(etContractFormNo.getText().toString(), prefs.getString("productid", ""),
                                                    prefs.getString("product", ""), etUnit2Contract.getText().toString(), prefs.getString("lmdid2", ""),
                                                    prefs.getString("lmdid", ""), "Inv", "0", "0", prefs.getString("lmdhub2", ""), tvDate1Contract.getText().toString()))){
                                                Toast.makeText(getActivity(), "Transaction already exists in the database", Toast.LENGTH_LONG).show();
                                            }
                                            //clears the product fragment
                                            prefs.edit().remove("product").commit();
                                            prefs.edit().remove("productid").commit();
                                            //prefs.edit().remove("waybill").commit(); //There's no waybill ID Scanned at this place.
                                            prefs.edit().remove("date1").commit();
                                            prefs.edit().remove("date2").commit();
                                            //prefs.edit().remove("lmdname2").commit();     //so that the lmd details don't need to be scanned again
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

                }else{
                    Toast.makeText(getActivity(), "Kindly Ensure all the input fields are field", Toast.LENGTH_LONG).show();
                }
            break;
        }

    }
}
