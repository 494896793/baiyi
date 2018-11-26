package com.asa.okvolley;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.LruCache;

import com.android.volley.toolbox.ImageLoader.ImageCache;

public class LruBitmapCache extends LruCache<String, Bitmap> implements ImageCache
{
    private LruBitmapCache(final int maxSize)
    {
        super(maxSize);
    }

    public LruBitmapCache(@NonNull final Context ctx)
    {
        this(getCacheSize(ctx));
    }

    @Override
    protected int sizeOf(String key, Bitmap value)
    {
        return value.getRowBytes() * value.getHeight();
    }

    @Override
    public Bitmap getBitmap(String url)
    {
        return get(url);
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap)
    {
        put(url, bitmap);
    }

    /**
     * @param context to get the resources from
     *
     * @return a cache size equal to approximately three screens worth of images.
     */
    private static int getCacheSize(@NonNull final Context context)
    {
        final DisplayMetrics displayMetrics = context.getResources().
                getDisplayMetrics();
        final int screenWidth = displayMetrics.widthPixels;
        final int screenHeight = displayMetrics.heightPixels;
        // 4 bytes per pixel
        final int screenBytes = screenWidth * screenHeight * 4;
        Log.d("getCacheSize",screenBytes * 3+"");
        return screenBytes * 3;
    }
}