package com.ofoegbuvgmail.journalboss.utills;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class InternetUtils {

    public static boolean isConnectedToInternet(Context context){
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

//            if(cm.getActiveNetworkInfo() != null){
//                try {
//                    InetAddress ipAddr = InetAddress.getByName("google.com");
//                    //You can replace it with your name
//                    return !ipAddr.equals("");
//
//                } catch (Exception e) {
//                    return false;
//                }
//            }
//
//            return false;
            return cm.getActiveNetworkInfo() != null;
    }

    public static class CheckNetworkState implements Runnable {
        private volatile boolean isConnected;
        private Context context;

        public CheckNetworkState(Context context) {
            this.context = context;
        }

        @Override
        public void run() {
            isConnected = isConnectedToInternet(context);
        }

        public boolean getValue() {
            return isConnected;
        }
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager
                cm = (ConnectivityManager) context.getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null
                && activeNetwork.isConnectedOrConnecting();

//        return false;
    }
}
