package com.payment.snappaytable;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.payment.snappaytable.model.Order;
import com.payment.snappaytable.model.Product;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    /**
     * Provide this class filter for debugging purpose
     */
    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    /**
     * Order object handling ordering product
     */
    private Order mOrder;

    /**
     * List of products
     */
    private ArrayList<Product> products = new ArrayList<>();

    /**
     * Text View object contain information about number of sushi ordered
     */
    private TextView mNumberOfSushiTextView;

    /**
     * Text View object contain information about total order
     */
    private TextView mTotalOrderTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mNumberOfSushiTextView = (TextView) findViewById(R.id.number_of_sushi_text_view);
        mTotalOrderTextView = (TextView) findViewById(R.id.total_order_text_view);

        prepareProducts();
        prepareGridView();
        prepareOrder();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {

            case R.id.action_clear_order:
                mOrder.removeProduct();
                refreshOrder();
                break;

            case R.id.action_pay_order:
                if (mOrder.getNumberOfProduct().equals("0")) {
                    Toast.makeText(MainActivity.this, "Please pick your order", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(MainActivity.this, PayActivity.class);
                    intent.putExtra(LOG_TAG, mOrder);
                    startActivity(intent);
                }
                break;

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
            mOrder = Order.build(AppSharedPref.getEmail(this), AppSharedPref.getUid(this));
        }
    }

    /**
     * Prepare list of sushi products
     */
    private void prepareProducts() {

        products.add(Product.build(Product.PRODUCT_GOLDEN_ROLL, 16.50f, R.drawable.product_golden_roll));
        products.add(Product.build(Product.PRODUCT_PHOENIX_ROLL, 18.50f, R.drawable.product_phoenix_roll));
        products.add(Product.build(Product.PRODUCT_LOBSTER_SALAD_ROLL, 10.50f, R.drawable.product_lobster_salad_roll));
        products.add(Product.build(Product.PRODUCT_SALMON_ROLL, 14.50f, R.drawable.product_salmon_roll));
        products.add(Product.build(Product.PRODUCT_UNAGI_ROLL, 14.50f, R.drawable.product_unagi_roll));
        products.add(Product.build(Product.PRODUCT_KANI_MENTAI_MAYO_ROLL, 9.50f, R.drawable.product_kani_mentai_mayo_roll));
        products.add(Product.build(Product.PRODUCT_MOMOJI, 21.00f, R.drawable.product_momoji_10pcs));
        products.add(Product.build(Product.PRODUCT_SUMIRE, 21.00f, R.drawable.product_sumire_10pcs));
    }

    /**
     * Prepare grid view of sushi product
     */
    private void prepareGridView() {

        GridView gridview = (GridView) findViewById(R.id.menu_grid_view);
        gridview.setAdapter(ProductAdapter.build(getBaseContext(), products));
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Product product = products.get(position);
                Log.d(LOG_TAG, "product name = " + product.getName());

                mOrder.addProduct(product);
                refreshOrder();
            }
        });
    }

    /**
     * Refresh information of the total price and numbe of order
     */
    private void refreshOrder() {
        mTotalOrderTextView.setText("Total $" + mOrder.getTotalPrice());
        mNumberOfSushiTextView.setText(mOrder.getNumberOfProduct() + " pieces of sushi");
    }
}
