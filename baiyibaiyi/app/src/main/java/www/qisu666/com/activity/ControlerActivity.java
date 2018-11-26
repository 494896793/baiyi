package www.qisu666.com.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import www.qisu666.com.BuildConfig;
import www.qisu666.com.R;
import www.qisu666.com.callback.SplashAndActivityResp;
import www.qisu666.com.constant.Config;
import www.qisu666.com.event.CemareOnActivityResultEvent;
import www.qisu666.com.event.CemareOnActivityResultEvents;
import www.qisu666.com.event.ControlerCloseEvent;
import www.qisu666.com.event.MapVisibleEvent;
import www.qisu666.com.rx.RxBus;
import www.qisu666.com.rx.RxBusEvent;
import www.qisu666.com.rx.RxEventCodes;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.OnClick;
import rx.functions.Action1;
import www.qisu666.com.utils.DataUtils;
import www.qisu666.com.utils.ToastUtil;

/**
 * 717219917@qq.com 2018/10/11 14:39.
 */
public class ControlerActivity extends BaseActivity {

    public static final int NONE = 0;
    public static final int PHOTOHRAPH = 1;// 拍照
    public static final int PHOTORESOULT = 3;// 结果
    private File mPicFile;
    private MainActivity mainFragment;
    private FragmentManager manager;

    @BindView(R.id.framelayout)
    FrameLayout framelayout;
    @BindView(R.id.elect_re)
    RelativeLayout elect_re;
    @BindView(R.id.car_re)
    RelativeLayout car_re;
    @BindView(R.id.view_second)
    View view_second;
    @BindView(R.id.view_first)
    View view_first;
    @BindView(R.id.elect_menu)
    TextView elect_menu;
    @BindView(R.id.car_menu)
    TextView car_menu;
    @BindView(R.id.bottom_menu)
    LinearLayout bottom_menu;

    PointAggregationAty poinFragment;

    private List<Fragment> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        observeRxEventCode();
    }

    private void observeRxEventCode() {
        busSubscription = RxBus.getInstance()
                .toObservable(RxBusEvent.class)
                .subscribe(new Action1<RxBusEvent>() {
                    @Override
                    public void call(RxBusEvent rxBusEvent) {
                        int eventCode = rxBusEvent.getEventCode();
                        switch (eventCode) {
                            case RxEventCodes.CODE_DRAWLAYOUT_STATUS://drawLayout状态
                                boolean status = (boolean) rxBusEvent.getContent();
                                if (status) {
                                    //打开侧边栏，隐藏底部按钮
                                    if (bottom_menu.getVisibility() != View.GONE) {
                                        bottom_menu.setVisibility(View.GONE);
                                    }
                                } else {
                                    if (bottom_menu.getVisibility() != View.VISIBLE) {
                                        bottom_menu.setVisibility(View.VISIBLE);
                                    }
                                }
                                break;
                        }
                    }
                });
    }

    private String getMyUUID() {

        final TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);

        final String tmDevice, tmSerial, tmPhone, androidId;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

        }
        tmDevice = "" + tm.getDeviceId();

        tmSerial = "" + tm.getSimSerialNumber();

        androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());

        String uniqueId = deviceUuid.toString();


        return uniqueId;

    }

    @Override
    public void setView() {
        setContentView(R.layout.activity_controler_layout);
    }

    @Override
    public void initDatas() {
        manager = getSupportFragmentManager();
        mainFragment = new MainActivity();

        ArrayList<SplashAndActivityResp> splashAndActivityResps = getIntent().getParcelableArrayListExtra("splashAndActivityResps");
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("splashAndActivityResps", splashAndActivityResps);
        mainFragment.setArguments(bundle);
        list.add(mainFragment);


        poinFragment = new PointAggregationAty();
        list.add(poinFragment);
        initFragment();
        selectFragment(0);
    }

    private void initFragment() {
        FragmentTransaction transaction = manager.beginTransaction();
        for (int i = 0; i < list.size(); i++) {
            transaction.add(R.id.framelayout, list.get(i));
        }
        transaction.commitAllowingStateLoss();
    }

//    public void

    private void selectFragment(int position) {
        hideAllFragment();
        FragmentTransaction transaction = manager.beginTransaction();
        if (list.get(position) != null) {
            switch (position) {
                case 0:
                    transaction.show(mainFragment);
                    break;
                case 1:
                    transaction.show(poinFragment);
                    break;
            }
        }
        transaction.commitAllowingStateLoss();
        if (position == 1) {
            EventBus.getDefault().post(new MapVisibleEvent(1));
        } else {
            EventBus.getDefault().post(new MapVisibleEvent(0));
        }
    }

    private void hideAllFragment() {
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.hide(poinFragment);
        transaction.hide(mainFragment);
        transaction.commitAllowingStateLoss();
    }

    @OnClick({R.id.elect_re, R.id.car_re})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.elect_re:
                initBottomMenuColor(1);
                selectFragment(1);
                break;
            case R.id.car_re:
                initBottomMenuColor(0);
                selectFragment(0);
                break;
        }
    }

    private void initBottomMenuColor(int index) {
        if (index == 0) {
            car_menu.setTextColor(getResources().getColor(R.color.new_primary));
            elect_menu.setTextColor(getResources().getColor(R.color.main_info_color));
            view_second.setVisibility(View.GONE);
            view_first.setVisibility(View.VISIBLE);
        } else {
            car_menu.setTextColor(getResources().getColor(R.color.main_info_color));
            elect_menu.setTextColor(getResources().getColor(R.color.new_primary));
            view_second.setVisibility(View.VISIBLE);
            view_first.setVisibility(View.GONE);
        }
    }

    @Override
    public void onComplete(String result, int type) {

    }

    @Override
    public void onFailure(String msg, int type) {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            exitApplication();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == NONE) {
            return;
        }
        CemareOnActivityResultEvent event = new CemareOnActivityResultEvent();
        CemareOnActivityResultEvents events = new CemareOnActivityResultEvents();
        event.resultCode = resultCode;
        event.requestCode = requestCode;
        event.data = data;

        events.resultCode = resultCode;
        events.requestCode = requestCode;
        events.data = data;
        EventBus.getDefault().post(event);
        EventBus.getDefault().post(events);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEvent(ControlerCloseEvent event) {
        if (bottom_menu != null) {
            if (event.closed == 1) {
                bottom_menu.setVisibility(View.GONE);
            } else {
                bottom_menu.setVisibility(View.VISIBLE);
            }
        }
    }

}