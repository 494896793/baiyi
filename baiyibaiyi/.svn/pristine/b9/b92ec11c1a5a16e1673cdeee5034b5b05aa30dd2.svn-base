package www.qisu666.com.activity

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.View
import com.alibaba.fastjson.JSON
import www.qisu666.com.R
import www.qisu666.com.app.PayMode
import www.qisu666.com.callback.CheckRefundOriginalResp
import www.qisu666.com.callback.PreRechargeResp
import www.qisu666.com.callback.UserInfoResp
import www.qisu666.com.callback.WeixinPayData
import www.qisu666.com.constant.Config
import www.qisu666.com.constant.RequestUrls
import www.qisu666.com.pay.ali.AlipayManager
import www.qisu666.com.pay.wx.WxPayManager
import www.qisu666.com.request.*
import www.qisu666.com.rx.RxBus
import www.qisu666.com.rx.RxBusEvent
import www.qisu666.com.rx.RxEventCodes
import www.qisu666.com.utils.*
import www.qisu666.com.view.CustomAlertDialog
import www.qisu666.com.view.CustomAlertDialogPay
import kotlinx.android.synthetic.main.activity_pledge.*
import kotlinx.android.synthetic.main.title_back.*


/**
 * Created by wujiancheng on 2017/10/20.
 * 用车保证金页面
 */
class PledgeActivity : BaseActivity() {
    private val QUERY_PLEDGE_CHANGE_REASON = 1//押金额度变动原因
    private val QUERY_PAY_PLEDGE = 2//支付押金
    private val QUERY_CHECK_REFUND_ORIGINAL = 3//是否可以原路退回
    private val QUERY_USER_INFO = 4//用户信息

    private var deposit = ""
    private var depositAll = ""
    private var depositLeft = 0//剩余需交押金
    private var changeReason = ""//更改押金额度原因

    private var payPledgeCategory: String? = ""

    override fun setView() {
        setContentView(R.layout.activity_pledge)
    }

    override fun initDatas() {
        ivTitleLeft.setOnClickListener { finish() }
        tvTitleName.text = "用车违章保证金"
        tvTitleRight.text = "明细"
        //明细
        tvTitleRight.setOnClickListener { startActivity(Intent(mContext, PledgeHistoryActivity::class.java)) }

        setData()

        //预约车辆之前没有交押金，直接弹框
//        val noDeposit = intent.getBooleanExtra("noDeposit", false)
//        if (noDeposit) {
//            showPayPledgeDialog()
//        }
        observeEvent()
    }

    private fun observeEvent() {
        busSubscription = RxBus.getInstance()
                .toObservable(RxBusEvent::class.java)
                .subscribe { rxBusEvent ->
                    when (rxBusEvent.eventCode) {
                        RxEventCodes.CODE_PLEDGE_PAY_SUCCESS -> {//押金充值成功
                            closeDialog()
                            finish()
                        }
                        RxEventCodes.CODE_CLOSE_WX_CLIENT_TIP -> closeDialog()
                        RxEventCodes.CODE_CANCEL_REFUND_PLEDGE_SUCCESS -> {
                            //取消押金退款成功后更新页面
                            setData()
                        }
                    }
                }
    }

    private fun setData() {
        val userInfo: UserInfoResp? = CacheUtils.getIn().userInfo

        val deposit: Int = userInfo?.deposit?.toInt()?.div(100) ?: 0
        val shouldDeposit: Int = userInfo?.shouldDeposit?.toInt()?.div(100) ?: 0

        //已交金额
        tvPledge.text = "¥$deposit"
        //应交总额
        tvShouldPledge.text = "应交总额¥$shouldDeposit"
        //支付押金
        tvCommitPledge.setOnClickListener { showPayPledgeDialog() }
        if (deposit != 0) {
            //已交金额不为0，可以退还押金
            tvReturnPledge.setOnClickListener {
                //是否可以原路退还
                isRefundOriginal()
            }
//            已交纳保证金且已交保证金<应交总额
            if (deposit < shouldDeposit) {//要补交押金
                depositLeft = shouldDeposit.minus(deposit)
                tvCommitPledge.text = "补交保证金(¥$depositLeft)"
                tvCommitPledge.visibility = View.VISIBLE
            } else {
                //隐藏交保证金按钮，不用交押金，可退押金
                tvCommitPledge.visibility = View.GONE
                tvReturnPledge.visibility = View.VISIBLE
            }
            if (deposit == shouldDeposit) {
                //没有问号
                ivPledgeHelp.visibility = View.GONE
            } else {
                //押金额度变动提示
                ivPledgeHelp.setOnClickListener {
                    showTip()
                }
            }
            logI("depositLeft11=" + depositLeft)
        } else {//还没交押金
            depositLeft = shouldDeposit
            logI("depositLeft22=" + depositLeft)
            tvCommitPledge.text = "交纳保证金(¥$shouldDeposit)"
            tvCommitPledge.visibility = View.VISIBLE
            //已交金额为0，隐藏退还押金按钮
            tvReturnPledge.visibility = View.GONE
            //没有问号
            ivPledgeHelp.visibility = View.GONE
        }
        queryPledgeChangeReason()
    }

