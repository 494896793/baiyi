package www.qisu666.com.activity

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.widget.GridLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import www.qisu666.com.R
import www.qisu666.com.adapter.PhotoAdapter
import www.qisu666.com.callback.ProblemTypesResp
import www.qisu666.com.callback.SecData
import www.qisu666.com.constant.Config
import www.qisu666.com.constant.RequestUrls
import www.qisu666.com.request.RequestBaseParams
import www.qisu666.com.request.UploadCarProblemRequest
import www.qisu666.com.request.VerifyInfoRequest
import www.qisu666.com.utils.*
import www.qisu666.com.view.CustomAlertDialog
import www.qisu666.com.view.RecyclerViewWithContentView
import kotlinx.android.synthetic.main.activity_problem_upload.*
import kotlinx.android.synthetic.main.title_back.*
import me.iwf.photopicker.PhotoPicker
import me.iwf.photopicker.PhotoPreview
import me.iwf.photopicker.utils.ImageCaptureManager
import me.iwf.photopicker.utils.PermissionsConstant
import me.iwf.photopicker.utils.PermissionsUtils
import java.io.File
import java.io.IOException
import java.util.HashMap
import kotlin.collections.ArrayList

/**
 * Created by wujiancheng on 2018/1/22.
 * 故障上报
 */
class ProblemUploadActivity : BaseActivity() {
    private val queryProblemTypes = 1

    //故障类型数据
    private var problemTypes = arrayListOf<ProblemTypesResp>()
    //照片展示列表
    private var selectedPhotos = ArrayList<String>()
    private var photoAdapter: PhotoAdapter? = null
    private var captureManager: ImageCaptureManager? = null
    //上传的图片文件
    private val photoFiles = arrayListOf<File>()

    private var hasCarNumber = false
    private var hasProblemType = false
    private var hasProblemPhoto = false

    override fun setView() {
        setContentView(R.layout.activity_problem_upload)
    }

    override fun initDatas() {
        ivTitleLeft.setOnClickListener { finish() }
        tvTitleName.text = "故障上报"
        //获取故障类型
        getProblemTypes()
        verifyEditTextContent(etProblemCarNumber)

        tvCommit.setOnClickListener { uploadProblem() }

        //最多5张
        Config.MAX_PHOTO = 5
        //选中照片展示
        photoAdapter = PhotoAdapter(mContext, selectedPhotos)
        rvPhotos.isNestedScrollingEnabled = false
        rvPhotos.layoutManager = GridLayoutManager(mContext, 4)
        rvPhotos.adapter = photoAdapter
        photoAdapter?.setOnPreviewPhotoListener { position ->
            if (position in selectedPhotos.indices) {
                //照片预览
                PhotoPreview.builder()
                        .setPhotos(selectedPhotos)
                        .setCurrentItem(position)
                        .setShowDeleteButton(true)
                        .start(mContext as ProblemUploadActivity)
            } else {
                if (PermissionsUtils.checkCameraPermission(this) && PermissionsUtils.checkWriteStoragePermission(this)) {
                    openCamera()
                }
            }
        }

        //图片删除监听
        photoAdapter?.setOnDeletePhotoListener {
            setPhotoCount(it.size)

            hasProblemPhoto = it.isNotEmpty()
            setCommitButtonBg(hasCarNumber && hasProblemType && hasProblemPhoto)
        }

        //监听是否有选中故障类型
        recyclerViewWithContentView.setOnHasSelectProblemTypeListener(object : RecyclerViewWithContentView.OnHasSelectProblemTypeListener {
            override fun onHasSelectProblemType(hasSelectedItem: Boolean) {
                hasProblemType = hasSelectedItem
                setCommitButtonBg(hasCarNumber && hasProblemType && hasProblemPhoto)
            }
        })
    }

    override fun onComplete(result: String?, type: Int) {
        if (isSuccess(result)) {
            when (type) {
                queryProblemTypes -> {
                    val resp = getList(result, String::class.java)
                    resp.forEach { problemTypes.add(ProblemTypesResp(it)) }
                    recyclerViewWithContentView.setData(problemTypes)
                }
            }
        }
    }

    override fun onFailure(msg: String?, type: Int) {

    }

    /**
     * 获取故障类型
     */
    private fun getProblemTypes() {
        val request = RequestBaseParams()
        request.method = RequestUrls.QUERY_PROBLEM_TYPE
        doGet(request, queryProblemTypes, Config.LOADING_STRING, true)
    }

