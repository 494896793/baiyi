package www.qisu666.com.callback

/**
 * Created by wujiancheng on 2017/10/21.
 * dealTime true string 处理时间
status false number 状态 (审核已通过, 已取消, 已打款 等)
type true string 类型 (charge:充值 refund:退款)
refundLogId false number 退款日志id (仅仅针对退款类型)
amount true string 金额(充值金额 退款金额)
 */
data class PledgeHistoryResp(var dealTime: Long = 0, var status: String = "", var type: String = "", var refundLogId: String = "", var amount: Int = 0)