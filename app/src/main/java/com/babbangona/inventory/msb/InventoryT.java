package com.babbangona.inventory.msb;

import android.net.Uri;
import android.provider.BaseColumns;

public final class InventoryT implements BaseColumns {

    public InventoryT() {
    }

    public static final Uri CONTENT_URI = Uri.parse("content://" + InvTContentProvider.AUTHORITY + "/InventoryT");

    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.jwei512.InventoryT";

    /**
     * Names for the columns in hte InventoryT table.
     */

    public static final String COLUMN_UNIQUE_ID = "uniqueID";
    public static final String COLUMN_TIMESTAMP = "timestamp";
    public static final String COLUMN_ID_ONLINE = "idonline";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_ITEM_ID = "ItemID";
    public static final String COLUMN_ITEM_NAME = "ItemName";
    public static final String COLUMN_UNIT = "Unit";
    public static final String COLUMN_INN = "Inn";
    public static final String COLUMN_OUT = "Out";
    public static final String COLUMN_ENTERED_DATE = "enteredDate";
    public static final String COLUMN_DATE = "Date";
    public static final String COLUMN_TYPE = "Type";
    public static final String COLUMN_UNIT_PRICE = "UnitPrice";
    public static final String COLUMN_SUGGESTED_UNIT_PRICE = "SuggestedUnitPrice";
    public static final String COLUMN_HUB = "Hub";
    public static final String COLUMN_SYNC = "sync";
}
