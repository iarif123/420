package com.street.larch.a420;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.street.larch.a420.data.BrosContract;
import com.street.larch.a420.data.BrosDbHelper;
import com.street.larch.a420.data.BrosProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class ChildActivity extends AppCompatActivity {

    private TextView mContactNameTextView;
    private TextView mContactNumberTextView;
    private EditText mContactMessageEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mContactNameTextView = (TextView) findViewById(R.id.contact_name);
        mContactNumberTextView = (TextView) findViewById(R.id.contact_number);
        mContactMessageEditText = (EditText) findViewById(R.id.contact_message);

        Intent intentThatStartedThisActivity = getIntent();
        HashMap<String,String> map = (HashMap<String,String>) intentThatStartedThisActivity.getSerializableExtra("map");
        long id = intentThatStartedThisActivity.getLongExtra("id",-1);
        if (map!=null) {
            String name = map.get("Name");
            String number = map.get("Phone");
            mContactNameTextView.setText(name);
            mContactNumberTextView.setText(number);
        } else if (id>=0) {
            Log.d("CHILD ACTIVITY", "onCreate: " + id);
            Cursor cursor = getContentResolver().query(
                    Uri.parse(BrosContract.BrosEntry.CONTENT_URI+"/"+id),
                    null,
                    null,
                    null,
                    null
            );
            if (!cursor.moveToPosition(0)) {
                return;
            }
            String name = cursor.getString(cursor.getColumnIndex(BrosContract.BrosEntry.COLUMN_NAME));
            String number = cursor.getString(cursor.getColumnIndex(BrosContract.BrosEntry.COLUMN_NUMBER));
            String message = cursor.getString(cursor.getColumnIndex(BrosContract.BrosEntry.COLUMN_MESSAGE));

            mContactNameTextView.setText(name);
            mContactNumberTextView.setText(number);
            mContactMessageEditText.setText(message);
        }
    }

    public void saveBro(View view) {
        if (mContactMessageEditText.getText().length()==0) {
            return;
        }

        String name = mContactNameTextView.getText().toString();
        String number = mContactNumberTextView.getText().toString();
        String message = mContactMessageEditText.getText().toString();

        long id = getIntent().getLongExtra("id",-1);

        if(id>=0) {
            updateBro(id, name, number, message);
        } else {
            addNewBro(name, number, message);
        }

        finish();
    }

    private void addNewBro(String name, String number, String message) {
        ContentValues cv = new ContentValues();
        cv.put(BrosContract.BrosEntry.COLUMN_NAME, name);
        cv.put(BrosContract.BrosEntry.COLUMN_NUMBER, number);
        cv.put(BrosContract.BrosEntry.COLUMN_MESSAGE, message);
        Uri uri = getContentResolver().insert(BrosContract.BrosEntry.CONTENT_URI, cv);
        if (uri != null) {
            Toast.makeText(getBaseContext(), uri.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private void updateBro(long id, String name, String number, String message) {
        ContentValues cv = new ContentValues();
        cv.put(BrosContract.BrosEntry.COLUMN_NAME, name);
        cv.put(BrosContract.BrosEntry.COLUMN_NUMBER, number);
        cv.put(BrosContract.BrosEntry.COLUMN_MESSAGE, message);
        getContentResolver().update(
                Uri.parse(BrosContract.BrosEntry.CONTENT_URI+"/"+id),
                cv,
                null,
                null
        );
    }

}
