package com.babbangona.inventory.msb;

import android.content.ComponentName;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button accessControl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        accessControl = findViewById(R.id.accesscontrol);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setComponent(new ComponentName("com.babbangona.accesscontrol","com.babbangona.accesscontrol.MainActivity"));
        startActivity(intent);


    }
}
