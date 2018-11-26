package www.qisu666.com.callback

/**
 * Created by wujiancheng on 2018/1/19.
 */
data class RechargeListResp(var customerId: String = "",
                            var fridayRechargeDesc: String = "",
                            var firstTimeRechargeDesc: String = "",
                            var isFirstTimeRecharge: Int = 1,//是否首充(0 : 首次充值 1 : 非抽次充值)
                            var isFridayRecharge: Int = 1,//是否首充(0 : 首次充值 1 : 非抽次充值)
                            var rechargeList: List<RechargeItem> = listOf()) {

    fun setIsFirstTimeRecharge(isFirstTimeRecharge: Int) {
        this.isFirstTimeRecharge = isFirstTimeRecharge
    }

    fun setIsFridayRecharge(isFridayRecharge: Int) {
        this.isFridayRecharge = isFridayRecharge
    }
}

data class RechargeItem(var id: Int = 0,
                        var money: Int = 0,
                        var commonGiftStatus: Int = 1,//常规赠送开启状态(0 : 开启 1 : 未开启)
                        var commonGiftDesc: String = "普通充值赠送",
                        var commonGiftMoney: Int = 0,
                        var firstTimeGiftStatus: Int = 1,//首充赠送状态(0 : 开启 1 : 未开启)
                        var firstTimeGiftDesc: String = "首充赠送",
                        var firstTimeGiftMoney: Int = 0,
                        var redpackGiftStatus: Int = 1,//红包赠送状态(0 : 开启 1 : 未开启)
                        var redpackGiftDesc: String = "红包赠送(周五红包)",
                        var redpackGiftRatioFloor: Int = 0,
                        var redpackGiftRatioUpperLimit: Int = 0,
                        var addTime: Long = 0)