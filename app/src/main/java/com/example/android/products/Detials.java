package com.example.android.products;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.products.data.ProductContract.ProductEntry;


public class Detials extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private boolean mproductHasChanged = false;
    private Uri mCurrentproductUri;
    TextView Name;
    TextView Price;
    TextView Quantity;
    TextView Description;
    ImageView detimage;
    Button plus;
    Button minus;
    Button order;
    int quantity;
    int price;
    int origquantity;

    Intent intent;
    long productId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detials);
        Name = findViewById(R.id.Product_Name_Details);
        Price = findViewById(R.id.Product_Price_Details);
        Quantity = findViewById(R.id.Product_Nquantity_Details);
        Description = findViewById(R.id.Product_description_Details);
        detimage = (ImageView) findViewById(R.id.Product_Image_Details);
        plus = (Button) findViewById(R.id.Product_plus_Details);
        minus = (Button) findViewById(R.id.Product_minus_product_Details);
        order = (Button) findViewById(R.id.Product_Order_Details);


        intent = getIntent();
        mCurrentproductUri = intent.getData();
        productId = intent.getExtras().getLong("itemsid");


        getLoaderManager().initLoader(0, null, this);


    }


    public void onBackPressed() {
        // If the product hasn't changed, continue with handling back button press
        if (!mproductHasChanged) {
            super.onBackPressed();
            return;
        }


        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.detailsmenu, menu);
        return true;
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            case R.id.action_edit:

                Intent i = new Intent(this, AddProduct.class);

                intent.setData(mCurrentproductUri);
                i.putExtra("itemsid", productId);
                i.putExtra("source", "edit");
                startActivity(i);
                return true;

            // Respond to a click on the "edit" menu option
            // edit product
            // Exit activity
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                showDeleteConfirmationDialog();

                return true;
            case android.R.id.home:
                // If the product hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mproductHasChanged) {
                    NavUtils.navigateUpFromSameTask(Detials.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(Detials.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the product.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteProduct() {
        Toast.makeText(this, "product deleted", Toast.LENGTH_SHORT).show();

        getContentResolver().delete(mCurrentproductUri, null, null);


    }


    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the product.
                deleteProduct();

                finish();

            }

        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the product.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_Price,
                ProductEntry.COLUMN_PRODUCT_Quantity,
                ProductEntry.COLUMN_Product_Descrption,
                ProductEntry.COLUMN_PRODUCT_Image
        };

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentproductUri,         // Query the content URI for the current product
                projection,             // Columns to include in the resulting Cursor
                null,                   // No projection clause
                null,                   // No projection arguments
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (data == null || data.getCount() < 1) {
            return;
        }

        if (data.moveToFirst()) {
            // Find the columns of product attributes that we're interested in
            int nameColumnIndex = data.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
            int PriceColumnIndex = data.getColumnIndex(ProductEntry.COLUMN_PRODUCT_Price);
            int descColumnIndex = data.getColumnIndex(ProductEntry.COLUMN_Product_Descrption);
            int origquan = data.getColumnIndex(ProductEntry.COLUMN_PRODUCT_Quantity);
            String image = data.getString(data.getColumnIndex(ProductEntry.COLUMN_PRODUCT_Image));


            // Extract out the value from the Cursor for the given column index
            String name = data.getString(nameColumnIndex);
            price = data.getInt(PriceColumnIndex);
            quantity = 1;
            origquantity = data.getInt(origquan);
            String description = data.getString(descColumnIndex);
            if (TextUtils.isEmpty(image)) {
                detimage.setImageResource(R.drawable.addproduct);

            } else {
                detimage.setImageURI(Uri.parse(image));
            }


            Name.setText(name);
            Price.setText(Integer.toString(price) + "$");
            Quantity.setText(Integer.toString(quantity));
            Description.setText(description);


        }
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (origquantity > quantity) {
                    quantity++;
                }
                Quantity.setText(Integer.toString(quantity));
                Price.setText(Integer.toString(quantity * price) + "$");

            }
        });
        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (quantity > 0) {
                    quantity--;
                }
                Quantity.setText(Integer.toString(quantity));
                Price.setText(Integer.toString(quantity * price));

            }
        });
        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                afterorder();

            }

        });
        data.close();

    }

    public void afterorder() {

        if (quantity == 0) {
            Toast.makeText(this, "Enter a quantity", Toast.LENGTH_SHORT).show();


        } else if (origquantity >= quantity) {
            ContentValues values = new ContentValues();

            values.put(ProductEntry.COLUMN_PRODUCT_Quantity, origquantity - quantity);
            getContentResolver().update(mCurrentproductUri, values, null, null);
            finish();
            Toast.makeText(this, "Orederd!", Toast.LENGTH_SHORT).show();


        } else
            Toast.makeText(this, getString(R.string.no_items), Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Name.setText("");
        Price.setText("");
        Quantity.setText("");
        Description.setText("");

    }
}
