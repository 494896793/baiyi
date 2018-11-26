package www.qisu666.com.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import www.qisu666.com.R;
import www.qisu666.com.adapter.StationDetailAdapter;

import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class StationDetailFragment extends BaseFragment {

    private List<Map<String, Object>> list;
    private StationDetailAdapter adapter;

    public StationDetailFragment() {
        // Required empty public constructor
    }

    public static Fragment newInstance(Bundle bundle){
        StationDetailFragment fragment = new StationDetailFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_station_detail, container, false);
        final ListView lv_charging = (ListView) view.findViewById(R.id.lv_charging);
        lv_charging.setFocusable(false);
        list = (List<Map<String, Object>>) getArguments().getSerializable("list");

        /**
         * 静态页面展示的测试数据
         */
//        List<Map<String, Object>> testlist = new ArrayList<>();
//        for (int i=0;i<10;i++){
//            testlist.add(new HashMap<String, Object>());
//        }
//        lv_charging.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent i = new Intent(getActivity(), DeviceDetailActivity.class);
//                i.putExtra("charge_pile_seri", list.get(position).get("charge_pile_num").toString());
//                startActivity(i);
//            }
//        });
        adapter = new StationDetailAdapter(getActivity(), list);
        lv_charging.setAdapter(adapter);
        //setListViewHeightBasedOnChildren(lv_charging);//不能滑动
//        lv_charging.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                lv_charging.getParent().requestDisallowInterceptTouchEvent(false);
//                return true;
//            }
//        });

        return view;
    }

//    @Subscribe
//    public void onEventMainThread(LoginEvent event) {
//        list.clear();
//        list.addAll((List<Map<String, Object>>) getArguments().getSerializable("list"));
//        adapter.notifyDataSetChanged();
//    }

    //调整listview全部显示  setAdapter后调用
    public   void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();

        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }


    @Override
    public int setLayoutResId() {
        return 0;
    }

    @Override
    public void initDatas(View view) {

    }

    @Override
    public void onComplete(String result, int type) {

    }

    @Override
    public void onFailure(String msg, int type) {

    }
}
