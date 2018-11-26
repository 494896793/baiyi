package www.qisu666.com.view;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import www.qisu666.com.R;
import www.qisu666.com.app.MyApplication;
import www.qisu666.com.app.PayMode;
import www.qisu666.com.callback.SystemConfigResp;
import www.qisu666.com.constant.Config;
import www.qisu666.com.utils.CacheUtils;
import www.qisu666.com.utils.NetWorkUtils;
import www.qisu666.com.utils.SharedPreferencesUtils;
import www.qisu666.com.utils.ToastUtil;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wujiancheng on 2017/10/16.
 * 切换收款方式
 */

public class GetMoneyAccountView extends LinearLayout {
    public static final String ZHIFUBAO_ACCOUNT_NAME = "zhifubaoaccountname";
    public static final String ZHIFUBAO_ACCOUNT_NO = "zhifubaoaccountno";
    private ViewHolder viewHolder;
    private static final String appId = "wx1f605acead7ddda8";
    private String accountType = PayMode.ALI_PAY_TYPE;//A:支付宝，WX:微信
    private OnSwitchAccountTypeListener mOnSwitchAccountTypeListener;
    private boolean hasGetWxAccount = false;//是否已经获取过微信账号
    private String accountNumber;//微信的openId/支付宝账号
    private String accountName;//支付宝姓名

    public GetMoneyAccountView(Context context) {
        super(context);
        init();
    }

    public GetMoneyAccountView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GetMoneyAccountView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        View viewParent = LayoutInflater.from(getContext()).inflate(R.layout.layout_get_money_account, this);
        viewHolder = new ViewHolder(viewParent);

        SystemConfigResp info = CacheUtils.getIn().getSystem_Info();
        if (null != info) {
            //是否支持商家打款(0 支持，1不支持)
            String isSupportMerchant = info.getIsSupportMerchant();
            if ("0".equals(isSupportMerchant)) {
                canSwitch();
            } else {
                //隐藏切换按钮
                viewHolder.tvSwitchAccount.setVisibility(View.GONE);
            }
        } else {
            viewHolder.tvSwitchAccount.setVisibility(View.GONE);
        }

