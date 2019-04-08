package com.payment.snappaymerchant.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Trx implements Parcelable {

    /**
     * Constant to access merchant info from payment data structure
     */
    final static int MERCHANT = 0;

    /**
     * Constant to access product info from payment data structure
     */
    final static int PRODUCT = 1;

    /**
     * Constant to access amount info from payment data structure
     */
    final static int AMOUNT = 2;

    /**
     * Time when the transaction happened
     */
    private final String mTimestamp;

    /**
     * Amount of transaction
     */
    private final String mAmount;

    /**
     * Product name of transaction
     */
    private final String mProductName;

    /**
     * Merchant name of transaction
     */
    private final String mMerchantName;

    /**
     * The constructor of Trx class
     *
     * @param timestamp    of transaction
     * @param amount       of transaction
     * @param productName  of transaction
     * @param merchantName of transaction
     */
    Trx(String timestamp, String amount, String productName, String merchantName) {
        this.mTimestamp = timestamp;
        this.mAmount = amount;
        this.mProductName = productName;
        this.mMerchantName = merchantName;
    }

    /**
     * The constructor from parcel object
     *
     * @param parcel of trx object
     */
    Trx(Parcel parcel) {
        mTimestamp = parcel.readString();
        mAmount = parcel.readString();
        mProductName = parcel.readString();
        mMerchantName = parcel.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mTimestamp);
        parcel.writeString(mAmount);
        parcel.writeString(mProductName);
        parcel.writeString(mMerchantName);
    }

    /**
     * CREATOR object
     */
    public static final Creator<Trx> CREATOR = new Creator<Trx>() {
        public Trx createFromParcel(Parcel in) {
            return new Trx(in);
        }

        public Trx[] newArray(int size) {
            return new Trx[size];
        }
    };

    /**
     * Get transaction timestamp
     *
     * @return transaction timestamp
     */
    public String getTimestamp() {
        return mTimestamp;
    }

    /**
     * Get Transaction Amount
     *
     * @return transaction amount
     */
    public String getAmount() {
        return mAmount;
    }

    /**
     * Get product name
     *
     * @return product name
     */
    public String getProductName() {
        return mProductName;
    }

    /**
     * Get merchant name
     *
     * @return merchant name
     */
    public String getMerchantName() {
        return mMerchantName;
    }

}