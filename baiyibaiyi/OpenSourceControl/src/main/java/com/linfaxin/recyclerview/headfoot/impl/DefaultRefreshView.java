package com.linfaxin.recyclerview.headfoot.impl;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.linfaxin.recyclerview.R;
import com.linfaxin.recyclerview.headfoot.RefreshView;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by linfaxin on 15/8/31.
 * Default impl of RefreshView
 */
public class DefaultRefreshView extends RefreshView {

    private GifImageView gifImageView;
    private GifDrawable gifDrawable;

    public DefaultRefreshView(Context context) {
        super(context);
        init();
    }

    public DefaultRefreshView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DefaultRefreshView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public DefaultRefreshView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        View view = View.inflate(getContext(), R.layout.widget_refresh_view, null);
        gifImageView = (GifImageView) view.findViewById(R.id.gifImageView);
        gifDrawable = (GifDrawable) gifImageView.getDrawable();
        addView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    @Override
    protected void onStateChange(int newState, int oldState) {
        switch (newState) {
            case STATE_NORMAL:
                gifDrawable.pause();
                break;
            case STATE_READY:
                gifDrawable.start();
                break;
            case STATE_LOADING:
                gifDrawable.start();
                break;
        }
    }
}
