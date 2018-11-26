package www.qisu666.com.activity

import android.content.Intent
import android.text.Html
import android.view.View
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import www.qisu666.com.R
import www.qisu666.com.app.PayMode
import www.qisu666.com.callback.ParkingFeeAuditStatusResp
import www.qisu666.com.callback.SecData
import www.qisu666.com.constant.Config
import www.qisu666.com.constant.RequestUrls
import www.qisu666.com.request.ParkingFeeAuditStatusRequest
import www.qisu666.com.request.ParkingFeeCommitRequest
import www.qisu666.com.request.VerifyInfoRequest
import www.qisu666.com.rx.RxBus
import www.qisu666.com.rx.RxBusEvent
import www.qisu666.com.rx.RxEventCodes
import www.qisu666.com.utils.*
import www.qisu666.com.view.CustomAlertDialog
import kotlinx.android.synthetic.main.activity_parking_fee_return_commit.*
import kotlinx.android.synthetic.main.title_back.*
import java.io.File
import java.util.*
import kotlin.collections.ArrayList


/**
 * Created by wujiancheng on 2017/10/9.
 * 停车费报销提交页面
 */
class ParkingFeeReturnCommitActivity : BaseActivity() {
    private val RETURN_TO_BALANCE = PayMode.BALANCE_PAY_TYPE//退款到余额
    private val RETURN_TO_CASH = PayMode.ALI_PAY_TYPE//退款到现金,A 支付宝，WX 微信
    private var returnType = RETURN_TO_BALANCE//收款类型

    private val REQUEST_COMMIT_PARKING_FEE = 1//提交报销信息
    private val QUERY_AUDIT_STATUS = 2//查询停车费报销审核进度

    private var carNumber = ""
    private var orderId = ""
    private var invoiceNos = ""
    private var invoiceMoney = ""
    private var accountType = ""//打款账户类型
    private var orderCategory: String? = ""
    private var isSecondTime = ""
    private var imgBatchNo = ""
    private var invoiceImgList = ""//之前上传的图片

    private var notDeletedInvoiceImgList = ""//没有被删除的图片

    private var wxNickName: String? = ""
    private var openId: String? = ""

    private var hasSwitch = false//是否点击切换收款账号
    private val photoFiles = arrayListOf<File>()//上传的图片文件

    override fun setView() {
        setContentView(R.layout.activity_parking_fee_return_commit)
    }

