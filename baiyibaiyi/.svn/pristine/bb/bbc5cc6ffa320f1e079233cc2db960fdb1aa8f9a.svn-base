package www.qisu666.com.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import www.qisu666.com.R;
import www.qisu666.com.activity.WebActivity;
import www.qisu666.com.callback.SplashAndActivityResp;
import www.qisu666.com.utils.StringUtils;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

/**
 * 活动广告
 */
public class MyAdPagerAdapter extends PagerAdapter {

    private List<SplashAndActivityResp> splashAndActivityResps = new ArrayList<>();

    public MyAdPagerAdapter(List<SplashAndActivityResp> splashAndActivityResps) {
        this.splashAndActivityResps = splashAndActivityResps;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        final Context context = container.getContext();
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.ppw_ad, container, false);
        final ImageView ivAd = itemView.findViewById(R.id.ivAd);
        final SplashAndActivityResp item = splashAndActivityResps.get(position);
        if (null != item) {
            String imgUrl = item.getImgUrl();
            if (!StringUtils.isEmpty(imgUrl)) {
                Glide.with(context).load(imgUrl).asBitmap().into(ivAd);

                ivAd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String adUrl = item.getUrl();
                        if (!StringUtils.isEmpty(adUrl)) {
                            Intent intent = new Intent(context, WebActivity.class);
                            intent.putExtra("url", adUrl);
                            context.startActivity(intent);
                        }
                    }
                });
            }
        }

        container.addView(itemView);
        return itemView;
    }


    @Override
    public int getCount() {
        return splashAndActivityResps.size();
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
