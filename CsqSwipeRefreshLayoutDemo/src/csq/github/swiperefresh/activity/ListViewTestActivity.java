/**
 * description : CsqSwipeRefreshLayout+ListView测试
 * Created by csq E-mail:csqwyyx@163.com
 * github:https://github.com/John-Chen
 * 14-11-23
 * Created with IntelliJ IDEA
 */

package csq.github.swiperefresh.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import csq.github.swiperefresh.CsqSwipeRefreshLayout;
import csq.github.swiperefresh.R;
import csq.github.swiperefresh.SwipeRefreshHeader;
import csq.github.swiperefresh.models.DataLoadListener;
import csq.github.swiperefresh.models.PageLoadStatus;
import csq.github.swiperefresh.models.TestData;
import csq.github.swiperefresh.utils.TestDataLoader;
import csq.github.swiperefresh.utils.ToastUtil;
import csq.github.swiperefresh.views.LoadNextPageView;

import java.util.Collections;
import java.util.List;

public class ListViewTestActivity extends BaseActivity {

    // ------------------------ Constants ------------------------


    // ------------------------- Fields --------------------------

    private CsqSwipeRefreshLayout lySwipeRefresh;
    private SwipeRefreshHeader vSwipeHeader;
    private ListView lvList;
    private ListAdaper adapter;

    private TestDataLoader pageLoader;
    private CsqSwipeRefreshLayout.OnRefreshListener refreshListener = new CsqSwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            pageLoader.loadFirstPage();
        }
    };
    private LoadNextPageView.LoadNextListener loadNextListener = new LoadNextPageView.LoadNextListener() {
        @Override
        public void loadNext() {
            pageLoader.loadNextPage();
        }
    };

    private DataLoadListener<TestData> dataLoadListener = new DataLoadListener<TestData>() {
        @Override
        public void loadPre(int page) {
            loadNextPageView.setStatus(PageLoadStatus.Loading);
        }

        @Override
        public void loadSuccess(List<TestData> newDatas) {
            lySwipeRefresh.setRefreshing(false);
            lySwipeRefresh.setRefreshTime(System.currentTimeMillis());
            loadNextPageView.setStatus(PageLoadStatus.Finished);

            adapter.updateData(newDatas);
        }

        @Override
        public void loadFinish(List<TestData> newDatas) {
            lySwipeRefresh.setRefreshing(false);
            lySwipeRefresh.setRefreshTime(System.currentTimeMillis());
            loadNextPageView.setStatus(PageLoadStatus.LoadedAll);

            adapter.updateData(newDatas);
        }

        @Override
        public void loadFailed(String error) {
            lySwipeRefresh.setRefreshing(false);
            loadNextPageView.setStatus(PageLoadStatus.Finished);

            ToastUtil.showInfo(ListViewTestActivity.this, "加载失败", false);
        }
    };

    private LoadNextPageView loadNextPageView;

    // ----------------------- Constructors ----------------------


    // -------- Methods for/from SuperClass/Interfaces -----------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview_test);

        initView();

        pageLoader = new TestDataLoader(dataLoadListener);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(isFirstResume()){
            pageLoader.loadFirstPage();
        }
    }

    // --------------------- Methods public ----------------------

    public static void launchActivity(Context mContext){
        Intent intent = new Intent();
        intent.setClass(mContext, ListViewTestActivity.class);
        if(!(mContext instanceof Activity)){
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        mContext.startActivity(intent);
    }


    // --------------------- Methods private ---------------------

    private void initView(){
        lySwipeRefresh = (CsqSwipeRefreshLayout) findViewById(R.id.lySwipeRefresh);
        vSwipeHeader = (SwipeRefreshHeader) findViewById(R.id.vSwipeHeader);
        lvList = (ListView) findViewById(R.id.lvList);

        loadNextPageView = new LoadNextPageView(this, loadNextListener);
        lvList.addFooterView(loadNextPageView);

        adapter = new ListAdaper(this);
        lvList.setAdapter(adapter);

        lySwipeRefresh.setOnRefreshListener(refreshListener);
    }

    // --------------------- Getter & Setter ---------------------


    // --------------- Inner and Anonymous Classes ---------------

    private class ListAdaper extends BaseAdapter{

        private LayoutInflater inflater;
        private List<TestData> datas;

        public ListAdaper(Context context) {
            inflater = LayoutInflater.from(context);
            datas = Collections.EMPTY_LIST;
        }

        public void updateData(List<TestData> datas){
            if(datas == null){
                datas = Collections.EMPTY_LIST;
            }
            this.datas = datas;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public Object getItem(int i) {
            return datas.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder vh = null;
            if(view == null){
                view = inflater.inflate(R.layout.itemview_list, null);
                vh = new ViewHolder(view);
                view.setTag(vh);
            }else{
                vh = (ViewHolder) view.getTag();
            }

            TestData data = (TestData) getItem(i);
            vh.updateData(data);

            return view;
        }
    }

    private class ViewHolder{

        public TextView tvName;

        public ViewHolder(View contentView){
            tvName = (TextView) contentView.findViewById(R.id.tvName);
        }

        public void updateData(TestData data){
            tvName.setText(data.name);
        }
    }

    // --------------------- logical fragments -------------------

}