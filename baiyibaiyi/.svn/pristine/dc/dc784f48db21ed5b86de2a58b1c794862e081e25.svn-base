package www.qisu666.com.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import www.qisu666.com.R
import www.qisu666.com.activity.MessageActivity
import www.qisu666.com.activity.PersonCenterActivity
import www.qisu666.com.activity.SearchParkActivity
import www.qisu666.com.event.DrawalayoutEvent
import www.qisu666.com.utils.StartActivityUtil
import kotlinx.android.synthetic.main.view_title_main.view.*
import org.greenrobot.eventbus.EventBus

/**
 * Created by wujiancheng on 2018/1/11.
 * 首页地图页的标题
 */
class TitleMainView : FrameLayout {
    private var newMessageNum: Int = 0//未读消息数量

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        LayoutInflater.from(context).inflate(R.layout.view_title_main, this)
        //跳转到个人中心
        ivTitleMainLeft.setOnClickListener {
//            StartActivityUtil<PersonCenterActivity>(mapOf()).startActivity(context, PersonCenterActivity::class.java)
            EventBus.getDefault().post(DrawalayoutEvent())
        }
        //搜索
        ivTitleSearch.setOnClickListener {
            StartActivityUtil<SearchParkActivity>(mapOf()).startActivity(context, SearchParkActivity::class.java)
        }
        //消息中心
        ivTitleInfo.setOnClickListener {
            val map = mapOf("newMessageNum" to newMessageNum)
            StartActivityUtil<MessageActivity>(map).startActivity(context, MessageActivity::class.java)
        }
    }

    /**
     * 设置图片标题
     */
    fun setTitleNameImg(resId: Int) {
        if (resId != 0) {
//            ivTitleName.setImageResource(resId)
        }
        ivTitleName.visibility = View.VISIBLE
        tvTitleName.visibility = View.GONE
    }

    /**
     * 设置文字标题
     */
    fun setTitleNameText(titleName: String) {
        tvTitleName.text = titleName
        tvTitleName.visibility = View.VISIBLE
        ivTitleName.visibility = View.GONE
    }

    /**
     * 未读消息红点展示与隐藏
     */
    fun setUnReadMsgNum(newMessageNum: Int) {
        this.newMessageNum = newMessageNum
        ivTitleInfoUnread.visibility = if (newMessageNum > 0) View.VISIBLE else View.GONE
    }
}