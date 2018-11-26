package www.qisu666.com.request

import www.qisu666.com.utils.UserUtils

/**
 * Created by wujiancheng on 2017/11/6.
 * 记录选择的还车网点
 */
data class RecordChooseParkRequest(var customerId: String = UserUtils.getCustomerId(), var parkId: String = "", var orderId: String = "") : RequestBaseParams()