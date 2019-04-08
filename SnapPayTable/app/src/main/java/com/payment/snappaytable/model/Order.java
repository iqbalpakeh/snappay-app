package com.payment.snappaytable.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;

public class Order implements Parcelable {

    /**
     * Provide this class filter for debugging purpose
     */
    private static final String LOG_TAG = Order.class.getSimpleName();

    /**
     * List of product for this order
     */
    private ArrayList<Product> mProductList;

    /**
     * merchant email
     */
    private String mMerchantEmail;

    /**
     * merchant id
     */
    private String mMerchantId;

    /**
     * Total price of this order. This data is always calculated everytime product is added to
     * the order.
     */
    private String mTotalPrice;

    /**
     * Total number of order. This data is always calculated everytime product is added to the order
     */
    private String mNumberOfOrder;

    /**
     * private constructor to make sure that it's only created by this class
     *
     * @param merchantEmail of the order
     * @param merchantId    of the order
     */
    Order(String merchantEmail, String merchantId) {
        mProductList = new ArrayList<>();
        mMerchantEmail = merchantEmail;
        mMerchantId = merchantId;
        mTotalPrice = "0";
        mNumberOfOrder = "0";
    }

    /**
     * The constructor from parcel object
     *
     * @param parcel of trx object
     */
    Order(Parcel parcel) {
        //NOTE:
        // mProducList is not send through parcel class since there's no way
        // to send ArrayList<E> via parcel. What we need in PayActivity is only payment tag.
        mMerchantEmail = parcel.readString();
        mMerchantId = parcel.readString();
        mTotalPrice = parcel.readString();
        mNumberOfOrder = parcel.readString();
    }

    /**
     * Build empty order object
     *
     * @param merchantEmail of the order
     * @param merchantId    of the order
     * @return order object
     */
    public static Order build(String merchantEmail, String merchantId) {
        return new Order(merchantEmail, merchantId);
    }

    /**
     * CREATOR object
     */
    public static final Parcelable.Creator<Order> CREATOR = new Parcelable.Creator<Order>() {
        public Order createFromParcel(Parcel in) {
            return new Order(in);
        }

        public Order[] newArray(int size) {
            return new Order[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        //NOTE:
        // mProducList is not send through parcel class since there's no way
        // to send ArrayList<E> via parcel. What we need in PayActivity is only payment tag.
        parcel.writeString(mMerchantEmail);
        parcel.writeString(mMerchantId);
        parcel.writeString(mTotalPrice);
        parcel.writeString(mNumberOfOrder);
    }

    /**
     * add product to order
     *
     * @param product to be added
     */
    public void addProduct(Product product) {
        Log.d(LOG_TAG, "add " + product.getName());
        mProductList.add(product);

        calculatePrice();
        calculateOrderNumber();
    }

    /**
     * Calculate total price
     */
    private void calculatePrice() {
        float price = 0;
        for (Product object : mProductList) {
            price += object.getPrice();
        }
        mTotalPrice = String.valueOf(price);
    }

    /**
     * Calculate Number of Order
     */
    private void calculateOrderNumber() {
        mNumberOfOrder = String.valueOf(mProductList.size());
    }

    /**
     * Create the necessary payment tag used by payment method
     *
     * @return tag information used by payment method
     */
    public String getPaymentTag() {

        String merchantName = mMerchantEmail.split("@")[0];
        String merchantId = mMerchantId;
        String productName = "Sushi";
        String price = mTotalPrice;

        return merchantName + ":" + merchantId + ":" + productName + ":" + price;
    }

    /**
     * Get total price of order
     *
     * @return total order price
     */
    public String getTotalPrice() {
        return mTotalPrice;
    }

    /**
     * Get total number of sushi ordered
     *
     * @return number of order sushi
     */
    public String getNumberOfProduct() {
        return mNumberOfOrder;
    }

    /**
     * Remove product from order
     */
    public void removeProduct() {
        for (int j = mProductList.size() - 1; j >= 0; j--) {
            mProductList.remove(j);
            Log.d(LOG_TAG, "size = " + mProductList.size());
        }
        calculatePrice();
        calculateOrderNumber();
    }

}
