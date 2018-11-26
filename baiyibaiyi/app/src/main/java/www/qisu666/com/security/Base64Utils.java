package www.qisu666.com.security;

import android.util.Base64;

import java.io.UnsupportedEncodingException;


/**
 * 封装Base64的工具类
 * 
 */
public class Base64Utils {

	public final static String ENCODING = "UTF-8";

	// 加密 : string -> string
	public static String encoded(String data) throws UnsupportedEncodingException {
		byte[] b = Base64.encode(data.getBytes(ENCODING), Base64.DEFAULT);
		return new String(b, ENCODING);
	}

	// 加密,遵循RFC标准
	public static String encodedSafe(String data) throws UnsupportedEncodingException {
		byte[] b = Base64.encode(data.getBytes(ENCODING), Base64.URL_SAFE);
		return new String(b, ENCODING);
	}

	// 解密
//	public static String decode(String data)
//			throws UnsupportedEncodingException {
//		byte[] b = Base64.decodeBase64(data.getBytes(ENCODING));
//		return new String(b, ENCODING);
//	}
	
	// 加密 : byte[] -> byte[]
	public static String encode(byte[] data) throws UnsupportedEncodingException {
		return new String(Base64.encode(data, Base64.DEFAULT), ENCODING);
	}
	
	// 解密 : string-> byte[]
	public static byte[] decode(String data) throws UnsupportedEncodingException {
		return Base64.decode(data.getBytes(ENCODING), Base64.DEFAULT);
	}

	/**
	 * 测试类
	 */
	public static void main(String[] args) throws UnsupportedEncodingException {
		
		//String str = "abc123";
		
//		// 加密该字符串
//		String encodedString = Base64Utils.encodedSafe(str);
//		System.out.println(encodedString);
//		
//		// 解密该字符串
//		String decodedString = Base64Utils.decode(encodedString);
//		System.out.println(decodedString);
	}

}