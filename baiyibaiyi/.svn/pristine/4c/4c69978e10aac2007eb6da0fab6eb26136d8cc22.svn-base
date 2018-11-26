package www.qisu666.com.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import www.qisu666.com.R;
import www.qisu666.com.activity.WebActivity;
import www.qisu666.com.utils.Logger;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

/**
 * 自定义提示框 <br/>
 * 2015-9-18-下午12:05:17
 *
 * @author Wu Jiancheng
 */
public class CustomAlertDialogAd extends Dialog {

    private static final String TAG = CustomAlertDialogAd.class.getSimpleName();
    public static final int AD = 0;//广告
    public static final int PARK_COST = 1;//停车费
    private Context mContext;
    private static CustomAlertDialogAd mAlertDialog = null;
    private ImageView iv_dialog_prompt_bg;
    private ImageView iv_dialog_prompt_bg_parking;
    private ImageView iv_dialog_delete;
    private String imgUrl;
    private String adUrl;
    private int type = 0;

    private CustomAlertDialogAd(Context context) {
        super(context);
        setOwnerActivity((Activity) context);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.i(TAG, "onCreate CustomAlertDialog");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_alert_content_ad);
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
    public static CustomAlertDialogAd getAlertDialog(Activity context) {
        if (mAlertDialog != null) {
            mAlertDialog.dismiss();
        }
        mAlertDialog = new CustomAlertDialogAd(context);
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
    public static CustomAlertDialogAd getAlertDialog(Activity context, boolean cancelable, boolean canceledOnTouchOutside) {
        if (mAlertDialog != null && !context.isFinishing()) {
            mAlertDialog.dismiss();
        }
        mAlertDialog = new CustomAlertDialogAd(context);
        mAlertDialog.setCancelable(cancelable);
        mAlertDialog.setCanceledOnTouchOutside(canceledOnTouchOutside);

        return mAlertDialog;
    }

    private void initView() {

        iv_dialog_delete = (ImageView) findViewById(R.id.iv_dialog_delete);
        iv_dialog_prompt_bg = (ImageView) findViewById(R.id.iv_dialog_prompt_bg);
        iv_dialog_prompt_bg_parking = (ImageView) findViewById(R.id.iv_dialog_prompt_bg_parking);

        iv_dialog_prompt_bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != adUrl && !"".equals(adUrl)) {
                    Intent intent = new Intent(mContext, WebActivity.class);
                    intent.putExtra("title", "活动");
                    intent.putExtra("url", adUrl);
                    mContext.startActivity(intent);
                    dismiss();
                }
            }
        });
        iv_dialog_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private void initData() {
        if (null != imgUrl && !"".equals(imgUrl)) {
            if (type == AD) {
                Glide.with(mContext).load(imgUrl).asBitmap().into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        iv_dialog_prompt_bg.setImageBitmap(resource);
//                    iv_dialog_prompt_bg.setVisibility(View.VISIBLE);
//                    iv_dialog_delete.setVisibility(View.VISIBLE);
                    }
                });
            }else if (type == PARK_COST){
                Glide.with(mContext).load(imgUrl).asBitmap().into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        iv_dialog_prompt_bg_parking.setImageBitmap(resource);
                        iv_dialog_prompt_bg.setVisibility(View.GONE);
                        iv_dialog_prompt_bg_parking.setVisibility(View.VISIBLE);

                    }
                });
            }
        }
    }

    public CustomAlertDialogAd setAd(String imgUrl, String adUrl, int type) {
        this.imgUrl = imgUrl;
        this.adUrl = adUrl;
        this.type = type;
        return mAlertDialog;
    }

}
