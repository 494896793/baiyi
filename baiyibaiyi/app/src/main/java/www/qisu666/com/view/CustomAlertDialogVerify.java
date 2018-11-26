package www.qisu666.com.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import www.qisu666.com.R;

/**
 * 自定义提示框 <br/>
 * 2015-9-18-下午12:05:17
 *
 * @author Wu Jiancheng
 */
public class CustomAlertDialogVerify extends Dialog {

    private static final String TAG = CustomAlertDialogVerify.class.getSimpleName();
    private Context mContext;
    private static CustomAlertDialogVerify mAlertDialog = null;
    private TextView tvChooseAlbum;
    private TextView tvChooseCamera;

    private View.OnClickListener onAlbumClickListener;
    private View.OnClickListener onCameraClickListener;

    private CustomAlertDialogVerify(Context context) {
        super(context);
        setOwnerActivity((Activity) context);
        mContext = context;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate CustomAlertDialog");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_alert_content_verify);
        initView();
        initData();

        if (mAlertDialog != null) {
            // 位置
            Window window = mAlertDialog.getWindow();
            window.setGravity(Gravity.CENTER); // dialog显示的位置
            window.setWindowAnimations(R.style.AnimBottom); // 添加动画
            window.setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    /**
     * @param context
     * @return
     */
    public static CustomAlertDialogVerify getAlertDialog(Context context) {
        if (mAlertDialog != null) {
            mAlertDialog.dismiss();
        }
        mAlertDialog = new CustomAlertDialogVerify(context);
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
    public static CustomAlertDialogVerify getAlertDialog(Context context, boolean cancelable, boolean canceledOnTouchOutside) {
        if (mAlertDialog != null) {
            mAlertDialog.dismiss();
        }
        mAlertDialog = new CustomAlertDialogVerify(context);
        mAlertDialog.setCancelable(cancelable);
        mAlertDialog.setCanceledOnTouchOutside(canceledOnTouchOutside);

        return mAlertDialog;
    }

    private void initView() {
        tvChooseAlbum = (TextView) findViewById(R.id.tvChooseAlbum);
        tvChooseCamera = (TextView) findViewById(R.id.tvChooseCamera);
    }

    private void initData() {
        if (null != onAlbumClickListener) {
            tvChooseAlbum.setOnClickListener(onAlbumClickListener);
        }
        if (null != onCameraClickListener) {
            tvChooseCamera.setOnClickListener(onCameraClickListener);
        }
    }

    /**
     * 选择相册
     *
     * @param onAlbumClickListener
     */
    public CustomAlertDialogVerify setOnAlbumClickListener(View.OnClickListener onAlbumClickListener) {
        this.onAlbumClickListener = onAlbumClickListener;
        return mAlertDialog;
    }

    /**
     * 选择相机
     *
     * @param onCameraClickListener
     */
    public CustomAlertDialogVerify setOnCameraClickListener(View.OnClickListener onCameraClickListener) {
        this.onCameraClickListener = onCameraClickListener;
        return mAlertDialog;
    }
}
