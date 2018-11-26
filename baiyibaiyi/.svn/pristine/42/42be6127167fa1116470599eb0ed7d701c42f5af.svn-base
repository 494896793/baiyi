package www.qisu666.com.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import www.qisu666.com.R;
import www.qisu666.com.adapter.DeviceDetailAdapter;
import www.qisu666.com.event.Message;
import www.qisu666.com.event.PaySuccessEvent;
import www.qisu666.com.request.MyNetwork;
import www.qisu666.com.request.utils.FlatFunction;
import www.qisu666.com.request.utils.MyMessageUtils;
import www.qisu666.com.request.utils.ResultSubscriber;
import www.qisu666.com.request.utils.RxNetHelper;
import www.qisu666.com.utils.CacheUtils;
import www.qisu666.com.utils.DialogHelper;
import www.qisu666.com.utils.GlobalParams;
import www.qisu666.com.utils.JsonUtils;
import www.qisu666.com.utils.SPParams;
import www.qisu666.com.utils.StringUtil;
import www.qisu666.com.utils.ToastUtil;
import www.qisu666.com.widget.AlertDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



//终端详情
public class DeviceDetailActivity extends BaseActivity {

    //    private ImageView right_btn;
    private LinearLayout layout_content;
    private TextView tv_station_name;
    private TextView tv_device_no;
    private TextView tv_charge_interface;
    private ImageView img_pile_type;
    private TextView tv_detail;
    private TextView tv_device_status;
    private TextView tv_device_service_fee;
    private ListView listView;
    //    private TextView tv_balance;
    private Button btn_charge;


    private Map<String, Object> map;

    //当前站点是否被收藏
    private boolean favorFlag = false;
    //站点id
    private String station_id = "";


    /**
     * 支付成功会接收到广播，关闭该界面
     */
//    private BroadcastReceiver finishReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if(intent.getAction().equals(ReceiverAction.ACTION_PAY_SUCCESS)){
//                finish();
//            }
//        }
//    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_detail);
        initView();
        initData();