    /**
     * 上传故障
     */
    private fun uploadProblem() {
        doCheck("正在上传...", true)

        val carNumber = etProblemCarNumber.text.toString().trim()
        if (StringUtils.isEmpty(carNumber)) {
            ToastUtil.show(mContext, "请输入车牌号")
            closeDialog()
            return
        }

        var problemTypeStr = ""
        problemTypes
                .filter { it.selectedStatus }
                .forEach {
                    problemTypeStr += ("\"${it.typeName}\",")
                }
        logI("problemTypeStr=" + problemTypeStr)
        if (problemTypeStr.isNotEmpty()) {
            problemTypeStr = problemTypeStr.substring(0, problemTypeStr.length - 1)
        } else {
            ToastUtil.show(mContext, "请至少选择一个故障类型")
            closeDialog()
            return
        }
        problemTypeStr = "[$problemTypeStr]"
        logI("problemTypeStr=" + problemTypeStr)

        if (selectedPhotos.isEmpty()) {
            ToastUtil.show(mContext, "给车辆拍个照吧")
            closeDialog()
            return
        }

        val request = UploadCarProblemRequest(carNumber, problemTypeStr, recyclerViewWithContentView.getInputContent())

        uploadFile(request, selectedPhotos)
    }

    /**
     * 验证输入的信息
     */
    private fun verifyEditTextContent(editText: EditText) {
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

                hasCarNumber = editText.text.toString().trim().isNotEmpty()
                setCommitButtonBg(hasCarNumber && hasProblemType && hasProblemPhoto)
            }
        })
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
                PermissionsConstant.REQUEST_CAMERA, PermissionsConstant.REQUEST_EXTERNAL_WRITE ->
                    if (PermissionsUtils.checkWriteStoragePermission(this) && PermissionsUtils.checkCameraPermission(this)) {
                        openCamera()
                    }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == PhotoPreview.REQUEST_CODE) {
            //预览删除照片后，更新adapter
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
        setPhotoCount(selectedPhotos.size)
        hasProblemPhoto = selectedPhotos.isNotEmpty()
        setCommitButtonBg(hasCarNumber && hasProblemType && hasProblemPhoto)
    }

    /**
     * 设置车况图片数量
     */
    private fun setPhotoCount(photoCount: Int) {
        val count = "($photoCount/5)"
        tvPhotoCount.text = count
    }

    /**
     *上传发票图片
     */
    private fun uploadFile(paramsBean: UploadCarProblemRequest, selectedPhotos: ArrayList<String>) {
        val uploadFile = UploadFile(object : UploadFile.UploadImgListener {

            override fun before() {

            }

            override fun after(response: SecData?) {
                if (response != null) {
                    if (response.code == Config.REQUEST_SUCCESS) {
                        //上传压缩的图片成功后删除掉
                        for (file in photoFiles) {
                            if (file.exists()) {
                                file.delete()
                                FileUtil2.updateFileFromDatabase(mContext, file)
                            }
                        }
                        closeDialog()
                        ToastUtil.show(mContext, response.msg)
                        finish()
                    } else {
                        closeDialog()
                        showTipDialog(response.msg)
                    }
                } else {
                    closeDialog()
                    showTipDialog("上传失败")
                }
                photoFiles.clear()
            }
        }, application)

        //上传的文件
        val files = hashMapOf<String, File>()
        for (index in selectedPhotos.indices) {
            val compressBitmap = ImageUtil.compressBitmapKeepTrue(selectedPhotos[index])
            val file = FileUtil2.saveBitmapToNew(mContext, compressBitmap, selectedPhotos[index])
            files.put("problemPhoto$index", file)
            if (null != file) {
                photoFiles.add(file)
            }
        }
        uploadFile.setFiles(files)
        //上传的参数
        val params = HashMap<String, String>()
        params.put("cityCode", paramsBean.cityCode)
        params.put("carNumber", "粤BD${paramsBean.carNumber}")
        params.put("faultType", paramsBean.faultType)
        params.put("userRemark", paramsBean.userRemark)
        logI("上传参数=" + params)
        uploadFile.setParams(params)
        uploadFile.setMethod(RequestUrls.QUERY_UPLOAD_PROBLEM)
        uploadFile.upLoad(VerifyInfoRequest())
    }

    private fun showTipDialog(msg: String) {
        val alertDialog = CustomAlertDialog.getAlertDialog(mContext, true, true)
        alertDialog.setMessage(msg)
                .setBtnConfirmColor(R.color.new_primary)
                .setOnPositiveClickListener("确定") { alertDialog.dismiss() }.show()
    }

    /**
     * 设置提交按钮背景
     */
    private fun setCommitButtonBg(isHighLight: Boolean) {
        when (isHighLight) {
//            true -> tvCommit.setBackgroundResource(R.drawable.bg_blue_radius_login_enable)
//            false -> tvCommit.setBackgroundResource(R.drawable.bg_blue_radius_login_unenable)
        }
    }
}