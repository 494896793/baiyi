package www.qisu666.com.utils;

import android.graphics.Bitmap;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * Created by xiao on 2015/10/13.
 */
public class BitmapList {
    private static BitmapList instance = new BitmapList();
    private Map<String, SoftReference<Bitmap>> softReferenceMap = new HashMap<String, SoftReference<Bitmap>>();
    synchronized public static BitmapList getInstance() {
        return instance;
    }
    public void saveBitMap(String key, Bitmap bitmap) {
        softReferenceMap.put(key, new SoftReference<Bitmap>(bitmap));
    }

    public Bitmap getCachedBitMap(String key) {
        Bitmap bitmap = null;
        if (softReferenceMap.containsKey(key)) {
            bitmap = softReferenceMap.get(key).get();
        }
        return bitmap;
    }
}
