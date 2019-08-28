package com.example.store.oc;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

public class DbCart extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME  = "cartDb";
    public static final String TABLE_CART  = "cart";

    public static final String KEY_ID  = "_id";
    public static final String KEY_PRODUCT_ID  = "product_id";
    public static final String KEY_NAME = "name";
    public static final String KEY_IMAGE  = "image";
    public static final String KEY_QUANTITY  = "quantity";
    public static final String KEY_PRICE  = "price";


    public DbCart(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_CART + " (" + KEY_ID + " integer PRIMARY KEY AUTOINCREMENT, "
                + KEY_PRODUCT_ID + " integer, "
                + KEY_NAME + " text, "
                + KEY_IMAGE + " text, "
                + KEY_QUANTITY + " intager, "
                + KEY_PRICE + " real)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

    }
}
