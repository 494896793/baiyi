package www.qisu666.com.view

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import www.qisu666.com.R
import kotlinx.android.synthetic.main.view_search.view.*

/**
 * Created by wujiancheng on 2018/1/25.
 */
class SearchView : FrameLayout {
    private var onFinishClickListener: OnFinishClickListener? = null
    private var onTextChangedListener: OnTextChangedListener? = null

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        LayoutInflater.from(context).inflate(R.layout.view_search, this)

        //返回按钮
        ivTitleLeft.setOnClickListener {
            onFinishClickListener?.onFinishClick()
        }

        //输入框
        etSearchKeyWord.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                onTextChangedListener?.afterTextChanged(s)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                onTextChangedListener?.beforeTextChanged(s, start, count, after)
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                onTextChangedListener?.onTextChanged(s, start, before, count)
            }
        })
    }

    interface OnFinishClickListener {
        fun onFinishClick()
    }

    /**
     * 返回按钮监听
     */
    fun setOnFinishClickListener(onFinishClickListener: OnFinishClickListener) {
        this.onFinishClickListener = onFinishClickListener
    }

    interface OnTextChangedListener {
        fun afterTextChanged(s: Editable?)

        fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int)

        fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int)
    }

    /**
     * 搜索关键字输入监听
     */
    fun setOnTextChangedListener(onTextChangedListener: OnTextChangedListener) {
        this.onTextChangedListener = onTextChangedListener
    }

    fun setData(searchHint: String) {
        etSearchKeyWord.hint = searchHint
    }
}