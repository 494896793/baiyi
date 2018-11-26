package www.qisu666.com.fragment

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import www.qisu666.com.R
import www.qisu666.com.adapter.MyCouponAdapter
import www.qisu666.com.app.CouponType
import www.qisu666.com.callback.CouponBean
import www.qisu666.com.callback.CouponListChooseResp
import www.qisu666.com.callback.CouponListResp
import www.qisu666.com.constant.Config
import www.qisu666.com.constant.RequestUrls
import www.qisu666.com.request.CouponListChooseRequest
import www.qisu666.com.request.CouponListRequest
import www.qisu666.com.rx.RxBus
import www.qisu666.com.rx.RxBusEvent
import www.qisu666.com.rx.RxEventCodes
import www.qisu666.com.utils.UserUtils
import com.liaoinstan.springview.container.DefaultFooter
import com.liaoinstan.springview.container.DefaultHeader
import com.liaoinstan.springview.widget.SpringView

/**
 * Created by wujiancheng on 2017/11/11.
 * 优惠券
 */
class MyCouponFragment : BaseFragment() {
    private val QUERY_COUPON_LIST = 0
    private val QUERY_COUPON_CHOOSE_LIST = 1

    private var curRequestIndex = 1
    private lateinit var springView: SpringView
    private lateinit var rvCoupon: RecyclerView
    private lateinit var springViewNoData: SpringView
    private lateinit var rlytNoCoupon: RelativeLayout
    private lateinit var tvCouponTip: TextView

    private val coupons = arrayListOf<CouponBean>()
    private var mCouponsAdapter: MyCouponAdapter? = null

    private var couponType = ""
    private var couponData: CouponListChooseResp? = null
    private var orderId: String? = ""

    override fun setLayoutResId(): Int {
        return R.layout.fragment_my_coupon
    }

    override fun initDatas(view: View) {
        springView = view.findViewById<SpringView>(R.id.springView) as SpringView
        rvCoupon = view.findViewById<RecyclerView>(R.id.rvCoupon) as RecyclerView
        springViewNoData = view.findViewById<SpringView>(R.id.springViewNoData) as SpringView
        rlytNoCoupon = view.findViewById<RelativeLayout>(R.id.rlytNoCoupon) as RelativeLayout
        tvCouponTip = view.findViewById<TextView>(R.id.tvCouponTip) as TextView

        couponType = arguments?.getString("type") ?: ""
        rvCoupon.layoutManager = LinearLayoutManager(activity, LinearLayout.VERTICAL, false)
        mCouponsAdapter = MyCouponAdapter(activity, coupons, couponType)
        rvCoupon.adapter = mCouponsAdapter

        springView.header = DefaultHeader(activity)
        springViewNoData.header = DefaultHeader(activity)
        if (couponType in CouponType.IS_IN_CHOOSE_COUPON) {
            //支付选择优惠券时，不可刷新
//            springView.isEnable = false
            //从支付页面得到的优惠券数据，不用再次请求数据
            couponData = arguments?.getSerializable("couponData") as CouponListChooseResp?
            orderId = arguments?.getString("orderId")

            setCouponChooseData()

            springView.setListener(object : SpringView.OnFreshListener {
                override fun onRefresh() {
                    onPullDownToRefresh()
                }

                override fun onLoadmore() {
                    stopFresh()
                }
            })
            springViewNoData.setListener(object : SpringView.OnFreshListener {
                override fun onRefresh() {
                    onPullDownToRefresh()
                }

                override fun onLoadmore() {
                    stopFresh()
                }
            })

            //点击选择可使用优惠券
            if (couponType == CouponType.CAN_USE_CHOOSE) {
                mCouponsAdapter?.setOnCouponItemClickListener(object : MyCouponAdapter.OnCouponItemClickListener {
                    override fun onCouponItemClick(couponBean: CouponBean) {
                        val rxBus = RxBus.getInstance()
                        val busEvent = RxBusEvent<CouponBean>()
                        busEvent.eventCode = RxEventCodes.CHOOSE_COUPON
                        busEvent.content = couponBean
                        rxBus.post(busEvent)

                        activity.finish()
                    }
                })
            }

        } else if (couponType in CouponType.IS_IN_SHOW_COUPON) {
            //展示，可上拉，可下拉
            springView.footer = DefaultFooter(activity)
            springView.setListener(object : SpringView.OnFreshListener {
                override fun onRefresh() {
                    onPullDownToRefresh()
                }

                override fun onLoadmore() {
                    onPullUpToRefresh()
                }
            })

            springViewNoData.footer = DefaultFooter(activity)
            springViewNoData.setListener(object : SpringView.OnFreshListener {
                override fun onRefresh() {
                    onPullDownToRefresh()
                }

                override fun onLoadmore() {
                    onPullUpToRefresh()
                }
            })
            //请求数据
            getCouponsList()
        }

        observeEvent()
    }

    private fun observeEvent() {
        busSubscription = RxBus.getInstance()
                .toObservable(RxBusEvent::class.java)
                .subscribe { rxBusEvent ->
                    when (rxBusEvent.eventCode) {
                        RxEventCodes.CODE_BIND_COUPON_SUCCESS -> {//绑定优惠券成功
                            //刷新数据
                            onPullDownToRefresh()
                        }
                    }
                }
    }

