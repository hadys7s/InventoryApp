package com.example.android.products.data;


import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;


/**
 * API Contract for the products app.
 */
public final class ProductContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private ProductContract() {
    }

    /**
     * Inner class that defines constant values for the products database table.
     * Each entry in the table represents a single product.
     */
    public static final class ProductEntry implements BaseColumns {

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of products.
         */



        /* Name of database table for products */
        public final static String TABLE_NAME = "products";

        /**
         * Unique ID number for the product (only for use in the database table).
         * <p>
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /*
          Name of the product.

          Type: TEXT
         */
        public final static String COLUMN_PRODUCT_NAME = "name";

        /**
         * price of the product.
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_PRODUCT_Price = "price";

        /**
         * quantity of the product.
         * <p>
         * <p>
         * Type: INTEGER
         */
        public final static String COLUMN_PRODUCT_Quantity = "quantity";

        public final static String COLUMN_PRODUCT_Image = "image";


        /**
         * description of the product.
         * <p>
         * Type: INTEGER
         */
        public final static String COLUMN_Product_Descrption = "description";

        public static final String CONTENT_AUTHORITY = "com.example.android.products";
        public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
        public static final String PATH_PRODUCTS = "products";
        public static final String PATH_PRODUCTS_ID = "products/#";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCTS);

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single product.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;


    }

}

