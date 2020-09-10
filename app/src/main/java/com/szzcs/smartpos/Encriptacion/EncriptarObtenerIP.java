package com.szzcs.smartpos.Encriptacion;

import android.util.Log;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static android.support.constraint.Constraints.TAG;

public class EncriptarObtenerIP {

    public  String getIP(){
        List<InetAddress> addrs;
        String address = "";
        try{
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for(NetworkInterface intf : interfaces){
                addrs = Collections.list(intf.getInetAddresses());
                for(InetAddress addr : addrs){
                    if(!addr.isLoopbackAddress() && addr instanceof Inet4Address){
                        address = addr.getHostAddress().toUpperCase(new Locale("es", "MX"));
                    }
                }
            }
        }catch (Exception e){
            Log.w(TAG, "Ex getting IP value " + e.getMessage());
        }
        return address;
    }

    public String getIPMD5(final String s) {
        try{
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();
            StringBuilder hexString = new StringBuilder();
            for(int i = 0; i < messageDigest.length; i++){
                String h = Integer.toHexString(0xFF & messageDigest[i]);
                while(h.length() < 2){
                    h = "0" + h;
                }
                hexString.append(h);
            }
            return hexString.toString();
        }catch (NoSuchAlgorithmException e){
            Log.e("MD5", "md5() NoSuchAlgorithmException: " + e.getMessage());
        }
        return "";
    }
}
