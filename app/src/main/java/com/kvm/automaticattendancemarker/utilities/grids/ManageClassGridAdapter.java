package com.kvm.automaticattendancemarker.utilities.grids;

import android.content.Context;
import android.content.res.ColorStateList;
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
 * Created by Viper GTS on 28-Jan-17.
 */

public class ManageClassGridAdapter extends BaseAdapter
{
    private Context context;
    private int maxRollNo;
    private HashMap<String, GridData> studentDataHashMap;

    private int colorTxtStudentAvailable;

    public ManageClassGridAdapter(Context context, int maxRollNo, String existsInClassCSV)
    {
        this.context = context;
        this.maxRollNo = maxRollNo;

        formDataSetFromCSV(existsInClassCSV);
        getColorReferences();
    }


    @Override
    public int getCount()
    {
        return maxRollNo;
    }

    @Override
    public Object getItem(int i)
    {
        return studentDataHashMap.get(String.valueOf(i+1));
    }

    @Override
    public long getItemId(int i)
    {
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
        holder.setExisting(temp.existsInClass);

        return v;
    }

    public boolean getItemExisting(int i)
    {
        return studentDataHashMap.get(String.valueOf(i)).existsInClass;
    }











    //////////////////////////////////////////////////////////////////////////////// Utils
    private void formDataSetFromCSV(String existingCSV)
    {
        ArrayList<String> rollNoExistingList = new ArrayList<>(Arrays.asList(new String[]{existingCSV}));

        if(existingCSV.contains(","))
        {
            rollNoExistingList = new ArrayList<>(Arrays.asList(existingCSV.split(",")));
        }

        studentDataHashMap = new HashMap<>();

        for(int i=1;i<=maxRollNo;i++)
        {
            if(rollNoExistingList.contains(String.valueOf(i)))
            {
                studentDataHashMap.put(String.valueOf(i), new GridData(i, true));
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
            colorTxtStudentAvailable = context.getColor(R.color.colorTxtStudentAvailable);
        }
        else
        {
            colorTxtStudentAvailable = ContextCompat.getColor(context, R.color.colorTxtStudentAvailable);
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

        public void setExisting(boolean isExisting)
        {
            if(isExisting)
            {
                rollNoTextView.setTextColor(colorTxtStudentAvailable);
            }
        }

        public void setRollNo(String rollNo)
        {
            rollNoTextView.setText(rollNo);
        }
    }

    protected class GridData
    {
        public int rollNo;
        public boolean existsInClass = false;

        GridData(int rollNo, boolean existsInClass)
        {
            this.rollNo = rollNo;
            this.existsInClass = existsInClass;
        }
    }
    //////////////////////////////////////////////////////////////////////////////// Owned Data
}
