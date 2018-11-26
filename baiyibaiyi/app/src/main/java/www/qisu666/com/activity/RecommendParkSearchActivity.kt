package www.qisu666.com.activity

import android.app.Activity
import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import com.amap.api.maps.model.LatLng
import com.amap.api.services.help.Inputtips
import www.qisu666.com.R
import www.qisu666.com.adapter.RecommendParkSearchAdapter
import www.qisu666.com.constant.Config
import www.qisu666.com.map.search.SearchAddrManager
import www.qisu666.com.utils.StringUtils
import www.qisu666.com.utils.logI
import www.qisu666.com.view.SearchView
import kotlinx.android.synthetic.main.activity_recommend_park_search.*

/**
 * Created by wujiancheng on 2018/1/25.
 * 推荐建点搜索
 */
class RecommendParkSearchActivity : BaseActivity() {
    private val mSearchData = arrayListOf<HashMap<String, String>>()
    private var mSearchKeyword = ""
    private var mSearchCityCode = Config.DEFAULT_CITY_CODE
    private var adapter: RecommendParkSearchAdapter? = null

    private var inputMethodManager: InputMethodManager? = null

    override fun setView() {
        setContentView(R.layout.activity_recommend_park_search)
    }

    override fun initDatas() {
        inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        searchView.setData("搜索推荐建点地点")
        searchView.setOnFinishClickListener(object : SearchView.OnFinishClickListener {
            override fun onFinishClick() {
                inputMethodManager?.hideSoftInputFromWindow(currentFocus.windowToken, 0)
                finish()
            }
        })
        searchView.setOnTextChangedListener(object : SearchView.OnTextChangedListener {
            override fun afterTextChanged(s: Editable?) {
                if (!StringUtils.isEmpty(s.toString().trim())) {
                    mSearchKeyword = s.toString().trim()
                    logI("mSearchKeyword =" + mSearchKeyword)
                    searchAddress()
                } else {
                    logI("清空")
                    mSearchData.clear()
                    mSearchKeyword = ""
                    adapter?.notifyDataSetChanged()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        })

        adapter = RecommendParkSearchAdapter(mContext, mSearchData)
        //搜索列表
        rvAddress.layoutManager = LinearLayoutManager(mContext, LinearLayout.VERTICAL, false)
        rvAddress.adapter = adapter
        adapter?.onItemClickListener = object : RecommendParkSearchAdapter.OnItemClickListener {
            override fun onItemClick(itemData: HashMap<String, String>) {
                inputMethodManager?.hideSoftInputFromWindow(currentFocus.windowToken, 0)
                val latLng = LatLng(itemData["lat"]?.toDouble() ?: 0.0, itemData["lng"]?.toDouble() ?: 0.0)
                val intent = Intent()
                intent.putExtra("latLng", latLng)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
    }

    override fun onComplete(result: String?, type: Int) {

    }

    override fun onFailure(msg: String?, type: Int) {

    }

    /**
     * 高德搜索
     */
    fun searchAddress() {
        SearchAddrManager.searchAddrByCityCode(mContext, mSearchKeyword, mSearchCityCode, Inputtips.InputtipsListener { tipList, rCode ->
            logI("rCode = " + rCode)
            if (rCode == 1000) {
                if (null != tipList && tipList.size > 0) {
                    val tmp = arrayListOf<HashMap<String, String>>()
                    for (i in tipList.indices) {
                        if (i > 9) {
                            break
                        }
                        val tip = tipList[i]
                        if (tip != null && tip.point != null) {
                            val searchDataMap = hashMapOf<String, String>()
                            searchDataMap.put("lat", tip.point.latitude.toString())
                            searchDataMap.put("lng", tip.point.longitude.toString())
                            searchDataMap.put("addressName", tip.name)
                            searchDataMap.put("addressDetail", tip.address)
                            tmp.add(searchDataMap)
                        }
                    }
                    mSearchData.clear()
                    mSearchData.addAll(tmp)
                    adapter?.keyword = mSearchKeyword
                    adapter?.notifyDataSetChanged()
                } else {
                    mSearchData.clear()
                    adapter?.notifyDataSetChanged()
                }
            }
        })
    }
}