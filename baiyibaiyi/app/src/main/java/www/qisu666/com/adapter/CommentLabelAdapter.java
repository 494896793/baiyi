package www.qisu666.com.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import www.qisu666.com.R;
import www.qisu666.com.view.ColorSpecView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wujiancheng on 2017/8/22.
 */

public class CommentLabelAdapter extends RecyclerView.Adapter<CommentLabelAdapter.CommentViewHolder> {
    private Context mContext;
    private List<String> mData;
    private OnItemClickListener mOnItemClickListener;
    private ArrayList<String> lablesChecked;

    public CommentLabelAdapter(Context context, List<String> data) {
        this.mContext = context;
        this.mData = data;
    }

    public void setCheckedLabels(ArrayList<String> lables){
        lablesChecked = lables;
    }

    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(
                R.layout.item_comments_lable, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CommentViewHolder holder, int position) {
        if (null == mData) {
            return;
        }
        String data = mData.get(position);
        if (null != data) {
            holder.btnLable.setText(data);
            holder.btnLable.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mOnItemClickListener) {
                        mOnItemClickListener.onItemClick(v);
                    }
                }
            });
            if (lablesChecked != null && lablesChecked.contains(data)) {
                holder.btnLable.setTextColor(ContextCompat.getColor(mContext, R.color.main_background));
                holder.btnLable.setBackground(mContext.getResources().getDrawable(R.drawable.corners_bg_et_grays));
                holder.btnLable.setChecked(true);
            } else {
                holder.btnLable.setTextColor(ContextCompat.getColor(mContext, R.color.color_gray_bbbbbb));
                holder.btnLable.setBackground(mContext.getResources().getDrawable(R.drawable.corners_bg_et_gray));
                holder.btnLable.setChecked(false);
            }
        }
    }

    @Override
    public int getItemCount() {
        if (null == mData) {
            return 0;
        }
        return mData.size();
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.btn_lable)
        ColorSpecView btnLable;

        public CommentViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View v);
    }

    public void setOnItemClickListner(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }
}
