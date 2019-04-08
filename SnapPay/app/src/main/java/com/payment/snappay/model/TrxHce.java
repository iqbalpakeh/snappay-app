package com.payment.snappay.model;

import android.database.Cursor;
import android.util.Log;

import com.payment.snappay.data.TrxContract;

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
     * @param merchantID   of transaction
     */
    private TrxHce(String timestamp, String amount, String productName, String merchantName, String merchantID) {
        super(timestamp, amount, productName, merchantName, merchantID);
    }

    /**
     * Build HCE Transaction object with productName
     *
     * @param amount       of transaction
     * @param merchantName of transaction
     * @param productName  of transaction
     * @param merchantID   of transaction
     * @return paid transaction object
     */
    public static TrxHce build(String amount, String merchantName, String productName, String merchantID) {
        String timestamp =
                String.valueOf(System.currentTimeMillis());
        return new TrxHce(timestamp, amount, productName, merchantName, merchantID);
    }

    /**
     * Build HCE Transaction object with productName
     *
     * @param timestamp    of transaction
     * @param amount       of transaction
     * @param merchantName of transaction
     * @param productName  of transaction
     * @param merchantID   of transaction
     * @return paid transaction object
     */
    public static TrxHce build(String timestamp, String amount, String merchantName, String productName, String merchantID) {
        return new TrxHce(timestamp, amount, productName, merchantName, merchantID);
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

            merchantName = stream.split((":"))[MERCHANT_NAME];
            merchantID = stream.split(":")[MERCHANT_ID];
            product = stream.split(":")[PRODUCT];
            amount = stream.split(":")[AMOUNT];

            Log.d(LOG_TAG, "merchant name = " + merchantName + ", merchant id = " + merchantID +
                    ", product = " + product + ", amount = " + amount);

        } catch (ArrayIndexOutOfBoundsException e) {
            Log.d(LOG_TAG, e.getMessage());
        }

        return new TrxHce(timestamp, amount, product, merchantName, merchantID);
    }

    /**
     * Build HCE transaction object from cursor loaded from content provider
     *
     * @param cursor of transaction object
     * @return HCE transaction object
     */
    public static TrxHce buildFromCursor(Cursor cursor) {

        String timestamp = cursor.getString(cursor.getColumnIndex(TrxContract.TrxHistory.COLUMN_TIMESTAMP));
        String merchantName = cursor.getString(cursor.getColumnIndex(TrxContract.TrxHistory.COLUMN_MERCHANT_NAME));
        String merchantID = cursor.getString(cursor.getColumnIndex(TrxContract.TrxHistory.COLUMN_MERCHANT_ID));
        String product = cursor.getString(cursor.getColumnIndex(TrxContract.TrxHistory.COLUMN_PRODUCT));
        String amount = cursor.getString(cursor.getColumnIndex(TrxContract.TrxHistory.COLUMN_AMOUNT));

        return new TrxHce(timestamp, amount, product, merchantName, merchantID);
    }

}