    override fun initDatas() {
        tvTitleName.text = "停车费报销"

        carNumber = intent.getStringExtra("carNumber")
        orderId = intent.getStringExtra("orderId")
        Logger.i("orderId == " + orderId)
        invoiceNos = intent.getStringExtra("invoiceNos")
        invoiceMoney = intent.getStringExtra("invoiceMoney")
        orderCategory = intent.getStringExtra("orderCategory")
        isSecondTime = intent.getStringExtra("isSecondTime")
        imgBatchNo = intent.getStringExtra("imgBatchNo")
        invoiceImgList = intent.getStringExtra("invoiceImgList")
        val selectPhotoPaths = intent.getStringArrayListExtra("selectPhotoPaths")

        if (!StringUtils.isEmpty(invoiceImgList)) {
            //获得没有被删除的图片
            getNotDeletedInvoiceImgList(selectPhotoPaths)
        }

        //返回
        ivTitleLeft.setOnClickListener {
            finish()
        }
        //退到余额
        llytReturnToBalance.setOnClickListener {
            ivReturnToBalance.setImageResource(R.mipmap.yc_43)
            ivReturnToCash.setImageResource(R.mipmap.yc_42)
            returnType = RETURN_TO_BALANCE
            logI("退到余额")
            getMoneyAccountView.visibility = View.GONE
        }
        tvBalanceValue.text = "(充值${invoiceMoney}元)"
        tvBalanceTip.text = Html.fromHtml(HighlightUtil.convertHightlightText(Config.BALANCE_TIP, Config.BALANCE_TIP_HIGH_LIGHT, "#51E7D3"))
        //退到现金
        llytReturnToCash.setOnClickListener {
            ivReturnToBalance.setImageResource(R.mipmap.yc_42)
            ivReturnToCash.setImageResource(R.mipmap.yc_43)
            if (!hasSwitch) {
                returnType = RETURN_TO_CASH
            }
            logI("退到现金")
            getMoneyAccountView.visibility = View.VISIBLE
        }
        tvCashValue.text = "(打款${invoiceMoney}元)"
        tvCashTip.text = Html.fromHtml(HighlightUtil.convertHightlightText(Config.CASH_TIP, Config.CASH_TIP_HIGH_LIGHT, "#51E7D3"))
        //切换收款账号监听
        getMoneyAccountView.setOnSwitchAccountTypeListener { accountType ->
            returnType = accountType

        }
        //上一步
        tvPrevious.setOnClickListener { finish() }
        //提交
        tvCommit.setOnClickListener {

            if (PayMode.ALI_PAY_TYPE == returnType) {//支付宝
                if (getMoneyAccountView.aliAccountName.isEmpty()) {
                    ToastUtil.show(mContext, "请输入真实姓名")
                    return@setOnClickListener
                }
                if (getMoneyAccountView.aliAccountNumber.isEmpty()) {
                    ToastUtil.show(mContext, "请输入支付宝账号")
                    return@setOnClickListener
                }
                if (!StringUtils.isLetterOrChinese(getMoneyAccountView.aliAccountName)) {
                    ToastUtil.show(mContext, "姓名只能为中文或字母")
                    return@setOnClickListener
                }
                if (!StringUtils.isEMail(getMoneyAccountView.aliAccountNumber)
                        && !StringUtils.isTel(getMoneyAccountView.aliAccountNumber)) {
                    ToastUtil.show(mContext, "请输入正确的支付宝账号信息(邮箱或手机号)")
                    return@setOnClickListener
                }

            } else if (PayMode.WEIXIN_PAY_TYPE == returnType) {//微信
                if (openId?.length == 0 || wxNickName?.length == 0) {
                    ToastUtil.show(mContext, "请获取微信账号信息")
                    return@setOnClickListener
                }
            }

            //上传不包含网络上下载的图片
            val selectPhotoPathsNotNet = selectPhotoPaths.filterNotTo(ArrayList<String>()) { it.startsWith("http") }
            if (selectPhotoPathsNotNet.isNotEmpty()) {
                //上传本地图片，非网络图片
                uploadFile(selectPhotoPathsNotNet, 0)
            } else {
                if (selectPhotoPaths.isNotEmpty()) {
                    //只有网络图片，没有本地图片，那么只上传报销数据，不上传图片
                    //提交报销信息
                    commitParkingFeeInfo()
                } else {
                    ToastUtil.show(mContext, "请选择要上传的发票图片")
                }
            }
        }

        observeRxEventCode()
    }

    private fun observeRxEventCode() {
        busSubscription = RxBus.getInstance().toObservable(RxBusEvent::class.java)
                .subscribe { rxBusEvent ->
                    val eventCode = rxBusEvent.eventCode
                    when (eventCode) {
                        RxEventCodes.CODE_WX_NICKNAME
                        -> {
                            val map = rxBusEvent.content as HashMap<String, String>
                            wxNickName = map["nickname"]
                            getMoneyAccountView.setWxNickName(wxNickName)
                            openId = map["openId"]
                        }
                        RxEventCodes.CODE_CLOSE_PARKING_FEE//关闭当前页面
                        -> {
                            finish()
                        }
                    }
                }
    }

