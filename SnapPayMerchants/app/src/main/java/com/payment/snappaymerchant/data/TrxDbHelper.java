package com.payment.snappaymerchant.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TrxDbHelper extends SQLiteOpenHelper {

    /**
     *  Data base name
     */
    private static final String DATABASE_NAME = "transactions.db";

    /**
     *  Data base version
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructs a new instance of {@link TrxDbHelper}.
     *
     * @param context of the app
     */
    public TrxDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String SQL_CREATE_TRANSACTIONS_TABLE = "CREATE TABLE " + TrxContract.TrxHistory.TABLE_NAME + " ("
                + TrxContract.TrxHistory._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TrxContract.TrxHistory.COLUMN_TIMESTAMP + " TEXT NOT NULL, "
                + TrxContract.TrxHistory.COLUMN_AMOUNT + " TEXT NOT NULL, "
                + TrxContract.TrxHistory.COLUMN_PRODUCT + " TEXT NOT NULL, "
                + TrxContract.TrxHistory.COLUMN_MERCHANT + " TEXT NOT NULL, "
                + TrxContract.TrxHistory.COLUMN_TYPE + " TEXT NOT NULL);";

        sqLiteDatabase.execSQL(SQL_CREATE_TRANSACTIONS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // The database is still at version 1,
        // so there's nothing to do be done here.
    }

}
