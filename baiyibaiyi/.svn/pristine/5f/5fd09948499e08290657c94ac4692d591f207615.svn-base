package com.asa.okvolley;

import android.content.Context;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import okhttp3.OkHttpClient;

public class OKVollyRequestQueenManager {

	public static final String DEFAULT_REQ_TAG = "default_request_tag";

	private RequestQueue mRequestQueue;
	private ImageLoader mImageLoader;


	private Context context;

	private LruBitmapCache mLruBitmapCache;
	
	/**
	 * 
	 * @param context 传入一个context
	 */
	public OKVollyRequestQueenManager(Context context){
		this.context=context;
		mRequestQueue = Volley.newRequestQueue(context,new OkHttpStack(new OkHttpClient.Builder().build()));
	}

	public RequestQueue getRequestQueue() {
	    return mRequestQueue;
	}

	public ImageLoader getImageLoader() {
	    getRequestQueue();
	    if (mImageLoader == null) {
	        mImageLoader = new ImageLoader(this.mRequestQueue,
	        		getVolleyImageCache());
	    }
	    return this.mImageLoader;
	}
	
	private LruBitmapCache getVolleyImageCache()
    {
        if (mLruBitmapCache == null)
        {
            mLruBitmapCache = new LruBitmapCache(context);
        }
        return mLruBitmapCache;
    }

	public <T> void addToRequestQueue(Request<T> req, String tag) {
	    // set the default tag if tag is empty
	    req.setTag(TextUtils.isEmpty(tag) ? DEFAULT_REQ_TAG : tag);
	    getRequestQueue().add(req);
//	    new JsonObjectRequest(method, url, jsonRequest, listener, errorListener)
//	    new StringRequest(method, url, listener, errorListener)
	}

	public <T> void addToRequestQueue(Request<T> req) {
	    req.setTag(DEFAULT_REQ_TAG);
	    getRequestQueue().add(req);
	}

	public void cancelPendingRequests(Object tag) {
	    if (mRequestQueue != null) {
	        mRequestQueue.cancelAll(tag);
	    }
	}
	
	
}
