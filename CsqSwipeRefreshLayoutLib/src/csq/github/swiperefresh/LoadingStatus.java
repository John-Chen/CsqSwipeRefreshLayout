/**
 * description : 加载状态
 * Created by csq E-mail:csqwyyx@163.com
 * github:https://github.com/John-Chen
 * 14-11-23
 * Created with IntelliJ IDEA
 */

package csq.github.swiperefresh;

public enum LoadingStatus {

    /**
     * 正常未加载数据状态, 显示“下拉刷新”
     */
    STATE_NORMAL,

    /**
     * 准备状态，显示“松开加载数据”
     */
    STATE_READY,

    /**
     * 加载数据状态
     */
    STATE_LOADING;

}
