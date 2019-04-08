package com.payment.snappay;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.payment.snappay.model.TrxNfc;

public class NfcPayContainerFragment extends Fragment {

    /**
     * Provide this class filter for debugging purpose
     */
    public static final String LOG_TAG = NfcPayContainerFragment.class.getSimpleName();

    /**
     * Listener to finger print event.
     */
    public interface OnFingerPrintListener {

        /**
         * Call back when finger print is already scanned.
         */
        void onFingerPrintScanned();
    }

    /**
     * Listener interface
     */
    private OnFingerPrintListener mListener;

    /**
     * Handles all about fragment transaction
     */
    private FragmentManager mFragmentManager;

    /**
     * Dummy finger print image handling long click
     * to simulate finger print scanner
     */
    private ImageView mFingerPrint;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.content_nfc_pay_container, container, false);

        Bundle bundle = getArguments();
        TrxNfc trxNfc = bundle.getParcelable(TrxNfc.LOG_TAG);

        Log.d(LOG_TAG, "product = " + trxNfc.getProductName() +
                ", vendor = " + trxNfc.getMerchantName() + ", amount = " + trxNfc.getAmount());

        mFragmentManager = getFragmentManager();

        mFingerPrint = rootView.findViewById(R.id.finger_print);
        mFingerPrint.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mListener.onFingerPrintScanned();
                return true;
            }
        });

        inflateNfcPayDetailFragment(bundle);

        return rootView;
    }

    /**
     * Inflate NFC Pay Detail Fragment showing the details of transaction
     *
     * @param bundle of trx details
     */
    private void inflateNfcPayDetailFragment(Bundle bundle) {

        NfcPayDetailFragment fragment = new NfcPayDetailFragment();
        fragment.setArguments(bundle);

        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.pay_container, fragment, NfcPayDetailFragment.LOG_TAG);
        fragmentTransaction.commit();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mListener = (OnFingerPrintListener) context;
        } catch (ClassCastException error) {
            throw new ClassCastException("must implement OnFingerPrintListener");
        }
    }
}
