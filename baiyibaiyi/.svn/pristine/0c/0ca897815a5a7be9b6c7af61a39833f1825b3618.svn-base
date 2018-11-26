package www.qisu666.com.utils;

/**
 * Created by Lver on 2016/3/28.
 */
public class CheckSum {

    /**
     * Constant for the maximum {@code short} value, 2<sup>11</sup>-1.
     */
    public static final short MAX_VALUE = (short) 0xFFF;

    /**
     * Constant for the minimum {@code short} value, 0.
     */
    public static final short MIN_VALUE = (short) 0x00;


    public static String checkSumLenght(short len) {
        if (len <= MAX_VALUE && len >= MIN_VALUE) {
            byte[] blen = ByteUtils.shortToBytes(len);
            byte h2 = (byte) (blen[0] & 0x0F);  /*高字节低4bit*/
            byte l1 = (byte) (blen[1] >> 4);	/*低字节高4bit*/
            byte l2 = (byte) (blen[1] & 0x0F);	/*低字节低4bit*/
            return ByteUtils.toHexString((byte) ((((~(h2 + l1 + l2) + 1) & 0x0F) << 4) | blen[0])) + ByteUtils.toHexString(blen[1]);
        }
        return null;
    }


    public static String checkSumInfo(String data) {
        if (StringUtils.isEmpty(data))
            return null;
        byte[] bytes = data.getBytes();
        int sum = 0;
        for (byte b : bytes) {
            sum += b;
        }
        return Integer.toHexString(((~sum) & 0xFFFF) + 1).toUpperCase();

    }


    /**
     * 00H	正常
     * 01H	协议版本错
     * 02H	CHKSUM错
     * 03H	LCHKSUM错	LCHKSUM参见7.3.2
     * 04H	CID2无效
     * 05H	命令格式错
     * 06H	无效数据
     * E0H	无效权限
     * E1H	操作失败
     * E2H	设备故障
     * E3H	设备写保护	不能设置参数
     * E4H-EFH	保留	用户自定义
     *
     * @param args
     */

    public static void main(String[] args) {
        CheckSum.checkSumLenght((short) 18);
    }
}
