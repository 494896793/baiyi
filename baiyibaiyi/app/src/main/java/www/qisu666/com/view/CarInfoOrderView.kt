package www.qisu666.com.view

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import www.qisu666.com.R
import www.qisu666.com.activity.WebActivity
import www.qisu666.com.utils.CacheUtils
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.view_car_info_order.view.*

/**
 * Created by wujiancheng on 2017/11/2.
 * 找车，约车的车辆信息
 */
class CarInfoOrderView : FrameLayout {
    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        LayoutInflater.from(context).inflate(R.layout.view_car_info_order, this)
    }

    /**
     * @param carImgUrl 车辆图片地址
     * @param carNumber 车牌号
     * @param carBrand 车型
     * @param carBatteryPercent 剩余电量百分比
     * @param carLeftEnduranceKM 剩余续航，千米
     * @param isRedCar 是否是红包车
     * @param isShowRegular 是否显示红包车规则
     *
     */
    fun setData(carColor: String,carSetsNums: String,carModels: String,carImgUrl: String, carNumber: String, carBrand: String, carBatteryPercent: Double, carLeftEnduranceKM: String, isRedCar: Boolean, isShowRegular: Boolean) {
        Glide.with(context).load(carImgUrl).into(ivCarImg)
        tvCarNumber.text = carNumber
        tvCarBrand.text = carBrand+"-"+carModels
        car_color_tx.text=carColor
        car_set_tx.text=carSetsNums+"座"
        batteryView.setBatteryPercent(carBatteryPercent)
        elect_tx.text=carBatteryPercent.toString()+"%";
        if(carBatteryPercent>80){
            elect_img.setImageResource(R.mipmap.yc_23)
        }else if(carBatteryPercent>60){
            elect_img.setImageResource(R.mipmap.yc_24)
        }else if(carBatteryPercent>40){
            elect_img.setImageResource(R.mipmap.yc_25)
        }else if(carBatteryPercent>20){
            elect_img.setImageResource(R.mipmap.yc_26)
        }else if(carBatteryPercent>10){
            elect_img.setImageResource(R.mipmap.yc_27)
        }else{
            elect_img.setImageResource(R.mipmap.yc_28)
        }
        val leftEndurance = "续航${carLeftEnduranceKM}KM"
        tvLeftEnduranceKM.text = leftEndurance
        distance_tx.text="${carLeftEnduranceKM}公里"
        ivFlagRedPacketCar.visibility = if (isRedCar) View.VISIBLE else View.GONE

        if (isShowRegular) {
            tvRedCarRegular.visibility = View.VISIBLE
            tvRedCarRegular.setOnClickListener {
                val systemConfigResp = CacheUtils.getIn().system_Info
                val intent = Intent(context, WebActivity::class.java)
                intent.putExtra("url", systemConfigResp?.redpackageUseRule ?: "")
                context.startActivity(intent)
            }
        } else {
            tvRedCarRegular.visibility = View.GONE
        }
    }

    fun setArrowRightVisibility(visibility: Int) {
        ivArrowRight.visibility = visibility
    }
}