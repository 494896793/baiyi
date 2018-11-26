package me.iwf.photopicker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import me.iwf.photopicker.fragment.ImagePagerFragment;
import me.iwf.photopicker.utils.PopupWindowWrap;

import static me.iwf.photopicker.PhotoPicker.KEY_SELECTED_PHOTOS;
import static me.iwf.photopicker.PhotoPreview.EXTRA_CURRENT_ITEM;
import static me.iwf.photopicker.PhotoPreview.EXTRA_PHOTOS;
import static me.iwf.photopicker.PhotoPreview.EXTRA_SHOW_DELETE;

/**
 * Created by donglua on 15/6/24.
 * 滑动查看图片
 */
public class PhotoPagerActivity extends AppCompatActivity {

    private ImagePagerFragment pagerFragment;

    //  private ActionBar actionBar;
    private boolean showDelete;
    private TextView tvTitleName;
    private ImageView ivTitleRight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.__picker_activity_photo_pager);

        int currentItem = getIntent().getIntExtra(EXTRA_CURRENT_ITEM, 0);
        //选中的图片路径
        List<String> paths = getIntent().getStringArrayListExtra(EXTRA_PHOTOS);
        showDelete = getIntent().getBooleanExtra(EXTRA_SHOW_DELETE, true);

        if (pagerFragment == null) {
            pagerFragment =
                    (ImagePagerFragment) getSupportFragmentManager().findFragmentById(R.id.photoPagerFragment);
        }
        pagerFragment.setPhotos(paths, currentItem);

//    Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
//    setSupportActionBar(mToolbar);
//
//    actionBar = getSupportActionBar();
//
//    if (actionBar != null) {
//      actionBar.setDisplayHomeAsUpEnabled(true);
//      updateActionBarTitle();
//      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//        actionBar.setElevation(25);
//      }
//    }
        findViewById(R.id.ivTitleLeft).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        tvTitleName = (TextView) findViewById(R.id.tvTitleName);

        findViewById(R.id.tvTitleRight).setVisibility(View.GONE);
        ivTitleRight = (ImageView) findViewById(R.id.ivTitleRight);
        ivTitleRight.setVisibility(showDelete ? View.VISIBLE : View.GONE);
        ivTitleRight.setImageResource(R.drawable.__picker_delete);
        //点击删除
        findViewById(R.id.llytTitleRight).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickDelete();
            }
        });

        //更新标题
        updateTitle();

        pagerFragment.getViewPager().addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                updateTitle();
            }
        });
    }


    @Override
    public void onBackPressed() {

        Intent intent = new Intent();
        intent.putExtra(KEY_SELECTED_PHOTOS, pagerFragment.getPaths());
        setResult(RESULT_OK, intent);
        finish();

        super.onBackPressed();
    }


