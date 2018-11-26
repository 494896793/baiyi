package www.qisu666.com.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import www.qisu666.com.R;


/**
 * Created by Administrator on 2015/8/19.
 */
public class AlertDialog extends Dialog {

    OnDialogButtonClickListener listener;

    private Context context;
    private String title = "";
    private String message,confirm = "确定",cancel = "取消";
    private boolean show;

    private TextView message_tv;
    private TextView title_tv;
    private TextView confirm_btn,cancel_btn;

    /**
     *
     * @param context
     * @param message 提示信息
     * @param show true 显示 确认/取消 两个按钮  false 仅显示 确认 按钮
     */
    public AlertDialog(Context context, String message, boolean show) {
        this(context, null, null, message ,show);
    }

    /**
     *
     * @param context
     * @param confirm 点击按钮文字
     * @param cancel 取消按钮文字
     * @param message 提示信息
     * @param show true 显示 确认/取消 两个按钮  false 仅显示 确认 按钮
     */
    public AlertDialog(Context context, String confirm, String cancel, String message, boolean show) {
        super(context, R.style.Dialog_Alert);
        this.context = context;
        if(confirm!=null){
            this.confirm = confirm;
        }
        if(cancel!=null){
            this.cancel = cancel;
        }
        this.message = message;
        this.show = show;
    }

    /**
     *
     * @param context
     * @param confirm 点击按钮文字
     * @param cancel 取消按钮文字
     * @param title 标题
     * @param message 提示信息
     * @param show true 显示 确认/取消 两个按钮  false 仅显示 确认 按钮
     */
    public AlertDialog(Context context, String confirm, String cancel, String title, String message, boolean show) {
        super(context, R.style.Dialog_Alert);
        this.context = context;
        if(confirm!=null){
            this.confirm = confirm;
        }
        if(cancel!=null){
            this.cancel = cancel;
        }
        this.message = message;
        this.title = title;
        this.show = show;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.dialog_alert);
        iniView();
    }

    private void iniView() {
        title_tv = (TextView)findViewById(R.id.title);
        message_tv = (TextView)findViewById(R.id.message);
        confirm_btn = (TextView) findViewById(R.id.confirm_btn);

        message_tv.setText(message);
        confirm_btn.setText(confirm);

        if (!TextUtils.isEmpty(title)){
            title_tv.setVisibility(View.VISIBLE);
            title_tv.setText(title);
        }else {
            title_tv.setVisibility(View.GONE);
        }


        if (show) {
            cancel_btn = (TextView) findViewById(R.id.cancel_btn);

            cancel_btn.setVisibility(View.VISIBLE);

            cancel_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (listener != null) {
                        cancel_btn.setTextColor(context.getResources().getColor(R.color.confirm_passed));
                        cancel_btn.setBackground(context.getResources().getDrawable(R.drawable.bt_main_radio));
                        confirm_btn.setTextColor(context.getResources().getColor(R.color.main_background));
                        confirm_btn.setBackground(context.getResources().getDrawable(R.drawable.bt_white_radio));
                        listener.onCancel();
                    }
                    cancel();
                }
            });
            cancel_btn.setText(cancel);

        }

        confirm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (listener != null) {
                    confirm_btn.setTextColor(context.getResources().getColor(R.color.confirm_passed));
                    confirm_btn.setBackground(context.getResources().getDrawable(R.drawable.bt_main_radio));
                    cancel_btn.setTextColor(context.getResources().getColor(R.color.main_background));
                    cancel_btn.setBackground(context.getResources().getDrawable(R.drawable.bt_white_radio));
                    listener.onConfirm();
                }
                cancel();
            }
        });
    }

    /**
     * 设置监听器
     * @param listener 按钮监听器
     * */
    public void setSampleDialogListener(OnDialogButtonClickListener listener){
        this.listener = listener;
    }

    public interface OnDialogButtonClickListener{
        void onConfirm();
        void onCancel();
    }

    public TextView getMessage_tv(){
        return message_tv;
    }

}
