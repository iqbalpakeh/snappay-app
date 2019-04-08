package com.payment.snappaymerchant.model;

import android.database.Cursor;
import android.util.Log;

import com.payment.snappaymerchant.data.TrxContract;

public class TrxNfc extends Trx {

    /**
     * Provide this class filter for debugging purpose
     */
    public static final String LOG_TAG = TrxNfc.class.getSimpleName();

    /**
     * The constructor
     *
     * @param timestamp    of transaction
     * @param amount       of transaction
     * @param productName  of transaction
     * @param merchantName of transaction
     */
    private TrxNfc(String timestamp, String amount, String productName, String merchantName) {
        super(timestamp, amount, productName, merchantName);
    }

    /**
     * Build NFC Transaction object with productName
     *
     * @param timestamp    of transaction
     * @param amount       of transaction
     * @param merchantName of transaction
     * @param productName  of transaction
     * @return paid transaction object
     */
    public static TrxNfc build(String timestamp, String amount, String merchantName, String productName) {
        return new TrxNfc(timestamp, amount, productName, merchantName);
    }

    /**
     * Build NFC transaction object by reading NFC TAG
     *
     * @param stream from NFC TAG
     * @return NFC transaction object
     */
    public static TrxNfc buildFromNfcTag(String stream) {

        String merchant = "";
        String product = "";
        String amount = "";

        String timestamp = String.valueOf(System.currentTimeMillis());

        try {

            merchant = stream.split((":"))[MERCHANT];
            product = stream.split(":")[PRODUCT];
            amount = stream.split(":")[AMOUNT];

            Log.d(LOG_TAG, "merchant = " + merchant +
                    ", product = " + product + ", amount = " + amount);

        } catch (ArrayIndexOutOfBoundsException e) {
            Log.d(LOG_TAG, e.getMessage());
        }

        return new TrxNfc(timestamp, amount, product, merchant);
    }

    /**
     * Build NFC transaction object from cursor loaded from content provider
     *
     * @param cursor of transaction object
     * @return NFC transaction object
     */
    public static TrxNfc buildFromCursor(Cursor cursor) {

        String timestamp = cursor.getString(cursor.getColumnIndex(TrxContract.TrxHistory.COLUMN_TIMESTAMP));
        String merchant = cursor.getString(cursor.getColumnIndex(TrxContract.TrxHistory.COLUMN_MERCHANT));
        String product = cursor.getString(cursor.getColumnIndex(TrxContract.TrxHistory.COLUMN_PRODUCT));
        String amount = cursor.getString(cursor.getColumnIndex(TrxContract.TrxHistory.COLUMN_AMOUNT));

        return new TrxNfc(timestamp, amount, product, merchant);
    }
}