package www.qisu666.com.activity

import www.qisu666.com.R
import www.qisu666.com.callback.InviteFriendsResp
import www.qisu666.com.constant.Config
import www.qisu666.com.constant.RequestUrls
import www.qisu666.com.request.InviteFriendsRequest
import www.qisu666.com.utils.UserUtils
import www.qisu666.com.view.CustomAlertDialog
import kotlinx.android.synthetic.main.activity_invite_friends.*
import kotlinx.android.synthetic.main.title_back.*

/**
 * Created by wujiancheng on 2017/12/13.
 * 邀请有礼
 */
class InviteFriendsActivity : BaseActivity() {
    private val QUERY_INVITE_FRIENDS_DATA = 1
    override fun setView() {
        setContentView(R.layout.activity_invite_friends)
    }

    override fun initDatas() {
        ivTitleLeft.setOnClickListener { finish() }
        tvTitleName.text = "邀请有礼"

        tvShareTip.text = Config.SHARE_TIP

        queryInviteFriendsData()

        //奖励规则
        tvRewardRegular.setOnClickListener {
            val alert = CustomAlertDialog.getAlertDialog(mContext, true, true)
            alert.setMessage(Config.INVITE_FRIENDS_REGULAR_TIP)
                    .setBtnConfirmColor(R.color.new_primary)
                    .setOnPositiveClickListener("知道了") { alert.dismiss() }
                    .show()
        }
    }

    override fun onComplete(result: String?, type: Int) {
        if (isSuccess(result)) {
            when (type) {
                QUERY_INVITE_FRIENDS_DATA -> {
                    val resp = getBean(result, InviteFriendsResp::class.java)
                    inviteFriendsDataView.setData("${resp.obtainAwardMoney.div(100)}元", "${resp.inviteSuccCount}人", "${resp.indirectInviteCount}人")
                    val vo = resp.vo
                    shareView.setData(vo.shareUrl, vo.title, vo.content)
                }
            }
        }
    }

    override fun onFailure(msg: String?, type: Int) {

    }

    /**
     * 获取邀请好友数据
     */
    private fun queryInviteFriendsData() {
        val request = InviteFriendsRequest(UserUtils.getCustomerId())
        request.method = RequestUrls.QUERY_INVITE_FRIENDS_DATA
        doGet(request, QUERY_INVITE_FRIENDS_DATA, Config.LOADING_STRING, true)
    }
}