package www.qisu666.com.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import www.qisu666.com.R;
import www.qisu666.com.event.AllorederCloseEvent;
import www.qisu666.com.event.MapVisibleEvent;
import www.qisu666.com.fragment.AAAFragment;
import www.qisu666.com.fragment.Fragment_ChongDian;
import www.qisu666.com.utils.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 717219917@qq.com 2018/10/15 19:18.
 */
public class AllOrderActivity extends BaseActivity {

    private FragmentManager manager=getSupportFragmentManager();
    private OrderListActivity orderFragment;
    private Fragment_ChongDian chongDianFragment;
    private List<Fragment> fragmentList=new ArrayList<>();
    private int toMain = 0;//1:后台还车后跳转到订单列表后，按返回键跳到主页
    @BindView(R.id.car_re)
    RelativeLayout car_re;
    @BindView(R.id.elect_re)
    RelativeLayout elect_re;
    @BindView(R.id.ivTitleLeft)
    ImageView ivTitleLeft;
    @BindView(R.id.elect_view_line)
    View elect_view_line;
    @BindView(R.id.car_view_line)
    View car_view_line;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void setView() {
        setContentView(R.layout.activity_allorder_layout);
    }

    @Override
    public void initDatas() {
        toMain = getIntent().getIntExtra("toMain", 0);
        initFragment();
        selectFragment(0);
    }

    @Subscribe
    public void onEvent(AllorederCloseEvent event){
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @OnClick({R.id.elect_re,R.id.car_re,R.id.ivTitleLeft})
    public void onViewClick(View view) {
        switch (view.getId()){
            case R.id.elect_re:
                selectFragment(1);
                chooseTitleBar(R.id.elect_re);
                break;
            case R.id.car_re:
                selectFragment(0);
                chooseTitleBar(R.id.car_re);
                break;
            case R.id.ivTitleLeft:
                finish();
                break;
        }
    }

    private void chooseTitleBar(int id){
        switch (id){
            case R.id.car_re:
                car_view_line.setVisibility(View.VISIBLE);
                elect_view_line.setVisibility(View.GONE);
                break;
            case R.id.elect_re:
                elect_view_line.setVisibility(View.VISIBLE);
                car_view_line.setVisibility(View.GONE);
                break;
        }
    }

    private void initFragment(){
        orderFragment=new OrderListActivity();
        Bundle bundle=new Bundle();
        bundle.putInt("toMain",toMain);
        orderFragment.setArguments(bundle);
        fragmentList.add(orderFragment);

        chongDianFragment=new Fragment_ChongDian();
        fragmentList.add(chongDianFragment);

        FragmentTransaction transaction=manager.beginTransaction();
        for(int i=0;i<fragmentList.size();i++){
            transaction.add(R.id.framelayout,fragmentList.get(i));
        }
        transaction.commitAllowingStateLoss();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Logger.i("", "keyCode:" + keyCode);
        if ((keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)) {
            back();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }


    private void selectFragment(int position){
        hideAllFragment();
        FragmentTransaction transaction=manager.beginTransaction();
        if(fragmentList.get(position)!=null){
            switch (position){
                case 0:
                    transaction.show(orderFragment);
                    break;
                case 1:
                    transaction.show(chongDianFragment);
                    break;
            }
        }
        transaction.commitAllowingStateLoss();
    }


    private void hideAllFragment(){
        FragmentTransaction transaction=manager.beginTransaction();
        transaction.hide(chongDianFragment);
        transaction.hide(orderFragment);
        transaction.commitAllowingStateLoss();
    }


    public void back() {
        if (toMain == 1) {
            activityUtil.jumpTo(ControlerActivity.class);
        }
        finish();
    }

    @Override
    public void onComplete(String result, int type) {

    }

    @Override
    public void onFailure(String msg, int type) {

    }
}
