package www.qisu666.com.utils;

public class ByteUtils {
    // TODO: Remove once native methods are in place.
    private static final char REPLACEMENT_CHAR = (char) 0xfffd;

    public static byte[] intToBytes(int n) {
        return intToBytes(n, new byte[4], 0);
    }

    public static byte[] intToBytes(int n, byte[] bytes) {
        return intToBytes(n, bytes, 0);
    }

    public static byte[] intToBytes(int n, byte[] bytes, int offset) {
        bytes[offset + 3] = (byte) (n & 0xFF);
        bytes[offset + 2] = (byte) ((n >> 8) & 0xFF);
        bytes[offset + 1] = (byte) ((n >> 16) & 0xFF);
        bytes[offset] = (byte) ((n >> 24) & 0xFF);

        return bytes;
    }

    public static byte[] shortToBytes(short n) {
        return shortToBytes(n, new byte[2], 0);
    }

    public static byte[] shortToBytes(short n, byte[] bytes) {
        return shortToBytes(n, bytes, 0);
    }

    public static byte[] shortToBytes(short n, byte[] bytes, int offset) {
        bytes[offset + 1] = (byte) (n & 0xFF);
        bytes[offset] = (byte) ((n >> 8) & 0xFF);

        return bytes;
    }

    public static int bytesToInt(byte[] bytes) {
        return bytesToInt(bytes, 0);
    }

    public static int bytesToInt(byte[] bytes, int offset) {
        return (bytes[offset] & 0XFF << 24) | (bytes[offset + 1] & 0XFF << 16) | (bytes[offset + 2] & 0XFF << 8)
                | bytes[offset + 3] & 0XFF;
    }

    public static int bytesToShort(byte[] bytes) {
        return bytesToShort(bytes, 0);
    }

    public static int bytesToShort(byte[] bytes, int offset) {
        return (bytes[offset + 1] & 0XFF) | ((bytes[offset] & 0XFF) << 8);
    }


    public static String short2String(short s) {

        return toHexString(shortToBytes(s));
    }

    public static String short2Stringl(short s) {
        return toHexString(shortToBytes(s)[1]);
    }

    public static String int2String(int s) {

        return toHexString(intToBytes(s));
    }

    public static String intBit2String(int s) {
        return toHexString2(intToBytes(s));
    }

    public static String float2String(float s) {
        return intBit2String(Float.floatToIntBits(s));
    }


    public static float ascString2Float(String data) {
        if (data.length() == 8) {
            byte l1 = ascString2byte(data.substring(0, 2));
            byte l2 = ascString2byte(data.substring(2, 4));
            byte h1 = ascString2byte(data.substring(4, 6));
            byte h2 = ascString2byte(data.substring(6, 8));
            return Float.intBitsToFloat(((h2 & 0XFF) << 24) | ((h1 & 0XFF) << 16) | ((l2 & 0XFF) << 8) | l1 & 0XFF);
        }
        return 0;
    }

    public static byte ascString2byte(String data) {
        return Byte.parseByte(data, 16);
    }

    public static int ascString2Int(String data) {
        return Integer.parseInt(data, 16);
    }

    public static short ascString2Short(String data) {
        return Short.parseShort(data, 16);
    }

    public static String ascString2String(String data) {
        StringBuffer sb = new StringBuffer();
        char[] chars = data.toCharArray();
        char[] temp = new char[2];
        for (int i = 0; i < chars.length; i++) {
            if (i % 2 == 0) {
                temp[0] = chars[i];
            } else {
                temp[1] = chars[i];
                sb.append((char) ascString2Int(new String(temp)));
            }
        }
        return sb.toString();
    }

    public static String toHexString(byte b) {
        String hex = Integer.toHexString(b & 0xFF).toUpperCase();
        if (hex.length() == 1) {
            return "0" + hex;
        } else {
            return hex;
        }
    }

    public static String toHexString(byte[] bytes) {
        StringBuffer value = new StringBuffer();

        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() == 1) {
                hex = "0" + hex;
            }
            value.append(hex.toUpperCase());
        }
        return value.toString();
    }

    public static String toHexString2(byte[] bytes) {
        StringBuffer value = new StringBuffer();

        for (int i = bytes.length - 1; i >= 0; i--) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() == 1) {
                hex = "0" + hex;
            }
            value.append(hex.toUpperCase());
        }
        return value.toString();
    }

    public static byte[] toBytes(String hexString) {
        if (hexString.length() % 2 != 0) {
            hexString = "0" + hexString;
        }

        byte[] bytes = new byte[hexString.length() / 2];
        for (int i = 0; i < hexString.length(); i += 2) {
            bytes[i / 2] = (byte) (Integer.parseInt(hexString.substring(i, i + 2), 16) & 0xFF);
        }

        return bytes;
    }

    //byte转换为char：
    public static char byteToChar(byte[] b) {
        char c = (char) (((b[0] & 0xFF) << 8) | (b[1] & 0xFF));
        return c;
    }

}
