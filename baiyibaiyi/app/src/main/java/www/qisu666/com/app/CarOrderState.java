package www.qisu666.com.app;

import java.util.HashMap;
import java.util.Map;

public class CarOrderState {
    public static final String Car_Order_Status_Finish = "finish";// 完成状态
    public static final String Car_Order_Status_Ordered = "ordered";// 预约状态
    public static final String Car_Order_Status_PlanReturn = "planReturn";//计划还车状态
    public static final String Car_Order_Status_WaitQueue = "waitQueue";//排队等待状态
    public static final String Car_Order_Status_Wait = "wait";//等待用车状态
    public static final String Car_Order_Status_Return = "return";// 还车状态
    public static final String Car_Order_Status_Cancel = "cancel";//取消状态
    public static final String Car_Order_Status_UseCar = "useCar";// 用车状态

    public static final String Car_Order_Status_pay = "aliPay";// 待付款
    public static final String Car_Order_Status_nopay = "noPay";// 待付款

    private final static String[] neetRevivalStates = {Car_Order_Status_Ordered,
            Car_Order_Status_UseCar, Car_Order_Status_Return};

    public static boolean isNeetRevival(String state) {
        return ArrayUtils.contains(neetRevivalStates, state);
    }

    public static String getStateText(String state) {
        if (state.equals(Car_Order_Status_Finish)) {
            return "已完成";
        } else if (state.equals(Car_Order_Status_Ordered)) {
            return "已预约";
        } else if (state.equals(Car_Order_Status_PlanReturn)) {
            return "准备还车";
        } else if (state.equals(Car_Order_Status_WaitQueue)) {
            return "排队中";
        } else if (state.equals(Car_Order_Status_Wait)) {
            return "等待用车";
        } else if (state.equals(Car_Order_Status_Return)) {
            return "还车中";
        } else if (state.equals(Car_Order_Status_Cancel)) {
            return "已取消";
        } else if (state.equals(Car_Order_Status_UseCar)) {
            return "使用中";
        }
        return state;
    }

    public static String getStateTimeTipsText(String state) {
        if (state.equals(Car_Order_Status_Finish)) {
            return "行程完成时间:";
        } else if (state.equals(Car_Order_Status_Ordered)) {
            return "订单失效时间:";
        } else if (state.equals(Car_Order_Status_Cancel)) {
            return "订单取消时间:";
        } else if (state.equals(Car_Order_Status_UseCar) || state.equals(Car_Order_Status_Return)) {
            return "上车时间:";
        }
        return "最后修改时间:";
    }


    static Map<String, String> status = new HashMap<String, String>();

    static {
        status.put("checkWait", "待审核");
        status.put("checkSucc", "审核通过");
        status.put("checkFail", "审核不通过");
        status.put("checkWithout", "无需审核");
        status.put("aliPay", "待支付");
        status.put("finish", "已完成");
        status.put("noPay", "待支付");
        status.put("cancel", "已取消");
    }

    public static String getCheckStatus(String key) {
        return status.get(key);
    }
}
