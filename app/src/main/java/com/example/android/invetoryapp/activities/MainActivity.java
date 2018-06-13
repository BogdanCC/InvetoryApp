package com.example.android.invetoryapp.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.invetoryapp.R;
import com.example.android.invetoryapp.data.ProductContract.ProductEntry;
import com.example.android.invetoryapp.data.ProductDbHelper;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionButton;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionHelper;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionLayout;
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RFACLabelItem;
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RapidFloatingActionContentLabelList;
import com.wangjie.rapidfloatingactionbutton.util.RFABTextUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements RapidFloatingActionContentLabelList.OnRapidFloatingActionContentLabelListListener {

    private RapidFloatingActionLayout rfaLayout;
    private RapidFloatingActionButton rfaBtn;
    private RapidFloatingActionHelper rfabHelper;

    private static final int ADD_PRODUCT_POSITION = 2;
    private static final int ADD_DUMMY_POSITION = 1;
    private static final int DELETE_PRODUCTS_POSITION = 0;

    private ProductDbHelper mDbHelper;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup the third party floating action buttons
        rfaLayout = findViewById(R.id.activity_main_rfal);
        rfaBtn = findViewById(R.id.activity_main_rfab);
        setupFloatingActionButtons();

        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity
        mDbHelper = new ProductDbHelper(this);
        displayDatabaseInfo();
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }

    /** Method to display some database information */
    private void displayDatabaseInfo() {
        Cursor cursor = queryData();
        try {
            TextView displayView = findViewById(R.id.display_view);
            TextView rowValues = findViewById(R.id.row_values);

            displayView.setText("Number of rows in products table : " + cursor.getCount() + "\n\n");
            // Get all the column names in an array
            String[] tableColumns = cursor.getColumnNames();
            // Make the array into a single string
            String tableColumnsString = Arrays.toString(tableColumns);
            // Format the array string
            tableColumnsString = tableColumnsString
                    .replace("[", "")
                    .replace("]", "")
                    .replace(", ", " - ");
            // Set the new string to the columnNames TextView
            displayView.append(tableColumnsString);

            // Initialise rowValues TextView to empty before the loop
            rowValues.setText("");

            // Find all the table columns indices now that we have the cursor
            int indexColumnId = cursor.getColumnIndex(ProductEntry._ID);
            int indexColumnName = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
            int indexColumnPrice = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
            int indexColumnQuantity = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
            int indexColumnSupplier = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME);
            int indexColumnSupplierPhone = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE);

            // Loop through all of the table rows indices
            // moveToNext() will return false when there are no more rows to go to
            while(cursor.moveToNext()) {

                // Get the column value at this row index, by passing in the column index
                int currentId = cursor.getInt(indexColumnId);
                String currentName = cursor.getString(indexColumnName);
                String currentPrice = cursor.getString(indexColumnPrice);
                String currentQuantity = cursor.getString(indexColumnQuantity);
                String currentSupplier = cursor.getString(indexColumnSupplier);
                String currentSupplierPhone = cursor.getString(indexColumnSupplierPhone);

                // Append the data to the empty rowValues TextView
                rowValues.append(currentId + " - " + currentName + " - " + currentPrice + " - " + currentQuantity
                        + " - " + currentSupplier + " - " + currentSupplierPhone + "\n");
            }

        } finally {
            cursor.close();
        }
    }

    /** Method to query database and return the cursor */
    private Cursor queryData() {
        // Create and/or open a database to read from it
        db = mDbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                ProductEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );
        // Return the resulted cursor
        return cursor;
    }
    @Override
    public void onRFACItemLabelClick(int position, RFACLabelItem item) {
        Toast.makeText(this, "clicked label: " + position, Toast.LENGTH_SHORT).show();
        rfabHelper.toggleContent();
    }

    @Override
    public void onRFACItemIconClick(int position, RFACLabelItem item) {
        switch(position) {
            case ADD_PRODUCT_POSITION:
                Intent i = new Intent(this, EditorActivity.class);
                startActivity(i);
                break;
            case ADD_DUMMY_POSITION:
                // Insert dummy data
                insertProducts();
                // Display db info again
                displayDatabaseInfo();
                break;
            case DELETE_PRODUCTS_POSITION:
                db.delete(ProductEntry.TABLE_NAME, null, null);
                displayDatabaseInfo();
                break;
        }
        rfabHelper.toggleContent();
    }

    /** Method to insert dummy data */
    private void insertProducts() {
        String productName = "New Item";
        double productPrice = 21.859522;
        // Format the price to have only 2 decimal points
        productPrice = Math.round(productPrice * 100.0) / 100.0;
        int productQuantity = 6;

        // Create an instance of ContentValues to help us with the data
        ContentValues productsValues = new ContentValues();
        // Add data as pairs of keys:values (column name and value)
        productsValues.put(ProductEntry.COLUMN_PRODUCT_NAME, productName);
        productsValues.put(ProductEntry.COLUMN_PRODUCT_PRICE, productPrice);
        productsValues.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, productQuantity);
        productsValues.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME, getString(R.string.supplier_one));
        productsValues.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE, ProductEntry.SUPPLIER_ONE_PHONE);
        // Insert the data into the database
        db = mDbHelper.getWritableDatabase();
        long newRowId = db.insert(ProductEntry.TABLE_NAME, null, productsValues);
        Toast rowId = Toast.makeText(this, "Added product with row id : " + newRowId, Toast.LENGTH_LONG);
        rowId.show();
    }

    /** Method to setup the floating action buttons */
    private void setupFloatingActionButtons() {
        RapidFloatingActionContentLabelList rfaContent = new RapidFloatingActionContentLabelList(this);
        rfaContent.setOnRapidFloatingActionContentLabelListListener(this);

        List<RFACLabelItem> items = new ArrayList<>();
        items.add(new RFACLabelItem<Integer>()
                .setLabel(getString(R.string.delete_all_products))
                .setResId(R.mipmap.remove_product_icon)
                .setIconNormalColor(getResources().getColor(R.color.delete_icon_color))
                .setIconPressedColor(getResources().getColor(R.color.delete_icon_color_pressed))
                .setLabelColor(getResources().getColor(R.color.delete_icon_color))
                .setWrapper(2)
        );
        items.add(new RFACLabelItem<Integer>()
                .setLabel(getString(R.string.add_dummy_data))
                .setResId(R.mipmap.add_dummy_data)
                .setIconNormalColor(getResources().getColor(R.color.add_dummy_color))
                .setIconPressedColor(getResources().getColor(R.color.add_dummy_color_pressed))
                .setLabelColor(getResources().getColor(R.color.add_dummy_color))
                .setWrapper(2)
        );
        items.add(new RFACLabelItem<Integer>()
                .setLabel(getString(R.string.add_product))
                .setResId(R.mipmap.add_product_icon)
                .setIconNormalColor(getResources().getColor(R.color.add_icon_color))
                .setIconPressedColor(getResources().getColor(R.color.add_icon_color_pressed))
                .setLabelColor(getResources().getColor(R.color.add_icon_color))
                .setWrapper(2)
        );

        rfaContent.setItems(items)
                .setIconShadowRadius(RFABTextUtil.dip2px(this, 5))
                .setIconShadowColor(0xff777777)
                .setIconShadowDy(RFABTextUtil.dip2px(this, 5));
        rfabHelper = new RapidFloatingActionHelper(
                this,
                rfaLayout,
                rfaBtn,
                rfaContent
        ).build();
    }
}
