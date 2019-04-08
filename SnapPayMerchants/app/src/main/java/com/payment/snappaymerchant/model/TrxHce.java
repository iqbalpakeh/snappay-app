package com.payment.snappaymerchant.model;

import android.database.Cursor;
import android.util.Log;

import com.payment.snappaymerchant.data.TrxContract;

public class TrxHce extends Trx {

    /**
     * Provide this class filter for debugging purpose
     */
    public static final String LOG_TAG = TrxHce.class.getSimpleName();

    /**
     * The constructor
     *
     * @param timestamp    of transaction
     * @param amount       of transaction
     * @param productName  of transaction
     * @param merchantName of transaction
     */
    private TrxHce(String timestamp, String amount, String productName, String merchantName) {
        super(timestamp, amount, productName, merchantName);
    }

    /**
     * Build HCE Transaction object with productName
     *
     * @param timestamp    of transaction
     * @param amount       of transaction
     * @param merchantName of transaction
     * @param productName  of transaction
     * @return paid transaction object
     */
    public static TrxHce build(String timestamp, String amount, String merchantName, String productName) {
        return new TrxHce(timestamp, amount, productName, merchantName);
    }

    /**
     * Build HCE transaction object by reading Smartcard emulated device
     *
     * @param stream from Smartcard emulated device
     * @return HCE transaction object
     */
    public static TrxHce buildFromSelectResponse(String stream) {

        String merchantName = "";
        String merchantID = "";
        String product = "";
        String amount = "";

        String timestamp = String.valueOf(System.currentTimeMillis());

        try {

            merchantName = stream.split((":"))[MERCHANT];
            product = stream.split(":")[PRODUCT];
            amount = stream.split(":")[AMOUNT];

            Log.d(LOG_TAG, "merchant name = " + merchantName + ", merchant id = " + merchantID +
                    ", product = " + product + ", amount = " + amount);

        } catch (ArrayIndexOutOfBoundsException e) {
            Log.d(LOG_TAG, e.getMessage());
        }

        return new TrxHce(timestamp, amount, product, merchantName);
    }

    /**
     * Build HCE transaction object from cursor loaded from content provider
     *
     * @param cursor of transaction object
     * @return HCE transaction object
     */
    public static TrxHce buildFromCursor(Cursor cursor) {

        String timestamp = cursor.getString(cursor.getColumnIndex(TrxContract.TrxHistory.COLUMN_TIMESTAMP));
        String merchant = cursor.getString(cursor.getColumnIndex(TrxContract.TrxHistory.COLUMN_MERCHANT));
        String product = cursor.getString(cursor.getColumnIndex(TrxContract.TrxHistory.COLUMN_PRODUCT));
        String amount = cursor.getString(cursor.getColumnIndex(TrxContract.TrxHistory.COLUMN_AMOUNT));

        return new TrxHce(timestamp, amount, product, merchant);
    }

}
