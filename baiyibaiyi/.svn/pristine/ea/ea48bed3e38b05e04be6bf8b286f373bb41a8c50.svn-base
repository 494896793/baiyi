package www.qisu666.com.view;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import www.qisu666.com.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

public class LoadingDialog extends ProgressDialog {

    private String _msg;
    private Activity _c;
    private TextView tvLoading;

    public LoadingDialog(Activity context, String msg) {
        super(context);
        this._msg = msg;
        this._c = context;
    }

    public LoadingDialog(Context context, int theme, String msg) {
        super(context, theme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.view_progress_dialog);
        LinearLayout layout = (LinearLayout) findViewById(R.id.dialog_view);// 加载布局
//        ImageView ivLoading = (ImageView) findViewById(R.id.ivLoading);
//        Glide.with(_c)
//                .load(R.mipmap.loading)
//                .asGif()
//                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
//                .into(ivLoading);
        tvLoading = (TextView) findViewById(R.id.tipTextView);// 提示文字
//        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
//                _c, R.anim.loading_animation);
//        spaceshipImage.startAnimation(hyperspaceJumpAnimation);
        if (TextUtils.isEmpty(_msg)) {
            tvLoading.setVisibility(View.GONE);
        } else {
            tvLoading.setVisibility(View.VISIBLE);
            tvLoading.setText(_msg);
        }
        setIndeterminate(true);
        setCanceledOnTouchOutside(false);
        setCancelable(false);
        setContentView(layout, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));// 设置布局
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        WindowManager m = _c.getWindowManager();
        Display d = m.getDefaultDisplay();
        lp.height = (int) (d.getHeight() * 0.4); // 高度设置为屏幕的0.6
        lp.width = (int) (d.getWidth() * 0.7);
        getWindow().setAttributes(lp);
    }

    public void setText(String message) {
        this._msg = message;
        if (TextUtils.isEmpty(message)) {
            tvLoading.setVisibility(View.GONE);
        } else {
            tvLoading.setVisibility(View.VISIBLE);
            tvLoading.setText(this._msg);
        }
    }
}
