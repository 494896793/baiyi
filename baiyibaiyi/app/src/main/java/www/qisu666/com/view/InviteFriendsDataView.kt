package www.qisu666.com.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import www.qisu666.com.R
import kotlinx.android.synthetic.main.view_invite_friends_data.view.*

/**
 * Created by wujiancheng on 2017/12/13.
 * 邀请好友的相关数据
 */
class InviteFriendsDataView : FrameLayout {
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
        LayoutInflater.from(context).inflate(R.layout.view_invite_friends_data, this)
    }

    fun setData(getMoney: String, inviteQuantity: String, indirectInviteQuantity: String) {
        tvGetMoney.text = getMoney
        tvInviteQuantity.text = inviteQuantity
        tvIndirectInviteQuantity.text = indirectInviteQuantity
    }
}