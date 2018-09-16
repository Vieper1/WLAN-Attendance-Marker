package com.kvm.automaticattendancemarker.utilities.lists.customlist;

/**
 * Created by Viper GTS on 03-Jan-17.
 */

public class AddStudentsListContent
{
    String  SSID;
    String  displayName;
    boolean selected = false;

    public AddStudentsListContent(String SSID, boolean selected)
    {
        this.SSID = SSID;
        this.selected = selected;

        String[] tempArray = SSID.split(":");
        displayName = tempArray[3]+" "+tempArray[4];
    }















    public boolean isSelected()
    {
        return selected;
    }

    public void setSelected(boolean selected)
    {
        this.selected = selected;
    }

    public String getSSID()
    {
        return SSID;
    }

    public void setSSID(String SSID)
    {
        this.SSID = SSID;
        String[] tempArray = SSID.split(":");
        displayName = tempArray[3]+" "+tempArray[4];
    }

    public String getDisplayName()
    {
        return displayName;
    }
}
