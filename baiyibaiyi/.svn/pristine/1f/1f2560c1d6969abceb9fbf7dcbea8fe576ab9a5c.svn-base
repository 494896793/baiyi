package www.qisu666.com.fragment

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.LinearLayout
import www.qisu666.com.R
import www.qisu666.com.activity.UseCarPreOrderingActivity
import www.qisu666.com.adapter.CarsListAdapter
import www.qisu666.com.callback.CarResp
import www.qisu666.com.callback.ParkResp
import www.qisu666.com.callback.ParksResp
import www.qisu666.com.utils.StartActivityUtil

/**
 * Created by wujiancheng on 2018/1/13.
 * 车辆列表
 */
class CarsListFragment : BaseFragment() {
    override fun setLayoutResId(): Int {
        return R.layout.fragment_car_list
    }

    override fun initDatas(view: View?) {
        val carList = arguments?.get("carList") as List<CarResp>
        val parksInfo = arguments?.get("parkInfo") as ParksResp
        val carsListAdapter = CarsListAdapter(activity, carList)
        val rvCarList: RecyclerView? = view?.findViewById(R.id.rvCarList)
        rvCarList?.isNestedScrollingEnabled = false
        rvCarList?.layoutManager = LinearLayoutManager(activity, LinearLayout.VERTICAL, false)
        rvCarList?.adapter = carsListAdapter

        carsListAdapter.onCarListClickListener = object : CarsListAdapter.OnCarListClickListener {
            override fun setOnCarListClick(carResp: CarResp) {
                val map = mapOf("carInfo" to carResp, "parkInfo" to getParkInfo(parksInfo))
                StartActivityUtil<UseCarPreOrderingActivity>(map).startActivity(activity, UseCarPreOrderingActivity::class.java)
            }
        }
    }

    override fun onComplete(result: String?, type: Int) {

    }

    override fun onFailure(msg: String?, type: Int) {

    }

    private fun getParkInfo(park: ParksResp): ParkResp {
        val parkResp = ParkResp()
        parkResp.parkName = park.parkName
        parkResp.parkAddress = park.parkAddress
        parkResp.latitude = park.latitude
        parkResp.longitude = park.longitude
        parkResp.parkType = park.parkType
        parkResp.id = park.id
        parkResp.distance = park.distance
        return parkResp
    }
}