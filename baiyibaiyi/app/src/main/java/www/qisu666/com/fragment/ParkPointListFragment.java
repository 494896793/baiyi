package www.qisu666.com.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import www.qisu666.com.R;
import www.qisu666.com.adapter.ParkPointListAdapter;
import www.qisu666.com.callback.ParksResp;
import www.qisu666.com.rx.RxBus;
import www.qisu666.com.rx.RxBusEvent;
import www.qisu666.com.rx.RxEventCodes;

import java.util.List;

/**
 * Created by Administrator on 2017/4/19.
 * 网点列表
 */
public class ParkPointListFragment extends Fragment {

    private Context mContext;
    private ParkPointListAdapter parkPointListAdapter;
    private List<ParksResp> mData;

    private String areaId;//地区id
    private String areaName;//地区名称
    private String carNum;//汽车数量
    private int from = 0;//默认从主页进来搜索

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_park_point_list, container, false);
        RecyclerView rvParkPointList = (RecyclerView) view.findViewById(R.id.rv_park_point_list);
//        rvParkPointList
        mContext = getActivity();

        //获取传过来的数据
        Bundle bundle = getArguments();
        from = bundle.getInt("from");
        areaId = bundle.getString("areaId");
        areaName = bundle.getString("areaName");
        carNum = bundle.getString("carNum");
        mData = (List<ParksResp>) bundle.getSerializable("data");
        parkPointListAdapter = new ParkPointListAdapter(mContext, mData);
        rvParkPointList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));

        rvParkPointList.setAdapter(parkPointListAdapter);

        parkPointListAdapter.setOnItemClickListener(new ParkPointListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, List<ParksResp> data, int position) {
                ParksResp dataBean = data.get(position);
                if (dataBean == null) {
                    return;
                }
                RxBusEvent event = new RxBusEvent();
                if (from == 0) {//从主页进入
                    event.setEventCode(RxEventCodes.CODE_SEARCH_PARK);
                } else if (from == RxEventCodes.CODE_SEARCH_PARK_FROM_PRE_ORDER_HOUR_RENT) {//来自时租预约前的页面
                    event.setEventCode(RxEventCodes.CODE_SEARCH_PARK_FROM_PRE_ORDER_HOUR_RENT);
                } else if (from == RxEventCodes.CODE_SEARCH_PARK_FROM_USE_CAR) {//来自用车页面
                    event.setEventCode(RxEventCodes.CODE_SEARCH_PARK_FROM_USE_CAR);
                }
                event.setContent(dataBean);
                RxBus.getInstance().post(event);

                getActivity().finish();
            }
        });
        return view;
    }
}