    /**
     * 获取没有被删除的图片地址
     */
    private fun getNotDeletedInvoiceImgList(selectPhotoPaths: ArrayList<String>) {
        val invoiceImgListJsonArray: JSONArray = JSONArray.parseArray(invoiceImgList)
        logI("selectPhotoPaths=" + selectPhotoPaths)
        //没有被删除的图片
        val invoiceImgsNotDelete = ArrayList<String>()
        for (img in invoiceImgListJsonArray) {
            if (img is JSONObject) {
                val imgUrl = img.getString("imgUrl")
//                val invoiceImg = InvoiceImg()
//                invoiceImg.imgUrl = imgUrl
//                invoiceImgsDelete.add(imgUrl)
                for (selected in selectPhotoPaths) {
                    if (selected == imgUrl) {//没被删除
                        invoiceImgsNotDelete.add(imgUrl)
                        break
                    }
                }
            }
        }
        notDeletedInvoiceImgList = "["
        for (img in invoiceImgsNotDelete) {
            notDeletedInvoiceImgList += "{\"imgUrl\":\"${img}\"},"
        }
        if (notDeletedInvoiceImgList.length > 1) {
            notDeletedInvoiceImgList = notDeletedInvoiceImgList.substring(0, notDeletedInvoiceImgList.length - 1)
            notDeletedInvoiceImgList += "]"
        } else {
            notDeletedInvoiceImgList = ""
        }

        logI("notDeletedInvoiceImgList=" + notDeletedInvoiceImgList)
    }

    private fun showTipDialog(msg: String) {
        val alertDialog = CustomAlertDialog.getAlertDialog(mContext, true, true)
        alertDialog.setMessage(msg)
                .setBtnConfirmColor(R.color.color_blue_02b2e4)
                .setOnPositiveClickListener("确定") { alertDialog.dismiss() }.show()
    }

    /**
     *上传发票图片
     */
    private fun uploadFile(selectPhotoPaths: ArrayList<String>, index: Int) {
        val uploadFile = UploadFile(object : UploadFile.UploadImgListener {

            override fun before() {
                doCheck("正在上传第" + (index + 1) + "张", true)
            }

            override fun after(response: SecData?) {
                if (response != null) {
                    if (response.code == Config.REQUEST_SUCCESS) {

                        if (index + 1 < selectPhotoPaths.size) {
                            uploadFile(selectPhotoPaths, index + 1)
                        } else {
                            for (file in photoFiles) {
                                if (file.exists()) {
                                    file.delete()
                                    FileUtil2.updateFileFromDatabase(mContext, file)
                                }
                            }
                            closeDialog()
                            ToastUtil.show(mContext, "上传成功")
                            //提交报销信息
                            commitParkingFeeInfo()
                        }
                    } else {
                        closeDialog()
                        showTipDialog(response.msg)
                    }
                } else {
                    closeDialog()
                    showTipDialog("上传失败")
                }
            }
        }, application)
        val filePath = selectPhotoPaths[index]
        val compressBitmap = ImageUtil.compressBitmapKeepTrue(filePath)
        val file = FileUtil2.saveBitmapToNew(mContext, compressBitmap, filePath)
        val files = hashMapOf<String, File>()
        files.put("imgFile", file)
        uploadFile.setFiles(files)
        if (null != file) {
            photoFiles.add(file)
        }
        val params = HashMap<String, String>()
        params.put("orderId", orderId)
        params.put("imgBatchNo", imgBatchNo)
        logI("图片上传的params=" + params)
        uploadFile.setParams(params)
        uploadFile.setMethod(RequestUrls.UPLOAD_INVOICE_PHOTO)
        uploadFile.upLoad(VerifyInfoRequest())
    }

