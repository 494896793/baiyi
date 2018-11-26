package www.qisu666.com.request

import www.qisu666.com.utils.UserUtils

/**
 * Created by wujiancheng on 2018/1/24.
 * 提交推荐建点信息
 */
data class RecommendParkRequest(var site: String,
                                var latitude: String,
                                var longitude: String,
                                var remark: String,
                                var cityCode: String = UserUtils.getCityCode(),
                                var customerId: String = UserUtils.getCustomerId()) : RequestBaseParams()