        //显示上次保存的支付宝账号
        setZhiFuBaoAccount();
    }

    /**
     * 设置背景
     *
     * @param resId R.color.xxx
     */
    public void setBackground(int resId) {
        viewHolder.llytContainer.setBackgroundResource(resId);
    }

    /**
     * 是否能切换收款方式
     */
    private void canSwitch() {
        viewHolder.llytSwitchAccount.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PayMode.ALI_PAY_TYPE.equals(accountType)) {
                    //切换到微信
                    viewHolder.tvAccountType.setText("微信");
//                    viewHolder.etAccountName.setEnabled(false);
                    viewHolder.etAccountName.setVisibility(View.GONE);

                    viewHolder.llytNickName.setVisibility(View.VISIBLE);
                    if (!hasGetWxAccount) {
                        viewHolder.tvNickName.setVisibility(View.GONE);
                        viewHolder.tvAccountName.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!hasGetWxAccount) {
                                    getWxAccount();
                                }
                            }
                        });
                        viewHolder.tvAccountName.setTextColor(ContextCompat.getColor(getContext(), R.color.new_primary));

                        String replace = "aa";
                        String getAccountInfo = "获取账号信息";
                        SpannableString spannableString = new SpannableString(getAccountInfo + " " + replace);
                        //获取图片
                        CustomImageSpan span = new CustomImageSpan(getContext(), R.mipmap.arrow_right_blue, CustomImageSpan.ALIGN_FONT_CENTER);
                        spannableString.setSpan(span, (getAccountInfo + " ").length(), (getAccountInfo + " " + replace).length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                        viewHolder.tvAccountName.setText(spannableString);
                    }

                    viewHolder.etAccountNo.setVisibility(View.GONE);
                    viewHolder.viewDivideLine.setVisibility(View.GONE);

                    accountType = PayMode.WEIXIN_PAY_TYPE;
                } else {
                    //切换到支付宝
                    viewHolder.tvAccountType.setText("支付宝");
//                    viewHolder.etAccountName.setEnabled(true);
//                    viewHolder.etAccountName.setText("");
//                    viewHolder.etAccountName.setTextColor(ContextCompat.getColor(getContext(), R.color.color_black_333333));

                    viewHolder.etAccountName.setVisibility(View.VISIBLE);
                    viewHolder.llytNickName.setVisibility(View.GONE);

                    viewHolder.etAccountNo.setVisibility(View.VISIBLE);
                    viewHolder.viewDivideLine.setVisibility(View.VISIBLE);
                    accountType = PayMode.ALI_PAY_TYPE;
                }
                if (null != mOnSwitchAccountTypeListener) {
                    mOnSwitchAccountTypeListener.onSwitchAccountType(accountType);
                }
            }
        });
    }

    static class ViewHolder {
        @BindView(R.id.llytContainer)
        LinearLayout llytContainer;
        @BindView(R.id.llytSwitchAccount)
        LinearLayout llytSwitchAccount;
        @BindView(R.id.tvAccountType)
        TextView tvAccountType;
        @BindView(R.id.tvSwitchAccount)
        TextView tvSwitchAccount;
        @BindView(R.id.etAccountName)
        EditText etAccountName;
        @BindView(R.id.llytNickName)
        LinearLayout llytNickName;
        @BindView(R.id.tvAccountName)
        TextView tvAccountName;
        @BindView(R.id.tvNickName)
        TextView tvNickName;
        @BindView(R.id.etAccountNo)
        EditText etAccountNo;
        @BindView(R.id.viewDivideLine)
        View viewDivideLine;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public interface OnSwitchAccountTypeListener {
        void onSwitchAccountType(String accountType);
    }

    public void setOnSwitchAccountTypeListener(OnSwitchAccountTypeListener onSwitchAccountTypeListener) {
        this.mOnSwitchAccountTypeListener = onSwitchAccountTypeListener;
    }

    /**
     * 获取用户微信信息
     */
    private void getWxAccount() {
        // send oauth request
        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";//获取个人信息
        req.state = "getMoneyAccount";
        MyApplication.getApplication().setAPP_ID(appId);
        IWXAPI api = WXAPIFactory.createWXAPI(getContext(), appId);
        api.registerApp(appId);
        if (!NetWorkUtils.isWXAppInstalledAndSupported(getContext())) {
            ToastUtil.show(getContext(), Config.NO_HAS_INSTALL_WX);
        } else {
            api.sendReq(req);
        }
    }

    /**
     * 设置微信昵称
     */
    public void setWxNickName(String nickName) {
        String replace = "打钩";
        String getAccountInfo = "获取成功：";
        SpannableString spannableString = new SpannableString(replace + " " + getAccountInfo);
        //获取图片
        CustomImageSpan span = new CustomImageSpan(getContext(), R.mipmap.hook_blue, CustomImageSpan.ALIGN_FONT_CENTER);
        spannableString.setSpan(span, 0, replace.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

        viewHolder.etAccountName.setVisibility(View.GONE);
        viewHolder.llytNickName.setVisibility(View.VISIBLE);
        viewHolder.tvNickName.setVisibility(View.VISIBLE);
        viewHolder.tvAccountName.setVisibility(View.VISIBLE);
        viewHolder.tvAccountName.setTextColor(ContextCompat.getColor(getContext(), R.color.color_gray_999999));
        viewHolder.tvAccountName.setText(spannableString);
        viewHolder.tvNickName.setText(nickName);

        hasGetWxAccount = true;
    }

    /**
     * 支付宝名称
     *
     * @return
     */
    public String getAliAccountName() {
        if (PayMode.ALI_PAY_TYPE.equals(accountType)) {
            accountName = viewHolder.etAccountName.getText().toString().trim();
        }
        return accountName;
    }

    /**
     * 获取支付宝账号
     *
     * @return
     */
    public String getAliAccountNumber() {
        if (PayMode.ALI_PAY_TYPE.equals(accountType)) {
            accountNumber = viewHolder.etAccountNo.getText().toString().trim();
        }
        return accountNumber;
    }

    /**
     * 保存支付宝账号信息
     *
     * @return
     */
    public void saveZhiFuBaoAccount() {
        if (PayMode.ALI_PAY_TYPE.equals(accountType)) {
            SharedPreferencesUtils.putString(getContext(), ZHIFUBAO_ACCOUNT_NAME, getAliAccountName());
            SharedPreferencesUtils.putString(getContext(), ZHIFUBAO_ACCOUNT_NO, getAliAccountNumber());
        }
    }

    public void saveZhiFuBaoAccount(String aliAccountName, String aliAccountNo) {
        SharedPreferencesUtils.putString(getContext(), ZHIFUBAO_ACCOUNT_NAME, aliAccountName);
        SharedPreferencesUtils.putString(getContext(), ZHIFUBAO_ACCOUNT_NO, aliAccountNo);
    }

    /**
     * 控件显示支付宝账号
     */
    public void setZhiFuBaoAccount() {
        if (PayMode.ALI_PAY_TYPE.equals(accountType)) {
            viewHolder.etAccountName.setText(SharedPreferencesUtils.getString(getContext(), ZHIFUBAO_ACCOUNT_NAME, ""));
            viewHolder.etAccountNo.setText(SharedPreferencesUtils.getString(getContext(), ZHIFUBAO_ACCOUNT_NO, ""));
        }
    }

    public void setZhiFuBaoAccount(String aliAccountName, String aliAccountNo) {
        viewHolder.etAccountName.setText(aliAccountName);
        viewHolder.etAccountNo.setText(aliAccountNo);

    }
}
