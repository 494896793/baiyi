package www.qisu666.com.utils;

import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author ngh
 *         AES128 算法
 *         <p/>
 *         CBC 模式
 *         <p/>
 *         PKCS7Padding 填充模式
 *         <p/>
 *         CBC模式需要添加一个参数iv
 *         <p/>
 *         介于java 不支持PKCS7Padding，只支持PKCS5Padding 但是PKCS7Padding 和 PKCS5Padding 没有什么区别
 *         要实现在java端用PKCS7Padding填充，需要用到bouncycastle组件来实现
 */
@SuppressWarnings("ALL")
public class AES {
    // 算法名称
    final String KEY_ALGORITHM = "AES";
    // 加解密算法/模式/填充方式
    final String algorithmStr = "AES/CBC/PKCS5Padding";
    //
    private Key key;
    private Cipher cipher;

    private static final byte[] iv = "kissxiexin990918".getBytes();
//    byte[] iv = "0102030405060708".getBytes();

    private volatile static AES instance;

    public static AES getInstance() {
        if (instance == null) {
            synchronized (AES.class) {
                instance = new AES();
            }
        }
        return instance;
    }

    private boolean init(String keyStr) {
        if (keyStr == null || keyStr.isEmpty()) {
            return false;
        }
        byte[] keyBytes;
        try {
            keyBytes = keyGenForSerial(keyStr);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        Logger.e("key:" + new String(keyBytes));
        return init(keyBytes);
    }

    public boolean init(byte[] keys) {
        if (keys == null || keys.length <= 0) {
            return false;
        }

        // 如果密钥不足16位，那么就补足.  这个if 中的内容很重要
        int base = 16;
        if (keys.length % base != 0) {
            int groups = keys.length / base + (keys.length % base != 0 ? 1 : 0);
            byte[] temp = new byte[groups * base];
            Arrays.fill(temp, (byte) 0);
            System.arraycopy(key, 0, temp, 0, keys.length);
            keys = temp;
        }
        // 转化成JAVA的密钥格式
        key = new SecretKeySpec(keys, KEY_ALGORITHM);
        try {
            // 初始化cipher
            cipher = Cipher.getInstance(algorithmStr);
            return true;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 加密方法
     *
     * @param content 要加密的字符串
     * @param keyStr  加密密钥
     * @return
     */
    synchronized public byte[] encrypt(byte[] content, String keyStr) {
        byte[] encryptedText = null;
        init(keyStr);
        System.out.println("IV：" + new String(iv));
        try {
            cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
            encryptedText = cipher.doFinal(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encryptedText;
    }

    /**
     * 加密方法
     *
     * @param content 要加密的字符串
     * @param keys    加密密钥
     * @return
     */
    public byte[] encrypt(byte[] content, byte[] keys) {
        byte[] encryptedText = null;
        init(keys);
        System.out.println("IV：" + ByteUtils.toHexString(iv));

        try {
            cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
            encryptedText = cipher.doFinal(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encryptedText;
    }

    /**
     * 解密方法
     *
     * @param encryptedData 要解密的字符串
     * @param keyStr        解密密钥
     * @return
     */
    synchronized public byte[] decrypt(byte[] encryptedData, String keyStr) {
        byte[] encryptedText = null;
        init(keyStr);
        System.out.println("IV：" + new String(iv));
        try {
            cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
            encryptedText = cipher.doFinal(encryptedData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encryptedText;
    }

    /**
     * 解密方法
     *
     * @param encryptedData 要解密的字符串
     * @param keys          解密密钥
     * @return
     */
    public byte[] decrypt(byte[] encryptedData, byte[] keys) {
        byte[] encryptedText = null;
        init(keys);
        System.out.println("IV：" + new String(iv));
        try {
            cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
            encryptedText = cipher.doFinal(encryptedData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encryptedText;
    }


    /**
     * 将二进制转换成16进制
     *
     * @param buf
     * @return
     */
    public static String parseByte2HexStr(byte buf[]) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * 将16进制转换为二进制
     *
     * @param hexStr
     * @return
     */
    public static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1)
            return null;
        hexStr = hexStr.replaceAll("[^\\w]+", "");
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }

    public static byte[] keyGenForSerial(String elpn) {
        if (elpn != null && elpn.length() > 8) {
            elpn = elpn.toLowerCase();
            elpn = elpn + elpn.substring(elpn.length() - (16 - elpn.length()), elpn.length());
            try {
                byte[] temp = elpn.getBytes("UTF-8");
                byte[] pwd = new byte[16];
                for (int i = 0; i < 16; i++) {
                    pwd[i] = (byte) (temp[i] | iv[i]);
                }
                return pwd;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    public static void main(String[] args) {
//        String s = "U2FsdGVkX1+LoUm2xjavqu8i9W2ZEfw/SaSFf2teh0PKhvgAxmjbLy3SCwBDg+av";
//        byte[] contentx = s.getBytes();
        AES aes = new AES();
        byte[] password = keyGenForSerial("001583500E3B");
        try {
            System.out.println("密码：" + new String(password, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
//        byte[] decryptResult = decrypt(contentx, password);
//        System.out.println("解密后：" + new String(decryptResult));
//        String encryptStr = "8BF3CD0ED21CA4DCD52E707B6D9450D1D0592B02CEB3A6EF891D2EACCC1D0C52";
//        byte[] encryptBytes = parseHexStr2Byte(encryptStr);
//        //加密
        String content = "yjs+bb001583500e3b";
//        System.out.println("加密前：" + content);
        byte[] encryptResult = aes.encrypt(content.getBytes(), password);
        String encryptResultStr = parseByte2HexStr(encryptResult);
        System.out.println("加密后：" + encryptResultStr);
        //解密
//        byte[] decryptFrom = parseHexStr2Byte(encryptStr);
        byte[] decryptResult = aes.decrypt(encryptResult, password);
        System.out.println("解密后：" + new String(decryptResult));
    }
}