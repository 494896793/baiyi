package www.qisu666.com.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import www.qisu666.com.R;
import www.qisu666.com.callback.ShareResp;
import www.qisu666.com.callback.UserInfoResp;
import www.qisu666.com.constant.Config;
import www.qisu666.com.constant.RequestUrls;
import www.qisu666.com.request.PrizeRequest;
import www.qisu666.com.request.ShareContentRequest;
import www.qisu666.com.rx.RxBus;
import www.qisu666.com.rx.RxBusEvent;
import www.qisu666.com.rx.RxEventCodes;
import www.qisu666.com.service.UpdateService;
import www.qisu666.com.utils.CacheUtils;
import www.qisu666.com.utils.Logger;
import www.qisu666.com.utils.ShareUtil;
import www.qisu666.com.utils.ToastUtil;
import www.qisu666.com.utils.UserUtils;

import butterknife.BindView;
import rx.functions.Action1;

public class WebActivity extends BaseActivity {
    public static final String TYPE_SHARE = "share";//分享
    public static final String TYPE_PRIZE = "prize";//抽奖
    public static final String TYPE_DOWNLOAD_APK = "downloadApk";//下载apk
    private static int REQUEST_SHARE = 1;
    private static int REQUEST_PRIZE = 2;
    private static final String TAG = WebActivity.class.getSimpleName();
    @BindView(R.id.tvTitleName)
    TextView tvTitleName;
    @BindView(R.id.ivTitleLeft)
    ImageView ivTitleLeft;
    @BindView(R.id.llyt_web_container)
    LinearLayout llytWebContainer;
    @BindView(R.id.llytTitleRight)
    LinearLayout llytTitleRight;

    private WebView myWebView;
    private String type = "";
    private ShareResp shareInfo;
    private String titleWeb = "";
    private String urlWeb = "";
    private boolean isFirst = true;

    //改写物理按键——返回的逻辑
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (myWebView.canGoBack()) {
                myWebView.goBack();//返回上一页面
                return true;
            } else {
//                System.exit(0);//退出程序
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myWebView.removeAllViews();
        myWebView.destroy();
    }

    /**
     * 分享
     */
    public void toShare() {
        if (TYPE_SHARE.equals(type)) {//分享邀请好友
            if (null != shareInfo) {
                ShareUtil.showShare(this, shareInfo.getShareUrl(), shareInfo.getTitle(), shareInfo.getContent());
            } else {
                ToastUtil.show(mContext, "暂无分享内容");
            }
        } else if (TYPE_PRIZE.equals(type)) {//分享国庆中秋
            ShareUtil.showShare(this, urlWeb, titleWeb, titleWeb);
        }
    }

    @JavascriptInterface
    public void showShare() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                toShare();
            }
        });
    }

    @JavascriptInterface
    public void finishPage() {
        finish();
    }

    @Override
    public void setView() {
        setContentView(R.layout.activity_common_web);
    }

    @Override
    public void initDatas() {
        doCheck(Config.LOADING_STRING, true);
        Intent intent = getIntent();
        urlWeb = intent.getStringExtra("url");
        Logger.i(TAG, "url == " + urlWeb);

        type = intent.getStringExtra("type");

        if (TYPE_SHARE.equals(type) || TYPE_PRIZE.equals(type)) {
            if (TYPE_SHARE.equals(type)) {
                //分享
                getShareInfo();
            }

            ImageView ivShare = new ImageView(mContext);
            ivShare.setImageResource(R.mipmap.share);
            llytTitleRight.addView(ivShare);
            llytTitleRight.setVisibility(View.VISIBLE);
            llytTitleRight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toShare();
                }
            });
        }

        myWebView = new WebView(getApplicationContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        myWebView.setLayoutParams(params);
        llytWebContainer.addView(myWebView);

        myWebView.addJavascriptInterface(this, "android");
        //启用支持javascript
        WebSettings settings = myWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setAllowFileAccess(true);// 设置允许访问文件数据
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setDefaultTextEncodingName("utf-8");// 设置编码格式
        settings.setBlockNetworkImage(false);//解决图片不显示
        settings.setUseWideViewPort(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        myWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                Logger.i(TAG, "点击的url == " + url);
                if (TYPE_DOWNLOAD_APK.equals(type)) {
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    Uri content_url = Uri.parse(url);
                    intent.setData(content_url);
                    startActivity(intent);

                } else if (url != null && url.startsWith("tel:")) {
                    Uri uri = Uri.parse(url);
                    Intent intent = new Intent(Intent.ACTION_DIAL, uri);
                    startActivity(intent);
                } else {
                    myWebView.loadUrl(url);
                }


                return true;
            }
        });


        myWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    // 网页加载完成
//                    Toast.makeText(WebActivity.this, "加载完成", Toast.LENGTH_LONG).show();
                    closeDialog();
                    Logger.i(TAG, "加载完成");
                    if (TYPE_PRIZE.equals(type)) {
                        if (isFirst) {
                            isFirst = false;
                            myWebView.loadUrl("javascript:getCustomerId(\"" + UserUtils.getPhone() + "\")");
                        }
                    }
                } else {
                    // 加载中

                }
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                WebActivity.this.titleWeb = title;
                tvTitleName.setText(title);
            }
        });
        myWebView.loadUrl(urlWeb);
        ivTitleLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myWebView.canGoBack()) {
                    myWebView.goBack();//返回上一页面
                } else {
                    finish();
                }
            }
        });

        observeRxEventCode();
    }

    @Override
    public void onComplete(String result, int type) {
        if (isSuccess(result)) {
            if (type == REQUEST_SHARE) {
                shareInfo = getBean(result, ShareResp.class);
            } else if (type == REQUEST_PRIZE) {

            }
        }
    }

    @Override
    public void onFailure(String msg, int type) {

    }

    /**
     * 分享
     */
    private void getShareInfo() {
        ShareContentRequest data = new ShareContentRequest("invite_friends");
        data.setMethod(RequestUrls.QUERY_SHARE_CONTENT);
        doGet(data, REQUEST_SHARE, "", false);
    }

    /**
     * 国庆中秋
     */
    private void getPrizeInfo() {
        UserInfoResp userInfoResp = CacheUtils.getIn().getUserInfo();
        if (userInfoResp != null) {
            PrizeRequest data = new PrizeRequest();
            data.setCustomerId(UserUtils.getCustomerId());
            data.setType("share");
            data.setMethod(RequestUrls.QUERY_PRIZE_CHANGE);
            doGet(data, REQUEST_PRIZE, Config.LOADING_STRING, true);
        }
    }

    public UpdateService.DownloadBinder binder;
    ServiceConnection conn = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            binder.cancel();
            stopService(new Intent(mContext, UpdateService.class));
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (UpdateService.DownloadBinder) service;
            // 开始下载
            binder.start();
        }
    };

    private void observeRxEventCode() {
        busSubscription = RxBus.getInstance()
                .toObservable(RxBusEvent.class)
                .subscribe(new Action1<RxBusEvent>() {
                    @Override
                    public void call(RxBusEvent rxBusEvent) {
                        int eventCode = rxBusEvent.getEventCode();
                        switch (eventCode) {
                            case RxEventCodes.CODE_SHARE_CALL_BACK://分享回调
                                if (TYPE_PRIZE.equals(type)) {
                                    getPrizeInfo();
                                }
                                break;
                        }
                    }
                });
    }
}
