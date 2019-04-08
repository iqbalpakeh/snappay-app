package com.payment.snappay.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class TrxProvider extends ContentProvider {

    /**
     * How to use ADB to debug SQLite3:
     *      https://stackoverflow.com/questions/18370219/how-to-use-adb-in-android-studio-to-view-an-sqlite-db
     *
     *********************************************************************************************************/

    /**
     * Provide this class filter for debugging purpose
     */
    private static final String LOG_TAG = TrxProvider.class.getSimpleName();

    /**
     * Responsible to select which SQL operation to be executed
     */
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    /**
     * SQLite data base helper
     */
    private TrxDbHelper mOpenHelper;

    /**
     * Integer used to execute query for the whole table
     */
    private static final int TRX = 100;

    /**
     * Integer used to execute query for specific ID from table
     */
    private static final int TRX_WITH_ID = 200;

    /**
     * Build Uri Matcher for query
     *
     * @return matcher object
     */
    private static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = TrxContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, TrxContract.TrxHistory.TABLE_NAME, TRX);
        matcher.addURI(authority, TrxContract.TrxHistory.TABLE_NAME + "/#", TRX_WITH_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new TrxDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        Cursor cursor;

        switch (sUriMatcher.match(uri)) {

            case TRX: {
                cursor = mOpenHelper.getReadableDatabase().query(
                        TrxContract.TrxHistory.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }

            case TRX_WITH_ID: {
                cursor = mOpenHelper.getReadableDatabase().query(
                        TrxContract.TrxHistory.TABLE_NAME,
                        projection,
                        TrxContract.TrxHistory._ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder);
                break;
            }

            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Uri returnUri;

        switch (sUriMatcher.match(uri)) {

            case TRX: {
                long _id = db.insert(TrxContract.TrxHistory.TABLE_NAME, null, contentValues);
                if (_id > 0) {
                    returnUri =
                            TrxContract.TrxHistory.buildFlavorsUri(_id);
                } else {
                    throw new android.database.
                            SQLException("Failed to insert row into: " + uri);
                }
                break;
            }

            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int numDeleted;
        switch (match) {

            case TRX:
                numDeleted = db.delete(
                        TrxContract.TrxHistory.TABLE_NAME,
                        selection,
                        selectionArgs
                );
                db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                        TrxContract.TrxHistory.TABLE_NAME + "'"
                );
                break;

            case TRX_WITH_ID:
                numDeleted = db.delete(
                        TrxContract.TrxHistory.TABLE_NAME,
                        TrxContract.TrxHistory._ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))}
                );
                db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                        TrxContract.TrxHistory.TABLE_NAME + "'"
                );
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return numDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
