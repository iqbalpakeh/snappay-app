package com.payment.snappay.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public final class TrxContract {

    /**
     * Content Authority of Snap Pay apps
     */
    public static final String CONTENT_AUTHORITY = "com.payment.snappay.data";

    /**
     * Base URI to access Snap Pay Apps content provider
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * To prevent someone from accidentally instantiating the contract class,
     * give it an empty constructor
     */
    private TrxContract() {
    }

    /**
     * Constant values of transactions data base table.
     */
    public static final class TrxHistory implements BaseColumns {

        /**
         * Name of database transaction table
         */
        public final static String TABLE_NAME = "trxs";

        /**
         * Unique ID number for the pet (only for use in the database table).
         * <p>
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Transaction timestamp
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_TIMESTAMP = "timestamp";

        /**
         * Transaction amount value
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_AMOUNT = "amount";

        /**
         * Transaction product name
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_PRODUCT = "product";

        /**
         * Transaction merchant name
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_MERCHANT_NAME = "merchant_name";

        /**
         * Transaction merchant id
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_MERCHANT_ID = "merchant_id";

        /**
         * Transaction type
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_TYPE = "type";

        /**
         * Value of NFC transaction
         */
        public final static String TRX_NFC = "nfc";

        /**
         * Value of QRCOde transaction
         */
        public final static String TRX_QRC = "qrcode";

        /**
         * Value of HCE transaction
         */
        public final static String TRX_HCE = "hce";

        /**
         * Create CONTENT_URI
         */
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();

        /**
         * Builds URIs on insertion
         *
         * @param id of selected row
         * @return concatenated uri
         */
        public static Uri buildFlavorsUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }

}
