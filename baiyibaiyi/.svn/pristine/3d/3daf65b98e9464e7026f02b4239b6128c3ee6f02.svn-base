package www.qisu666.com.activity

import android.view.View
import www.qisu666.com.R
import www.qisu666.com.callback.PreRechargeResp
import www.qisu666.com.utils.StringUtils
import www.qisu666.com.utils.TVUtils
import www.qisu666.com.view.RedPacketRechargePPW
import kotlinx.android.synthetic.main.activity_recharge_success.*
import kotlinx.android.synthetic.main.title_back.*

/**
 * Created by wujiancheng on 2018/1/19.
 * 充值成功
 */
class RechargeSuccessActivity : BaseActivity() {
    private var isFirst = true
    private var rechargeDetail: PreRechargeResp.CustomerRechargeDetail? = null
    private var redpackMoney: String? = ""
    override fun setView() {
        setContentView(R.layout.activity_recharge_success)
    }

    override fun initDatas() {
        ivTitleLeft.visibility = View.GONE
        tvTitleName.text = "充值成功"
        tvComplete.setOnClickListener { finish() }

        rechargeDetail = intent.getSerializableExtra("customerRechargeDetail") as PreRechargeResp.CustomerRechargeDetail?
        //充值金额
        val money = rechargeDetail?.money
        if (!StringUtils.isEmpty(money) && money != "0") {
            tvRechargeMoney.text = "${money?.toInt()?.div(100)}元"
        }
        //常规赠送金额
        val commonMoney = rechargeDetail?.commonMoney
        if (!StringUtils.isEmpty(commonMoney) && commonMoney != "0") {
            tvGiftMoney.text = "${commonMoney?.toInt()?.div(100)}元"
        } else {
            llytGiftMoneyContainer.visibility = View.GONE
        }
        //首充赠送金额
        val firstMoney = rechargeDetail?.firstMoney
        if (!StringUtils.isEmpty(firstMoney) && firstMoney != "0") {
            tvFirstRechargeGiftMoney.text = "${firstMoney?.toInt()?.div(100)}元"
        } else {
            llytFirstRechargeGiftMoney.visibility = View.GONE
        }
        //星期五充值赠送红包金额
        redpackMoney = rechargeDetail?.redpackMoney
        if (!StringUtils.isEmpty(redpackMoney) && redpackMoney != "0") {
            tvFridayRechargeGiftMoney.text = "${TVUtils.toString2(redpackMoney?.toDouble()?.div(100).toString())}元"
        } else {
            llytFridayRechargeGiftMoney.visibility = View.GONE
        }
        if (llytGiftMoneyContainer.visibility == View.GONE
                && llytFirstRechargeGiftMoney.visibility == View.GONE
                && llytFridayRechargeGiftMoney.visibility == View.GONE) {
            //没有任何赠送金额
            viewGiftMoneyDivide.visibility = View.GONE
        }
        //到账金额
        val allMoney = rechargeDetail?.allMoney
        if (!StringUtils.isEmpty(allMoney)) {
            tvArriveMoney.text = "${TVUtils.toString2(allMoney?.toDouble()?.div(100).toString())}元"
        } else {
            llytArriveMoney.visibility = View.GONE
        }
    }

    override fun onComplete(result: String?, type: Int) {

    }

    override fun onFailure(msg: String?, type: Int) {

    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (StringUtils.isEmpty(redpackMoney) && redpackMoney != "0") {
            return
        }
        if (isFirst && hasFocus) {
            isFirst = false
            val list = arrayListOf<PreRechargeResp.CustomerRechargeDetail?>()
            list.add(rechargeDetail)
            RedPacketRechargePPW().showRedPacketPPW(this, list)
        }
    }
}