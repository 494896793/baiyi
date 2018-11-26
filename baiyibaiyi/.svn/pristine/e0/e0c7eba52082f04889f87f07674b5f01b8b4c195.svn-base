package www.qisu666.com.utils;

import android.app.ProgressDialog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadUtils {
    /**
     * 下载任务
     *
     * @param path           远程路径
     * @param filePath       本地文件路径
     * @param progressDialog
     * @return 返回下载文件
     * @throws Exception
     */
    public static File getFile(String path, String filePath,
                               ProgressDialog progressDialog) throws Exception {
        URL url = new URL(path);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url
                .openConnection();
        httpURLConnection.setConnectTimeout(2000);
        httpURLConnection.setRequestMethod("GET");
        if (httpURLConnection.getResponseCode() == 200) {
            int total = httpURLConnection.getContentLength();
            progressDialog.setMax(total);
            // 获取远程apk解析成输入流
            InputStream is = httpURLConnection.getInputStream();
            // 创建本地文件�?
            File file = new File(filePath);
            FileOutputStream fos = new FileOutputStream(file);
            // 缓存用于存储下载数据 交换区空�?
            byte[] buffer = new byte[1024];
            int len;
            int process = 0;
            // 判断buffer
            while ((len = is.read(buffer)) != -1) {
                // 把交互区数据写入fos文件即本地卡内存文件
                fos.write(buffer, 0, len);
                // 进度条长度加�?
                process += len;
                // 变化精度�?
                progressDialog.setProgress(process);
                float all = (float) total / 1024 / 1024;
                float percent = (float) process / 1024 / 1024;
                Logger.d("down------------------------->" + all + "," + percent);
                progressDialog.setProgressNumberFormat(String.format("%.2fM/%.2fM", percent, all));
            }
            fos.flush();
            fos.close();
            is.close();
            return file;
        }
        return null;
    }

    /**
     * 下载任务
     *
     * @param path           远程路径
     * @param filePath       本地文件路径
     * @param progressDialog
     * @return 返回下载文件
     * @throws Exception
     */
//    public static File getFile(String path, String filePath, UpdatePresenter progressDialog) throws Exception {
//        URL url = new URL(path);
//        HttpURLConnection httpURLConnection = (HttpURLConnection) url
//                .openConnection();
//        httpURLConnection.setConnectTimeout(30*1000);
//        httpURLConnection.setRequestMethod("GET");
//        if (httpURLConnection.getResponseCode() == 200) {
//            int total = httpURLConnection.getContentLength();
//            // 获取远程apk解析成输入流
//            InputStream is = httpURLConnection.getInputStream();
//            // 创建本地文件�?
//            File file = new File(filePath);
//            FileOutputStream fos = new FileOutputStream(file);
//            // 缓存用于存储下载数据 交换区空�?
//            byte[] buffer = new byte[1024];
//            int len;
//            int process = 0;
//            // 判断buffer
//            while ((len = is.read(buffer)) != -1) {
//                // 把交互区数据写入fos文件即本地卡内存文件
//                fos.write(buffer, 0, len);
//                // 进度条长度加�?
//                process += len;
//                // 变化精度�?
//                double all = (double) total / 1024 / 1024;
//                double percent = (double) process / 1024 / 1024;
//                Logger.d("down------------------------->" + all + "," + percent);
//                progressDialog.onPregressUpdate(percent / all);
//            }
//            fos.flush();
//            fos.close();
//            is.close();
//            return file;
//        }
//        return null;
//    }

}