    override fun onComplete(result: String?, type: Int) {
        if (isSuccess(result)) {
            when (type) {
                QUERY_PLEDGE_CHANGE_REASON -> {//押金额度更改原因
                    val resp = getBean(result, PledgeChangeReasonResp::class.java)
                    deposit = resp.deposit.div(100).toString()
                    depositAll = resp.depositAll.div(100).toString()
                    changeReason = resp.desc
                    logI(changeReason)
                }
                QUERY_PAY_PLEDGE -> {//支付押金
                    val msg = getBean(result, PreRechargeResp::class.java)
                    if (PayMode.ALI_PAY_TYPE == payPledgeCategory) {
                        Logger.e(" result.getRequestData()=" + msg.customerRechargeRequestData)
                        doCheck("请稍后...", true)
                        //                    PayUtils.aliPay(mContext, msg.getRequestData(), new Handler());

                        val alipayManager = AlipayManager()
                        alipayManager.pay(this, msg.customerRechargeRequestData, RxEventCodes.CODE_PLEDGE_PAY_SUCCESS)

                    } else if (PayMode.WEIXIN_PAY_TYPE == payPledgeCategory) {

                        val data = JSON.parseObject(msg.customerRechargeRequestData, WeixinPayData::class.java)
                        val wxPayManager = WxPayManager()
                        wxPayManager.pay(this, application, data, msg.customerRechargeMoney, RxEventCodes.CODE_PLEDGE_PAY_SUCCESS)

                    }
                }
                QUERY_CHECK_REFUND_ORIGINAL -> {//检查是否可以原路退回
                    showToRefund("用车保证金退还后，将无法用车，确认退还吗？", result)
                }
                QUERY_USER_INFO -> {//用户信息
                    val userInfoResp = getBean(result, UserInfoResp::class.java)
                    CacheUtils.getIn().save(userInfoResp)
                    setData()
                }
            }
        } else {
            when (type) {
                QUERY_PAY_PLEDGE -> {
                    ToastUtil.show(mContext, getMsg(result))
                }
                QUERY_CHECK_REFUND_ORIGINAL -> {
                    when (getCode(result)) {
                        "600215", "600214" -> {
                            showRefundTip2(getMsg(result))
                        }
                        else -> {
                            showRefundTip(getMsg(result))
                        }
                    }
                }
            }
        }
    }

    override fun onFailure(msg: String?, type: Int) {

    }

    override fun onResume() {
        super.onResume()
        queryUserInfo()
    }

