package www.qisu666.com.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import www.qisu666.com.R;
import www.qisu666.com.app.CouponType;
import www.qisu666.com.callback.RedPacketResp;
import www.qisu666.com.utils.StringUtils;
import www.qisu666.com.utils.TVUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 红包
 */
public class MyRedPacketPagerAdapter extends PagerAdapter {

    private List<RedPacketResp> redPacketResps = new ArrayList<>();

    public MyRedPacketPagerAdapter(List<RedPacketResp> redPacketResps) {
        this.redPacketResps = redPacketResps;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        final Context context = container.getContext();
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.pager_send_red_packet, container, false);
        TextView tvMoneyFlag = (TextView) itemView.findViewById(R.id.tvMoneyFlag);
        TextView tvRedPacketMoney = (TextView) itemView.findViewById(R.id.tvRedPacketMoney);
        TextView tvDiscountFlag = (TextView) itemView.findViewById(R.id.tvDiscountFlag);
        TextView tvRedPacketDesc = (TextView) itemView.findViewById(R.id.tvRedPacketDesc);
        if (redPacketResps != null) {
            RedPacketResp redPacketResp = redPacketResps.get(position);
            String couponMoney = redPacketResp.getCouponMoney();
            if (!StringUtils.isEmpty(couponMoney)) {
                if (CouponType.RED_PK_COUPON.equals(redPacketResp.getType())) {
                    //红包车优惠券
                    tvRedPacketMoney.setText((Integer.parseInt(couponMoney) / 100) + "");
                    tvMoneyFlag.setVisibility(View.VISIBLE);
                    tvDiscountFlag.setVisibility(View.GONE);
                } else if (CouponType.DISCOUNT_COUPON.equals(redPacketResp.getType())) {
                    //折扣券
                    tvRedPacketMoney.setText(TVUtils.toString1(Integer.parseInt(couponMoney) / 10.0F));
                    tvMoneyFlag.setVisibility(View.GONE);
                    tvDiscountFlag.setVisibility(View.VISIBLE);
                }
            }
            String msg = redPacketResp.getMsg();
            msg = msg.replaceAll("，", "\n");
            tvRedPacketDesc.setText(msg);
        }

        container.addView(itemView);

        return itemView;
    }


    @Override
    public int getCount() {
        return redPacketResps.size();
    }


    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

}