        EventBus.getDefault().register(this);
//        registerReceiver(finishReceiver, new IntentFilter(ReceiverAction.ACTION_PAY_SUCCESS));

//        List<Map<String,Object>> list = new ArrayList<Map<String, Object>>();
//        list.add(new HashMap<String, Object>());
//        list.add(new HashMap<String, Object>());
//        list.add(new HashMap<String, Object>());
//        list.add(new HashMap<String, Object>());
//        list.add(new HashMap<String, Object>());
//        list.add(new HashMap<String, Object>());
//        list.add(new HashMap<String, Object>());
//        list.add(new HashMap<String, Object>());
//        listView.setAdapter(new DeviceDetailAdapter(DeviceDetailActivity.this, list));
    }

    @SuppressLint("SetTextI18n")
    @SuppressWarnings("unchecked")
    private void initData() {
        map = JsonUtils.jsonToMap(getIntent().getStringExtra("map"));
        Log.e("aaa", "map:" + map.toString());

//        LogUtils.e(map.toString());
        station_id = map.get("stationId").toString();
        getSharedPreferences(SPParams.CONFIG_INFO, Context.MODE_PRIVATE).edit().putString("stationId", station_id).apply();
        tv_station_name.setText(map.get("stationName").toString());
//                tv_device_no.setText(getIntent().getStringExtra("result"));
        tv_device_no.setText(map.get("chargePileName").toString());
        tv_charge_interface.setText((map.get("chargeInterface").toString().equals("01") ? "国标" : "特斯拉") + GlobalParams.chargeTypes[StringUtil.stringToInt(map.get("chargePileType").toString())]);
        img_pile_type.setImageResource(map.get("chargeMethod").toString().equals("01") ? R.mipmap.yd_34 : R.mipmap.yd_35);
        StringBuilder detail = new StringBuilder();
        detail.append(map.get("chargePileBel").toString().equals("01") ? "公共桩" : "个人桩").append(" ").
                append(GlobalParams.chargeTypes[StringUtil.stringToInt(map.get("chargePileType").toString())]).append(" ").append(map.get("chargePilePower").toString()).append("kW");
        tv_detail.setText(detail.toString());

        int state = Integer.valueOf(map.get("pileState").toString());
        tv_device_status.setText(GlobalParams.pileStates[state]);
        tv_device_status.setTextColor(ContextCompat.getColor(this, getStatusColor(state)));

//        right_btn.setImageResource(map.get("is_favor").toString().equals("0") ? R.mipmap.ic_collection_yellow : R.mipmap.ic_title_collection);
//        favorFlag = map.get("is_favor").toString().equals("0");
//        right_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                favorAction();
//            }
//        });

//        btn_charge.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity();
//            }
//        });

        if (map.get("pileState").toString().equals("07")) {
            btn_charge.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity();
                }
            });
        } else if (map.get("pileState").toString().equals("03")) {
            btn_charge.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showConfirmDialog();
                }
            });
        } else {
            btn_charge.setEnabled(false);
        }

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        if (map.get("timeList") != null && !"".equals(map.get("timeList"))) {
            try {
                String timeListStr=map.get("timeList").toString();
                Map<String,Object> map1=JsonUtils.jsonToMap(timeListStr);
                JSONArray array = new JSONArray(map1.get("values").toString());

                int count = array.length();
                if (count != 0) {
                    for (int i = 0; i < count; i++) {
                        JSONObject object = array.getJSONObject(i);
                        list.add(JsonUtils.jsonToMap(object.optString("nameValuePairs")));
                    }
                    tv_device_service_fee.setText("(" + list.get(0).get("servicePrice").toString() + "元/度)");
                }
                listView.setAdapter(new DeviceDetailAdapter(DeviceDetailActivity.this, list));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 根据状态设置显示颜色
     *
     * @param state
     * @return
     */
    private int getStatusColor(int state) {
        int color;
        switch (state) {
            case 0:
                color = R.color.warn_notice_color;
                break;
            case 1:
                color = R.color.warn_notice_color;
                break;
            case 2:
                color = R.color.warn_notice_color;
                break;
            case 3:
                color = R.color.new_primary;
                break;
            case 4:
                color = R.color.new_primary;
                break;
            case 5:
                color = R.color.new_primary;
                break;
            case 6:
                color = R.color.new_primary;
                break;
            case 7:
                color = R.color.new_primary;
                break;
            default:
                color = R.color.black;
                break;
        }
        return color;
    }

    /**
     * 显示未插枪的提示窗
     */
    private void showConfirmDialog() {
        DialogHelper.confirmDialog(DeviceDetailActivity.this, getString(R.string.dialog_prompt_not_insert_gun), new AlertDialog.OnDialogButtonClickListener() {

            @Override
            public void onConfirm() {
                connToServer();
            }

            @Override
            public void onCancel() {
                finish();
            }

        });
    }

    /**
     * 跳转到充电页面
     */
    private void startActivity() {
        Intent i = new Intent(DeviceDetailActivity.this, ConnectionActivity.class);
        i.putExtra("gunCode", map.get("gunCode").toString());
        i.putExtra("chargePileSeri", map.get("chargePileSeri").toString());
//        startActivityForResult(i, ConstantCode.REQ_PAY);
        startActivity(i);
    }

    private void initView() {
        tv_station_name = (TextView) findViewById(R.id.tv_station_name);
        tv_device_no = (TextView) findViewById(R.id.tv_device_no);
        tv_charge_interface = (TextView) findViewById(R.id.tv_charge_interface);
        img_pile_type = (ImageView) findViewById(R.id.img_pile_type);
        tv_detail = (TextView) findViewById(R.id.tv_detail);
        tv_device_service_fee = (TextView) findViewById(R.id.tv_device_service_fee);
        tv_device_status = (TextView) findViewById(R.id.tv_device_status);
        listView = (ListView) findViewById(R.id.listView);
        listView.setFocusable(false);
//        tv_balance = (TextView) findViewById(R.id.tv_balance);
        btn_charge = (Button) findViewById(R.id.btn_charge);
        initTitleBar();
    }

    private void initTitleBar() {
        TextView title = (TextView) findViewById(R.id.tv_title);
        title.setText(R.string.device_detail_title);
        View left_btn = findViewById(R.id.img_title_left);
        left_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
//        right_btn = (ImageView) findViewById(R.id.img_title_right);
//        right_btn.setImageResource(R.mipmap.ic_title_collection);
//        if(TextUtils.isEmpty(UserParams.INSTANCE.getUser_id())){
//            right_btn.setVisibility(View.GONE);
//        }else{
//            right_btn.setVisibility(View.VISIBLE);
//        }
    }

    /**
     * 发送请求，收藏或者取消收藏充电站
     */
//    private void favorAction() {
//        JSONObject jsonObject = new JSONObject();
//        if(!favorFlag){
//            //收藏站点
//            try {
//                jsonObject.put("req_code", "D104");
//                jsonObject.put("user_id", UserParams.INSTANCE.getUser_id());
//                jsonObject.put("s_token", UserParams.INSTANCE.getS_token());
//                jsonObject.put("station_id", station_id);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }else{
//            //取消收藏站点
//            try {
//                jsonObject.put("req_code", "D106");
//                jsonObject.put("user_id", UserParams.INSTANCE.getUser_id());
//                jsonObject.put("s_token", UserParams.INSTANCE.getS_token());
//                jsonObject.put("station_id", station_id);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//        HttpLogic httpLogic = new HttpLogic(this);
//        httpLogic.sendRequest(Config.REQUEST_URL, jsonObject, true, new AbstractResponseCallBack() {
//
//            @Override
//            public void onResponse(Map<String,Object> map, String tag) {
//                favorFlag = !favorFlag;
//                if(favorFlag){
//                    ToastUtil.showToast(R.string.toast_D104);
//                    right_btn.setImageResource(R.mipmap.ic_collection_yellow);
//                }else {
//                    ToastUtil.showToast(R.string.toast_D106);
//                    right_btn.setImageResource(R.mipmap.ic_title_collection);
//                }
//            }
//        });
//    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if(requestCode == ConstantCode.REQ_PAY && resultCode == ConstantCode.RES_PAY){
//            finish();
//        }
//    }

    /**
     * 发送 D107 请求，获取电桩信息
     */
    private void connToServer() {

        String url = "api/pile/gun/info/query";
        HashMap<String, Object> map = new HashMap<>();
        map.put("stationId ", station_id);
        map.put("userId", CacheUtils.getIn().getUserInfo().getId());

        MyNetwork.getMyApi()
                .carRequest(url, MyMessageUtils.addBody(map))
                .map(new FlatFunction<>(Object.class))
                .compose(RxNetHelper.<Object>io_main())
                .subscribe(new ResultSubscriber<Object>() {
                    @Override
                    public void onSuccessCode(Message object) {

                    }

                    @Override
                    @SuppressWarnings("unchecked")
                    public void onSuccess(Object bean) {
                        // 对象转json
                        String s = JsonUtils.objectToJson(bean);
                        Log.e("aaaa", "s:" + s);
                        // json转 map
                        Map jsonToMap = JsonUtils.jsonToMap(s);
                        Log.e("aaaa", "jsonToMap:" + jsonToMap.toString());

                        String return_code = jsonToMap.get("pile_state").toString();
                        if (!TextUtils.isEmpty(return_code)) {
                            if (jsonToMap.get("pile_state").toString().equals("07")) {
                                startActivity();
                            } else if (jsonToMap.get("pile_state").toString().equals("03")) {
//                        startPayActivity();
                                showConfirmDialog();
                            }
                            tv_device_status.setText(GlobalParams.pileStates[Integer.valueOf(jsonToMap.get("pile_state").toString())]);
                        } else {
                            ToastUtil.showToast(R.string.toast_D107_failed);
                        }
                    }

                    @Override
                    public void onFail(Message<Object> bean) {
                        Log.e("aaaa", "获取失败：" + bean.toString());
                    }

                });


//        JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject.put("req_code", "D107");
//            jsonObject.put("stationId ", station_id);
//            jsonObject.put("user_id", UserParams.INSTANCE.getUser_id());
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        HttpLogic httpLogic = new HttpLogic(this);
//        httpLogic.sendRequest(Config.REQUEST_URL, jsonObject, true, "rotate", new AbstractResponseCallBack() {
//
//            @Override
//            public void onResponse(Map<String, Object> map, String tag) {
//                /**
//                 * D107 response:{"station_name":"荷兰站","charge_pile_type":"01","return_msg":"操作成功.","charge_pile_num":"01","brand_id":"","charge_pile_seri":"000000012345","charge_pile_id":6,
//                 *      "time_list":[{"service_price":80,"end_time":"600","low_price":90,"start_time":"000","charge_price":90,"max_price":220,"avg_price":160,"high_price":200,"division_type":"low_price"},
//                 *                  {"service_price":80,"end_time":"1900","low_price":90,"start_time":"600","charge_price":90,"max_price":220,"avg_price":160,"high_price":200,"division_type":"max_price"},
//                 *                  {"service_price":80,"end_time":"2100","low_price":90,"start_time":"1900","charge_price":90,"max_price":220,"avg_price":160,"high_price":200,"division_type":"high_price"},
//                 *                  {"service_price":80,"end_time":"000","low_price":90,"start_time":"2100","charge_price":90,"max_price":220,"avg_price":160,"high_price":200,"division_type":"avg_price"}],
//                 *      "charge_pile_name":"超级快充桩","tariff_policy_id":6,"charge_interface":"01","charging_gun":"0","charge_method":"01","station_id":23,"pile_state":"00","return_code":"0000","charge_pile_bel":"01"}
//                 */
//                String return_code = map.get("return_code").toString();
//                if (return_code.equals("0000")) {
//                    if (map.get("pile_state").toString().equals("07")) {
//                        startPayActivity();
//                    } else if (map.get("pile_state").toString().equals("03")) {
////                        startPayActivity();
//                        showConfirmDialog();
//                    }
//                    tv_device_status.setText(GlobalParams.pileStates[Integer.valueOf(map.get("pile_state").toString())]);
//                } else {
//                    ToastUtil.showToast(R.string.toast_D107_failed);
//                }
//            }
//        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEventMainThread(PaySuccessEvent event) {
        finish();
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
