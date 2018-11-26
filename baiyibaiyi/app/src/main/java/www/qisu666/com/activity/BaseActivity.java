package www.qisu666.com.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import www.qisu666.com.R;
import www.qisu666.com.app.MyApplication;
import www.qisu666.com.constant.Config;
import www.qisu666.com.receiver.PublicReceiver;
import www.qisu666.com.request.RequestBaseParams;
import www.qisu666.com.root.IBase;
import www.qisu666.com.root.RequestUtil;
import www.qisu666.com.utils.ActivityRootStackControlUtil;
import www.qisu666.com.utils.CacheUtils;
import www.qisu666.com.utils.DataUtils;
import www.qisu666.com.utils.GPSUtils;
import www.qisu666.com.utils.Logger;
import www.qisu666.com.utils.NetWorkUtils;
import www.qisu666.com.utils.ResultUtil;
import www.qisu666.com.utils.SBUtils;
import www.qisu666.com.utils.SharedPreferencesUtils;
import www.qisu666.com.utils.StatusBarUtil;
import www.qisu666.com.utils.ToastUtil;
import www.qisu666.com.view.CustomAlertDialog;
import www.qisu666.com.view.LoadingDialog;
import com.google.gson.JsonArray;
import com.umeng.analytics.MobclickAgent;

import org.xutils.common.Callback;
import org.xutils.x;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Subscription;


