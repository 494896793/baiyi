package www.qisu666.com.activity;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import www.qisu666.com.R;
import www.qisu666.com.adapter.RechargeAdapter;
import www.qisu666.com.app.PayMode;
import www.qisu666.com.callback.PreRechargeResp;
import www.qisu666.com.callback.RechargeItem;
import www.qisu666.com.callback.RechargeListResp;
import www.qisu666.com.callback.SystemConfigResp;
import www.qisu666.com.callback.UserInfoResp;
import www.qisu666.com.callback.WeixinPayData;
import www.qisu666.com.constant.Config;
import www.qisu666.com.constant.RequestUrls;
import www.qisu666.com.pay.ali.AlipayManager;
import www.qisu666.com.pay.wx.WxPayManager;
import www.qisu666.com.request.ChargeRequest;
import www.qisu666.com.request.RechargeListRequest;
import www.qisu666.com.request.UserInfoRequest;
import www.qisu666.com.rx.RxBus;
import www.qisu666.com.rx.RxBusEvent;
import www.qisu666.com.rx.RxEventCodes;
import www.qisu666.com.utils.CacheUtils;
import www.qisu666.com.utils.CustomGridLayoutManager;
import www.qisu666.com.utils.HighlightUtil;
import www.qisu666.com.utils.Logger;
import www.qisu666.com.utils.StringUtils;
import www.qisu666.com.utils.ToastUtil;
import www.qisu666.com.utils.UserUtils;
import www.qisu666.com.view.CustomAlertDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.functions.Action1;

/**
 * 充值余额
 */
public class RechargeBalanceActivity extends BaseActivity {

    @BindView(R.id.tvTitleName)
    TextView tvTitleName;
    @BindView(R.id.scrollView)
    ScrollView scrollView;
    @BindView(R.id.rlytTip)
    RelativeLayout rlytTip;
    @BindView(R.id.tvFirstRechargeTip)
    TextView tvFirstRechargeTip;
    @BindView(R.id.tvFridayTip)
    TextView tvFridayTip;
    @BindView(R.id.rvRecharge)
    RecyclerView rvRecharge;
    @BindView(R.id.llytPresentMoney)
    LinearLayout llytPresentMoney;
    @BindView(R.id.tvPresentMoney)
    TextView tvPresentMoney;
    @BindView(R.id.ivWxCheck)
    ImageView ivWxCheck;
    @BindView(R.id.ivAliCheck)
    ImageView ivAliCheck;
    @BindView(R.id.tvToRecharge)
    TextView tvToRecharge;

    private int money;
    private int payMode = PayMode.WEIXIN_PAY_MODE;
    //    private String moneyGet;
    private Integer chooseId = -1;//选择的充值项的Id

    public static int COMMIT_RECHARGER_BALANCE = 1;//充值
    public static int QUERY_USER_INFO = 2;//用戶信息
    //    public static int QUERY_RECHARGER_BALANCE = 3;//充值
    public static int QUERY_RECHARGER_LIST = 4;//充值列表

    public String payType;//支付方式
    private String msg = "余额不能申请退款，只用于租车消费！";
    private RechargeAdapter rbAdapter;
    private int chooseItem = -1;
    private List<RechargeItem> mData = new ArrayList<>();
    private PreRechargeResp preRechargeResp;

    @Override
    public void setView() {
        setContentView(R.layout.activity_recharge_balance);
    }

    @Override
    public void initDatas() {
        tvTitleName.setText("充值");

        scrollView.setVisibility(View.GONE);
        initView();
        //充值列表
        queryRechargeList();

        observeEvent();
    }

    private void observeEvent() {
        busSubscription = RxBus.getInstance()
                .toObservable(RxBusEvent.class)
                .subscribe(new Action1<RxBusEvent>() {
                    @Override
                    public void call(RxBusEvent rxBusEvent) {
                        switch (rxBusEvent.getEventCode()) {
                            case RxEventCodes.CODE_RECHARGE_PAY_SUCCESS:
                                closeDialog();
                                //跳转到充值成功页面
                                if (null != preRechargeResp) {
                                    PreRechargeResp.CustomerRechargeDetail detail = preRechargeResp.getCustomerRechargeDetail();
                                    if (null != detail) {
                                        Intent intent = new Intent(mContext, RechargeSuccessActivity.class);
                                        intent.putExtra("customerRechargeDetail", detail);
                                        startActivity(intent);
                                    }
                                }
                                //充值成功关闭页面
                                finish();
                                break;
                            case RxEventCodes.CODE_CLOSE_WX_CLIENT_TIP:
                                closeDialog();
                                break;
                            default:
                                break;
                        }
                    }
                });
    }


