package www.qisu666.com.view;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import www.qisu666.com.R;
import www.qisu666.com.app.PayMode;
import www.qisu666.com.callback.UserInfoResp;
import www.qisu666.com.constant.Config;
import www.qisu666.com.utils.Arith;
import www.qisu666.com.utils.CacheUtils;
import www.qisu666.com.utils.StringUtils;
import www.qisu666.com.utils.TVUtils;

/**
 * Created by wujiancheng on 2017/9/24.
 * 选择支付方式
 */

public class ChoosePayTypeView extends LinearLayout implements View.OnClickListener {
    private OnPayTypeChooseListener mOnPayTypeChooseListener;
    private PayViewHolder payViewHolder;
    private String payType;
    private UserInfoResp.Company company;
    private double companyAmount = 0;//企业余额
    private int companyDiscount;//企业折扣
    private String discountLimitPerson;

    //用户余额
    private double useBalance;

    public ChoosePayTypeView(Context context) {
        super(context);
        init();
    }

    public ChoosePayTypeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ChoosePayTypeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_choose_pay_mode, this);
        payViewHolder = new PayViewHolder(view);

        UserInfoResp userInfoResp = CacheUtils.getIn().getUserInfo();
        //余额
        if (null != userInfoResp) {
            //个人余额
            String balance = userInfoResp.getBalance();
            if (StringUtils.isIntOrFloat(balance)) {
                try {
                    useBalance = Arith.div(balance, "100.00", 2);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            //企业
            company = userInfoResp.getCompany();
            if (null != company) {
                //企业名称
                String companyName = company.getCompanyName();
                //剩余额度
                String amount = userInfoResp.getQuotaRemain();
                String nameAndAmount;
                if (!StringUtils.isEmpty(amount) && !"0".equals(amount)) {
                    companyAmount = Integer.parseInt(amount) / 100.00;
                    nameAndAmount = companyName + "(" + TVUtils.toString(companyAmount) + ")";
                } else {
                    nameAndAmount = companyName + "(0.00)";
                }
                payViewHolder.tvCompanyName.setText(nameAndAmount);
                discountLimitPerson = company.getDiscountLimitPerson();
                //企业折扣
                companyDiscount = company.getDiscount();
                if (companyDiscount != 0) {
                    String discountStr = TVUtils.toString1(companyDiscount / 10.0f) + "折";
                    payViewHolder.tvCompanyDiscount.setText(discountStr);
                    payViewHolder.tvCompanyDiscountBalance.setText(discountStr);
                    payViewHolder.tvCompanyDiscountWeixin.setText(discountStr);
                    payViewHolder.tvCompanyDiscountAli.setText(discountStr);
                }
                judgeDiscountFlagVisibility();
            }

        }
        payViewHolder.tvPayType.setText("余额(" + TVUtils.toString(useBalance) + ")");

        payViewHolder.llytCompanyPay.setOnClickListener(this);
        payViewHolder.llytBalancePay.setOnClickListener(this);
        payViewHolder.llytWeixinPay.setOnClickListener(this);
        payViewHolder.llytAliPay.setOnClickListener(this);
    }

    /**
     * 设置用户应付金额
     *
     * @param payMoney           应付金额
     * @param orderType          订单类型
     * @param isChooseCompanyPay true：企业用户打开企业支付开关，false：非企业用户或者企业用户关闭企业支付开关
     */
    public void setPayMoney(double payMoney, String orderType, boolean isChooseCompanyPay) {
        //非企业用户或者企业用户没有选择企业支付，则隐藏企业支付
        payViewHolder.llytCompanyPay.setVisibility(isChooseCompanyPay ? View.VISIBLE : View.GONE);
        if (null != company && Config.ORDER_CATEGORY_TIME_RENT.equals(orderType)) {//是企业用户,并且是时租订单
            if (isChooseCompanyPay) {
                judgeDiscountFlagVisibility();
            } else {
                setDiscountFlagVisibility(View.GONE, View.GONE);
            }

            if (isChooseCompanyPay && companyAmount >= payMoney) {//选择了企业支付，并且企业额度大于支付金额，就可以企业支付
                chooseCompanyPay();
                //余额不够支付，置灰
                if (useBalance < payMoney) {
                    setBalancePayUnenable();
                }
            } else {
                //企业支付置灰
                setCompanyPayUnenable();
                //选择个人支付
                setBalanceBg(useBalance >= payMoney);
            }
        } else {//选择个人支付
            //隐藏打折标签
            setDiscountFlagVisibility(View.GONE, View.GONE);

            setBalanceBg(useBalance >= payMoney);
        }
    }

    /**
     * 企业支付优惠金额
     *
     * @param companyDiscountMoney
     */
    public void setCompanyDiscountMoney(String companyDiscountMoney) {
        String discountMoney = "优惠" + companyDiscountMoney + "元";
        payViewHolder.tvCompanyDiscountMoney.setText(discountMoney);
        if ("1".equals(discountLimitPerson)) {//有个人支付也有企业折扣
            payViewHolder.tvCompanyDiscountMoneyBalance.setText(discountMoney);
            payViewHolder.tvCompanyDiscountMoneyWeixin.setText(discountMoney);
            payViewHolder.tvCompanyDiscountMoneyAli.setText(discountMoney);
        }
    }

    /**
     * 设置打折标签可见性
     */
    private void setDiscountFlagVisibility(int visibilityCompany, int visibilityPerson) {
        payViewHolder.tvCompanyDiscount.setVisibility(visibilityCompany);
        payViewHolder.tvCompanyDiscountBalance.setVisibility(visibilityPerson);
        payViewHolder.tvCompanyDiscountWeixin.setVisibility(visibilityPerson);
        payViewHolder.tvCompanyDiscountAli.setVisibility(visibilityPerson);
    }

    /**
     * 判断打折标签的可见性
     */
    private void judgeDiscountFlagVisibility() {
        if (company != null && companyDiscount != 0) {
            if ("1".equals(discountLimitPerson)) {
                setDiscountFlagVisibility(View.VISIBLE, View.VISIBLE);
            } else {
                //有企业折扣，没有个人折扣
                setDiscountFlagVisibility(View.VISIBLE, View.GONE);
            }
        } else {
            //隐藏打折标签
            setDiscountFlagVisibility(View.GONE, View.GONE);
        }
    }

    /**
     * 设置余额是否可点击
     *
     * @param enable 是否可点击
     */
    private void setBalanceBg(boolean enable) {
        if (enable) {
            chooseBalancePay();
        } else {
            //余额不够，置灰
            setBalancePayUnenable();
            //默认选择微信支付
            setPayMode(PayMode.WEIXIN_PAY_MODE);
        }
    }

    /**
     * 选择余额支付
     */
    private void chooseBalancePay() {
//        payViewHolder.ivBalancePic.setImageResource(R.mipmap.pay_has_balance);
//        payViewHolder.tvPayType.setTextColor(ContextCompat.getColor(getContext(), R.color.color_black_333333));
//        payViewHolder.tvCompanyDiscountBalance.setBackgroundResource(R.drawable.bg_solid_yellow);
        payViewHolder.ivChoosePaymoney.setVisibility(View.VISIBLE);
        payViewHolder.llytBalancePay.setEnabled(true);
        setPayMode(PayMode.BALANCE_PAY_MODE);
    }

    /**
     * 余额支付置灰
     */
    private void setBalancePayUnenable() {
//        payViewHolder.ivBalancePic.setImageResource(R.mipmap.pay_no_balance);
//        payViewHolder.tvPayType.setTextColor(ContextCompat.getColor(getContext(), R.color.color_gray_cccccc));
//        payViewHolder.tvCompanyDiscountBalance.setBackgroundResource(R.drawable.bg_solid_gray);
        payViewHolder.ivChoosePaymoney.setVisibility(View.GONE);
        payViewHolder.llytBalancePay.setEnabled(false);
    }

    /**
     * 选择企业支付
     */
    private void chooseCompanyPay() {
        payViewHolder.ivCompanyIcon.setImageResource(R.mipmap.pay_company_icon);
        payViewHolder.tvCompanyName.setTextColor(ContextCompat.getColor(getContext(), R.color.color_black_333333));
        payViewHolder.tvCompanyDiscount.setBackgroundResource(R.drawable.bg_solid_yellow);
        payViewHolder.ivChooseCompany.setVisibility(View.VISIBLE);
        payViewHolder.llytCompanyPay.setEnabled(true);
        setPayMode(PayMode.COMPANY_PAY_MODE);
    }

    /**
     * 企业支付置灰
     */
    private void setCompanyPayUnenable() {
        payViewHolder.ivCompanyIcon.setImageResource(R.mipmap.pay_company_not_enough);
        payViewHolder.tvCompanyName.setTextColor(ContextCompat.getColor(getContext(), R.color.color_gray_cccccc));
        payViewHolder.tvCompanyDiscount.setBackgroundResource(R.drawable.bg_solid_gray);
        payViewHolder.ivChooseCompany.setVisibility(View.GONE);
        payViewHolder.llytCompanyPay.setEnabled(false);
//        setPayMode(PayMode.COMPANY_PAY_MODE);
    }

    /**
     * 设置支付类型
     */
    private void setPayMode(int payMode) {
        payViewHolder.ivChooseCompany.setImageResource(R.mipmap.yc_42);
        payViewHolder.ivChoosePaymoney.setImageResource(R.mipmap.yc_42);
        payViewHolder.ivChooseWeixinpaymoney.setImageResource(R.mipmap.yc_42);
        payViewHolder.ivChoosePaytreasure.setImageResource(R.mipmap.yc_42);

        payViewHolder.tvCompanyDiscountMoney.setVisibility(View.GONE);
        payViewHolder.tvCompanyDiscountMoneyBalance.setVisibility(View.GONE);
        payViewHolder.tvCompanyDiscountMoneyWeixin.setVisibility(View.GONE);
        payViewHolder.tvCompanyDiscountMoneyAli.setVisibility(View.GONE);

        switch (payMode) {
            case PayMode.COMPANY_PAY_MODE://企业支付
                payType = PayMode.COMPANY_PAY_TYPE;
                payViewHolder.ivChooseCompany.setImageResource(R.mipmap.yc_43);
                payViewHolder.tvCompanyDiscountMoney.setVisibility(payViewHolder.tvCompanyDiscount.getVisibility());
                break;
            case PayMode.BALANCE_PAY_MODE://余额支付
                payType = PayMode.BALANCE_PAY_TYPE;
                payViewHolder.ivChoosePaymoney.setImageResource(R.mipmap.yc_43);
                payViewHolder.tvCompanyDiscountMoneyBalance.setVisibility(payViewHolder.tvCompanyDiscountBalance.getVisibility());
                break;
            case PayMode.WEIXIN_PAY_MODE://微信支付
                payType = PayMode.WEIXIN_PAY_TYPE;
                payViewHolder.ivChooseWeixinpaymoney.setImageResource(R.mipmap.yc_43);
                payViewHolder.tvCompanyDiscountMoneyWeixin.setVisibility(payViewHolder.tvCompanyDiscountWeixin.getVisibility());
                break;
            case PayMode.ALI_PAY_MODE://阿里支付
                payType = PayMode.ALI_PAY_TYPE;
                payViewHolder.ivChoosePaytreasure.setImageResource(R.mipmap.yc_43);
                payViewHolder.tvCompanyDiscountMoneyAli.setVisibility(payViewHolder.tvCompanyDiscountAli.getVisibility());
                break;
        }
        if (null != mOnPayTypeChooseListener) {
            mOnPayTypeChooseListener.onPayTypeChoose(payType);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llytCompanyPay://企业支付
                setPayMode(PayMode.COMPANY_PAY_MODE);
                break;
            case R.id.llytBalancePay://余额支付
                setPayMode(PayMode.BALANCE_PAY_MODE);
                break;
            case R.id.llytWeixinPay://微信支付
                setPayMode(PayMode.WEIXIN_PAY_MODE);
                break;
            case R.id.llytAliPay://阿里支付
                setPayMode(PayMode.ALI_PAY_MODE);
                break;
        }
    }

    public interface OnPayTypeChooseListener {
        void onPayTypeChoose(String payType);
    }

    public void setOnPayTypeChooseListener(OnPayTypeChooseListener onPayTypeChooseListener) {
        this.mOnPayTypeChooseListener = onPayTypeChooseListener;
    }

    class PayViewHolder {
        //企业支付
        LinearLayout llytCompanyPay;
        ImageView ivCompanyIcon;
        TextView tvCompanyName;
        TextView tvCompanyDiscount;
        ImageView ivChooseCompany;
        TextView tvCompanyDiscountMoney;
        //余额支付
        LinearLayout llytBalancePay;
        ImageView ivBalancePic;
        TextView tvPayType;
        ImageView ivChoosePaymoney;
        TextView tvCompanyDiscountBalance;
        TextView tvCompanyDiscountMoneyBalance;
        //微信支付
        LinearLayout llytWeixinPay;
        ImageView ivWeixinPic;
        ImageView ivChooseWeixinpaymoney;
        TextView tvCompanyDiscountWeixin;
        TextView tvCompanyDiscountMoneyWeixin;
        //阿里支付
        LinearLayout llytAliPay;
        ImageView ivPayTreasure;
        ImageView ivChoosePaytreasure;
        TextView tvCompanyDiscountAli;
        TextView tvCompanyDiscountMoneyAli;

        PayViewHolder(View view) {
            llytCompanyPay = view.findViewById(R.id.llytCompanyPay);
            ivCompanyIcon = view.findViewById(R.id.ivCompanyIcon);
            tvCompanyName = view.findViewById(R.id.tvCompanyName);
            tvCompanyDiscount = view.findViewById(R.id.tvCompanyDiscount);
            ivChooseCompany = view.findViewById(R.id.ivChooseCompany);
            tvCompanyDiscountMoney = view.findViewById(R.id.tvCompanyDiscountMoney);

            llytBalancePay = view.findViewById(R.id.llytBalancePay);
            ivBalancePic = view.findViewById(R.id.iv_balance_pic);
            tvPayType = view.findViewById(R.id.tvPayType);
            ivChoosePaymoney = view.findViewById(R.id.iv_choose_paymoney);
            tvCompanyDiscountBalance = view.findViewById(R.id.tvCompanyDiscountBalance);
            tvCompanyDiscountMoneyBalance = view.findViewById(R.id.tvCompanyDiscountMoneyBalance);

            llytWeixinPay = view.findViewById(R.id.llytWeixinPay);
            ivWeixinPic = view.findViewById(R.id.iv_weixin_pic);
            ivChooseWeixinpaymoney = view.findViewById(R.id.iv_choose_weixinpaymoney);
            tvCompanyDiscountWeixin = view.findViewById(R.id.tvCompanyDiscountWeixin);
            tvCompanyDiscountMoneyWeixin = view.findViewById(R.id.tvCompanyDiscountMoneyWeixin);

            llytAliPay = view.findViewById(R.id.llytAliPay);
            ivPayTreasure = view.findViewById(R.id.iv_pay_treasure);
            ivChoosePaytreasure = view.findViewById(R.id.iv_choose_paytreasure);
            tvCompanyDiscountAli = view.findViewById(R.id.tvCompanyDiscountAli);
            tvCompanyDiscountMoneyAli = view.findViewById(R.id.tvCompanyDiscountMoneyAli);
        }
    }
}
