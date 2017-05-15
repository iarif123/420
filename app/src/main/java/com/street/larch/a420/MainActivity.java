package com.street.larch.a420;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.street.larch.a420.data.BrosContract;
import com.street.larch.a420.data.BrosDbHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity implements
        BrosListAdapter.BrosListAdapterOnTouchHandler,
        RecyclerView.OnItemTouchListener {

    private final String LOG_TAG = MainActivity.class.getSimpleName();

    private ArrayList<Map<String, String>> mContactList;

    private SimpleAdapter mAdapter;
    private AutoCompleteTextView mAutoCompleteTextView;

    private BrosListAdapter mBrosAdapter;
    private RecyclerView brosListRecyclerView;
    private GestureDetectorCompat gestureDetector;
    private int MY_PERMISSIONS_REQUEST_READ_CONTACTS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getPermission();

        gestureDetector = new GestureDetectorCompat(this, new RecyclerViewOnGestureListener());

        mContactList = new ArrayList<Map<String, String>>();
        populateContactList();

        mAutoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.autocomplete);

        mAdapter = new SimpleAdapter(this, mContactList, R.layout.contact_autocomplete, new String[] { "Name", "Phone", "Type" }, new int[] {R.id.ccontName, R.id.ccontNo, R.id.ccontType});

        mAutoCompleteTextView.setAdapter(mAdapter);

        mAutoCompleteTextView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, String> map = (HashMap<String, String>) parent.getItemAtPosition(position);
                String name  = map.get("Name");
                mAutoCompleteTextView.setText(name);
                Context context = MainActivity.this;
                Class destinationClass = ChildActivity.class;
                Intent intent = new Intent(context, destinationClass);
                intent.putExtra("map", map);
                int requestCode = 1;
                startActivityForResult(intent, requestCode);
            }
        });

        brosListRecyclerView = (RecyclerView) findViewById(R.id.all_bros_list_view);

        brosListRecyclerView.addOnItemTouchListener(this);

        brosListRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        Cursor cursor = getAllBros();

        mBrosAdapter = new BrosListAdapter(this, cursor, this);

        brosListRecyclerView.setAdapter(mBrosAdapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                long id = (long) viewHolder.itemView.getTag();
                removeBro(id);
                mBrosAdapter.swapCursor(getAllBros());
            }
        }).attachToRecyclerView(brosListRecyclerView);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1) {
            mBrosAdapter.swapCursor(getAllBros());
        }
    }

    private Cursor getAllBros() {
        return getContentResolver().query(
                BrosContract.BrosEntry.CONTENT_URI,
                null,
                null,
                null,
                BrosContract.BrosEntry.COLUMN_TIMESTAMP
        );
    }

    public void populateContactList() {
        mContactList.clear();
        Cursor contact = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,null,null,null,null);
        while (contact.moveToNext()) {
            String contactName = contact.getString(contact.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            String contactId = contact.getString(contact.getColumnIndex(ContactsContract.Contacts._ID));
            String hasPhone = contact.getString(contact.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

            if ((Integer.parseInt(hasPhone) > 0)) {
                Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID+" = "+ contactId,null,null);

                while (phones.moveToNext()) {
                    String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    String numberType = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));

                    Map<String, String> NamePhoneType = new HashMap<String, String>();

                    NamePhoneType.put("Name", contactName);
                    NamePhoneType.put("Phone", phoneNumber);

                    if (numberType.equals("0")) {
                        NamePhoneType.put("Type", "Work");
                    } else if (numberType.equals("1")) {
                        NamePhoneType.put("Type", "Home");
                    } else if (numberType.equals("2")) {
                        NamePhoneType.put("Type", "Mobile");
                    } else {
                        NamePhoneType.put("Type", "Other");
                    }

                    mContactList.add(NamePhoneType);
                }
                phones.close();
            }
        }
        contact.close();
        //startManagingCursor(contact);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.



        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Context context = MainActivity.this;
            String message = "it worked";
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void getPermission() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.READ_CONTACTS)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.READ_CONTACTS},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    private boolean removeBro(long id) {
        return getContentResolver().delete(Uri.parse(BrosContract.BrosEntry.CONTENT_URI + "/" + id), null, null) > 0 ;
    }

    @Override
    public void onTouch(long id, MotionEvent event) {
        Context context = MainActivity.this;
        Class destinationClass = ChildActivity.class;
        Intent intent = new Intent(context, destinationClass);
        intent.putExtra("id", id);
        int requestCode = 1;
        startActivityForResult(intent, requestCode);
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        gestureDetector.onTouchEvent(e);
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    private class RecyclerViewOnGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            View view = brosListRecyclerView.findChildViewUnder(e.getX(), e.getY());
            long id = (long) view.getTag();
            Context context = MainActivity.this;
            Class destinationClass = ChildActivity.class;
            Intent intent = new Intent(context, destinationClass);
            intent.putExtra("id", id);
            int requestCode = 1;
            startActivityForResult(intent, requestCode);
            return super.onSingleTapConfirmed(e);
        }
    }
}
