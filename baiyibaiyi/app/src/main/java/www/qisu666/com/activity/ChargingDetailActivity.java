package www.qisu666.com.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import www.qisu666.com.R;
import www.qisu666.com.adapter.ChargeFeeAdapter;
import www.qisu666.com.event.Message;
import www.qisu666.com.request.MyNetwork;
import www.qisu666.com.request.utils.FlatFunction;
import www.qisu666.com.request.utils.MyMessageUtils;
import www.qisu666.com.request.utils.ResultSubscriber;
import www.qisu666.com.request.utils.RxNetHelper;
import www.qisu666.com.utils.CacheUtils;
import www.qisu666.com.utils.JsonUtils;
import www.qisu666.com.utils.StringUtil;
import www.qisu666.com.widget.NoScrollListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



//充电结束详情页面
public class ChargingDetailActivity extends BaseActivity {

    private TextView tv_title;
    private ImageView img_title_left;

    private TextView tv_terminal_no;
    private TextView tv_payment_amount;
    private TextView tv_charging_amount;
    private TextView tv_charging_duration;
    private TextView tv_start_time;
    private TextView tv_end_time;

    private NoScrollListView lv_fees;
    private List<Map<String,Object>> list;
    private ChargeFeeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setView(R.layout.activity_charging_detail);
        initViews();
//        setAdapter();
        connToServer();
    }

    /**
     * 初始化控件
     */
    private void initViews() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(getIntent().getStringExtra("title"));
        img_title_left = (ImageView) findViewById(R.id.img_title_left);
        img_title_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tv_terminal_no = (TextView) findViewById(R.id.tv_terminal_no);
        tv_payment_amount = (TextView) findViewById(R.id.tv_payment_amount);
        tv_charging_amount = (TextView) findViewById(R.id.tv_charging_amount);
        tv_charging_duration = (TextView) findViewById(R.id.tv_charging_duration);
        tv_start_time = (TextView) findViewById(R.id.tv_start_time);
        tv_end_time = (TextView) findViewById(R.id.tv_end_time);
        lv_fees = (NoScrollListView) findViewById(R.id.lv_fees);
    }

    /**
     * 设置适配器
     */
    private void setAdapter() {
        list = new ArrayList<Map<String, Object>>();
        adapter = new ChargeFeeAdapter(this, list);
        lv_fees.setAdapter(adapter);

//        list.add(new HashMap<String, Object>());
//        list.add(new HashMap<String, Object>());
//        list.add(new HashMap<String, Object>());
//        list.add(new HashMap<String, Object>());
//        list.add(new HashMap<String, Object>());
//        list.add(new HashMap<String, Object>());
    }

    /**
     * 订单结束  查询结果
     */
    private void connToServer() {

        String url = "api/charge/order/result/query";
        HashMap<String, Object> map = new HashMap<>();
        map.put("userId", CacheUtils.getIn().getUserInfo().getId());
        map.put("outTradeNo", getIntent().getStringExtra("outTradeNo"));

        MyNetwork.getMyApi()
                .carRequest(url, MyMessageUtils.addBody(map))
                .map(new FlatFunction<>(Object.class))
                .compose(RxNetHelper.<Object>io_main())
                .subscribe(new ResultSubscriber<Object>() {
                    @Override
                    public void onSuccessCode(Message object) {

                    }

                    @SuppressLint("SetTextI18n")
                    @Override
                    @SuppressWarnings("unchecked")
                    public void onSuccess(Object bean) {
                        // 对象转json
                        String s = JsonUtils.objectToJson(bean);
                        // json转 map
                        Map jsonToMap = JsonUtils.jsonToMap(s);

                        tv_terminal_no.setText(getIntent().getStringExtra("terminalNumber"));
                        tv_payment_amount.setText("￥"+ jsonToMap.get("totalChargeMoney").toString());
                        tv_charging_amount.setText(jsonToMap.get("totalChargeQuantity").toString()+"度");
                        tv_charging_duration.setText(jsonToMap.get("totalChargeTimes").toString().length() > 5 ? StringUtil.formatTime(jsonToMap.get("totalChargeTimes").toString()) : "00:00:00");
                        tv_start_time.setText(jsonToMap.get("startChargeTime").toString());
                        tv_end_time.setText(jsonToMap.get("endChargeTime").toString());

//                        if (jsonToMap.get("record_list") != null && !"".equals(jsonToMap.get("record_list".toString()))) {
//                            try {
//                                JSONArray array = new JSONArray(jsonToMap.get("record_list").toString());
//
//                                int count = array.length();
//                                if (count != 0) {
//                                    for (int i = 0; i < count; i++) {
//                                        JSONObject object = array.getJSONObject(i);
//                                        list.add(JsonUtils.jsonToMap(object.toString()));
//                                    }
//                                }
//                                adapter.notifyDataSetChanged();
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
                    }

                    @Override
                    public void onFail(Message<Object> bean) {
                        Log.e("aaaa", "获取失败：" + bean.toString());
                    }

                });



//        JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject.put("req_code", "C103");
//
//            jsonObject.put("s_token", UserParams.INSTANCE.getS_token());
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        new HttpLogic(this).sendRequest(Config.REQUEST_URL, jsonObject, new AbstractResponseCallBack() {
//            @Override
//            public void onResponse(Map<String,Object> map, String tag) {
//                /**
//                 *  C103 response:{"terminal_number":"0001","charge_end_time":"20160909122525","charge_time":"101010","return_msg":"操作成功.","charge_station_id":"3","charge_electricity":"1",
//                 *      "reback_money":"0","pay_money":"5.5",
//                 *      "record_list":[{"charge_fee":"12.5","service_fee":"12.8","time_slot":"2015-2016","charge_electricity_slot":"12"}],"real_money":"5.5",
//                 *      "charge_station_name":"动力火车","return_code":"0000","charge_records_id":"1","charge_start_time":"20160909112525"}
//                 *
//                 */
//                tv_terminal_no.setText(map.get("terminal_number").toString());
//                tv_payment_amount.setText("￥"+ map.get("pay_money").toString());
//                tv_charging_amount.setText(map.get("charge_electricity").toString()+"度");
//                tv_charging_duration.setText(map.get("charge_time").toString().length() > 5 ? StringUtil.formatTime(map.get("charge_time").toString()) : "00:00:00");
//                tv_start_time.setText(StringUtil.formatDate(map.get("charge_start_time").toString()));
//                tv_end_time.setText(StringUtil.formatDate(map.get("charge_end_time").toString()));
//
//                if (map.get("record_list") != null && !"".equals(map.get("record_list".toString()))) {
//                    try {
//                        JSONArray array = new JSONArray(map.get("record_list").toString());
//
//                        int count = array.length();
//                        if (count != 0) {
//                            for (int i = 0; i < count; i++) {
//                                JSONObject object = array.getJSONObject(i);
//                                list.add(JsonUtils.jsonToMap(object.toString()));
//                            }
//                        }
//                        adapter.notifyDataSetChanged();
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });
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
