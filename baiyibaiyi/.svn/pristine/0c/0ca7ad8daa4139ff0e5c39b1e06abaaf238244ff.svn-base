package www.qisu666.com.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import www.qisu666.com.R;
import www.qisu666.com.app.CouponType;
import www.qisu666.com.constant.Config;
import www.qisu666.com.constant.RequestUrls;
import www.qisu666.com.fragment.MyCouponFragment;
import www.qisu666.com.request.CouponBindRequest;
import www.qisu666.com.request.ScanQrCodeCouponRequest;
import www.qisu666.com.rx.RxBus;
import www.qisu666.com.rx.RxBusEvent;
import www.qisu666.com.rx.RxEventCodes;
import www.qisu666.com.utils.DataUtils;
import www.qisu666.com.utils.Logger;
import www.qisu666.com.utils.ToastUtil;
import www.qisu666.com.utils.UserUtils;
import www.qisu666.com.view.CouponViewPagerWithTitleView;
import www.qisu666.com.view.CustomAlertDialog;
import www.qisu666.com.view.ViewPagerWithTitleView;
import com.baogang.zxing.CaptureActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import rx.functions.Action1;

public class CouponActivity extends BaseActivity {

    @BindView(R.id.tvTitleName)
    TextView tvTitleName;
    @BindView(R.id.btn_registered_coupon)
    Button btnRegisteredCoupon;
    @BindView(R.id.et_coupon_id)
    EditText etCouponId;
    @BindView(R.id.layout_binding_coupons)
    RelativeLayout layoutBindingCoupons;
    @BindView(R.id.viewPagerWithTitleView)
    CouponViewPagerWithTitleView viewPagerWithTitleView;

    private int QUERY_BIND_COUPON = 1;//绑定优惠码
    private int QUERY_QRCODE_COUPON = 2;//扫描二维码得优惠券

    private static final int REQUEST_SCAN_QR_CODE = 1;

    @Override
    public void setView() {
        setContentView(R.layout.activity_coupon);
    }

    @Override
    public void initDatas() {
        tvTitleName.setText("优惠券");
        observeRxEventCode();

        //标题
        List<String> titles = new ArrayList<String>() {{
            add("未使用");
            add("使用记录");
            add("已过期");
        }};
        //内容
        List<Fragment> fragments = new ArrayList<>();
        //可使用
        Fragment fragmentCanUse = new MyCouponFragment();
        Bundle bundleCanUse = new Bundle();
        bundleCanUse.putString("type", CouponType.CAN_USE);
        fragmentCanUse.setArguments(bundleCanUse);
        //已使用
        Fragment fragmentUsed = new MyCouponFragment();
        Bundle bundleUsed = new Bundle();
        bundleUsed.putString("type", CouponType.USED);
        fragmentUsed.setArguments(bundleUsed);
        //过期
        Fragment fragmentInvalid = new MyCouponFragment();
        Bundle bundleInvalid = new Bundle();
        bundleInvalid.putString("type", CouponType.OUT_TIME);
        fragmentInvalid.setArguments(bundleInvalid);

        fragments.add(fragmentCanUse);
        fragments.add(fragmentUsed);
        fragments.add(fragmentInvalid);

        viewPagerWithTitleView.setData(this, titles, fragments);
    }

    private void observeRxEventCode() {
        busSubscription = RxBus.getInstance()
                .toObservable(RxBusEvent.class)
                .subscribe(new Action1<RxBusEvent>() {
                    @Override
                    public void call(RxBusEvent rxBusEvent) {
                        int eventCode = rxBusEvent.getEventCode();
                        switch (eventCode) {
                            case RxEventCodes.CODE_UPDATE_COUPON_COUNT://更新优惠券的数量显示
                                Logger.i("CODE_UPDATE_COUPON_COUNT");
                                final Map<String, String> map = (Map<String, String>) rxBusEvent.getContent();

                                List<String> titles = new ArrayList<String>() {{
                                    String canUseNumber = map.get(CouponType.CAN_USE);
                                    String usedNumber = map.get(CouponType.USED);
                                    String outTimeNumber = map.get(CouponType.OUT_TIME);
                                    Logger.i("canUseNumber = " + canUseNumber + ",usedNumber=" + usedNumber + ",outTimeNumber=" + outTimeNumber);

                                    add("未使用(" + canUseNumber + ")");
                                    add("使用记录(" + usedNumber + ")");
                                    add("已过期(" + outTimeNumber + ")");
                                }};
                                viewPagerWithTitleView.updateTitle(titles);
                                break;
                        }
                    }
                });
    }

