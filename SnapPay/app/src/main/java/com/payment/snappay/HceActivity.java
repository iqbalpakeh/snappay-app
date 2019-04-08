package com.payment.snappay;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.payment.snappay.data.TrxContract;
import com.payment.snappay.firebase.FbStoreTrx;
import com.payment.snappay.model.Trx;
import com.payment.snappay.model.TrxHce;

public class HceActivity extends AppCompatActivity
        implements HcePayContainerFragment.OnFingerPrintListener, FbStoreTrx.FbStoreTrxAble {

    /**
     * Provide this class filter for debugging purpose
     */
    private static final String LOG_TAG = HceActivity.class.getSimpleName();

    /**
     * Handles all about fragment transaction
     */
    private FragmentManager mFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hce);

        mFragmentManager = getSupportFragmentManager();

        inflateHceScanFragment();
    }

    @Override
    public void onFingerPrintScanned() {
        Log.d(LOG_TAG, "@onFingerPrintScanned()");

        String amount = ((EditText) findViewById(R.id.trx_amount)).getText().toString();
        String merchantName = ((TextView) findViewById(R.id.merchant_name)).getText().toString();
        String productName = ((TextView) findViewById(R.id.product_name)).getText().toString();
        String merchantID = ((TextView) findViewById(R.id.merchant_id)).getText().toString();

        Log.d(LOG_TAG, "@onApprovedTrx(): product = " + productName +
                ", merchantName = " + merchantName + ", amount = " + amount + ", merchantId = " + merchantID);

        if (amount.equals("")) {
            Toast.makeText(this, "Please fill in transaction amount", Toast.LENGTH_SHORT).show();

        } else {
            ImageView fingerPrint = (ImageView) findViewById(R.id.finger_print);
            fingerPrint.setOnLongClickListener(null);
            fingerPrint.setImageDrawable(getResources().getDrawable(R.drawable.ic_fingerprint_disable_56dp));

            TrxHce updatedTrxHce = TrxHce.build(amount, merchantName, productName, merchantID);
            FbStoreTrx.build(this, this).storeTrxToFb(updatedTrxHce);
        }
    }

    @Override
    public void onStoreTrxSuccess(Trx trx) {

        TrxHce trxHce = (TrxHce) trx;
        Log.d(LOG_TAG, "@onApprovedTrx(): product = " + trxHce.getProductName() +
                ", vendor = " + trxHce.getMerchantName() + ", amount = " + trxHce.getAmount());

        ContentValues values = new ContentValues();
        values.put(TrxContract.TrxHistory.COLUMN_TIMESTAMP, trxHce.getTimestamp());
        values.put(TrxContract.TrxHistory.COLUMN_AMOUNT, trxHce.getAmount());
        values.put(TrxContract.TrxHistory.COLUMN_PRODUCT, trxHce.getProductName());
        values.put(TrxContract.TrxHistory.COLUMN_MERCHANT_NAME, trxHce.getMerchantName());
        values.put(TrxContract.TrxHistory.COLUMN_MERCHANT_ID, trxHce.getMerchantID());
        values.put(TrxContract.TrxHistory.COLUMN_TYPE, TrxContract.TrxHistory.TRX_HCE);

        getContentResolver().insert(TrxContract.TrxHistory.CONTENT_URI, values);

        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_right,
                R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
        fragmentTransaction.replace(R.id.pay_container, new HcePayOutcomeFragment(), HcePayOutcomeFragment.LOG_TAG);
        fragmentTransaction.commit();
    }

    @Override
    public void onStoreTrxFailed() {
        Log.d(LOG_TAG, "onStoreTrxFailed");
        Toast.makeText(this, "Transaction failed...", Toast.LENGTH_SHORT).show();
    }

    /**
     * Inflate Hce Scan Fragment showing instruction to tap the phone
     * to the HCE payment module
     */
    private void inflateHceScanFragment() {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, new HceScanFragment(), HceScanFragment.LOG_TAG);
        fragmentTransaction.commit();
    }

}
