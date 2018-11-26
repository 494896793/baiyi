package www.qisu666.com.callback

/**
 * Created by wujiancheng on 2017/11/18.
 * 红包车
 */
data class ParkRedPacketCarResp(var map: Map<String, RedPacketCar> = hashMapOf())

data class RedPacketCar(var redPacketCarNum: String = "0", var parkFreeCarNum: String = "0")