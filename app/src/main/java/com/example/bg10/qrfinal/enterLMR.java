package com.example.bg10.qrfinal;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

public class enterLMR extends AppCompatActivity {
    SharedPreferences prefs;
    SharedPreferences.Editor prefsEdit;
    SmartUpdateAccess smartUpdateAccess;
    List<String> LMRList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_lmr);
        prefs = getSharedPreferences("Preferences", MODE_PRIVATE);
        prefsEdit = prefs.edit();
        smartUpdateAccess = SmartUpdateAccess.getInstance(this);

        Button send = findViewById(R.id.send);
        final AutoCompleteTextView atvLMRID = findViewById(R.id.atvLMR);
        final EditText etLMRName = findViewById(R.id.etLMRName);
        final EditText etLMRHub = findViewById(R.id.etLMRHub);

        smartUpdateAccess.open();
        //LMRList = null;
        LMRList = smartUpdateAccess.getLMRID();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(enterLMR.this, android.R.layout.simple_list_item_1, LMRList);
        atvLMRID.setAdapter(adapter);

        atvLMRID.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                final AutoCompleteTextView atvLMRID = findViewById(R.id.atvLMR);
                final EditText etLMRName = findViewById(R.id.etLMRName);
                final EditText etLMRHub = findViewById(R.id.etLMRHub);

                smartUpdateAccess.open();
                etLMRName.setText(smartUpdateAccess.autoLMRName(s));
                etLMRHub.setText(smartUpdateAccess.autoLMRHub(s));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (prefs.getString("fragment", "")) {
                    case "Stocking2":
                    switch (prefs.getString("transtype", "")) {
                        case "Delivery to LMR":

                                    prefsEdit.putString("lmdname2", etLMRName.getText().toString());
                                    prefsEdit.putString("lmdid2", atvLMRID.getText().toString());
                                    prefsEdit.putString("lmdhub2", etLMRHub.getText().toString());
                                    prefsEdit.commit();
                                    if ((atvLMRID.getText().toString().charAt(0) != 'R') || (atvLMRID.getText().toString().charAt(16) != '7')) {
                                        Toast.makeText(getApplicationContext(), "Please enter an LMR's details to proceed", Toast.LENGTH_LONG).show();
                                        finish();
                                    }
                                    new AlertDialog.Builder(enterLMR.this)
                                            .setTitle("Delivery to LMR")
                                            .setMessage("Are you sure you want to begin transfer to LMR " + etLMRName.getText().toString() + "?")
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int id) {
                                                    prefsEdit.putBoolean("loadfrag", true).commit();
                                                    startActivity(new Intent(enterLMR.this, Transactions.class)
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
                            break;


                        case "Pickup from LMR":
                            /*switch (prefs.getString("datatobescanned", "")) {
                                case "LMD":*/
                                    try {
                                        prefsEdit.putString("lmdname", etLMRName.getText().toString());
                                        prefsEdit.putString("lmdid", atvLMRID.getText().toString());
                                        prefsEdit.putString("lmdhub", etLMRHub.getText().toString());
                                        prefsEdit.commit();
                                        if ((atvLMRID.getText().toString().charAt(0) != 'R') || (atvLMRID.getText().toString().charAt(16) != '7')) {
                                            Toast.makeText(getApplicationContext(), "Please scan an LMR's details to proceed", Toast.LENGTH_LONG).show();
                                            finish();
                                        }
                                        new AlertDialog.Builder(enterLMR.this)
                                                .setTitle("Pickup from LMR")
                                                .setMessage("Are you sure you want to begin pickup from " + etLMRName.getText().toString() + "?")
                                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        startActivity(new Intent(enterLMR.this, Transactions.class)
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
                                    /*}
                                    break;*/
                            }
                        break;
                    }

                    case "LMRPayments":
                        switch(prefs.getString("transtype", "")) {
                            case "LMRPayments":
                                /*switch(prefs.getString("datatobescanned", "")) {
                                    case "LMD":*/
                                        try {
                                            prefsEdit.putString("lmdname", etLMRName.getText().toString());
                                            prefsEdit.putString("lmdid", atvLMRID.getText().toString());
                                            prefsEdit.putString("lmdhub", etLMRHub.getText().toString());
                                            prefsEdit.commit();
                                            new AlertDialog.Builder(enterLMR.this)
                                                    .setTitle("Begin scanning of LMR")
                                                    .setMessage("Are you sure you want to collect payment from " + etLMRName.getText().toString())
                                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            startActivity(new Intent(enterLMR.this, Transactions.class)
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
                                            Toast.makeText(getApplicationContext(), "Please scan the appropriate LMR QR code to proceed", Toast.LENGTH_LONG).show();
                                            finish();
                                        } catch (StringIndexOutOfBoundsException e) {
                                            Toast.makeText(getApplicationContext(), "Please scan an the appropriate LMR QR code to proceed", Toast.LENGTH_LONG).show();
                                            finish();
                                       /* }
                                        break;*/
                                }

                        }
                }

            }
        });
    }
}
