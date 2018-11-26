package www.qisu666.com.utils;

/**
 * Created by Administrator on 2016/6/15.
 */
public class NaviErrorUtils {

    public static String errorMsg(int rCode){
        String msg = null;
        switch (rCode){
            case 2:
            case 15:
                msg = "请检查您的网络是否通畅，稍候再试";
                break;
            case 3:
                msg = "起点位置必须在国内，请对起点位置进行调整";
                break;
            case 6:
                msg = "终点位置必须在国内，请对终点位置进行调整";
                break;
            case 4:
                msg = "解析错误，请稍后再试";
                break;
            case 10:
                msg = "起点附近没有找到可行道路，请对起点位置进行调整";
                break;
            case 11:
                msg = "终点附近没有找到可行道路，请对终点位置进行调整";
                break;
            case 12:
                msg = "途经点附近没有找到可行道路，请对途经点进行调整";
                break;
            case 14:
                msg = "请求的服务不存在，请稍后再试";
                break;
            case 16:
                msg = "无权限访问此服务，请稍后再试";
                break;
        }
        return msg;
    }

}
