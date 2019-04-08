package com.payment.snappay;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.payment.snappay.model.TrxHce;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class HcePayDetailFragment extends Fragment {

    /**
     * Provide this class filter for debugging purpose
     */
    public static final String LOG_TAG = HcePayDetailFragment.class.getSimpleName();

    /**
     * View of transaction amount
     */
    private EditText mTrxAmountView;

    /**
     * View of merchant name
     */
    private TextView mMerchantNameView;

    /**
     * View of product name
     */
    private TextView mProductNameView;

    /**
     * View of Merchant Id. This view has no dimension since this view is just to stored temporarily
     * the merchant id information. Value is read by HceActivity while this fragment is on the foreground.
     */
    private TextView mMerchantIdView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.content_hce_pay_details, container, false);

        mTrxAmountView = rootView.findViewById(R.id.trx_amount);
        mMerchantNameView = rootView.findViewById(R.id.merchant_name);
        mProductNameView = rootView.findViewById(R.id.product_name);
        mMerchantIdView = rootView.findViewById(R.id.merchant_id);

        Bundle bundle = getArguments();
        TrxHce trxHce = bundle.getParcelable(TrxHce.LOG_TAG);

        Log.d(LOG_TAG, "product = " + trxHce.getProductName() +
                ", vendor = " + trxHce.getMerchantName() + ", amount = " + trxHce.getAmount());

        if (!trxHce.getAmount().equals("0")) {
            mTrxAmountView.setText(new DecimalFormat("#0.00").format(new BigDecimal(trxHce.getAmount())));
        }
        mMerchantNameView.setText(trxHce.getMerchantName());
        mProductNameView.setText(trxHce.getProductName());
        mMerchantIdView.setText(trxHce.getMerchantID());

        return rootView;
    }

}
