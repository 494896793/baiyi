package www.qisu666.com.root;

import www.qisu666.com.request.RequestBaseParams;

import java.io.File;
import java.util.List;

public interface IBase {

    void setView();

    void initDatas();

    <T> List<T> getList(String result, Class<T> clazz);

    <T> T getBean(String result, Class<T> clazz);

    boolean isSuccess(String result);

    String getMsg(String result);

    String getCode(String result);

    // 联网请求的操作
    void doGet(RequestBaseParams param, final int type,
               String title, final boolean dialog);

    void doPost(RequestBaseParams param, File mFile, final int type,
                String title, final boolean dialog);

    // 请求成功或失败的操作
    void onComplete(String result, int type);

    void onFailure(String msg, int type);

    String request_wrong = "加载失败";
}
