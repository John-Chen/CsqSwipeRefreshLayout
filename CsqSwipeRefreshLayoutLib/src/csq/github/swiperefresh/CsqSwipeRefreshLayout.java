/**
 * description : 仿照google的SwipeRefreshLayout，可以非常简单的自定义RefreshView，
 *      RefreshView的MeasuredHeight即为判定是否下拉刷新的阀值
 * Created by csq E-mail:csqwyyx@163.com
 * github:https://github.com/John-Chen
 * 14-11-23
 * Created with IntelliJ IDEA
 */

package csq.github.swiperefresh;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;
import android.widget.AbsListView;

public class CsqSwipeRefreshLayout extends ViewGroup {

    // ------------------------ Constants ------------------------

    private static final float ACCELERATE_INTERPOLATION_FACTOR = 1.5f;
    private static final float DECELERATE_INTERPOLATION_FACTOR = 2f;
    private final static float OFFSET_RADIO = 2.0f;


    // ------------------------- Fields --------------------------

    private int mMediumAnimationDuration;  //android.R.integer.config_mediumAnimTime

    private ISwipeRefresh mHeader;
    private View mTarget;

    private int idContent, idHeader;

    /**
     * 刷新拉动的距离
     */
    private float mDistanceToRefresh = -1;

    private OnRefreshListener mListener;

    /**
     * 是否正在刷新
     */
    private boolean mRefreshing = false;
    /**
     * 是否恢复到目标位置动画正在进行
     */
    private boolean mAnimating;

    private final DecelerateInterpolator mDecelerateInterpolator;
    private final AccelerateInterpolator mAccelerateInterpolator;

    private static final int[] LAYOUT_ATTRS = new int[] {
            android.R.attr.enabled
    };


    /**
     * 当前界面的相对偏移量
     */
    private int mCurrentOffset;

    /**
     * 动画开始的相对偏移
     */
    private int mAnimateStartOffset;
    /**
     * 动画结束的相对偏移，如果是刷新的话，就是mHeader的高度，否则就是0
     */
    private int mAnimateEndOffset;
    /**
     * mTarget恢复到目标位置动画
     */
    private final Animation mAnimateToTargetOffset = new Animation() {
        @Override
        public void applyTransformation(float interpolatedTime, Transformation t) {
            int offset = (int) (mAnimateStartOffset +
                    (mAnimateEndOffset - mAnimateStartOffset) * interpolatedTime);
            setTargetOffsetTopAndBottom(offset);
        }
    };
    private final Animation.AnimationListener mAnimateToTargetOffsetListener = new BaseAnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
            super.onAnimationStart(animation);

            mAnimating = true;
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            // Once the target content has returned to its start position, reset
            // the target offset to 0
            mCurrentOffset = mAnimateEndOffset;

