package www.qisu666.com.utils;

/**
 *
 */
public class HighlightUtil {

    /**
     * @param target 要高亮的字符串
     * @param tag    高亮的字段
     * @param color  十六进制颜色
     * @return 改后的字符串
     */
    public static String convertHightlightText(String target, String tag, String color) {
        if (target == null || target.trim().length() == 0 || tag == null || tag.trim().length() == 0) {
            return target;
        }
        target = target.replace(tag, "<font color='" + color + "'>" + tag + "</font>");
        return target;
    }
}
