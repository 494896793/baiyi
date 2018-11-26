package www.qisu666.com.utils;

import android.app.Activity;
import android.content.Context;

import www.qisu666.com.widget.AlertDialog;
import www.qisu666.com.widget.LoadingDialog;


/**
 * Created by zsd on 2016/6/2.zsd
 */
public class DialogHelper {

    public static boolean isShowLoginDialog = false;

    //含确认取消的对话框
    public static AlertDialog confirmDialog(Context context, String message, AlertDialog.OnDialogButtonClickListener listener){
        AlertDialog dialog = new AlertDialog(context, message, true);
        dialog.show();
        dialog.setSampleDialogListener(listener);
        return dialog;
    }

    //不含取消按钮
    public static AlertDialog confirmTitleDialog2(Context context, String title, String message, AlertDialog.OnDialogButtonClickListener listener){
        AlertDialog dialog = new AlertDialog(context, "确认", null, title, message, true);
        dialog.show();
        dialog.setSampleDialogListener(listener);
        return dialog;
    }

    public static AlertDialog confirmTitleDialog(Context context, String title, String message, AlertDialog.OnDialogButtonClickListener listener){
        AlertDialog dialog = new AlertDialog(context, "确认", "取消", title, message, true);
        dialog.show();
        dialog.setSampleDialogListener(listener);
        return dialog;
    }

    //取消合同专用
    public static AlertDialog confirmTitleDialog_hetong(Context context, String title, String message, AlertDialog.OnDialogButtonClickListener listener){
        AlertDialog dialog = new AlertDialog(context, "确定申请", "取消申请", title, message, true);
        dialog.show();
        dialog.setSampleDialogListener(listener);
        return dialog;
    }


    public static AlertDialog confirmTitleDialog(Context context, String title, String message, String confirm, String cancel, AlertDialog.OnDialogButtonClickListener listener){
        AlertDialog dialog = new AlertDialog(context, confirm, cancel, title, message, true);
        dialog.show();
        dialog.setSampleDialogListener(listener);
        return dialog;
    }

    //仅含确认的对话框
    public static AlertDialog alertDialog(Context context, String message){
        AlertDialog dialog = new AlertDialog(context, message, false);
        dialog.show();
        return dialog;
    }

    //仅含确认的对话框
    public static AlertDialog alertDialog(Context context, String title, String message){
        AlertDialog dialog = new AlertDialog(context, null, null, title, message, false);
        dialog.show();
        return dialog;
    }



    //仅含确认的对话框
    public static AlertDialog alertDialog(Context context, String message, AlertDialog.OnDialogButtonClickListener listener){
        AlertDialog dialog = new AlertDialog(context, message, false);
        dialog.show();
        dialog.setSampleDialogListener(listener);
        return dialog;
    }

    //仅含确认的对话框
    public static AlertDialog finishDialog(final Activity activity, String message){
        final AlertDialog dialog = new AlertDialog(activity, message, false);
        dialog.setSampleDialogListener(new AlertDialog.OnDialogButtonClickListener() {
            @Override
            public void onConfirm() {
                dialog.dismiss();
                activity.finish();
            }

            @Override
            public void onCancel() {

            }
        });
        dialog.show();
        return dialog;
    }

    //仅含确认的对话框
    public static AlertDialog alertDialog(Context context, String message, AlertDialog.OnDialogButtonClickListener listener, boolean isBack){
        AlertDialog dialog = new AlertDialog(context, message, false);
        dialog.setCancelable(false);//不可取消
        dialog.show();
        dialog.setSampleDialogListener(listener);
        return dialog;
    }



    public static LoadingDialog loadingDialog(Context context){
        LoadingDialog loadingDialog = new LoadingDialog(context);
        loadingDialog.show();
        return loadingDialog;
    }

    public static LoadingDialog loadingDialog(Context context, String type, String message){
        LoadingDialog loadingDialog = new LoadingDialog(context, type, message);
        loadingDialog.show();
        return loadingDialog;
    }

    public static LoadingDialog loadingDialog(Context context, String message){
        LoadingDialog loadingDialog = new LoadingDialog(context, LoadingDialog.TYPE_ROTATE, message);
        loadingDialog.show();
        return loadingDialog;
    }

    public static LoadingDialog loadingAletDialog(Context context, String message){
        LoadingDialog loadingDialog = new LoadingDialog(context, LoadingDialog.TYPE_ROTATE, message);
        return loadingDialog;
    }

}
