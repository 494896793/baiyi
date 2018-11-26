package www.qisu666.com.view

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import www.qisu666.com.R
import www.qisu666.com.activity.LoginActivity
import www.qisu666.com.activity.ProblemUploadActivity
import www.qisu666.com.activity.RecommendParkActivity
import www.qisu666.com.activity.WebActivity
import www.qisu666.com.utils.CacheUtils
import www.qisu666.com.utils.SharedPreferencesUtils

/**
 * Created by wujiancheng on 2018/1/22.
 * 客服
 */
class KfPPWs(val context: Activity) {
    private var onUsingGuideClickListener: OnUsingGuideClickListener? = null
    fun showKfPPW() {
        val ppw = PopupWindowWrap(context)
        ppw
                .setContentView(R.layout.ppw_kfs, PopupWindowWrap.OnCreatedPPWListener { contentView ->
                    //背景
                    val viewBackground: View = contentView.findViewById(R.id.viewBackground)
                    viewBackground.setOnClickListener { ppw.dismiss() }

                    val info = CacheUtils.getIn().system_Info
                    //客服电话
                    if (null != info) {
                        val tvKfPhone: TextView = contentView.findViewById(R.id.tvKfPhone)
                        val tel = "${info.kfphone}"
                        tvKfPhone.text = tel
                        //拨打电话
                        tvKfPhone.setOnClickListener {
                            val uri = Uri.parse("tel:" + info.kfphone)
                            val intent = Intent(Intent.ACTION_DIAL, uri)
                            context.startActivity(intent)
                        }
                    }
                    //使用指南已读未读
                    val ivUnread = contentView.findViewById<View>(R.id.ivUnread)
                    val isRead = SharedPreferencesUtils.getBoolean(context, "isReadUsingGuide", false)
                    if (isRead) {
                        ivUnread.visibility = View.GONE
                    } else {
                        ivUnread.visibility = View.VISIBLE
                    }
                    //使用指南点击
                    val rlytKfGuide: RelativeLayout = contentView.findViewById(R.id.rlytKfGuide)
                    rlytKfGuide.setOnClickListener {
                        //跳转到使用指南
                        val intent = Intent(context, WebActivity::class.java)
                        intent.putExtra("url", info?.cdzn ?: "")
                        context.startActivity(intent)

                        if (!isRead) {
                            //红点消失
                            SharedPreferencesUtils.putBoolean(context, "isReadUsingGuide", true)
                            ivUnread.visibility = View.GONE
                            //通知首页客服中心红点消失
                            onUsingGuideClickListener?.onUsingGuideClick()
                        }
                    }
                    //故障上报
                    contentView.findViewById<TextView>(R.id.tvProblemUpload).setOnClickListener {
                        context.startActivity(Intent(context, ProblemUploadActivity::class.java))
                    }
                    //推荐建点
                    contentView.findViewById<TextView>(R.id.tvRecommendPark).setOnClickListener {
                        if (CacheUtils.getIn().isLogin) {
                            context.startActivity(Intent(context, RecommendParkActivity::class.java))
                        } else {
                            context.startActivity(Intent(context, LoginActivity::class.java))
                        }
                    }
                })
                .setHeight(ViewGroup.LayoutParams.MATCH_PARENT)
                .isChangeWindowBg(false)
                .setOnDismissListener {
                    ppw.dismiss()

                }.showAtLocation(context.window.decorView.findViewById(android.R.id.content), Gravity.CENTER, 0, 0)
    }

    interface OnUsingGuideClickListener {
        fun onUsingGuideClick()
    }

    fun setOnUsingGuideClickListener(onUsingGuideClickListener: OnUsingGuideClickListener) {
        this.onUsingGuideClickListener = onUsingGuideClickListener
    }

}