            mAnimating = false;
        }
    };

    private int mTargetOffset;
    private final Runnable setTargetOffsetTopAndBottomCallback = new Runnable() {
        @Override
        public void run() {
            removeCallbacks(setTargetOffsetTopAndBottomCallback);

            if(mTargetOffset < 0){
                mTargetOffset = 0;
            }
            int dy = mTargetOffset - mCurrentOffset;
            if(dy != 0){
                mTarget.offsetTopAndBottom(dy);
                mHeader.getView().offsetTopAndBottom(dy);
                mCurrentOffset = mTargetOffset;
            }
        }
    };

    // ----------------------- Constructors ----------------------

    public CsqSwipeRefreshLayout(Context context) {
        this(context, null);
    }

    public CsqSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

        mMediumAnimationDuration = getResources().getInteger(
                android.R.integer.config_mediumAnimTime);

        setWillNotDraw(false);

        mDecelerateInterpolator = new DecelerateInterpolator(DECELERATE_INTERPOLATION_FACTOR);
        mAccelerateInterpolator = new AccelerateInterpolator(ACCELERATE_INTERPOLATION_FACTOR);

        final TypedArray a = context.obtainStyledAttributes(attrs, LAYOUT_ATTRS);
        setEnabled(a.getBoolean(0, true));
        a.recycle();

        final TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CsqSwipeRefreshLayout);
        idContent = ta.getResourceId(R.styleable.CsqSwipeRefreshLayout_IdContentView, 0);
        if(idContent == 0){
            throw new IllegalStateException(
                    "CsqSwipeRefreshLayout must have a child view with styleable IdContentView");
        }

        idHeader = ta.getResourceId(R.styleable.CsqSwipeRefreshLayout_IdHeaderView, 0);
    }

    // -------- Methods for/from SuperClass/Interfaces -----------

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (mHeader != null) {
            mHeader.getView().measure(widthMeasureSpec,
                    MeasureSpec.makeMeasureSpec(
                            getMeasuredHeight(),
                            MeasureSpec.UNSPECIFIED));

            mDistanceToRefresh = mHeader.getView().getMeasuredHeight();

        }else{
            mDistanceToRefresh = dip2px(160);
        }

        if (mTarget != null) {
            mTarget.measure(
                    MeasureSpec.makeMeasureSpec(
                            getMeasuredWidth() - getPaddingLeft() - getPaddingRight(),
                            MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(
                            getMeasuredHeight() - getPaddingTop() - getPaddingBottom(),
                            MeasureSpec.EXACTLY));
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        final int width =  getMeasuredWidth();
        final int height = getMeasuredHeight();
        if (getChildCount() == 0) {
            return;
        }

        if(mTarget != null){
            final int childLeft = getPaddingLeft();
            final int childTop = top + getPaddingTop() + mCurrentOffset;
            final int childWidth = width - getPaddingLeft() - getPaddingRight();
            final int childHeight = height - getPaddingTop() - getPaddingBottom();
            mTarget.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
        }

        if(mHeader != null){
            mHeader.getView().layout(left,
                    top-mHeader.getView().getMeasuredHeight()+ mCurrentOffset,
                    right,
                    top+ mCurrentOffset);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mTarget = findViewById(idContent);

        if(idHeader > 0){
            View h = findViewById(idHeader);
            if(h instanceof ISwipeRefresh){
                mHeader = (ISwipeRefresh) h;

            }else{
                throw new IllegalStateException("CsqSwipeRefreshLayout mHeader must implements ISwipeRefresh");
            }
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    private void animateOffsetToTarget(int targetOffset) {
        if(mCurrentOffset != targetOffset){
            mAnimateStartOffset = mCurrentOffset;
            mAnimateEndOffset = targetOffset;
            mAnimateToTargetOffset.reset();
            mAnimateToTargetOffset.setInterpolator(mDecelerateInterpolator);
            mAnimateToTargetOffset.setDuration(mMediumAnimationDuration);
            mAnimateToTargetOffset.setAnimationListener(mAnimateToTargetOffsetListener);
            mTarget.startAnimation(mAnimateToTargetOffset);
            mHeader.getView().startAnimation(mAnimateToTargetOffset);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean handled = false;
        if (isEnabled() && !mAnimating && !canChildScrollUp()) {
            handled = onTouchEvent(ev);
        }
        Log.e("", "CsqSwipeRefreshLayout  onInterceptTouchEvent = " + handled);
        return handled;
    }

    @Override
    public void requestDisallowInterceptTouchEvent(boolean b) {
        // Nope.
    }

    private int mTouchSlop;
    private float mDownY;
    private float mPrevY;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        boolean handled = false;
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mDownY = event.getY();
                mPrevY = mDownY;
                break;

            case MotionEvent.ACTION_MOVE:
                if (!mAnimating){
                    final float eventY = event.getY();
                    float yDiff = eventY - mDownY;
                    float dy = eventY - mPrevY;

                    if(Math.abs(dy) > mTouchSlop){
                        if(dy > 0){
                            //下拉
                            if (!isRefreshing()) {
                                // User velocity passed min velocity; trigger a refresh
                                // Just track the user's movement
                                if (yDiff/OFFSET_RADIO <= mDistanceToRefresh){
                                    setTriggerPercentage(mAccelerateInterpolator.getInterpolation(yDiff / mDistanceToRefresh));
                                }else{
                                    setTriggerPercentage(1);
                                }

                                int os = (int) ((eventY - mPrevY) / OFFSET_RADIO);
                                setTargetOffsetTopAndBottom(mCurrentOffset + os);
                                handled = true;

                            }else{
                                if (mCurrentOffset < mDistanceToRefresh){
                                    int os = (int) (eventY - mPrevY);
                                    if(mCurrentOffset + os > mDistanceToRefresh){
                                        setTargetOffsetTopAndBottom((int) mDistanceToRefresh);

                                    }else{
                                        setTargetOffsetTopAndBottom(mCurrentOffset + os);
                                    }
                                    handled = true;
                                }
                            }

                        }else{
                            //上划
                            if (mCurrentOffset != 0) {
                                int target = (int) (mCurrentOffset + dy);
                                setTargetOffsetTopAndBottom(target);

                                handled = true;
                            }else{
                                //上划到
                            }
                        }

                        mPrevY = event.getY();
                    }

                }

                break;

            case MotionEvent.ACTION_UP:
                if(mCurrentOffset != 0){
                    final float eventY = event.getY();
                    float yDiff = eventY - mDownY;

                    if(isRefreshing()){
                        if (mCurrentOffset > (int) mDistanceToRefresh) {
                            //超过拉动距离，回退
                            animateOffsetToTarget((int) mDistanceToRefresh);
                        }

                    }else{
                        if (yDiff/OFFSET_RADIO >= mDistanceToRefresh) {
                            //超过拉动距离，刷新
                            setRefreshing(true, false);
                            mListener.onRefresh();
                        }

                        if(isRefreshing()){
                            animateOffsetToTarget((int) mDistanceToRefresh);

                        }else{
                            animateOffsetToTarget(0);
                        }
                    }

                    handled = true;
                }
                break;

            case MotionEvent.ACTION_CANCEL:

                break;
        }
        return handled;
    }

    // --------------------- Methods public ----------------------

    /**
     * Notify the widget that refresh state has changed. Do not call this when
     * refresh is triggered by a swipe gesture.
     *
     * @param refreshing Whether or not the view should show refresh progress.
     */
    public void setRefreshing(boolean refreshing) {
        setRefreshing(refreshing, true);
    }

    /**
     * @return Whether the SwipeRefreshWidget is actively showing refresh
     *         progress.
     */
    public boolean isRefreshing() {
        return mRefreshing;
    }

    public void setRefreshTime(long time) {
        if(mHeader != null){
            mHeader.setRefreshTime(time);
        }
    }

    // --------------------- Methods private ---------------------

    private void setRefreshing(boolean refreshing, boolean animateToTarget) {
        if (mRefreshing != refreshing) {
            mRefreshing = refreshing;

            int mTargetOffset = 0;
            if (mRefreshing) {
                changeHeaderStatus(LoadingStatus.STATE_LOADING);

                if(mHeader != null){
                    mTargetOffset = mHeader.getView().getMeasuredHeight();
                }

            } else {
                changeHeaderStatus(LoadingStatus.STATE_NORMAL);
            }
            if(animateToTarget){
                animateOffsetToTarget(mTargetOffset);
            }
        }
    }

    private void changeHeaderStatus(LoadingStatus status){
        if(mHeader != null){
            mHeader.changeLoadingStatus(status);
        }
    }

    private void setTargetOffsetTopAndBottom(int offset) {
        mTargetOffset = offset;
        post(setTargetOffsetTopAndBottomCallback);
    }

    private float lastTriggerPercent = 0;
    private void setTriggerPercentage(float percent) {
        if(percent <= 1){
            if(percent == 1 && lastTriggerPercent < 1){
                changeHeaderStatus(LoadingStatus.STATE_READY);
            }

            lastTriggerPercent = percent;
            mHeader.setTriggerPercentage(percent);
        }
    }

    /**
     * @return Whether it is possible for the child view of this layout to
     *         scroll up. Override this if the child view is a custom view.
     */
    private boolean canChildScrollUp() {
        if (android.os.Build.VERSION.SDK_INT < 14) {
            if (mTarget instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) mTarget;
                return absListView.getChildCount() > 0
                        && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
                        .getTop() < absListView.getPaddingTop());
            } else {
                return mTarget.getScrollY() > 0;
            }
        } else {
            return ViewCompat.canScrollVertically(mTarget, -1);
        }
    }

    private int dip2px(float dipValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    // --------------------- Getter & Setter ---------------------

    public OnRefreshListener getListener() {
        return mListener;
    }

    public void setOnRefreshListener(OnRefreshListener mListener) {
        this.mListener = mListener;
    }

    public int getCurrentOffset() {
        return mCurrentOffset;
    }


    // --------------- Inner and Anonymous Classes ---------------

    /**
     * Classes that wish to be notified when the swipe gesture correctly
     * triggers a refresh should implement this interface.
     */
    public interface OnRefreshListener {
        public void onRefresh();
    }

    /**
     * Simple AnimationListener to avoid having to implement unneeded methods in
     * AnimationListeners.
     */
    private class BaseAnimationListener implements Animation.AnimationListener {
        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }
    }

    // --------------------- logical fragments -------------------

}