package com.babbangona.inventory.msb;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class enterSupplier extends AppCompatActivity {
    SharedPreferences prefs;
    SharedPreferences.Editor prefsEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_supplier);
        prefs = getSharedPreferences("Preferences", MODE_PRIVATE);
        prefsEdit = prefs.edit();

        Button btnSupplierDetails = findViewById(R.id.btnSupplierDetails);
        final EditText etSupplierID = findViewById(R.id.etSupplierID);
        final EditText etSupplierName = findViewById(R.id.etSupplierName);

        btnSupplierDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            try{
                    if(etSupplierID.getText().toString().length() == 18 && etSupplierID.getText().charAt(0) == 'V') {
                            prefsEdit.putString("supplier name", etSupplierName.getText().toString());
                            prefsEdit.putString("supplier ID", etSupplierID.getText().toString());
                            prefsEdit.commit();
                            prefsEdit.putBoolean("loadfrag", true).commit();
                            startActivity(new Intent(enterSupplier.this, Transactions.class)
                                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                            }else{
                            Toast.makeText(getApplicationContext(), "Please enter the appropriate Supplier's QR code to proceed", Toast.LENGTH_LONG).show();
                            }
                    } catch (ArrayIndexOutOfBoundsException exception) {
                        Toast.makeText(getApplicationContext(), "Please enter the appropriate Supplier's QR code to proceed", Toast.LENGTH_LONG).show();
                        finish();
                    } catch (StringIndexOutOfBoundsException e) {
                        Toast.makeText(getApplicationContext(), "Please enter the appropriate Supplier's QR code to proceed", Toast.LENGTH_LONG).show();
                        finish();
                    }
                }
        });


    }
}
