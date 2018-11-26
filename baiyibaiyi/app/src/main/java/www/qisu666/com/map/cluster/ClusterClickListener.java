package www.qisu666.com.map.cluster;

import com.amap.api.maps.model.Marker;

/**
 * Created by yiyi.qi on 16/10/10.
 */

public interface ClusterClickListener {
    /**
     * 点击聚合点的回调处理函数
     *
     * @param marker 点击的聚合点
     */
    void onClick(Marker marker);
}
