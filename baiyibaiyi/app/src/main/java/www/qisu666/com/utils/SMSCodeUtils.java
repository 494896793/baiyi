package www.qisu666.com.utils;

/**
 * Created by baby on 2016/9/5.
 * <p>
 * 佰壹出行，天天速达
 */

public class SMSCodeUtils {
    public static String code(String message) {
        Logger.e("验证码--------------->" + message);//【】您的验证码是9496。如非本人操作，请忽略本短信
        String code = message.substring(message.indexOf("您的验证码是") + 6, message.indexOf("。如非本人操作"));
        Logger.e("code--------------->" + code);
        return code;
    }
}
