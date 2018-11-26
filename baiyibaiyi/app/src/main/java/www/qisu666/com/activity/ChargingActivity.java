package www.qisu666.com.activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import www.qisu666.com.R;
import www.qisu666.com.adapter.ShareAdapter;
import www.qisu666.com.event.FinishActivityEvent;
import www.qisu666.com.event.LoginEvent;
import www.qisu666.com.request.MyNetwork;
import www.qisu666.com.request.utils.FlatFunction;
import www.qisu666.com.request.utils.MyMessageUtils;
import www.qisu666.com.request.utils.ResultSubscriber;
import www.qisu666.com.request.utils.RxNetHelper;
import www.qisu666.com.utils.CacheUtils;
import www.qisu666.com.utils.ConstantCode;
import www.qisu666.com.utils.DialogHelper;
import www.qisu666.com.utils.JsonUtils;
import www.qisu666.com.utils.LogUtils;
import www.qisu666.com.utils.NumberUtils;
import www.qisu666.com.utils.SPParams;
import www.qisu666.com.utils.ToastUtil;
import www.qisu666.com.view.WaveView;
import www.qisu666.com.widget.AlertDialog;
import www.qisu666.com.widget.CircleProgressBar;
import www.qisu666.com.widget.LoadingDialog;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Cache;


/**
 *  正在充电
 */
public class ChargingActivity extends BaseActivity implements View.OnClickListener {

    private static final int REPEAT_CONN = 1;
    private static final int REFRESH_TIME = 2;
    private WaveView wave;

    private TextView tv_title;
    private TextView tv_charging_charge;
    private AnimatorSet mAnimatorSet;
    private ImageView gifImageView;

    private float curPersent;
    private int duration;

    private TextView tv_station_name;
    private TextView tv_terminal_no;
    private LinearLayout ll_index_map;
    private LinearLayout ll_index_nearby;
    private LinearLayout ll_index_history;
    private LinearLayout ll_index_share;
    private TextView tv_charge_fee;
    private TextView tv_charge_quantity;
    private TextView tv_charge_duration;
    private TextView tv_charge_voltage;
    private TextView tv_charge_current;
    private TextView tv_charge_power;
    private TextView tv_charge_soc, tv_charge_soc_p;
    private ImageView img_rotate;
    private CircleProgressBar pro;
    private TextView tx_elect;
    private TextView tx_persen;

    private PopupWindow sharePopupWindow;
    private String station_id;

    //分享内容
    private String text;
    private String title;
    private UMImage image;
    //    private UMusic music;
    //    private UMVideo video;
    private String url;
    private String charge_pile_num;


    //重复请求，刷新页面间隔
    private static final int REPEAT_DURATION = 30000;
    private boolean firstConn = true;
    private boolean isFromResume = true;

    //充电结束dialog
    private AlertDialog alertDialog;
    private LoadingDialog mLoadingDialog;//加载中弹框
    @SuppressLint("HandlerLeak")
    private Handler connHandler = new Handler() {
        @SuppressLint("SetTextI18n")
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REPEAT_CONN:
                    connToServer(false);
                    break;
                case REFRESH_TIME:
                    if (firstConn || isFromResume) {
                        String time_stamp = (String) msg.obj;
//                        int h = Integer.parseInt(min) / 60;
//                        int m = Integer.parseInt(min) % 60;
//                        int s = 0;
                        String s_h = time_stamp.substring(0, 2);
                        String s_m = time_stamp.substring(2, 4);
                        String s_s = time_stamp.substring(4, 6);
                        tv_charge_duration.setText(s_h + ":" + s_m + ":" + s_s);
                        firstConn = false;
                        isFromResume = false;
                    } else {
                        String[] t_strs = tv_charge_duration.getText().toString().split(":");
                        int h = Integer.parseInt(t_strs[0]);
                        int m = Integer.parseInt(t_strs[1]);
                        int s = Integer.parseInt(t_strs[2]);
                        s++;
                        if (s == 60) {
                            s = 0;
                            m++;
                            if (m == 60) {
                                m = 0;
                                h++;
                            }
                        }
                        tv_charge_duration.setText(getStringTime(h) + ":" + getStringTime(m) + ":" + getStringTime(s));
                    }
                    sendEmptyMessageDelayed(REFRESH_TIME, 1000);
                    break;
                default:
                    break;
            }
        }
    };

    private String getStringTime(int i_t) {
        String t = String.valueOf(i_t);
        if (t.length() == 1) {
            t = "0" + t;
        }
        return t;
    }

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
            ToastUtil.showToast(platform + "分享成功");
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            ToastUtil.showToast(platform + "分享失败");
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
//            ToastUtil.showToast(platform + " 分享取消了");
        }
    };

    @Subscribe
    public void onEventMainThread(LoginEvent event) {
        connToServer(true);
    }

    @Subscribe
    public void onEventMainThread(FinishActivityEvent event) {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charging);
        initView();
        setShareContent();
        initSharePopupWindow();
        setListeners();
