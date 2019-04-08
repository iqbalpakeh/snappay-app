package com.payment.snappaymerchant.firebase;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.payment.snappaymerchant.AppSharedPref;
import com.payment.snappaymerchant.data.TrxContract;
import com.payment.snappaymerchant.model.TrxHce;
import com.payment.snappaymerchant.model.TrxNfc;
import com.payment.snappaymerchant.model.TrxQrc;

public class FbRetrieveTrx extends FbContract {

    /**
     * for debugging purpose
     */
    private static final String LOG_TAG = FbRetrieveTrx.class.getSimpleName();

    /**
     * Interface to be implemented
     */
    public interface FbRetrieveTrxAble {

        /**
         * Call back when the transaction is completely retrieved
         */
        void onRetrieveTrxSuccess();

        /**
         * Call back when the transaction retrieval is failed
         */
        void onRetrieveTrxFailed();
    }

    /**
     * Interface to be implemented in Activity class
     */
    private FbRetrieveTrxAble mInterface;

    /**
     * Reference to transaction history location
     */
    private DatabaseReference mTrxHistoryRef;

    /**
     * Listener of transaction history db
     */
    private ValueEventListener mListener;

    /**
     * The constructor
     *
     * @param context of application
     * @param anInterface to be implemented
     */
    private FbRetrieveTrx(Context context, FbRetrieveTrxAble anInterface) {
        this.mInterface = anInterface;
        this.mContext = context;

        String merchantId = AppSharedPref.getUID(mContext);
        mTrxHistoryRef = mDatabase.child(FbContract.ROOT_MERCHANT).child(merchantId).child("transactions");

    }

    /**
     * Build FbRetrieveTrx object
     *
     * @param context of application
     * @return FbRetrieveTrx object
     */
    public static FbRetrieveTrx build(Context context, FbRetrieveTrxAble anInterface) {
        return new FbRetrieveTrx(context, anInterface);
    }

    /**
     * Retrieve transaction history and store to the cache. Refresh list view only if there's
     * new data created.
     */
    public void retrieveTrxHistory() {

        showProgress(true);

        mListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mContext.getContentResolver().delete(TrxContract.TrxHistory.CONTENT_URI, null, null);

                for(DataSnapshot children : dataSnapshot.getChildren()) {

                    String type = children.child("trx_type").getValue().toString();
                    String amount = children.child("amount").getValue().toString();
                    String merchantName = children.child("merchantName").getValue().toString();
                    String productName = children.child("productName").getValue().toString();
                    String timestamp = children.child("timestamp").getValue().toString();

                    if (type.equals("NFC")) {

                        TrxNfc trxNfc = TrxNfc.build(timestamp, amount, merchantName, productName);
                        ContentValues values = new ContentValues();

                        values.put(TrxContract.TrxHistory.COLUMN_TIMESTAMP, trxNfc.getTimestamp());
                        values.put(TrxContract.TrxHistory.COLUMN_AMOUNT, trxNfc.getAmount());
                        values.put(TrxContract.TrxHistory.COLUMN_PRODUCT, trxNfc.getProductName());
                        values.put(TrxContract.TrxHistory.COLUMN_MERCHANT, trxNfc.getMerchantName());
                        values.put(TrxContract.TrxHistory.COLUMN_TYPE, TrxContract.TrxHistory.TRX_NFC);

                        mContext.getContentResolver().insert(TrxContract.TrxHistory.CONTENT_URI, values);

                    } else if (type.equals("QRC")) {

                        TrxQrc trxQrc = TrxQrc.build(timestamp, amount, merchantName, productName);
                        ContentValues values = new ContentValues();

                        values.put(TrxContract.TrxHistory.COLUMN_TIMESTAMP, trxQrc.getTimestamp());
                        values.put(TrxContract.TrxHistory.COLUMN_AMOUNT, trxQrc.getAmount());
                        values.put(TrxContract.TrxHistory.COLUMN_PRODUCT, trxQrc.getProductName());
                        values.put(TrxContract.TrxHistory.COLUMN_MERCHANT, trxQrc.getMerchantName());
                        values.put(TrxContract.TrxHistory.COLUMN_TYPE, TrxContract.TrxHistory.TRX_QRC);

                        mContext.getContentResolver().insert(TrxContract.TrxHistory.CONTENT_URI, values);

                    } else {

                        TrxHce trxHce = TrxHce.build(timestamp, amount, merchantName, productName);
                        ContentValues values = new ContentValues();

                        values.put(TrxContract.TrxHistory.COLUMN_TIMESTAMP, trxHce.getTimestamp());
                        values.put(TrxContract.TrxHistory.COLUMN_AMOUNT, trxHce.getAmount());
                        values.put(TrxContract.TrxHistory.COLUMN_PRODUCT, trxHce.getProductName());
                        values.put(TrxContract.TrxHistory.COLUMN_MERCHANT, trxHce.getMerchantName());
                        values.put(TrxContract.TrxHistory.COLUMN_TYPE, TrxContract.TrxHistory.TRX_HCE);

                        mContext.getContentResolver().insert(TrxContract.TrxHistory.CONTENT_URI, values);

                    }

                }

                showProgress(false);
                mInterface.onRetrieveTrxSuccess();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(LOG_TAG, "Error: " + databaseError);
                mInterface.onRetrieveTrxFailed();
            }
        };

        mTrxHistoryRef.addValueEventListener(mListener);

    }

    /**
     * Add listener to firebase transaction
     */
    public void addListener() {
        mTrxHistoryRef.addValueEventListener(mListener);
    }

    /**
     * Remove authentication listener from firebase authentication object
     */
    public void removeListener() {
        if (mListener != null) {
            mTrxHistoryRef.removeEventListener(mListener);
        }
    }

}
