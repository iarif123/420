package com.street.larch.a420.sync;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

/**
 * Created by irteza.arif on 2017-05-03.
 */

public class TextIntentService extends IntentService {

    public TextIntentService() {
        super("TextIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String action = intent.getAction();
        TextTasks.executeTast(getBaseContext(), action);
    }
}
