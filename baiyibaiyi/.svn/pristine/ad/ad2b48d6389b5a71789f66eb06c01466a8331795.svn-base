package www.qisu666.com.widget;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import www.qisu666.com.R;
import www.qisu666.com.utils.ImageLogic;
import com.bigkoo.convenientbanner.holder.Holder;



/**
 * Created by Sai on 15/8/4.
 * 网络图片加载例子
 */
public class NetworkImageHolderView implements Holder<String> {
    private ImageView imageView;
    private ImageView.ScaleType scaleType;

    public NetworkImageHolderView() {
    }

    public NetworkImageHolderView(ImageView.ScaleType scaleType) {
        this.scaleType = scaleType;
    }

    @Override
    public View createView(Context context) {
        //你可以通过layout文件来创建，也可以像我一样用代码创建，不一定是Image，任何控件都可以进行翻页
        imageView = new ImageView(context);
        if(scaleType==null){
            scaleType = ImageView.ScaleType.FIT_XY;
        }
        imageView.setScaleType(scaleType);
        imageView.setImageResource(R.mipmap.img_banner_default);
        return imageView;
    }

    @Override
    public void UpdateUI(Context context, int position, String data) {
//        Picasso.with(context) .load(data) .fit() .noFade().centerInside() .into(imageView);//
        ImageLogic.sendRequest(data, imageView, 0, 0);
    }
}
