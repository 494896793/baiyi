package www.qisu666.com.activity

import android.content.Intent
import android.net.Uri
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.LinearLayout
import www.qisu666.com.R
import www.qisu666.com.adapter.ParkingFeeStatusAdapter
import www.qisu666.com.callback.ParkingFeeAuditStatusResp
import www.qisu666.com.constant.Config
import www.qisu666.com.rx.RxBus
import www.qisu666.com.rx.RxBusEvent
import www.qisu666.com.rx.RxEventCodes
import www.qisu666.com.utils.CacheUtils
import www.qisu666.com.utils.logI
import www.qisu666.com.view.CustomAlertDialog
import kotlinx.android.synthetic.main.activity_parking_fee_status.*
import kotlinx.android.synthetic.main.title_back.*

/**
 * Created by wujiancheng on 17-10-17.
 * 停车费报销单审核进度
 */
class ParkingFeeStatusActivity : BaseActivity() {
    var orderCategory: String? = ""//订单类型
    var orderId: String? = ""//订单类型
    var carNumber: String? = ""//订单类型
    var commitToAudit: Boolean? = false//提交报销单审核
    var parkingFeeId: String? = ""

    var parkingFeeStatus: String? = ""//当前的审核状态

    override fun setView() {
        setContentView(R.layout.activity_parking_fee_status)
    }

    override fun initDatas() {
        orderId = intent.getStringExtra("orderId")
        orderCategory = intent.getStringExtra("orderCategory")
        carNumber = intent.getStringExtra("carNumber")
        commitToAudit = intent.getBooleanExtra("commitToAudit", false)
        logI("从提交页面进来：" + commitToAudit.toString())

        ivTitleLeft.setOnClickListener {
            onBackPressed()
        }
        tvTitleName.text = "停车费报销状态"
        //审核状态数据
        val auditStatus = intent.getSerializableExtra("auditStatus") as ParkingFeeAuditStatusResp

        //支付方式
        val receiptStatus = auditStatus.receiptStatus
        receiptAccountView.setData(receiptStatus, auditStatus.customerNumber, auditStatus.customerName, auditStatus.money)

        //审核状态列表
        val data = auditStatus.list
        rvAuditStatus.layoutManager = LinearLayoutManager(mContext, LinearLayout.VERTICAL, false)
        rvAuditStatus.isNestedScrollingEnabled = false
        val adapter = ParkingFeeStatusAdapter(mContext, data)
        rvAuditStatus.adapter = adapter

        parkingFeeId = auditStatus.parkingFeeId
        parkingFeeStatus = auditStatus.parkingFeeStatus
        if ("4" == parkingFeeStatus || "6" == parkingFeeStatus || "8" == parkingFeeStatus) {
            llytReCommit.visibility = View.VISIBLE
            //重新提交
            tvReCommit.setOnClickListener {
                val intent = Intent(this, ParkingFeeReturnActivity::class.java)
                intent.putExtra("carNumber", carNumber)
                intent.putExtra("orderId", orderId)
                intent.putExtra("orderCategory", orderCategory)
                intent.putExtra("parkingFeeId", parkingFeeId)
                startActivity(intent)
                finish()
            }
            //联系客服
            tvTel.setOnClickListener {
                val info = CacheUtils.getIn().system_Info
                val uri = Uri.parse("tel:" + info?.kfphone)
                val intent = Intent(Intent.ACTION_DIAL, uri)
                startActivity(intent)
            }
        } else {
            llytReCommit.visibility = View.GONE
        }

        //提交成功显示提示
        if (commitToAudit as Boolean) {
            showTip()
        }
    }

    override fun onComplete(result: String?, type: Int) {

    }

    override fun onFailure(msg: String?, type: Int) {
    }

    override fun onBackPressed() {
        logI("onBackPressed从提交页面进来：" + commitToAudit.toString())
        if (commitToAudit as Boolean) {
            //如果是从提交报销单审核页面到该页面，那么按返回键后，要发通知
            //关闭报销单填写页面和报销单提交页面，并将当前审核状态带到订单详情页去更新
            val rxBusEvent = RxBusEvent<String>()
            rxBusEvent.eventCode = RxEventCodes.CODE_CLOSE_PARKING_FEE
            rxBusEvent.content = parkingFeeId
            RxBus.getInstance().post(rxBusEvent)
        }

        super.onBackPressed()
    }

    private fun showTip() {
        val alertDialog = CustomAlertDialog.getAlertDialog(mContext, true, true)
        alertDialog
                .setTitle("提交成功!")
                .setMessage(Config.PARKING_FEE_TIP)
                .setBtnConfirmColor(R.color.color_blue_02b2e4)
                .setOnPositiveClickListener("知道了") { alertDialog.dismiss() }.show()
    }
}