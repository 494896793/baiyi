package www.qisu666.com.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

/**
 * Created by Administrator on 2016/6/2.
 *
 * viewpage 和listview 相互冲突 将父view 传递到viewpage 里面  *
 * 使用父类的方法 parent.requestDisallowInterceptTouchEvent(true);  *
 * 当 requestDisallowInterceptTouchEvent 如果为true的时候 表示:父view 不拦截子view的touch 事件
 */
public class CustomListView extends ListView {
    public CustomListView(Context context) {
        super(context);
    }

    public CustomListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private boolean isDispatch = true;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(!isDispatch){
            getParent().requestDisallowInterceptTouchEvent(false);
            return true;
        }
        getParent().requestDisallowInterceptTouchEvent(true);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        getParent().requestDisallowInterceptTouchEvent(true);
        return super.onInterceptTouchEvent(arg0);
    }

    @Override
    public boolean onTouchEvent(MotionEvent arg0) {
        getParent().requestDisallowInterceptTouchEvent(true);
        return super.onTouchEvent(arg0);
    }

    public void setDispatch(boolean dispatch) {
        isDispatch = dispatch;
    }

    public boolean isDispatch() {
        return isDispatch;
    }
}
