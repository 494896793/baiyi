package www.qisu666.com.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import www.qisu666.com.BuildConfig;
import www.qisu666.com.R;
import www.qisu666.com.callback.SecData;
import www.qisu666.com.callback.UserInfoResp;
import www.qisu666.com.constant.Config;
import www.qisu666.com.constant.RequestUrls;
import www.qisu666.com.request.VerifyInfoRequest;
import www.qisu666.com.utils.CacheUtils;
import www.qisu666.com.utils.DataUtils;
import www.qisu666.com.utils.FileSizeUtil;
import www.qisu666.com.utils.FileUtil2;
import www.qisu666.com.utils.Logger;
import www.qisu666.com.utils.ToastUtil;
import www.qisu666.com.utils.UploadFile;
import www.qisu666.com.utils.UserUtils;
import www.qisu666.com.view.CustomAlertDialog;
import www.qisu666.com.view.CustomAlertDialogVerify;
import www.qisu666.com.view.ViewShowUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 上传证件
 */
public class VerifyIDCardActivity extends BaseActivity {
    public static final int NONE = 0;
    public static final int PHOTOHRAPH = 1;// 拍照
    public static final int PHOTORESOULT = 3;// 结果
    @BindView(R.id.tvTitleName)
    TextView tvTitleName;
    @BindView(R.id.ivDriveCard)
    ImageView ivDriveCard;
    @BindView(R.id.ivIdCard)
    ImageView ivIdCard;
    @BindView(R.id.ivPersonWithCard)
    ImageView ivPersonWithCard;
    @BindView(R.id.tvSubmit)
    TextView tvSubmit;

    private File driveCardFile;
    private File idCardFile;
    private File personWithCardFile;
    private ArrayList<File> photoFiles = new ArrayList<>();
    private int photoId = 0;//0：驾驶证，1：身份证，2：手持身份证
    //更改提交按钮背景
    //是否选择驾照
    private boolean hasDriverCard = false;
    //是否选择身份证
    private boolean hasIdCard = false;
    //是否选择手持身份证
    private boolean hasPersonWitdhCard = false;

    private int driverNumberStatus;//驾照认证状态 1:驳回，2:过期

    @Override
    public void setView() {
        setContentView(R.layout.activity_verify_id_card);
    }

