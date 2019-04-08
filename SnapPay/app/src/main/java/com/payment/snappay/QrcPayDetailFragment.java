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

import com.payment.snappay.model.TrxQrc;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class QrcPayDetailFragment extends Fragment {

    /**
     * Provide this class filter for debugging purpose
     */
    public static final String LOG_TAG = QrcPayDetailFragment.class.getSimpleName();

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.content_qrc_pay_details, container, false);

        mTrxAmountView = rootView.findViewById(R.id.trx_amount);
        mMerchantNameView = rootView.findViewById(R.id.merchant_name);
        mProductNameView = rootView.findViewById(R.id.product_name);

        Bundle bundle = getArguments();
        TrxQrc trxQrc = bundle.getParcelable(TrxQrc.LOG_TAG);

        Log.d(LOG_TAG, "product = " + trxQrc.getProductName() +
                ", vendor = " + trxQrc.getMerchantName() + ", amount = " + trxQrc.getAmount());

        if (!trxQrc.getAmount().equals("0")) {
            mTrxAmountView.setText(new DecimalFormat("#0.00").format(new BigDecimal(trxQrc.getAmount())));
        }

        mMerchantNameView.setText(trxQrc.getMerchantName());
        mProductNameView.setText(trxQrc.getProductName());

        return  rootView;
    }
}
