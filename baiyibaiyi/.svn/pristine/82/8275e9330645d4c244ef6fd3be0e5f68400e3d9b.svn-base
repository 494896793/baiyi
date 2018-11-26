package www.qisu666.com.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import www.qisu666.com.R;
import www.qisu666.com.app.MyApplication;
import www.qisu666.com.callback.AppUpdateResp;
import www.qisu666.com.callback.SystemConfigResp;
import www.qisu666.com.constant.RequestUrls;
import www.qisu666.com.event.ControlerCloseEvent;
import www.qisu666.com.request.APPVersionRequest;
import www.qisu666.com.rx.RxBus;
import www.qisu666.com.rx.RxBusEvent;
import www.qisu666.com.rx.RxEventCodes;
import www.qisu666.com.service.UpdateService;
import www.qisu666.com.utils.CacheUtils;
import www.qisu666.com.utils.ScreenUtils;
import www.qisu666.com.utils.ToastUtil;
import www.qisu666.com.view.CustomAlertDialog;
import www.qisu666.com.view.UpdateView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 设置
 */
public class SettingActivity extends BaseActivity {

    @BindView(R.id.rlytSettingContainer)
    RelativeLayout rlytSettingContainer;
    @BindView(R.id.tvTitleName)
    TextView tvTitleName;
    @BindView(R.id.tvVersionName)
    TextView tvVersionName;
    @BindView(R.id.tvLogout)
    TextView tvLogout;

    private List<String> bugList;
    private UpdateView updateView;
    private PackageInfo pi = null;
    public static int checkUpdate = 0;
    public static int serToServerSuc = 2;

    @Override
    public void setView() {
        setContentView(R.layout.activity_setting);
    }

    @Override
    public void initDatas() {
        tvTitleName.setText("设置");
        tvVersionName.setText(ScreenUtils.getVersionName(this));

        if (!MyApplication.isLoginSuccess) {
            tvLogout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onComplete(String result, int type) {
        if (isSuccess(result)) {
            if (type == checkUpdate) {
                AppUpdateResp appUpdateResp = getBean(result, AppUpdateResp.class);
                checkOk(appUpdateResp);
            } else if (type == serToServerSuc) {
                ToastUtil.show(mContext, getMsg(result));
            }
        } else {
            if (type == checkUpdate) {
                ToastUtil.show(mContext, "检查新版本失败！");
            } else if (type == serToServerSuc) {
                ToastUtil.show(mContext, getMsg(result));
            }
        }
    }

    @Override
    public void onFailure(String msg, int type) {

    }

    public void checkOk(AppUpdateResp appUpdateResp) {
        if (appUpdateResp != null) {
            int records = appUpdateResp.getVersionRecord();
            if (records > pi.versionCode) {
                application.isNeedForceUpdate = true;
                CacheUtils.getIn().saveStr(CacheUtils.apkUrl, appUpdateResp.getDownloadUrl());
            }
            if (application.isNeedForceUpdate) {
                showUpdateView();
            } else {
                ToastUtil.show(mContext, "当前已是最新版本！");
            }
        } else {
            ToastUtil.show(mContext, "当前已是最新版本！");
        }
    }

    private void showUpdateView() {
        if (updateView == null) {
            updateView = new UpdateView(mContext);
            if (bugList == null) {
                bugList = new ArrayList<>();
            }
            updateView.setListener(new UpdateView.UpdateListener() {
                @Override
                public void cancel() {
                    rlytSettingContainer.removeView(updateView);
                }

                @Override
                public void confirm(String isUpdate) {
                    showToast("开始下载");
                    if (!isUpdate.equals("true")) {
                        rlytSettingContainer.removeView(updateView);
                    }
                    Intent it = new Intent(SettingActivity.this, UpdateService.class);
                    startService(it);
                    bindService(it, conn, Context.BIND_AUTO_CREATE);
                    UpdateService.downoverListener = new UpdateService.DownoverListener() {
                        @Override
                        public void downOver() {
                            unbindService(conn);
                        }
                    };
                }
            });
        }
        rlytSettingContainer.addView(updateView, layoutParams);
    }

    public UpdateService.DownloadBinder binder;
    ServiceConnection conn = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            binder.cancel();
            stopService(new Intent(mContext, UpdateService.class));
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (UpdateService.DownloadBinder) service;
            // 开始下载
            binder.start();
        }
    };

    /**
     * 检查新版本
     */
    @OnClick({R.id.ivTitleLeft, R.id.llytAbout, R.id.llytCheckUpdate, R.id.tvLogout})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.ivTitleLeft:
                finish();
                break;
            case R.id.llytAbout://关于佰壹出行
                SystemConfigResp systemConfigResp = CacheUtils.getIn().getSystem_Info();
                Intent intent = new Intent(this, WebActivity.class);
                intent.putExtra("title", "关于");
                intent.putExtra("url", systemConfigResp.getAboutUrl());
                startActivity(intent);

                break;
            case R.id.llytCheckUpdate://检查更新
                PackageManager pm = application.getPackageManager();
                try {
                    pi = pm.getPackageInfo(application.getPackageName(), 0);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                APPVersionRequest data = new APPVersionRequest();
                if (null != pi) {
                    data.setAppVersionRecords(pi.versionCode + "");
                }
                data.setAppVersionType("android");
                data.setAddressType(RequestUrls.url);
                data.setMethod(RequestUrls.QUERY_VERSION_RECORD);
                doGet(data, checkUpdate, "正在查询...", true);
                break;
            case R.id.tvLogout://点击退出登录
                if (MyApplication.isLoginSuccess) {
                    showLogoutDialog();
                } else {
                    activityUtil.jumpTo(LoginActivity.class);
                    finish();
                }
                break;
        }
    }

    private void showLogoutDialog() {
        final CustomAlertDialog alertDialog = CustomAlertDialog.getAlertDialog(mContext, true, true);
        alertDialog.setMessage("是否确认退出登录？")
                .setBtnCancelColor(R.color.main_background)
                .setBtnConfirmColor(R.color.new_primary)
                .setOnNegativeClickListener("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                })
                .setOnPositiveClickListener("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        EventBus.getDefault().post(new ControlerCloseEvent());
                        MyApplication.timingStart = true;
                        application.logOut();
                        activityUtil.jumpTo(ControlerActivity.class);
                        alertDialog.dismiss();
                        //切换地址
                        RxBusEvent busEvent = new RxBusEvent();
                        busEvent.setEventCode(RxEventCodes.CODE_REQUEST_HUI_DU);
                        RxBus.getInstance().post(busEvent);
                        finish();
                    }
                }).show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            if (updateView != null && findViewById(R.id.rl_update_all_view) != null) {
                rlytSettingContainer.removeView(updateView);
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
