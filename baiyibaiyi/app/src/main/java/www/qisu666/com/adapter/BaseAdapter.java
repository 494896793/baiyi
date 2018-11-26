package www.qisu666.com.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;

import www.qisu666.com.utils.ResultUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseAdapter<T> extends android.widget.BaseAdapter {

    private LayoutInflater mInflater;
    private List<T> mData;
    private Context mContext;
    protected onIClick mOnClick;
    protected onLongIClick mOnLClick;

    public BaseAdapter(Context context, T[] data) {
        this(context, Arrays.asList(data));
    }

    public BaseAdapter(Context context, List<T> data) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        if (data != null) {
            this.mData = data;
        } else {
            this.mData = new ArrayList<T>();
        }
    }

    protected String getCon(HashMap data, String key) {
        return ResultUtil.getCon(data, key);
    }

    public void setData(List<T> data) {
        this.mData = data;
        notifyDataSetChanged();
    }

    public void updateData(List<T> data) {
        clear();
        setData(data);
    }

    public void clear() {
        if (mData != null && mData.size() > 0) {
            mData.clear();
            notifyDataSetChanged();
        }
    }

    public List<T> getmData() {
        return mData;
    }

    public void setData(T[] data) {
        setData(Arrays.asList(data));
    }

    public void appendData(List<T> data) {
        this.mData.addAll(data);
    }

    public void appendData(T[] data) {
        appendData(Arrays.asList(data));
    }

    public void deleteItem(int pos) {
        if (mData != null && mData.size() > 0) {
            mData.remove(pos);
            notifyDataSetChanged();
        }
    }

    protected Context getContext() {
        return mContext;
    }

    @Override
    public int getCount() {
        return null == mData ? 0 : mData.size();
    }

    @Override
    public T getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    protected <T extends View> T getH(View view, int id) {
        SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();
        if (viewHolder == null) {
            viewHolder = new SparseArray<View>();
            view.setTag(viewHolder);
        }
        View childView = viewHolder.get(id);
        if (childView == null) {
            childView = view.findViewById(id);
            viewHolder.put(id, childView);
        }
        return (T) childView;
    }

    protected View getV(int layoutId) {
        return mInflater.inflate(layoutId, null);
    }

    public interface onIClick {
        void onClick(Object data, int pos);
    }

    public interface onLongIClick {
        void onLongClick(Map data, int pos);
    }

    public void setOnClick(onIClick mOnClick) {
        this.mOnClick = mOnClick;
    }

    public void setLOnClick(onLongIClick mOnLClick) {
        this.mOnLClick = mOnLClick;
    }

}
