package com.supercompany.alexeyp.github.tools;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Detect internet connection.
 */
public class InternetDetector {

    /**
     * Activity context.
     */
    private Context context;

    public InternetDetector(Context context) {
        this.context = context;
    }

    /**
     * Is app connected to internet.
     * @return true if connected, else false.
     */
    public boolean isConnectedToInternet() {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }

        }
        return false;
    }
}
