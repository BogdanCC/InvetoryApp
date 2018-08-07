package com.example.android.invetoryapp.activities;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.invetoryapp.R;
import com.example.android.invetoryapp.adapters.ProductCursorAdapter;
import com.example.android.invetoryapp.data.ProductContract.ProductEntry;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionButton;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionHelper;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionLayout;
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RFACLabelItem;
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RapidFloatingActionContentLabelList;
import com.wangjie.rapidfloatingactionbutton.util.RFABTextUtil;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements RapidFloatingActionContentLabelList.OnRapidFloatingActionContentLabelListListener,
        LoaderManager.LoaderCallbacks<Cursor>{

    /** Identifier for the product data loader */
    private static final int PRODUCT_LOADER = 0;

    /** Adapter for the ListView */
    ProductCursorAdapter mProductCursorAdapter;

    private RapidFloatingActionLayout rfaLayout;
    private RapidFloatingActionButton rfaBtn;
    private RapidFloatingActionHelper rfabHelper;

    private static final int ADD_PRODUCT_POSITION = 2;
    private static final int ADD_DUMMY_POSITION = 1;
    private static final int DELETE_PRODUCTS_POSITION = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find the ListView which will be populated with the products data
        ListView productListView = findViewById(R.id.product_lv);

        // Set a default empty view if no data in database
        View emptyView = findViewById(R.id.lv_empty_view);
        productListView.setEmptyView(emptyView);

        // Setup an Adapter to create a list item for each row of product data in the Cursor
        // There is no product data yet (until the loader finishes) so pass in null for the Cursor
        mProductCursorAdapter = new ProductCursorAdapter(this, null);
        productListView.setAdapter(mProductCursorAdapter);

        // Setup the item click listener
        productListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Create new intent to go to {@link EditorActivity}
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);

                // Form the content URI that represents the specific product that was clicked on,
                // by appending the "id" (passed as input to this method) onto the
                // {@link ProductEntry#CONTENT_URI}.
                Uri currentProductUri = ContentUris.withAppendedId(ProductEntry.PRODUCTS_CONTENT_URI, id);

                // Set the URI on teh data field of the intent
                intent.setData(currentProductUri);

                // Launch the {@link EditorActivity} to display the data for the current product.
                startActivity(intent);
            }
        });

        // Kick off the Cursor Loader
        getLoaderManager().initLoader(PRODUCT_LOADER, null, this);

        // Setup the third party floating action buttons
        rfaLayout = findViewById(R.id.activity_main_rfal);
        rfaBtn = findViewById(R.id.activity_main_rfab);
        setupFloatingActionButtons();

    }

    /** Method to insert dummy data */
    private void insertProducts() {
        String productName = getString(R.string.dummy_product_name);
        String supplierPhone = getString(R.string.dummy_supplier_phone);
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
        productsValues.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME, getString(R.string.dummy_supplier_name));
        productsValues.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE, supplierPhone);

        Uri newRowUri = getContentResolver().insert(ProductEntry.PRODUCTS_CONTENT_URI, productsValues);

    }

    @Override
    public void onRFACItemLabelClick(int position, RFACLabelItem item) {
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
                break;
            case DELETE_PRODUCTS_POSITION:
                showDeleteConfirmationDialog();
                break;
        }
        rfabHelper.toggleContent();
    }
    /** Method to show a deletion dialog before deleting all data */
    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.delete_all_products_question));
        builder.setPositiveButton(getString(R.string.delete_option), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // User clicked on "Delete", so delete all products
                deleteProducts();
            }
        });
        builder.setNegativeButton(getString(R.string.cancel_option), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // User clicked on "Cancel", so dismiss the dialog
                if (dialogInterface != null) {
                    dialogInterface.dismiss();
                }
            }
        });
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /** Method to delete all products */
    private void deleteProducts() {
        getContentResolver().delete(ProductEntry.PRODUCTS_CONTENT_URI, null, null);
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

    /** Overriding the Loader methods */
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        // Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductEntry.COLUMN_PRODUCT_QUANTITY
        };

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(
                this,
                ProductEntry.PRODUCTS_CONTENT_URI,
                projection,
                null,
                null,
                null );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Update {@link ProductCursorAdapter} with this new cursor containing updated product data
        mProductCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        mProductCursorAdapter.swapCursor(null);
    }
}
