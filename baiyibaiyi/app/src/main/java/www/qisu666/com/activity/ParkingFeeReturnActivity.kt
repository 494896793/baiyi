package www.qisu666.com.activity

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.widget.GridLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import www.qisu666.com.R
import www.qisu666.com.adapter.PhotoAdapter
import www.qisu666.com.callback.CustomerParkingFeeResp
import www.qisu666.com.constant.Config
import www.qisu666.com.constant.RequestUrls
import www.qisu666.com.request.ParkingFeeRequest
import www.qisu666.com.rx.RxBus
import www.qisu666.com.rx.RxBusEvent
import www.qisu666.com.rx.RxEventCodes
import www.qisu666.com.utils.*
import www.qisu666.com.view.CustomAlertDialog
import www.qisu666.com.view.PopupWindowWrap
import kotlinx.android.synthetic.main.activity_parking_fee_return.*
import kotlinx.android.synthetic.main.title_back.*
import me.iwf.photopicker.PhotoPicker
import me.iwf.photopicker.PhotoPreview
import me.iwf.photopicker.utils.ImageCaptureManager
import me.iwf.photopicker.utils.PermissionsConstant
import me.iwf.photopicker.utils.PermissionsUtils
import java.io.IOException

/**
 * Created by wujiancheng on 2017/10/9.
 * 停车费报销
 */
class ParkingFeeReturnActivity : BaseActivity() {
    private val MAX_PHOTO = 20
    private val COLUMN_COUNT = 4
    //    private val INVOICE_COUNT = 8//发票号码位数
    private val GET_CUSTOMER_PARKING_FEE = 1//获取发票报销信息
    //发票号码
    private var mInvoiceNos = ArrayList<String>()
    //照片展示列表，包括本地选择的照片，拍照的照片，网络上下载的照片
    private var selectedPhotos = ArrayList<String>()
    private var photoAdapter: PhotoAdapter? = null

    private var captureManager: ImageCaptureManager? = null
    private var parkingFeeId: String? = ""
    private var orderId: String? = ""
    private var orderCategory: String? = ""
    private var carNumber: String = ""
    private var isSecondTime = "0"
    private var imgBatchNo = ""
    private var invoiceImgList: String? = null

    override fun setView() {
        setContentView(R.layout.activity_parking_fee_return)
    }

    override fun initDatas() {
        tvTitleName.text = "停车费报销"

        parkingFeeId = intent.getStringExtra("parkingFeeId")
        orderId = intent.getStringExtra("orderId")
        orderCategory = intent.getStringExtra("orderCategory")
        carNumber = intent.getStringExtra("carNumber")

        //返回
        ivTitleLeft.setOnClickListener {
            finish()
        }

        tvOrderNo.text = orderId

        //发票号码编辑，添加监听器
        editInvoiceNo()
        Config.MAX_PHOTO = 20
        //选中照片展示
        photoAdapter = PhotoAdapter(mContext, selectedPhotos)
        rvPhotos.isNestedScrollingEnabled = false
        var dimens=resources.getDimensionPixelSize(R.dimen.dimen_5dp)
        rvPhotos.addItemDecoration(SpacesItemDecoration(dimens))
        rvPhotos.layoutManager = GridLayoutManager(mContext, 4)
        rvPhotos.adapter = photoAdapter
        photoAdapter?.setOnPreviewPhotoListener { position ->
            if (position in selectedPhotos.indices) {
                //照片预览
                PhotoPreview.builder()
                        .setPhotos(selectedPhotos)
                        .setCurrentItem(position)
                        .start(mContext as ParkingFeeReturnActivity)
            } else {
                //添加图片
                showPhotoPPW()
            }
        }

        //下一步
        tvNext.setOnClickListener {
            val invoices = getInvoiceNos()
            if (invoices.isNotEmpty()) {
//                var isOkInvoice = true//发票号码位数是否正确
//                for (invoice in mInvoiceNos) {
//                    if (invoice.length != INVOICE_COUNT) {
//                        isOkInvoice = false
//                        break
//                    }
//                }
//                if (!isOkInvoice) {
//                    ToastUtil.show(mContext, "发票号码位数为" + INVOICE_COUNT + "位数")
//                    return@setOnClickListener
//                }
                val repeatInvoice = mutableListOf<String>()
                for (i in 0 until mInvoiceNos.size) {
                    for (j in i + 1 until mInvoiceNos.size) {
                        if (mInvoiceNos[i] == mInvoiceNos[j]) {
                            if (!repeatInvoice.contains(mInvoiceNos[i])) {
                                repeatInvoice.add(mInvoiceNos[i])
                                break
                            }
                        }
                    }
                }
                if (repeatInvoice.size > 0) {
                    showTip("您有重复的发票号码：$repeatInvoice")
                    return@setOnClickListener
                }
                val parkingFee = etParkingFee.text.toString().trim()
                if (parkingFee.isNotEmpty()) {
                    if (selectedPhotos.isNotEmpty()) {
                        val intent = Intent(this, ParkingFeeReturnCommitActivity::class.java)
                        intent.putExtra("carNumber", carNumber)
                        intent.putExtra("orderId", orderId)
                        intent.putExtra("invoiceNos", invoices)
                        intent.putExtra("invoiceMoney", parkingFee)
                        intent.putExtra("selectPhotoPaths", selectedPhotos)
                        intent.putExtra("orderCategory", orderCategory)
                        intent.putExtra("isSecondTime", isSecondTime)
                        intent.putExtra("imgBatchNo", imgBatchNo)
                        intent.putExtra("invoiceImgList", invoiceImgList)//上一次上传的图片

                        startActivity(intent)
                    } else {
                        ToastUtil.show(mContext, "请上传报销单图片")
                    }
                } else {
                    ToastUtil.show(mContext, "请输入报销的停车费金额")
                }

            } else {
                ToastUtil.show(mContext, "请输入发票号码")
            }
        }
//        获取停车费已有信息
        getCustomerParkingFee()

        observeRxEventCode()
    }

