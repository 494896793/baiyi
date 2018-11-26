package www.qisu666.com.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import www.qisu666.com.R;
import www.qisu666.com.utils.Logger;

/**
 * 自定义提示框 <br/>
 * 2015-9-18-下午12:05:17
 *
 * @author Wu Jiancheng
 */
public class CustomAlertDialog2 extends Dialog {

    private static final String TAG = CustomAlertDialog2.class.getSimpleName();
    private Context mContext;
    private static CustomAlertDialog2 mAlertDialog = null;
    private ImageView ivDialogPromptBg;
    private TextView tvDialogTitle;
    private TextView tvPromptContent;
    private TextView tvDialogCancel;
    private TextView tvDialogConfirm;
    private View viewButtonDivide;

    private String confirmText = "";
    private String cancelText = "";
    private String message = "";
    private String title = "";
    private int imgRes = 0;//背景图片
    /**
     * 确定按钮监听
     */
    private View.OnClickListener onPositiveClickListener;
    /**
     * 取消按钮监听
     */
    private View.OnClickListener onNegaClickListener;

    private CustomAlertDialog2(Context context) {
        super(context);
        setOwnerActivity((Activity) context);
        mContext = context;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.i(TAG, "onCreate CustomAlertDialog");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_alert_content2);
        initView();
        initData();

        if (mAlertDialog != null) {
            // 位置
            Window window = mAlertDialog.getWindow();
            window.setGravity(Gravity.CENTER); // dialog显示的位置
            window.setWindowAnimations(R.style.AnimBottom); // 添加动画
            window.setBackgroundDrawableResource(android.R.color.transparent);
            //设置宽度
            setWidth(window);
        }
    }

    /**
     * @param context
     * @return
     */
    public static CustomAlertDialog2 getAlertDialog(Context context) {
        if (mAlertDialog != null) {
            mAlertDialog.dismiss();
        }
        mAlertDialog = new CustomAlertDialog2(context);
        mAlertDialog.setCancelable(false);
        mAlertDialog.setCanceledOnTouchOutside(false);

        return mAlertDialog;
    }

    /**
     * @param context
     * @param cancelable
     * @param canceledOnTouchOutside
     * @return
     */
    public static CustomAlertDialog2 getAlertDialog(Context context, boolean cancelable, boolean canceledOnTouchOutside) {
        if (mAlertDialog != null) {
            mAlertDialog.dismiss();
        }
        mAlertDialog = new CustomAlertDialog2(context);
        mAlertDialog.setCancelable(cancelable);
        mAlertDialog.setCanceledOnTouchOutside(canceledOnTouchOutside);

        return mAlertDialog;
    }

    private void initView() {
        ivDialogPromptBg = (ImageView) findViewById(R.id.iv_dialog_prompt_bg);
        tvDialogTitle = (TextView) findViewById(R.id.tvDialogTitle);
        tvPromptContent = (TextView) findViewById(R.id.tv_prompt_content);

        tvDialogCancel = (TextView) findViewById(R.id.tv_dialog_cancel);
        tvDialogConfirm = (TextView) findViewById(R.id.tv_dialog_confirm);
        viewButtonDivide = findViewById(R.id.view_button_divide);
    }

    /**
     * 将对话框的大小按屏幕大小的百分比设置
     *
     * @param window
     */
    private void setWidth(Window window) {
        int screenWidth = mContext.getResources().getDisplayMetrics().widthPixels;
        WindowManager.LayoutParams p = window.getAttributes(); //获取对话框当前的参数值
        p.width = (int) (screenWidth * 62.0 / 75.0);
        window.setAttributes(p);
    }

    private void initData() {
        // 各个控件如果没有设置就将他们隐藏
        if (imgRes != 0) {
            ivDialogPromptBg.setImageResource(imgRes);
        }

        if (message != null && !"".equals(message)) {
            tvPromptContent.setText(message);
        } else {
            tvPromptContent.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(title)) {
            tvDialogTitle.setText(title);
            tvDialogTitle.setVisibility(View.VISIBLE);
        }

        if (onPositiveClickListener == null) {
            tvDialogConfirm.setVisibility(View.GONE);
            viewButtonDivide.setVisibility(View.GONE);
        } else {
            tvDialogConfirm.setText(confirmText);
            tvDialogConfirm.setOnClickListener(onPositiveClickListener);
        }
        if (onNegaClickListener == null) {
            tvDialogCancel.setVisibility(View.GONE);
            viewButtonDivide.setVisibility(View.GONE);
        } else {
            tvDialogCancel.setText(cancelText);
            tvDialogCancel.setOnClickListener(onNegaClickListener);
        }
        if (onPositiveClickListener == null && onNegaClickListener == null) {
        }
        if (onPositiveClickListener != null && onNegaClickListener == null) {
//            tvDialogConfirm.setBackgroundResource(R.drawable.button_bottom_corner);
        }
        if (onPositiveClickListener == null && onNegaClickListener != null) {
//            tvDialogCancel.setBackgroundResource(R.drawable.button_bottom_corner);
        }
    }

    /**
     * 确定按钮
     *
     * @param character
     * @param onPositiveClickListener
     */
    public CustomAlertDialog2 setOnPositiveClickListener(String character, View.OnClickListener onPositiveClickListener) {
        confirmText = character;
        this.onPositiveClickListener = onPositiveClickListener;
        return mAlertDialog;
    }

    /**
     * 取消按钮
     *
     * @param character
     * @param onNegativeClickListener
     */
    public CustomAlertDialog2 setOnNegativeClickListener(String character, View.OnClickListener onNegativeClickListener) {
        cancelText = character;
        this.onNegaClickListener = onNegativeClickListener;
        return mAlertDialog;
    }

    /**
     * 提示内容
     *
     * @param message
     */
    public CustomAlertDialog2 setMessage(String message) {
        this.message = message;
        return mAlertDialog;
    }

    /**
     * 提示标题
     *
     * @param title
     */
    public CustomAlertDialog2 setTitle(String title) {
        this.title = title;
        return mAlertDialog;
    }

    /**
     * 设置背景图片
     *
     * @param imgRes R.mipmap.xxx
     * @return
     */
    public CustomAlertDialog2 setBackgroundImg(int imgRes) {
        this.imgRes = imgRes;
        return mAlertDialog;
    }
}
