package com.example.android.invetoryapp.adapters;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.invetoryapp.R;
import com.example.android.invetoryapp.data.ProductContract;

/**
 * {@link ProductCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of products data as its data source. This adapter knows
 * how to create list items for each row of products data in the {@link Cursor}.
 */
public class ProductCursorAdapter extends CursorAdapter{

    /**
     * Constructs a new {@link ProductCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the product data (in the current row pointed to by cursor) to the given
     * list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        TextView productNameTV = view.findViewById(R.id.product_name_tv);
        TextView productPriceTV = view.findViewById(R.id.product_price_tv);
        TextView productQuantityTV = view.findViewById(R.id.product_quantity_tv);
        final TextView productSellTV = view.findViewById(R.id.product_sell_button);

        // Find the columns of product info that we're interested in
        int productIdIndex = cursor.getColumnIndex(ProductContract.ProductEntry._ID);
        int productNameColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME);
        int productPriceColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE);
        int productQuantityColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY);

        // Read the product data from the Cursor for the current product
        String productName = cursor.getString(productNameColumnIndex);
        String productPrice = cursor.getString(productPriceColumnIndex);
        final int productQuantity = cursor.getInt(productQuantityColumnIndex);
        final int productId = cursor.getInt(productIdIndex);

        // Update the views
        productNameTV.setText(context.getString(R.string.product_name, productName));
        productPriceTV.setText(context.getString(R.string.product_price, productPrice));
        productQuantityTV.setText(context.getString(R.string.product_quantity, String.valueOf(productQuantity)));

        if(productQuantity == 0) {
            productSellTV.setTextColor(Color.GRAY);
        } else {
            productSellTV.setTextColor(context.getResources().getColor(R.color.add_icon_color));
        }
        productSellTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Create the URI for this product
                Uri thisProductUri = ContentUris.withAppendedId(ProductContract.ProductEntry.PRODUCTS_CONTENT_URI, productId);

                // If there is no product left, do nothing
                // Otherwise, subtract 1 from quantity and update product info
                if(productQuantity > 0) {
                    // Subtract one from product quantity
                    int newQuantity = productQuantity - 1;
                    // Create a new ContentValues object with the updated quantity
                    ContentValues updatedQuantity = new ContentValues();
                    updatedQuantity.put(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY, newQuantity);

                    // Update this product's quantity
                    context.getContentResolver().update(thisProductUri, updatedQuantity,
                            null, null);
                }
            }
        });
    }
}