    @Override
    public void initDatas() {
        tvSubmit.getBackground().setAlpha(200);
        tvTitleName.setText("身份认证");
        driverNumberStatus = getIntent().getIntExtra("driverNumberStatus", 0);
        if (UserUtils.isNeedUpdateDriverCard(driverNumberStatus)) {//驾照认证状态 1:驳回，2，3:过期
            UserInfoResp mUser = CacheUtils.getIn().getUserInfo();
            if (null != mUser) {
                //加载之前上传的身份证
                Glide.with(this).load(mUser.getIdcardUrl()).asBitmap().into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        if (null != resource) {
                            ivIdCard.setImageBitmap(resource);
                            idCardFile = FileUtil2.saveBitmap("/idCardOld.jpg", File.separator + FileUtil2.IMG_VERIFY_DIR, resource);
                            hasIdCard = true;
                        }
                    }
                });
                Glide.with(this).load(mUser.getHandCardUrl()).asBitmap().into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        if (null != resource) {
                            ivPersonWithCard.setImageBitmap(resource);
                            personWithCardFile = FileUtil2.saveBitmap("/personWithCardOld.jpg", File.separator + FileUtil2.IMG_VERIFY_DIR, resource);
                            hasPersonWitdhCard = true;
                        }
                    }
                });
            }
        }
    }

    @Override
    public void onComplete(String result, int type) {
    }

    @Override
    public void onFailure(String msg, int type) {
    }

    /**
     * 选择照相机
     */
    protected void chooseCamera() {
        if (!DataUtils.isCameraCanUse()) {
            ToastUtil.show(mContext, Config.PERMISSION_CAMERA);
            DataUtils.permissCamera(this);
            return;
        }
        if (!DataUtils.permissCamera(this)) {
            ToastUtil.show(mContext, Config.PERMISSION_CAMERA);
            return;
        }
        if (!DataUtils.permissStorage(this)) {
            ToastUtil.show(mContext, Config.PERMISSION_STORAGE);
            return;
        }
        switch (photoId) {
            case 0:
                driveCardFile = FileUtil2.newFileInstance(FileUtil2.IMG_VERIFY_DIR);
                startCamera(driveCardFile);
                break;
            case 1:
                idCardFile = FileUtil2.newFileInstance(FileUtil2.IMG_VERIFY_DIR);
                startCamera(idCardFile);
                break;
            case 2:
                personWithCardFile = FileUtil2.newFileInstance(FileUtil2.IMG_VERIFY_DIR);
                startCamera(personWithCardFile);
                break;
        }
    }

    /**
     * 启动相机
     *
     * @param file 拍完照的图片
     */
    private void startCamera(File file) {
        Intent cIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //判断是否是AndroidN以及更高的版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            cIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(mContext, BuildConfig.APPLICATION_ID + ".fileProvider", file);
            cIntent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
        } else {
            cIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        }
        startActivityForResult(cIntent, PHOTOHRAPH);
    }

    /**
     * 选择本地相册
     */
    protected void chooseAlbum() {
        if (!DataUtils.permissStorage(this)) {
            ToastUtil.show(mContext, Config.PERMISSION_STORAGE);
            return;
        }
        switch (photoId) {
            case 0:
                driveCardFile = FileUtil2.newFileInstance(FileUtil2.IMG_VERIFY_DIR);
                break;
            case 1:
                idCardFile = FileUtil2.newFileInstance(FileUtil2.IMG_VERIFY_DIR);
                break;
            case 2:
                personWithCardFile = FileUtil2.newFileInstance(FileUtil2.IMG_VERIFY_DIR);
                break;
        }
        Intent gIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(gIntent, PHOTORESOULT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == NONE) {
            return;
        }
        Bitmap mBitmapPhotoData = null;
        // 拍照
        if (requestCode == PHOTOHRAPH) {
            if (photoId == 0) {
                if (driveCardFile != null && driveCardFile.exists()) {
                    mBitmapPhotoData = getPhoto();
                } else {
                    return;
                }
            } else if (photoId == 1) {
                if (idCardFile != null && idCardFile.exists()) {
                    mBitmapPhotoData = getPhoto();
                } else {
                    return;
                }
            } else if (photoId == 2) {
                if (personWithCardFile != null && personWithCardFile.exists()) {
                    mBitmapPhotoData = getPhoto();
                } else {
                    return;
                }
            }
        } else {
            //相册
            if (data == null) {
                return;
            }
            Uri uri = data.getData();
            if (uri == null) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    mBitmapPhotoData = (Bitmap) extras.get("data");
                }
            } else {
                try {
                    mBitmapPhotoData = ViewShowUtil.getThumbnail(mContext, uri, 720);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        OutputStream out;
        try {
            if (photoId == 0) {
                out = new FileOutputStream(driveCardFile);
            } else if (photoId == 1) {
                out = new FileOutputStream(idCardFile);
            } else {
                out = new FileOutputStream(personWithCardFile);
            }
            mBitmapPhotoData.compress(Bitmap.CompressFormat.PNG, 60,
                    out);// (0-100)压缩文件
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (photoId == 0) {
            ivDriveCard.setBackgroundResource(R.mipmap.upload_bg);
            ivDriveCard.setImageBitmap(mBitmapPhotoData);
            hasDriverCard = true;
        } else if (photoId == 1) {
            ivIdCard.setBackgroundResource(R.mipmap.upload_bg);
            ivIdCard.setImageBitmap(mBitmapPhotoData);
            hasIdCard = true;
        } else if (photoId == 2) {
            ivPersonWithCard.setBackgroundResource(R.mipmap.upload_bg);
            ivPersonWithCard.setImageBitmap(mBitmapPhotoData);
            hasPersonWitdhCard = true;
        }
        if (hasDriverCard && hasIdCard && hasPersonWitdhCard) {
            tvSubmit.getBackground().setAlpha(255);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private Bitmap getPhoto() {
        Bitmap mBitmapPhotoData = null;
        try {
            if (photoId == 0) {
                Uri uri = FileUtil2.getFileUri(mContext, driveCardFile);
                mBitmapPhotoData = ViewShowUtil.getThumbnail(mContext, uri, 720);
            } else if (photoId == 1) {
                Uri uri = FileUtil2.getFileUri(mContext, idCardFile);
                mBitmapPhotoData = ViewShowUtil.getThumbnail(mContext, uri, 720);
            } else if (photoId == 2) {
                Uri uri = FileUtil2.getFileUri(mContext, personWithCardFile);
                mBitmapPhotoData = ViewShowUtil.getThumbnail(mContext, uri, 720);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mBitmapPhotoData;
    }

    @OnClick({R.id.ivTitleLeft, R.id.ivDriveCard, R.id.ivIdCard, R.id.ivPersonWithCard, R.id.tvSubmit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ivTitleLeft:
//                activityUtil.jumpBackTo(PersonCenterActivity.class);
                finish();
                break;
            case R.id.ivDriveCard://上传驾驶证
                photoId = 0;
                choosePic();
                break;
            case R.id.ivIdCard://上传身份证
                if (UserUtils.isNeedUpdateDriverCard(driverNumberStatus) && hasIdCard) {
                    //驾驶证过期，身份证不上传
                    break;
                }
                photoId = 1;
                choosePic();
                break;
            case R.id.ivPersonWithCard://上传手持身份证
                if (UserUtils.isNeedUpdateDriverCard(driverNumberStatus) && hasPersonWitdhCard) {
                    //驾驶证过期，身份证不上传
                    break;
                }
                photoId = 2;
                choosePic();
                break;
            case R.id.tvSubmit://提交
                upload();
                break;
        }
    }

    /**
     * 选择图片
     */
    private void choosePic() {
        final CustomAlertDialogVerify dialogVerify = CustomAlertDialogVerify.getAlertDialog(mContext, true, true);
        dialogVerify
                .setOnAlbumClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        chooseAlbum();
                        dialogVerify.dismiss();
                    }
                })
                .setOnCameraClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        chooseCamera();
                        dialogVerify.dismiss();
                    }
                }).show();
    }

    private void upload() {
        if (driveCardFile == null) {
            showLongToast("请选择要上传的驾驶证照片");
            return;
        }
        if (idCardFile == null) {
            showLongToast("请选择要上传的身份证照片");
            return;
        }
        if (personWithCardFile == null) {
            showLongToast("请选择要上传的手持身份证照片");
            return;
        }
        double driveCardSize = FileSizeUtil.getFileOrFilesSize(driveCardFile.getAbsolutePath(), FileSizeUtil.SIZETYPE_B);
        double idCardSize = FileSizeUtil.getFileOrFilesSize(idCardFile.getAbsolutePath(), FileSizeUtil.SIZETYPE_B);
        double personWithCardSize = FileSizeUtil.getFileOrFilesSize(personWithCardFile.getAbsolutePath(), FileSizeUtil.SIZETYPE_B);
        if (driveCardSize <= 0 || idCardSize <= 0 || personWithCardSize <= 0) {
            showLongToast("请选择要上传的照片");
            return;
        }
        Logger.e("驾驶证图片的路径：" + driveCardFile.getAbsolutePath() + ";大小=" + driveCardSize);
        Logger.e("身份证图片的路径：" + idCardFile.getAbsolutePath() + ";大小=" + idCardSize);
        Logger.e("手持身份证图片的路径：" + personWithCardFile.getAbsolutePath() + ";大小=" + personWithCardSize);
        VerifyInfoRequest data = new VerifyInfoRequest();
        UploadFile uploadFile = new UploadFile(new UploadFile.UploadImgListener() {

            @Override
            public void before() {
                doCheck("您的资料正在上传", true);
            }

            @Override
            public void after(SecData response) {
                closeDialog();
                if (response != null) {
                    if (response.getCode().equals(Config.REQUEST_SUCCESS)) {
                        for (File file : photoFiles) {
                            if (file.exists()) {
                                file.delete();
                                FileUtil2.updateFileFromDatabase(mContext, file);
                            }
                        }
                        Intent intent = new Intent(mContext, IdVerifyStatusActivity.class);
                        intent.putExtra("showDialog", 1);//提交认证后，在资质认证页面弹框提示
                        startActivity(intent);
                        finish();
                    } else {
                        showTipDialog(response.getMsg());
                    }
                } else {
                    showTipDialog("上传失败");
                }
            }
        }, application);
        Map<String, File> files = new HashMap<>();
        files.put("driverNumberPic", driveCardFile);//驾照
        files.put("idCardPic", idCardFile);//身份证
        files.put("handCardPic", personWithCardFile);//手持身份证
        if (null != driveCardFile) {
            photoFiles.add(driveCardFile);
        }
        if (null != idCardFile) {
            photoFiles.add(idCardFile);
        }
        if (null != personWithCardFile) {
            photoFiles.add(personWithCardFile);
        }

        uploadFile.setFiles(files);
        uploadFile.setMethod(RequestUrls.UPLOAD_VERIFY_PHOTOS);
        uploadFile.upLoad(data);
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)) {
//            Intent intent = new Intent(mContext, PersonCenterActivity.class);
//            startActivity(intent);
            finish();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }
}