    override fun onComplete(result: String?, type: Int) {
        if (isSuccess(result)) {
            if (type == QUERY_COUPON_LIST) {//查看优惠券，不可选
                stopFresh()
                val data = getBean(result, CouponListResp::class.java)
                if (curRequestIndex == 1) {
                    coupons.clear()
                }
                coupons.addAll(data.couponBatchVo)

                setCouponData(data)
            } else if (type == QUERY_COUPON_CHOOSE_LIST) {//支付选择优惠券
                stopFresh()
                val data = getBean(result, CouponListChooseResp::class.java)
                couponData = data ?: couponData
                if (curRequestIndex == 1) {
                    coupons.clear()
                }
                setCouponChooseData()
            }
        } else {
            stopFresh()
        }
    }

    override fun onFailure(msg: String?, type: Int) {
        springView.visibility = View.GONE
        springViewNoData.visibility = View.VISIBLE
        stopFresh()
    }

    private fun stopFresh() {
        springView.onFinishFreshAndLoad()//停止刷新
        springViewNoData.onFinishFreshAndLoad()
    }

    /**
     * 下拉刷新
     */
    fun onPullDownToRefresh() {
        curRequestIndex = 1
        if (couponType in CouponType.IS_IN_CHOOSE_COUPON) {
            getCouponsChooseList()
        } else if (couponType in CouponType.IS_IN_SHOW_COUPON) {
            getCouponsList()
        }
    }

    /**
     * 上拉加载更多
     */
    fun onPullUpToRefresh() {
        curRequestIndex++
        if (couponType in CouponType.IS_IN_CHOOSE_COUPON) {
            getCouponsChooseList()
        } else if (couponType in CouponType.IS_IN_SHOW_COUPON) {
            getCouponsList()
        }
    }

    /**
     * 获取优惠券
     */
    private fun getCouponsList() {
        val data = CouponListRequest()
        data.customerId = UserUtils.getCustomerId()
//            查询会员优惠券接口
        data.size = 10
        data.page = curRequestIndex
        data.type = couponType
        data.method = RequestUrls.QUERY_USER_BINDING_COUPON
        doGet(data, QUERY_COUPON_LIST, Config.LOADING_STRING, true)
    }

    /**
     * 获取支付选择使用的优惠券
     */
    private fun getCouponsChooseList() {
        val data = CouponListChooseRequest()
        data.customerId = UserUtils.getCustomerId()
        data.orderId = orderId ?: ""
        data.method = RequestUrls.QUERY_ORDER_CAN_USE_COUPON
        doGet(data, QUERY_COUPON_CHOOSE_LIST, Config.LOADING_STRING, true)
    }

    private fun refreshListViews() {
        if (coupons.size == 0) {
            springView.visibility = View.GONE
            springViewNoData.visibility = View.VISIBLE
            if (CouponType.CAN_USE == couponType || CouponType.CAN_USE_CHOOSE == couponType) {//可使用
                tvCouponTip.text = "暂无可用优惠券~"
            } else if (CouponType.USED == couponType) {
                tvCouponTip.text = "暂无使用记录~"
            } else if (CouponType.OUT_TIME == couponType) {
                tvCouponTip.text = "暂无过期优惠券~"
            } else if (CouponType.CANNOT_USE_CHOOSE == couponType) {
                tvCouponTip.text = "暂无不可用优惠券~"
            }
        } else {
            springView.visibility = View.VISIBLE
            springViewNoData.visibility = View.GONE

            mCouponsAdapter?.notifyDataSetChanged()
        }
    }

    private fun setCouponChooseData() {
        if (couponType in CouponType.IS_IN_CHOOSE_COUPON) {
            val canUseNumber = couponData?.canUseList?.size ?: 0
            val cannotUseNumber = couponData?.notCanUseList?.size ?: 0
            if (couponType == CouponType.CAN_USE_CHOOSE) {
                val list = couponData?.canUseList?.asIterable()
                if (null != list) {
                    coupons.addAll(list)
                }
            } else if (couponType == CouponType.CANNOT_USE_CHOOSE) {
                val list = couponData?.notCanUseList?.asIterable()
                if (null != list) {
                    coupons.addAll(list)
                }
            }
            refreshListViews()

            //更新数量
            val event = RxBusEvent<Map<String, String>>()
            event.eventCode = RxEventCodes.CODE_UPDATE_COUPON_COUNT
            val map = HashMap<String, String>()
            map.put(CouponType.CAN_USE_CHOOSE, canUseNumber.toString())
            map.put(CouponType.CANNOT_USE_CHOOSE, cannotUseNumber.toString())

            event.content = map
            RxBus.getInstance().post(event)
        }
    }

    private fun setCouponData(data: CouponListResp = CouponListResp()) {
        if (couponType in CouponType.IS_IN_SHOW_COUPON) {
            refreshListViews()

            //更新数量
            val event = RxBusEvent<Map<String, String>>()
            event.eventCode = RxEventCodes.CODE_UPDATE_COUPON_COUNT
            val map = HashMap<String, String>()
            map.put(CouponType.CAN_USE, data.canUseNumber)
            map.put(CouponType.USED, data.usedNumber)
            map.put(CouponType.OUT_TIME, data.outTimeNumber)

            event.content = map
            RxBus.getInstance().post(event)
        }
    }
}