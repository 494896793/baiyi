package www.qisu666.com.utils;

import android.os.Environment;

import com.alibaba.fastjson.JSONObject;
import www.qisu666.com.callback.SecData;
import www.qisu666.com.callback.UserInfoResp;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

/**
 * 描述：文件操作类.
 *
 * @author zhaoqp
 * @version v1.0
 * @date 2011-12-10
 */
public class AbFileUtil {

    /**
     * The tag.
     */
    private static String TAG = "AbFileUtil";

    /** The Constant D. */

    /**
     * 默认下载文件地址.
     */
    public static String downPathRootDir = File.separator + "download"
            + File.separator;

    /**
     * 默认下载图片文件地址.
     */
    public static String downPathImageDir = File.separator + "download"
            + File.separator + "cache_images" + File.separator;

    /**
     * 默认下载文件地址.
     */
    public static String downPathFileDir = File.separator + "download"
            + File.separator + "cache_files" + File.separator;

    private static String responseData = "";

    /**
     * HTTP文件上传.
     *
     * @param actionUrl 要使用的URL
     * @param params    表单参数
     * @param files     要上传的文件列表
     * @return http返回的结果 或http响应码
     * the ab app exception
     */
    public static String postFile(String actionUrl,
                                  Map<String, String> params, Map<String, File> files, String token) {
        responseData = "";
        // 标识每个文件的边界
        String BOUNDARY = java.util.UUID.randomUUID().toString();
        String PREFIX = "--";
        String LINEND = "\r\n";
        String MULTIPART_FROM_DATA = "multipart/form-data";
        String CHARSET = "UTF-8";
        HttpURLConnection conn = null;
        DataOutputStream outStream = null;
//		String retStr = "200";
        if (actionUrl != null) {

        }
        try {
            String method = params.get("method");
            Logger.i(TAG, "method=" + method);
            URL uri;
            if (method.contains("http")) {
                uri = new URL(method);
            } else {
                uri = new URL(actionUrl + params.get("method"));
            }

            conn = (HttpURLConnection) uri.openConnection();
            // 允许输入
            conn.setDoInput(true);
            // 允许输出
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            // Post方式
            conn.setRequestMethod("POST");
//            if (MyApplication.isLoginSuccess && !TextUtils.isEmpty(application.getUserInfo().getToken())) {
////            params.addHeader("authorization", "Token " + application.getUserInfo().getToken());
//                params.setHeader("token", application.getUserInfo().getToken());
//            }

            conn.setConnectTimeout(300 * 1000);
            conn.setReadTimeout(300 * 1000);
            // 设置request header 属性
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("token", token);//增加token校验
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA
                    + ";boundary=" + BOUNDARY);
//			conn.connect();
            // 组装表单参数数据
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                sb.append(PREFIX);
                sb.append(BOUNDARY);
                sb.append(LINEND);
                sb.append("Content-Disposition: form-data; name=\""
                        + entry.getKey() + "\"" + LINEND);
                sb.append("Content-Type: text/plain; charset=" + CHARSET
                        + LINEND);
                sb.append("Content-Transfer-Encoding: 8bit" + LINEND);
                sb.append(LINEND);
                sb.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                sb.append(LINEND);
            }
            outStream = new DataOutputStream(conn.getOutputStream());
            outStream.write(sb.toString().getBytes());
            // 发送文件数据
            if (files != null)
                for (Map.Entry<String, File> file : files.entrySet()) {
                    // 获取连接发送参数数据
                    StringBuilder sb1 = new StringBuilder();
                    sb1.append(PREFIX);
                    sb1.append(BOUNDARY);
                    sb1.append(LINEND);
                    Logger.i("文件name = " + file.getKey());
                    Logger.i("文件filename = " + file.getValue().getName());
                    sb1.append("Content-Disposition: form-data; name=\"" + file.getKey() + "\"; filename=\""
                            + file.getValue().getName() + "\"" + LINEND);
                    sb1.append("Content-Type: application/octet-stream; charset="
                            + CHARSET + LINEND);
                    sb1.append(LINEND);
                    // 请求头结束至少有一个空行（即有两对\r\n）表示请求头结束了
                    outStream.write(sb1.toString().getBytes());
                    InputStream is = new FileInputStream(file.getValue());
                    byte[] buffer = new byte[1024];
                    int len = 0;
                    while ((len = is.read(buffer)) != -1) {
                        outStream.write(buffer, 0, len);
                    }
                    is.close();
                    // 一个文件结束一个空行
                    outStream.write(LINEND.getBytes());
                }
            // 请求结束的边界打印
            byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
            outStream.write(end_data);
            outStream.flush();
            outStream.close();
            // 获取响应码

