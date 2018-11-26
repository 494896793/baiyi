package www.qisu666.com.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import www.qisu666.com.R;
import www.qisu666.com.activity.ControlerActivity;
import www.qisu666.com.activity.SplashActivity;
import www.qisu666.com.activity.LoginActivity;
import www.qisu666.com.activity.MainActivity;
import www.qisu666.com.app.MyApplication;
import www.qisu666.com.constant.Config;
import www.qisu666.com.request.RequestBaseParams;
import www.qisu666.com.root.IBaseFragment;
import www.qisu666.com.root.RequestUtil;
import www.qisu666.com.utils.ActivityRootStackControlUtil;
import www.qisu666.com.utils.CacheUtils;
import www.qisu666.com.utils.DialogHelper;
import www.qisu666.com.utils.GPSUtils;
import www.qisu666.com.utils.Logger;
import www.qisu666.com.utils.NetWorkUtils;
import www.qisu666.com.utils.ResultUtil;
import www.qisu666.com.utils.SharedPreferencesUtils;
import www.qisu666.com.utils.ToastUtil;
import www.qisu666.com.view.CustomAlertDialog;
import www.qisu666.com.view.LoadingDialog;

import org.xutils.common.Callback;
import org.xutils.x;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Subscription;

public abstract class BaseFragment extends Fragment implements
        IBaseFragment {
    private static final String TAG = BaseFragment.class.getSimpleName();
    protected Activity activity;
    protected ActivityRootStackControlUtil activityUtil;
    private LoadingDialog mDialog;
    protected MyApplication application;
    protected Bundle savedInstanceState;
    private Unbinder unbinder;
    protected Subscription busSubscription;
    protected RelativeLayout.LayoutParams layoutParams;
    protected www.qisu666.com.widget.LoadingDialog mLoadingDialog;//加载中弹框

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (layoutParams == null) {
            layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
        }
        activityUtil = ActivityRootStackControlUtil.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.savedInstanceState = savedInstanceState;
        int layoutResId = setLayoutResId();
        if (layoutResId == 0) {
            return null;
        }
        activity = getActivity();
        View view = LayoutInflater.from(activity).inflate(layoutResId, container, false);
        unbinder = ButterKnife.bind(this, view);
        application = (MyApplication) activity.getApplication();

        initDatas(view);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mLoadingDialog = DialogHelper.loadingAletDialog(getActivity(), "正在加载中...");
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

    /**
     * 是否开启GPS
     */
    protected void isOpenGps(Context mContext) {
        if (GPSUtils.isOpen(mContext)) {
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

    protected void showToast(Context context,String msg) {
        ToastUtil.show(context, msg);
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
                        Logger.d("请求结果: " + mBean.getMethod() + "：" + result);
                        close(dialog);
                        if (ResultUtil.isTokenError(result)) {
                            if (!(activity instanceof SplashActivity)) {
                                loginOut();
                            }
                            return;
                        }
                        //账号不存在
                        if (Config.REQUEST_USER_NOT_EXIST.equals(ResultUtil.getCode(result))) {
                            SharedPreferencesUtils.putString(activity, "PHONE", "");
                            SharedPreferencesUtils.putString(activity, "PWD", "");
                            CacheUtils.getIn().clearMyInfo();
                            activityUtil.jumpTo(LoginActivity.class);
                            return;
                        } else if (Config.REQUEST_FAILURE.equals(ResultUtil.getCode(result))) {
                            ToastUtil.show(activity, getMsg(result));
                        }
                        onComplete(result, type);
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        onFailure(request_wrong, type);
                        close(dialog);
                        Logger.i("请求出错：" + ex.getMessage());
//                        ToastUtil.show(mContext, "出问题了，请稍后重试！");
                    }

                    @Override
                    public void onCancelled(CancelledException cex) {
                    }

                    @Override
                    public void onFinished() {
                    }
                });
    }

    public void loginOut() {
        CacheUtils.getIn().clearMyInfo();

        final CustomAlertDialog alertDialog = CustomAlertDialog.getAlertDialog(activity, false, false);
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
                        Intent intent = new Intent(activity, LoginActivity.class);
                        intent.putExtra("beOut", 1);//被挤下线
                        startActivity(intent);
                        alertDialog.dismiss();
                    }
                }).show();

    }

    public void doCheck(String title, boolean dialog) {
        if (!NetWorkUtils.isNet()) {
            ToastUtil.show(activity, "世界上最遥远的距离就是没有网络...");
            return;
        }
        if (dialog) {
            createDialog(title);
        }
    }

    protected void createDialog(String title) {
        if (mDialog != null) {
            closeDialog();
        }
        mDialog = new LoadingDialog(activity, title);
        if (!activity.isFinishing()) {
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
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
        if (busSubscription != null && !busSubscription.isUnsubscribed()) {
            busSubscription.unsubscribe();
        }
        if(mLoadingDialog!=null){
            mLoadingDialog.dismiss();
            mLoadingDialog = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        closeDialog();
    }
}