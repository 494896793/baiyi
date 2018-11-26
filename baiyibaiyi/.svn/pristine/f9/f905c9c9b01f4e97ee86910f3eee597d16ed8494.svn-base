package www.qisu666.com.activity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import www.qisu666.com.R;
import www.qisu666.com.callback.UserInfoResp;
import www.qisu666.com.constant.RequestUrls;
import www.qisu666.com.request.ReturnDepositRequest;
import www.qisu666.com.utils.CacheUtils;
import www.qisu666.com.utils.Logger;
import www.qisu666.com.utils.StringUtils;
import www.qisu666.com.utils.ToastUtil;
import www.qisu666.com.utils.UserUtils;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by wujiancheng on 2017/8/14.
 * 退押金
 */

public class DepositRefundActivity extends BaseActivity {
    @BindView(R.id.tvTitleRight)
    TextView tvTitleRight;
    @BindView(R.id.llytTitleRight)
    LinearLayout llytTitleRight;
    @BindView(R.id.tvTitleName)
    TextView tvTitleName;
    @BindView(R.id.etCardHolder)
    EditText etCardHolder;
    @BindView(R.id.etOpenCardBank)
    EditText etOpenCardBank;
    @BindView(R.id.etCardNumber)
    EditText etCardNumber;
    @BindView(R.id.tvCommit)
    TextView tvCommit;
    @BindView(R.id.ivDeleteOpenCardBank)
    ImageView ivDeleteOpenCardBank;
    @BindView(R.id.ivDeleteCardNumber)
    ImageView ivDeleteCardNumber;

    private static final String TAG = DepositRefundActivity.class.getSimpleName();
    private static final int REFUND_DEPOSIT = 1;
    private String cardHolder;
    private String openCardBank;
    private String cardNumber;
    private boolean hasCardHolder, hasOpenCardBank, hasCardNumber;

    private boolean isRun = false;
    private String d = "";

    @Override
    public void setView() {
        setContentView(R.layout.activity_deposit_refund);
    }

    @Override
    public void initDatas() {
        tvTitleName.setText("退款信息");
        setCanCommit(false, false, false);
        //持卡人
        etCardHolder.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                hasCardHolder = s.toString().trim().length() > 0;
                setCanCommit(hasCardHolder, hasOpenCardBank, hasCardNumber);
            }
        });
        etOpenCardBank.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String bankName = s.toString().trim();
                Logger.i("bankName=" + bankName);
                int inputLength = bankName.length();
                if (inputLength > 0) {
                    hasOpenCardBank = true;
//                    char lastWord = bankName.charAt(inputLength - 1);
//                    if (!(19968 <= lastWord && lastWord < 40869)) {//非汉字
//                        if (inputLength == 1) {
//                            etOpenCardBank.setText("");
//                            hasOpenCardBank = false;
//                        } else {
//                            etOpenCardBank.setText(bankName.substring(0, inputLength - 1));
//                        }
//                    }
                } else {
                    hasOpenCardBank = false;
                }
                setCanCommit(hasCardHolder, hasOpenCardBank, hasCardNumber);
//                etOpenCardBank.setSelection(etOpenCardBank.length());
            }
        });
        etCardNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isRun) {
                    isRun = false;
                    return;
                }
                isRun = true;
                d = "";
                String newStr = s.toString();
                newStr = newStr.replace(" ", "");
                int index = 0;
                while ((index + 4) < newStr.length()) {
                    d += (newStr.substring(index, index + 4) + " ");
                    index += 4;
                }
                d += (newStr.substring(index, newStr.length()));
                int i = etCardNumber.getSelectionStart();
                etCardNumber.setText(d);
                try {


                    if (i % 5 == 0 && before == 0) {
                        if (i + 1 <= d.length()) {
                            etCardNumber.setSelection(i + 1);
                        } else {
                            etCardNumber.setSelection(d.length());
                        }
                    } else if (before == 1 && i < d.length()) {
                        etCardNumber.setSelection(i);
                    } else if (before == 0 && i < d.length()) {
                        etCardNumber.setSelection(i);
                    } else
                        etCardNumber.setSelection(d.length());


                } catch (Exception e) {

                }
                hasCardNumber = s.toString().trim().length() > 0;
                setCanCommit(hasCardHolder, hasOpenCardBank, hasCardNumber);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        UserInfoResp userInfoResp = CacheUtils.getIn().getUserInfo();
        if (null != userInfoResp && !StringUtils.isEmpty(userInfoResp.getName())) {
            etCardHolder.setText(userInfoResp.getName());
            etCardHolder.setEnabled(false);
        }

        etOpenCardBank.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                ivDeleteOpenCardBank.setVisibility((hasFocus && !StringUtils.isEmpty(etOpenCardBank.getText().toString().trim())) ? View.VISIBLE : View.GONE);
            }
        });
        etCardNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                ivDeleteCardNumber.setVisibility((hasFocus && !StringUtils.isEmpty(etCardNumber.getText().toString().trim())) ? View.VISIBLE : View.GONE);
            }
        });

    }

    private void setCanCommit(boolean hasCardHolder, boolean hasOpenCardBank, boolean hasCardNumber) {
        tvCommit.setClickable(hasCardHolder && hasOpenCardBank && hasCardNumber);
        if (tvCommit.isClickable()) {
            tvCommit.setBackgroundResource(R.drawable.bg_blue_radius_login_enable);
        } else {
            tvCommit.setBackgroundResource(R.drawable.bg_blue_radius_login_unenable);
        }
    }

    @Override
    public void onComplete(String result, int type) {
        ToastUtil.show(mContext, getMsg(result));
        if (isSuccess(result)) {
            finish();
        }
    }

    @Override
    public void onFailure(String msg, int type) {
        if (type == REFUND_DEPOSIT) {
            ToastUtil.show(mContext, "退款失败");
        }
    }

    @OnClick({R.id.ivTitleLeft, R.id.tvCommit, R.id.ivDeleteOpenCardBank, R.id.ivDeleteCardNumber})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ivTitleLeft://返回
                finish();
                break;
            case R.id.tvCommit://提交退款
                refund();
                break;
            case R.id.ivDeleteOpenCardBank://删除开户行
                etOpenCardBank.setText("");
                ivDeleteOpenCardBank.setVisibility(View.GONE);
                break;
            case R.id.ivDeleteCardNumber://删除卡号
                etCardNumber.setText("");
                ivDeleteCardNumber.setVisibility(View.GONE);
                break;
        }
    }

    /**
     * 退款
     */
    private void refund() {
        String tmp = etOpenCardBank.getText().toString().trim();
        for (int i = 0; i < tmp.length(); i++) {
            char c = tmp.charAt(i);
            if (!(19968 <= c && c < 40869)) {//非汉字
                ToastUtil.show(mContext, "开户行不能存在非中文");
                return;
            }
        }
        ReturnDepositRequest data = new ReturnDepositRequest();
        data.setCustomerId(UserUtils.getCustomerId());
        cardNumber = etCardNumber.getText().toString().trim();
        cardNumber = cardNumber.replaceAll(" ", "");
        data.setBankCard(cardNumber);
        data.setBankCardUserName(etCardHolder.getText().toString().trim());

        openCardBank = etOpenCardBank.getText().toString().trim();
        data.setBankName(openCardBank);

        data.setMethod(RequestUrls.USER_REQUEST_REFUND);
        doGet(data, REFUND_DEPOSIT, "正在提交", true);
    }
}
