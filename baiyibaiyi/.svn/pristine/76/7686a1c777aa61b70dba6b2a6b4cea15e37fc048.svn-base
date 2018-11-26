package me.iwf.photopicker.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupWindow;

/**
 * @author wujiancheng
 *         对PopupWindow的封装
 */
public class PopupWindowWrap {
    private static final String TAG = PopupWindowWrap.class.getSimpleName();
    private static final float ALPHA_SHOW = 0.5f;
    private static final float ALPHA_DISMISS = 1.0f;

    private Context mContext;
    private PopupWindow mPopupWindow = null;
    private boolean mIsChangeWindowBg = true;

    public PopupWindowWrap(Context context) {
        this.mContext = context;
        mPopupWindow = new PopupWindow(context.getApplicationContext());
        mPopupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        mPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        mPopupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(context, android.R.color.transparent)));
    }

    /**
     * 设置内容
     *
     * @param contentView          布局view
     * @param onCreatedPPWListener ppw中的视图创建完成后的回调
     * @return PopupWindowWrap
     */
    public PopupWindowWrap setContentView(@NonNull View contentView, OnCreatedPPWListener onCreatedPPWListener) {
        if (null != mPopupWindow) {
            mPopupWindow.setContentView(contentView);
            if (null != onCreatedPPWListener) {
                onCreatedPPWListener.onCreatedPPW(contentView);
            }
        } else {
            Log.e(TAG, "PopupWindow还没有初始化，请先调用getInstance()初始化");
        }
        return this;
    }

    /**
     * 设置内容
     *
     * @param layoutId             布局id
     * @param onCreatedPPWListener ppw中的视图创建完成后的回调
     * @return PopupWindowWrap
     */
    public PopupWindowWrap setContentView(@NonNull int layoutId, OnCreatedPPWListener onCreatedPPWListener) {
        if (null != mPopupWindow) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View contentView = inflater.inflate(layoutId, null);
            mPopupWindow.setContentView(contentView);
            if (null != onCreatedPPWListener) {
                onCreatedPPWListener.onCreatedPPW(contentView);
            }
        } else {
            Log.e(TAG, "PopupWindow还没有初始化，请先调用getInstance()初始化");
        }
        return this;
    }

    /**
     * 设置宽度
     *
     * @param width 宽度
     * @return PopupWindowWrap
     */
    public PopupWindowWrap setWidth(int width) {
        if (null != mPopupWindow) {
            mPopupWindow.setWidth(width);
        } else {
            Log.e(TAG, "PopupWindow还没有初始化，请先调用getInstance()初始化");
        }
        return this;
    }

    /**
     * 设置高度
     *
     * @param height 高度
     * @return PopupWindowWrap
     */
    public PopupWindowWrap setHeight(int height) {
        if (null != mPopupWindow) {
            mPopupWindow.setHeight(height);
        } else {
            Log.e(TAG, "PopupWindow还没有初始化，请先调用getInstance()初始化");
        }
        return this;
    }

    /**
     * 设置弹出和消失动画
     *
     * @param animationStyle 动画风格
     * @return PopupWindowWrap
     */
    public PopupWindowWrap setAnim(int animationStyle) {
        if (null != mPopupWindow) {
            mPopupWindow.setAnimationStyle(animationStyle);
        } else {
            Log.e(TAG, "PopupWindow还没有初始化，请先调用getInstance()初始化");
        }
        return this;
    }

    /**
     * 设置背景
     *
     * @param colorId 颜色资源id:R.color.xxx
     * @return PopupWindowWrap
     */
    public PopupWindowWrap setBackgroundColor(int colorId) {
        if (null != mPopupWindow) {
            mPopupWindow.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(mContext, colorId)));
        } else {
            Log.e(TAG, "PopupWindow还没有初始化，请先调用getInstance()初始化");
        }
        return this;
    }

    /**
     * 设置背景
     *
     * @param drawable drawable
     * @return PopupWindowWrap
     */
    public PopupWindowWrap setBackgroundDrawable(Drawable drawable) {
        if (null != mPopupWindow) {
            mPopupWindow.setBackgroundDrawable(drawable);
        } else {
            Log.e(TAG, "PopupWindow还没有初始化，请先调用getInstance()初始化");
        }
        return this;
    }

    /**
     * 设置PopupWindow显示或消失时窗口是否要改变背景颜色
     *
     * @param isChangeWindowBg ppw显示时是否要改变窗口的背景颜色
     */
    public PopupWindowWrap isChangeWindowBg(boolean isChangeWindowBg) {
        this.mIsChangeWindowBg = isChangeWindowBg;
        return this;
    }

    /**
     * 设置消失监听
     *
     * @param onDismissListener 消失监听器
     * @return PopupWindowWrap
     */
    public PopupWindowWrap setOnDismissListener(PopupWindow.OnDismissListener onDismissListener) {
        if (null != mPopupWindow) {
            mPopupWindow.setOnDismissListener(onDismissListener);
        } else {
            Log.e(TAG, "setOnDismissListener PopupWindow还没有初始化，请先调用getInstance()初始化");
        }
        return this;
    }

    /**
     * 点击返回键或点击空白处是否可以消失
     *
     * @return PopupWindowWrap
     */
    public PopupWindowWrap canPPWDismiss(boolean canDismiss) {
        mPopupWindow.setOutsideTouchable(canDismiss);
        mPopupWindow.setFocusable(canDismiss);
        return this;
    }

    /**
     * 是否显示
     *
     * @return
     */
    public boolean isShowing() {
        return mPopupWindow.isShowing();
    }

    public void showAtLocation(View parent, int gravity, int x, int y) {
        setPopupWindowOutsideBg(ALPHA_SHOW);
        if (null != mPopupWindow) {
            mPopupWindow.showAtLocation(parent, gravity, x, y);
        } else {
            Log.e(TAG, "PopupWindow还没有初始化，请先调用getInstance()初始化");
        }
    }

    public void showAsDropDown(View anchor, int xoff, int yoff) {
        setPopupWindowOutsideBg(ALPHA_SHOW);
        if (null != mPopupWindow) {
            mPopupWindow.showAsDropDown(anchor, xoff, yoff);
        } else {
            Log.e(TAG, "PopupWindow还没有初始化，请先调用getInstance()初始化");
        }
    }

    public void dismiss() {
        setPopupWindowOutsideBg(ALPHA_DISMISS);
        mPopupWindow.dismiss();
    }

    /**
     * 设置PopupWindow显示或消失时的窗口黑背景透明程度
     *
     * @param alpha 透明度 0 -- 1.0
     */
    private void setPopupWindowOutsideBg(float alpha) {
        if (!mIsChangeWindowBg) {
            return;
        }
        if (mContext instanceof Activity) {
            Window window = ((Activity) mContext).getWindow();
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.alpha = alpha;
            window.setAttributes(layoutParams);
        } else {
            Log.e(TAG, "传入的context必须为Activity的context");
        }
    }

    /**
     * ppw中的视图创建完成后的回调接口
     */
    public interface OnCreatedPPWListener {
        void onCreatedPPW(View contentView);
    }
}
