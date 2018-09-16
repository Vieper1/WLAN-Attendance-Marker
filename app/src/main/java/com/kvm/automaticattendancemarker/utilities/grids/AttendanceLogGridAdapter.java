package com.kvm.automaticattendancemarker.utilities.grids;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kvm.automaticattendancemarker.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by Viper GTS on 22-Jan-17.
 */

public class AttendanceLogGridAdapter extends BaseAdapter
{
    private Context context;
    private int maxRollNo;
    private HashMap<String, GridData> studentDataHashMap;


    private int colorTxtStudentUnavailable;
    private int colorTxtStudentAvailable;
    private int colorStudentPresent;
    private int colorStudentAbsent;

    public AttendanceLogGridAdapter(Context context, int maxRollNo, String presentCSV, String absentCSV)
    {
        this.context = context;
        this.maxRollNo = maxRollNo;

        formDataSetFromCSV(presentCSV, absentCSV);
        getColorReferences();
    }










    @Override
    public int getCount() {
        return maxRollNo;
    }

    @Override
    public Object getItem(int i) {
        return studentDataHashMap.get(String.valueOf(i+1));
    }

    @Override
    public long getItemId(int i) {
        return i+1;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup)
    {
        View v = view;
        ViewHolder holder;

        if(view == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.attendance_log_gridview_item, viewGroup, false);
            holder = new ViewHolder(v);
            v.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) v.getTag();
        }


        GridData temp = studentDataHashMap.get(String.valueOf(i+1));
        holder.setRollNo(String.valueOf(i+1));
        if(temp.existsInClass)
        {
            holder.setPresent(temp.present);
        }




        return v;
    }














    //////////////////////////////////////////////////////////////////////////////// Utils
    private void formDataSetFromCSV(String presentCSV, String absentCSV)
    {
        ArrayList<String> rollNoPresentList = new ArrayList<>(Arrays.asList(new String[]{presentCSV}));
        ArrayList<String> rollNoAbsentList = new ArrayList<>(Arrays.asList(new String[]{absentCSV}));

        if(presentCSV.contains(","))
        {
            rollNoPresentList = new ArrayList<>(Arrays.asList(presentCSV.split(",")));
        }

        if(absentCSV.contains(","))
        {
            rollNoAbsentList = new ArrayList<>(Arrays.asList(absentCSV.split(",")));
        }

        studentDataHashMap = new HashMap<>();

        for(int i=1;i<=maxRollNo;i++)
        {
            if(rollNoPresentList.contains(String.valueOf(i)))
            {
                studentDataHashMap.put(String.valueOf(i), new GridData(i, true, true));
            }
            else if(rollNoAbsentList.contains(String.valueOf(i)))
            {
                studentDataHashMap.put(String.valueOf(i), new GridData(i, true, false));
            }
            else
            {
                studentDataHashMap.put(String.valueOf(i), new GridData(i, false));
            }
        }
    }

    private void getColorReferences()
    {
        if(Build.VERSION.SDK_INT >= 23)
        {
            colorTxtStudentUnavailable = context.getColor(R.color.colorTxtStudentUnavailable);
            colorTxtStudentAvailable = context.getColor(R.color.colorTxtStudentAvailable);
            colorStudentPresent = context.getColor(R.color.colorStudentPresent);
            colorStudentAbsent = context.getColor(R.color.colorStudentAbsent);
        }
        else
        {
            colorTxtStudentUnavailable = ContextCompat.getColor(context, R.color.colorTxtStudentUnavailable);
            colorTxtStudentAvailable = ContextCompat.getColor(context, R.color.colorTxtStudentAvailable);
            colorStudentPresent = ContextCompat.getColor(context, R.color.colorStudentPresent);
            colorStudentAbsent = ContextCompat.getColor(context, R.color.colorStudentAbsent);
        }
    }
    //////////////////////////////////////////////////////////////////////////////// Utils




























    //////////////////////////////////////////////////////////////////////////////// Owned Data
    private class ViewHolder
    {
        public TextView rollNoTextView;

        ViewHolder(View v)
        {
            rollNoTextView = (TextView) v.findViewById(R.id.list_item_roll_no);
        }

        public void setPresent(boolean condition)
        {
            if(condition)
            {
                rollNoTextView.setBackgroundTintList(ColorStateList.valueOf(colorStudentPresent));
                rollNoTextView.setBackgroundTintMode(PorterDuff.Mode.SCREEN);
                rollNoTextView.setTextColor(colorTxtStudentAvailable);
            }
            else
            {
                rollNoTextView.setTextColor(colorTxtStudentAvailable);
            }
        }

        public void setRollNo(String rollNo)
        {
            rollNoTextView.setText(rollNo);
        }
    }

    private class GridData
    {
        public int rollNo;
        public boolean existsInClass = false;
        public boolean present = false;

        GridData(int rollNo, boolean existsInClass)
        {
            this.rollNo = rollNo;
            this.existsInClass = existsInClass;
        }

        GridData(int rollNo, boolean existsInClass, boolean present)
        {
            this.rollNo = rollNo;
            this.existsInClass = existsInClass;
            this.present = present;
        }

        public void setPresent(boolean temp)
        {
            this.present = temp;
        }
    }
    //////////////////////////////////////////////////////////////////////////////// Owned Data
}
