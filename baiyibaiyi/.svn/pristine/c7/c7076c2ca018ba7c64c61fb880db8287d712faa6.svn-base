package www.qisu666.com.utils;

import android.content.Context;
import android.location.LocationManager;

/**
 * GPS
 */
public class GPSUtils {
    public static boolean isOpen(Context c) {
        LocationManager locationManager = (LocationManager) c
                .getSystemService(Context.LOCATION_SERVICE);
        return locationManager != null && !locationManager.
                isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
}
