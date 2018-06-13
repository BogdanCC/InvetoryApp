package com.example.android.invetoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.android.invetoryapp.data.ProductContract.ProductEntry;

public class ProductDbHelper extends SQLiteOpenHelper {

    public static final StringBuilder sqlBuilder = new StringBuilder();

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    // Creating Strings for SQL statement
    public static final String DATABASE_NAME = "Products.db";
    public static final String PRIMARY_KEY_AI = " INTEGER PRIMARY KEY AUTOINCREMENT";
    public static final String CREATE_TABLE = "CREATE TABLE ";
    public static final String TEXT_TYPE = " TEXT";
    public static final String INTEGER_TYPE = " INTEGER";
    public static final String DECIMAL_TYPE = " DECIMAL(10,2)";
    public static final String NOT_NULL = " NOT NULL";
    public static final String DEFAULT = " DEFAULT ";
    public static final String OPEN_PARANTHESES = "(";
    public static final String CLOSE_PARANTHESES = ")";
    public static final String COMMA_SEP = ", ";

    // Using a StringBuilder to create our statement
    public static final String createTableStatement() {
        sqlBuilder.append(CREATE_TABLE).append(ProductEntry.TABLE_NAME).append(OPEN_PARANTHESES).append(ProductEntry._ID).append(PRIMARY_KEY_AI)
                .append(COMMA_SEP).append(ProductEntry.COLUMN_PRODUCT_NAME).append(TEXT_TYPE).append(NOT_NULL).append(COMMA_SEP)
                .append(ProductEntry.COLUMN_PRODUCT_PRICE).append(DECIMAL_TYPE).append(NOT_NULL).append(DEFAULT).append(0.00)
                .append(COMMA_SEP).append(ProductEntry.COLUMN_PRODUCT_QUANTITY).append(INTEGER_TYPE).append(NOT_NULL).append(DEFAULT)
                .append(0).append(COMMA_SEP).append(ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME).append(TEXT_TYPE).append(NOT_NULL)
                .append(DEFAULT).append("Unknown").append(COMMA_SEP).append(ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE)
                .append(TEXT_TYPE).append(NOT_NULL).append(CLOSE_PARANTHESES).append(";");
        return sqlBuilder.toString();
    }

    public static final String SQL_CREATE_ENTRIES = createTableStatement();

    public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + ProductEntry.TABLE_NAME;

    public ProductDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}
