package com.example.android.invetoryapp.data;

import android.provider.BaseColumns;

public final class ProductContract {
    /** Preventing an accidental instance of this class */
    private ProductContract(){}

    public static abstract class ProductEntry implements BaseColumns {

        /** The name of the table */
        public static final String TABLE_NAME = "products";

        /** The table's columns */
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_PRODUCT_NAME = "name";
        public static final String COLUMN_PRODUCT_PRICE = "price";
        public static final String COLUMN_PRODUCT_QUANTITY = "quantity";
        public static final String COLUMN_PRODUCT_SUPPLIER_NAME = "supplierName";
        public static final String COLUMN_PRODUCT_SUPPLIER_PHONE = "supplierPhone";

        /** Possible values for suppliers */
        public static final String SUPPLIER_ONE_PHONE = "123456789";
        public static final String SUPPLIER_TWO_PHONE = "123123123";
        public static final String SUPPLIER_THREE_PHONE = "987654321";
    }
}
