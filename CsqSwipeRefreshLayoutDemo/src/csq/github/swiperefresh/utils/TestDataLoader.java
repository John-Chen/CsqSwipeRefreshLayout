/**
 * description :
 * Created by csq E-mail:csqwyyx@163.com
 * github:https://github.com/John-Chen
 * 14-11-23
 * Created with IntelliJ IDEA
 */

package csq.github.swiperefresh.utils;

import csq.github.swiperefresh.models.DataLoadListener;
import csq.github.swiperefresh.models.PageDatas;
import csq.github.swiperefresh.models.TestData;

import java.util.ArrayList;
import java.util.List;

public class TestDataLoader {

    // ------------------------ Constants ------------------------


    // ------------------------- Fields --------------------------

    private PageDatas<TestData> datas = new PageDatas<TestData>(20);

    private DataLoadListener listener;

    private volatile boolean isLoading = false;

    private int testDataGenerateIndex = 0;

    // ----------------------- Constructors ----------------------

    public TestDataLoader(DataLoadListener listener) {
        this.listener = listener;
    }


    // -------- Methods for/from SuperClass/Interfaces -----------


    // --------------------- Methods public ----------------------

    /**
     * 重新加载数据
     */
    public void loadFirstPage(){
        loadPage(1);
    }

    /**
     * 加载指定页数据
     */
    public void loadPage(final int page){
        if(isLoading){
            return;
        }

        if(page == 1){
            testDataGenerateIndex = 0;
        }

        new CsqAsycTask<List<TestData>>(null){
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                isLoading = true;

                if(listener != null){
                    listener.loadPre(page);
                }
            }

            @Override
            protected void onPostExecute(List<TestData> testData) {
                super.onPostExecute(testData);

                isLoading = false;
            }

            @Override
            protected List<TestData> run() throws Exception {
                try {
                    Thread.sleep(3000);
                } catch (Exception e){
                    e.printStackTrace();
                }

                List<TestData> ds = new ArrayList<TestData>(datas.getPageCount());
                for(int i = 0; i < datas.getPageCount(); i++) {
                    ds.add(new TestData((testDataGenerateIndex++) + ""));
                }

                return ds;
            }

            @Override
            protected void onResult(List<TestData> result) {
                if(result == null){
                    if(listener != null){
                        listener.loadFailed("");
                    }

                }else{
                    datas.addPage(page, result);

                    if(listener != null){
                        List<TestData> ds = datas.getAll();

                        if(result.size() < datas.getPageCount()){
                            listener.loadFinish(ds);

                        }else{
                            listener.loadSuccess(ds);
                        }
                    }
                }
            }

        }.execute();
    }

    /**
     * 加载下一页数据
     */
    public void loadNextPage(){
        final int curPage = datas.getNextPageIndex();

        loadPage(curPage);
    }

    public void clearDatas(){
        datas.clear();
    }

    public int getLoadedPages(){
        return datas.getCurFullLoadedPage();
    }

    // --------------------- Methods private ---------------------


    // --------------------- Getter & Setter ---------------------


    // --------------- Inner and Anonymous Classes ---------------


    // --------------------- logical fragments -------------------

}