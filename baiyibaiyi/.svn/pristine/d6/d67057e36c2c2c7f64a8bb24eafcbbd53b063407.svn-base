package www.qisu666.com.view;

import android.app.Activity;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import www.qisu666.com.R;
import www.qisu666.com.adapter.MyRedPacketRechargePagerAdapter;
import www.qisu666.com.callback.PreRechargeResp;

import java.util.List;

/**
 * Created by wujiancheng on 2017/11/23.
 * 充值赠送红包的popupWindow
 */

public class RedPacketRechargePPW {
    public void showRedPacketPPW(final Activity activity, final List<PreRechargeResp.CustomerRechargeDetail> resp) {
        if (resp == null || resp.size() == 0) {
            return;
        }
        final PopupWindowWrap popupWindowWrap = new PopupWindowWrap(activity);
        popupWindowWrap
                .setContentView(R.layout.ppw_send_red_packet, new PopupWindowWrap.OnCreatedPPWListener() {
                    @Override
                    public void onCreatedPPW(View contentView) {
                        ViewPager viewPager = (ViewPager) contentView.findViewById(R.id.viewPagerSendRedPacket);
                        MyRedPacketRechargePagerAdapter adapter = new MyRedPacketRechargePagerAdapter(resp);
                        viewPager.setAdapter(adapter);

                        adapter.setOnDeleteClickListener(new MyRedPacketRechargePagerAdapter.OnDeleteClickListener() {
                            @Override
                            public void onDeleteClick() {
                                popupWindowWrap.dismiss();
                            }
                        });

                        if (resp.size() > 1) {//多于一个，底部有圆点
                            final LinearLayout llytPointContainer = (LinearLayout) contentView.findViewById(R.id.llytPointContainer);
                            LayoutInflater layoutInflater = activity.getLayoutInflater();
                            for (int i = 0; i < resp.size(); i++) {
                                View view = layoutInflater.inflate(R.layout.layout_point, llytPointContainer, false);
                                if (i == 0) {///第一个选中，白色
                                    View viewPoint = view.findViewById(R.id.viewPoint);
                                    viewPoint.setBackgroundResource(R.drawable.point_white);
                                }
                                llytPointContainer.addView(view);
                            }
                            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                                @Override
                                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                                }

                                @Override
                                public void onPageSelected(int position) {
                                    int pointCount = llytPointContainer.getChildCount();
                                    for (int i = 0; i < pointCount; i++) {
                                        View view = llytPointContainer.getChildAt(i);
                                        View viewPoint = view.findViewById(R.id.viewPoint);
                                        if (i == position) {
                                            viewPoint.setBackgroundResource(R.drawable.point_white);
                                        } else {
                                            viewPoint.setBackgroundResource(R.drawable.point_gray);
                                        }
                                    }
                                }

                                @Override
                                public void onPageScrollStateChanged(int state) {

                                }
                            });
                        }
                    }
                })
                .setWidth(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setAnim(R.style.AnimBottom);
        popupWindowWrap.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                popupWindowWrap.dismiss();
            }
        });
        popupWindowWrap.showAtLocation(activity.getWindow().getDecorView().findViewById(android.R.id.content), Gravity.CENTER, 0, 0);
    }
}