    override fun onComplete(result: String?, type: Int) {
        when {
            isSuccess(result) -> {
                when (type) {
                    REQUEST_COMMIT_PARKING_FEE -> {//提交报销信息
                        ToastUtil.show(mContext, "提交报销申请成功")
                        val parkingFeeId = getBean(result, String::class.java)
                        //保存支付宝账号
                        getMoneyAccountView.saveZhiFuBaoAccount()
                        //审核进度
                        getParkingFeeAuditStatus(parkingFeeId)
                    }
                    QUERY_AUDIT_STATUS -> {//查询停车费报销审核进度
                        val resp = getBean(result, ParkingFeeAuditStatusResp::class.java)
                        if (StringUtils.isEmpty(result)) {
                            ToastUtil.show(mContext, getMsg(result))
                        } else {
                            //查看进度条
                            val intent = Intent(mContext, ParkingFeeStatusActivity::class.java)
                            intent.putExtra("auditStatus", resp)
                            intent.putExtra("carNumber", carNumber)
                            intent.putExtra("orderId", orderId)
                            intent.putExtra("orderCategory", orderCategory)//短租要传订单类型
                            intent.putExtra("commitToAudit", true)//提交报销单审核
                            startActivity(intent)
                        }
                    }
                }
            }
            else -> {
                when (type) {
                    REQUEST_COMMIT_PARKING_FEE -> {//提交报销信息
                        if (getCode(result) == Config.REQUEST_REPEAT_INVOICE_NO) {//存在重复的发票号码
                            val invoices = getList(result, String::class.java)
                            showTip(getMsg(result) + ":" + invoices)
                        }
                    }
                    else ->
                        ToastUtil.show(mContext, getMsg(result))
                }

            }
        }
    }

    override fun onFailure(msg: String?, type: Int) {

    }

    /**
     * 提交报销信息
     */
    private fun commitParkingFeeInfo() {
        val request = ParkingFeeCommitRequest()
        request.customerPhone = UserUtils.getPhone()
        request.orderId = orderId
        request.carNumber = carNumber
        var invoiceMoneyI = 0
        if (!StringUtils.isEmpty(invoiceMoney)) {
            invoiceMoneyI = invoiceMoney.toInt()
            invoiceMoneyI *= 100
        }
        request.applyParkMoney = invoiceMoneyI.toString()
        request.invoiceNumber = invoiceNos
        request.receiptStatus = returnType
        if (PayMode.ALI_PAY_TYPE == returnType) {//支付宝
            request.customerNumber = getMoneyAccountView.aliAccountNumber
            request.customerName = getMoneyAccountView.aliAccountName
        } else if (PayMode.WEIXIN_PAY_TYPE == returnType) {//微信
            request.customerNumber = openId
            request.customerName = wxNickName
        } else if (PayMode.BALANCE_PAY_TYPE == returnType) {//余额
            request.customerName = UserUtils.getPhone()
        }
        request.orderCategory = orderCategory ?: ""
        request.isSecondTime = isSecondTime
        request.clientCategory = "APP"
        request.imgUrlList = notDeletedInvoiceImgList
        request.imgBatchNo = imgBatchNo
        request.method = RequestUrls.COMMIT_PARKING_FEE
        doGet(request, REQUEST_COMMIT_PARKING_FEE, Config.LOADING_STRING, true)
    }

    /**
     * 查询停车费报销审核进度
     */
    private fun getParkingFeeAuditStatus(parkingFeeId: String) {
        val request = ParkingFeeAuditStatusRequest()
        request.parkingFeeId = parkingFeeId
        request.orderId = orderId
        request.orderCategory = orderCategory
        request.method = RequestUrls.QUERY_PARKING_FEE_AUDIT_STATUS
        doGet(request, QUERY_AUDIT_STATUS, Config.LOADING_STRING, true)
    }

    private fun showTip(msg: String) {
        val alertDialog = CustomAlertDialog.getAlertDialog(mContext, false, false)
        alertDialog
                .setMessage(msg)
                .setBtnConfirmColor(R.color.color_blue_02b2e4)
                .setOnPositiveClickListener("知道了") {
                    alertDialog.dismiss()
                    val event = RxBusEvent<String>()
                    event.eventCode = RxEventCodes.CODE_REPEAT_INVOICE_NO
                    RxBus.getInstance().post(event)
                }.show()
    }
}