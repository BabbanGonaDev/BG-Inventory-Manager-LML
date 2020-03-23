package com.babbangona.inventory.msb;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.sql.Date;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class InventoryTDBhandler extends SQLiteOpenHelper {
    private static final String database = "InventoryT.db";
    private static final String table = "InventoryT";
    private static final String uniqueID = "uniqueID";
    private static final String timestamp = "timestamp";
    private static final String idonline = "idonline";
    private static final String id = "id";
    private static final String itemid = "ItemID";
    private static final String itemName = "ItemName";
    private static final String unit = "Unit";
    private static final String in = "Inn";
    private static final String out = "Out";
    private static final String enteredDate = "enteredDate";
    private static final String date = "Date";
    private static final String type = "Type";
    private static final String unitPrice = "UnitPrice";
    private static final String suggUnitPrice = "SuggestedUnitPrice";
    private static final String hub = "Hub";
    private static final String sync = "sync";

    SharedPreferences prefs;    //for Zxing scanning variables
    SharedPreferences prefs1;   //for users details
    SharedPreferences prefs2;   //start date and end date
    Context context;
    String user;
    String version;
    String staffid;
    String todaysDate =  new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new java.util.Date());
    Long seconds, timeDifference;
    String oldId, oldItemId, oldUnit, oldIn, oldOut, oldDate;
    String unique_id = "";

    public InventoryTDBhandler(Context context) {
        super(context, database, null, 1);
        this.context = context;
        prefs1 = context.getSharedPreferences("Preferences1", Context.MODE_PRIVATE);
        prefs2 = context.getSharedPreferences("Preferences2", Context.MODE_PRIVATE);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //create InventoryT table
        String create = "create table " + table + "(" + "idlocal integer primary key autoincrement not null, "
                + timestamp + " text, "
                + uniqueID + " text unique, "
                + idonline + " text, "
                + id + " text, "
                + itemid + " text, "
                + itemName + " text, "
                + unit + " text, "
                + in + " text, "
                + out + " text, "
                + enteredDate + " text, "
                + date + " text, "
                + type + " text, "
                + unitPrice + " text, "
                + suggUnitPrice + " text, "
                + hub + " text, "
                + sync + " text);";

        sqLiteDatabase.execSQL(create);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists " + table);
        sqLiteDatabase.execSQL("drop table if exists Inventory02T");
        onCreate(sqLiteDatabase);
    }

    public boolean transExists(String id, String Inn, String Out)
    {
        SQLiteDatabase db = getWritableDatabase();
        String all = "select * from " + table + " where id = \"" + id + "\" and Inn = \"" + Inn + "\" and Out = \"" + Out + "\""; //Edit 10/08/18 By Rehoboth, to allow a waybill number to be used for the full movement
        Cursor c = db.rawQuery(all,null);
        c.moveToFirst();

        if(c.getCount() < 1){ return false;}

        c.close();
        db.close();
        return true;
    }

    public boolean isWaybillTwice(String id)
    {
        SQLiteDatabase db = getWritableDatabase();
        String all = "select * from " + table + " where id = '" + id + "' and id != '0'";   //edit 23/07/2018: added exception for the default 0 case
        Cursor c = db.rawQuery(all,null);
        c.moveToFirst();

        if(c.getCount() < 2){ return false;}

        c.close();
        db.close();
        return true;
    }

    public boolean duplicateTime(String passedId, String passedItemid, String passedUnit, String passedIn, String passedOut, String passedDate){

        try {
            SQLiteDatabase db = getWritableDatabase();
            String former = "SELECT * FROM " + table + " ORDER BY " + date + " DESC LIMIT 1";
            Cursor c = db.rawQuery(former, null);
            Log.i("query", former);
            c.moveToFirst();

            if(c.getCount() < 1){
                Log.i("HeroAgain", "We returned false because of cursor count.");
                return false;
            }

            Log.i("HERO", "We checked the duplicate");
            do {
                oldId = c.getString(c.getColumnIndex(id));
                oldItemId = c.getString(c.getColumnIndex(itemid));
                oldUnit = c.getString(c.getColumnIndex(unit));
                oldIn = c.getString(c.getColumnIndex(in));
                oldOut = c.getString(c.getColumnIndex(out));
                oldDate = c.getString(c.getColumnIndex(date));
            }while(c.moveToNext());

            Log.i("HeroAfter", "After the cursor movement");
            c.close();
            db.close();

            //Calculate time difference and pass false is the difference is small.
            Log.i("HERO2", "We've started the Try catch in dateDifference Big");
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());

            java.util.Date dateOld, dateCurrent;
            dateOld = format.parse(oldDate);
            dateCurrent = format.parse(passedDate);

            timeDifference = dateCurrent.getTime() - dateOld.getTime();
            seconds = timeDifference/1000;

            Log.i("HERO3", String.valueOf(seconds));
            Log.d("REHO","OLD: "+oldId+" "+oldItemId+" "+oldUnit+" "+oldIn+" "+oldOut);
            Log.d("REHO","NEW: "+passedId+" "+passedItemid+" "+passedUnit+" "+passedIn+" "+passedOut);
            if((oldId.trim().equals(passedId)) && (oldItemId.equals(passedItemid)) && (oldUnit.equals(passedUnit)) && (oldIn.equals(passedIn)) && (oldOut.equals(passedOut)) && (seconds < 300)){
                Log.i("HERO4", "The values have been entered before.");

                return true;
            }else{
                return false;
            }


        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean onAdd(Transaction trans)
    {
        if(!transExists(trans.getId(), trans.getIn(), trans.getOut()))
        {
            if(!isWaybillTwice(trans.getId())){
            String dateToStr = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss ", Locale.getDefault()).format(new java.util.Date());
            try {
                if((trans.getIn().length() != 16) || (trans.getOut().length() != 16)){
                    if ((trans.getIn().charAt(16) == '9') || (trans.getOut().charAt(16) == '9')) {
                        Log.d("CHECK", "Its an LMD Transaction");
                        unique_id = prefs1.getString("staff_id", "") + "_" + new Date(System.currentTimeMillis()).getTime() + "_LML";
                    } else {
                        Log.d("CHECK", "Its an INV Transaction");
                        unique_id = prefs1.getString("staff_id", "") + "_" + new Date(System.currentTimeMillis()).getTime() + "_INV";
                    }
                }else{
                    unique_id = prefs1.getString("staff_id", "") + "_" + new Date(System.currentTimeMillis()).getTime() + "_INV";
                }

            }catch(Exception e){
                e.printStackTrace();
                unique_id = prefs1.getString("staff_id", "") + "_" + new Date(System.currentTimeMillis()).getTime() + "_INV";

            }

            if(!duplicateTime(trans.getId(), trans.getItemid(), trans.getUnit(), trans.getIn(), trans.getOut(), dateToStr)){

            SQLiteDatabase db = getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(timestamp, new Time(System.currentTimeMillis()).toString());
            values.put(uniqueID, unique_id);
            values.put(id, trans.getId());
            values.put(itemid, trans.getItemid());
            values.put(itemName, trans.getItemName());
            values.put(unit, trans.getUnit());
            values.put(in, trans.getIn());
            values.put(out, trans.getOut());
            values.put(enteredDate, trans.getEnteredDate());
            values.put(date, dateToStr);
            values.put(type, trans.getType());
            values.put(unitPrice, trans.getUnitPrice());
            values.put(suggUnitPrice, trans.getSuggUnitPrice());
            values.put(hub, trans.getHub());
            values.put(sync, trans.getSync());

            db.insert(table, null, values);
            db.close();

            try {
                //Insert into the LMTC app if its an LMD transaction.
                if (trans.getIn().charAt(16) == '9') {
                    //Insert as a +
                    //This is a delivery into the LMD's shop.
                    try {
                        Log.d("CHECK", "Delivery to LMD shop");

                        String URL = "content://com.bgenterprise.bglmtcinventory/Inventory03T";
                        Uri inventory = Uri.parse(URL);
                        ContentValues contentV = new ContentValues();

                        contentV.put("ItemID", trans.getItemid());
                        contentV.put("ItemName", trans.getItemName());
                        contentV.put("LMDID", trans.getIn());
                        contentV.put("Notes", "");
                        contentV.put("Staff_ID", prefs1.getString("staff_id", ""));
                        contentV.put("SyncDate", dateToStr);
                        contentV.put("SyncStatus", "yes");
                        contentV.put("TxnDate", trans.getEnteredDate());
                        contentV.put("Type", "LML");
                        contentV.put("UniqueID", unique_id);
                        contentV.put("Unit", trans.getUnit());
                        contentV.put("UnitPrice", trans.getUnitPrice());

                        Uri x = context.getContentResolver().insert(inventory, contentV);
                        long result = ContentUris.parseId(x);
                        Log.d("RESPONSE", result + "");
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(context, "Unable to insert into LMTC", Toast.LENGTH_LONG).show();
                    }


                } else if (trans.getOut().charAt(16) == '9') {
                    //Insert as a -
                    //This is a take-away from the LMD's shop.
                    try {
                        Log.d("CHECK", "Collection from LMD shop");


                        String URL = "content://com.bgenterprise.bglmtcinventory/Inventory03T";
                        Uri inventory = Uri.parse(URL);
                        ContentValues contentV = new ContentValues();

                        contentV.put("ItemID", trans.getItemid());
                        contentV.put("ItemName", trans.getItemName());
                        contentV.put("LMDID", trans.getIn());
                        contentV.put("Notes", "");
                        contentV.put("Staff_ID", prefs1.getString("staff_id", ""));
                        contentV.put("SyncDate", dateToStr);
                        contentV.put("SyncStatus", "yes");
                        contentV.put("TxnDate", trans.getEnteredDate());
                        contentV.put("Type", "LML");
                        contentV.put("UniqueID", unique_id);
                        contentV.put("Unit", "-" + trans.getUnit());
                        contentV.put("UnitPrice", trans.getUnitPrice());

                        Uri x = context.getContentResolver().insert(inventory, contentV);
                        long result = ContentUris.parseId(x);
                        Log.d("RESPONSE", result + "");
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(context, "Unable to insert into LMTC", Toast.LENGTH_LONG).show();
                    }

                }
            }catch(Exception e){
                e.printStackTrace();
            }
            return true;

                }
           }
            return false;
        }
        return false;
    }




    //display functions
    //
    //
    public ArrayList<Transaction> displayTransactions()
    {
        SQLiteDatabase db = getWritableDatabase();
        String all = "select * from " + table + " order by " + enteredDate + " desc";

        /*String all = "select * from " + table + " " +
                "where Date >= + '" + prefs2.getString("startdate", "2018-04-01")+ "' " +
                "and Date <= '" + prefs2.getString("enddate", "") + "' order by timestamp";*/
        Cursor c = db.rawQuery(all,null);
        c.moveToFirst();

        ArrayList<Transaction> trans = new ArrayList<>();

        if(c.getCount() < 1){ return trans;} //makes sure a null cursor is not passed to the add log function below

        do
        {
            trans.add(new Transaction(c.getString(c.getColumnIndex(id)), c.getString(c.getColumnIndex(itemid)),
                    c.getString(c.getColumnIndex(itemName)), c.getString(c.getColumnIndex(unit)), c.getString(c.getColumnIndex(in)),
                    c.getString(c.getColumnIndex(out)), c.getString(c.getColumnIndex(type)), c.getString(c.getColumnIndex(unitPrice)),
                    c.getString(c.getColumnIndex(suggUnitPrice)),c.getString(c.getColumnIndex(hub)), c.getString(c.getColumnIndex(enteredDate)), c.getString(c.getColumnIndex(date)), c.getString(c.getColumnIndex(sync))));
        }while(c.moveToNext());

        c.close();
        db.close();


        return trans;
    }

    public ArrayList<Transaction> displaySupplierTransactions()
    {
        SQLiteDatabase db = getWritableDatabase();
        prefs = context.getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        //show all deliveries to this warehouse that happened today
        String all = "select * from " + table + " where Inn = '"+ prefs.getString("lmdid","") +"' and Out = 'GIT-In' and Date ='" + new Date(System.currentTimeMillis()).toString() + "' order by timestamp";
        Cursor c = db.rawQuery(all,null);
        c.moveToFirst();

        ArrayList<Transaction> trans = new ArrayList<>();

        if(c.getCount() < 1){ return trans;} //makes sure a null cursor is not passed to the add log function below

        do
        {
            trans.add(new Transaction(c.getString(c.getColumnIndex(id)), c.getString(c.getColumnIndex(itemid)),
                    c.getString(c.getColumnIndex(itemName)), c.getString(c.getColumnIndex(unit)), c.getString(c.getColumnIndex(in)),
                    c.getString(c.getColumnIndex(out)), c.getString(c.getColumnIndex(type)), c.getString(c.getColumnIndex(unitPrice)),
                    c.getString(c.getColumnIndex(suggUnitPrice)),c.getString(c.getColumnIndex(hub)), c.getString(c.getColumnIndex(enteredDate)), c.getString(c.getColumnIndex(date)), c.getString(c.getColumnIndex(sync))));
        }while(c.moveToNext());

        c.close();
        db.close();

        return trans;
    }

    public ArrayList<Transaction> displayAllSupplierTransactions()
    {
        SQLiteDatabase db = getWritableDatabase();
        //show all deliveries to this warehouse that happened today
        String all = "select * from " + table + " where Out = 'GIT-In' and Inn like 'N%' " +
                "and Date >= + '" + prefs2.getString("startdate", todaysDate)+ "' " +
                "and Date <= '" + prefs2.getString("enddate", todaysDate) + "' order by timestamp";
        Cursor c = db.rawQuery(all,null);
        c.moveToFirst();

        ArrayList<Transaction> trans = new ArrayList<>();

        if(c.getCount() < 1){ return trans;} //makes sure a null cursor is not passed to the add log function below

        do
        {
            trans.add(new Transaction(c.getString(c.getColumnIndex(id)), c.getString(c.getColumnIndex(itemid)),
                    c.getString(c.getColumnIndex(itemName)), c.getString(c.getColumnIndex(unit)), c.getString(c.getColumnIndex(in)),
                    c.getString(c.getColumnIndex(out)), c.getString(c.getColumnIndex(type)), c.getString(c.getColumnIndex(unitPrice)),
                    c.getString(c.getColumnIndex(suggUnitPrice)),c.getString(c.getColumnIndex(hub)), c.getString(c.getColumnIndex(enteredDate)), c.getString(c.getColumnIndex(date)), c.getString(c.getColumnIndex(sync))));
        }while(c.moveToNext());

        c.close();
        db.close();

        return trans;
    }

    public ArrayList<Transaction> displayITInTransactions()
    {
        SQLiteDatabase db = getWritableDatabase();
        prefs = context.getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        //show all ITIns made by this truck that happened today
        String all = "select * from " + table + " where Out = '"+ prefs.getString("lmdid","") +"' and Inn like 'N%' and Date ='" + new Date(System.currentTimeMillis()).toString() + "' order by timestamp";
        Cursor c = db.rawQuery(all,null);
        c.moveToFirst();

        ArrayList<Transaction> trans = new ArrayList<>();

        if(c.getCount() < 1){ return trans;} //makes sure a null cursor is not passed to the add log function below

        do
        {
            trans.add(new Transaction(c.getString(c.getColumnIndex(id)), c.getString(c.getColumnIndex(itemid)),
                    c.getString(c.getColumnIndex(itemName)), c.getString(c.getColumnIndex(unit)), c.getString(c.getColumnIndex(in)),
                    c.getString(c.getColumnIndex(out)), c.getString(c.getColumnIndex(type)), c.getString(c.getColumnIndex(unitPrice)),
                    c.getString(c.getColumnIndex(suggUnitPrice)),c.getString(c.getColumnIndex(hub)), c.getString(c.getColumnIndex(enteredDate)), c.getString(c.getColumnIndex(date)), c.getString(c.getColumnIndex(sync))));
        }while(c.moveToNext());

        c.close();
        db.close();

        return trans;
    }

    public ArrayList<Transaction> displayAllITInTransactions()
    {
        SQLiteDatabase db = getWritableDatabase();
        prefs = context.getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        //show all ITIns made by this truck that happened today
        String all = "select * from " + table + " where Out like 'T%' and Inn like 'N%' "+
                "and Date >= + '" + prefs2.getString("startdate", todaysDate)+ "' " +
                "and Date <= '" + prefs2.getString("enddate", todaysDate) + "' order by timestamp";
        Cursor c = db.rawQuery(all,null);
        c.moveToFirst();

        ArrayList<Transaction> trans = new ArrayList<>();

        if(c.getCount() < 1){ return trans;} //makes sure a null cursor is not passed to the add log function below

        do
        {
            trans.add(new Transaction(c.getString(c.getColumnIndex(id)), c.getString(c.getColumnIndex(itemid)),
                    c.getString(c.getColumnIndex(itemName)), c.getString(c.getColumnIndex(unit)), c.getString(c.getColumnIndex(in)),
                    c.getString(c.getColumnIndex(out)), c.getString(c.getColumnIndex(type)), c.getString(c.getColumnIndex(unitPrice)),
                    c.getString(c.getColumnIndex(suggUnitPrice)),c.getString(c.getColumnIndex(hub)), c.getString(c.getColumnIndex(enteredDate)), c.getString(c.getColumnIndex(date)), c.getString(c.getColumnIndex(sync))));
        }while(c.moveToNext());

        c.close();
        db.close();

        return trans;
    }
    public ArrayList<Transaction> displayITOutTransactions()
    {
        SQLiteDatabase db = getWritableDatabase();
        prefs = context.getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        String all = "select * from " + table + " where Out = '"+ prefs.getString("lmdid","") +"' and Inn like 'T%' and Date ='" + new Date(System.currentTimeMillis()).toString() + "' order by timestamp";
        Cursor c = db.rawQuery(all,null);
        c.moveToFirst();

        ArrayList<Transaction> trans = new ArrayList<>();

        if(c.getCount() < 1){ return trans;} //makes sure a null cursor is not passed to the add log function below

        do
        {
            trans.add(new Transaction(c.getString(c.getColumnIndex(id)), c.getString(c.getColumnIndex(itemid)),
                    c.getString(c.getColumnIndex(itemName)), c.getString(c.getColumnIndex(unit)), c.getString(c.getColumnIndex(in)),
                    c.getString(c.getColumnIndex(out)), c.getString(c.getColumnIndex(type)), c.getString(c.getColumnIndex(unitPrice)),
                    c.getString(c.getColumnIndex(suggUnitPrice)),c.getString(c.getColumnIndex(hub)), c.getString(c.getColumnIndex(enteredDate)), c.getString(c.getColumnIndex(date)), c.getString(c.getColumnIndex(sync))));
        }while(c.moveToNext());

        c.close();
        db.close();

        return trans;
    }

    public ArrayList<Transaction> displayAllITOutTransactions()
    {
        SQLiteDatabase db = getWritableDatabase();
        prefs = context.getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        String all = "select * from " + table + " where Out like 'N%' and Inn like 'T%' "+
                "and Date >= + '" + prefs2.getString("startdate", todaysDate)+ "' " +
                "and Date <= '" + prefs2.getString("enddate", todaysDate) + "' order by timestamp";
        Cursor c = db.rawQuery(all,null);
        c.moveToFirst();

        ArrayList<Transaction> trans = new ArrayList<>();

        if(c.getCount() < 1){ return trans;} //makes sure a null cursor is not passed to the add log function below

        do
        {
            trans.add(new Transaction(c.getString(c.getColumnIndex(id)), c.getString(c.getColumnIndex(itemid)),
                    c.getString(c.getColumnIndex(itemName)), c.getString(c.getColumnIndex(unit)), c.getString(c.getColumnIndex(in)),
                    c.getString(c.getColumnIndex(out)), c.getString(c.getColumnIndex(type)), c.getString(c.getColumnIndex(unitPrice)),
                    c.getString(c.getColumnIndex(suggUnitPrice)),c.getString(c.getColumnIndex(hub)), c.getString(c.getColumnIndex(enteredDate)), c.getString(c.getColumnIndex(date)), c.getString(c.getColumnIndex(sync))));
        }while(c.moveToNext());

        c.close();
        db.close();

        return trans;
    }
    public ArrayList<Transaction> displayLMRPickupTransactions()
    {
        SQLiteDatabase db = getWritableDatabase();
        prefs = context.getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        String all = "select * from " + table + " where Out = '"+ prefs.getString("lmdid","") +"' and Date ='" + new Date(System.currentTimeMillis()).toString() + "' order by timestamp";
        Cursor c = db.rawQuery(all,null);
        c.moveToFirst();

        ArrayList<Transaction> trans = new ArrayList<>();

        if(c.getCount() < 1){ return trans;} //makes sure a null cursor is not passed to the add log function below

        do
        {
            trans.add(new Transaction(c.getString(c.getColumnIndex(id)), c.getString(c.getColumnIndex(itemid)),
                    c.getString(c.getColumnIndex(itemName)), c.getString(c.getColumnIndex(unit)), c.getString(c.getColumnIndex(in)),
                    c.getString(c.getColumnIndex(out)), c.getString(c.getColumnIndex(type)), c.getString(c.getColumnIndex(unitPrice)),
                    c.getString(c.getColumnIndex(suggUnitPrice)),c.getString(c.getColumnIndex(hub)), c.getString(c.getColumnIndex(enteredDate)), c.getString(c.getColumnIndex(date)), c.getString(c.getColumnIndex(sync))));
        }while(c.moveToNext());

        c.close();
        db.close();

        return trans;
    }

    public ArrayList<Transaction> displayAllLMRPickupTransactions()
    {
        SQLiteDatabase db = getWritableDatabase();
        prefs = context.getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        String all = "select * from " + table + " where Out like 'R%7_'  and Inn != 'Sale' " +
                "and Date >= + '" + prefs2.getString("startdate", todaysDate)+ "' " +
                "and Date <= '" + prefs2.getString("enddate", todaysDate) + "' order by timestamp";
        Cursor c = db.rawQuery(all,null);
        c.moveToFirst();

        ArrayList<Transaction> trans = new ArrayList<>();

        if(c.getCount() < 1){ return trans;} //makes sure a null cursor is not passed to the add log function below

        do
        {
            trans.add(new Transaction(c.getString(c.getColumnIndex(id)), c.getString(c.getColumnIndex(itemid)),
                    c.getString(c.getColumnIndex(itemName)), c.getString(c.getColumnIndex(unit)), c.getString(c.getColumnIndex(in)),
                    c.getString(c.getColumnIndex(out)), c.getString(c.getColumnIndex(type)), c.getString(c.getColumnIndex(unitPrice)),
                    c.getString(c.getColumnIndex(suggUnitPrice)),c.getString(c.getColumnIndex(hub)), c.getString(c.getColumnIndex(enteredDate)), c.getString(c.getColumnIndex(date)), c.getString(c.getColumnIndex(sync))));
        }while(c.moveToNext());

        c.close();
        db.close();

        return trans;
    }

    public ArrayList<Transaction> displayLMDDeliveryTransactions()
    {
        SQLiteDatabase db = getWritableDatabase();
        prefs = context.getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        String all = "select * from " + table + " where Out = '"+ prefs.getString("lmdid","") +"' and Inn like 'R%9_' and Date ='" + new Date(System.currentTimeMillis()).toString() + "' order by timestamp";
        Cursor c = db.rawQuery(all,null);
        c.moveToFirst();

        ArrayList<Transaction> trans = new ArrayList<>();

        if(c.getCount() < 1){ return trans;} //makes sure a null cursor is not passed to the add log function below

        do
        {
            trans.add(new Transaction(c.getString(c.getColumnIndex(id)), c.getString(c.getColumnIndex(itemid)),
                    c.getString(c.getColumnIndex(itemName)), c.getString(c.getColumnIndex(unit)), c.getString(c.getColumnIndex(in)),
                    c.getString(c.getColumnIndex(out)), c.getString(c.getColumnIndex(type)), c.getString(c.getColumnIndex(unitPrice)),
                    c.getString(c.getColumnIndex(suggUnitPrice)),c.getString(c.getColumnIndex(hub)), c.getString(c.getColumnIndex(enteredDate)), c.getString(c.getColumnIndex(date)), c.getString(c.getColumnIndex(sync))));
        }while(c.moveToNext());

        c.close();
        db.close();

        return trans;
    }

    public ArrayList<Transaction> displayAllLMDDeliveryTransactions()
    {
        SQLiteDatabase db = getWritableDatabase();
        prefs = context.getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        String all = "select * from " + table + " where Out like 'T%' and Inn like 'R%9_' " +
                "and Date >= + '" + prefs2.getString("startdate", todaysDate)+ "' " +
                "and Date <= '" + prefs2.getString("enddate", todaysDate) + "' order by timestamp";
        Cursor c = db.rawQuery(all,null);
        c.moveToFirst();

        ArrayList<Transaction> trans = new ArrayList<>();

        if(c.getCount() < 1){ return trans;} //makes sure a null cursor is not passed to the add log function below

        do
        {
            trans.add(new Transaction(c.getString(c.getColumnIndex(id)), c.getString(c.getColumnIndex(itemid)),
                    c.getString(c.getColumnIndex(itemName)), c.getString(c.getColumnIndex(unit)), c.getString(c.getColumnIndex(in)),
                    c.getString(c.getColumnIndex(out)), c.getString(c.getColumnIndex(type)), c.getString(c.getColumnIndex(unitPrice)),
                    c.getString(c.getColumnIndex(suggUnitPrice)),c.getString(c.getColumnIndex(hub)), c.getString(c.getColumnIndex(enteredDate)), c.getString(c.getColumnIndex(date)), c.getString(c.getColumnIndex(sync))));
        }while(c.moveToNext());

        c.close();
        db.close();

        return trans;
    }

    public ArrayList<Transaction> displayLMRDeliveryTransactions()
    {
        SQLiteDatabase db = getWritableDatabase();
        prefs = context.getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        String all = "select * from " + table + " where Out = '"+ prefs.getString("lmdid","") +"' and Inn like 'R%7_' and Date ='" + new Date(System.currentTimeMillis()).toString() + "' order by timestamp";
        Cursor c = db.rawQuery(all,null);
        c.moveToFirst();

        ArrayList<Transaction> trans = new ArrayList<>();

        if(c.getCount() < 1){ return trans;} //makes sure a null cursor is not passed to the add log function below

        do
        {
            trans.add(new Transaction(c.getString(c.getColumnIndex(id)), c.getString(c.getColumnIndex(itemid)),
                    c.getString(c.getColumnIndex(itemName)), c.getString(c.getColumnIndex(unit)), c.getString(c.getColumnIndex(in)),
                    c.getString(c.getColumnIndex(out)), c.getString(c.getColumnIndex(type)), c.getString(c.getColumnIndex(unitPrice)),
                    c.getString(c.getColumnIndex(suggUnitPrice)),c.getString(c.getColumnIndex(hub)), c.getString(c.getColumnIndex(enteredDate)), c.getString(c.getColumnIndex(date)), c.getString(c.getColumnIndex(sync))));
        }while(c.moveToNext());

        c.close();
        db.close();

        return trans;
    }

    public ArrayList<Transaction> displayAllLMRDeliveryTransactions()
    {
        SQLiteDatabase db = getWritableDatabase();
        prefs = context.getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        String all = "select * from " + table + " where Out like 'R%9_' and Inn like 'R%7_' " +
                "and Date >= + '" + prefs2.getString("startdate", todaysDate)+ "' " +
                "and Date <= '" + prefs2.getString("enddate", todaysDate) + "' order by timestamp";
        Cursor c = db.rawQuery(all,null);
        c.moveToFirst();

        ArrayList<Transaction> trans = new ArrayList<>();

        if(c.getCount() < 1){ return trans;} //makes sure a null cursor is not passed to the add log function below

        do
        {
            trans.add(new Transaction(c.getString(c.getColumnIndex(id)), c.getString(c.getColumnIndex(itemid)),
                    c.getString(c.getColumnIndex(itemName)), c.getString(c.getColumnIndex(unit)), c.getString(c.getColumnIndex(in)),
                    c.getString(c.getColumnIndex(out)), c.getString(c.getColumnIndex(type)), c.getString(c.getColumnIndex(unitPrice)),
                    c.getString(c.getColumnIndex(suggUnitPrice)),c.getString(c.getColumnIndex(hub)), c.getString(c.getColumnIndex(enteredDate)), c.getString(c.getColumnIndex(date)), c.getString(c.getColumnIndex(sync))));
        }while(c.moveToNext());

        c.close();
        db.close();

        return trans;
    }

    public ArrayList<Transaction> displaySaleTransactions()
    {
        SQLiteDatabase db = getWritableDatabase();
        prefs = context.getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        //show all sales to this lmd that happened today
        String all = "select * from " + table + " where Out = '"+ prefs.getString("lmdid","") +"' and Inn = 'Sale' and Date ='" + new Date(System.currentTimeMillis()).toString() + "' order by timestamp";
        Cursor c = db.rawQuery(all,null);
        c.moveToFirst();

        ArrayList<Transaction> trans = new ArrayList<>();

        if(c.getCount() < 1){ return trans;} //makes sure a null cursor is not passed to the add log function below

        do
        {
            trans.add(new Transaction(c.getString(c.getColumnIndex(id)), c.getString(c.getColumnIndex(itemid)),
                    c.getString(c.getColumnIndex(itemName)), c.getString(c.getColumnIndex(unit)), c.getString(c.getColumnIndex(in)),
                    c.getString(c.getColumnIndex(out)), c.getString(c.getColumnIndex(type)), c.getString(c.getColumnIndex(unitPrice)),
                    c.getString(c.getColumnIndex(suggUnitPrice)),c.getString(c.getColumnIndex(hub)), c.getString(c.getColumnIndex(enteredDate)), c.getString(c.getColumnIndex(date)), c.getString(c.getColumnIndex(sync))));
        }while(c.moveToNext());

        c.close();
        db.close();

        return trans;
    }

    public ArrayList<Transaction> displayAllSaleTransactions()
    {
        SQLiteDatabase db = getWritableDatabase();
        prefs = context.getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        //show all sales to this lmd that happened today
        String all = "select * from " + table + " where Inn = 'Sale' " +
                "and Date >= + '" + prefs2.getString("startdate", todaysDate)+ "' " +
                "and Date <= '" + prefs2.getString("enddate", todaysDate) + "' order by timestamp";
        Cursor c = db.rawQuery(all,null);
        c.moveToFirst();

        ArrayList<Transaction> trans = new ArrayList<>();

        if(c.getCount() < 1){ return trans;} //makes sure a null cursor is not passed to the add log function below

        do
        {
            trans.add(new Transaction(c.getString(c.getColumnIndex(id)), c.getString(c.getColumnIndex(itemid)),
                    c.getString(c.getColumnIndex(itemName)), c.getString(c.getColumnIndex(unit)), c.getString(c.getColumnIndex(in)),
                    c.getString(c.getColumnIndex(out)), c.getString(c.getColumnIndex(type)), c.getString(c.getColumnIndex(unitPrice)),
                    c.getString(c.getColumnIndex(suggUnitPrice)),c.getString(c.getColumnIndex(hub)), c.getString(c.getColumnIndex(enteredDate)), c.getString(c.getColumnIndex(date)), c.getString(c.getColumnIndex(sync))));
        }while(c.moveToNext());

        c.close();
        db.close();

        return trans;
    }

    public ArrayList<Transaction> displayAllLMDPickupTransactions()
    {
        SQLiteDatabase db = getWritableDatabase();
        prefs = context.getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        String all = "select * from " + table + " where Out like 'R%9_' and Inn like 'T%' " +
                "and Date >= + '" + prefs2.getString("startdate", todaysDate)+ "' " +
                "and Date <= '" + prefs2.getString("enddate", todaysDate) + "' order by timestamp";
        Cursor c = db.rawQuery(all,null);
        c.moveToFirst();

        ArrayList<Transaction> trans = new ArrayList<>();

        if(c.getCount() < 1){ return trans;} //makes sure a null cursor is not passed to the add log function below

        do
        {
            trans.add(new Transaction(c.getString(c.getColumnIndex(id)), c.getString(c.getColumnIndex(itemid)),
                    c.getString(c.getColumnIndex(itemName)), c.getString(c.getColumnIndex(unit)), c.getString(c.getColumnIndex(in)),
                    c.getString(c.getColumnIndex(out)), c.getString(c.getColumnIndex(type)), c.getString(c.getColumnIndex(unitPrice)),
                    c.getString(c.getColumnIndex(suggUnitPrice)),c.getString(c.getColumnIndex(hub)), c.getString(c.getColumnIndex(enteredDate)), c.getString(c.getColumnIndex(date)), c.getString(c.getColumnIndex(sync))));
        }while(c.moveToNext());

        c.close();
        db.close();

        return trans;
    }

    //update functions
    public void updateSyncStatus(String unique_id, String sync){
        SQLiteDatabase database = getWritableDatabase();
        String updateQuery = "update " + table + " set sync = '"+ sync +"' where "+ uniqueID +" = \"" + unique_id +"\"";
        Log.d("QUERY", updateQuery);
        database.execSQL(updateQuery);
    }

    public ArrayList<HashMap<String, String>> getAllTrans(){
        ArrayList<HashMap<String, String>> wordList = new ArrayList<>();
        String selectQuery = "select * from " + table;
        Cursor cursor = getReadableDatabase().rawQuery(selectQuery, null);

        prefs1 = context.getSharedPreferences("Preferences1", MODE_PRIVATE);
        user = prefs1.getString("username"," ");
        version = prefs1.getString("appVersion", "");
        cursor.moveToFirst();
        if(cursor.getCount() < 1){ return wordList;}

        do
        {
            HashMap<String, String> map = new HashMap<>();
            map.put("idonline", cursor.getString(cursor.getColumnIndex("id")));
            map.put("username", user);
            map.put("timestamp", cursor.getString(cursor.getColumnIndex("timestamp")));
            map.put("itemid", cursor.getString(cursor.getColumnIndex("ItemID")));
            map.put("itemname", cursor.getString(cursor.getColumnIndex("ItemName")));
            map.put("unit", cursor.getString(cursor.getColumnIndex("Unit")));
            map.put("in", cursor.getString(cursor.getColumnIndex("Inn")));
            map.put("out", cursor.getString(cursor.getColumnIndex("Out")));
            map.put("entereddate", cursor.getString(cursor.getColumnIndex("enteredDate")));
            map.put("date", cursor.getString(cursor.getColumnIndex("Date")));
            map.put("type", cursor.getString(cursor.getColumnIndex("Type")));
            map.put("unitprice", cursor.getString(cursor.getColumnIndex("UnitPrice")));
            map.put("suggestedprice", cursor.getString(cursor.getColumnIndex("SuggestedUnitPrice")));
            map.put("hub", cursor.getString(cursor.getColumnIndex("Hub")));
            map.put("sync", cursor.getString(cursor.getColumnIndex("sync")));
            map.put("version", version);
            wordList.add(map);
        }while (cursor.moveToNext());
        cursor.close();

        return wordList;
    }

    public ArrayList<HashMap<String, String>> getAllUnsyncedTrans(){
        ArrayList<HashMap<String, String>> wordList = new ArrayList<>();
        String selectQuery = "select * from " + table + " where sync = 'no'";
        Cursor cursor = getReadableDatabase().rawQuery(selectQuery, null);

        //gets the mik's name and hub to be appended to each log for identification of users
        prefs1 = context.getSharedPreferences("Preferences1", MODE_PRIVATE);
        user = prefs1.getString("username","");
        staffid = prefs1.getString("staff_id", "");
        version = prefs1.getString("appVersion", "");
        String timeStamp = String.valueOf(System.currentTimeMillis());



        cursor.moveToFirst();
        do
        {
            HashMap<String, String> map = new HashMap<>();
            map.put("idonline", cursor.getString(cursor.getColumnIndex("id")));
            map.put("username", staffid);
            map.put("timestamp", cursor.getString(cursor.getColumnIndex("timestamp")));
            map.put("itemid", cursor.getString(cursor.getColumnIndex("ItemID")));
            map.put("itemname", cursor.getString(cursor.getColumnIndex("ItemName")));
            map.put("unit", cursor.getString(cursor.getColumnIndex("Unit")));
            map.put("in", cursor.getString(cursor.getColumnIndex("Inn")));
            map.put("out", cursor.getString(cursor.getColumnIndex("Out")));
            map.put("entereddate", cursor.getString(cursor.getColumnIndex("enteredDate")));
            map.put("date", cursor.getString(cursor.getColumnIndex("Date")));
            map.put("type", cursor.getString(cursor.getColumnIndex("Type")));
            map.put("unitprice", cursor.getString(cursor.getColumnIndex("UnitPrice")));
            map.put("suggestedprice", cursor.getString(cursor.getColumnIndex("SuggestedUnitPrice")));
            map.put("hub", cursor.getString(cursor.getColumnIndex("Hub")));
            map.put("sync", cursor.getString(cursor.getColumnIndex("sync")));
            map.put("version", version);
            map.put("uniqueid", cursor.getString(cursor.getColumnIndex("uniqueID")));
            wordList.add(map);
        }while (cursor.moveToNext());
        cursor.close();

        return wordList;
    }

    public int dbSyncCount()
    {
        int count;
        String selectQuery = "select * from " + table + " where sync = 'no'";
        Cursor cursor = getReadableDatabase().rawQuery(selectQuery, null);
        count = cursor.getCount();
        return count;
    }

    public String composeJSONfromSQLite()
    {
        ArrayList<HashMap<String, String>> unsynced = this.getAllUnsyncedTrans();
        Gson gson = new GsonBuilder().create();
        Log.i("FINAL","JSON composed "+ gson.toJson(unsynced));
        return gson.toJson(unsynced);
    }
}
