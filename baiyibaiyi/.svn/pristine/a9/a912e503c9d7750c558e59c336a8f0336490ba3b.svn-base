package www.qisu666.com.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.TextUtils;
import android.util.LruCache;
import android.view.View;
import android.widget.ImageView;

import www.qisu666.com.utils.weixinpay.MD5Util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by YiXunHuan03 on 2016/7/27.
 */
public class LoadingPicturesUtils {
    private static LoadingPicturesUtils loadingPicturesUtils;
    // 图片缓存技术的核心类，用于缓存所有下载好的图片，在程序内存达到设定值时会//将最少最近使用的图片移除掉。
    static private LruCache<String, Bitmap> mMemoryCache;
    public static final String CACHE_PATH = Environment
            .getExternalStorageDirectory().getAbsolutePath() + "/ybcx_cache_52";
    private MD5Util md5;
    private Context mContext;

    public static void getLruCache() {
        // 获取应用程序最大可用内存
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 8;
        // 设置图片缓存大小为程序最大可用内存的1/8
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount();
            }
        };
    }

    public static LoadingPicturesUtils getIn() {
        if (loadingPicturesUtils == null) {
            getLruCache();
            loadingPicturesUtils = new LoadingPicturesUtils();

        }
        return loadingPicturesUtils;
    }

    public Bitmap getBitmapFormURL(String urlstring) {
        Bitmap bitmap;
        InputStream is = null;
        try {
            URL url = new URL(urlstring);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            is = new BufferedInputStream(connection.getInputStream());
            bitmap = BitmapFactory.decodeStream(is);
            connection.disconnect();
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                //  is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 给ImageView设置图片。首先从LruCache中取出图片的缓存，设置到ImageView上。如果LruCache中没有该图片的缓存，
     * 就给ImageView设置一张默认图片。
     *
     * @param imageUrl  图片的URL地址，用于作为LruCache的键。
     * @param imageView 用于显示图片的控件。
     */
    public void setImageView(ImageView imageView, String imageUrl) {
        Bitmap forLocation = getBitmapFromLocal(imageUrl);
        Bitmap lruBitmap = getBitmapFromMemoryCache(imageUrl);
        if (lruBitmap != null) {
            imageView.setImageBitmap(lruBitmap);
        } else if (forLocation != null) {
            imageView.setImageBitmap(forLocation);
        } else {
            new NewsAsyncTask(imageView, imageUrl).execute(imageUrl);
        }
    }

    /**
     * 给view设置图片。首先从LruCache中取出图片的缓存，view。如果LruCache中没有该图片的缓存，
     * view。
     *
     * @param imageUrl  图片的URL地址，用于作为LruCache的键。
     * @param view 用于显示图片的控件。
     */
    public void setBgView(View bgView, String imageUrl) {
        Bitmap forLocation = getBitmapFromLocal(imageUrl);
        Bitmap lruBitmap = getBitmapFromMemoryCache(imageUrl);
        if (lruBitmap != null) {
            Drawable drawable = new BitmapDrawable(lruBitmap);
            bgView.setBackgroundDrawable(drawable);
        } else if (forLocation != null) {
            Drawable drawable = new BitmapDrawable(forLocation);
            bgView.setBackgroundDrawable(drawable);
        } else {
            new bgAsyncTask(bgView, imageUrl).execute(imageUrl);
        }
    }

    /**
     * 将一张图片存储到LruCache中。
     *
     * @param key    LruCache的键，这里传入图片的URL地址。
     * @param bitmap LruCache的键，这里传入从网络上下载的Bitmap对象。
     */
    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemoryCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    /**
     * 从LruCache中获取一张图片，如果不存在就返回null。
     *
     * @param key LruCache的键，这里传入图片的URL地址。
     * @return 对应传入键的Bitmap对象，或者null。
     */
    public Bitmap getBitmapFromMemoryCache(String key) {
        return mMemoryCache.get(key);
    }

    /**
     * 从本地sdcard读图片
     */
    public Bitmap getBitmapFromLocal(String url) {
        try {
            String fileName = MD5Util.encode(url);
            File file = new File(CACHE_PATH, fileName);

            if (file.exists()) {
                Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(
                        file));
                return bitmap;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 向sdcard写图片
     *
     * @param url
     * @param bitmap
     */
    public void setBitmapToLocal(String url, Bitmap bitmap) {
        try {
            String fileName = MD5Util.encode(url);
            File file = new File(CACHE_PATH, fileName);

            File parentFile = file.getParentFile();
            if (!parentFile.exists()) {// 如果文件夹不存在, 创建文件夹
                parentFile.mkdirs();
            }
            // 将图片保存在本地
            bitmap.compress(Bitmap.CompressFormat.PNG, 50,
                    new FileOutputStream(file));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void showImageByAsyncTask(ImageView imageView, String uri) {
        new NewsAsyncTask(imageView, uri).execute(uri);
    }

    //图片异步加载类
    private class NewsAsyncTask extends AsyncTask<String, Void, Bitmap> {
        private ImageView mimageView;
        private String muri;

        public NewsAsyncTask(ImageView imageView, String uri) {
            mimageView = imageView;
            muri = uri;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap = getBitmapFormURL(params[0]);
            if (bitmap != null) {
                setBitmapToLocal(params[0], bitmap);
                addBitmapToMemoryCache(params[0], bitmap);
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (!TextUtils.isEmpty((String) mimageView.getTag()) && mimageView.getTag() != null && mimageView.getTag().equals(muri)) {
                mimageView.setImageBitmap(bitmap);
            }
        }
    }


    //图片异步加载类
    private class bgAsyncTask extends AsyncTask<String, Void, Bitmap> {
        private View llView;
        private String muri;

        public bgAsyncTask(View imageView, String uri) {
            llView = imageView;
            muri = uri;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap = getBitmapFormURL(params[0]);
            if (bitmap != null) {
                setBitmapToLocal(params[0], bitmap);
                addBitmapToMemoryCache(params[0], bitmap);
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (!TextUtils.isEmpty((String) llView.getTag()) && llView.getTag() != null && llView.getTag().equals(muri)) {
                Drawable drawable = new BitmapDrawable(bitmap);
                llView.setBackgroundDrawable(drawable);
            }
        }
    }
}

