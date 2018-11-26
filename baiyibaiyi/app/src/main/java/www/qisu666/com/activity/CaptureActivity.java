package www.qisu666.com.activity;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;


import www.qisu666.com.R;
import www.qisu666.com.controls.InactivityTimer;
import www.qisu666.com.event.Message;
import www.qisu666.com.request.MyNetwork;
import www.qisu666.com.request.utils.FlatFunction;
import www.qisu666.com.request.utils.MyMessageUtils;
import www.qisu666.com.request.utils.ResultSubscriber;
import www.qisu666.com.request.utils.RxNetHelper;
import www.qisu666.com.utils.CacheUtils;
import www.qisu666.com.utils.DialogHelper;
import www.qisu666.com.utils.JsonUtils;
import www.qisu666.com.utils.ToastUtil;
import www.qisu666.com.widget.AlertDialog;
import www.qisu666.com.zxing.camera.CameraManager;

import www.qisu666.com.zxing.decode.CaptureActivityHandler;
import www.qisu666.com.zxing.view.ViewfinderView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;







/**
 * 扫码充电
 */
public class CaptureActivity extends BaseActivity implements Callback, OnClickListener {

    private View ll_device_no, ll_scan, ll_scan_flash;
    private ImageView iv_scan_light;

    private CaptureActivityHandler handler;
    private ViewfinderView viewfinderView;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.10f;
    private boolean vibrate;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);
        CameraManager.init(getApplication());
        initView();
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
    }

    private void initView() {
        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);

        View mButtonBack = findViewById(R.id.img_title_left);
        mButtonBack.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                CaptureActivity.this.finish();

            }
        });
        TextView title = (TextView) findViewById(R.id.tv_title);
        title.setText("扫码充电");

        iv_scan_light = (ImageView) findViewById(R.id.iv_scan_light);

        ll_device_no = findViewById(R.id.ll_device_no);
        ll_scan = findViewById(R.id.ll_scan);
        ll_scan_flash = findViewById(R.id.ll_scan_flash);

        ll_device_no.setOnClickListener(this);
        ll_scan.setOnClickListener(this);
        ll_scan_flash.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;

        playBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    /**
     * 扫码后的事件
     *
     * @param result
     * @param barcode
     */
    public void handleDecode(Result result, Bitmap barcode) {
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        String resultString = result.getText();
        if (resultString.equals("")) {
            ToastUtil.showToast(R.string.toast_prompt_scan_failed);
        } else {
//			Intent resultIntent = new Intent(this, DeviceDetailActivity.class);
//			Bundle bundle = new Bundle();
//			resultIntent.putExtra("charge_pile_seri", resultString);
//			bundle.putParcelable("bitmap", barcode);
//			resultIntent.putExtras(bundle);
//			startActivity(resultIntent);
            //扫码后的  桩子编号信息
            connToServer(resultString);
        }
        finish();
    }

    /**
     * 发送 D107 请求，获取电桩信息
     */
    private void connToServer(final String resultString) {

        String url = "api/pile/gun/info/query";
        HashMap<String, Object> map = new HashMap<>();
        map.put("gunCode", resultString);
        map.put("userId", CacheUtils.getIn().getUserInfo().getId());

        MyNetwork.getMyApi()
                .carRequest(url, MyMessageUtils.addBody(map))
                .map(new FlatFunction<>(Object.class))
                .compose(RxNetHelper.<Object>io_main())
                .subscribe(new ResultSubscriber<Object>() {
                    @Override
                    public void onSuccessCode(Message object) {

                    }

                    @Override
                    @SuppressWarnings("unchecked")
                    public void onSuccess(Object bean) {
                        // 对象转json
                        String s = JsonUtils.objectToJson(bean);
                        // json转 map
                        Map jsonToMap = JsonUtils.jsonToMap(s);
                        Log.e("aaaa", "jsonToMap:" + jsonToMap);

//                        SPUtil.put(CaptureActivity.this,"gunCode",jsonToMap.get("gunCode").toString());
//                        SPUtil.put(CaptureActivity.this,"chargePileSeri",jsonToMap.get("chargePileSeri").toString());

                        // 充电站ID
                        String id = jsonToMap.get("stationId").toString();
                        if (!TextUtils.isEmpty(id)) {
                            Intent i = new Intent(CaptureActivity.this, DeviceDetailActivity.class);
                            i.putExtra("map", jsonToMap.toString());
                            i.putExtra("charge_pile_num", resultString);
                            startActivity(i);
                            finish();
                        } else {
                            ToastUtil.showToast(R.string.toast_D107_failed);
                        }
                    }

                    @Override
                    public void onFail(Message<Object> bean) {
                        ToastUtil.showToast(bean.msg);
                        Log.e("aaa", "msg:" + bean.msg);
                        Log.e("aaaa", "获取失败：" + bean.toString());
                    }

                });


//        JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject.put("req_code", "D107");
//            jsonObject.put("charge_pile_num", resultString);
//            jsonObject.put("user_id", UserParams.INSTANCE.getUser_id());
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        HttpLogic httpLogic = new HttpLogic(this);
//        httpLogic.sendRequest(Config.REQUEST_URL, jsonObject, true, "rotate", new AbstractResponseCallBack() {
//
//            @Override
//            public void onResponse(Map<String, Object> map, String tag) {
//                /**
//                 * D107 response:{"station_name":"荷兰站","charge_pile_type":"01","return_msg":"操作成功.","charge_pile_num":"01","brand_id":"","charge_pile_seri":"000000012345","charge_pile_id":6,
//                 *      "time_list":[{"service_price":80,"end_time":"600","low_price":90,"start_time":"000","charge_price":90,"max_price":220,"avg_price":160,"high_price":200,"division_type":"low_price"},
//                 *                  {"service_price":80,"end_time":"1900","low_price":90,"start_time":"600","charge_price":90,"max_price":220,"avg_price":160,"high_price":200,"division_type":"max_price"},
//                 *                  {"service_price":80,"end_time":"2100","low_price":90,"start_time":"1900","charge_price":90,"max_price":220,"avg_price":160,"high_price":200,"division_type":"high_price"},
//                 *                  {"service_price":80,"end_time":"000","low_price":90,"start_time":"2100","charge_price":90,"max_price":220,"avg_price":160,"high_price":200,"division_type":"avg_price"}],
//                 *      "charge_pile_name":"超级快充桩","tariff_policy_id":6,"charge_interface":"01","charging_gun":"0","charge_method":"01","station_id":23,"pile_state":"00","return_code":"0000","charge_pile_bel":"01"}
//                 */
//                String return_code = map.get("return_code").toString();
//                if (return_code.equals("0000")) {
//                    Intent i = new Intent(CaptureActivity.this, DeviceDetailActivity.class);
//                    i.putExtra("map", map.toString());
//                    i.putExtra("charge_pile_num", resultString);
//                    startActivity(i);
//                    finish();
//                } else {
//                    ToastUtil.showToast(R.string.toast_D107_failed);
//                }
//            }
//        });
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            findViewById(R.id.preview_view).setBackgroundColor(ContextCompat.getColor(this, R.color.black));
            DialogHelper.alertDialog(this, getString(R.string.toast_permission_camera), new AlertDialog.OnDialogButtonClickListener() {
                @Override
                public void onConfirm() {
                    finish();
                }

                @Override
                public void onCancel() {

                }
            });
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(this, decodeFormats,
                    characterSet);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;

    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();

    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(
                    R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final OnCompletionListener beepListener = new OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_device_no:
                Intent i = new Intent(this, InputDeviceNoActivity.class);
                startActivity(i);
                finish();
                break;
            case R.id.ll_scan:

                break;
            case R.id.ll_scan_flash:
                flashToggle();
                break;
            default:
                break;
        }
    }

    boolean toggleFlag = false;

    private void flashToggle() {
        try {
            CameraManager.get().flashHandler();
            if (!toggleFlag) {
                iv_scan_light.setImageResource(R.mipmap.yd_7);
            } else {
                iv_scan_light.setImageResource(R.mipmap.yd_6);
            }
            toggleFlag = !toggleFlag;
        } catch (Exception e) {
            ToastUtil.showToast(R.string.toast_permission_camera);
        }
    }

    @Override
    public void setView() {

    }

    @Override
    public void initDatas() {

    }

    @Override
    public void onComplete(String result, int type) {

    }

    @Override
    public void onFailure(String msg, int type) {

    }
}