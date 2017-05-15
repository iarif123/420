package com.street.larch.a420;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.street.larch.a420.data.BrosContract;

/**
 * Created by irteza.arif on 2017-04-26.
 */

public class BrosListAdapter extends RecyclerView.Adapter<BrosListAdapter.BrosViewHolder>{

    private Context mContext;
    private Cursor mCursor;

    final private BrosListAdapterOnTouchHandler mTouchHandler;

    public interface BrosListAdapterOnTouchHandler {
        void onTouch(long id, MotionEvent event);
    }

    public BrosListAdapter(Context context, Cursor cursor, BrosListAdapterOnTouchHandler touchHandler) {
        this.mContext = context;
        this.mCursor = cursor;
        this.mTouchHandler = touchHandler;
    }

    @Override
    public BrosViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.bros_list_item, parent, false);
        return new BrosViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BrosListAdapter.BrosViewHolder holder, int position) {
        if (!mCursor.moveToPosition(position)) {
            return;
        }

        String name = mCursor.getString(mCursor.getColumnIndex(BrosContract.BrosEntry.COLUMN_NAME));

        long id = mCursor.getLong(mCursor.getColumnIndex(BrosContract.BrosEntry._ID));

        holder.nameTextView.setText(name);

        holder.itemView.setTag(id);
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    public void swapCursor(Cursor cursor) {
        if (mCursor != null) mCursor.close();
        mCursor = cursor;
        if(cursor != null) {
            this.notifyDataSetChanged();
        }
    }

    class BrosViewHolder extends RecyclerView.ViewHolder implements View.OnTouchListener {

        TextView nameTextView;

        public BrosViewHolder(View itemView) {
            super(itemView);
            nameTextView = (TextView) itemView.findViewById(R.id.list_item_bros_textview);
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            long id = (long) v.getTag();
            mTouchHandler.onTouch(id, event);
            Log.d("BROSVIEWHOLDER", "onTouch: ");
            return true;
        }
    }
}