    private fun observeRxEventCode() {
        busSubscription = RxBus.getInstance().toObservable(RxBusEvent::class.java)
                .subscribe { rxBusEvent ->
                    val eventCode = rxBusEvent.eventCode
                    when (eventCode) {
                        RxEventCodes.CODE_CLOSE_PARKING_FEE//关闭当前页面
                        -> {
                            finish()
                        }
                        RxEventCodes.CODE_REPEAT_INVOICE_NO//存在重复的发票号码
                        -> {
                            getCustomerParkingFee()
                        }
                    }
                }
    }

    override fun onComplete(result: String?, type: Int) {
        when {
            isSuccess(result) -> {
                when (type) {
                    GET_CUSTOMER_PARKING_FEE -> {
                        val resp = getBean(result, CustomerParkingFeeResp::class.java)

                        if (!StringUtils.isEmpty(resp.applyForOrderId)) {
                            logI("不是第一次提交")
                            isSecondTime = "1"//不是第一次提交
                        } else {
                            logI("第一次提交")
                            isSecondTime = "0"//第一次提交
                        }

                        invoiceImgList = resp.listImgUrl
                        if (!StringUtils.isEmpty(invoiceImgList)) {
                            val invoiceImgListJsonArray: JSONArray = JSONArray.parseArray(invoiceImgList ?: "")
                            if (invoiceImgListJsonArray.size > 0) {
                                setParkingFeeData(resp)
                            }
                        }
                        //批次号
                        imgBatchNo = resp.imgBatchNo
                    }
                }
            }
            else -> {
                ToastUtil.show(mContext, getMsg(result))
            }
        }
    }

    override fun onFailure(msg: String?, type: Int) {

    }

    /**
     * 获取发票报销信息
     */
    private fun getCustomerParkingFee() {
        val parkingFeeRequest = ParkingFeeRequest()
        parkingFeeRequest.parkingFeeId = parkingFeeId ?: ""
        parkingFeeRequest.orderId = orderId ?: ""
        parkingFeeRequest.orderCategory = orderCategory ?: ""

        parkingFeeRequest.method = RequestUrls.QUERY_CUSTOMER_PARKING_FEE
        doGet(parkingFeeRequest, GET_CUSTOMER_PARKING_FEE, Config.LOADING_STRING, true)
    }

