package www.qisu666.com.request

/**
 * Created by wujiancheng on 2017/11/21.
 * 预约还车网点
 */
data class AppointmentsReturnParkRequest(var orderId: String, var parkId: String) : RequestBaseParams()