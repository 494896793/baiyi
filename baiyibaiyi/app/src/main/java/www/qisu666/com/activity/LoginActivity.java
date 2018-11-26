package www.qisu666.com.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import www.qisu666.com.R;
import www.qisu666.com.app.UserState;
import www.qisu666.com.callback.CarParkResp;
import www.qisu666.com.callback.CarResp;
import www.qisu666.com.callback.SystemConfigResp;
import www.qisu666.com.callback.UserInfoResp;
import www.qisu666.com.constant.Config;
import www.qisu666.com.constant.RequestUrls;
import www.qisu666.com.request.InterfaceAddressRequest;
import www.qisu666.com.request.LoginRequest;
import www.qisu666.com.request.RecoverDataRequest;
import www.qisu666.com.request.SystemArgumentRequest;
import www.qisu666.com.request.VerifyCodeRequest;
import www.qisu666.com.rx.RxBus;
import www.qisu666.com.rx.RxBusEvent;
import www.qisu666.com.rx.RxEventCodes;
import www.qisu666.com.service.TimeCountService;
import www.qisu666.com.utils.CacheUtils;
import www.qisu666.com.utils.Logger;
import www.qisu666.com.utils.NetWorkUtils;
import www.qisu666.com.utils.ResultUtil;
import www.qisu666.com.utils.SBUtils;
import www.qisu666.com.utils.SharedPreferencesUtils;
import www.qisu666.com.utils.ToastUtil;
import www.qisu666.com.utils.UserUtils;
import com.igexin.sdk.PushManager;
import com.tencent.tauth.Tencent;

import java.util.Date;

import butterknife.BindView;
import butterknife.OnClick;
import rx.functions.Action1;

