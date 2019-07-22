package com.example.bg10.qrfinal;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class SmartUpdateHelper extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "smartUpdate.db";
    private static final int DATABASE_VERSION = 1;


    public SmartUpdateHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        super.onUpgrade(db, oldVersion, newVersion);
    }
}
