package www.qisu666.com.view

import android.content.Context
import android.graphics.BitmapFactory
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import cn.sharesdk.framework.Platform
import cn.sharesdk.framework.ShareSDK
import cn.sharesdk.tencent.qq.QQ
import cn.sharesdk.wechat.friends.Wechat
import cn.sharesdk.wechat.moments.WechatMoments
import www.qisu666.com.R
import www.qisu666.com.app.MyApplication
import www.qisu666.com.constant.Config
import www.qisu666.com.utils.FileUtil2
import www.qisu666.com.utils.NetWorkUtils
import www.qisu666.com.utils.ShareCustomerUtil
import www.qisu666.com.utils.ToastUtil
import kotlinx.android.synthetic.main.view_share.view.*
import java.io.File

/**
 * Created by wujiancheng on 2017/12/13.
 * 分享
 */
class ShareView : FrameLayout {
    private var shareLogo: File? = null

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        LayoutInflater.from(context).inflate(R.layout.view_share, this)
    }

    /**
     * 设置微信好友图标
     */
    fun setWeixinFriendIcon(resId: Int) {
        ivWeixinFriend.setImageResource(resId)
    }

    /**
     * 设置微信朋友圈图标
     */
    fun setWeixinTimeLineIcon(resId: Int) {
        ivWeixinTimeLine.setImageResource(resId)
    }

    /**
     * 设置QQ好友图标
     */
    fun setQQFriendIcon(resId: Int) {
        ivQQFriend.setImageResource(resId)
    }

    fun setData(url: String, title: String, content: String) {
        ShareSDK.initSDK(context)
        val bitmap = BitmapFactory.decodeResource(MyApplication.getApplication().resources, R.mipmap.logo)
        shareLogo = FileUtil2.saveBitmap(File.separator + "/logo.jpg", File.separator + FileUtil2.IMG_CACHE_DIR, bitmap)

        shareFriend(context, url, title, content)
        shareTimeLine(context, url, title)
        shareQQ(context, url, title, content)
    }

    /**
     * 分享微信好友
     */
    private fun shareFriend(context: Context, url: String, title: String, content: String) {
        val shareUtil = ShareCustomerUtil(context, Wechat.NAME)
                .setShareType(Platform.SHARE_WEBPAGE)
                .setTitle(title)
                .setText(content)
                .setUrl(url)
                .setImagePath(shareLogo?.absolutePath ?: "")

        llytShareFriend.setOnClickListener {
            if (!NetWorkUtils.isWXAppInstalledAndSupported(getContext())) {
                ToastUtil.show(getContext(), Config.NO_HAS_INSTALL_WX)
            } else {
                shareUtil.share()
            }
        }
    }

    /**
     * 分享朋友圈
     */
    private fun shareTimeLine(context: Context, url: String, title: String) {
        val shareUtil = ShareCustomerUtil(context, WechatMoments.NAME)
                .setShareType(Platform.SHARE_WEBPAGE)
                .setTitle(title)
                .setUrl(url)
                .setImagePath(shareLogo?.absolutePath ?: "")

        llytShareTimeLine.setOnClickListener {
            if (!NetWorkUtils.isWXAppInstalledAndSupported(getContext())) {
                ToastUtil.show(getContext(), Config.NO_HAS_INSTALL_WX)
            } else {
                shareUtil.share()
            }
        }
    }

    /**
     * 分享qq好友
     */
    private fun shareQQ(context: Context, url: String, title: String, content: String) {
        val shareUtil = ShareCustomerUtil(context, QQ.NAME)
                .setTitle(title)
                .setTitleUrl(url)
                .setText(content)
                .setImagePath(shareLogo?.absolutePath ?: "")

        llytShareQQ.setOnClickListener { shareUtil.share() }
    }
}