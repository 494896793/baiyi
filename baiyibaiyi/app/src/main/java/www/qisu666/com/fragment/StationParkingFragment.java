package www.qisu666.com.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import www.qisu666.com.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class StationParkingFragment extends Fragment {

    public StationParkingFragment() {
        // Required empty public constructor
    }

    public static Fragment newInstance(Bundle bundle){
        StationParkingFragment fragment = new StationParkingFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_station_parking, container, false);
        TextView tv_parking_fee = (TextView) view.findViewById(R.id.tv_parking_fee);
        TextView tv_parking_no = (TextView) view.findViewById(R.id.tv_parking_no);
        TextView tv_guide = (TextView) view.findViewById(R.id.tv_guide);

        tv_parking_fee.setText(getArguments().getString("parking_fee"));
        tv_parking_no.setText(getArguments().getString("parking_lot"));
        tv_guide.setText(getArguments().getString("direction_area"));
        return view;
    }

}
