package www.qisu666.com.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import www.qisu666.com.R
import www.qisu666.com.utils.HighlightUtil

/**
 * Created by wujiancheng on 2018/1/25.
 */
class RecommendParkSearchAdapter(val context: Context, private val searchAddress: ArrayList<HashMap<String, String>>)
    : RecyclerView.Adapter<RecommendParkSearchAdapter.RecommendParkSearchViewHolder>() {
    var onItemClickListener: OnItemClickListener? = null
    var keyword = ""
    override fun getItemCount(): Int = searchAddress.size

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecommendParkSearchViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.listitem_recommend_park, parent, false)
        return RecommendParkSearchViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecommendParkSearchViewHolder?, position: Int) {
        val itemData = searchAddress[position]
        var addressName = itemData["addressName"]
        addressName = HighlightUtil.convertHightlightText(addressName, keyword, "#02b2e4")
        holder?.tvAddress?.text = Html.fromHtml(addressName)
        holder?.tvAddressDetail?.text = itemData["addressDetail"]

//        if (position == searchAddress.size - 1) {
//            holder?.viewDivideLine?.visibility = View.GONE
//        } else {
//            holder?.viewDivideLine?.visibility = View.VISIBLE
//        }

        holder?.llytAddressContainer?.setOnClickListener {
            onItemClickListener?.onItemClick(itemData)
        }
    }

    class RecommendParkSearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val llytAddressContainer: LinearLayout = itemView.findViewById(R.id.llytAddressContainer)
        val tvAddress: TextView = itemView.findViewById(R.id.tvAddress)
        val tvAddressDetail: TextView = itemView.findViewById(R.id.tvAddressDetail)
//        val viewDivideLine: View = itemView.findViewById(R.id.viewDivideLine)
    }

    interface OnItemClickListener {
        fun onItemClick(itemData: HashMap<String, String>)
    }
}