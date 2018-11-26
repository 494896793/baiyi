package www.qisu666.com.sdk.stationMap;

/**
 * 717219917@qq.com ${DATA} 18:04.
 */

/**对高德地图的请求封装 */

import android.content.Context;
import android.os.Handler;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonStringRequest;
import www.qisu666.com.app.MyApplication;
import www.qisu666.com.net.ResponseCallBack;
import www.qisu666.com.net.ResponseListener;
import www.qisu666.com.request.utils.Config;
import www.qisu666.com.security.Base64Utils;
import www.qisu666.com.utils.HttpRequestManager;
import www.qisu666.com.utils.JsonUtils;
import www.qisu666.com.utils.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.util.LogUtil;

import java.io.UnsupportedEncodingException;
import java.util.Map;



//http逻辑  加密等操作
public class HttpLogic_amap implements ResponseListener {

    private Handler handler;
    private ResponseCallBack callBack;
//    private LoadingDialog dialog;

    private Context context;

    private JsonStringRequest request;

    //解密后的响应字符串
    private String result;
    private String tag;

    public HttpLogic_amap(Context context){
        handler = new Handler(MyApplication.getApplication().getMainLooper());
        this.context = context;
    }

    @Override
    public void onResponse(String response) {

        try {
            if(response!=null && response.length()>0) {
                result = new String(Base64Utils.decode(response));
                LogUtils.v(tag+" response:"+result);
                final Map<String,Object> responseMap = JsonUtils.jsonToMap(result);
                String return_code = responseMap.get("return_code").toString();

                //返回码为0000时，或者部分需单独处理的协议号
                if(tag.equals("D107") || tag.equals("A106") || return_code.equals("0000")){
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(callBack!=null){
                                callBack.onResponse(responseMap, tag);
                            }
                        }
                    });
                } else if(return_code.equals("1001")){
                    LogUtil.e("1001异常"+responseMap.get("return_msg").toString());
                } else{
                    LogUtil.e("其他异常"+responseMap.get("return_msg").toString());
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(VolleyError error) {
//        if(error!=null){
////            LogUtil.e("VolleyError:"+error.getMessage());
////            if(error.networkResponse!=null){
//////                LogUtil.e("VolleyError:"+error.networkResponse.statusCode);
////            }
////            LogUtil.e("VolleyError:"+error.networkResponse.statusCode);
////            ToastUtil.showToast(R.string.toast_network_server_outage);
//        } else {
////            ToastUtil.showToast(R.string.toast_network_interrupt);
//        }

        if(callBack!=null){
            handler.post(new Runnable() {
                @Override
                public void run() {
                    callBack.onError(tag);
                }
            });
        }
    }

    /**
     * 发送请求
     * @param url
     * @param jsonObject
     * @param callBack
     */
    public void sendRequest(String url, JSONObject jsonObject, ResponseCallBack callBack){
        sendRequest(url, jsonObject, true, true, "gif", callBack);
    }

    /**
     * 发送请求
     * @param url
     * @param jsonObject
     * @param flag 是否显示LoadingDialog
     * @param callBack
     */
    public void sendRequest(String url, JSONObject jsonObject, boolean flag, ResponseCallBack callBack){
        sendRequest(url, jsonObject, flag, true, "gif", callBack);
    }

    /**
     * 发送请求
     * @param url
     * @param jsonObject
     * @param flag 是否显示LoadingDialog
     * @param callBack
     */
    public void sendRequest(String url, JSONObject jsonObject, boolean flag, String type, ResponseCallBack callBack){
        sendRequest(url, jsonObject, flag, true, type, callBack);
    }

    /**
     * 发送请求
     * @param url
     * @param jsonObject
     * @param callBack
     * @param flag 是否显示LoadingDialog
     * @param cancelable 返回键能否关闭LoadingDialog
     * @param type LoadingDialog样式
     */
    public void sendRequest(String url, JSONObject jsonObject, boolean flag, boolean cancelable, String type, ResponseCallBack callBack){
//        if(!NetworkUtils.isConnected(context)){//如果没有网络，不发送请求
//            return;
//        }
        this.callBack = callBack;
        try {
            tag = String.valueOf(jsonObject.get("req_code")); //将协议号作为tag

            if(jsonObject==null){
                request = HttpRequestManager.newGetStringRequest(url,this);
            } else {
                jsonObject.put("req_chanl", Config.REG_CHANL);
                String jsonRequest = jsonObject.toString();
                LogUtils.d(tag+" request:"+jsonRequest);
                jsonRequest = Base64Utils.encoded(jsonRequest);
                request = HttpRequestManager.newPostStringRequest(url,jsonRequest,this);
            }
            MyApplication.addRequest(request, tag);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            LogUtils.e("请求字符串Base64加密失败");
        } catch (JSONException e) {
            e.printStackTrace();
            LogUtils.e("无法从json中获取请求的协议号");
        }
    }


}