public class LoginActivity extends BaseActivity implements
        TimeCountService.TimeCountListener {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final int REQUEST_CODE_REGISTER = 1;
    @BindView(R.id.tvTitleName)
    TextView tvTitleName;
    @BindView(R.id.rlytDeletePhone)
    RelativeLayout rlytDeletePhone;
    @BindView(R.id.etLoginPhone)
    EditText etLoginPhone;
    @BindView(R.id.rlytDeletePwd)
    RelativeLayout rlytDeletePwd;
    @BindView(R.id.etLoginPwd)
    EditText etLoginPwd;
    @BindView(R.id.btnLogin)
    TextView btnLogin;
    @BindView(R.id.llytProtocol)
    LinearLayout llytProtocol;
    @BindView(R.id.tvGetVerifyCode)
    TextView tvGetVerifyCode;

    private boolean isOnClickPhone = false;
    private boolean isOnClickPwd = false;


    private static final int LOGIN = 0;
    private static final int REQUEST_RECOVER_DATA = 1;
    private static final int QUERY_INTERFACE_ADDRESS = 2;
    private static final int GET_VERIFY_CODE = 3;
    private static final int MSG_AUTH_COMPLETE = 4;
    private static final int SYSTEM_PARAM = 5;

    private Tencent mTencent;
    //从服务器获取到的验证码
    private String verificationCode = "";
    private String verificationPhone = "";
    private String loginPhone = "";//发送验证码的手机号码

    //是否正在获取验证码
    private boolean isGetVerifyCode = false;

    private int beOut = 0;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                Logger.i(TAG, "handler what == " + 1);
                startService(new Intent(application, TimeCountService.class));
                TimeCountService.setTime(Config.TIME_COUNT);
            } else if (msg.what == 2) {//停止计时后，重新开始
                Logger.i(TAG, "handler what == " + 2);
                startService(new Intent(application, TimeCountService.class));
                TimeCountService.setTime(Config.TIME_COUNT, 1);
            }
        }
    };

    @Override
    public void setView() {
        setContentView(R.layout.activity_login);
    }

    @Override
    public void initDatas() {
        beOut = getIntent().getIntExtra("beOut", 0);//是否是被挤下线的登录，1：是，0：否
        SharedPreferencesUtils.putString(this, Config.LOGIN_TOKEN, "");
        setListener();
        btnLogin.setText("登录");
        String strPhone = SharedPreferencesUtils.getString(mContext, "PHONE");
        if (!TextUtils.isEmpty(strPhone)) {
            etLoginPhone.setText(strPhone.subSequence(0, 3) + "" + strPhone.substring(3, 7) + "" + strPhone.substring(7, 11));
            etLoginPhone.setSelection(etLoginPhone.getText().toString().trim().length());
        } else {
            etLoginPhone.setText("");
        }
        tvTitleName.setText("登录");

        TimeCountService.listener = this;
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
                            case RxEventCodes.FINISH_LOGIN:
                                finishPage();
                                break;
                        }
                    }
                });
    }

    @Override
    public void onComplete(String result, int type) {
        if (isSuccess(result)) {
            if (type == LOGIN) {
                UserInfoResp mUser = getBean(result, UserInfoResp.class);

                if (null != mUser) {
                    CacheUtils.getIn().save(mUser);
                    queryInterfaceAddress();
                    reBind(mUser);
                    //保存token
                    SharedPreferencesUtils.putString(application, Config.LOGIN_TOKEN, mUser.getToken());
                }
            } else if (type == REQUEST_RECOVER_DATA) {//复活
                CarParkResp carParkResp = getBean(result, CarParkResp.class);
//                onCarParkInfo(carParkResp);
                toMain(carParkResp);
            } else if (type == GET_VERIFY_CODE) {
                ToastUtil.showImage(mContext, "验证码已发送");
                verificationCode = ResultUtil.getStringResult(result);
                Logger.i("服务器获取到的验证码=" + verificationCode);
            } else if (type == QUERY_INTERFACE_ADDRESS) {
                String changeUrl = getMsg(result);
                if (!TextUtils.isEmpty(changeUrl) && !"null".equals(changeUrl)) {
                    RequestUrls.url = changeUrl;
                }
                //复活
                RecoverDataRequest data = new RecoverDataRequest();
                data.setMethod(RequestUrls.RECOVER_DATA);
                data.setCustomerPhone(UserUtils.getPhone());
                doGet(data, REQUEST_RECOVER_DATA, Config.LOADING_STRING, true);
            } else if (type == SYSTEM_PARAM) {
                SystemConfigResp systemConfigResp = getBean(result, SystemConfigResp.class);
                if (systemConfigResp != null) {
                    CacheUtils.getIn().save(systemConfigResp);
                }
            }
        } else {
            if (type == REQUEST_RECOVER_DATA) {
                toMain(new CarParkResp());
            } else if (type == GET_VERIFY_CODE) {
                setGetVerifyCodeEnable(true);
                showLongToast(getMsg(result));
            } else if (type == LOGIN) {
                ToastUtil.show(mContext, "登录失败");
            }
        }
    }

    @Override
    public void onFailure(String msg, int type) {
        if (type == GET_VERIFY_CODE) {
            ToastUtil.show(mContext, "验证码发送失败");
            setGetVerifyCodeEnable(true);
        } else if (type == REQUEST_RECOVER_DATA) {
            toMain(new CarParkResp());
        }
    }

    private void clickChange(boolean isOnClickPhone, boolean isOnClickPwd) {
        btnLogin.setClickable(isOnClickPhone && isOnClickPwd);
        if (btnLogin.isClickable()) {
            btnLogin.getBackground().setAlpha(255);
//            btnLogin.setBackgroundResource(R.drawable.bg_blue_radius_login_enable);
        } else {
            btnLogin.getBackground().setAlpha(180);
//            btnLogin.setBackgroundResource(R.drawable.bg_blue_radius_login_unenable);
        }
    }

    private void toMain(CarParkResp data) {
//        showToast("登录成功!");
        UserInfoResp mUser = CacheUtils.getIn().getUserInfo();
        CarResp carResp = data.getCarBaseInfo();

        boolean isRecoverData = false;
        if (!TextUtils.isEmpty(mUser.getStatus()) && UserState.isNeedRevival(mUser.getStatus())) {
            if (UserState.isGetCarPage(mUser.getStatus())) {//复活
                if (carResp != null) {
                    if (UserState.isOrdering(mUser.getStatus())) {//预约中
                        isRecoverData = true;

                        Intent intent = new Intent();
                        if (Config.ORDER_CATEGORY_LONG_RENT.equals(data.getOrderType())) {//短租预约复活
                            intent.setClass(mContext, UseCarPreOrderingActivity.class);

                            RxBusEvent event = new RxBusEvent();
                            event.setEventCode(RxEventCodes.CODE_RECOVER_LONG_RENT);
                            RxBus.getInstance().post(event);

                        } else {//短租预约复活
                            intent.setClass(mContext, UseCarOrderingActivity.class);
                        }
                        intent.putExtra("recoverData", data);
                        startActivity(intent);

                    } else {//用车中
                        isRecoverData = true;

                        Intent intent = new Intent();
                        if (Config.ORDER_CATEGORY_LONG_RENT.equals(data.getOrderType())) {//短租用车复活
                            intent.setClass(mContext, UseCarLongRentReturnActivity.class);
                        } else {//短租用车复活
                            intent.setClass(mContext, UseCarReturnActivity.class);
                        }
                        intent.putExtra("recoverData", data);
                        startActivity(intent);
                    }
                }
            }
        }
        SBUtils.send(SBUtils.loginOk, 1);
        if (!isRecoverData) {
            //登录成功，没有跳转到恢复页面，预约之前的页面要更新一下，从而知道是企业用户还是普通用户
            RxBusEvent event = new RxBusEvent();
            event.setEventCode(RxEventCodes.CODE_UPDATE_PRE_ORDER_CAR);
            RxBus.getInstance().post(event);
        }

        int isNewMember = mUser.getIsNewCustomer();
        if (isNewMember == 1) {//新用户
            activityUtil.jumpTo(IdVerifyStatusActivity.class);
        }

        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSystemData();
//        initDatas();
    }

    /**
     * 从服务器获取系统信息
     */
    public void getSystemData() {
        SystemArgumentRequest data = new SystemArgumentRequest();
        data.setAddressType(RequestUrls.url);
        data.setMethod(RequestUrls.QUERY_SYSTEM_PARAM);
        doGet(data, SYSTEM_PARAM, null, false);
    }

    @OnClick({R.id.btnLogin, R.id.ivTitleLeft, R.id.rlytDeletePhone, R.id.rlytDeletePwd, R.id.llytProtocol, R.id.tvGetVerifyCode})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.btnLogin://登录
                String phone = etLoginPhone.getText().toString().replaceAll(" ", "").trim();
                String verifyCode = etLoginPwd.getText().toString().trim();
                if (TextUtils.isEmpty(phone)) {
                    ToastUtil.show(this, "用户名/手机号不能为空");
                    return;
                }
                if (phone.length() != 11) {
                    ToastUtil.show(this, "手机号数为11位");
                    return;
                }
                if (!phone.matches(Config.PHONE_MATCH)) {
                    // loginPhoneNumber.setError("请输入正确的手机号");
                    ToastUtil.show(this, "请输入正确的手机号");
                    return;
                }
                if (TextUtils.isEmpty(verifyCode)) {
                    ToastUtil.show(this, "验证码不能为空");
                    return;
                }

