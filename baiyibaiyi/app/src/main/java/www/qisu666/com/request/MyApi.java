package www.qisu666.com.request;


import www.qisu666.com.bean.CardBackBean;
import www.qisu666.com.bean.CardDriverBean;
import www.qisu666.com.bean.CardFrontBean;
import www.qisu666.com.bean.HttpResult;

import java.util.List;
import java.util.Map;

import io.reactivex.Flowable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Url;

/**
 * Created by zhang on 2016/12/9.
 */

public interface MyApi {

//    @Multipart
//    @POST("photo/upload")
//    Flowable<HttpResult<Object>> uploadPhoto(@Part MultipartBody.Part file, @PartMap(encoding = "8-bit") Map<String, RequestBody> options);

//    @FormUrlEncoded
//    @POST("index.php/Api/User/login")
//    Flowable<LoginBean> login(@FieldMap Map<String, String> params);

//    //编辑个人信息
//    @Multipart
//    @POST("index.php/Api/User/edit")
//    Flowable<HttpResult<String>> addPersonInfo(@Part MultipartBody.Part file, @PartMap(encoding = "8-bit") Map<String, RequestBody> options);

    //登录
    @FormUrlEncoded
    @POST("account/login")
    Flowable<HttpResult<Object>> login(@Field("username") String username, @Field("password") String password);

    //锁车
    @FormUrlEncoded
    @POST("tss/api/car/door/lock")
    Flowable<HttpResult<String>> lock(@Field("req") String req);

    //锁车
    @POST
    Flowable<HttpResult<String>> carRequest(@Url String url, @Body RequestBody body);

//    @Multipart
//    @POST("photo/upload")
//    Flowable<HttpResult<Object>> uploadPhoto(@Part MultipartBody.Part file, @PartMap(encoding = "8-bit") Map<String, RequestBody> options);

    @Multipart
    @POST
    Flowable<HttpResult<String>> uploadLive(@Url String url, @Part("req") RequestBody req, @Part MultipartBody.Part file);

    @Multipart
    @POST
    Flowable<HttpResult<String>> uploadPhoto(@Url String url, @Part("req") RequestBody req, @Part MultipartBody.Part frontImg, @Part MultipartBody.Part backImg);

    @Multipart
    @POST
    Flowable<HttpResult<String>> uploadPhoto(@Url String url, @Part("req") RequestBody req, @Part MultipartBody.Part frontImg, @Part MultipartBody.Part backImg, @Part MultipartBody.Part headImg);

    @Multipart
    @POST
    Flowable<CardFrontBean> uploadIdPhoto(@Url String url, @Part MultipartBody.Part file, @PartMap(encoding = "8-bit") Map<String, RequestBody> options);

    @Multipart
    @POST
    Flowable<CardBackBean> uploadBackPhoto(@Url String url, @Part MultipartBody.Part file, @PartMap(encoding = "8-bit") Map<String, RequestBody> options);

    @Multipart
    @POST
    Flowable<CardDriverBean> uploadDriverPhoto(@Url String url, @Part MultipartBody.Part file, @PartMap(encoding = "8-bit") Map<String, RequestBody> options);

//    @Multipart
//    @POST
//    Flowable<CardFrontBean> uploadFrontPhoto(@Url String url, @Part("api_key") String  apiKey, @Part("apiSecret") String  apiSecret,@Part("legality") String  legality, @Part MultipartBody.Part image);

    //登录
    @FormUrlEncoded
    @POST("position/add")
    Flowable<List<Object>> addPosition(@FieldMap Map<String, String> params);


}
