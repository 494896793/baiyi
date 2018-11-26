package www.qisu666.com.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Created by jimmy on 2016/9/29.
 */

public class IntentUtli {

    public static void callPhone(Context c, String phone) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phone));
        c.startActivity(intent);
    }
}
