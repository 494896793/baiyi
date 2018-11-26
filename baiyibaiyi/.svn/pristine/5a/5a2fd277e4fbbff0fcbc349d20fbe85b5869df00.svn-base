package www.qisu666.com.adapter;

import android.support.v7.widget.RecyclerView;

import www.qisu666.com.callback.CarResp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Created by wujiancheng on 2017/7/31.
 */

public abstract class ParkListAdapter<VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> {
    private static final String TAG = ParkListAdapter.class.getSimpleName();
    private List<CarResp> mData = new ArrayList<>();

    public ParkListAdapter() {
        setHasStableIds(true);
    }

    public void add(CarResp object) {
        mData.add(object);
        notifyDataSetChanged();
    }

    public void add(int index, CarResp object) {
        mData.add(index, object);
        notifyDataSetChanged();
    }

    public void addAll(Collection<? extends CarResp> collection) {
        if (collection != null) {
            mData.addAll(collection);
            notifyDataSetChanged();
        }
    }

    public void addAll(CarResp... items) {
        addAll(Arrays.asList(items));
    }

    public void clear() {
        mData.clear();
        notifyDataSetChanged();
    }

    public void remove(CarResp object) {
        mData.remove(object);
        notifyDataSetChanged();
    }

    public CarResp getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).hashCode();
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}