//                if (!pwd.matches(Config.PWD_MATCH)) {
//                    ToastUtil.show(this, "密码只能为数字或字母");
//                    return;
//                }
                if (!verifyCode()) {
                    return;
                }

                SharedPreferencesUtils.putString(mContext, "PHONE", phone);
//                SharedPreferencesUtils.putString(mContext, "PWD", pwd);
                LoginRequest loginRequest = new LoginRequest();
                loginRequest.setCustomerPhone(phone);
                loginRequest.setCustomerEnrollType("Android");
                loginRequest.setCustomerCityCode(UserUtils.getCityCode());
                loginRequest.setMethod(RequestUrls.FAST_LOGIN);
                doGet(loginRequest, LOGIN, "正在登录中...", true);
                break;
            case R.id.ivTitleLeft://返回
                finishPage();
                break;
            case R.id.rlytDeletePhone://删除电话号码
                etLoginPhone.setText("");
                rlytDeletePhone.setVisibility(View.GONE);
                break;
            case R.id.rlytDeletePwd://删除密码
                etLoginPwd.setText("");
                rlytDeletePwd.setVisibility(View.GONE);
                break;
            case R.id.llytProtocol://租赁协议
                SystemConfigResp systemConfigResp = CacheUtils.getIn().getSystem_Info();
                if (null == systemConfigResp) {
                    break;
                }
                Intent intent = new Intent(this, www.qisu666.com.activity.WebActivity.class);
                intent.putExtra("title", "奇速共享用户租赁协议");
                intent.putExtra("url", systemConfigResp.getUserLeasingAgreementUrl());
                startActivity(intent);
                break;
            case R.id.tvGetVerifyCode://获取验证码
                String mPhone = etLoginPhone.getText().toString().replaceAll(" ", "").trim();
                if (mPhone.length() != 11) {
                    ToastUtil.show(this, "手机号码必须11位");
                    break;
                }

                if (mPhone.trim().equals("") || mPhone == null
                        || !mPhone.matches(Config.PHONE_MATCH)) {
                    // loginPhoneNumber.setError("请输入正确的手机号");
                    ToastUtil.show(this, "请输入正确的手机号");
                    return;
                } else {
                    if (NetWorkUtils.isNet()) {
                        loginPhone = etLoginPhone.getText().toString().replaceAll(" ", "").trim();
                        handler.sendEmptyMessageDelayed(1, 1000);
//                        SharedPreferencesUtils.putString(application, Config.REGISTER_CODE, "");
//                        SharedPreferencesUtils.putString(application, Config.REGISTER_PHONE, "");

                        setGetVerifyCodeEnable(false);

                        VerifyCodeRequest data = new VerifyCodeRequest();
                        data.setPhone(mPhone);
                        data.setMethod(RequestUrls.VERIFICATION_CODE);
                        doGet(data, GET_VERIFY_CODE, "正在发送验证码", true);
                    } else {
                        showLongToast("没有网络```");
                    }
                }
                break;
        }
    }

    /**
     * 关闭页面
     */
    private void finishPage() {
        if (beOut == 1) {//被挤下线，但是不登录，直接返回，则回到主界面
            Intent intent = new Intent(mContext, ControlerActivity.class);
            startActivity(intent);
        }
        finish();
    }

    public void setListener() {
        etLoginPhone.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String tel = s.toString().trim();
                if (!TextUtils.isEmpty(tel)) {
                    if (tel.length() == 11) {
                        isOnClickPhone = true;
                        if (!isGetVerifyCode) {
                            tvGetVerifyCode.setEnabled(true);
//                            tvGetVerifyCode.setTextColor(ContextCompat.getColor(mContext, R.color.color_blue_02b2e4));
                        }
                    } else {
                        isOnClickPhone = false;
                        if (!isGetVerifyCode) {
                            tvGetVerifyCode.setEnabled(false);
//                            tvGetVerifyCode.setTextColor(ContextCompat.getColor(mContext, R.color.color_gray_bcbbc4));
                        }
                    }
                    rlytDeletePhone.setVisibility(View.VISIBLE);
                    if (tel.length() > 11) {
                        etLoginPhone.setText(tel.substring(0, 11));
                        etLoginPhone.setSelection(etLoginPhone.length());
                    }
                } else {
                    isOnClickPhone = false;
                    rlytDeletePhone.setVisibility(View.GONE);
                }
                clickChange(isOnClickPhone, isOnClickPwd);
            }
        });
