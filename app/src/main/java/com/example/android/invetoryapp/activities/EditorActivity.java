package com.example.android.invetoryapp.activities;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.invetoryapp.R;
import com.example.android.invetoryapp.data.ProductContract.ProductEntry;
import com.example.android.invetoryapp.data.ProductDbHelper;

public class EditorActivity extends AppCompatActivity {

    private Spinner supplierSpinner;
    private EditText editProductName;
    private EditText editProductPrice;
    private EditText editProductQuantity;
    private EditText editSupplierName;
    private EditText editSupplierPhone;
    private String supplierName;
    private String supplierPhone;
    private String productName;
    private double productPrice;
    private int productQuantity;

    ProductDbHelper productDbHelper;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        // Getting all edit texts
        supplierSpinner = findViewById(R.id.spinner_supplier);
        editSupplierName = findViewById(R.id.edit_supplier_name);
        editSupplierPhone = findViewById(R.id.edit_supplier_phone);
        editProductName = findViewById(R.id.edit_product_name);
        editProductPrice = findViewById(R.id.edit_product_price);
        editProductQuantity = findViewById(R.id.edit_product_quantity);
        // Initialising database
        productDbHelper = new ProductDbHelper(this);
        // Setting up the spinner
        setupSpinner();
    }

    /** Setup the dropdown spinner that allows the user to select the supplier */
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout

        ArrayAdapter supplierSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_supplier_options, android.R.layout.simple_spinner_item);
        // Specify dropdown layout style - simple list view with 1 item per line
        supplierSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        supplierSpinner.setAdapter(supplierSpinnerAdapter);

        supplierSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selection = (String) adapterView.getItemAtPosition(i);
                if(!TextUtils.isEmpty(selection)) {
                    if(selection.equals(getString(R.string.supplier_new))) {
                        supplierName = "";
                        supplierPhone = "";
                        editSupplierName.setVisibility(View.VISIBLE);
                        editSupplierPhone.setVisibility(View.VISIBLE);
                    } else {
                        editSupplierName.setVisibility(View.GONE);
                        editSupplierPhone.setVisibility(View.GONE);
                    }
                    if(selection.equals(getString(R.string.supplier_one))) {
                        supplierName = getString(R.string.supplier_one);
                        supplierPhone = ProductEntry.SUPPLIER_ONE_PHONE;
                    } else if(selection.equals(getString(R.string.supplier_two))) {
                        supplierName = getString(R.string.supplier_two);
                        supplierPhone = ProductEntry.SUPPLIER_TWO_PHONE;
                    } else if(selection.equals(getString(R.string.supplier_three))) {
                        supplierName = getString(R.string.supplier_three);
                        supplierPhone = ProductEntry.SUPPLIER_THREE_PHONE;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                supplierName = getString(R.string.supplier_one);
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
        if(editSupplierPhone.getVisibility() == View.VISIBLE && editSupplierName.getVisibility() == View.VISIBLE) {
            supplierName = editSupplierName.getText().toString().trim();
            supplierPhone = editSupplierPhone.getText().toString().trim();
        }
        if(productQuantityString.equals("")) {
            productQuantity = 0;
        } else {
            productQuantity = Integer.parseInt(editProductQuantity.getText().toString().trim());
        } if (productPriceString.equals("")) {
            productPrice = 0.00;
        } else {
            productPrice = Double.parseDouble(editProductPrice.getText().toString());
        }
        // Creating a toast for showing errors
        Toast error;
        // Checking for errors, return true early if errors are found
        if(productName.equals("")) {
            error = Toast.makeText(this, "Please enter a valid product name.", Toast.LENGTH_LONG);
            error.show();
            return true;
        } else if(supplierName.equals("")) {
            error = Toast.makeText(this,"Please enter a valid supplier name.", Toast.LENGTH_LONG);
            error.show();
            return true;
        } else if(supplierPhone.equals("") || supplierPhone.length() < 6) {
            error = Toast.makeText(this, "Please enter a valid phone number.", Toast.LENGTH_LONG);
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
        // Getting the writable database to INSERT data
        db = productDbHelper.getWritableDatabase();
        // Creating a ContentValues object and adding keys and values to it
        ContentValues productValue = new ContentValues();
        productValue.put(ProductEntry.COLUMN_PRODUCT_NAME, productName);
        productValue.put(ProductEntry.COLUMN_PRODUCT_PRICE, productPrice);
        productValue.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, productQuantity);
        productValue.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME, supplierName);
        productValue.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE, supplierPhone);
        // Inserting the data into the table
        long productRowId = db.insert(ProductEntry.TABLE_NAME, null, productValue);
        Toast toast = Toast.makeText(this, "Product created with id : " + productRowId, Toast.LENGTH_LONG);
        toast.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
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
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
