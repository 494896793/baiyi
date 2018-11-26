package www.qisu666.com.utils;

/**
 * 身份证格式校验
 */
public class IDValidator {
	private final int[] xishu = new int[] { 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7,
			9, 10, 5, 8, 4, 2 };

	private final char[] last = new char[] { '1', '0', 'X', '9', '8', '7', '6',
			'5', '4', '3', '2' };

	private String id;

	public IDValidator(String id) {
		this.id = id;
	}

	public boolean validate() {
		if (null == id || id.length() != 18) {
			return false;
		}
		char[] string = id.toCharArray();
		int sum = 0;
		for (int i = 0; i < string.length - 1; i++) {
			sum += (string[i] - '0') * xishu[i];
		}
		return last[sum % 11] == string[17];
	}


}
