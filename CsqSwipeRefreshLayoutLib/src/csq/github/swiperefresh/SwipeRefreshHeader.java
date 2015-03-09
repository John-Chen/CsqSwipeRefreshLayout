/**
 * description : 顶部下拉刷新界面的一个实现
 * Created by csq E-mail:csqwyyx@163.com
 * github:https://github.com/John-Chen
 * 14-11-23
 * Created with IntelliJ IDEA
 */

package csq.github.swiperefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.*;
import csq.github.swiperefresh.utils.DateUtil;

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