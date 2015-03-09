/**
 * description :
 * Created by csq E-mail:csqwyyx@163.com
 * github:https://github.com/John-Chen
 * 14-11-23
 * Created with IntelliJ IDEA
 */

package csq.github.swiperefresh.models;

import java.util.List;

public interface DataLoadListener<T> {

    public void loadPre(int page);

    public void loadSuccess(List<T> newDatas);

    public void loadFinish(List<T> newDatas);

    public void loadFailed(String error);
}
