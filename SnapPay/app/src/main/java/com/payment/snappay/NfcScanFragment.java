package com.payment.snappay;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.payment.snappay.model.TrxNfc;

public class NfcScanFragment extends Fragment {

    /**
     * Provide this class filter for debugging purpose
     */
    public static final String LOG_TAG = NfcScanFragment.class.getSimpleName();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.content_nfc_scan, container, false);

        if (AppSharedPref.isDebugMode()) {
            TextView textView = rootView.findViewById(R.id.tbd_text_view);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Bundle bundle = new Bundle();
                    bundle.putParcelable(TrxNfc.LOG_TAG, TrxNfc.buildFromNfcTag(getString(R.string.debug_value)));

                    NfcPayContainerFragment fragment = new NfcPayContainerFragment();
                    fragment.setArguments(bundle);

                    FragmentManager fm = getFragmentManager();
                    fm.beginTransaction()
                            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                            .replace(R.id.fragment_container, fragment, NfcPayContainerFragment.LOG_TAG)
                            .commit();
                }
            });
        }

        return rootView;
    }


}
