package com.street.larch.a420.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by irteza.arif on 2017-04-25.
 */

public class BrosContract {

    public static final String AUTHORITY = "com.street.larch.a420";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_BROS = "contacts";

    private BrosContract() {}

    public static final class BrosEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_BROS).build();

        public static final String TABLE_NAME = "contacts";

        public static final String COLUMN_NAME = "name";

        public static final String COLUMN_NUMBER = "number";

        public static final String COLUMN_MESSAGE = "message";

        public static final String COLUMN_TIMESTAMP = "timestamp";
    }
}