    /**
     * 赋值停车费信息
     */
    private fun setParkingFeeData(resp: CustomerParkingFeeResp) {
        val jsonArray = JSONArray.parseArray(resp.invoiceNumber ?: "")//发票号码列表
        val layoutInflate = LayoutInflater.from(mContext)
        if (jsonArray != null) {
            mInvoiceNos.clear()
            for (i in jsonArray.indices) {
                val invoiceNo = jsonArray[i].toString()
                mInvoiceNos.add(invoiceNo)

                var ivInvoiceEditFirst: ImageView? = null//第一个图片 +加号
                val childCount = llytInvoiceNos.childCount
                if (i < childCount) {//填写发票号码的行数够的情况
                    val llytFirstChild = llytInvoiceNos.getChildAt(i) as LinearLayout
                    with(llytFirstChild) {
                        if (i == 0) {//第一行
//                            findViewById<View>(R.id.divideLine).visibility = View.GONE
                            ivInvoiceEditFirst = findViewById<ImageView>(R.id.ivInvoiceEdit) as ImageView
                            val etInvoiceNo = findViewById<EditText>(R.id.etInvoiceNo)
                            verifyInvoiceNumber(etInvoiceNo)
                            etInvoiceNo.setText(invoiceNo)
                        } else {
//                            findViewById<View>(R.id.divideLine).visibility = View.VISIBLE
                        }
                    }
                } else {
                    if (llytInvoiceNos.childCount < MAX_PHOTO) {
                        val view = layoutInflate.inflate(R.layout.listitem_invoice_nos, llytInvoiceNos, false)
                        val ivInvoiceEditChild = view.findViewById<ImageView>(R.id.ivInvoiceEdit)
                        ivInvoiceEditChild.setImageResource(R.mipmap.edit_delete)
                        val etInvoiceNo = view.findViewById<EditText>(R.id.etInvoiceNo)
                        verifyInvoiceNumber(etInvoiceNo)
                        etInvoiceNo.setText(invoiceNo)
                        //给已有数据添加控件显示
                        llytInvoiceNos.addView(view)
                        //删除
                        ivInvoiceEditChild.setOnClickListener {
                            llytInvoiceNos.removeView(view)
                            ivInvoiceEditFirst?.visibility = View.VISIBLE
                        }
                    } else {
                        //超过能添加的最大行数，+ 号隐藏
                        ivInvoiceEditFirst?.visibility = View.GONE
                    }
                }
            }
        }
        logI("赋值停车费信息$mInvoiceNos")
        //报销金额
        val parkingFee = resp.applyParkMoney
        if (!StringUtils.isEmpty(parkingFee)) {
            val fee = parkingFee.toInt() / 100
            etParkingFee.setText(TVUtils.toStringInt(fee.toString()))
        }

        //加载之前上传的图片
        loadPhoto(resp)
    }

    /**
     * 加载之前上传的图片
     */
    private fun loadPhoto(resp: CustomerParkingFeeResp) {
        selectedPhotos.clear()
        val invoiceImgList: String = resp.listImgUrl
        val invoiceImgListJsonArray: JSONArray = JSONArray.parseArray(invoiceImgList)
        for (array in invoiceImgListJsonArray) {
            if (array is JSONObject) {
                selectedPhotos.add(array.getString("imgUrl"))
            }
        }
        photoAdapter?.setData(selectedPhotos)
    }

    /**
     * 添加发票号码
     */
    private fun editInvoiceNo() {
        val childCount = llytInvoiceNos.childCount
        if (childCount > 0) {

            //只有一行
//            if (childCount == 1) {
            val llytFirstChild = llytInvoiceNos.getChildAt(0) as LinearLayout
            with(llytFirstChild) {
//                val divideLine: View = findViewById(R.id.divideLine)
//                divideLine.visibility = View.GONE
                val ivInvoiceEditFirst = findViewById<ImageView>(R.id.ivInvoiceEdit)
                ivInvoiceEditFirst.setImageResource(R.mipmap.edit_add)
                val etInvoiceNo = findViewById<EditText>(R.id.etInvoiceNo)
                //设置监听
                verifyInvoiceNumber(etInvoiceNo)

                val layoutInflate = LayoutInflater.from(mContext)
                //点击第一行的 + 添加一行
                ivInvoiceEditFirst.setOnClickListener {
                    if (llytInvoiceNos.childCount < MAX_PHOTO) {
                        val view = layoutInflate.inflate(R.layout.listitem_invoice_nos, llytInvoiceNos, false)
                        val ivInvoiceEditChild = view.findViewById<ImageView>(R.id.ivInvoiceEdit)
                        ivInvoiceEditChild.setImageResource(R.mipmap.edit_add)
                        val etInvoiceNo2 = view.findViewById<EditText>(R.id.etInvoiceNo)
                        verifyInvoiceNumber(etInvoiceNo2)
                        llytInvoiceNos.addView(view, 0)
                        view.requestFocus()
                        //原本的第一行，变为第二行，修改点击事件功能
                        //删除本身
                        ivInvoiceEditFirst.setOnClickListener {
                            llytInvoiceNos.removeView(this)
                            ivInvoiceEditChild.visibility = View.VISIBLE
                        }
                        ivInvoiceEditFirst.setImageResource(R.mipmap.edit_delete)
//                        divideLine.visibility = View.VISIBLE
                        //重新执行一遍
                        editInvoiceNo()
                    } else {
                        //超过能添加的最大行数，+ 号隐藏
                        ivInvoiceEditFirst.visibility = View.GONE
                    }
                }
            }
//            }
        }
    }

