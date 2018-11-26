package www.qisu666.com.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;

import www.qisu666.com.R;
import www.qisu666.com.app.CouponType;
import www.qisu666.com.callback.CouponListChooseResp;
import www.qisu666.com.fragment.MyCouponFragment;
import www.qisu666.com.rx.RxBus;
import www.qisu666.com.rx.RxBusEvent;
import www.qisu666.com.rx.RxEventCodes;
import www.qisu666.com.utils.Logger;
import www.qisu666.com.view.ViewPagerWithTitleView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import rx.functions.Action1;

public class CouponChoiceActivity extends BaseActivity {

    @BindView(R.id.tvTitleName)
    TextView tvTitleName;
    @BindView(R.id.viewPagerWithTitleView)
    ViewPagerWithTitleView viewPagerWithTitleView;
    private CouponListChooseResp couponData;
    private String orderId = "";

    @Override
    public void setView() {
        setContentView(R.layout.activity_coupon_choice);
    }

    @Override
    public void initDatas() {
        tvTitleName.setText("选择优惠券");

        couponData = (CouponListChooseResp) getIntent().getSerializableExtra("couponData");
        orderId = getIntent().getStringExtra("orderId");
        observeRxEventCode();

        //标题
        List<String> titles = new ArrayList<String>() {{
            add("可用优惠券");
            add("不可用优惠券");
        }};
        //内容
        List<Fragment> fragments = new ArrayList<>();
        //可使用
        Fragment fragmentCanUseChoose = new MyCouponFragment();
        Bundle bundleCanUseChoose = new Bundle();
        bundleCanUseChoose.putString("type", CouponType.CAN_USE_CHOOSE);
        bundleCanUseChoose.putSerializable("couponData", couponData);
        bundleCanUseChoose.putString("orderId", orderId);
        fragmentCanUseChoose.setArguments(bundleCanUseChoose);
        //不可用优惠券
        Fragment fragmentCannotUseChoose = new MyCouponFragment();
        Bundle bundleCannotUseChoose = new Bundle();
        bundleCannotUseChoose.putString("type", CouponType.CANNOT_USE_CHOOSE);
        bundleCannotUseChoose.putSerializable("couponData", couponData);
        bundleCannotUseChoose.putString("orderId", orderId);
        fragmentCannotUseChoose.setArguments(bundleCannotUseChoose);

        fragments.add(fragmentCanUseChoose);
        fragments.add(fragmentCannotUseChoose);

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
                                final Map<String, String> map = (Map<String, String>) rxBusEvent.getContent();
                                List<String> titles = new ArrayList<String>() {{
                                    String canUseChooseNumber = map.get(CouponType.CAN_USE_CHOOSE);
                                    String cannotUseChooseNumber = map.get(CouponType.CANNOT_USE_CHOOSE);
                                    Logger.i("canUseChooseNumber = " + canUseChooseNumber + ",cannotUseChooseNumber=" + cannotUseChooseNumber);
                                    add("可用优惠券(" + canUseChooseNumber + ")");
                                    add("不可用优惠券(" + cannotUseChooseNumber + ")");
                                }};
                                viewPagerWithTitleView.updateTitle(titles);
                                break;
                        }
                    }
                });
    }

    @Override
    public void onComplete(String result, int type) {

    }

    @Override
    public void onFailure(String msg, int type) {

    }

    @OnClick({R.id.ivTitleLeft})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.ivTitleLeft:
                finish();
                break;
        }
    }
}
