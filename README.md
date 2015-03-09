CsqSwipeRefreshLayout
=====================

参考google的SwipeRefreshLayout，开发的一个可以自定义刷新界面的layout，支持AdapterView、ScrollView。

此控件已经有一个更好的实现：
android-Ultra-Pull-to-Refresh：https://github.com/liaohuqiu/android-Ultra-Pull-To-Refresh
此控件考虑的方面更全面。


CsqSwipeRefreshLayout使用步骤：

1、implements ISwipeRefresh自定义一个顶部刷新的View，View的高度即为刷新的阀值，即下拉View的高度的距离，就会触发刷新：

public class SwipeRefreshHeader extends FrameLayout implements ISwipeRefresh {

    // ------------------------ Constants ------------------------


    // ------------------------- Fields --------------------------

    private ImageView mArrowImageView;
    private ProgressBar mProgressBar;
    private TextView mHintTextView;
    private View lyRefreshTime;
    private TextView tvRefreshTime;
    private Animation mRotateUpAnim;
    private Animation mRotateDownAnim;

    private LoadingStatus mState = LoadingStatus.STATE_NORMAL;

    private final int ROTATE_ANIM_DURATION = 180;

    // ----------------------- Constructors ----------------------

    public SwipeRefreshHeader(Context context) {
        super(context);
        initView(context);
    }

    public SwipeRefreshHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }


    // -------- Methods for/from SuperClass/Interfaces -----------

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void setTriggerPercentage(float percent) {

    }

    @Override
    public void changeLoadingStatus(LoadingStatus status) {
        setState(status);
    }

    @Override
    public void setRefreshTime(long time) {
        if(lyRefreshTime.getVisibility() != View.VISIBLE){
            lyRefreshTime.setVisibility(View.VISIBLE);
        }
        tvRefreshTime.setText(DateUtil.formatTimeSimple(time));
    }

    // --------------------- Methods public ----------------------


    // --------------------- Methods private ---------------------

    private void initView(Context context) {
        // 初始情况，设置下拉刷新view高度为0
        LayoutInflater.from(context).inflate(
                R.layout.view_swipe_refresh_header, this, true);

        mArrowImageView = (ImageView)findViewById(R.id.xlistview_header_arrow);
        mHintTextView = (TextView)findViewById(R.id.xlistview_header_hint_textview);
        mProgressBar = (ProgressBar)findViewById(R.id.xlistview_header_progressbar);
        lyRefreshTime = findViewById(R.id.lyRefreshTime);
        lyRefreshTime.setVisibility(View.GONE);
        tvRefreshTime = (TextView)findViewById(R.id.tvRefreshTime);

        mRotateUpAnim = new RotateAnimation(0.0f, -180.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        mRotateUpAnim.setDuration(ROTATE_ANIM_DURATION);
        mRotateUpAnim.setFillAfter(true);

        mRotateDownAnim = new RotateAnimation(-180.0f, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        mRotateDownAnim.setDuration(ROTATE_ANIM_DURATION);
        mRotateDownAnim.setFillAfter(true);
    }

    private void setState(LoadingStatus state) {
        if (state == mState) return ;

        if (state == LoadingStatus.STATE_LOADING) {	// 显示进度
            mArrowImageView.clearAnimation();

            mArrowImageView.setVisibility(View.INVISIBLE);
            mProgressBar.setVisibility(View.VISIBLE);
        } else {	// 显示箭头图片
            mArrowImageView.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.INVISIBLE);
        }

        switch(state){
            case STATE_NORMAL:
                if (mState == LoadingStatus.STATE_READY) {
                    mArrowImageView.startAnimation(mRotateDownAnim);
                }
                if (mState == LoadingStatus.STATE_LOADING) {
                    mArrowImageView.clearAnimation();
                }
                mHintTextView.setText(R.string.swiperefresh_header_hint_normal);
                break;
            case STATE_READY:
                if (mState != LoadingStatus.STATE_READY) {
                    mArrowImageView.clearAnimation();
                    mArrowImageView.startAnimation(mRotateUpAnim);
                    mHintTextView.setText(R.string.swiperefresh_header_hint_ready);
                }
                break;
            case STATE_LOADING:
                mHintTextView.setText(R.string.swiperefresh_header_hint_loading);
                break;
            default:
                break;
        }


        mState = state;
    }

    // --------------------- Getter & Setter ---------------------


    // --------------- Inner and Anonymous Classes ---------------


    // --------------------- logical fragments -------------------

}



2、布局中定义HeadView及TargetView：

<csq.github.swiperefresh.CsqSwipeRefreshLayout
            android:id="@+id/lySwipeRefresh"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:background="@null"
            app:IdContentView="@+id/lvList"
            app:IdHeaderView="@+id/vSwipeHeader">

        <csq.github.swiperefresh.SwipeRefreshHeader
                android:id="@+id/vSwipeHeader"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                />

        <ListView
                android:id="@+id/lvList"
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:background="@null"
                android:cacheColorHint="@android:color/transparent"
                android:listSelector="@android:color/transparent"
                />

    </csq.github.swiperefresh.CsqSwipeRefreshLayout>
    


3、设置刷新回调OnRefreshListener：

lySwipeRefresh.setOnRefreshListener(refreshListener);

private CsqSwipeRefreshLayout.OnRefreshListener refreshListener = new CsqSwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            pageLoader.loadFirstPage();
        }
    };
    

4、根据数据刷新加载状态，更新界面：

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


暂时此控件还不算特别完善，欢迎交流！
