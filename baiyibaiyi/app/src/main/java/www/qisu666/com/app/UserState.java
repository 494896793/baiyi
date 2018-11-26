package www.qisu666.com.app;

public class UserState {
    public static final String USER_STATUS_ORDERED = "ordered";// 预约状态
    public static final String USER_STATUS_READY = "ready";// 空虚状态
    public static final String USER_STATUS_USE_CAR = "useCar";// 用车状态
    public static final String USER_STATUS_EXPERIENCE = "experience";// 体验用户
    public static final String USER_STATUS_RETURN = "return";// 还车状态
    public static final String USER_STATUS_FINISH = "finish";// 完成状态

    // public static final String User_Status_Cancel="cancel";//取消状态
    // public static final String User_Status_PlanReturn= "planReturn";//计划还车状态
    // public static final String User_Status_WaitQueue= "waitQueue";//排队等待状态
    // public static final String User_Status_Wait= "wait";//等待用车状态

    public static final String NO_PAY = "noPay";//
    public static final String PAY_FINISH = "finish";//

    public static final String PAY_BOUND = "bound";//

    private static final String[] time_rent_hava_car = {PAY_BOUND,
            USER_STATUS_USE_CAR, USER_STATUS_RETURN};

    public static boolean isTimeRent_car(String state) {
        if (state == null)
            return false;
        return ArrayUtils.contains(time_rent_hava_car, state);
    }

    private static final String[] time_share_no_car = {USER_STATUS_EXPERIENCE,
            USER_STATUS_READY};

    public static boolean isHaveNocar(String state) {
        if (state == null)
            return false;
        return ArrayUtils.contains(time_share_no_car, state);
    }

    private final static String[] neetRevivalStates = {USER_STATUS_ORDERED,
            USER_STATUS_USE_CAR, USER_STATUS_RETURN};

    private final static String[] neetReturnStates = {
            USER_STATUS_USE_CAR, USER_STATUS_RETURN};

    private final static String[] getCarPage = {USER_STATUS_ORDERED,
            USER_STATUS_USE_CAR};

    private final static String[] idelState = {
            USER_STATUS_READY, USER_STATUS_FINISH, USER_STATUS_EXPERIENCE};

    public static boolean isNeedRevival(String state) {
        if (state == null)
            return false;
        return ArrayUtils.contains(neetRevivalStates, state);
    }

    public static boolean isOrdering(String state) {
        if (state == null)
            return false;
        return state.equals(USER_STATUS_ORDERED);
    }

    public static boolean isUsingCar(String state) {
        if (state == null)
            return false;
        return state.equals(USER_STATUS_USE_CAR);
    }

    public static boolean isNeedReturn(String state) {
        if (state == null)
            return false;
        return ArrayUtils.contains(neetReturnStates, state);
    }

    public static boolean isInIdel(String state) {
        if (state == null)
            return false;
        return ArrayUtils.contains(idelState, state);
    }

    public static boolean isGetCarPage(String state) {
        if (state == null)
            return false;
        return ArrayUtils.contains(getCarPage, state);
    }

    public static boolean isReturnPage(String state) {
        if (state == null)
            return false;
        return state.equals(USER_STATUS_RETURN);
    }

    private static String[] level = new String[]{"一", "二", "三", "四", "五"};
}
