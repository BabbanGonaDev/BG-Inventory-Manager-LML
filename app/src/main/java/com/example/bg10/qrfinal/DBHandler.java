package com.example.bg10.qrfinal;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;

/**
 * Created by ciani on 08/03/2018.
 */
public class DBHandler extends SQLiteAssetHelper {

    public DBHandler(Context context) {
        super(context, "users.db", null, 1);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);

    }

    public ArrayList<String> getUsers()
    {
        SQLiteDatabase db = getWritableDatabase();

        String all = "select username from users";
        Cursor c = db.rawQuery(all,null);
        c.moveToFirst();

        ArrayList<String> users = new ArrayList<>();

        if(c.getCount() < 1){ return users;} //makes sure a null cursor is not passed to the add log function below

        do
        {
            users.add(c.getString(c.getColumnIndex("username")));
        }while(c.moveToNext());

        c.close();
        db.close();

        return users;

    }

    public boolean userExists(String fieldValue) {
        SQLiteDatabase sqldb = getWritableDatabase();
        String Query = "select * from users where username = " + "'" + fieldValue + "'";
        Cursor cursor = sqldb.rawQuery(Query, null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    public String password(String username)
    {
        SQLiteDatabase db = getWritableDatabase();

        String all = "select * from users where username = " + "'" + username + "'";

        Cursor c = db.rawQuery(all,null);
        c.moveToFirst();

        return c.getString(c.getColumnIndex("password"));


    }

    public String department(String username, String StaffId)
    {
        SQLiteDatabase db = getWritableDatabase();

        String all = "select * from users where username = \"" + username + "\" and staff_id = \"" + StaffId + "\"";

        Cursor c = db.rawQuery(all,null);
        c.moveToFirst();

        return c.getString(c.getColumnIndex("department"));


    }
    public String hub(String username, String StaffId)
    {
        SQLiteDatabase db = getWritableDatabase();

        String all = "select * from users where username = \"" + username + "\" and staff_id = \"" + StaffId + "\"" ;

        Cursor c = db.rawQuery(all,null);
        c.moveToFirst();

        return c.getString(c.getColumnIndex("hub"));


    }

    public boolean logged(String username, String StaffId)
    {
        try {
            SQLiteDatabase db = getWritableDatabase();

            String all = "select * from users where username = \"" + username + "\" and staff_id = \"" + StaffId + "\"";

            Cursor c = db.rawQuery(all, null);
            c.moveToFirst();

            return (c.getString(c.getColumnIndex("logged")).equals("yes") ? true : false);
        }catch(Exception e){
            return false;
        }

    }
    public boolean synced(String username, String StaffId)
    {
        try {
            SQLiteDatabase db = getWritableDatabase();

            String all = "select * from users where username = \"" + username + "\" and staff_id = \"" + StaffId + "\"";

            Cursor c = db.rawQuery(all, null);
            c.moveToFirst();

            return (c.getString(c.getColumnIndex("synced")).equals("yes") ? true : false);
        }catch(Exception e){
            return false;
        }

    }

    public void updateLogged(String username, String StaffId, String staff_role, String passedHub)
    {
        String pass = "1111";
        SQLiteDatabase db = getWritableDatabase();
        String find = "SELECT * FROM users WHERE staff_id = \"" + StaffId + "\"";
        Cursor cursor = db.rawQuery(find, null);
        cursor.moveToFirst();

        if(cursor.getCount() == 0){
            db.execSQL("INSERT INTO users (staff_id, username, department, hub, password, logged, synced) VALUES (\""+ StaffId +"\",\""+ username +"\",\""+ staff_role +"\",\""+ passedHub +"\",'"+ pass +"','yes','no')");

        }else{
            db.execSQL("update users set logged = 'yes' where username = \"" + username + "\" and staff_id = \"" + StaffId + "\"");

        }
        cursor.close();
        db.close();
    }

    public void updateSynced(String username, String StaffId)
    {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("update users set synced = 'yes' where username = \"" + username + "\" and staff_id = \"" + StaffId + "\"");
        db.close();
    }
}
