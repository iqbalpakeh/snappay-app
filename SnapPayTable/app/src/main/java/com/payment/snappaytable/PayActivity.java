package com.payment.snappaytable;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.payment.snappaytable.model.Order;

import net.glxn.qrgen.android.QRCode;

public class PayActivity extends AppCompatActivity {

    /**
     * Provide this class filter for debugging purpose
     */
    public static final String LOG_TAG = PayActivity.class.getSimpleName();

    /**
     * Order object handling ordering product
     */
    private Order mOrder;

    /**
     * Text view object contain information about total price
     */
    private TextView mTotalPriceTextView;

    /**
     * Image view object of qrcode generated
     */
    private ImageView mQrcImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);

        mTotalPriceTextView = (TextView) findViewById(R.id.total_price_text_view);
        mQrcImageView = (ImageView) findViewById(R.id.qrc_image_view);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        prepareOrder();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_pay, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {

            case R.id.action_logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return mOrder;
    }

    /**
     * Prepare order object. Check if there's already list of product stored from previous
     * rotation.
     */
    private void prepareOrder() {

        Order order = ((Order) getLastCustomNonConfigurationInstance());
        if (order != null) {
            Log.d(LOG_TAG, "Order object is not null");
            mOrder = order;
            refreshOrder();
        } else {
            Log.d(LOG_TAG, "Create new order object");
            mOrder = getIntent().getParcelableExtra(MainActivity.LOG_TAG);
            refreshOrder();
        }
    }

    /**
     * Refresh information of the total price and number of order
     */
    private void refreshOrder() {

        mTotalPriceTextView.setText("Please pay $" + mOrder.getTotalPrice());

        Bitmap bitmap = QRCode.from(mOrder.getPaymentTag()).bitmap();
        mQrcImageView.setImageBitmap(bitmap);
    }

}
