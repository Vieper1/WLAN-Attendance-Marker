package com.kvm.automaticattendancemarker.wifimanager;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.ArrayList;

/**
 * Created by Viper GTS on 11/11/2016.
 */

public class WifiApManager
{
    private final WifiManager wifiManager;
    private final Context context;


    public WifiApManager(Context context)
    {
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        this.context = context;
    }


    public boolean isApOn(Context context)
    {
        try
        {
            Method method = wifiManager.getClass().getMethod("isWifiApEnabled");
            method.setAccessible(true);
            return (Boolean) method.invoke(wifiManager);
        }
        catch(Exception e)
        {
            // Ignore exception
        }
        return false;
    }


    public boolean configApState(Context context)
    {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiConfiguration wifiConfiguration = null;

        try
        {
            if(isApOn(context))
            {
                wifiManager.setWifiEnabled(false);
            }
            Method method = wifiManager.getClass().getMethod("setWifiEnabled", WifiConfiguration.class, boolean.class);
            method.invoke(wifiManager, wifiConfiguration, !isApOn(context));

        }
        catch(Exception e)
        {
            quickToaster(context, "WIFI ENABLE FUNCTION CALL FAILED!");
            return false;
        }

        return true;
    }


    public ArrayList<ProfessorScanResult> getUpList(boolean onlyReachable, int reachableTimeout)
    {
        BufferedReader br = null;
        ArrayList<ProfessorScanResult> result = null;

        try
        {
            result = new ArrayList<>();
            br = new BufferedReader(new FileReader("/proc/net/arp"));
            String line;

            while ((line = br.readLine()) != null)
            {
                String[] splitted = line.split(" +");

                if ((splitted != null) && (splitted.length >= 4))
                {
                    // Basic sanity check
                    String mac = splitted[3];

                    if (mac.matches("..:..:..:..:..:.."))
                    {
                        boolean isReachable = InetAddress.getByName(splitted[0]).isReachable(reachableTimeout);

                        if (!onlyReachable || isReachable)
                        {
                            result.add(new ProfessorScanResult(splitted[0], splitted[3], splitted[5], isReachable));
                        }
                    }
                }
            }
        }
        catch(Exception e)
        {
            quickToaster(context, "getUpList FAILED");
        }
        finally
        {
            try
            {
                br.close();
            }
            catch (IOException e)
            {
                Log.e(this.getClass().toString(), e.getMessage());
            }
        }
        return result;
    }


    //////////////////////////////////////////////////////////////////////////////// Can Be Used
    private void quickToaster(Context context, String message)
    {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
    //////////////////////////////////////////////////////////////////////////////// Can Be Used
}