    @Override
    public void onComplete(String result, int type) {
        if (isSuccess(result)) {
            if (type == QUERY_BIND_COUPON || type == QUERY_QRCODE_COUPON) {
                if (type == QUERY_BIND_COUPON) {
                    ToastUtil.show(mContext, "绑定优惠券成功");
                } else {
                    ToastUtil.showImage(mContext, getMsg(result));
                }

                RxBusEvent event = new RxBusEvent();
                event.setEventCode(RxEventCodes.CODE_BIND_COUPON_SUCCESS);
                RxBus.getInstance().post(event);
            }
        } else {
//            if (type == QUERY_BIND_COUPON || type == QUERY_QRCODE_COUPON) {
//                showTipDialog(getMsg(result));
//            } else {
            ToastUtil.show(mContext, getMsg(result));
//            }
        }
    }

    @Override
    public void onFailure(String msg, int type) {

    }

    @OnClick({R.id.ivTitleLeft, R.id.btn_registered_coupon, R.id.ivQrCode})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.ivTitleLeft:
                finish();
                break;
            case R.id.btn_registered_coupon:
                String discount_id = etCouponId.getText().toString().trim();
                if (TextUtils.isEmpty(discount_id)) {
                    showTipDialog("请输入优惠券编码");
                } else {
                    bindingCoupon(discount_id);
                }
                break;
            case R.id.ivQrCode://二维码
                if (!DataUtils.permissCamera(this)) {
                    ToastUtil.show(mContext, Config.PERMISSION_CAMERA);
                    return;
                }
                if (!DataUtils.permissStorage(this)) {
                    ToastUtil.show(mContext, "请允许访问文件");
                    return;
                }
                startActivityForResult(new Intent(mContext, CaptureActivity.class), REQUEST_SCAN_QR_CODE);
                break;
        }
    }

    private void showTipDialog(String msg) {
        final CustomAlertDialog alertDialog = CustomAlertDialog.getAlertDialog(mContext, true, true);
        alertDialog.setMessage(msg)
                .setBtnConfirmColor(R.color.new_primary)
                .setOnPositiveClickListener("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                }).show();
    }

    /**
     * 绑定优惠券
     */
    private void bindingCoupon(String str) {
        if (str != null && !str.trim().equals("")) {
            CouponBindRequest data = new CouponBindRequest();
            data.setCustomerId(UserUtils.getCustomerId());
            data.setCouponCode(str);
            data.setMethod(RequestUrls.BIND_USER_AND_COUPON);
            doGet(data, QUERY_BIND_COUPON, Config.LOADING_STRING, true);
        }
    }

    /**
     * 扫码绑定优惠券
     */
    private void scanQrCodeGetCoupon(String batchNo) {
        ScanQrCodeCouponRequest request = new ScanQrCodeCouponRequest(batchNo, UserUtils.getCustomerId());
        request.setMethod(RequestUrls.SCAN_QRCODE_GET_COUPON);
        doGet(request, QUERY_QRCODE_COUPON, Config.LOADING_STRING, true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (null == data) {
            return;
        }
        if (requestCode == REQUEST_SCAN_QR_CODE) {
            if (resultCode == RESULT_OK) {
                String text = data.getStringExtra("qrResult");
                scanQrCodeGetCoupon(text);
            }
        }
    }
}
