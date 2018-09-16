package com.kvm.automaticattendancemarker.wifimanager;

/**
 * Created by Viper GTS on 11/11/2016.
 */

public class ProfessorScanResult
{
    private String IpAddress;
    private String HWAddress;
    private String Device;
    private Boolean isReachable;

    public ProfessorScanResult(String ipAddress, String hWAddress, String device, boolean isReachable)
    {
        super();
        this.IpAddress = ipAddress;
        this.HWAddress = hWAddress;
        this.Device = device;

    }

    public String getIpAddress()
    {
        return IpAddress;
    }

    public String getHWAddress()
    {
        return HWAddress;
    }

    public boolean isReachable()
    {
        return isReachable;
    }

    public void setIsReachable(boolean isReachable)
    {
        this.isReachable = isReachable;
    }


}
