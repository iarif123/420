package com.street.larch.a420.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by irteza.arif on 2017-05-02.
 */

public class BrosProvider extends ContentProvider {

    public static final int BROS = 100;
    public static final int BROS_WITH_ID = 101;

    public static final UriMatcher sUriMatcher = buildUriMatcher();

    private BrosDbHelper mBrosDbHelper;

    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(BrosContract.AUTHORITY, BrosContract.PATH_BROS, BROS);
        uriMatcher.addURI(BrosContract.AUTHORITY, BrosContract.PATH_BROS + "/#", BROS_WITH_ID);
        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        mBrosDbHelper = new BrosDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db = mBrosDbHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        Cursor cursor;
        switch (match) {
            case BROS :
                cursor = db.query(
                        BrosContract.BrosEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case BROS_WITH_ID :
                long id = ContentUris.parseId(uri);
                cursor = db.query(
                        BrosContract.BrosEntry.TABLE_NAME,
                        projection,
                        "_id=?",
                        new String[] { String.valueOf(id) },
                        null,
                        null,
                        sortOrder
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        SQLiteDatabase db = mBrosDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        Uri returnUri = null;
        switch (match) {
            case BROS :
                long id = db.insert(
                        BrosContract.BrosEntry.TABLE_NAME,
                        null,
                        values
                );
                if (id>0) {
                    returnUri = ContentUris.withAppendedId(BrosContract.BrosEntry.CONTENT_URI, id);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = mBrosDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int rows;
        switch (match) {
            case BROS_WITH_ID :
                long id = ContentUris.parseId(uri);
                rows = db.delete(
                        BrosContract.BrosEntry.TABLE_NAME,
                        "_id=?",
                        new String[] { String.valueOf(id) }
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }
        return rows;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = mBrosDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int rows;
        switch (match) {
            case BROS_WITH_ID :
                long id = ContentUris.parseId(uri);
                rows = db.update(
                        BrosContract.BrosEntry.TABLE_NAME,
                        values,
                        "_id=?",
                        new String[] { String.valueOf(id) }
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }
        return rows;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }
}