    @Override
    public void onComplete(String result, int type) {
        if (isSuccess(result)) {
            if (type == COMMIT_RECHARGER_BALANCE) {//充值余额
                preRechargeResp = getBean(result, PreRechargeResp.class);
                if (null != preRechargeResp) {
                    if (payType.equals(PayMode.ALI_PAY_TYPE)) {
                        Logger.e(" result.getRequestData()=" + preRechargeResp.getCustomerRechargeRequestData());
                        doCheck("请稍后...", true);

                        AlipayManager alipayManager = new AlipayManager();
                        alipayManager.pay(this, preRechargeResp.getCustomerRechargeRequestData(), RxEventCodes.CODE_RECHARGE_PAY_SUCCESS);
                    } else if (payType.equals(PayMode.WEIXIN_PAY_TYPE)) {
                        WeixinPayData data = JSON.parseObject(preRechargeResp.getCustomerRechargeRequestData(), WeixinPayData.class);
                        WxPayManager wxPayManager = new WxPayManager();
                        wxPayManager.pay(RechargeBalanceActivity.this, application, data, money, RxEventCodes.CODE_RECHARGE_PAY_SUCCESS);
                    }
                }

            } else if (type == QUERY_USER_INFO) {//个人信息
                UserInfoResp userInfoResp = getBean(result, UserInfoResp.class);
                UserInfoResp memberInfo = CacheUtils.getIn().getUserInfo();
                if (memberInfo == null) {
                    return;
                }
                memberInfo.setPhone(userInfoResp.getPhone());
                CacheUtils.getIn().save(userInfoResp);
            } else if (type == QUERY_RECHARGER_LIST) {//充值列表
                RechargeListResp rechargeListResp = getBean(result, RechargeListResp.class);
                if (null != rechargeListResp) {
                    int isFirstTimeRecharge = rechargeListResp.isFirstTimeRecharge();
                    int isFridayRecharge = rechargeListResp.isFridayRecharge();

                    if (isFirstTimeRecharge == 1 && isFridayRecharge == 1) {
                        //没有首充和星期五活动
                        rlytTip.setVisibility(View.GONE);
                    } else {
                        rlytTip.setVisibility(View.VISIBLE);
                        if (isFirstTimeRecharge == 0) {
                            //有首充活动
                            String firstTimeRechargeDesc = rechargeListResp.getFirstTimeRechargeDesc();
                            if (!StringUtils.isEmpty(firstTimeRechargeDesc)) {
//                                int start = firstTimeRechargeDesc.indexOf("(");
                                firstTimeRechargeDesc = firstTimeRechargeDesc.replace("(", "<font color='#000010' weight='bold'><big>");
//                                int end = firstTimeRechargeDesc.indexOf(")");
                                firstTimeRechargeDesc = firstTimeRechargeDesc.replace(")", "</big></font>");
//                                String highLight = firstTimeRechargeDesc.substring(start, end);
                                tvFirstRechargeTip.setText(Html.fromHtml(firstTimeRechargeDesc));
                            } else {
                                tvFirstRechargeTip.setVisibility(View.GONE);
                            }
                        } else {
                            tvFirstRechargeTip.setVisibility(View.GONE);
                        }
                        if (isFridayRecharge == 0) {
                            String fridayRechargeDesc = rechargeListResp.getFridayRechargeDesc();
                            if (!StringUtils.isEmpty(fridayRechargeDesc)) {
                                //有星期五活动
                                int start = fridayRechargeDesc.indexOf("(");
                                fridayRechargeDesc = fridayRechargeDesc.replace("(", "");
                                int end = fridayRechargeDesc.indexOf(")");
                                fridayRechargeDesc = fridayRechargeDesc.replace(")", "");
                                String highLight = fridayRechargeDesc.substring(start, end);
                                tvFridayTip.setText(Html.fromHtml(HighlightUtil.convertHightlightText(fridayRechargeDesc, highLight, "#0B1222")));
                            } else {
                                tvFridayTip.setVisibility(View.GONE);
                            }
                        } else {
                            tvFridayTip.setVisibility(View.GONE);
                        }
                    }

                    List<RechargeItem> items = rechargeListResp.getRechargeList();
                    if (items.size() > 0) {
                        rbAdapter.setIsFirstTimeRecharge(isFirstTimeRecharge);
                        rbAdapter.setIsFridayRecharge(isFridayRecharge);
                        mData.clear();
                        mData.addAll(items);
                        rbAdapter.notifyItemRangeInserted(0, mData.size());
                    }
                }
                scrollView.setVisibility(View.VISIBLE);
            }
        } else {
            if (type == COMMIT_RECHARGER_BALANCE) {//充值余额
                showLongToast(getMsg(result));
            }
        }
    }

