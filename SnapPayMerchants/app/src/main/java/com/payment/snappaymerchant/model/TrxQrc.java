package com.payment.snappaymerchant.model;

import android.database.Cursor;
import android.util.Log;

import com.payment.snappaymerchant.data.TrxContract;

public class TrxQrc extends Trx {

    /**
     * Provide this class filter for debugging purpose
     */
    public static final String LOG_TAG = TrxQrc.class.getSimpleName();

    /**
     * The constructor
     *
     * @param timestamp    of transaction
     * @param amount       of transaction
     * @param productName  of transaction
     * @param merchantName of transaction
     */
    private TrxQrc(String timestamp, String amount, String productName, String merchantName) {
        super(timestamp, amount, productName, merchantName);
    }

    /**
     * Build QRC Transaction object with product name
     *
     * @param timestamp    of transaction
     * @param amount       of transaction
     * @param merchantName of transaction
     * @param productName  of transaction
     * @return pay transaction object
     */
    public static TrxQrc build(String timestamp, String amount, String merchantName, String productName) {
        return new TrxQrc(timestamp, amount, productName, merchantName);
    }

    /**
     * Build QRC transaction object by scanning QRC image
     *
     * @param stream from QRC image
     * @return QRC transaction object
     */
    public static TrxQrc buildFromQRCImage(String stream) {

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

        return new TrxQrc(timestamp, amount, product, merchant);
    }

    /**
     * Build QRCode transaction object from cursor loaded from content provider
     *
     * @param cursor of transaction object
     * @return QRCode transaction object
     */
    public static TrxQrc buildFromCursor(Cursor cursor) {

        String timestamp = cursor.getString(cursor.getColumnIndex(TrxContract.TrxHistory.COLUMN_TIMESTAMP));
        String merchant = cursor.getString(cursor.getColumnIndex(TrxContract.TrxHistory.COLUMN_MERCHANT));
        String product = cursor.getString(cursor.getColumnIndex(TrxContract.TrxHistory.COLUMN_PRODUCT));
        String amount = cursor.getString(cursor.getColumnIndex(TrxContract.TrxHistory.COLUMN_AMOUNT));

        return new TrxQrc(timestamp, amount, product, merchant);
    }

}