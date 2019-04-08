package com.payment.snappay;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.payment.snappay.data.TrxContract;
import com.payment.snappay.model.Trx;
import com.payment.snappay.model.TrxHce;
import com.payment.snappay.model.TrxNfc;
import com.payment.snappay.model.TrxQrc;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TrxCursorAdapter extends CursorAdapter {

    public TrxCursorAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).
                inflate(R.layout.transaction_detail, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        String type = cursor.getString(cursor.getColumnIndex(TrxContract.TrxHistory.COLUMN_TYPE));
        Trx trx;

        if (type.equals(TrxContract.TrxHistory.TRX_NFC)) {
            trx = TrxNfc.buildFromCursor(cursor);
        } else if (type.equals(TrxContract.TrxHistory.TRX_QRC)) {
            trx = TrxQrc.buildFromCursor(cursor);
        } else {
            trx = TrxHce.buildFromCursor(cursor);
        }

        String transactionNote = prepareTrxNote(trx);
        String date = prepareDate(trx);
        String time = prepareTime(trx);
        String avatar = prepareTrxAvatar(trx);
        Drawable drawable = prepareTrxDrawable(trx);

        TextView typeTextView = view.findViewById(R.id.transaction_note);
        typeTextView.setText(transactionNote);

        TextView amountTextView = view.findViewById(R.id.transaction_amount);
        amountTextView.setText("$ " + trx.getAmount());

        TextView dateTextView = view.findViewById(R.id.transaction_date);
        dateTextView.setText(date);

        TextView timeTextView = view.findViewById(R.id.transaction_time);
        timeTextView.setText(time);

        TextView avatarTextView = view.findViewById(R.id.avatar);
        avatarTextView.setText(avatar);
        avatarTextView.setBackground(drawable);

    }

    @Override
    public CharSequence[] getAutofillOptions() {
        return new CharSequence[0];
    }

    /**
     * Prepare avatar message to shows on the transaction details
     *
     * @param trx object
     * @return avatar message
     */
    private String prepareTrxAvatar(Trx trx) {

        String avatar;

        if (trx instanceof TrxNfc) {
            avatar = "NFC";
        } else if (trx instanceof TrxQrc) {
            avatar = "QRC";
        } else {
            avatar = "HCE";
        }

        return avatar;
    }

    /**
     * Select drawable object based on transaction to shows on the transaction details
     *
     * @param trx object
     * @return drawable object
     */
    private Drawable prepareTrxDrawable(Trx trx) {

        Drawable drawable;

        if (trx instanceof TrxNfc) {
            drawable = mContext.getResources()
                    .getDrawable(R.drawable.avatar_nfc_payment);
        } else if (trx instanceof TrxQrc) {
            drawable = mContext.getResources()
                    .getDrawable(R.drawable.avatar_qrc_payment);
        } else {
            drawable = mContext.getResources()
                    .getDrawable(R.drawable.avatar_hce_payment);
        }

        return drawable;
    }

    /**
     * Prepare transaction note to shows on the transaction details
     *
     * @param trx object
     * @return transaction note
     */
    private String prepareTrxNote(Trx trx) {
        
        return "Buy " + trx.getProductName() + " from " + trx.getMerchantName();
    }

    /**
     * Convert Unix Time format to readable Date format
     *
     * @param trx object
     * @return readable data format
     */
    private String prepareDate(Trx trx) {

        String date;

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.valueOf(trx.getTimestamp()));

        SimpleDateFormat fmt = new SimpleDateFormat("MMM d, yyyy");
        date = fmt.format(calendar.getTime());

        return date;
    }

    /**
     * Convert Unix Time format to readable Time format
     *
     * @param trx object
     * @return readable time format
     */
    private String prepareTime(Trx trx) {

        String time;

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.valueOf(trx.getTimestamp()));

        SimpleDateFormat fmt = new SimpleDateFormat("h:mm a");
        time = fmt.format(calendar.getTime());

        return time;
    }

}
