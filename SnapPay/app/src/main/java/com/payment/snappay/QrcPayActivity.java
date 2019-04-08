package com.payment.snappay;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.payment.snappay.data.TrxContract;
import com.payment.snappay.firebase.FbStoreTrx;
import com.payment.snappay.model.Trx;
import com.payment.snappay.model.TrxQrc;

public class QrcPayActivity extends AppCompatActivity implements FbStoreTrx.FbStoreTrxAble {

    /**
     * Provide this class filter for debugging purpose
     */
    private static final String LOG_TAG = QrcPayActivity.class.getSimpleName();

    /**
     * Dummy finger print image handling long click
     * to simulate finger print scanner
     */
    private ImageView mFingerPrint;

    /**
     * Temporary object of QRC transaction because the final amount should be taken
     * from QrcPayDetailFragment in case user change the amount.
     */
    private TrxQrc mTrxQrc;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrc);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

        String qrc = getIntent().getStringExtra(TrxQrc.LOG_TAG);
        mTrxQrc = TrxQrc.buildFromQRCImage(qrc);

        Log.d(LOG_TAG, "product = " + mTrxQrc.getProductName() +
                ", vendor = " + mTrxQrc.getMerchantName() + ", amount = " + mTrxQrc.getAmount());

        Bundle bundle = new Bundle();
        bundle.putParcelable(TrxQrc.LOG_TAG, mTrxQrc);
        inflateQrcPayDetailFragment(bundle);

        mFingerPrint = (ImageView) findViewById(R.id.finger_print);
        mFingerPrint.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                storeTransaction();
                return true;
            }
        });
    }

    /**
     * Store final Qrc Transaction object to server and wait for the result
     */
    private void storeTransaction() {

        String amount = ((EditText) findViewById(R.id.trx_amount)).getText().toString();
        String merchantName = mTrxQrc.getMerchantName();
        String productName = mTrxQrc.getProductName();
        String merchantID = mTrxQrc.getMerchantID();

        if (amount.equals("")) {
            Toast.makeText(this, "Please fill in transaction amount", Toast.LENGTH_SHORT).show();

        } else {
            mFingerPrint.setOnLongClickListener(null);
            mFingerPrint.setImageDrawable(getResources().getDrawable(R.drawable.ic_fingerprint_disable_56dp));

            TrxQrc updatedTrxQrc = TrxQrc.build(amount, merchantName, productName, merchantID);
            FbStoreTrx.build(this, this).storeTrxToFb(updatedTrxQrc);
        }
    }

    @Override
    public void onStoreTrxSuccess(Trx trx) {

        TrxQrc trxQrc = (TrxQrc) trx;
        Log.d(LOG_TAG, "@onApprovedTrx(): product = " + trxQrc.getProductName() +
                ", vendor = " + trxQrc.getMerchantName() + ", amount = " + trxQrc.getAmount());

        ContentValues values = new ContentValues();
        values.put(TrxContract.TrxHistory.COLUMN_TIMESTAMP, trxQrc.getTimestamp());
        values.put(TrxContract.TrxHistory.COLUMN_AMOUNT, trxQrc.getAmount());
        values.put(TrxContract.TrxHistory.COLUMN_PRODUCT, trxQrc.getProductName());
        values.put(TrxContract.TrxHistory.COLUMN_MERCHANT_NAME, trxQrc.getMerchantName());
        values.put(TrxContract.TrxHistory.COLUMN_MERCHANT_ID, trxQrc.getMerchantID());
        values.put(TrxContract.TrxHistory.COLUMN_TYPE, TrxContract.TrxHistory.TRX_QRC);

        getContentResolver().insert(TrxContract.TrxHistory.CONTENT_URI, values);

        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                .replace(R.id.qrc_pay_container, new QrcPayOutcomeFragment(), QrcPayOutcomeFragment.LOG_TAG)
                .commit();
    }

    @Override
    public void onStoreTrxFailed() {
        Log.d(LOG_TAG, "onStoreTrxFailed");
        Toast.makeText(this, "Transaction failed...", Toast.LENGTH_SHORT).show();
    }

    /**
     * Inflate QRC Pay Detail Fragment showing the details of transaction
     *
     * @param bundle of trx details
     */
    private void inflateQrcPayDetailFragment(Bundle bundle) {

        QrcPayDetailFragment fragment = new QrcPayDetailFragment();
        fragment.setArguments(bundle);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.add(R.id.qrc_pay_container, fragment, QrcPayDetailFragment.LOG_TAG);
        fragmentTransaction.commit();
    }

}