//        etLoginPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (hasFocus && etLoginPhone.getSelectionStart() > 0) {
//                    rlytDeletePhone.setVisibility(View.VISIBLE);
//                } else {
//                    rlytDeletePhone.setVisibility(View.GONE);
//                }
//            }
//        });

        etLoginPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String str = s.toString();
                if (str.length() > 4) {
                    str = str.substring(0, 4);
                    etLoginPwd.setText(str);
                    etLoginPwd.setSelection(str.length());
                }
                if (!TextUtils.isEmpty(str)) {
                    isOnClickPwd = true;
                    rlytDeletePwd.setVisibility(View.VISIBLE);
                } else {
                    isOnClickPwd = false;
                    rlytDeletePwd.setVisibility(View.GONE);
                }
                clickChange(isOnClickPhone, isOnClickPwd);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishPage();
    }

    public void reBind(final UserInfoResp userInfoResp) {
        CacheUtils.getIn().save(userInfoResp);
        if (!TextUtils.isEmpty(userInfoResp.getPhone())) {
            PushManager.getInstance().unBindAlias(application, userInfoResp.getPhone(), false);
            Logger.e("解绑时间" + new Date());
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Logger.e("绑定时间" + new Date());
                    PushManager.getInstance().bindAlias(application, userInfoResp.getPhone());
                }
            }, 5500);
        }
    }

