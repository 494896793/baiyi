package www.qisu666.com.utils;

import android.content.Context;

import www.qisu666.com.rx.RxBus;
import www.qisu666.com.rx.RxBusEvent;
import www.qisu666.com.rx.RxEventCodes;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;

/**
 * Created by Administrator on 2017/5/17.
 */

public class ShareCustomerUtil {
    private Platform platform;
    private Platform.ShareParams shareParams;

    public ShareCustomerUtil(final Context context, String platformName) {
        platform = ShareSDK.getPlatform(platformName);
        shareParams = new Platform.ShareParams();

        platform.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                Logger.i("分享成功");
                ToastUtil.show(context, "分享成功");
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
        });
    }

    public ShareCustomerUtil setShareType(int type) {
        shareParams.setShareType(type);
        return this;
    }

    /**
     * title标题，在印象笔记、邮箱、信息、微信（包括好友、朋友圈和收藏）、
     * 易信（包括好友、朋友圈）、人人网和QQ空间使用，否则可以不提供
     */
    public ShareCustomerUtil setTitle(String title) {
        shareParams.setTitle(title);
        return this;
    }

    /**
     * titleUrl是标题的网络链接，仅在人人网和QQ空间使用，否则可以不提供
     */
    public ShareCustomerUtil setTitleUrl(String titleUrl) {
        shareParams.setTitleUrl(titleUrl);
        return this;
    }

    /**
     * text是分享文本，所有平台都需要这个字段
     */
    public ShareCustomerUtil setText(String text) {
        shareParams.setText(text);
        return this;
    }

    /**
     * imagePath是本地的图片路径，除Linked-In外的所有平台都支持这个字段
     */
    public ShareCustomerUtil setImagePath(String imagePath) {
        shareParams.setImagePath(imagePath);
        return this;
    }

    /**
     * imageUrl是图片的网络路径，新浪微博、人人网、QQ空间和Linked-In支持此字段
     */
    public ShareCustomerUtil setImageUrl(String imageUrl) {
        shareParams.setImageUrl(imageUrl);
        return this;
    }

    /**
     * url在微信（包括好友、朋友圈收藏）和易信（包括好友和朋友圈）中使用，否则可以不提供
     */
    public ShareCustomerUtil setUrl(String url) {
        shareParams.setUrl(url);
        return this;
    }

    /**
     * site是分享此内容的网站名称，仅在QQ空间使用，否则可以不提供
     */
    public ShareCustomerUtil setSite(String site) {
        shareParams.setSite(site);
        return this;
    }

    /**
     * siteUrl是分享此内容的网站地址，仅在QQ空间使用，否则可以不提供
     */
    public ShareCustomerUtil setSiteUrl(String siteUrl) {
        shareParams.setSiteUrl(siteUrl);
        return this;
    }

    public void share() {
        platform.share(shareParams);
    }
}
