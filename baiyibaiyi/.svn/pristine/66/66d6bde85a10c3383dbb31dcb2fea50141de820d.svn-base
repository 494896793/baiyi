package www.qisu666.com.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import www.qisu666.com.R
import www.qisu666.com.callback.CarResp
import www.qisu666.com.utils.Logger
import www.qisu666.com.utils.logI
import www.qisu666.com.view.CarInfoOrderView
import java.lang.Exception

/**
 * Created by wujiancheng on 2018/1/13.
 */
class CarsListAdapter(val context: Context, val carInfo: List<CarResp>) : RecyclerView.Adapter<CarsListAdapter.CarsListViewHolder>() {
    var onCarListClickListener: OnCarListClickListener? = null
    private val mData = arrayListOf<CarResp>()

    init {
        setData(carInfo)
    }

    fun setData(data: List<CarResp>) {
        try {
//            val lastIndex = mData.size
            mData.clear()
//            notifyItemRangeRemoved(0, lastIndex)
            mData.addAll(data)
//            notifyItemRangeInserted(0, mData.size)
            notifyDataSetChanged()
            logI("车辆数 = " + mData.size)
        } catch (e: Exception) {
            Logger.e(e.message)
        }
    }

    fun addData(data: List<CarResp>) {
        logI("addData车辆数 = " + data.size)
        val lastIndex = mData.size
        mData.addAll(data)
        notifyItemRangeInserted(lastIndex, data.size)
    }

    override fun onBindViewHolder(holder: CarsListViewHolder?, position: Int) {
        val carResp = mData[position]
        holder?.carInfoOrderView?.setData(carResp.carColor?:"",carResp.carSetsNums?:"",carResp.models?:"",carResp.carImgUri ?: "", carResp.carNumber ?: "", carResp.carBrand ?: "",
                carResp.batteryResidual.toDouble(), carResp.canUseMileage ?: "", carResp.isRedPkCar == 1,false)
        holder?.carInfoOrderView?.setOnClickListener {
            onCarListClickListener?.setOnCarListClick(carResp)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): CarsListViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.listitem_car_list, parent, false)
        return CarsListViewHolder(view)
    }

    override fun getItemCount(): Int = mData.size

    class CarsListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val carInfoOrderView: CarInfoOrderView = view.findViewById(R.id.carInfoOrderView)
    }

    interface OnCarListClickListener {
        fun setOnCarListClick(carResp: CarResp)
    }
}