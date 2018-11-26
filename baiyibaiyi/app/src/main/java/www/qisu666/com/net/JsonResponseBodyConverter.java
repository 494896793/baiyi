package www.qisu666.com.net;

import www.qisu666.com.bean.HttpResult;
import www.qisu666.com.utils.JsonUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * @author ldl
 * 717219917@qq.com 2018/10/14 18:35.
 */
public class JsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {

    private final Gson gson;
    private final TypeAdapter<T> adapter;


    JsonResponseBodyConverter(Gson gson, TypeAdapter<T> adapter) {
        this.gson = gson;
        this.adapter = adapter;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        String result=value.string();
        HttpResult httpResult=null;
        T data=null;
        try {
            try{
                    httpResult = new HttpResult();
                    JSONObject jsonObject1 = new JSONObject(result);
                    Map<String, Object> map = new HashMap<>();
                    map.put("data", jsonObject1.opt("data"));
                    if(result.contains("myOrderList")){          //订单接口单独处理
                        Object object1=jsonObject1.opt("data");
                        Object object2=new JSONObject(object1.toString()).opt("data");
                        Object object3=new JSONObject(object2.toString()).opt("myOrderList");
                        Map<String,Object> map1=new HashMap<>();
                        map1.put("data",object3);
                        map1.put("msg",jsonObject1.optString("msg"));
                        map1.put("code",jsonObject1.optInt("code"));
                        String jsonString = gson.toJson(map1);
                        Map<String,Object> map2=new HashMap<>();
                        map2.put("nameValuePairs",map1);
                        String jsonString2 = gson.toJson(map2);
                        Map<String,Object> map3=new HashMap<>();
                        map3.put("data",map2);
                        String jsonString3=gson.toJson(map3);
                        httpResult.data = jsonString;
                    }else{
                        String jsonString = gson.toJson(map);
                        httpResult.data = jsonString;
                    }

                    httpResult.msg = jsonObject1.optString("msg");
                    httpResult.code = jsonObject1.optInt("code");
                    data = (T) httpResult;
            }catch (Exception e){
                e.printStackTrace();
            }
            return data;
        } finally {
            value.close();
        }
    }
}
