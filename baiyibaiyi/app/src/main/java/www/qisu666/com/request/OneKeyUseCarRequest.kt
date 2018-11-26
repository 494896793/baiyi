package www.qisu666.com.request

import www.qisu666.com.utils.UserUtils

/**
 * Created by wujiancheng on 2018/2/26.
 * 一键用车
 */
data class OneKeyUseCarRequest(var cityCode: String = UserUtils.getCityCode(),
                               var latitude: String = UserUtils.getLatitude(),
                               var longitude: String = UserUtils.getLongitude()) : RequestBaseParams()