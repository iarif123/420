package com.street.larch.a420.sync;

import android.content.Context;
import android.database.Cursor;

import com.street.larch.a420.data.BrosContract;
import com.street.larch.a420.data.BrosProvider;

/**
 * Created by irteza.arif on 2017-05-03.
 */

public class TextTasks {

    public static final String ACTION_TEXT_AT_420 = "text-at-420";

    public static void executeTast(Context context, String action) {
        if (ACTION_TEXT_AT_420.equals(action)) {
            text(context);
        }
    }

    private static void text(Context context) {
        Cursor cursor = context.getContentResolver().query(
                BrosContract.BrosEntry.CONTENT_URI,
                null,
                null,
                null,
                BrosContract.BrosEntry.COLUMN_TIMESTAMP
        );
        while (cursor.moveToNext()) {
            String number = "";
            String message = "";
        }
    }
}
