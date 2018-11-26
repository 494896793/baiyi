package www.qisu666.com.activity

import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.widget.LinearLayout
import www.qisu666.com.R
import www.qisu666.com.adapter.PledgeHistoryAdapter
import www.qisu666.com.callback.PledgeHistoryResp
import www.qisu666.com.constant.Config
import www.qisu666.com.constant.RequestUrls
import www.qisu666.com.request.PledgeHistoryRequest
import www.qisu666.com.rx.RxBus
import www.qisu666.com.rx.RxBusEvent
import www.qisu666.com.rx.RxEventCodes
import www.qisu666.com.utils.UserUtils
import kotlinx.android.synthetic.main.activity_pledge_history.*
import kotlinx.android.synthetic.main.title_back.*
import java.util.*

/**
 * Created by wujiancheng on 2017/10/20.
 * 用车保证金明细页面
 */
class PledgeHistoryActivity : BaseActivity() {
    private val QUERY_PLEDGE_HISTORY = 1
    private val data: ArrayList<PledgeHistoryResp> = ArrayList()
    private var adapter: PledgeHistoryAdapter? = null

    override fun setView() {
        setContentView(R.layout.activity_pledge_history)
    }

    override fun initDatas() {
        ivTitleLeft.setOnClickListener { finish() }
        tvTitleName.text = "保证金明细"

        adapter = PledgeHistoryAdapter(mContext, data)
        rvPledgeHistory.layoutManager = LinearLayoutManager(mContext, LinearLayout.VERTICAL, false)
        rvPledgeHistory.adapter = adapter
        adapter?.setOnItemClickListener(object : PledgeHistoryAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                if (position in data.indices) {
                    val intent = Intent(mContext, PledgeRefundStatusActivity::class.java)
                    intent.putExtra("refundLogId", data[position].refundLogId)
                    startActivity(intent)
                }
            }
        })

        queryPledgeHistory()

        observeEvent()
    }

    private fun observeEvent() {
        busSubscription = RxBus.getInstance()
                .toObservable(RxBusEvent::class.java)
                .subscribe { rxBusEvent ->
                    when (rxBusEvent.eventCode) {
                        RxEventCodes.CODE_CANCEL_REFUND_PLEDGE_SUCCESS -> {
                            //取消押金退款成功后关闭明细页面
                            finish()
                        }
                    }
                }
    }

    override fun onComplete(result: String?, type: Int) {
        if (isSuccess(result)) {
            when (type) {
                QUERY_PLEDGE_HISTORY -> {
                    val resps = getList(result, PledgeHistoryResp::class.java)
                    data.clear()
                    data.addAll(resps)
                    data.sortByDescending { it.dealTime }
                    adapter?.notifyDataSetChanged()
                }
            }
        }
    }

    override fun onFailure(msg: String?, type: Int) {

    }

    /**
     * 查询保证金退还资金明细
     */
    private fun queryPledgeHistory() {
        val request = PledgeHistoryRequest(UserUtils.getCustomerId())
        request.method = RequestUrls.QUERY_REFUND_PLEDGE_HISTORY
        doGet(request, QUERY_PLEDGE_HISTORY, Config.LOADING_STRING, true)
    }
}