//        connToServer(true);
        EventBus.getDefault().register(this);
    }

    private void refreshData() {
        curPersent = (float) Math.random();

    }

    private void initView() {

        initTitleBar();

        initWave();
        tx_persen=findViewById(R.id.tx_persen);
        pro=findViewById(R.id.pro);
        tx_elect=findViewById(R.id.tx_elect);
        img_rotate=findViewById(R.id.img_rotate);
        gifImageView = (ImageView) findViewById(R.id.gifImageView);

        tv_charging_charge = (TextView) findViewById(R.id.tv_charging_charge);
        tv_station_name = (TextView) findViewById(R.id.tv_station_name);
        tv_terminal_no = (TextView) findViewById(R.id.tv_terminal_no);
        ll_index_map = (LinearLayout) findViewById(R.id.ll_index_map);
        ll_index_nearby = (LinearLayout) findViewById(R.id.ll_index_nearby);
        ll_index_history = (LinearLayout) findViewById(R.id.ll_index_history);
        ll_index_share = (LinearLayout) findViewById(R.id.ll_index_share);
        tv_charge_fee = (TextView) findViewById(R.id.tv_charge_fee);
        tv_charge_quantity = (TextView) findViewById(R.id.tv_charge_quantity);
        tv_charge_duration = (TextView) findViewById(R.id.tv_charge_duration);
        tv_charge_voltage = (TextView) findViewById(R.id.tv_charge_voltage);
        tv_charge_current = (TextView) findViewById(R.id.tv_charge_current);
        tv_charge_power = (TextView) findViewById(R.id.tv_charge_power);
        tv_charge_soc = (TextView) findViewById(R.id.tv_charge_soc);
        tv_charge_soc_p = (TextView) findViewById(R.id.tv_charge_soc_p);

        initElectImgRotate();
    }

    private void initElectImgRotate(){
        ObjectAnimator animator = ObjectAnimator.ofFloat(img_rotate, "rotation", 0f, 359f);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(1500);
        animator.start();

        pro.setMax(100);
//        pro.setProgress(35);
        pro.setCircleColor(R.color.confirm_passed);
        RotateAnimation rotateAnimation=new RotateAnimation(0,-90,
                Animation.RELATIVE_TO_SELF,0.5f, Animation.RELATIVE_TO_SELF,0.5f);
        rotateAnimation.setDuration(10);
        rotateAnimation.setFillAfter(true);
        pro.startAnimation(rotateAnimation);
    }


    /**
     * 设置监听器
     */
    private void setListeners() {
        tv_charging_charge.setOnClickListener(this);
        ll_index_map.setOnClickListener(this);
        ll_index_nearby.setOnClickListener(this);
        ll_index_history.setOnClickListener(this);
        ll_index_share.setOnClickListener(ChargingActivity.this);
    }

    private void initTitleBar() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("充电中");
        View left_btn = findViewById(R.id.img_title_left);
        left_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(ConstantCode.RES_CHARGING);
                finish();
            }
        });
        ImageView right_btn = (ImageView) findViewById(R.id.img_title_right);
        right_btn.setImageResource(R.mipmap.ic_charging_refresh);
        right_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connHandler.removeMessages(REPEAT_CONN);
                connToServer(true);
            }
        });
    }

    private void resetProgress() {
        if (mAnimatorSet.isRunning()) {
            mAnimatorSet.cancel();
        }
        initWave();
    }

    private void initWave() {

        mAnimatorSet = new AnimatorSet();
        wave = (WaveView) findViewById(R.id.wave);

        setWave(false);
    }

    private void setWave(boolean isHasSoc) {
        if (isHasSoc) {
            mAnimatorSet.cancel();
            mAnimatorSet = new AnimatorSet();
        }
        List<Animator> animators = new ArrayList<>();
        ObjectAnimator waveShiftAnim = ObjectAnimator.ofFloat(
                wave, "waveShiftRatio", 0f, 1f);
        waveShiftAnim.setRepeatCount(ValueAnimator.INFINITE);
        waveShiftAnim.setDuration(1000);
        waveShiftAnim.setInterpolator(new LinearInterpolator());
        animators.add(waveShiftAnim);

        if (!isHasSoc) {
            // vertical animation.
            // water level increases from 0 to center of WaveView
            ObjectAnimator waterLevelAnim = ObjectAnimator.ofFloat(wave, "waterLevelRatio", 0f, 1.0f);
            waterLevelAnim.setDuration(7500);
            waterLevelAnim.setInterpolator(new LinearInterpolator());
            animators.add(waterLevelAnim);
            waterLevelAnim.setRepeatMode(ValueAnimator.RESTART);
            waterLevelAnim.setRepeatCount(10000);
//        waterLevelAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                float ratio = wave.getWaterLevelRatio();
//                tv_charge_persent.setText((int)(ratio * 100) + 1 + "");
//            }
//        });
        } else {
            LogUtils.e(curPersent + "");
            ObjectAnimator waterLevelAnim = ObjectAnimator.ofFloat(wave, "waterLevelRatio", 0f, curPersent);
            waterLevelAnim.setDuration(duration);
            waterLevelAnim.setInterpolator(new LinearInterpolator());
            animators.add(waterLevelAnim);
        }
        mAnimatorSet.playTogether(animators);
        startWave();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        wave.setWaveColor(
                Color.parseColor("#0289C3"),
                Color.parseColor("#883AA8E1"));
        super.onWindowFocusChanged(hasFocus);
    }

    private void startWave() {
        wave.setShowWave(true);
        if (mAnimatorSet != null) {
            mAnimatorSet.start();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_charging_charge:
                DialogHelper.confirmDialog(ChargingActivity.this, getString(R.string.dialog_prompt_stop_charge), new AlertDialog.OnDialogButtonClickListener() {
                    @Override
                    public void onConfirm() {

                        String url = "api/charge/stop";
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("userId", CacheUtils.getIn().getUserInfo().getId());
                        map.put("outTradeNo", CacheUtils.getIn().getUserInfo().getOut_trade_no());

                        MyNetwork.getMyApi()
                                .carRequest(url, MyMessageUtils.addBody(map))
                                .map(new FlatFunction<>(Object.class))
                                .compose(RxNetHelper.<Object>io_main())
                                .subscribe(new ResultSubscriber<Object>() {

                                    @Override
                                    public void onSuccessCode(www.qisu666.com.event.Message object) {

                                    }

                                    @Override
                                    public void onSuccess(Object bean) {
                                        ToastUtil.showToast("订单结算中...");
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                finishCharge();
                                            }
                                        }, 2500);

                                    }

                                    @Override
                                    public void onFail(www.qisu666.com.event.Message<Object> bean) {

                                    }
                                });


//                        JSONObject jsonObject = new JSONObject();
//                        try {
//                            jsonObject.put("req_code", "E103");
//                            jsonObject.put("user_id", UserParams.INSTANCE.getUser_id());
//                            jsonObject.put("s_token", UserParams.INSTANCE.getS_token());
//                            jsonObject.put("out_trade_no", UserParams.INSTANCE.getOut_trade_no());
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                        HttpLogic httpLogic = new HttpLogic(ChargingActivity.this);
//                        httpLogic.sendRequest(Config.REQUEST_URL, jsonObject, true, new AbstractResponseCallBack() {
//
//                            @Override
//                            public void onResponse(Map<String, Object> map, String tag) {
////                                connToServer(false);
//
//                            }
//                        });
                    }

                    @Override
                    public void onCancel() {
                    }
                });
                break;
            case R.id.ll_index_map:
                DialogHelper.alertDialog(ChargingActivity.this, "建设中，敬请期待");
