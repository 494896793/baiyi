package www.qisu666.com.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import www.qisu666.com.R;
import www.qisu666.com.app.PayMode;
import www.qisu666.com.utils.StringUtils;
import www.qisu666.com.utils.TVUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wujiancheng on 17-10-17.
 * 收款方式
 */

public class ReceiptAccountView extends LinearLayout {

    private ViewHolder viewHolder;

    public ReceiptAccountView(Context context) {
        super(context);
        init();
    }

    public ReceiptAccountView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ReceiptAccountView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_receipt_accout, this);
        viewHolder = new ViewHolder(view);
    }

    public void setData(String accountType, String accountNumber, String accountName, String accountMoney) {
        int accountMoneyI = 0;
        if (!StringUtils.isEmpty(accountMoney)) {
            accountMoneyI = Integer.parseInt(accountMoney);
            accountMoneyI = accountMoneyI / 100;
        }

        String account = "";
        String receiptStatusStr = "";
        String tip = "";
        if (PayMode.ORIGINAL_PAY_TYPE.equals(accountType)) {//原路退回
            if (PayMode.ALI_PAY_TYPE.equals(accountNumber)) {
                receiptStatusStr = "支付宝";
            } else if (PayMode.WEIXIN_PAY_TYPE.equals(accountNumber)) {
                receiptStatusStr = "微信";
            }
            tip = "金额 " + TVUtils.toStringInt(accountMoneyI + "") + "元";
        } else {//非原路退回
            if (PayMode.BALANCE_PAY_TYPE.equals(accountType)) {
                receiptStatusStr = "余额";
                account = accountName;
            } else if (PayMode.ALI_PAY_TYPE.equals(accountType)) {
                receiptStatusStr = "支付宝";
                account = accountNumber;
            } else if (PayMode.WEIXIN_PAY_TYPE.equals(accountType)) {
                receiptStatusStr = "微信";
                account = accountName;
            }
            tip = "账号 " + account + "，金额 " + TVUtils.toStringInt(accountMoneyI + "") + "元";
        }

        viewHolder.tvAccountType.setText("[收款方式]" + receiptStatusStr);
        viewHolder.tvReceiptDetail.setText(tip);
    }

    static class ViewHolder {
        @BindView(R.id.tvAccountType)
        TextView tvAccountType;
        @BindView(R.id.tvReceiptDetail)
        TextView tvReceiptDetail;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
