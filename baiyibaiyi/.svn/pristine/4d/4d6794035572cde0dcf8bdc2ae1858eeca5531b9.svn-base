package www.qisu666.com.map.search

import android.content.Context
import com.amap.api.services.core.PoiItem
import com.amap.api.services.help.Inputtips
import com.amap.api.services.help.InputtipsQuery
import com.amap.api.services.poisearch.PoiResult
import com.amap.api.services.poisearch.PoiSearch

/**
 * Created by wujiancheng on 2017/11/1.
 * 根据关键字搜索内容管理类
 */
class SearchAddrManager {
    var onPoiSearchByPoiIDListener: OnPoiSearchByPoiIDListener? = null

    companion object {
        fun searchAddrByCityCode(context: Context, searchKey: String, searchCityCode: String, inputtipsListener: Inputtips.InputtipsListener) {
            //第二个参数默认代表全国，也可以为城市区号
            val inputQuery = InputtipsQuery(searchKey, searchCityCode)
            inputQuery.cityLimit = true
            // 构造 Inputtips 对象，并设置监听。
            val inputTips = Inputtips(context, inputQuery)
            inputTips.setInputtipsListener(inputtipsListener)
            inputTips.requestInputtipsAsyn()
        }
    }

    /**
     * 通过poiID搜索
     */
    fun poiSearchByPoiID(context: Context, poiID: String?) {
        val poiSearch = PoiSearch(context, null)
        poiSearch.setOnPoiSearchListener(object : PoiSearch.OnPoiSearchListener {
            override fun onPoiItemSearched(p0: PoiItem?, p1: Int) {
                onPoiSearchByPoiIDListener?.onPoiSearchByPoiID(p0, p1)
            }

            override fun onPoiSearched(p0: PoiResult?, p1: Int) {

            }
        })
        poiSearch.searchPOIIdAsyn(poiID)
    }

    interface OnPoiSearchByPoiIDListener {
        fun onPoiSearchByPoiID(poiItem: PoiItem?, rCode: Int)
    }

}