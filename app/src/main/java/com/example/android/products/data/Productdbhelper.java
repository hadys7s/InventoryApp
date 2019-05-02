package com.example.android.products.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.products.data.ProductContract.ProductEntry;


public class Productdbhelper extends SQLiteOpenHelper {

    /**
     * Name of the database file
     */
    private static final String DATABASE_NAME = "store.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;

    public Productdbhelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create a String that contains the SQL statement to create the products table
        String SQL_CREATE_productS_TABLE = "CREATE TABLE " + ProductEntry.TABLE_NAME + " ("
                + ProductEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ProductEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
                + ProductEntry.COLUMN_PRODUCT_Price + " INTEGER, "
                + ProductEntry.COLUMN_PRODUCT_Quantity + " INTEGER NOT NULL, "
                + ProductEntry.COLUMN_Product_Descrption + " TEXT NOT NULL,"
                + ProductEntry.COLUMN_PRODUCT_Image + " TEXT NOT NULL);";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_productS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
