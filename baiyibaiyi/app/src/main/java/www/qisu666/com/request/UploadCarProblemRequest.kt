package www.qisu666.com.request

import www.qisu666.com.utils.UserUtils

/**
 * Created by wujiancheng on 2018/1/22.
 */
data class UploadCarProblemRequest(
        var carNumber: String,
        var faultType: String,
        var userRemark: String,
        var cityCode: String = UserUtils.getCityCode(),
        var customerId: String = UserUtils.getCustomerId()) : RequestBaseParams()