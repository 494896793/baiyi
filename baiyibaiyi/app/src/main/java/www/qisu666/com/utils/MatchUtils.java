package www.qisu666.com.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MatchUtils {
	
	public static boolean matchMobileNo(String str){
		Pattern pattern = Pattern.compile("^(13[0-9]|14[0-9]|15[0-9]|18[0-9]|17[0-9])\\d{8}$");
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}

}
