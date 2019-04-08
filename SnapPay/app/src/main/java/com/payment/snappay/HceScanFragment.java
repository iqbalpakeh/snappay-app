package com.payment.snappay;

import android.app.Activity;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.payment.snappay.model.TrxHce;

public class HceScanFragment extends Fragment implements HceCardReader.AccountCallback {

    /**
     * Provide this class filter for debugging purpose
     */
    public static final String LOG_TAG = HceScanFragment.class.getSimpleName();

    /**
     * Recommend NfcAdapter flags for reading from other Android devices. Indicates that this
     * activity is interested in NFC-A devices (including other Android devices), and that the
     * system should not check for the presence of NDEF-formatted data (e.g. Android Beam).
     */
    public static int READER_FLAGS = NfcAdapter.FLAG_READER_NFC_A | NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK;

    /**
     * Card reader object that read android hce device acting as a card
     */
    private HceCardReader mHceCardReader;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.content_hce_scan, container, false);

        if (AppSharedPref.isDebugMode()) {
            TextView textView = rootView.findViewById(R.id.tbd_text_view);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Bundle bundle = new Bundle();
                    bundle.putParcelable(TrxHce.LOG_TAG, TrxHce.buildFromSelectResponse(getString(R.string.debug_value)));

                    HcePayContainerFragment fragment = new HcePayContainerFragment();
                    fragment.setArguments(bundle);

                    FragmentManager fm = getFragmentManager();
                    fm.beginTransaction()
                            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                            .replace(R.id.fragment_container, fragment, HcePayContainerFragment.LOG_TAG)
                            .commit();
                }
            });
        }

        mHceCardReader = new HceCardReader(this);
        enableReaderMode();

        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        disableReaderMode();
    }

    @Override
    public void onResume() {
        super.onResume();
        enableReaderMode();
    }

    /**
     * Enable android phone as HCE reader mode
     */
    private void enableReaderMode() {
        Log.i(LOG_TAG, "Enabling reader mode");
        Activity activity = getActivity();
        NfcAdapter nfc = NfcAdapter.getDefaultAdapter(activity);
        if (nfc != null) {
            nfc.enableReaderMode(activity, mHceCardReader, READER_FLAGS, null);
        }
    }

    /**
     * Disable android phone as HCE reader mode
     */
    private void disableReaderMode() {
        Log.i(LOG_TAG, "Disabling reader mode");
        Activity activity = getActivity();
        NfcAdapter nfc = NfcAdapter.getDefaultAdapter(activity);
        if (nfc != null) {
            nfc.disableReaderMode(activity);
        }
    }

    @Override
    public void onAccountReceived(final String account) {

        // This callback is run on a background thread, but updates to UI
        // elements must be perform on the UI thread.

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(LOG_TAG, "@onAccountReceived() account = " + account);

                Bundle bundle = new Bundle();
                bundle.putParcelable(TrxHce.LOG_TAG, TrxHce.buildFromSelectResponse(account));

                HcePayContainerFragment fragment = new HcePayContainerFragment();
                fragment.setArguments(bundle);

                FragmentManager fm = getFragmentManager();
                fm.beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                        .replace(R.id.fragment_container, fragment, HcePayContainerFragment.LOG_TAG)
                        .commit();
            }
        });
    }
}
