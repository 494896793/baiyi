package com.linfaxin.recyclerview.headfoot.impl;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.linfaxin.recyclerview.R;
import com.linfaxin.recyclerview.headfoot.LoadMoreView;

/**
 * Created by linfaxin on 15/8/31.
 * Default impl of LoadMoreView
 */
public class DefaultLoadMoreView extends LoadMoreView {

    private Animation animation;

    private ImageView iv_loading;

    private TextView tv_loading;

    private View contentView;

    public DefaultLoadMoreView(Context context) {
        super(context);
        init();
    }

    public DefaultLoadMoreView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DefaultLoadMoreView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public DefaultLoadMoreView(Context context, AttributeSet attrs, int defStyleAttr,
                               int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        contentView = View.inflate(getContext(), R.layout.widget_load_view, null);
        iv_loading = (ImageView) contentView.findViewById(R.id.iv_loading);
        tv_loading = (TextView) contentView.findViewById(R.id.tv_loading);
        animation = AnimationUtils.loadAnimation(getContext(), R.anim.loading_animation);
        addView(contentView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    @Override
    protected void onStateChange(int state, int oldState) {
        switch (state) {
            case STATE_NORMAL:
                tv_loading.setText("加载更多");
                iv_loading.clearAnimation();
                iv_loading.setVisibility(View.GONE);
                break;
            case STATE_LOADING:
                tv_loading.setText("正在加载中...");
                iv_loading.setVisibility(View.VISIBLE);
                iv_loading.startAnimation(animation);
                break;
            case STATE_READY:
                tv_loading.setText("加载更多");
                iv_loading.clearAnimation();
                iv_loading.setVisibility(View.GONE);
                break;
            case STATE_NO_MORE:
                tv_loading.setText("没有更多");
                iv_loading.clearAnimation();
                iv_loading.setVisibility(View.GONE);
                break;
            case STATE_LOAD_FAIL:
                tv_loading.setText("加载失败");
                iv_loading.clearAnimation();
                iv_loading.setVisibility(View.GONE);
                break;
            case STATE_EMPTY_RELOAD:
                tv_loading.setText("加载更多");
                iv_loading.clearAnimation();
                iv_loading.setVisibility(View.GONE);
                break;
        }
    }

}
