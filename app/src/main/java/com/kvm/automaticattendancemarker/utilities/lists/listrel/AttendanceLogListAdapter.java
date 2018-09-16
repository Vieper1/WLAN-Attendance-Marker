package com.kvm.automaticattendancemarker.utilities.lists.listrel;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kvm.automaticattendancemarker.R;
import com.kvm.automaticattendancemarker.utilities.dbrel.AttendanceSystemDBHelper;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Viper GTS on 22-Jan-17.
 */

public class AttendanceLogListAdapter  extends BaseAdapter
{
    private Context context;
    private Cursor cursor;

    private class ViewHolder
    {
        public TextView side;
        public TextView sub;
        public TextView main;

        ViewHolder(View v)
        {
            side = (TextView) v.findViewById(R.id.attendance_log_list_item_side);
            sub = (TextView) v.findViewById(R.id.attendance_log_list_item_sub);
            main = (TextView) v.findViewById(R.id.attendance_log_list_item_header);
        }
    }

    public AttendanceLogListAdapter(Context context, Cursor cursor)
    {
        this.context = context;
        this.cursor = cursor;
    }

    @Override
    public int getCount() {
        return cursor.getCount();
    }

    @Override
    public Object getItem(int i)
    {
        cursor.moveToPosition(i);
        return cursor;
    }

    @Override
    public long getItemId(int i)
    {
        cursor.moveToPosition(i);
        return Long.valueOf(cursor.getString(cursor.getColumnIndex(AttendanceSystemDBHelper.TABLE_6_COL_0)));
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup)
    {
        View v = view;
        ViewHolder holder;

        if(view == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.attendance_log_list_item, viewGroup, false);
            holder = new ViewHolder(v);
            v.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) v.getTag();
        }
        cursor.moveToPosition(i);

        Calendar tempCal = Calendar.getInstance();
        tempCal.setTimeInMillis(Long.valueOf(cursor.getString(cursor.getColumnIndex(AttendanceSystemDBHelper.TABLE_6_COL_5))));
        Date tempDate = tempCal.getTime();
        String[] dArray = tempDate.toString().split(" ");



        holder.main.setText(dArray[3].substring(0, 5));
        holder.side.setText(String.valueOf(cursor.getString(cursor.getColumnIndex(AttendanceSystemDBHelper.TABLE_6_COL_4)).split(",").length));
        holder.sub.setText(dArray[0]+", "+dArray[2]+" "+dArray[1]+", "+dArray[5]);


        return v;
    }
}