    @Override
    public void onFailure(String msg, int type) {
    }

    public void getUserInfo() {
        UserInfoRequest data = new UserInfoRequest();
        UserInfoResp userInfoResp = CacheUtils.getIn().getUserInfo();
        if (userInfoResp != null) {
            data.setCustomerPhone(userInfoResp.getPhone());
            data.setMethod(RequestUrls.QUERY_USER_INFO);
            doGet(data, QUERY_USER_INFO, "", false);
        }
    }

    private void initView() {
        payMode = PayMode.WEIXIN_PAY_MODE;

        rbAdapter = new RechargeAdapter(mContext, mData);
        rvRecharge.setLayoutManager(new CustomGridLayoutManager(mContext, 2));
        rvRecharge.setAdapter(rbAdapter);
        rbAdapter.setOnItemClickListner(new RechargeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, RechargeItem amount, int position) {
                rbAdapter.chooseItem(position);
                chooseItem = position;
                chooseId = amount.getId();
                money = amount.getMoney();
            }
        });
    }

    @OnClick({R.id.ivTitleLeft, R.id.llytRechargeProtocol, R.id.tvToRecharge, R.id.llytWxPay, R.id.llytAliPay})
    public void onClickedView(View view) {
        switch (view.getId()) {
            case R.id.ivTitleLeft:
                finish();
                break;
            case R.id.llytRechargeProtocol://充值协议
                SystemConfigResp systemConfigResp = CacheUtils.getIn().getSystem_Info();
                Intent intent = new Intent(this, WebActivity.class);
                intent.putExtra("title", "充值协议");
                intent.putExtra("url", systemConfigResp.getRechargeAgreementUrl());
                startActivity(intent);
                break;
            case R.id.tvToRecharge://充值
                if (chooseItem < 0) {
                    ToastUtil.show(mContext, "请选择您要充值的金额");
                    return;
                }
                UserInfoResp mUser = CacheUtils.getIn().getUserInfo();
                if (mUser != null && !TextUtils.isEmpty(mUser.getPhone())) {
                    showDialog(msg, 1);
                }
                break;
            case R.id.llytWxPay://微信支付
                payMode = PayMode.WEIXIN_PAY_MODE;
                ivWxCheck.setImageResource(R.mipmap.gr_24);
                ivAliCheck.setImageResource(R.mipmap.gr_25);
                break;
            case R.id.llytAliPay://支付宝支付
                payMode = PayMode.ALI_PAY_MODE;
                ivWxCheck.setImageResource(R.mipmap.gr_25);
                ivAliCheck.setImageResource(R.mipmap.gr_24);
                break;
        }
    }

    /**
     * 去充值
     */
    private void toPay() {
        payType = PayMode.ALI_PAY_TYPE;
        if (payMode == PayMode.ALI_PAY_MODE) {
            payType = PayMode.ALI_PAY_TYPE;
        } else if (payMode == PayMode.WEIXIN_PAY_MODE) {
            payType = PayMode.WEIXIN_PAY_TYPE;
        }
        if (chooseId == null || chooseId == -1) {
            showDialog("请选择您要充值的金额", 0);
            return;
        }

        ChargeRequest data = new ChargeRequest();
        data.setBankType(payType);
        data.setRechargeType(Config.BANLANCE_TYPE);
////        money = 1;
//        data.setRechargeMoney("" + money);
        data.setCustomerId(UserUtils.getCustomerId());
        data.setStandardId(chooseId + "");
        data.setMethod(RequestUrls.QUERY_PRE_PAY_RECHARGE);
        doGet(data, COMMIT_RECHARGER_BALANCE, "请稍后...", true);
    }

    private void showDialog(String msg, final int type) {
        final CustomAlertDialog alertDialog = CustomAlertDialog.getAlertDialog(mContext, true, true);
        alertDialog.setMessage(msg)
                .setBtnConfirmColor(R.color.new_primary)
                .setOnPositiveClickListener("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                        if (type == 1) {
                            toPay();
                        }
                    }
                }).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getUserInfo();
        closeDialog();
    }

    /**
     * 充值列表
     */
    private void queryRechargeList() {
        RechargeListRequest rechargeListRequest = new RechargeListRequest();
        rechargeListRequest.setMethod(RequestUrls.QUERY_RECHARGE_LIST);
        doGet(rechargeListRequest, QUERY_RECHARGER_LIST, Config.LOADING_STRING, true);
    }
}