//    public void onCarParkInfo(CarParkResp data) {
//        CarResp mCar = data.getCarBaseInfo();
//        if (mCar != null) {
//            CacheUtils.getIn().save(mCar);
//        } else {
//            CacheUtils.getIn().clear(CacheUtils.car_Info);
//        }
//        ParkResp mPark = data.getParkBaseinfo();
//        if (mPark != null) {
//            CacheUtils.getIn().save(mPark);
//        } else {
//            CacheUtils.getIn().clear(CacheUtils.park_Info);
//        }
//
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_REGISTER) {
            if (resultCode == RESULT_OK) {
//                finish();
            }
        }
    }

    /**
     * 验证验证码是否正确
     */
    private boolean verifyCode() {
        String code = etLoginPwd.getText().toString().trim();
        String phone = etLoginPhone.getText().toString().replaceAll(" ", "").trim();
        if (!TextUtils.isEmpty(code)) {
            Logger.i("输入的验证码=" + code + "  服务器的验证码=" + verificationCode);
            Logger.i("输入的手机号码=" + phone + "  保存的手机号码=" + loginPhone);
            if ("".equals(verificationCode) || (!"".equals(verificationCode) && !code.equals(verificationCode))
                    || "".equals(loginPhone) || (!"".equals(loginPhone) && !phone.equals(loginPhone))) {
                ToastUtil.show(this, "验证码不正确");
                return false;
            }
        } else {
            ToastUtil.show(this, "验证码不能为空");
            return false;
        }
        return true;
    }

    @Override
    public void timeGo(int millisUntilFinished) {
        Logger.i("发送验证码timeGo millisUntilFinished = " + millisUntilFinished);
//        if (isVisable) {
        if (tvGetVerifyCode != null) {
            tvGetVerifyCode.setText(millisUntilFinished + "秒");
            setGetVerifyCodeEnable(false);
        }
    }

    @Override
    public void timeOver() {
        //清空保存的验证码
//        SharedPreferencesUtils.putString(application, Config.REGISTER_CODE, "");
//        SharedPreferencesUtils.putString(application, Config.REGISTER_PHONE, "");
//        SharedPreferencesUtils.putString(application, Config.PRE_PHONE, "");

        Logger.i("发送验证码 时间timeOver");
//        if (isVisable) {
        changetofalse();
//        }
    }

    private void changetofalse() {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (null != tvGetVerifyCode) {
                    tvGetVerifyCode.setText("获取验证码");
                    setGetVerifyCodeEnable(true);
                }
            }
        });
    }

    /**
     * 发送验证码按钮是否可点击
     *
     * @param isEnable 是否可点击
     */
    private void


    setGetVerifyCodeEnable(boolean isEnable) {
        tvGetVerifyCode.setEnabled(isEnable);
        if (isEnable) {
//            tvGetVerifyCode.setTextColor(ContextCompat.getColor(mContext, R.color.color_blue_02b2e4));
        } else {
//            tvGetVerifyCode.setTextColor(ContextCompat.getColor(mContext, R.color.color_gray_bcbbc4));
        }
        isGetVerifyCode = !isEnable;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        //停止计时
        TimeCountService.stopTime();
//                stopService(new Intent(getBaseContext(), TimeCountService.class));
        TimeCountService.listener = null;
    }

    /**
     * 切换地址接口
     */
    private void queryInterfaceAddress() {
        InterfaceAddressRequest request = new InterfaceAddressRequest();
        request.setPhone(UserUtils.getPhone());
        request.setMethod(RequestUrls.QUERY_INTERFACE_ADDRESS);
        doGet(request, QUERY_INTERFACE_ADDRESS, "", false);
    }
}
