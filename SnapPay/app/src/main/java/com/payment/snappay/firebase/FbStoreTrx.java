package com.payment.snappay.firebase;

import android.content.Context;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.payment.snappay.AppSharedPref;
import com.payment.snappay.model.Trx;
import com.payment.snappay.model.TrxNfc;
import com.payment.snappay.model.TrxQrc;

public class FbStoreTrx extends FbContract {

    /**
     * for debugging purpose
     */
    private static final String LOG_TAG = FbUserAuth.class.getSimpleName();

    /**
     * Interface to be implemented
     */
    public interface FbStoreTrxAble {

        /**
         * Call back when the transaction is completely stored
         *
         * @param trx object of transaction
         */
        void onStoreTrxSuccess(Trx trx);

        /**
         * Call back when the transaction is failed
         */
        void onStoreTrxFailed();
    }

    /**
     * Interface to be implemented in Activity class
     */
    private FbStoreTrxAble mInterface;

    /**
     * Constructor of FbStoreTrx
     *
     * @param context     of application
     * @param anInterface of firebase store transaction
     */
    private FbStoreTrx(Context context, FbStoreTrxAble anInterface) {
        this.mContext = context;
        this.mInterface = anInterface;
    }

    /**
     * Build FbStoreTrx object
     *
     * @param context     of application
     * @param anInterface of firebase store transaction
     * @return
     */
    public static FbStoreTrx build(Context context, FbStoreTrxAble anInterface) {
        return new FbStoreTrx(context, anInterface);
    }

    /**
     * Store transaction data to firebase db
     *
     * @param trx object of transaction
     */
    public void storeTrxToFb(final Trx trx) {

        showProgress(true);

        final String merchantID = trx.getMerchantID();
        final String key = mDatabase.child(ROOT_MERCHANT).child(merchantID).child("transactions").push().getKey();

        mDatabase.child(ROOT_MERCHANT).child(merchantID).child("transactions").child(key)
                .runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {

                        mutableData.setValue(trx);

                        if (trx instanceof TrxNfc) {
                            mutableData.child("trx_type").setValue("NFC");
                        } else if (trx instanceof TrxQrc) {
                            mutableData.child("trx_type").setValue("QRC");
                        } else {
                            mutableData.child("trx_type").setValue("HCE");
                        }

                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {

                        if (committed) {

                            DatabaseReference trxLatestCacheRef = mDatabase.child(ROOT_MERCHANT).child(merchantID).child("transactions_latest");

                            trxLatestCacheRef.runTransaction(new Transaction.Handler() {
                                @Override
                                public Transaction.Result doTransaction(MutableData mutableData) {

                                    mutableData.setValue(trx);
                                    mutableData.child("trx_key").setValue(key);

                                    if (trx instanceof TrxNfc) {
                                        mutableData.child("trx_type").setValue("NFC");
                                    } else if (trx instanceof TrxQrc) {
                                        mutableData.child("trx_type").setValue("QRC");
                                    } else {
                                        mutableData.child("trx_type").setValue("HCE");
                                    }

                                    return Transaction.success(mutableData);
                                }

                                @Override
                                public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {

                                    if (committed) {

                                        String uid = AppSharedPref.getUID(mContext);
                                        String key = mDatabase.child(ROOT_CONSUMER).child(uid).child("transactions").push().getKey();
                                        mDatabase.child(ROOT_CONSUMER).child(uid).child("transactions").child(key).runTransaction(new Transaction.Handler() {
                                            @Override
                                            public Transaction.Result doTransaction(MutableData mutableData) {

                                                mutableData.setValue(trx);

                                                if (trx instanceof TrxNfc) {
                                                    mutableData.child("trx_type").setValue("NFC");
                                                } else if (trx instanceof TrxQrc) {
                                                    mutableData.child("trx_type").setValue("QRC");
                                                } else {
                                                    mutableData.child("trx_type").setValue("HCE");
                                                }

                                                return Transaction.success(mutableData);
                                            }

                                            @Override
                                            public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
                                                if (committed) {
                                                    showProgress(false);
                                                    mInterface.onStoreTrxSuccess(trx);
                                                } else {
                                                    Log.d(LOG_TAG, "storeTrxToFb:failed" + databaseError.getDetails());
                                                    mInterface.onStoreTrxFailed();
                                                }
                                            }
                                        });

                                    } else {
                                        Log.d(LOG_TAG, "storeTrxToFb:failed" + databaseError.getDetails());
                                        mInterface.onStoreTrxFailed();
                                    }
                                }
                            });

                        } else {

                            showProgress(true);
                            Log.d(LOG_TAG, "storeTrxToFb:failed" + databaseError.getDetails());
                            mInterface.onStoreTrxFailed();
                        }
                    }
                });
    }

}
