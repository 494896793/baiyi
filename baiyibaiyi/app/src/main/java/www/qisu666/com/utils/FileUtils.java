package www.qisu666.com.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {
    public static String[] getChildFolderNames(String startFolder) {
        List<String> folderNames = new ArrayList<String>();

        File aFolder = new File(startFolder);
        File[] allFiles = aFolder.listFiles();
        for (File f : allFiles) {
            if (f.isDirectory()) {
                folderNames.add(f.getName());
            }
        }

        return folderNames.toArray(new String[0]);
    }

    public static boolean isFileExists(String path) {
        File file = new File(path);
        try {
            if (file.isFile() && file.exists()) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public static void deleteFile(String path) {
        File file = new File(path);
        try {
            if (file.isFile() && file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
        }
    }

    public static void delFolder(String folderPath) {
        try {
            delAllFile(folderPath);
            String filePath = folderPath;
            filePath = filePath.toString();
            java.io.File myFilePath = new java.io.File(filePath);
            myFilePath.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void delAllFile(String path) {
        File file = new File(path);

        if (!file.isDirectory()) {
            return;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + File.separator + tempList[i]);
                delFolder(path + File.separator + tempList[i]);
            }
        }
    }

    public static String getFilePath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED); // determine whether sd
        // card is exist
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();
        } else {
            sdDir = Environment.getRootDirectory();
        }
        return sdDir.toString();
    }

    /**
     * readFile
     *
     * @param fileName
     * @param isSDCard
     * @return read content
     */
    public static String readFile(Context context, String fileName,
                                  boolean isSDCard) {
        FileInputStream fis = null;
        byte[] data = null;
        try {
            if (isSDCard) {
                File file = new File(Environment.getExternalStorageDirectory(),
                        fileName);
                fis = new FileInputStream(file);
            } else {
                fis = context.openFileInput(fileName);
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int len = 0;

            while ((len = fis.read(buf)) != -1) {
                baos.write(buf, 0, len);
            }

            data = baos.toByteArray();

            baos.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fis.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            fis = null;
        }

        return data.toString();
    }

    public static String read2array(String fileName) throws Exception {
        try {
            File file = new File(fileName);
            FileInputStream in = new FileInputStream(file);
            byte[] buffer = new byte[(int) file.length() + 100];
            int length = in.read(buffer);

            String data = Base64.encodeToString(buffer, 0, length,
                    Base64.DEFAULT);
            if (in != null) {
                try {
                    in.close();
                } catch (Exception e) {

                }
            }
            return data;
        } catch (Exception e) {

        }

        return "";
    }

    // ����λ/K
    public static long getFileSizes(File f) throws Exception {// ȡ���ļ���С
        long s = 0;
        if (f.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(f);
            s = fis.available() / 1024;
            fis.close();
        } else {
            return 0;
        }
        return s;
    }

	/*
     * public static byte[] read2array(String file) throws Exception { Exception
	 * ex = null; InputStream in = null; byte[] out = new byte[0];
	 * 
	 * try { in = new BufferedInputStream(new FileInputStream(file)); // the
	 * length of a buffer can vary int bufLen = 20 * 1024; byte[] buf = new
	 * byte[bufLen]; byte[] tmp = null; int len = 0;
	 * 
	 * while ((len = in.read(buf, 0, bufLen)) != -1) { // extend array tmp = new
	 * byte[out.length + len];
	 * 
	 * // copy data System.arraycopy(out, 0, tmp, 0, out.length);
	 * System.arraycopy(buf, 0, tmp, out.length, len); out = tmp; tmp = null; }
	 * buf = null; } catch (Exception e) { ex = e; } finally { // always close
	 * the stream if (in != null) { try { in.close(); } catch (Exception e) { }
	 * }
	 * 
	 * if (ex != null) { throw ex; } } return out; }
	 */
}

//class FileFilter implements FilenameFilter {
//	@Override
//	public boolean accept(File dir, String fileName) {
//		return fileName.endsWith(".txt");
//	}
//}
