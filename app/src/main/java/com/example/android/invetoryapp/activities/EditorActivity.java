package com.example.android.invetoryapp.activities;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.invetoryapp.R;
import com.example.android.invetoryapp.data.ProductContract.ProductEntry;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    /** Identifier for the product data loader */
    private static final int EXISTING_PRODUCT_LOADER = 0;

    /** Content URI for the existing product (null if it's a new product) */
    private Uri mCurrentProductUri;

    /** Boolean flag that keeps track of whether the product has been edited (true) or not (false) */
    private boolean mProductHasChanged = false;

    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the mProductHasChanged boolean to true.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mProductHasChanged = true;
            return false;
        }
    };

    private EditText editProductName;
    private EditText editProductPrice;
    private EditText editProductQuantity;
    private EditText editSupplierName;
    private EditText editSupplierPhone;
    private TextView callSupplierTV;
    private String supplierName;
    private String supplierPhone;
    private String productName;
    private double productPrice;
    private int productQuantity;
    private ImageView increaseQuantityIV;
    private ImageView decreaseQuantityIV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new product or editing an existing one.
        Intent intent = getIntent();
        mCurrentProductUri = intent.getData();

        callSupplierTV = findViewById(R.id.call_supplier_tv);

        // If the intent DOES NOT contain a product content URI, then we know that we are
        // creating a new product.
        if (mCurrentProductUri == null) {
            setTitle(getString(R.string.editor_activity));
            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a product that hasn't been created yet.)
            invalidateOptionsMenu();

            // Hide the call supplier TextView
            callSupplierTV.setVisibility(View.GONE);
        } else {
            // Otherwise this is an existing product, so change app bar to say "Edit Product"
            setTitle(getString(R.string.editor_activity_edit));

            // Initialize a loader to read the product data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);

            // Show "Call supplier" TextView
            callSupplierTV.setVisibility(View.VISIBLE);
        }

        // Getting all edit texts

        editSupplierName = findViewById(R.id.edit_supplier_name);
        editSupplierPhone = findViewById(R.id.edit_supplier_phone);
        editProductName = findViewById(R.id.edit_product_name);
        editProductPrice = findViewById(R.id.edit_product_price);
        editProductQuantity = findViewById(R.id.edit_product_quantity);
        increaseQuantityIV = findViewById(R.id.increase_quantity);
        decreaseQuantityIV = findViewById(R.id.decrease_quantity);

        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        editSupplierName.setOnTouchListener(mTouchListener);
        editSupplierPhone.setOnTouchListener(mTouchListener);
        editProductName.setOnTouchListener(mTouchListener);
        editProductPrice.setOnTouchListener(mTouchListener);
        editProductQuantity.setOnTouchListener(mTouchListener);
        increaseQuantityIV.setOnTouchListener(mTouchListener);
        decreaseQuantityIV.setOnTouchListener(mTouchListener);

        increaseQuantityIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                productQuantity++;
                editProductQuantity.setText(Integer.toString(productQuantity));
            }
        });
        decreaseQuantityIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(productQuantity == 0) return;
                productQuantity--;
                editProductQuantity.setText(Integer.toString(productQuantity));
            }
        });

    }



    /** Method to check for errors
     * @return true  - if it has errors
     *         false - if it doesn't have errors */
    private boolean hasErrors() {
        // Setting the data
        productName = editProductName.getText().toString().trim();
        String productQuantityString = editProductQuantity.getText().toString().trim();
        String productPriceString = editProductPrice.getText().toString();
        supplierName = editSupplierName.getText().toString().trim();
        supplierPhone = editSupplierPhone.getText().toString().trim();

        if(productQuantityString.equals("")) {
            productQuantity = 0;
        } else {
            productQuantity = Integer.parseInt(productQuantityString);
            if(productQuantity < 0) {
                Toast.makeText(this, getString(R.string.invalid_quantity), Toast.LENGTH_LONG).show();
                return true;
            }
        } if (productPriceString.equals("")) {
            productPrice = 0.00;
        } else {
            productPrice = Double.parseDouble(editProductPrice.getText().toString());
        }
        // Creating a toast for showing errors
        Toast error;
        // Checking for errors, return true early if errors are found
        if(productName.equals("")) {
            error = Toast.makeText(this, getString(R.string.invalid_product_name), Toast.LENGTH_LONG);
            error.show();
            return true;
        } else if(supplierName.equals("")) {
            error = Toast.makeText(this,getString(R.string.invalid_supplier_name), Toast.LENGTH_LONG);
            error.show();
            return true;
        } else if(supplierPhone.equals("") || supplierPhone.length() < 6) {
            error = Toast.makeText(this, getString(R.string.invalid_supplier_phone), Toast.LENGTH_LONG);
            error.show();
            return true;
        }
        // Format the price to have only 2 decimal points
        productPrice = Math.round(productPrice * 100.0) / 100.0;
        // Return false if no errors found
        return false;
    }

    /** Method to insert a product into the Database */
    private void insertProduct() {
        // Creating a ContentValues object and adding keys and values to it
        ContentValues productValue = new ContentValues();
        productValue.put(ProductEntry.COLUMN_PRODUCT_NAME, productName);
        productValue.put(ProductEntry.COLUMN_PRODUCT_PRICE, productPrice);
        productValue.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, productQuantity);
        productValue.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME, supplierName);
        productValue.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE, supplierPhone);
        // Inserting the data into the table if this is a new product
        if(mCurrentProductUri == null) {
            Uri newUri = getContentResolver().insert(ProductEntry.PRODUCTS_CONTENT_URI, productValue);

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                Toast.makeText(this, getString(R.string.error_saving), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this,getString(R.string.product_saved), Toast.LENGTH_LONG).show();
            }
        }
        // Otherwise, this is an existing product, so update the pet with content URI : mCurrentProductUri
        else {
            int rowAffected = getContentResolver().update(mCurrentProductUri, productValue, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowAffected == 0) {
                Toast.makeText(this, getString(R.string.error_updating), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, getString(R.string.product_updated), Toast.LENGTH_LONG).show();
            }
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new product, hide the "Delete" menu item.
        if (mCurrentProductUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                if(!hasErrors()) {
                    insertProduct();
                    finish();
                }
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                if(!mProductHasChanged) {
                    // Navigate back to parent activity (CatalogActivity)
                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                }
                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int id) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };
                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        // If the product has not changed, continue with handling back button press
        if(!mProductHasChanged) {
            super.onBackPressed();
            return;
        }
        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };
        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor.
     *
     * @param discardButtonClickListener is the click listener for what to do when
     *                                   the user confirms they want to discard their changes
     */
    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.discard_changes_question));
        builder.setPositiveButton(getString(R.string.discard_option), discardButtonClickListener);
        builder.setNegativeButton(getString(R.string.keep_editing_option), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the product.
                if (dialogInterface != null) {
                    dialogInterface.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Prompt the user to confirm that they want to delete this pet.
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.delete_product_question));
        builder.setPositiveButton(getString(R.string.delete_option), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // User clicked the "Delete" button, so delete the product.
                deleteProduct();
            }
        });
        builder.setNegativeButton(getString(R.string.cancel_option), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the product.
                if (dialogInterface != null) {
                    dialogInterface.dismiss();
                }
            }
        });

        // Create and show the AlertDialog.
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /** Method to delete product */
    private void deleteProduct() {
        // Only perform the delete if this is an existing product.
        if (mCurrentProductUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentProductUri, null, null);

            if(rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.error_deleting), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, getString(R.string.product_deleted), Toast.LENGTH_LONG).show();
            }
            // Close the activity
            finish();
        }
    }

    @Override
    public android.content.Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Since the editor shows all products attributes, define a projection that contains
        // all columns from the products table
        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductEntry.COLUMN_PRODUCT_QUANTITY,
                ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME,
                ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE };

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,
                mCurrentProductUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of product info that we're interested in
            int productNameColIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
            int productPriceColIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
            int productQuantityColIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
            int supplierNameColIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME);
            int supplierPhoneColIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE);

            // Extract out the value from the Cursor for the given column index
            String productName = cursor.getString(productNameColIndex);
            float productPrice = cursor.getFloat(productPriceColIndex);
            int productQuantity = cursor.getInt(productQuantityColIndex);
            this.productQuantity = productQuantity;
            String supplierName = cursor.getString(supplierNameColIndex);
            final String supplierPhone = cursor.getString(supplierPhoneColIndex);

            // Update the views on the screen with the values from the database
            editProductName.setText(productName);
            editProductPrice.setText(Float.toString(productPrice));
            editProductQuantity.setText(Integer.toString(productQuantity));
            editSupplierName.setText(supplierName);
            editSupplierPhone.setText(supplierPhone);

            callSupplierTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String uri = "tel:" + supplierPhone.trim();
                    Intent call = new Intent(Intent.ACTION_DIAL);
                    call.setData(Uri.parse(uri));
                    startActivity(call);
                }
            });
        }
    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        editProductName.setText("");
        editProductPrice.setText("");
        editProductQuantity.setText("");
        editSupplierName.setText("");
        editSupplierPhone.setText("");
    }
}
