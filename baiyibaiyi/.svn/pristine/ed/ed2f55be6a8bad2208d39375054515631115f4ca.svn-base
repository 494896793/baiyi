package www.qisu666.com.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import www.qisu666.com.R;
import www.qisu666.com.callback.SplashAndActivityResp;
import www.qisu666.com.constant.Config;
import www.qisu666.com.utils.ScreenUtils;
import www.qisu666.com.utils.SharedPreferencesUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 新手引导
 */
public class GuideActivity extends BaseActivity {
    private static final int[] mImageIds = new int[]{R.mipmap.guide_first, R.mipmap.guide_second,R.mipmap.guide_third};
    @BindView(R.id.vp_guide)
    ViewPager vpGuide;
    @BindView(R.id.btn_start)
    ImageView btnStart;
    @BindView(R.id.ll_point_group)
    LinearLayout llPointGroup;
    @BindView(R.id.view_red_point)
    View viewRedPoint;
    private ArrayList<ImageView> mImageViewList;
    private int mPointWidth;// 圆点间的距离

    private ArrayList<SplashAndActivityResp> splashAndActivityResps;

    @Override
    public void setView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题
        setContentView(R.layout.activity_guide);
    }

    @Override
    public void initDatas() {
        initViews();
        vpGuide.setAdapter(new GuideAdapter());
        vpGuide.addOnPageChangeListener(new GuidePageListener());
        splashAndActivityResps = getIntent().getParcelableArrayListExtra("splashAndActivityResps");
    }

    @Override
    public void onComplete(String result, int type) {
    }

    @Override
    public void onFailure(String msg, int type) {
    }

    @OnClick(R.id.btn_start)
    void start() {
        // 更新sp, 表示已经展示了新手引导
        SharedPreferencesUtils.putBoolean(application,
                Config.IS_SHOW_GUIDE, true);
        boolean aa=SharedPreferencesUtils.getBoolean(application,
                Config.IS_SHOW_GUIDE, false);
        PackageManager pm = application.getPackageManager();

        try {
            PackageInfo pi = pm.getPackageInfo(application.getPackageName(), 0);
            SharedPreferencesUtils.putInt(mContext, "oldVersion", pi.versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        // 跳转主页面
        Intent intent = new Intent(mContext, ControlerActivity.class);
        intent.putParcelableArrayListExtra("splashAndActivityResps", splashAndActivityResps);
        startActivity(intent);
        finish();
    }

    /**
     * 初始化界面
     */
    private void initViews() {
        mImageViewList = new ArrayList<ImageView>();

        // 初始化引导页的3个页面
        for (int i = 0; i < mImageIds.length; i++) {
            ImageView image = new ImageView(this);
            image.setBackgroundResource(mImageIds[i]);// 设置引导页背景
            mImageViewList.add(image);
        }

        // 初始化引导页的小圆点
        for (int i = 0; i < mImageIds.length; i++) {
            View point = new View(this);
            point.setBackgroundResource(R.drawable.shape_point_gray);// 设置引导页默认圆点

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ScreenUtils.dp2px(this, 10), ScreenUtils.dp2px(this, 10));
            if (i > 0) {
                params.leftMargin = ScreenUtils.dp2px(this, 10);// 设置圆点间隔
            }
            point.setLayoutParams(params);// 设置圆点的大小
            llPointGroup.addView(point);// 将圆点添加给线性布局
        }
        // 获取视图树, 对layout结束事件进行监听
        llPointGroup.getViewTreeObserver().addOnGlobalLayoutListener(
                new OnGlobalLayoutListener() {

                    // 当layout执行结束后回调此方法
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onGlobalLayout() {
                        if (llPointGroup != null && llPointGroup.getViewTreeObserver() != null && llPointGroup.getChildCount() > 1) {
                            llPointGroup.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            mPointWidth = llPointGroup.getChildAt(1).getLeft()
                                    - llPointGroup.getChildAt(0).getLeft();
                        }
                    }
                });
        if (mImageIds.length == 1) {
            btnStart.setVisibility(View.VISIBLE);
        } else {
            btnStart.setVisibility(View.GONE);
        }
    }

    /**
     * ViewPager数据适配器
     *
     * @author Kevin
     */
    class GuideAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mImageIds.length;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mImageViewList.get(position));
            return mImageViewList.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    /**
     * viewpager的滑动监听
     *
     * @author Kevin
     */
    class GuidePageListener implements OnPageChangeListener {

        // 滑动事件
        @Override
        public void onPageScrolled(int position, float positionOffset,
                                   int positionOffsetPixels) {
            // System.out.println("当前位置:" + position + ";百分比:" + positionOffset
            // + ";移动距离:" + positionOffsetPixels);
            int len = (int) (mPointWidth * positionOffset) + position
                    * mPointWidth;
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) viewRedPoint
                    .getLayoutParams();// 获取当前红点的布局参数
            params.leftMargin = len;// 设置左边距
            viewRedPoint.setLayoutParams(params);// 重新给小红点设置布局参数
        }

        // 某个页面被选中
        @Override
        public void onPageSelected(int position) {
            if (position == mImageIds.length - 1) {// 最后一个页面
                btnStart.setVisibility(View.VISIBLE);// 显示开始体验的按钮
                llPointGroup.setVisibility(View.GONE);
                viewRedPoint.setVisibility(View.GONE);
            } else {
                btnStart.setVisibility(View.INVISIBLE);
                llPointGroup.setVisibility(View.GONE);
                viewRedPoint.setVisibility(View.GONE);

            }
        }

        // 滑动状态发生变化
        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            exitApplication();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}