    /**
     * 获取发票号码
     */
    private fun getInvoiceNos(): String {
        mInvoiceNos.clear()
        var i = 0
        while (i < llytInvoiceNos.childCount) {
            val view = llytInvoiceNos.getChildAt(i)
            val etInvoiceNo = view.findViewById<EditText>(R.id.etInvoiceNo)
            val no = etInvoiceNo.text.toString()
            if (no.trim().isNotEmpty()) {
                mInvoiceNos.add(no)
            }
            i++
        }
        if (mInvoiceNos.size != 0) {
            val json = JSON.toJSONString(mInvoiceNos)
            logI("获取发票号码$json")
            return json
        }
        return ""
    }

    /**
     * 显示照片选择框
     */
    private fun showPhotoPPW() {
        val popupWindowWrap = PopupWindowWrap(mContext)
        popupWindowWrap
                .setContentView(R.layout.ppw_photo_choose, { contentView ->
                    //拍照
                    val llytTakePhoto = contentView.findViewById<LinearLayout>(R.id.llytTakePhoto)
                    llytTakePhoto.setOnClickListener {
                        if (PermissionsUtils.checkCameraPermission(this) && PermissionsUtils.checkWriteStoragePermission(this)) {
                            openCamera()
                        }
                        popupWindowWrap.dismiss()
                    }

                    //选择相册
                    val llytChoosePhoto = contentView.findViewById<LinearLayout>(R.id.llytChoosePhoto)
                    llytChoosePhoto.setOnClickListener {
                        PhotoPicker.builder()
                                .setPhotoCount(MAX_PHOTO)
                                .setGridColumnCount(COLUMN_COUNT)
                                .setShowCamera(false)
                                .setSelected(selectedPhotos)
                                .start(mContext as ParkingFeeReturnActivity)

                        popupWindowWrap.dismiss()
                    }
                    //取消
                    val llytCancel = contentView.findViewById<LinearLayout>(R.id.llytCancel)
                    llytCancel.setOnClickListener {
                        popupWindowWrap.dismiss()
                    }
                })
                .setAnim(R.style.AnimBottom)
                .setOnDismissListener {
                    popupWindowWrap.dismiss()
                }
                .showAtLocation(window.decorView.findViewById(android.R.id.content), Gravity.BOTTOM, 0, 0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK &&
                (requestCode == PhotoPicker.REQUEST_CODE || requestCode == PhotoPreview.REQUEST_CODE)) {
            //选择相册照片或者预览删除照片后，更新adapter
            //相册
            val photos = data?.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS) as ArrayList<String>
            selectedPhotos.clear()
            selectedPhotos.addAll(photos)
            photoAdapter?.setData(selectedPhotos)
        } else if (requestCode == ImageCaptureManager.REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            //拍照
            if (captureManager == null) {
                captureManager = ImageCaptureManager(this)
            }

            //将拍照完的图片更新到相册
            captureManager?.galleryAddPic()
            val path: String? = captureManager?.currentPhotoPath
            selectedPhotos.add(path ?: "")
            photoAdapter?.setData(selectedPhotos)
        }
    }

    /**
     * 拍照
     */
    private fun openCamera() {
        try {
            captureManager = ImageCaptureManager(mContext)
            val intent = captureManager?.dispatchTakePictureIntent()
            startActivityForResult(intent, ImageCaptureManager.REQUEST_TAKE_PHOTO)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            when (requestCode) {
                PermissionsConstant.REQUEST_CAMERA, PermissionsConstant.REQUEST_EXTERNAL_WRITE -> if (PermissionsUtils.checkWriteStoragePermission(this) && PermissionsUtils.checkCameraPermission(this)) {
                    openCamera()
                }
            }
        }
    }

    /**
     * 验证发票号码
     */
    private fun verifyInvoiceNumber(editText: EditText) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                logI(s.toString())
                //有非字母或非数字，过滤
                if (s.toString().isNotEmpty() && !StringUtils.isLetterDigit(s.toString())) {
                    logI("有中文")
                    val tmp = StringBuilder()
                    for (i in s.toString()) {
                        if (StringUtils.isLetterDigit(i.toString())) {
                            tmp.append(i.toString())
                        }
                    }
                    editText.setText(tmp.toString())
                    editText.setSelection(tmp.toString().length)
                }
            }
        })
    }

    private fun showTip(msg: String) {
        val alertDialog = CustomAlertDialog.getAlertDialog(mContext, true, true)
        alertDialog
                .setMessage(msg)
                .setBtnConfirmColor(R.color.new_primary)
                .setOnPositiveClickListener("知道了") {
                    alertDialog.dismiss()
                }.show()
    }
}