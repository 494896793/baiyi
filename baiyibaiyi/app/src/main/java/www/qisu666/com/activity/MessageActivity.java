package www.qisu666.com.activity;

import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;

import www.qisu666.com.R;
import www.qisu666.com.fragment.ImgTextMessageFragment;
import www.qisu666.com.fragment.SystemMessageFragment;
import www.qisu666.com.view.MessageViewPagerWithTitleView;
import www.qisu666.com.view.ViewPagerWithTitleView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class MessageActivity extends BaseActivity {

    @BindView(R.id.tvTitleName)
    TextView tvTitleName;
    @BindView(R.id.viewPagerWithTitleView)
    MessageViewPagerWithTitleView viewPagerWithTitleView;

    private boolean isFirst = true;

    @Override
    public void setView() {
        setContentView(R.layout.activity_message);
    }

    @Override
    public void initDatas() {
        tvTitleName.setText("消息中心");

        //未读消息红点的状态
        final List<Integer> redPointStatus = new ArrayList<>();

        List<String> tabNames = new ArrayList<>();
        tabNames.add("热门活动");
        tabNames.add("系统消息");

        List<Fragment> fragments = new ArrayList<>();
        ImgTextMessageFragment imgTextMessageFragment = new ImgTextMessageFragment();
        fragments.add(imgTextMessageFragment);
        //没有未读消息
        redPointStatus.add(0);

        final SystemMessageFragment systemMessageFragment = new SystemMessageFragment();
        fragments.add(systemMessageFragment);
        //可能有未读消息
        int newMessageNum = getIntent().getIntExtra("newMessageNum", 0);
        redPointStatus.add(newMessageNum);

        viewPagerWithTitleView.setData(this, tabNames, fragments);
        viewPagerWithTitleView.setOnPageSelectedListener(new MessageViewPagerWithTitleView.OnPageSelectedListener() {
            @Override
            public void onPageSelected(int position) {
                //第一次滑动到系统消息的Fragment页面，请求网络数据
                if (isFirst && position == 1) {
                    isFirst = false;
                    systemMessageFragment.querySystemMessage(1);
                    //将系统消息的红点设置为消失，已读
                    redPointStatus.set(1, 0);
                    viewPagerWithTitleView.updateTitleRedPoint(redPointStatus);
                }
            }
        });
        viewPagerWithTitleView.updateTitleRedPoint(redPointStatus);

    }

    @Override
    public void onComplete(String result, int type) {

    }

    @Override
    public void onFailure(String msg, int type) {

    }

    @OnClick({R.id.ivTitleLeft})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ivTitleLeft:
                finish();
                break;
        }
    }
}