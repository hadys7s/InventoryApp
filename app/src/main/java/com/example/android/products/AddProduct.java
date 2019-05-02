package com.example.android.products;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.products.data.ProductContract.ProductEntry;


/**
 * Allows user to create a new product or edit an existing one.
 */
public class AddProduct extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * EditText field to enter the product's name
     */
    private boolean mproductHasChanged = false;
    private EditText mNameEditText;
    Uri mCurrentproductUri;
    private static final int PICK_IMAGE_REQUEST = 0;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    Uri actualUri;
    String imagestring;


    /**
     * EditText field to enter the product's breed
     */
    private EditText PriceEditText;
    private Button uplodimage;

    /**
     * EditText field to enter the product's weight
     */
    private EditText QuantityEditText;
    EditText descriptionEditText;
    private Button Save;
    ImageView productimage;

    long productid;




    /**
     */
    private static final int EXISTING_product_LOADER = 0;
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mproductHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        mNameEditText = (EditText) findViewById(R.id.edit_product_name);
        PriceEditText = (EditText) findViewById(R.id.edit_product_price);
        QuantityEditText = (EditText) findViewById(R.id.edit_product_quantity);
        descriptionEditText = (EditText) findViewById(R.id.edit_product_descrption);
        uplodimage = (Button) findViewById(R.id.uplod_image);
        productimage = findViewById(R.id.Producimageall);
        mNameEditText.setOnTouchListener(mTouchListener);
        PriceEditText.setOnTouchListener(mTouchListener);
        QuantityEditText.setOnTouchListener(mTouchListener);
        descriptionEditText.setOnTouchListener(mTouchListener);
        uplodimage.setOnTouchListener(mTouchListener);


        uplodimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryToOpenImageSelector();


            }
        });

        Intent intent = getIntent();


        final String source = intent.getExtras().getString("source");
        productid = intent.getExtras().getLong("itemsid");


        switch (source) {
            case "edit":
                Toast.makeText(getApplicationContext(), "Edit", Toast.LENGTH_SHORT).show();
                setTitle(getString(R.string.editor_activity_title_edit_product));
                mCurrentproductUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, productid);
                intent.setData(mCurrentproductUri);
                getLoaderManager().initLoader(EXISTING_product_LOADER, null, this);


                break;
            case "main":
                Toast.makeText(getApplicationContext(), "Main", Toast.LENGTH_SHORT).show();
                setTitle(getString(R.string.editor_activity_title_new_product));
                break;
        }

        Save = (Button) findViewById(R.id.Save_Product);
        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveProduct();

            }
        });
        uplodimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryToOpenImageSelector();
            }
        });




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

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the product.
                deleteproduct();
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

    private void deleteproduct() {
        Toast.makeText(this, "Product deleted", Toast.LENGTH_SHORT).show();

        getContentResolver().delete(mCurrentproductUri, null, null);


    }


    @Override
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




    /**
     * Get user input from editor and save new product into database.
     */
    private void saveProduct() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String nameString = mNameEditText.getText().toString().trim();
        String priceString = PriceEditText.getText().toString().trim();
        String quantity = QuantityEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();


        // Check if this is supposed to be a new product
        // and check if all the fields in the editor are blank
        if (TextUtils.isEmpty(nameString) ||
                TextUtils.isEmpty(priceString) || TextUtils.isEmpty(quantity)) {

            if (TextUtils.isEmpty(nameString)) {

                Toast.makeText(this, "Enter the Product Name", Toast.LENGTH_SHORT).show();

            }
            if (TextUtils.isEmpty(priceString)) {

                Toast.makeText(this, "Enter the Product Price", Toast.LENGTH_SHORT).show();

            }
            if (TextUtils.isEmpty(quantity)) {
                Toast.makeText(this, "Enter the Product quantity", Toast.LENGTH_SHORT).show();


            }


            // Since no fields were modified, we can return early without creating a new product.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            return;
        }

        // Create a ContentValues object where column names are the keys,
        // and product attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, nameString);
        if (actualUri != null) {
            imagestring = actualUri.toString();
            values.put(ProductEntry.COLUMN_PRODUCT_Image, imagestring);
        } else {
            values.put(ProductEntry.COLUMN_PRODUCT_Image, "");

        }


        if (TextUtils.isEmpty(description)) {

            description = "No Description";

        }

        values.put(ProductEntry.COLUMN_PRODUCT_Price, priceString);
        values.put(ProductEntry.COLUMN_PRODUCT_Quantity, quantity);
        values.put(ProductEntry.COLUMN_Product_Descrption, description);


        // Determine if this is a new or existing product by checking if mCurrentproductUri is null or not
        if (mCurrentproductUri == null) {
            // This is a NEW product, so insert a new product into the provider,
            // returning the content URI for the new product.
            Uri newUri = getContentResolver().insert(ProductEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            // Otherwise this is an EXISTING product, so update the product with content URI: mCurrentproductUri
            // and pass in the new ContentValues. Pass in null for the projection and projection args
            // because mCurrentproductUri will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver().update(mCurrentproductUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, "Update Failed",
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, "Product Updated", Toast.LENGTH_SHORT).show();
            }
        }
        NavUtils.navigateUpFromSameTask(AddProduct.this);

    }


    /*public void insert() {
        //we get the data that the user enter it

        String namestriing = mNameEditText.getText().toString().trim();
        String breedname = mBreedEditText.getText().toString().trim();
        String genderstring = mBreedEditText.getText().toString().trim();
        int gender = Integer.parseInt(genderstring);
        String weightstring = QuantityEditText.getText().toString().trim();
        int weight = Integer.parseInt(weightstring);

        //instance from our Dbhelper that creates DB
        productDbHelper helper = new productDbHelper(this);

        //make the database writeable inorder to update delete insert dada
        SQLiteDatabase db = helper.getWritableDatabase();

        //we input these value int Content
        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_product_NAME, namestriing);
        values.put(ProductEntry.COLUMN_product_BREED, breedname);
        values.put(ProductEntry.COLUMN_product_GENDER, gender);
        values.put(ProductEntry.COLUMN_product_WEIGHT, weight);
        long rowid = db.insert(ProductEntry.TABLE_NAME, null, values);


    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.listmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_delete:
                showDeleteConfirmationDialog();

                // Do nothing for now
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the product hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mproductHasChanged) {
                    NavUtils.navigateUpFromSameTask(AddProduct.this);
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
                                NavUtils.navigateUpFromSameTask(AddProduct.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }
    /*@Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new product, hide the "Delete" menu item.
        if (mCurrentproductUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }*/


    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {


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
                null);                  // Default sort order


    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if (data == null || data.getCount() < 1) {
            return;
        }

        if (data.moveToFirst()) {
            // Find the columns of product attributes that we're interested in
            int nameColumnIndex = data.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
            int PriceColumnIndex = data.getColumnIndex(ProductEntry.COLUMN_PRODUCT_Price);
            int QuantityColumnIndex = data.getColumnIndex(ProductEntry.COLUMN_PRODUCT_Quantity);
            int DescriptionColumnIndex = data.getColumnIndex(ProductEntry.COLUMN_Product_Descrption);
            int imageColumnIndex = data.getColumnIndex(ProductEntry.COLUMN_PRODUCT_Image);


            // Extract out the value from the Cursor for the given column index
            String name = data.getString(nameColumnIndex);
            String breed = data.getString(PriceColumnIndex);
            String quantity = data.getString(QuantityColumnIndex);
            String desc = data.getString(DescriptionColumnIndex);
            String image = data.getString(imageColumnIndex);

            mNameEditText.setText(name);
            PriceEditText.setText(breed);
            QuantityEditText.setText(quantity);
            descriptionEditText.setText(desc);
            productimage.setImageURI(Uri.parse(image));


        }


    }

    public void tryToOpenImageSelector() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            return;
        }
        openImageSelector();

    }


    private void openImageSelector() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openImageSelector();
                    // permission was granted
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        // The ACTION_OPEN_DOCUMENT intent was sent with the request code READ_REQUEST_CODE.
        // If the request code seen here doesn't match, it's the response to some other intent,
        // and the below code shouldn't run at all.
        if (resultCode != RESULT_CANCELED) {


            if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
                // The document selected by the user won't be returned in the intent.
                // Instead, a URI to that document will be contained in the return intent
                // provided to this method as a parameter.  Pull that uri using "resultData.getData()"
                actualUri = resultData.getData();
                productimage.setImageURI(actualUri);
                productimage.invalidate();
            }


        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

        mNameEditText.setText("");
        PriceEditText.setText("");
        QuantityEditText.setText("");
        productimage.setImageURI(null);
    }
}