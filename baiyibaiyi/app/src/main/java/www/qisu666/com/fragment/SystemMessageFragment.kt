package www.qisu666.com.fragment

import android.os.Bundle
import android.view.View
import www.qisu666.com.R
import www.qisu666.com.adapter.SystemMsgAdapter
import www.qisu666.com.callback.PersonSystemMsgResp
import www.qisu666.com.callback.SystemConfigResp
import www.qisu666.com.callback.UserInfoResp
import www.qisu666.com.constant.Config
import www.qisu666.com.constant.RequestUrls
import www.qisu666.com.request.SystemArgumentRequest
import www.qisu666.com.request.SystemMsgLogRequest
import www.qisu666.com.request.UpdateMsgTimeRequest
import www.qisu666.com.utils.CacheUtils
import www.qisu666.com.utils.ResultUtil
import www.qisu666.com.utils.StringUtils
import www.qisu666.com.utils.UserUtils
import com.liaoinstan.springview.container.DefaultFooter
import com.liaoinstan.springview.container.DefaultHeader
import com.liaoinstan.springview.widget.SpringView
import kotlinx.android.synthetic.main.fragment_system_message.*

/**
 * Created by wujiancheng on 2017/12/18.
 * 个人系统消息
 */
class SystemMessageFragment : BaseFragment() {
    private var QUERY_SYSTEM_MSG = 0
    private var QUERY_SYSTEM_PARAM = 1
    private var QUERY_LOOK_MSG_TIME = 2

    private var mUser: UserInfoResp? = null
    private var curPage = 1
    private var systemMsgs: MutableList<PersonSystemMsgResp.Datas> = mutableListOf()
    private var systemMsgAdapter: SystemMsgAdapter? = null

    override fun setLayoutResId(): Int {
        return R.layout.fragment_system_message
    }

    override fun initDatas(view: View?) {
        mUser = CacheUtils.getIn().userInfo

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!StringUtils.isEmpty(mUser?.id)) {
            springView.header = DefaultHeader(activity)
            springView.footer = DefaultFooter(activity)
            springView.setListener(object : SpringView.OnFreshListener {
                override fun onRefresh() {
                    onPullDownToRefresh()
                }

                override fun onLoadmore() {
                    onPullUpToRefresh()
                }
            })
        } else {
            rlytNoMessage.visibility = View.VISIBLE
        }
        systemMsgAdapter = SystemMsgAdapter(activity, systemMsgs)
        lvMessage.adapter = systemMsgAdapter

    }

    override fun onComplete(result: String?, type: Int) {
        if (isSuccess(result)) {
            when (type) {
                QUERY_SYSTEM_MSG -> {
                    val datas = ResultUtil.getListByPage(result, PersonSystemMsgResp.Datas::class.java)
                    if (datas != null && datas.size > 0) {
                        if (curPage == 1) {
                            systemMsgs.clear()
                        }
                        datas.sortByDescending { it.createTime }
                        systemMsgs.addAll(datas)
                    }
                    systemMsgAdapter?.setData(systemMsgs)
                    if (systemMsgs.isEmpty()) {
                        rlytNoMessage.visibility = View.VISIBLE
                    } else {
                        rlytNoMessage.visibility = View.GONE
                    }
                    callMsg()//上次查看消息时间
                    stopFresh()
                }
                QUERY_LOOK_MSG_TIME -> {//上传查看消息时间
                    querySystemParams()
                }
                QUERY_SYSTEM_PARAM -> {//系统参数
                    val msgData = getBean(result, SystemConfigResp::class.java)
                    if (msgData != null) {
                        CacheUtils.getIn().save(msgData)
                    }
                }
            }
        } else {
            when (type) {
                QUERY_SYSTEM_MSG -> {
                    rlytNoMessage.visibility = View.VISIBLE
                }
            }
            stopFresh()
        }
    }

    override fun onFailure(msg: String?, type: Int) {
        when (type) {
            QUERY_SYSTEM_MSG -> {
                rlytNoMessage.visibility = View.VISIBLE
            }
        }
        stopFresh()
    }

    /**
     * 从服务器获取个人系统消息
     */
    fun querySystemMessage(curPage: Int) {
        if (!StringUtils.isEmpty(mUser?.id)) {
            val data = SystemMsgLogRequest(mUser?.id)
            data.page = curPage.toString() + ""
            data.size = 10
            data.method = RequestUrls.QUERY_SYSTEM_MSG_LOG
            doGet(data, QUERY_SYSTEM_MSG, Config.LOADING_STRING, true)
        }
    }

    /**
     * 下拉
     */
    private fun onPullDownToRefresh() {
        if (!StringUtils.isEmpty(mUser?.id)) {
            curPage = 1
            querySystemMessage(curPage)
        }
    }

    /**
     * 上拉
     */
    private fun onPullUpToRefresh() {
        if (!StringUtils.isEmpty(mUser?.id)) {
            curPage++
            querySystemMessage(curPage)
        }
    }

    private fun stopFresh() {
        springView?.onFinishFreshAndLoad()//停止刷新
    }

    /**
     * 通知服务器已查看消息
     */
    private fun callMsg() {
        val request = UpdateMsgTimeRequest()
        request.customerId = UserUtils.getCustomerId()
        request.method = RequestUrls.UPLOAD_MEMBER_QUERY_MSG_TIME
        doGet(request, QUERY_LOOK_MSG_TIME, "", false)
    }

    /**
     * 系统参数
     */
    private fun querySystemParams() {
        val data = SystemArgumentRequest()
        data.addressType = RequestUrls.url
        data.method = RequestUrls.QUERY_SYSTEM_PARAM
        doGet(data, QUERY_SYSTEM_PARAM, "", false)
    }
}