            int res = conn.getResponseCode();
            Logger.e("图片上传code=" + res);
            if (res == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        conn.getInputStream(), CHARSET));
                // 数据
                String retData = null;
                responseData = "";
                while ((retData = in.readLine()) != null) {
                    responseData += retData;
                }
                in.close();
            } else if (res == 413) {
                SecData data = new SecData();
                data.setCode("413");
                data.setMsg("图片太大了！");
                UserInfoResp info = new UserInfoResp();
                data.setResult(info);
                responseData = JSONObject.toJSONString(data);
            }
            outStream.close();
            conn.disconnect();
            return responseData.toString();
        } catch (Exception e) {
            Logger.i(e.getMessage());
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return responseData.toString();
    }

    /**
     * 描述：从sd卡中的文件读取到byte[].
     *
     * @param path sd卡中文件路径
     * @return byte[]
     */
    public static byte[] getByteArrayFromSD(String path) {
        byte[] bytes = null;
        ByteArrayOutputStream out = null;
        try {
            File file = new File(path);
            // SD卡是否存在
            if (!isCanUseSD()) {
                return null;
            }
            // 文件是否存在
            if (!file.exists()) {
                return null;
            }

            long fileSize = file.length();
            if (fileSize > Integer.MAX_VALUE) {
                return null;
            }

            FileInputStream in = new FileInputStream(path);
            out = new ByteArrayOutputStream(1024);
            byte[] buffer = new byte[1024];
            int size = 0;
            while ((size = in.read(buffer)) != -1) {
                out.write(buffer, 0, size);
            }
            in.close();
            bytes = out.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (Exception e) {
                }
            }
        }
        return bytes;
    }

    /**
     * 描述：将byte数组写入文件.
     *
     * @param path    the path
     * @param content the content
     * @param create  the create
     */
    public static void writeByteArrayToSD(String path, byte[] content,
                                          boolean create) {

        FileOutputStream fos = null;
        try {
            File file = new File(path);
            // SD卡是否存在
            if (!isCanUseSD()) {
                return;
            }
            // 文件是否存在
            if (!file.exists()) {
                if (create) {
                    File parent = file.getParentFile();
                    if (!parent.exists()) {
                        parent.mkdirs();
                        file.createNewFile();
                    }
                } else {
                    return;
                }
            }
            fos = new FileOutputStream(path);
            fos.write(content);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (Exception e) {
                }
            }
        }
    }

    /**
     * 描述：SD卡是否能用.
     *
     * @return true 可用,false不可用
     */
    public static boolean isCanUseSD() {
        try {
            return Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 描述：获得当前下载的地址.
     *
     * @return 下载的地址（默认SD卡download）
     */
    public static String getDownPathImageDir() {
        return downPathImageDir;
    }

    /**
     * 描述：设置图片文件的下载保存路径（默认SD卡download/cache_images）.
     *
     * @param downPathImageDir 图片文件的下载保存路径
     */
    public static void setDownPathImageDir(String downPathImageDir) {
        AbFileUtil.downPathImageDir = downPathImageDir;
    }

    /**
     * Gets the down path file dir.
     *
     * @return the down path file dir
     */
    public static String getDownPathFileDir() {
        return downPathFileDir;
    }

    /**
     * 描述：设置文件的下载保存路径（默认SD卡download/cache_files）.
     *
     * @param downPathFileDir 文件的下载保存路径
     */
    public static void setDownPathFileDir(String downPathFileDir) {
        AbFileUtil.downPathFileDir = downPathFileDir;
    }

    /**
     * 描述：获取默认的图片保存全路径.
     *
     * @return the default image down path dir
     */
    public static String getDefaultImageDownPathDir() {
        String pathDir = null;
        try {
            if (!isCanUseSD()) {
                return null;
            }
            // 初始化图片保存路径
            File fileRoot = Environment.getExternalStorageDirectory();
            File dirFile = new File(fileRoot.getAbsolutePath()
                    + AbFileUtil.downPathImageDir);
            if (!dirFile.exists()) {
                dirFile.mkdirs();
            }
            pathDir = dirFile.getPath();
        } catch (Exception e) {
        }
        return pathDir;
    }

}
