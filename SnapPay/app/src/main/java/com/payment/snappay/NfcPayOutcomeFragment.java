package com.payment.snappay;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class NfcPayOutcomeFragment extends Fragment {

    /**
     * Provide this class filter for debugging purpose
     */
    public static final String LOG_TAG = NfcPayOutcomeFragment.class.getSimpleName();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.content_nfc_pay_outcome, container, false);
        return rootView;
    }
}
