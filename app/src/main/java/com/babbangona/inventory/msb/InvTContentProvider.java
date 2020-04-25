package com.babbangona.inventory.msb;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.HashMap;

public class InvTContentProvider extends ContentProvider {

    private static final String TABLE_NAME = "InventoryT";

    public static final String AUTHORITY = "com.babbangona.inventory.msb";
    private static final UriMatcher uriMatch;

    //URL provided to all other applications, giving them only one access to one table.
    static final String URL = "content://" + AUTHORITY + "/" + TABLE_NAME;

    private static final int theInventory = 1;
    private static final int theInventory_ID = 2;
    private static HashMap<String, String> InventoryProjectionMap;

    InventoryTDBhandler dbHandler;

    @Override
    public boolean onCreate() {
        dbHandler = new InventoryTDBhandler(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(TABLE_NAME);
        builder.setProjectionMap(InventoryProjectionMap);

        switch (uriMatch.match(uri)){
            case theInventory:
                break;
            case theInventory_ID:
                selection = selection + "uniqueID = " + uri.getLastPathSegment();
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        SQLiteDatabase db = dbHandler.getReadableDatabase();
        Cursor cursor = builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (uriMatch.match(uri)){
            case theInventory:
                return InventoryT.CONTENT_TYPE;
            default:
                throw new IllegalArgumentException("(getType) Unknown URI " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        if(uriMatch.match(uri) != theInventory){
            throw new IllegalArgumentException("(Insert) Unknown URI " + uri);
        }

        SQLiteDatabase db = dbHandler.getWritableDatabase();
        long rowId = db.insert(TABLE_NAME, InventoryT.COLUMN_UNIQUE_ID, contentValues);
        if(rowId > 0){
            Uri invUri = ContentUris.withAppendedId(InventoryT.CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(invUri, null);
            return invUri;
        }

        throw new SQLException("Failed to insert row into " + uri);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String where, @Nullable String[] whereArgs) {
        SQLiteDatabase db = dbHandler.getWritableDatabase();
        switch (uriMatch.match(uri)){
            case theInventory:
                //Case to delete all records, statements missing.
                break;
            case theInventory_ID:
                where = where + "uniqueID = " + uri.getLastPathSegment();
                break;
            default:
                throw new IllegalArgumentException("(Delete) Unknown URI " + uri);
        }

        int count = db.delete(TABLE_NAME, where, whereArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String where, @Nullable String[] whereArgs) {
        SQLiteDatabase db = dbHandler.getWritableDatabase();
        int count;
        switch (uriMatch.match(uri)){
            case theInventory:
                count = db.update(TABLE_NAME, contentValues, where, whereArgs);
                break;
            default:
                throw new IllegalArgumentException("(Update) Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    static {
        uriMatch = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatch.addURI(AUTHORITY, TABLE_NAME, theInventory);
        uriMatch.addURI(AUTHORITY, TABLE_NAME + "/#", theInventory_ID);

        InventoryProjectionMap = new HashMap<String, String>();
        InventoryProjectionMap.put(InventoryT.COLUMN_UNIQUE_ID, InventoryT.COLUMN_UNIQUE_ID);
        InventoryProjectionMap.put(InventoryT.COLUMN_TIMESTAMP, InventoryT.COLUMN_TIMESTAMP);
        InventoryProjectionMap.put(InventoryT.COLUMN_ID_ONLINE, InventoryT.COLUMN_ID_ONLINE);
        InventoryProjectionMap.put(InventoryT.COLUMN_ID, InventoryT.COLUMN_ID);
        InventoryProjectionMap.put(InventoryT.COLUMN_ITEM_ID, InventoryT.COLUMN_ITEM_ID);
        InventoryProjectionMap.put(InventoryT.COLUMN_ITEM_NAME, InventoryT.COLUMN_ITEM_NAME);
        InventoryProjectionMap.put(InventoryT.COLUMN_UNIT, InventoryT.COLUMN_UNIT);
        InventoryProjectionMap.put(InventoryT.COLUMN_INN, InventoryT.COLUMN_INN);
        InventoryProjectionMap.put(InventoryT.COLUMN_OUT, InventoryT.COLUMN_OUT);
        InventoryProjectionMap.put(InventoryT.COLUMN_ENTERED_DATE, InventoryT.COLUMN_ENTERED_DATE);
        InventoryProjectionMap.put(InventoryT.COLUMN_DATE, InventoryT.COLUMN_DATE);
        InventoryProjectionMap.put(InventoryT.COLUMN_TYPE, InventoryT.COLUMN_TYPE);
        InventoryProjectionMap.put(InventoryT.COLUMN_UNIT_PRICE, InventoryT.COLUMN_UNIT_PRICE);
        InventoryProjectionMap.put(InventoryT.COLUMN_SUGGESTED_UNIT_PRICE, InventoryT.COLUMN_SUGGESTED_UNIT_PRICE);
        InventoryProjectionMap.put(InventoryT.COLUMN_HUB, InventoryT.COLUMN_HUB);
        InventoryProjectionMap.put(InventoryT.COLUMN_SYNC, InventoryT.COLUMN_SYNC);

    }
}
