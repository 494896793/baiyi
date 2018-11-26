package www.qisu666.com.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;

/**
 * 图片bitmap引用释放
 * Created by xiao on 2015/10/13.
 */
public class RecycleUtils {

    /**
     * 图片本地展示
     * @param mContext
     * @param iv
     * @param path
     */
    public static  void bmSet(Context mContext, ImageView iv, String path) {
        InputStream is = null;
        try {
            is = mContext.getAssets().open(path);
            Bitmap bmp = BitmapFactory.decodeStream(is);
            iv.setImageBitmap(bmp);
            BitmapList.getInstance().saveBitMap(path,bmp);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(is != null) {
                try {
                    is.close();
                } catch (Exception var2) {
                }
            }
        }
    }
}
