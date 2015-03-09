/**
 * description : 列表分页加载数据集合
 * Created by csq E-mail:csqwyyx@163.com
 * github:https://github.com/John-Chen
 * 14-11-23
 * Created with IntelliJ IDEA
 */

package csq.github.swiperefresh.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class PageDatas<I> {

    // ------------------------ Constants ------------------------


    // ------------------------- Fields --------------------------

    private int pageCount = 20;

    /**
     * 加载下一个的页数为curFullLoadedPage+1
     */
    private int curFullLoadedPage = 0;

    private TreeMap<Integer, List<I>> pageMap = new TreeMap<Integer, List<I>>();

    // ----------------------- Constructors ----------------------

    public PageDatas(int pageCount) {
        this.pageCount = pageCount;
    }


    // -------- Methods for/from SuperClass/Interfaces -----------


    // --------------------- Methods public ----------------------

    public synchronized void addPage(int page, List<I> datas){
        if(page == 1){
            clear();
        }

        if(page > curFullLoadedPage){
            pageMap.put(page, datas);
            if(datas.size() == pageCount){
                curFullLoadedPage++;
            }
        }
    }

    public synchronized void clear(){
        pageMap.clear();
        curFullLoadedPage = 0;
    }

    public synchronized List<I> getAll(){
        List<I> rs = new ArrayList<I>();
        if(!pageMap.isEmpty()){
            for(Map.Entry<Integer, List<I>> entry : pageMap.entrySet()){
                rs.addAll(entry.getValue());
            }
        }
        return rs;
    }

    public int getCurFullLoadedPage() {
        return curFullLoadedPage;
    }

    public int getNextPageIndex() {
        return curFullLoadedPage+1;
    }

    // --------------------- Methods private ---------------------


    // --------------------- Getter & Setter ---------------------

    public int getPageCount() {
        return pageCount;
    }


    // --------------- Inner and Anonymous Classes ---------------


    // --------------------- logical fragments -------------------

}