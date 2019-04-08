package com.payment.snappay.model;

import android.database.Cursor;
import android.util.Log;

import com.payment.snappay.data.TrxContract;

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
     * @param merchantID   of transaction
     */
    private TrxQrc(String timestamp, String amount, String productName, String merchantName, String merchantID) {
        super(timestamp, amount, productName, merchantName, merchantID);
    }

    /**
     * Build QRC Transaction object with product name
     *
     * @param amount       of transaction
     * @param merchantName of transaction
     * @param productName  of transaction
     * @param merchantID   of transaction
     * @return pay transaction object
     */
    public static TrxQrc build(String amount, String merchantName, String productName, String merchantID) {
        String timestamp =
                String.valueOf(System.currentTimeMillis());
        return new TrxQrc(timestamp, amount, productName, merchantName, merchantID);
    }

    /**
     * Build QRC Transaction object with productName
     *
     * @param timestamp    of transaction
     * @param amount       of transaction
     * @param merchantName of transaction
     * @param productName  of transaction
     * @param merchantID   of transaction
     * @return paid transaction object
     */
    public static TrxQrc build(String timestamp, String amount, String merchantName, String productName, String merchantID) {
        return new TrxQrc(timestamp, amount, productName, merchantName, merchantID);
    }

    /**
     * Build QRC transaction object by scanning QRC image
     *
     * @param stream from QRC image
     * @return QRC transaction object
     */
    public static TrxQrc buildFromQRCImage(String stream) {

        String merchantName = "";
        String merchantID = "";
        String product = "";
        String amount = "";

        String timestamp = String.valueOf(System.currentTimeMillis());

        try {

            merchantName = stream.split(":")[MERCHANT_NAME];
            merchantID = stream.split(":")[MERCHANT_ID];
            product = stream.split(":")[PRODUCT];
            amount = stream.split(":")[AMOUNT];

            Log.d(LOG_TAG, "merchant = " + merchantName +
                    ", product = " + product + ", amount = " + amount);

        } catch (ArrayIndexOutOfBoundsException e) {
            Log.d(LOG_TAG, e.getMessage());
        }

        return new TrxQrc(timestamp, amount, product, merchantName, merchantID);
    }

    /**
     * Build QRCode transaction object from cursor loaded from content provider
     *
     * @param cursor of transaction object
     * @return QRCode transaction object
     */
    public static TrxQrc buildFromCursor(Cursor cursor) {

        String timestamp = cursor.getString(cursor.getColumnIndex(TrxContract.TrxHistory.COLUMN_TIMESTAMP));
        String merchantName = cursor.getString(cursor.getColumnIndex(TrxContract.TrxHistory.COLUMN_MERCHANT_NAME));
        String merchantID = cursor.getString(cursor.getColumnIndex(TrxContract.TrxHistory.COLUMN_MERCHANT_ID));
        String product = cursor.getString(cursor.getColumnIndex(TrxContract.TrxHistory.COLUMN_PRODUCT));
        String amount = cursor.getString(cursor.getColumnIndex(TrxContract.TrxHistory.COLUMN_AMOUNT));

        return new TrxQrc(timestamp, amount, product, merchantName, merchantID);
    }

}