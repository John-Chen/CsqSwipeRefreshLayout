/**
 * description : 自定义RefreshView的通用接口
 * Created by csq E-mail:csqwyyx@163.com
 * github:https://github.com/John-Chen
 * 14-11-23
 * Created with IntelliJ IDEA
 */

package csq.github.swiperefresh;

import android.view.View;

public interface ISwipeRefresh {

    public View getView();

    /**
     * 距离触发刷新的进度
     */
    public void setTriggerPercentage(float percent);

    /**
     * 所有状态改变都会通过此接口回调
     */
    public void changeLoadingStatus(LoadingStatus status);

    /**
     * 设置数据更新时间
     */
    public void setRefreshTime(long time);
}
