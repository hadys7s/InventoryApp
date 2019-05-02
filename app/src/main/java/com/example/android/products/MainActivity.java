package com.example.android.products;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.android.products.data.ProductContract.ProductEntry;
import com.example.android.products.data.Productdbhelper;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int PRODUCT_LOADER = 0;
    private Productdbhelper mDbHelper;
    int origquantity;
    TextView reload;
    ProductCursorAdapter productCursorAdapter;
    long id;
    SearchView mSearchViewField;
    String searchstring;
    Button searchbutton;
    Bundle bundle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        reload = findViewById(R.id.Product_quantity);
/*
        mSearchViewField = findViewById(R.id.search_view_field);
*/
        /*searchbutton = findViewById(R.id.search_button);


        mSearchViewField.setQueryHint("Enter product name");*/
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddProduct.class);
                intent.putExtra("source", "main");
                startActivity(intent);
            }
        });

        ListView productistView = findViewById(R.id.list);
        productCursorAdapter = new ProductCursorAdapter(this, null, 0);
        productistView.setAdapter(productCursorAdapter);
        productistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int postion, long id) {
                Intent intent = new Intent(MainActivity.this, Detials.class);
                Uri currentPeturi = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, id);
                intent.setData(currentPeturi);
                intent.putExtra("itemsid", id);
                startActivity(intent);

            }
        });


        /*searchstring = mSearchViewField.getQuery().toString();
        bundle = new Bundle();


        final String[] projection = {ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME
        };
        final String Selection =  "name=?";
        final String[] args = {searchstring};


        searchbutton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                bundle.putStringArray("projection", projection);
                bundle.putStringArray("args", args);
                bundle.putString("selection", Selection);
                getLoaderManager().restartLoader(PRODUCT_LOADER,bundle,MainActivity.this);
            }
        });*/


        getLoaderManager().initLoader(PRODUCT_LOADER, null, this);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.listmenu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                getContentResolver().delete(ProductEntry.CONTENT_URI, null, null);


                // Do nothing for nows
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {

        /*String[] projections = bundle.getStringArray("projection");
        String[] args1 = bundle.getStringArray("args");
        String selection = bundle.getString("selection");
*/

        String[] Projection = {
                ProductEntry._ID
                , ProductEntry.COLUMN_PRODUCT_NAME
                , ProductEntry.COLUMN_PRODUCT_Price
                , ProductEntry.COLUMN_PRODUCT_Quantity
                , ProductEntry.COLUMN_PRODUCT_Image

        };

        return new CursorLoader(this,
                ProductEntry.CONTENT_URI,
                Projection,
                null,
                null,
                null);


    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        productCursorAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        productCursorAdapter.swapCursor(null);


    }
}
