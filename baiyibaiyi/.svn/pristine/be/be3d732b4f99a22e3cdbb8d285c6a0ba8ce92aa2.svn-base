package www.qisu666.com.view

import android.content.Context
import android.support.v7.widget.GridLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import www.qisu666.com.R
import www.qisu666.com.adapter.ProblemTypesAdapter
import www.qisu666.com.callback.ProblemTypesResp
import kotlinx.android.synthetic.main.view_recycler_view_with_content.view.*

/**
 * Created by wujiancheng on 2018/1/22.
 * RecyclerView底部有输入框
 */
class RecyclerViewWithContentView : FrameLayout {
    //故障类型数据
    private var problemTypesResult = arrayListOf<ProblemTypesResp>()

    private var onHasSelectProblemTypeListener: OnHasSelectProblemTypeListener? = null

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        LayoutInflater.from(context).inflate(R.layout.view_recycler_view_with_content, this)

        //输入的内容
        etProblemDesc.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                tvTextLength.text = "${s?.length ?: 0}/100"
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })
    }

    /**
     * 设置故障类型数据
     */
    fun setData(problemTypes: ArrayList<ProblemTypesResp>) {
        rvProblemDesc.layoutManager = GridLayoutManager(context, 3)
        problemTypesResult = problemTypes
        val adapter = ProblemTypesAdapter(context, problemTypesResult)
        rvProblemDesc.adapter = adapter

        adapter.setOnProblemTypesSelectListener(object : ProblemTypesAdapter.OnProblemTypesSelectListener {
            override fun onProblemTypesSelect(problemTypes: List<ProblemTypesResp>) {
                val hasSelectedItem = problemTypes.any { it.selectedStatus }
                //是否有选择类型
//                for (type in problemTypes) {
//                    if (type.selectedStatus) {
//                        hasSelectedItem = true
//                        break
//                    }
//                }

                //监听是否有选中Item
                onHasSelectProblemTypeListener?.onHasSelectProblemType(hasSelectedItem)
            }
        })
    }

    /**
     * 获取输入的内容
     */
    fun getInputContent(): String {
        return etProblemDesc.text.toString().trim()
    }

    /**
     * 监听是否有选中Item接口
     */
    interface OnHasSelectProblemTypeListener {
        fun onHasSelectProblemType(hasSelectedItem: Boolean)
    }

    /**
     * 监听是否有选中Item
     */
    fun setOnHasSelectProblemTypeListener(onHasSelectProblemTypeListener: OnHasSelectProblemTypeListener) {
        this.onHasSelectProblemTypeListener = onHasSelectProblemTypeListener
    }
}