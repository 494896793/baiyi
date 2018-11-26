package www.qisu666.com.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import www.qisu666.com.R;
import www.qisu666.com.callback.ImgTextMsgResp;
import www.qisu666.com.utils.Logger;
import www.qisu666.com.utils.StringUtils;
import www.qisu666.com.utils.glideUtil.GlideRoundTransform;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 图文消息
 */
public class ImgTextMsgAdapter extends android.widget.BaseAdapter {
    private static final String TAG = ImgTextMsgAdapter.class.getSimpleName();
    private Context mContext;
    private List<ImgTextMsgResp> data;

    public ImgTextMsgAdapter(Context context, List<ImgTextMsgResp> data) {
        this.mContext = context;
        this.data = data;
    }

    public void setData(List<ImgTextMsgResp> data) {
        if (data != null) {
            this.data = data;
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (null != data) {
            return data.size();
        }
        return 0;
    }

    @Override
    public ImgTextMsgResp getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.listitem_img_text_msg, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final ImgTextMsgResp item = getItem(position);
        if (item != null) {
            String imgUrl = item.getPictureUrl();
            Logger.i("imgUrl = " + imgUrl);
            if (!StringUtils.isEmpty(imgUrl)) {
                Glide.with(mContext).load(imgUrl).asBitmap().transform(new GlideRoundTransform(mContext, 5)).into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        //图片的宽高按等比例缩放，布局中要添加
//                        android:adjustViewBounds="true"
//                        android:scaleType="fitXY"
                        holder.ivImg.setImageBitmap(resource);
                    }
                });
            }
        }
        return convertView;
    }


    static class ViewHolder {
        @BindView(R.id.ivImg)
        ImageView ivImg;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
