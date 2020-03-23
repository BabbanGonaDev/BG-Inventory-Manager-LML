package com.babbangona.inventory.msb;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class SmartUpdateAccess {

    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase database;
    private static SmartUpdateAccess instance;


    public SmartUpdateAccess(Context context){
        this.openHelper = new SmartUpdateHelper(context);
    }

    public static SmartUpdateAccess getInstance(Context context){
        if(instance == null){
            instance = new SmartUpdateAccess(context);
        }
        return instance;
    }

    public void open(){
        this.database = openHelper.getWritableDatabase();
    }

    public void close(){
        if(database != null){
            this.database.close();
        }
    }

    public boolean checkLMRStatus(String product){
        try {
            String reply = "";
            String LMRQuery = "SELECT LMRStatus FROM smartProduct WHERE itemID = '" + product + "'";
            Cursor cursor = database.rawQuery(LMRQuery, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                reply = cursor.getString(cursor.getColumnIndex("LMRStatus"));
                cursor.moveToNext();
            }
            cursor.close();

            if (reply == "yes") {
                return true;
            }
            return false;
        }catch(SQLiteException e){
            e.printStackTrace();
        }
        return false;
    }

    public Integer CheckStockingQty(String product){
        try{
            Integer value = 0;
            String StockingQuery = "SELECT stockingQty FROM smartProduct WHERE itemID = '" + product + "'";
            Cursor cursor = database.rawQuery(StockingQuery, null);
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                value = cursor.getInt(cursor.getColumnIndex("stockingQty"));
                cursor.moveToNext();
            }
            cursor.close();
            if(value == 0){
                return 1;
            }
            return value;
            }catch(NullPointerException e){
            return  1;
        }

    }

    public Integer CheckKnownQty(String product){
        try{
            Integer qty = 0;
            String KnownQuery = "SELECT expectedUnits FROM smartProduct WHERE itemID = '" + product + "'";
            Cursor cursor = database.rawQuery(KnownQuery, null);
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                qty = cursor.getInt(cursor.getColumnIndex("expectedUnits"));
                cursor.moveToNext();
            }
            cursor.close();
            if(qty == 0){
                return 1;
            }
            return qty;
        }catch(NullPointerException e){
            return 1;
        }

    }

    public Integer CheckUpperLimit(String product){
        try{
            Integer limit = 0;
            String LimitQuery = "SELECT upperLimit FROM smartProduct WHERE itemID = '" + product + "'";
            Cursor cursor = database.rawQuery(LimitQuery, null);
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                limit = cursor.getInt(cursor.getColumnIndex("upperLimit"));
                cursor.moveToNext();
            }
            cursor.close();
            if(limit == 0){
                return 1;
            }
            return limit;
        }catch(NullPointerException e){
            return 1;

        }

    }

    public Integer CheckInvoiceQty(String product){
        try{
            Integer invoice = 0;
            String InvoiceQuery = "SELECT invoiceQty FROM smartProduct WHERE itemID = '" + product + "'";
            Cursor cursor = database.rawQuery(InvoiceQuery, null);
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                invoice = cursor.getInt(cursor.getColumnIndex("invoiceQty"));
                cursor.moveToNext();
            }
            cursor.close();
            if(invoice == 0){
                return 1;
            }
            return invoice;
        }catch(NullPointerException e){
            return 1;
        }
    }

    public String CheckSupplier(String product){
        try{
            String supplier = "";
            String supplierQuery = "SELECT supplierA FROM smartProduct WHERE itemID = '" + product + "'";
            Cursor cursor = database.rawQuery(supplierQuery, null);
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                supplier = cursor.getString(cursor.getColumnIndex("supplierA"));
                cursor.moveToNext();
            }
            cursor.close();
            return supplier;
        }catch(NullPointerException e){
            return "";
        }

    }

    public Integer CheckCartonNo(String product){
        try{
            Integer cartonNo = 0;
            String CartonQuery = "SELECT unitsPerCarton FROM smartProduct WHERE itemID = '" + product + "'";
            Log.d("TAG", CartonQuery);
            Cursor cursor = database.rawQuery(CartonQuery, null);
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                cartonNo = cursor.getInt(cursor.getColumnIndex("unitsPerCarton"));
                Log.d("TAG", String.valueOf(cartonNo));
                cursor.moveToNext();
            }
            cursor.close();
            if(cartonNo == 0){
                return 1;
            }
            return cartonNo;
        }catch(NullPointerException e){
            return 1;
        }
    }

    public List<String> getLMRID(){
        List<String> list = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT LMRID FROM smartLMR", null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            list.add(cursor.getString(0));
            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }

    public String autoLMRName (CharSequence data){
        String value = "";
        String empty = "";

        String LMRNameQuery = "SELECT LMRName FROM smartLMR WHERE LMRID = '" + data + "'";
        Cursor cursor = database.rawQuery(LMRNameQuery, null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            value = cursor.getString(cursor.getColumnIndex("LMRName"));
            cursor.moveToNext();
        }
        cursor.close();
        int LMRNameCount = cursor.getCount();
        if(LMRNameCount > 0){
            return value;
        }
        return empty;
    }

    public String autoLMRHub (CharSequence data){
        String value = "";
        String empty = "";

        String LMRNameQuery = "SELECT LMRHub FROM smartLMR WHERE LMRID = '" + data + "'";
        Cursor cursor = database.rawQuery(LMRNameQuery, null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            value = cursor.getString(cursor.getColumnIndex("LMRHub"));
            cursor.moveToNext();
        }
        cursor.close();
        int LMRHubCount = cursor.getCount();
        if(LMRHubCount > 0){
            return value;
        }
        return empty;
    }

    public void updateSmartUpdate(String product_id, String unitsPerCarton, String expectedUnits, String LMRStatus){
        try {
            String updateQuery = "UPDATE smartProduct SET unitsPerCarton = '" + unitsPerCarton + "', expectedUnits = '" + expectedUnits + "', LMRStatus = '" + LMRStatus + "' WHERE itemID = '" + product_id + "'";
            Log.d("Query", updateQuery);
            database.execSQL(updateQuery);
        }catch(NullPointerException e){
            e.printStackTrace();
        }

    }


}
