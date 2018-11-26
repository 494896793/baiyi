package me.iwf.photopicker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import me.iwf.photopicker.fragment.ImagePagerFragment;

import static me.iwf.photopicker.PhotoPicker.KEY_SELECTED_PHOTOS;
import static me.iwf.photopicker.PhotoPreview.EXTRA_ALL_PHOTOS;
import static me.iwf.photopicker.PhotoPreview.EXTRA_CURRENT_ITEM;
import static me.iwf.photopicker.PhotoPreview.EXTRA_PHOTOS;
import static me.iwf.photopicker.PhotoPreview.EXTRA_SHOW_DELETE;
import static me.iwf.photopicker.R.id.ivTitleRight;

/**
 * Created by donglua on 15/6/24.
 * 选择图片页面的滑动查看图片
 */
public class PhotoPickerPagerActivity extends AppCompatActivity {
    private static final String TAG = PhotoPickerPagerActivity.class.getSimpleName();
    private ImagePagerFragment pagerFragment;

    //  private ActionBar actionBar;
    private boolean showDelete;
    private TextView tvTitleName;
    private TextView tvTitleRight;
    private ImageView ivChecked;

    private ArrayList<String> paths = new ArrayList<>();
    private int currentItem;
    private boolean isClickComplete = false;//是否点击“完成”按钮

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.__picker_activity_photo_picker_pager);

        currentItem = getIntent().getIntExtra(EXTRA_CURRENT_ITEM, 0);//当前点击位置
        paths = getIntent().getStringArrayListExtra(EXTRA_PHOTOS);//已选中的图片路径
        final List<String> allPaths = getIntent().getStringArrayListExtra(EXTRA_ALL_PHOTOS);//所有图片路径
        showDelete = getIntent().getBooleanExtra(EXTRA_SHOW_DELETE, true);

        if (pagerFragment == null) {
            pagerFragment =
                    (ImagePagerFragment) getSupportFragmentManager().findFragmentById(R.id.photoPagerFragment);
        }
        pagerFragment.setPhotos(allPaths, currentItem);

        findViewById(R.id.ivTitleLeft).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        ivChecked = (ImageView) findViewById(R.id.ivChecked);
        tvTitleName = (TextView) findViewById(R.id.tvTitleName);
        findViewById(ivTitleRight).setVisibility(View.GONE);
        tvTitleRight = (TextView) findViewById(R.id.tvTitleRight);
        tvTitleRight.setBackgroundResource(R.drawable.__picker_bg_blue_radius_enable);
        //已选中的图片数目
        changeCheckedNum();
        //点击完成
        tvTitleRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentItem >= 0 && currentItem < allPaths.size()) {
                    String path = allPaths.get(currentItem);
                    //如果没选中的图片，就选中
                    if (paths.size() == 0) {
                        paths.add(path);
                    }
                    isClickComplete = true;
                    onBackPressed();
                }
            }
        });

        changeCheckStatus(allPaths);
        //图片滑动
        pagerFragment.getViewPager().addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentItem = position;
                if (currentItem >= 0 && currentItem < allPaths.size()) {
                    changeCheckStatus(allPaths);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        //点击选择按钮
        ivChecked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path = allPaths.get(currentItem);
                if (paths.contains(path)) {
                    ivChecked.setImageResource(R.drawable.__picker_pic_unchecked);
                    paths.remove(path);
                    Log.i(TAG, "移除");
                } else {
                    List<String> tmp = new ArrayList<String>();
                    for (String str : paths) {
                        if (!str.startsWith("http")) {
                            tmp.add(str);
                        }
                    }
                    if (tmp.size() < 20) {
                        ivChecked.setImageResource(R.drawable.__picker_pic_checked);
                        paths.add(path);
                        Log.i(TAG, "选中");
                    } else {
                        Toast.makeText(PhotoPickerPagerActivity.this, "选择的图片不能超过20张", Toast.LENGTH_SHORT).show();
                    }
                }
                Log.i(TAG, "paths = " + paths);
                changeCheckedNum();
            }
        });
    }


    @Override
    public void onBackPressed() {

        Intent intent = new Intent();
        intent.putExtra(KEY_SELECTED_PHOTOS, paths);
        Log.i(TAG, "onBackPressed paths=" + paths);
        intent.putExtra("isClickComplete", isClickComplete);
        setResult(RESULT_OK, intent);
        finish();

        super.onBackPressed();
    }

    /**
     * 更改图片选择状态
     */
    private void changeCheckStatus(List<String> allPaths) {
        String path = allPaths.get(currentItem);
        if (paths.contains(path)) {
            ivChecked.setImageResource(R.drawable.__picker_pic_checked);
        } else {
            ivChecked.setImageResource(R.drawable.__picker_pic_unchecked);
        }
    }

    /**
     * 更改选择的数量
     */
    private void changeCheckedNum() {
        List<String> tmp = new ArrayList<String>();
        for (String str : paths) {
            if (!str.startsWith("http")) {
                tmp.add(str);
            }
        }
        if (tmp.size() > 0) {
            tvTitleRight.setText("完成(" + tmp.size() + ")");
        } else {
            tvTitleRight.setText("完成");
        }
    }
}
