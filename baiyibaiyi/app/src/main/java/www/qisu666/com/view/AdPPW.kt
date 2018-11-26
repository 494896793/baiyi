package www.qisu666.com.view

import android.app.Activity
import android.os.Handler
import android.support.v4.view.ViewPager
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import www.qisu666.com.R
import www.qisu666.com.adapter.MyAdPagerAdapter
import www.qisu666.com.callback.SplashAndActivityResp
import www.qisu666.com.rx.RxTimeCountDown
import www.qisu666.com.utils.logI
import rx.Subscription

/**
 * Created by wujiancheng on 2017/12/13.
 * 循环滚动的广告页
 */
class AdPPW {
    private var subscription: Subscription? = null
    private val handler = Handler()
    fun showAdPPW(activity: Activity, resp: ArrayList<SplashAndActivityResp>?) {
        if (resp == null || resp.isEmpty()) {
            return
        }
        val screenWidth = activity.resources.displayMetrics.widthPixels

        val popupWindowWrap = PopupWindowWrap(activity)
        popupWindowWrap
                .setContentView(R.layout.layout_cyclic_viewpager) { contentView ->
                    val viewPager = contentView.findViewById<View>(R.id.viewPager) as ViewPager
                    val params = viewPager.layoutParams
                    //宽为屏幕宽的84%
                    val viewPagerWidth = screenWidth.times(0.84).toInt()
                    params.width = viewPagerWidth
                    viewPager.layoutParams = params
                    logI("ViewPager的宽=$viewPagerWidth")

                    val adapter = MyAdPagerAdapter(resp)
                    viewPager.adapter = adapter

                    //要循环播放
                    if (resp.size > 3) {//多于一个，底部有圆点,（因为头尾各加了一条数据，所以圆点数比数据数量少2）
                        val llytPointContainer: LinearLayout = contentView.findViewById(R.id.llytPointContainer)
                        val layoutInflater = activity.layoutInflater
                        //圆点数量,因为头尾各加了一条数据，所以圆点数比数据数量少2
                        for (i in 0 until resp.size - 2) {
                            val view = layoutInflater.inflate(R.layout.layout_point, llytPointContainer, false)
                            if (i == 0) {//第一个选中，白色
                                val viewPoint = view.findViewById<View>(R.id.viewPoint)
                                viewPoint.setBackgroundResource(R.drawable.point_white)
                            }
                            llytPointContainer.addView(view)
                        }
                        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                                if (resp.size > 3) {//因为头尾各加了一条数据，所以轮播的条件是至少有4条数据（原本有两条+头尾两条数据）
                                    if (position == 0 && positionOffset == 0F) {
                                        handler.post({
                                            viewPager.setCurrentItem(resp.size - 2, false)
                                        })
                                    } else if (position == resp.size - 1 && positionOffset == 0F) {
                                        handler.post({
                                            viewPager.setCurrentItem(1, false)
                                        })
                                    }
                                }
                            }

                            override fun onPageSelected(position: Int) {
                                val pointCount = llytPointContainer.childCount

                                var selectPos = position - 1//圆点的位置为图片的位置-1
                                if (position == 0) {//如果图片的位置为0，那么圆点的位置为最后一个
                                    selectPos = pointCount - 1
                                } else if (position == resp.size - 1) {//如果图片的位置为最后一个，那么圆点的位置为第一个
                                    selectPos = 0
                                }

                                for (i in 0 until pointCount) {
                                    val view = llytPointContainer.getChildAt(i)
                                    val viewPoint = view.findViewById<View>(R.id.viewPoint)
                                    if (i == selectPos) {
                                        viewPoint.setBackgroundResource(R.drawable.point_blue)
                                    } else {
                                        viewPoint.setBackgroundResource(R.drawable.point_gray_aaa)
                                    }
                                }
                            }

                            override fun onPageScrollStateChanged(state: Int) {

                            }
                        })

                        subscription = RxTimeCountDown.timeCountUp(1)
                                .subscribe {
                                    //每3秒切换一次
                                    val pos = it.rem(3)
                                    if (pos == 0) {
                                        val currentItem = viewPager.currentItem
                                        handler.post({
                                            var position = currentItem + 1
                                            if (position == resp.size - 1) {
                                                position = 1
                                            }
                                            viewPager.setCurrentItem(position, true)
                                        })
                                    }
                                }
                        //默认位置
                        viewPager.setCurrentItem(1, false)
                    }

                    contentView.findViewById<ImageView>(R.id.ivDelete).setOnClickListener { popupWindowWrap.dismiss() }
                    contentView.findViewById<View>(R.id.viewBackground).setOnClickListener { popupWindowWrap.dismiss() }
                }
                .setHeight(ViewGroup.LayoutParams.MATCH_PARENT)
                .isChangeWindowBg(false)
        popupWindowWrap.setOnDismissListener {
            popupWindowWrap.dismiss()
            if (subscription?.isUnsubscribed != true) {
                subscription?.unsubscribe()
            }
        }
        popupWindowWrap.showAtLocation(activity.window.decorView.findViewById(android.R.id.content), Gravity.CENTER, 0, 0)
    }
}