//                startActivity(new Intent(this, StationMapActivity.class));
                break;
            case R.id.ll_index_nearby:
                DialogHelper.alertDialog(ChargingActivity.this, "建设中，敬请期待");
//                startActivity(new Intent(this, NearbyStationActivity.class));
                break;
            case R.id.ll_index_history:
                DialogHelper.alertDialog(ChargingActivity.this, "建设中，敬请期待");
//                startActivity(new Intent(this, ChargingInfoActivity.class));
                break;
            case R.id.ll_index_share:
                DialogHelper.alertDialog(ChargingActivity.this, "建设中，敬请期待");
//                sharePopupWindow.showAtLocation(findViewById(R.id.layout_main), Gravity.BOTTOM, 0, 0);
                break;
            default:
                break;
        }
    }

    /**
     * 设置分享的内容
     */
    private void setShareContent() {
        title = "我在用奇速共享APP来给我的爱车充电啦！畅享低价优质体验";
        text = "此站充电巨划算，点击查看详情";
//        image = new UMImage(this, "http://www.umeng.com/images/pic/social/integrated_3.png");
        image = new UMImage(this, R.mipmap.ic_share_logo);

        if (getSharedPreferences(SPParams.CONFIG_INFO, Context.MODE_PRIVATE).getString("station_id", null) != null) {
            url = "http://wx.qisu666.com/idn/chat/share/pile?station_id=" + getSharedPreferences(SPParams.CONFIG_INFO, Context.MODE_PRIVATE).getString("station_id", null);
        } else {
            url = "http://www.qisu666.com/";
        }

//        UMImage image = new UMImage(ShareActivity.this, "http://blog.thegmic.com/wp-content/uploads/2012/05/umeng.jpg");
//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.info_icon_1);
//
//        UMImage image = new UMImage(ShareActivity.this, bitmap);
        //UMImage image1 = new UMImage(ShareActivity.this,new File("/storage/emulated/0/umeng_cache/0EADEEDFC79B32EB58E871D44E6DAA91"));

        // two line below are for testing on nexus 5 only, don't use it on other phones
        //UMImage image = new UMImage(ShareActivity.this,new File("/storage/emulated/0/DCIM/1453176647573.jpg"));
        //UMVideo video2 = new UMVideo("/storage/emulated/0/DCIM/test.mp4");

//        music = new UMusic("http://music.huoxing.com/upload/20130330/1364651263157_1085.mp3");
//        music.setTitle("This is music title");
//        music.setThumb(new UMImage(this, "http://www.umeng.com/images/pic/social/chart_1.png"));
//        video = new UMVideo("http://video.sina.com.cn/p/sports/cba/v/2013-10-22/144463050817.html");

    }

    /**
     * 分享PopupWindow
     */
    private void initSharePopupWindow() {

        // 一个自定义的布局，作为显示的内容
        View contentView = LayoutInflater.from(this).inflate(
                R.layout.popup_share, null);

        GridView gridView = (GridView) contentView.findViewById(R.id.gridView);
        gridView.setAdapter(new ShareAdapter(this));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LogUtils.d("onItemClick");
                switch (position) {
                    case 0:
                        new ShareAction(ChargingActivity.this).setPlatform(SHARE_MEDIA.WEIXIN).setCallback(umShareListener)
                                .withText(text)
                                .withMedia(image)
                                .withTargetUrl(url)
                                .withTitle(title)
                                //.withMedia(new UMEmoji(ShareActivity.this,"http://img.newyx.net/news_img/201306/20/1371714170_1812223777.gif"))
                                .share();
                        break;
                    case 1:
                        new ShareAction(ChargingActivity.this).setPlatform(SHARE_MEDIA.QQ).setCallback(umShareListener)
                                .withText(text)
                                .withMedia(image)
                                .withTitle(title)
                                .withTargetUrl(url)
                                .share();
                        break;
                    case 2:
                        /** shareaction need setplatform , callbacklistener,and content(text,image).then share it **/
                        new ShareAction(ChargingActivity.this).setPlatform(SHARE_MEDIA.QZONE).setCallback(umShareListener)
                                .withText(text)
                                .withMedia(image)
                                .withTargetUrl(url)
                                .withTitle(title)
                                .share();
                        break;
                    case 3:
                        /** shareaction need setplatform , callbacklistener,and content(text,image).then share it **/
                        new ShareAction(ChargingActivity.this).setPlatform(SHARE_MEDIA.SINA).setCallback(umShareListener)
                                .withText(text)
                                .withMedia(image)
                                .withTargetUrl(url)
                                .share();
                        break;
                    case 4:
                        new ShareAction(ChargingActivity.this).setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE).setCallback(umShareListener)
                                .withText(text)
                                .withMedia(image)
                                .withTargetUrl(url)
                                .withTitle(title)
                                .share();
                        break;
                    default:
                        break;

                }
                sharePopupWindow.dismiss();
            }
        });

        sharePopupWindow = new PopupWindow(contentView,
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);

        sharePopupWindow.setTouchable(true);
        sharePopupWindow.setTouchInterceptor(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
                // 这里如果返回true的话，touch事件将被拦截
                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
            }
        });

        // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
        // 我觉得这里是API的一个bug
        sharePopupWindow.setBackgroundDrawable(ContextCompat.getDrawable(this, R.color.white));
        sharePopupWindow.setAnimationStyle(R.style.Popup_Anim_Bottom);
    }

    @Override
    protected void onResume() {
        super.onResume();
        connToServer(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (sharePopupWindow != null) {
            sharePopupWindow.dismiss();
            sharePopupWindow = null;
        }
        if (alertDialog != null) {
            alertDialog.dismiss();
            alertDialog = null;
        }
        if (connHandler != null) {
            connHandler.removeMessages(REPEAT_CONN);
            connHandler.removeMessages(REFRESH_TIME);
            connHandler = null;
        }
    }

    private void connToServer(boolean flag) {

        String url = "api/charge/order/result/query";
        HashMap<String, Object> map = new HashMap<>();
        map.put("userId", CacheUtils.getIn().getUserInfo().getId());
        map.put("outTradeNo", CacheUtils.getIn().getUserInfo().getOut_trade_no());

        MyNetwork.getMyApi()
                .carRequest(url, MyMessageUtils.addBody(map))
                .map(new FlatFunction<>(Object.class))
                .compose(RxNetHelper.<Object>io_main())
                .subscribe(new ResultSubscriber<Object>() {
                    @Override
                    public void onSuccessCode(www.qisu666.com.event.Message object) {

                    }

                    @SuppressLint("SetTextI18n")
                    @Override
                    @SuppressWarnings("unchecked")
                    public void onSuccess(Object bean) {
                        Log.e("aaa", "asd:" + bean.toString());
                        // 对象转json
                        String s = JsonUtils.objectToJson(bean);
                        // json转 map
                        Map jsonToMap = JsonUtils.jsonToMap(s);

                        station_id = jsonToMap.get("stationId").toString();
                        tv_charge_fee.setText("￥" + jsonToMap.get("totalChargeMoney").toString());
                        tv_charge_quantity.setText(jsonToMap.get("totalChargeQuantity").toString() + "度");
//                tv_charge_fee.setText(map.get("total_charge_money") == null ? "￥0.00" : map.get("total_charge_money").toString());
                        String v = jsonToMap.get("currentV").toString();
                        String a = jsonToMap.get("currentA").toString();
                        tv_charge_voltage.setText(v + "伏");
                        tv_charge_current.setText(a + "安");
                        String w = NumberUtils.getRoundNumber(String.valueOf(Double.parseDouble(v) * Double.parseDouble(a) / 1000), 2);
                        tv_charge_power.setText(w + "千瓦");
                        Message msg;
                        try {
                            msg = connHandler.obtainMessage();
                        } catch (Throwable t) {  //
                            t.printStackTrace();
                            msg = new Message();
                        }
                        showOrHideSoc(jsonToMap.get("chargeSoc").toString());
                        if(!jsonToMap.get("chargeSoc").toString().equals("-1")){
                            tx_elect.setText(jsonToMap.get("chargeSoc").toString());
                            pro.setProgress(Integer.valueOf(jsonToMap.get("chargeSoc").toString()));
                            tx_persen.setVisibility(View.VISIBLE);
                        }else{
                            pro.setProgress(0);
                            tx_persen.setVisibility(View.GONE);
                            tx_elect.setText("充电中...");
                        }
//                        if(!TextUtils.isEmpty(jsonToMap.get("chargeSoc").toString())&&!jsonToMap.get("chargeSoc").toString().equals("-1")){
//                            tv_charging_charge.setVisibility(View.VISIBLE);
//                        }else{
//                            tv_charging_charge.setVisibility(View.GONE);
//                        }
                        if (firstConn || isFromResume) {
                            tv_station_name.setText(jsonToMap.get("stationName") == null ? "未知" : jsonToMap.get("stationName").toString());
                            tv_terminal_no.setText(jsonToMap.get("chargePileNum") == null ? "0" : jsonToMap.get("chargePileNum").toString());
                            String time_stamp = TextUtils.isEmpty(jsonToMap.get("totalChargeTimes") != null ?
                                    jsonToMap.get("totalChargeTimes").toString() : "") ? "000000" : jsonToMap.get("totalChargeTimes").toString();
                            msg.what = REFRESH_TIME;
                            msg.obj = time_stamp;
                            connHandler.sendMessage(msg);
                        }
                        if ("03".equals(jsonToMap.get("orderState").toString()) || "04".equals(jsonToMap.get("orderState").toString())) {
                            finishCharge();
                        } else {
                            Message connMsg = connHandler.obtainMessage();
                            connMsg.what = REPEAT_CONN;
                            connHandler.sendMessageDelayed(connMsg, REPEAT_DURATION);//30秒钟 更新一次
                        }
                    }

                    @Override
                    public void onFail(www.qisu666.com.event.Message<Object> bean) {
                        Log.e("aaaa", "获取失败：" + bean.toString());
                    }

                });

//        JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject.put("req_code", "E102");
//            jsonObject.put("user_id", UserParams.INSTANCE.getUser_id());
//            jsonObject.put("out_trade_no", UserParams.INSTANCE.getOut_trade_no());
////            jsonObject.put("out_trade_no", "20160812172219000750");
//            jsonObject.put("s_token", UserParams.INSTANCE.getS_token() == null ? "" : UserParams.INSTANCE.getS_token());
//        } catch (Exception e) {
//
//        }
//        HttpLogic httpLogic = new HttpLogic(ChargingActivity.this);
//        httpLogic.sendRequest(Config.REQUEST_URL, jsonObject, flag, LoadingDialog.TYPE_ROTATE, new AbstractResponseCallBack() {
//            @Override
//            public void onResponse(Map<String, Object> map, String tag) {
////                tv_charge_fee.setText(TextUtils.isEmpty(map.get("total_charge_money") != null ? map.get("total_charge_money").toString() : "") ? "￥0.00" : "￥" + NumberUtils.getRoundNumber(Double.parseDouble(map.get("total_charge_money").toString()) / 100, 2));
////                tv_charge_quantity.setText(TextUtils.isEmpty(map.get("total_charge_quantity") != null ? map.get("total_charge_quantity").toString() : "") ? "0.00度" : NumberUtils.getRoundNumber(Double.parseDouble(map.get("total_charge_quantity").toString()), 2) + "度");
//////                tv_charge_fee.setText(map.get("total_charge_money") == null ? "￥0.00" : map.get("total_charge_money").toString());
////                String v = TextUtils.isEmpty(map.get("current_v") != null ? map.get("current_v").toString() : "") ? "0" : map.get("current_v").toString();
////                String a = TextUtils.isEmpty(map.get("current_a") != null ? map.get("current_a").toString() : "") ? "0" : map.get("current_a").toString();
////                tv_charge_voltage.setText(TextUtils.isEmpty(v) ? "0.0伏" : NumberUtils.getRoundNumber(Double.parseDouble(v) / 10, 1) + "伏");
////                tv_charge_current.setText(TextUtils.isEmpty(a) ? "0.00安" : NumberUtils.getRoundNumber(Double.parseDouble(a) / 100, 2) + "安");
////                String w = String.valueOf(NumberUtils.getRoundNumber(Double.parseDouble(v) * Double.parseDouble(a) / 1000, 2));
////                tv_charge_power.setText(w + "瓦");
//                station_id = map.get("station_id").toString();
//                tv_charge_fee.setText("￥" + map.get("total_charge_money").toString());
//                tv_charge_quantity.setText(map.get("total_charge_quantity").toString() + "度");
////                tv_charge_fee.setText(map.get("total_charge_money") == null ? "￥0.00" : map.get("total_charge_money").toString());
//                String v = map.get("current_v").toString();
//                String a = map.get("current_a").toString();
//                tv_charge_voltage.setText(v + "伏");
//                tv_charge_current.setText(a + "安");
//                String w = NumberUtils.getRoundNumber(String.valueOf(Double.parseDouble(v) * Double.parseDouble(a) / 1000), 2);
//                tv_charge_power.setText(w + "千瓦");
//                Message msg;
//                try {
//                    msg = connHandler.obtainMessage();
//                } catch (Throwable t) {  //
//                    t.printStackTrace();
//                    msg = new Message();
//                }
//                showOrHideSoc(map.get("charge_soc").toString());
//                if (firstConn || isFromResume) {
//                    tv_station_name.setText(map.get("station_name") == null ? "未知" : map.get("station_name").toString());
//                    tv_terminal_no.setText(map.get("charge_pile_num") == null ? "0" : map.get("charge_pile_num").toString());
//                    String time_stamp = TextUtils.isEmpty(map.get("total_charge_times") != null ? map.get("total_charge_times").toString() : "") ? "000000" : map.get("total_charge_times").toString();
//                    msg.what = REFRESH_TIME;
//                    msg.obj = time_stamp;
//                    connHandler.sendMessage(msg);
//                }
//                if ("03".equals(map.get("order_state").toString()) || "04".equals(map.get("order_state").toString())) {
//                    finishCharge();
//                } else {
//                    Message connMsg = connHandler.obtainMessage();
//                    connMsg.what = REPEAT_CONN;
//                    connHandler.sendMessageDelayed(connMsg, REPEAT_DURATION);//30秒钟 更新一次
//                }
//
//            }
//        });
    }

    private boolean isInit = false;

    private void showOrHideSoc(String charge_soc) {
        if (charge_soc.equals("-1")) {
            gifImageView.setVisibility(View.VISIBLE);
            tv_charge_soc.setVisibility(View.GONE);
            tv_charge_soc_p.setVisibility(View.GONE);
        } else {
            gifImageView.setVisibility(View.GONE);
            tv_charge_soc.setVisibility(View.VISIBLE);
            tv_charge_soc_p.setVisibility(View.VISIBLE);
            tv_charge_soc.setText(charge_soc);
            curPersent = Float.valueOf(charge_soc) / 100;
            duration = (int) (curPersent * 7500);
            if (!isInit) {
                setWave(true);
                isInit = true;
            } else {
                wave.setWaterLevelRatio(curPersent);
            }
        }
    }

    /**
     * 充电结束后
     */
    private void finishCharge() {
        Log.e("aaaaa", "finishChargefinishChargefinishChargefinishChargefinishCharge");
//        ChargeStatus.INSTANCE.setStatus(ChargeStatus.STATUS_PREPARE_STOP);
//        if(alertDialog==null ||  (alertDialog!=null && !alertDialog.isShowing())){
//            alertDialog = DialogHelper.alertDialog(ChargingActivity.this, getString(R.string.dialog_prompt_charge_finish), new AlertDialog.OnDialogButtonClickListener() {
//                @Override
//                public void onConfirm() {
//                    startActivity(new Intent(ChargingActivity.this, FinishChargingActivity.class));
//                }
//
//                @Override
//                public void onCancel() {
//
//                }
//            });
//        }
//        mAnimatorSet.cancel();
//        wave.setWaterLevelRatio(1);
//        wave.setWaveLengthRatio(0);
//        wave.setWaveLengthRatio(0);
//        connHandler.removeMessages(REPEAT_CONN);
//        connHandler.removeMessages(REFRESH_TIME);
//        tv_charging_charge.setVisibility(View.GONE);
//        tv_title.setText("充电完成");
        Intent i = new Intent(ChargingActivity.this, FinishChargingActivity.class);
        i.putExtra("charge_order_id", CacheUtils.getIn().getUserInfo().getOut_trade_no());
        i.putExtra("station_id", station_id);
        startActivity(i);
    }


    @Override
    public void setView() {

    }

    @Override
    public void initDatas() {

    }

    @Override
    public void onComplete(String result, int type) {

    }

    @Override
    public void onFailure(String msg, int type) {

    }
}
