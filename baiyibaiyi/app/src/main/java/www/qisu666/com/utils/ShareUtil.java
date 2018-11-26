package www.qisu666.com.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import www.qisu666.com.R;
import www.qisu666.com.app.MyApplication;
import www.qisu666.com.rx.RxBus;
import www.qisu666.com.rx.RxBusEvent;
import www.qisu666.com.rx.RxEventCodes;

import java.io.File;
import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 * Created by Administrator on 2017/5/17.
 */

public class ShareUtil {
    public static void showShare(Context context, String url, String title, String content) {
        Bitmap bitmap = BitmapFactory.decodeResource(MyApplication.getApplication().getResources(), R.mipmap.logo);
        File file = FileUtil2.saveBitmap(File.separator + "/logo.jpg", File.separator + FileUtil2.IMG_CACHE_DIR, bitmap);
        if (TextUtils.isEmpty(url)) {
            url = "http://www.baogny.com";
        }
        if (TextUtils.isEmpty(title)) {
            title = "爱上每一次出行";
        }
        if (TextUtils.isEmpty(content)) {
            content = "佰壹出行——电动汽车分时租赁，手机下单控制车辆，任意网点随租随还，出行就是这么任性！";
        }

        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        // title标题，印象笔记、邮箱、信息、微信、人人网、QQ和QQ空间使用
        oks.setTitle(title);
        // titleUrl是标题的网络链接，仅在Linked-in,QQ和QQ空间使用
        oks.setTitleUrl(url);
        // text是分享文本，所有平台都需要这个字段
        oks.setText(content);
        //分享网络图片，新浪微博分享网络图片需要通过审核后申请高级写入接口，否则请注释掉测试新浪微博
//        oks.setImageUrl("http://f1.sharesdk.cn/imgs/2014/02/26/owWpLZo_638x960.jpg");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        if (null != file) {
            oks.setImagePath(file.getAbsolutePath());//确保SDcard下面存在此张图片
        }
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(url);
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite("佰壹出行");
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl(url);
// 设置自定义的外部回调
        oks.setCallback(new OneKeyShareCallback());
// 启动分享GUI
        oks.show(context);
    }

    public static class OneKeyShareCallback implements PlatformActionListener {

        @Override
        public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
            Logger.i("分享回调成功");
            RxBusEvent event = new RxBusEvent();
            event.setEventCode(RxEventCodes.CODE_SHARE_CALL_BACK);
            RxBus.getInstance().post(event);
        }

        @Override
        public void onError(Platform platform, int i, Throwable throwable) {

        }

        @Override
        public void onCancel(Platform platform, int i) {

        }
    }
}
