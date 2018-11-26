package www.qisu666.com.utils

import android.content.Context
import android.content.Intent
import java.io.Serializable

/**
 * Created by wujiancheng on 2018/1/11.
 * 启动activity
 */
class StartActivityUtil<T>(private val map: Map<String, Any> = mapOf()) {
    fun startActivity(context: Context, clazz: Class<T>) {
        val intent = Intent()
        intent.setClass(context, clazz)
        for ((k, v) in map) {
            when (v) {
                is String -> intent.putExtra(k, v)
                is Int -> intent.putExtra(k, v)
                is Double -> intent.putExtra(k, v)
                is Boolean -> intent.putExtra(k, v)
                is Serializable -> intent.putExtra(k, v)
            }
        }
        context.startActivity(intent)
    }
}