public abstract class BaseActivity extends FragmentActivity implements
        IBase {
    private static final String TAG = BaseActivity.class.getSimpleName();
    protected Context mContext;
    protected ActivityRootStackControlUtil activityUtil;
    private LoadingDialog mDialog;
    private boolean isOnKeyBack;
    protected Handler uiHandler;
    protected MyApplication application;
    protected Bundle savedInstanceState;
    protected RelativeLayout.LayoutParams layoutParams;
    private PublicReceiver mReceiver;
    private Unbinder unbinder;
    protected Subscription busSubscription;

    private OnKeyboardStatusListener onKeyboardStatusListener;

    private View mStatusBar;
    /**
     * 当前沉浸模式，默认为布局沉浸式
     */
    private String immersionType = TYPE_LAYOUT;

    /**
     * 仅仅改变状态栏颜色的沉浸模式
     */
    protected static final String TYPE_LAYOUT = "type_layout";

    /**
     * 将原布局背景扩散至状态栏的沉浸模式
     */
    protected static final String TYPE_BACKGROUND = "type_background";

    private int mStatusBarColor = Color.parseColor("#1B2130");

    /**
     * 直接将布局扩散至状态栏，不做任何处理
     */
    protected static final String TYPE_NULL = "type_null";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.savedInstanceState = savedInstanceState;
        if (savedInstanceState != null) {
            Intent intent = new Intent(this, SplashActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
//        if (this instanceof UseCarPreOrderingActivity){
//            this.setTheme(R.style.translucent);
//        }else {
            this.setTheme(R.style.MyAppTheme);
//        }

        mContext = this;
        setView();
        try{
            unbinder = ButterKnife.bind(this);
            application = (MyApplication) getApplication();
            uiHandler = new Handler(mContext.getMainLooper());
        }catch (Exception e){
            e.printStackTrace();
        }
        activityUtil = ActivityRootStackControlUtil.getInstance();
        activityUtil.onCreate(this);
        application.addActivates(this);
        if (layoutParams == null) {
            layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
        }
        initDatas();
        mReceiver = new PublicReceiver(SBUtils.token_error);
        mReceiver.setBeanReceive(new PublicReceiver.IBeanReceive() {
            @Override
            public void getBean() {
                CacheUtils.getIn().clearMyInfo();
                activityUtil.jumpTo(LoginActivity.class);

            }
        });

        if (this instanceof PersonCenterActivity && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //判断当前设备版本号是否为4.4以上，如果是，则通过调用setTranslucentStatus让状态栏变透明
            setTranslucentStatus(true);
//            StatusBarUtil.setStatusBarColor(this, R.color.color_black_262930);
        } else {
            StatusBarUtil.setStatusBarColor(this, R.color.main_background);
        }

    }

    /**
     * 子类设置布局时应调用该方法
     * @param resId 布局id
     * @param color 状态栏颜色
     */
    protected void setView(int resId, int color){
        mStatusBarColor = color;
        setView(resId);
    }

    /**
     * 子类设置布局时应调用该方法
     * @param resId 布局id
     */

    protected void setView(int resId){
        View contentView = View.inflate(this, resId, null);
        setView(contentView);
    }

    protected void setView(View contentView){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //去掉状态栏布局
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //当API>=21时，状态栏会自动增加一块半透明色块，这段代码将其设为全透明
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                        | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.TRANSPARENT);
            }
            if (immersionType.equals(TYPE_LAYOUT)) {
                LinearLayout ll_content = new LinearLayout(this);
                ll_content.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
                ll_content.setOrientation(LinearLayout.VERTICAL);
                addStatusBar(ll_content);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                ll_content.addView(contentView, lp);
                setContentView(ll_content);
            } else if (immersionType.equals(TYPE_BACKGROUND)) {
                contentView.setPadding(0, getStatusBarHeight(), 0, 0);
                setContentView(contentView);
            } else if (immersionType.equals(TYPE_NULL)){
                setContentView(contentView);
            }

        }else{
            setContentView(contentView);
        }
    }

    /**
     * 将状态栏添加至布局中
     * @param viewGroup
     */
    private void addStatusBar(ViewGroup viewGroup) {
        mStatusBar = new View(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight());
        mStatusBar.setLayoutParams(lp);
//		if(immersionType == TYPE_LAYOUT) {
        mStatusBar.setBackgroundColor(mStatusBarColor);
//		}else if(immersionType == TYPE_BACKGROUND){
//			//当浸入模式为背景浸入时，状态栏设为透明色
//			mStatusBar.setBackgroundColor(Color.TRANSPARENT);
//		}
        viewGroup.addView(mStatusBar);
    }


    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    /**
     * 获取当前设备状态栏高度
     *
     * @return
     */
    protected int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        Logger.i("状态栏高度=" + result);
        return result;
    }

    public void loginOut() {
        CacheUtils.getIn().clearMyInfo();

        final CustomAlertDialog alertDialog = CustomAlertDialog.getAlertDialog(mContext, false, false);
        alertDialog.setTitle("下线通知")
                .setMessage("您的账号刚在另一台手机登录。如非本人操作，请重新登录。")
                .setBtnCancelColor(R.color.main_background)
                .setBtnConfirmColor(R.color.new_primary)
                .setOnNegativeClickListener("退出", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        activityUtil.jumpTo(ControlerActivity.class);
                        alertDialog.dismiss();
                    }
                })
                .setOnPositiveClickListener("重新登录", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, LoginActivity.class);
                        intent.putExtra("beOut", 1);//被挤下线
                        startActivity(intent);
                        alertDialog.dismiss();
                    }
                }).show();

    }

    @Override
    public <T> List<T> getList(String result, Class<T> clazz) {
        return ResultUtil.getList(result, clazz);
    }

    @Override
    public <T> T getBean(String result, Class<T> clazz) {
        return ResultUtil.getBean(result, clazz);
    }

    @Override
    public boolean isSuccess(String result) {
        return ResultUtil.isSuccess(result);
    }

    @Override
    public String getMsg(String result) {
        return ResultUtil.getMsg(result);
    }

    protected String getCon(HashMap data, String key) {
        return ResultUtil.getCon(data, key);
    }

    @Override
    public String getCode(String result) {
        return ResultUtil.getCode(result);
    }

    @Override
    public void doPost(RequestBaseParams mBean, File mFile, final int type,
                       String title, final boolean dialog) {
        doCheck(title, dialog);
        x.http().post(RequestUtil.setRequestPost(mBean, mFile),
                new Callback.CommonCallback<String>() {

                    @Override
                    public void onSuccess(String result) {
                        close(dialog);
                        onComplete(result, type);
                        Logger.d("请求结果: " + result);
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        onFailure(request_wrong, type);
                        close(dialog);
                    }

                    @Override
                    public void onCancelled(CancelledException cex) {
                        close(dialog);
                        // onFailure(getString(R.string.cancel_request));
                    }

                    @Override
                    public void onFinished() {
                        // close(dialog);
                    }
                });
    }

    @Override
    public void doGet(final RequestBaseParams mBean, final int type,
                      final String title, final boolean dialog) {

        doCheck(title, dialog);
        x.http().get(RequestUtil.setRequest(mBean),
                new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
//                        ToastUtil.show(mContext, RequestUrls.url + mBean.getMethod());
                        Logger.d("请求结果: " + mBean.getMethod() + "：" + result);
                        close(dialog);
                        if (ResultUtil.isTokenError(result)) {
                            if (!(mContext instanceof SplashActivity)) {
                                loginOut();
                            }
                            return;
                        }
                        //账号不存在
                        if (Config.REQUEST_USER_NOT_EXIST.equals(ResultUtil.getCode(result))) {
                            SharedPreferencesUtils.putString(mContext, "PHONE", "");
                            SharedPreferencesUtils.putString(mContext, "PWD", "");
                            CacheUtils.getIn().clearMyInfo();
                            activityUtil.jumpTo(LoginActivity.class);
                            return;
                        } else if (Config.REQUEST_FAILURE.equals(ResultUtil.getCode(result))) {
                            ToastUtil.show(mContext, getMsg(result));
                        }
                        onComplete(result, type);
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        onFailure(request_wrong, type);
                        close(dialog);
                        Logger.i("请求出错：" + mBean.getMethod() + "，原因：" + ex.getMessage());
//                        ToastUtil.show(mContext, "出问题了，请稍后重试！");
                    }

                    @Override
                    public void onCancelled(CancelledException cex) {
                        close(dialog);
                    }

                    @Override
                    public void onFinished() {

                    }
                });
    }

    public void doCheck(String title, boolean dialog) {
        if (!NetWorkUtils.isNet()) {
            showToast("世界上最遥远的距离就是没有网络...");
//            return;
        }
        if (dialog) {
            createDialog(title);
        }
    }

    protected void createDialog(String title) {
        if (mDialog != null) {
            closeDialog();
        }
        mDialog = new LoadingDialog(this, title);
        if (!isFinishing()) {
            mDialog.show();
        }
    }

    private void close(boolean dialog) {
        if (dialog) {
            closeDialog();
        }
    }

    // 关闭对话框
    protected void closeDialog() {
        try {
            if (mDialog != null && mDialog.isShowing()) {
                mDialog.dismiss();
                mDialog = null;
            }
        } catch (Exception e) {
            mDialog = null;
        } finally {
            mDialog = null;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }


    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    protected void showToast(String msg) {
        ToastUtil.show(this, msg);
    }

    protected void showLongToast(String msg) {
        ToastUtil.show(this, msg);
    }

    public void doNext() {
    }

    protected void exitApplication() {
        if (isOnKeyBack) {
            uiHandler.removeCallbacks(onBackExitRunnable);
            /**直接退出*/
            application.exitApp();
        } else {
            isOnKeyBack = true;
            showToast("再按一次返回键退出应用");
            uiHandler.postDelayed(onBackExitRunnable, 2000);
        }
    }

    public Runnable onBackExitRunnable = new Runnable() {

        @Override
        public void run() {
            isOnKeyBack = false;
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
        if (busSubscription != null && !busSubscription.isUnsubscribed()) {
            busSubscription.unsubscribe();
        }
        closeDialog();
        try {
            unregisterReceiver(mReceiver);
        } catch (Exception e) {
            mReceiver = null;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            DataUtils.packupKeyBor(mContext);
        }
        return super.onTouchEvent(event);
    }

    protected void dismissProgress() {

    }

    /**
     * @param root         最外层布局，需要调整的布局
     * @param scrollToView 被键盘遮挡的scrollToView，滚动root,使scrollToView在root可视区域的底部
     */
    protected void controlKeyboardLayout(final View root, final View scrollToView) {
        // 注册一个回调函数，当在一个视图树中全局布局发生改变或者视图树中的某个视图的可视状态发生改变时调用这个回调函数。
        root.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        Rect rect = new Rect();
                        // 获取root在窗体的可视区域
                        root.getWindowVisibleDisplayFrame(rect);
                        // 当前视图最外层的高度减去现在所看到的视图的最底部的y坐标
                        int rootViewHeight = root.getRootView().getHeight();
                        int rootInvisibleHeight = rootViewHeight - rect.bottom;

                        Logger.i(TAG, "最外层的高度" + rootViewHeight);
                        Logger.i(TAG, "rect.bottom=" + rect.bottom);
                        Logger.i(TAG, "rootInvisibleHeight=" + rootInvisibleHeight);

                        // 若rootInvisibleHeight高度大于100，则说明当前视图上移了，说明软键盘弹出了
                        if (rootInvisibleHeight > 100) {
                            //软键盘弹出来的时候
                            int[] location = new int[2];
                            // 获取scrollToView在窗体的坐标
                            scrollToView.getLocationInWindow(location);
                            Logger.i(TAG, "弹出来了location x = " + location[0] + "  y = " + location[1]);
                            Logger.i(TAG, "弹出来了scrollToView.getHeight() = " + scrollToView.getHeight());
                            Logger.i(TAG, "弹出来了rect.bottom = " + rect.bottom);
                            // 计算root滚动高度，使scrollToView在可见区域的底部
//                            int srollHeight = (location[1] + scrollToView
//                                    .getHeight()) - rect.bottom;
                            int srollHeight = rootInvisibleHeight - (rootViewHeight - (location[1] + scrollToView.getHeight()));
                            Logger.i(TAG, "弹出来了,还要上滚的距离srollHeight = " + srollHeight);
                            if (srollHeight > 0) {
                                setKeyboardStatus(true);
                                root.scrollTo(0, srollHeight);
                            } else if (srollHeight < 0) {
                                setKeyboardStatus(false);
                                root.scrollTo(0, 0);
                            }
                        } else {
                            // 软键盘没有弹出来的时候
                            Logger.i(TAG, "键盘没有弹出来");
                            setKeyboardStatus(false);
                            root.scrollTo(0, 0);
                        }
                    }
                });
    }

    /**
     * 是否开启GPS
     */
    protected void isOpenGps() {
        if (GPSUtils.isOpen(this)) {
            final CustomAlertDialog alertDialog = CustomAlertDialog.getAlertDialog(mContext, false, false);
            alertDialog.setMessage("开启GPS定位，佰壹出行会给您带来更好的体验，去设置一下吧。")
                    .setBtnConfirmColor(R.color.new_primary)
                    .setOnPositiveClickListener("确定", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(
                                    Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivityForResult(intent, 0);
                            alertDialog.dismiss();
                        }
                    }).show();
        }
    }

    /**
     * 定位权限提示
     */
    protected void toOpenLocatedPermission() {
        final CustomAlertDialog dialog = CustomAlertDialog.getAlertDialog(mContext, false, false);
        dialog.setMessage("拒绝定位权限，本应用将无法使用，建议您在设置中开启定位权限")
                .setBtnCancelColor(R.color.main_background)
                .setOnNegativeClickListener("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        finish();
                    }
                })
                .setOnPositiveClickListener("去设置", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //引导用户至设置页手动授权
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getApplicationContext().getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                        dialog.dismiss();
                    }
                }).show();

    }

    public interface OnKeyboardStatusListener {
        /**
         * 键盘的显示与关闭
         *
         * @param status true:显示,false:关闭
         */
        void onKeyboardStatus(boolean status);
    }

    /**
     * 监听键盘的显示与关闭
     *
     * @param onKeyboardStatusListener
     */
    public void setOnKeyboardStatusListener(OnKeyboardStatusListener onKeyboardStatusListener) {
        this.onKeyboardStatusListener = onKeyboardStatusListener;
    }

    private void setKeyboardStatus(boolean status) {
        if (onKeyboardStatusListener != null) {
            onKeyboardStatusListener.onKeyboardStatus(status);
        }
    }
}