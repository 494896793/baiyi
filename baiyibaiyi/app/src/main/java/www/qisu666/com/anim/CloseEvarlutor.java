package www.qisu666.com.anim;

import android.animation.TypeEvaluator;

/**
 * Created by admin on 2016/7/19.
 */
public class CloseEvarlutor implements TypeEvaluator<Integer> {
    @Override
    public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
        return (int)(Math.abs(endValue - startValue)*fraction);
    }
}