//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        if (item.getItemId() == android.R.id.home) {
//            onBackPressed();
//            return true;
//        }
//
//        if (item.getItemId() == R.id.delete) {
//            final int index = pagerFragment.getCurrentItem();
//
//            final String deletedPath = pagerFragment.getPaths().get(index);
//
//            Snackbar snackbar = Snackbar.make(pagerFragment.getView(), R.string.__picker_deleted_a_photo,
//                    Snackbar.LENGTH_LONG);
//
//            if (pagerFragment.getPaths().size() <= 1) {
//
//                // show confirm dialog
//                new AlertDialog.Builder(this)
//                        .setTitle(R.string.__picker_confirm_to_delete)
//                        .setPositiveButton(R.string.__picker_yes, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                dialogInterface.dismiss();
//                                pagerFragment.getPaths().remove(index);
//                                pagerFragment.getViewPager().getAdapter().notifyDataSetChanged();
//                                onBackPressed();
//                            }
//                        })
//                        .setNegativeButton(R.string.__picker_cancel, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                dialogInterface.dismiss();
//                            }
//                        })
//                        .show();
//
//            } else {
//
//                snackbar.show();
//
//                pagerFragment.getPaths().remove(index);
//                pagerFragment.getViewPager().getAdapter().notifyDataSetChanged();
//            }
//
//            snackbar.setAction(R.string.__picker_undo, new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    if (pagerFragment.getPaths().size() > 0) {
//                        pagerFragment.getPaths().add(index, deletedPath);
//                    } else {
//                        pagerFragment.getPaths().add(deletedPath);
//                    }
//                    pagerFragment.getViewPager().getAdapter().notifyDataSetChanged();
//                    pagerFragment.getViewPager().setCurrentItem(index, true);
//                }
//            });
//
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    /**
     * 更新标题
     */
    public void updateTitle() {
        if (tvTitleName == null) {
            return;
        }
        ArrayList<String> paths = pagerFragment.getPaths();

        int currentPos = pagerFragment.getViewPager().getCurrentItem();
        if (currentPos >= 0 && currentPos < paths.size()) {
//            if (paths.get(currentPos).startsWith("http")) {
//                ivTitleRight.setVisibility(View.GONE);
//            } else {
                ivTitleRight.setVisibility(showDelete ? View.VISIBLE : View.GONE);
//            }
        }

        tvTitleName.setText((currentPos + 1) + "/" +
                (paths.size()));
    }

    /**
     * 点击删除
     */
    public void clickDelete() {
        final int index = pagerFragment.getCurrentItem();

//        final String deletedPath = pagerFragment.getPaths().get(index);

//        Snackbar snackbar = Snackbar.make(pagerFragment.getView(), R.string.__picker_deleted_a_photo,
//                Snackbar.LENGTH_LONG);

//        if (pagerFragment.getPaths().size() <= 1) {
//
//            // show confirm dialog
//            new AlertDialog.Builder(this)
//                    .setTitle(R.string.__picker_confirm_to_delete)
//                    .setPositiveButton(R.string.__picker_yes, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                            dialogInterface.dismiss();
//                            pagerFragment.getPaths().remove(index);
//                            pagerFragment.getViewPager().getAdapter().notifyDataSetChanged();
//                            onBackPressed();
//                        }
//                    })
//                    .setNegativeButton(R.string.__picker_cancel, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                            dialogInterface.dismiss();
//                        }
//                    })
//                    .show();
//
//        } else {
//
//            snackbar.show();
//
//            pagerFragment.getPaths().remove(index);
//            pagerFragment.getViewPager().getAdapter().notifyDataSetChanged();
//        }

//        snackbar.setAction(R.string.__picker_undo, new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (pagerFragment.getPaths().size() > 0) {
//                    pagerFragment.getPaths().add(index, deletedPath);
//                } else {
//                    pagerFragment.getPaths().add(deletedPath);
//                }
//                pagerFragment.getViewPager().getAdapter().notifyDataSetChanged();
//                pagerFragment.getViewPager().setCurrentItem(index, true);
//            }
//        });

        final PopupWindowWrap popupWindowWrap = new PopupWindowWrap(this);
        popupWindowWrap
                .setContentView(R.layout.__picker_ppw_delete, new PopupWindowWrap.OnCreatedPPWListener() {
                    @Override
                    public void onCreatedPPW(View contentView) {
                        //删除
                        contentView.findViewById(R.id.llytDeletePhoto).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pagerFragment.getPaths().remove(index);
                                pagerFragment.getViewPager().getAdapter().notifyDataSetChanged();
                                popupWindowWrap.dismiss();
                                //只有一张，删除后关闭该页面
                                if (pagerFragment.getPaths().size() <= 0) {
                                    onBackPressed();
                                }
                            }
                        });
                        //取消
                        contentView.findViewById(R.id.llytCancel).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                popupWindowWrap.dismiss();
                            }
                        });
                    }
                })
                .setAnim(R.style.__picker_AnimBottom);
        popupWindowWrap.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                popupWindowWrap.dismiss();
            }
        });
        popupWindowWrap.showAtLocation(getWindow().getDecorView().findViewById(android.R.id.content), Gravity.BOTTOM, 0, 0);
    }
}
