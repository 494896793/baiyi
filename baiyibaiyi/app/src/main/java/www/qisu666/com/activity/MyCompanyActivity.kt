package www.qisu666.com.activity

import android.view.View
import www.qisu666.com.R
import www.qisu666.com.utils.StringUtils
import www.qisu666.com.utils.TVUtils
import kotlinx.android.synthetic.main.activity_my_company.*
import kotlinx.android.synthetic.main.title_back.*

/**
 * Created by wujiancheng on 2017/12/11.
 * 我的认证企业
 */
class MyCompanyActivity : BaseActivity() {
    override fun setView() {
        setContentView(R.layout.activity_my_company)
    }

    override fun initDatas() {
        ivTitleLeft.setOnClickListener { finish() }
        tvTitleName.text = "认证企业"

        val myCompanyName = intent.getStringExtra("myCompanyName")
        if (StringUtils.isEmpty(myCompanyName)) {
            //没有企业认证信息
            rlytNoData.visibility = View.VISIBLE
            llytCompanyContainer.visibility = View.GONE
        } else {
            val myLeftAmount = intent.getStringExtra("myLeftAmount")
            var amount = "0"
            if (!StringUtils.isEmpty(myLeftAmount)) {
                amount = "${TVUtils.toString(Integer.parseInt(myLeftAmount) / 100.00)}元"
            }
            tvLeftAmount.text = amount
            tvCompanyName.text = myCompanyName
        }
    }

    override fun onComplete(result: String?, type: Int) {
    }

    override fun onFailure(msg: String?, type: Int) {
    }
}