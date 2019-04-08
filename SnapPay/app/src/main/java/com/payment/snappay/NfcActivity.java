package com.payment.snappay;

import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.payment.snappay.data.TrxContract;
import com.payment.snappay.firebase.FbStoreTrx;
import com.payment.snappay.model.Trx;
import com.payment.snappay.model.TrxNfc;

public class NfcActivity extends AppCompatActivity
        implements NfcPayContainerFragment.OnFingerPrintListener, FbStoreTrx.FbStoreTrxAble {

    /**
     * Provide this class filter for debugging purpose
     */
    private static final String LOG_TAG = NfcActivity.class.getSimpleName();

    /**
     * Handles all about fragment transaction
     */
    private FragmentManager mFragmentManager;

    /**
     * Object handles utility function related to NFC tag
     */
    private NfcAdapter mNfcAdapter;

    /**
     * Temporary object of NFC transaction because the final amount should be taken
     * from NfcPayDetailFragment in case user change the amount.
     */
    private TrxNfc mTrxNfc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc);

        mFragmentManager = getSupportFragmentManager();
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        String intentAction = getIntent().getAction();
        Log.d(LOG_TAG, "intent: " + intentAction);

        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intentAction)) {
            inflateNfcPayFragment(getIntent());
        } else {
            inflateNfcScanFragment();
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {

        Log.d(LOG_TAG, "onNewIntent: " + intent.getAction());

        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction()) && checkScanVisible()) {

            mTrxNfc = TrxNfc.buildFromNfcTag(readNFCTag(intent));

            Bundle bundle = new Bundle();
            bundle.putParcelable(TrxNfc.LOG_TAG, mTrxNfc);

            NfcPayContainerFragment fragment = new NfcPayContainerFragment();
            fragment.setArguments(bundle);

            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                    .replace(R.id.fragment_container, fragment, NfcPayContainerFragment.LOG_TAG)
                    .commit();
        }
    }

    @Override
    public void onFingerPrintScanned() {

        if (AppSharedPref.isDebugMode()) {
            if (mTrxNfc == null)
                mTrxNfc = TrxNfc.buildFromNfcTag(getString(R.string.debug_value));
        }

        String amount = ((EditText) findViewById(R.id.trx_amount)).getText().toString();
        String merchantName = mTrxNfc.getMerchantName();
        String productName = mTrxNfc.getProductName();
        String merchantID = mTrxNfc.getMerchantID();

        if (amount.equals("")) {
            Toast.makeText(this, "Please fill in transaction amount", Toast.LENGTH_SHORT).show();

        } else {
            ImageView fingerPrint = (ImageView) findViewById(R.id.finger_print);
            fingerPrint.setOnLongClickListener(null);
            fingerPrint.setImageDrawable(getResources().getDrawable(R.drawable.ic_fingerprint_disable_56dp));

            TrxNfc updatedTrxNfc = TrxNfc.build(amount, merchantName, productName, merchantID);
            FbStoreTrx.build(this, this).storeTrxToFb(updatedTrxNfc);
        }
    }

    @Override
    public void onStoreTrxSuccess(Trx trx) {

        TrxNfc trxNfc = (TrxNfc) trx;
        Log.d(LOG_TAG, "@onApprovedTrx(): product = " + trxNfc.getProductName() +
                ", vendor = " + trxNfc.getMerchantName() + ", amount = " + trxNfc.getAmount());

        ContentValues values = new ContentValues();
        values.put(TrxContract.TrxHistory.COLUMN_TIMESTAMP, trxNfc.getTimestamp());
        values.put(TrxContract.TrxHistory.COLUMN_AMOUNT, trxNfc.getAmount());
        values.put(TrxContract.TrxHistory.COLUMN_PRODUCT, trxNfc.getProductName());
        values.put(TrxContract.TrxHistory.COLUMN_MERCHANT_NAME, trxNfc.getMerchantName());
        values.put(TrxContract.TrxHistory.COLUMN_MERCHANT_ID, trxNfc.getMerchantID());
        values.put(TrxContract.TrxHistory.COLUMN_TYPE, TrxContract.TrxHistory.TRX_NFC);

        getContentResolver().insert(TrxContract.TrxHistory.CONTENT_URI, values);

        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_right,
                R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
        fragmentTransaction.replace(R.id.pay_container, new NfcPayOutcomeFragment(), NfcPayOutcomeFragment.LOG_TAG);
        fragmentTransaction.commit();
    }

    @Override
    public void onStoreTrxFailed() {
        Log.d(LOG_TAG, "onStoreTrxFailed");
        Toast.makeText(this, "Transaction failed...", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter ndefFilter = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            ndefFilter.addDataType("text/plain");
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("fail", e);
        }

        IntentFilter[] nfcIntentFilter = new IntentFilter[]{ndefFilter};
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        String[][] techListsArray = new String[][]{new String[]{Ndef.class.getName()}};
        if (mNfcAdapter != null)
            mNfcAdapter.enableForegroundDispatch(this, pendingIntent, nfcIntentFilter, techListsArray);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mNfcAdapter != null)
            mNfcAdapter.disableForegroundDispatch(this);
    }

    /**
     * Inflate NFC Scan Fragment showing instruction to tap
     * the phone to the NFC Tag
     */
    private void inflateNfcScanFragment() {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, new NfcScanFragment(), NfcScanFragment.LOG_TAG);
        fragmentTransaction.commit();
    }

    /**
     * Read NFC tag and concatenate all the payload
     *
     * @param intent from NFC tag
     * @return concatenated payload
     */
    private String readNFCTag(Intent intent) {

        String payload = "";
        Parcelable[] rawMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

        if (rawMessages != null) {
            for (Parcelable rawMessage : rawMessages) {
                for (NdefRecord record : ((NdefMessage) rawMessage).getRecords()) {
                    payload += new String(record.getPayload());
                    Log.d(LOG_TAG, "payload = " + payload);
                }
            }
        }

        return payload;
    }

    /**
     * Check if the scan nfc fragment is visible
     *
     * @return true if nfc scan fragment is visible. Otherwise, return false
     */
    private boolean checkScanVisible() {

        NfcScanFragment fragment = (NfcScanFragment)
                getSupportFragmentManager().findFragmentByTag(NfcScanFragment.LOG_TAG);

        if (fragment != null && fragment.isVisible()) {

            return true;

        } else {

            return false;
        }

    }

    /**
     * Inflate Pay Scan Fragment showing instruction to pay
     * the transaction
     *
     * @param intent read from NDEF NFC tag
     */
    private void inflateNfcPayFragment(Intent intent) {

        mTrxNfc = TrxNfc.buildFromNfcTag(readNFCTag(intent));

        Bundle bundle = new Bundle();
        bundle.putParcelable(TrxNfc.LOG_TAG, mTrxNfc);

        NfcPayContainerFragment fragment = new NfcPayContainerFragment();
        fragment.setArguments(bundle);

        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment, NfcPayContainerFragment.LOG_TAG);
        fragmentTransaction.commit();
    }

}
