package com.kvm.automaticattendancemarker.utilities.lists.customlist;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.kvm.automaticattendancemarker.AttendanceModeAddStudents;
import com.kvm.automaticattendancemarker.R;

import java.util.List;

public class AddStudentsListAdapter extends ArrayAdapter<AddStudentsListContent>
{
    private List<AddStudentsListContent> listContents;
    private Context context;






    public AddStudentsListAdapter(Context context, List<AddStudentsListContent> list)
    {
        super(context, R.layout.add_students_lv_layout, list);
        this.listContents = list;
        this.context = context;
    }






    private static class AddStudentsListContentHolder
    {
        public TextView hld_displayName;
        public TextView hld_SSID;
        public CheckBox hld_checkBox;
    }






    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View v = convertView;
        AddStudentsListContentHolder holder = new AddStudentsListContentHolder();

        if(convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.add_students_lv_layout, null);

            holder.hld_displayName = (TextView) v.findViewById(R.id.add_students_lv_main_text);
            holder.hld_SSID = (TextView) v.findViewById(R.id.add_students_lv_ssid_text);
            holder.hld_checkBox = (CheckBox) v.findViewById(R.id.add_students_lv_checkbox);

            holder.hld_checkBox.setOnCheckedChangeListener((AttendanceModeAddStudents) context);   ////////// Listener
        }
        else
        {
            holder = (AddStudentsListContentHolder) v.getTag();
        }

        AddStudentsListContent content = listContents.get(position);



/*
        holder.hld_SSID.setText(content.getSSID());
        holder.hld_displayName.setText(content.getDisplayName());
        holder.hld_checkBox.setChecked(content.isSelected());
        holder.hld_checkBox.setTag(content);
*/
        return v;
    }
}








