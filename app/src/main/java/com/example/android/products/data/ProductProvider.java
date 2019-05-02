package com.example.android.products.data;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.android.products.data.ProductContract.ProductEntry;


public class ProductProvider extends ContentProvider {

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = ProductProvider.class.getSimpleName();

    /**
     * Initialize the provider and the database helper object.
     */
    /**
     * URI matcher code for the content URI for the products table
     */
    private static final int Products = 100;

    /**
     * URI matcher code for the content URI for a single product in the products table
     */
    private static final int PRODUCTS_ID = 101;
    Productdbhelper dbHelper;
    SQLiteDatabase sqLiteDatabase;
    public static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {

        uriMatcher.addURI(ProductEntry.CONTENT_AUTHORITY, ProductEntry.PATH_PRODUCTS, Products);
        uriMatcher.addURI(ProductEntry.CONTENT_AUTHORITY, ProductEntry.PATH_PRODUCTS_ID, PRODUCTS_ID);


    }


    @Override
    public boolean onCreate() {
        // TODO: Create and initialize a productDbHelper object to gain access to the products database.
        dbHelper = new Productdbhelper(getContext());
        // Make sure the variable is a global variable, so it can be referenced from other
        // ContentProvider methods.
        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, projection, projection arguments, and sort order.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database
        SQLiteDatabase Write = dbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = uriMatcher.match(uri);
        switch (match) {
            case Products:
                // For the Products code, query the products table directly with the given
                // projection, projection, projection arguments, and sort order. The cursor
                // could contain multiple rows of the Products table.
                cursor = Write.query(ProductEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case PRODUCTS_ID:
                // For the product_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.example.android.products/Products/3",
                // the projection will be "_id=?" and the projection argument will be a
                // String array containing the actual ID of 3 in this case.
                //
                // For every "?" in the projection, we need to have an element in the projection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // projection, we have 1 String in the projection arguments' String array.
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                // This will perform a query on the products table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = Write.query(ProductEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case Products:
                return insertProduct(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }

    }

    /**
     * Updates the data at the given projection and projection arguments, with the new ContentValues.
     */

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case Products:
                return updateProduct(uri, contentValues, selection, selectionArgs);
            case PRODUCTS_ID:
                // For the product_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and projection
                // arguments will be a String array containing the actual ID.
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateProduct(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }

    }

    /**
     * Delete the data at the given projection and projection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database
        int rowsDeleted;
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        switch (match) {
            case Products:
                // Delete all rows that match the projection and projection args
                rowsDeleted = database.delete(ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PRODUCTS_ID:
                // Delete a single row given by the ID in the URI
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);


        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;

    }


    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case Products:
                return ProductEntry.CONTENT_LIST_TYPE;
            case PRODUCTS_ID:
                return ProductEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    private Uri insertProduct(Uri uri, ContentValues values) {
        // Check that the name is not null
        String name = values.getAsString(ProductEntry.COLUMN_PRODUCT_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Product requires a name");
        }


        // If the quantity is provided, check that it's greater than or equal to 0 
        Integer quantity = values.getAsInteger(ProductEntry.COLUMN_PRODUCT_Quantity);
        if (quantity != null && quantity < 0) {
            throw new IllegalArgumentException("Product requires valid quantity");
        }

        SQLiteDatabase Read = dbHelper.getWritableDatabase();
        long id = Read.insert(ProductEntry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, id);
    }

    private int updateProduct(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        if (values.containsKey(ProductEntry.COLUMN_PRODUCT_NAME)) {
            String name = values.getAsString(ProductEntry.COLUMN_PRODUCT_NAME);
            if (name == null) {
                throw new IllegalArgumentException("product requires a name");
            }
        }

        // If the {@link ProductEntry#COLUMN_product_Price} key is present,
        if (values.containsKey(ProductEntry.COLUMN_PRODUCT_Price)) {
            Integer price = values.getAsInteger(ProductEntry.COLUMN_PRODUCT_Price);
            if (price == null) {
                throw new IllegalArgumentException("product requires valid price");
            }
        }

        // If the {@link ProductEntry#COLUMN_product_quantity} key is present,
        // check that the quantity value is valid.
        if (values.containsKey(ProductEntry.COLUMN_PRODUCT_Quantity)) {
            // Check that the quantity is greater than or equal to 0
            Integer quantity = values.getAsInteger(ProductEntry.COLUMN_PRODUCT_Quantity);
            if (quantity != null && quantity < 0) {
                throw new IllegalArgumentException("product requires valid quantity");
            }
        }

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase Update = dbHelper.getWritableDatabase();
        Update.update(ProductEntry.TABLE_NAME, values, selection, selectionArgs);


        // TODO: Return the number of rows that were affected
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        int rowsUpdated = database.update(ProductEntry.TABLE_NAME, values, selection, selectionArgs);
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;

    }

}