package com.babbangona.inventory.msb;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class ProductDBHelper extends SQLiteAssetHelper {

    Context context;
    public ProductDBHelper(Context context) {
        super(context,  "products.db", null,1);
        this.context = context;
    }

    String price(String itemID)
    {
        try {

            SQLiteDatabase db = getWritableDatabase();

            String all = "select * from products where itemID = '" + itemID +"'";

            Cursor c = db.rawQuery(all, null);
            c.moveToFirst();
            String price =  c.getString(c.getColumnIndex("Price"));
            c.close();
            return price;


        }catch(Exception e){
            Toast.makeText(context,"Scanned product does not exist in the database",Toast.LENGTH_LONG).show();
            return "-1";
        }
    }

    public String latestPriceUpdate()
    {
        SQLiteDatabase db = getWritableDatabase();

        String all = "select * from products order by timestamp desc";
        Cursor c = db.rawQuery(all,null);
        c.moveToFirst();

        if(c.getCount() < 1){ return "0";} //makes sure a null cursor is not passed to the get size function below

        String lastUpdated = c.getString(c.getColumnIndex("timestamp"));
        c.close();
        db.close();

        return lastUpdated;

    }

    public void pricesAddUpdate( String itemID, String itemName, String price, String timestamp)
    {
        SQLiteDatabase db = getWritableDatabase();

        //We are using this "check" method instead of the "replace" method because the database has been installed as at edit and there's no primary key.
        String check = "SELECT * FROM products WHERE itemID = '" + itemID + "'";
        Cursor c = db.rawQuery(check, null);
        c.moveToFirst();

        if(c.getCount() < 1){
            //It didn't find the ItemID, hence insert the record into it.
            db.execSQL("INSERT INTO products (itemName, itemID, price, timestamp) VALUES ('"+ itemName +"','"+ itemID +"','"+ price +"','"+ timestamp +"')");
            db.close();
        }else{
            //It found the item ID, hence update it.
            db.execSQL("update products set itemName = '" + itemName + "', timestamp = '" + timestamp + "', price = '" + price + "' where itemID = '" + itemID +"'" );
            db.close();
        }

        c.close();



    }



}
