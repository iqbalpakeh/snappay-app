package com.payment.snappaymerchant;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.payment.snappaymerchant.data.TrxContract;
import com.payment.snappaymerchant.firebase.FbRetrieveTrx;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>, FbRetrieveTrx.FbRetrieveTrxAble {

    /**
     * Provide this class filter for debugging purpose
     */
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    /**
     * Total number of transaction
     */
    private BigDecimal mTotal;

    /**
     * View showing total number of transaction
     */
    private TextView mTotalView;

    /**
     * Reference to Fb object
     */
    private FbRetrieveTrx mFbRetrieveTrx;

    /**
     * Cursor adapter of the transactions
     */
    private TrxCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mTotalView = (TextView) findViewById(R.id.total_trx_amount);
        mCursorAdapter = new TrxCursorAdapter(this, null, true);

        ListView trxHistory = (ListView) findViewById(R.id.history_list_item);
        trxHistory.setEmptyView(findViewById(R.id.empty_view));
        trxHistory.setAdapter(mCursorAdapter);

        mFbRetrieveTrx = FbRetrieveTrx.build(this, this);
        mFbRetrieveTrx.retrieveTrxHistory();
    }

    @Override
    public void onStart() {
        super.onStart();
        mFbRetrieveTrx.addListener();
    }

    @Override
    public void onStop() {
        super.onStop();
        mFbRetrieveTrx.removeListener();
    }

    @Override
    public void onRetrieveTrxSuccess() {
        getSupportLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onRetrieveTrxFailed() {
        Log.d(LOG_TAG, "onRetrieveTrxFailed");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_exit) {
            mFbRetrieveTrx.removeListener();
            getContentResolver().delete(TrxContract.TrxHistory.CONTENT_URI, null, null);
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,
                TrxContract.TrxHistory.CONTENT_URI, null, null, null, TrxContract.TrxHistory._ID + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.d(LOG_TAG, "@onLoadFinished()");
        if (cursor.getCount() > 0) {
            mCursorAdapter.swapCursor(cursor);
            calculateTotalFromCursor(cursor);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(LOG_TAG, "@onLoaderReset()");
        mCursorAdapter.swapCursor(null);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        Log.d(LOG_TAG, "@onPointerCaptureChanged()");
    }

    /**
     * Calculate total amount of transaction
     *
     * @param cursor return from cursor loader
     */
    private void calculateTotalFromCursor(Cursor cursor) {

        String amount;

        mTotal = new BigDecimal("0");

        Log.d(LOG_TAG, "first cursor");
        cursor.moveToFirst();
        do {
            Log.d(LOG_TAG, "next cursor");
            amount = cursor.getString(cursor.getColumnIndex(TrxContract.TrxHistory.COLUMN_AMOUNT));
            mTotal = mTotal.add(new BigDecimal(amount));
        } while (cursor.moveToNext());

        mTotalView.setText(new DecimalFormat("#0.00").format(mTotal));

    }

}