    /**
     * 补交押金提示
     */
    private fun showTip() {
        val tip = String.format(Config.PLEDGE_CHANGE_REASON, deposit, depositAll, changeReason)
        val ssb = SpannableStringBuilder(tip)
        //ForegroundColorSpan 为文字前景色，BackgroundColorSpan为文字背景色
        val blueSpan = ForegroundColorSpan(Color.parseColor("#02b2e4"))
        val blueSpan2 = ForegroundColorSpan(Color.parseColor("#02b2e4"))
        ssb.setSpan(blueSpan, tip.indexOf(deposit), deposit.length + tip.indexOf(deposit), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        ssb.setSpan(blueSpan2, tip.indexOf(depositAll), depositAll.length + tip.indexOf(depositAll), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        val alert = CustomAlertDialog.getAlertDialog(mContext, true, true)
        alert.setMessage(ssb)
                .setBtnConfirmColor(R.color.new_primary)
                .setOnPositiveClickListener("知道了") { alert.dismiss() }
                .show()
    }

    private fun showRefundTip(msg: String) {
        val alert = CustomAlertDialog.getAlertDialog(mContext, true, true)
        alert.setMessage(msg)
                .setBtnConfirmColor(R.color.new_primary)
                .setOnPositiveClickListener("知道了") { alert.dismiss() }
                .show()
    }

    private fun showRefundTip2(msg: String) {
        val alert = CustomAlertDialog.getAlertDialog(mContext, true, true)
        alert.setTitle("暂无法申请")
                .setMessage(msg)
                .setBtnConfirmColor(R.color.new_primary)
                .setBtnCancelColor(R.color.main_background)
                .setOnPositiveClickListener("联系客服") {
                    val info = CacheUtils.getIn().system_Info
                    val uri = Uri.parse("tel:" + info?.kfphone)
                    val intent = Intent(Intent.ACTION_DIAL, uri)
                    startActivity(intent)
                    alert.dismiss()
                }
                .setOnNegativeClickListener("知道了") { alert.dismiss() }
                .show()
    }

    private fun showToRefund(msg: String, result: String?) {
        val alert = CustomAlertDialog.getAlertDialog(mContext, true, true)
        alert.setMessage(msg)
                .setBtnConfirmColor(R.color.new_primary)
                .setBtnCancelColor(R.color.main_background)
                .setOnPositiveClickListener("确认退还") {
                    val resp = getList(result, CheckRefundOriginalResp::class.java)
                    SharedPreferencesUtils.putString(mContext, "CheckRefundOriginalResp", result)
//                    resp.sortByDescending { it.depositRest }
                    val intent = Intent(mContext, PledgeRefundCommitActivity::class.java)

                    startActivity(intent)
                    alert.dismiss()
                }
                .setOnNegativeClickListener("取消") { alert.dismiss() }
                .show()
    }

    /**
     * 押金额度更改原因
     */
    private fun queryPledgeChangeReason() {
        val request = PledgeChangeReasonRequest(UserUtils.getCustomerId())
        request.method = RequestUrls.QUERY_PLEDGE_CHANGE_EXPLAIN
        doGet(request, QUERY_PLEDGE_CHANGE_REASON, Config.LOADING_STRING, false)
    }

    private fun showPayPledgeDialog() {
        val dialogPay = CustomAlertDialogPay.getAlertDialog(mContext, true, true)
        dialogPay.setOnPositiveClickListener("交纳") {
            //微信或支付宝充值
            val data = ChargeRequest()
            data.bankType = payPledgeCategory
            data.rechargeType = Config.DEPOSIT_TYPE
            data.rechargeMoney = "" + (depositLeft.times(100))//500块押金
            //                data.setRechargeMoney("" + 1);//500块押金
            data.customerId = UserUtils.getCustomerId()
            data.method = RequestUrls.PRE_RECHARGE
            doGet(data, QUERY_PAY_PLEDGE, "请稍后...", true)
            dialogPay.dismiss()
        }.setWxDesc("微信(${depositLeft}元)").setAliDesc("支付宝(${depositLeft}元)").setOnPayTypeClickListener { payType ->
            logI("默认交押金方式==" + payType)
            payPledgeCategory = payType
        }.show()
    }

    /**
     * 是否可以原路退回
     */
    private fun isRefundOriginal() {
        val request = CheckRefundOriginalRequest(UserUtils.getCustomerId())
        request.method = RequestUrls.CHECK_IS_ORIGINAL_REFUND
        doGet(request, QUERY_CHECK_REFUND_ORIGINAL, Config.LOADING_STRING, true)
    }

    private fun queryUserInfo() {
        val data = UserInfoRequest()
        val userInfoResp = CacheUtils.getIn().userInfo
        if (userInfoResp != null) {
            data.customerPhone = userInfoResp.phone
            data.method = RequestUrls.QUERY_USER_INFO
            doGet(data, QUERY_USER_INFO, "", false)
        }
    }
}