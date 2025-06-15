
package com.zhangpan.site.calendarview;

import static com.zhangpan.site.calendarview.MonthViewPager.MIN_DAMPING;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.PathInterpolator;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;

public class CalendarLayout extends LinearLayout {

    private static final String TAG = "CalendarLayout";

    private static final int MONTH_WEEK_VIEW_SWITCH_MIN_VELOCITY = 800;

    private static final int ANIMATE_DURATION = 300;

    private static final float EXPAND_MONTH_VIEW_SLIDE_MAX_PERCENT = 0.35f;
    private static final int EXPAND_MONTH_VIEW_MIN_VELOCITY = 1200;

    private static final float EXPAND_MONTH_VIEW_MAX_VELOCITY = 26000f;

    private static final long EXPAND_MONTH_VIEW_ANIM_DURATION = 300;
    private static final long EXPAND_MONTH_VIEW_ANIM_MIN_DURATION = 50;

    public static final long EXPAND_MONTH_VIEW_SPRING_ANIM_DURATION = 400;

    private static final long SHOW_YEAR_VIEW_SCALE_ANIM_DURATION = 580;

    private static final long SHOW_YEAR_VIEW_ALPHA_ANIM_DURATION = 100;

    private static final long SHOW_YEAR_VIEW_ANIMATE_DURATION = 580L;


    /**
     * 月视图模式，即非全屏状态的月视图模式，日历视图的默认模式
     */
    public static final int STATUS_MONTH_VIEW_FOLD = 0;

    /**
     * 周视图模式
     */
    public static final int STATUS_WEEK_VIEW = 1;

    /**
     * 月视图模式，月视图全屏展开模式
     */
    public static final int STATUS_MONTH_VIEW_EXPANDED = 2;

    /**
     * 多点触控支持
     */
    private int mActivePointerId;

    private static final int ACTIVE_POINTER = 1;

    private static final int INVALID_POINTER = -1;

    /**
     * 设置日历可以显示周视图与月视图
     */
    private static final int CALENDAR_SHOW_MODE_BOTH_MONTH_WEEK_VIEW = 0;

    /**
     * 设置日历仅可以显示周视图
     */
    private static final int CALENDAR_SHOW_MODE_ONLY_WEEK_VIEW = 1;

    /**
     * 设置日历仅可以显示月视图
     */
    private static final int CALENDAR_SHOW_MODE_ONLY_MONTH_VIEW = 2;

    /**
     * 标记默认是周视图模式还是月视图模式
     */
    private final int mDefaultCalendarMode;

    /**
     * 日历视图当前的状态
     * 可选值为 {@link CalendarStatus} 中表示的几种状态
     */
    private @CalendarStatus int mCalendarStatus;

    private boolean isWeekView;

    /**
     * 星期栏
     */
    WeekBar mWeekBar;

    /**
     * 月视图 ViewPager
     */
    MonthViewPager mMonthView;

    private FrameLayout mMonthWeekViewParent;

    /**
     * 日历视图，包含了星期栏、周视图、月视图、以及年视图
     */
    CalendarView mCalendarView;

    /**
     * 周视图 ViewPager
     */
    WeekViewPager mWeekPager;

    /**
     * 年视图 ViewPager
     */
    YearViewPager mYearView;

    /**
     * ContentView
     */
    ViewGroup mContentView;

    /**
     * 默认手势
     */
    private static final int GESTURE_MODE_DEFAULT = 0;

    /**
     * 禁用手势
     */
    private static final int GESTURE_MODE_DISABLED = 2;

    /**
     * 手势模式
     */
    private final int mGestureMode;

    private int mCalendarShowMode;
    /**
     * ContentView  可滑动的最大距离距离
     */
    private int mContentViewTranslateY;
    /**
     * ViewPager 可以平移的距离，不代表 mMonthView 的平移距离
     */
    private int mViewPagerTranslateY = 0;

    private float mDownY;
    private float mLastY;
    private float mLastX;
    /**
     * 是否正在执行月视图与周视图切换动画
     */
    private boolean isMonthViewWeekViewSwitchAnimating = false;
    /**
     * 是否正在执行显示或隐藏年视图的动画
     */
    private boolean mYearViewAnimating = false;
    /**
     * 内容布局id
     */
    private final int mContentViewId;

    private int mItemHeight;

    private CalendarViewDelegate mDelegate;

    private CalendarStatusChangeListener calendarStatusChangeListener;
    /**
     * 月视图是否支持全屏展开
     */
    private boolean mMonthViewExpandable = false;

    /**
     * 当前 CalendarLayout 的高度
     */
    private int mCalendarLayoutHeight;

    /**
     * 手速判断
     */
    private final VelocityTracker mVelocityTracker;
    private final int mMaximumVelocity;

    private final int mTouchSlop;

    private PathInterpolator mScalePathInterpolator;

    private PathInterpolator mSpringInterpolator;

    private PathInterpolator mAlphaInterpolator;
    /**
     * 年视图中每个月份对应的坐标
     */
    private final HashMap<Integer, Pair<Integer, Integer>> monthViewCoordinateMap = new HashMap<>();

    public CalendarLayout(Context context) {
        this(context, null);
    }

    public CalendarLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOrientation(LinearLayout.VERTICAL);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CalendarLayout);
        mContentViewId = array.getResourceId(R.styleable.CalendarLayout_calendar_content_view_id, 0);
        mDefaultCalendarMode = array.getInt(R.styleable.CalendarLayout_default_status, STATUS_MONTH_VIEW_FOLD);
        mCalendarStatus = mDefaultCalendarMode;
        mCalendarShowMode = array.getInt(R.styleable.CalendarLayout_calendar_show_mode, CALENDAR_SHOW_MODE_BOTH_MONTH_WEEK_VIEW);
        mGestureMode = array.getInt(R.styleable.CalendarLayout_gesture_mode, GESTURE_MODE_DEFAULT);
        array.recycle();
        mVelocityTracker = VelocityTracker.obtain();
        final ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = configuration.getScaledTouchSlop();
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        if (mContentView == null || mCalendarView == null) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }

        int year = mDelegate.mIndexCalendar.getYear();
        int month = mDelegate.mIndexCalendar.getMonth();
        int weekBarHeight = CalendarUtil.dipToPx(getContext(), 1)
                + mDelegate.getWeekBarHeight();

        int monthHeight = CalendarUtil.getMonthViewHeight(year, month, mDelegate) + weekBarHeight;

        int height = MeasureSpec.getSize(heightMeasureSpec);

        if (mDelegate.isFullScreenCalendar()) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            int heightSpec = MeasureSpec.makeMeasureSpec(height - weekBarHeight - mDelegate.getCalendarItemHeight(),
                    MeasureSpec.EXACTLY);
            mContentView.measure(widthMeasureSpec, heightSpec);
            mContentView.layout(mContentView.getLeft(), mContentView.getTop(), mContentView.getRight(), mContentView.getBottom());
            return;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (monthHeight >= height && mMonthView.getHeight() > 0) {
            height = monthHeight;
        }

        int h = getH(height, monthHeight, weekBarHeight);

        int heightSpec = MeasureSpec.makeMeasureSpec(h, MeasureSpec.EXACTLY);
        mContentView.measure(widthMeasureSpec, heightSpec);
        mContentView.layout(mContentView.getLeft(), mContentView.getTop(), mContentView.getRight(), mContentView.getBottom());
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mMonthView = findViewById(R.id.vp_month);
        mWeekPager = findViewById(R.id.vp_week);
        mMonthWeekViewParent = findViewById(R.id.frameContent);
        if (getChildCount() > 0) {
            mCalendarView = findCalendarView();
            if (mCalendarView != null) {
                mCalendarView.setOnMonthViewExpandStateChangedListener(expand -> {
                    if (expand) {
                        calendarStatusChanged(STATUS_MONTH_VIEW_EXPANDED);
                    } else {
                        calendarStatusChanged(STATUS_MONTH_VIEW_FOLD);
                    }
                });
            }
        }
        mContentView = findViewById(mContentViewId);
        mYearView = findViewById(R.id.year_view_pager);
        post(() -> updateCalendarViewExpandHeight(getHeight()));
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (isMonthViewWeekViewSwitchAnimating) {
            return super.dispatchTouchEvent(ev);
        }
        if (mGestureMode == GESTURE_MODE_DISABLED) {
            return super.dispatchTouchEvent(ev);
        }
        if (mYearView == null ||
                mCalendarView == null || mCalendarView.getVisibility() == GONE ||
                mContentView == null ||
                mContentView.getVisibility() != VISIBLE) {
            return super.dispatchTouchEvent(ev);
        }

        if (mCalendarShowMode == CALENDAR_SHOW_MODE_ONLY_MONTH_VIEW ||
                mCalendarShowMode == CALENDAR_SHOW_MODE_ONLY_WEEK_VIEW) {
            return super.dispatchTouchEvent(ev);
        }

        if (mYearView.getVisibility() == VISIBLE || mDelegate.isShowYearSelectedLayout) {
            return super.dispatchTouchEvent(ev);
        }
        final int action = ev.getAction();
        float y = ev.getY();
        if (action == MotionEvent.ACTION_MOVE) {
            float dy = y - mLastY;
            /*
             * 如果向下滚动，有 2 种情况处理 且y在ViewPager下方
             * 1、RecyclerView 或者其它滚动的View，当mContentView滚动到顶部时，拦截事件
             * 2、非滚动控件，直接拦截事件
             */
            if (dy > 0 && mContentView.getTranslationY() == -mContentViewTranslateY) {
                if (isScrollTop()) {
                    requestDisallowInterceptTouchEvent(false);//父View向子View拦截分发事件
                    return super.dispatchTouchEvent(ev);
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mGestureMode == GESTURE_MODE_DISABLED ||
                mCalendarShowMode == CALENDAR_SHOW_MODE_ONLY_MONTH_VIEW ||
                mCalendarShowMode == CALENDAR_SHOW_MODE_ONLY_WEEK_VIEW) {//禁用手势，或者只显示某种视图
            return false;
        }
        if (mDelegate == null) {
            return false;
        }
        if (mDelegate.isShowYearSelectedLayout) {
            return false;
        }

        if (mContentView == null || mCalendarView == null || mCalendarView.getVisibility() == GONE) {
            return false;
        }

        int action = event.getAction();
        float y = event.getY();
        mVelocityTracker.addMovement(event);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                int index = event.getActionIndex();
                mActivePointerId = event.getPointerId(index);
                mLastY = mDownY = y;
                return true;
            case MotionEvent.ACTION_POINTER_DOWN: {
                final int i = event.getActionIndex();
                mActivePointerId = event.getPointerId(i);
                if (mActivePointerId == 0) {
                    // 核心代码：就是让下面的 dy = y- mLastY == 0，避免抖动
                    mLastY = event.getY(mActivePointerId);
                }
                break;
            }
            case MotionEvent.ACTION_MOVE:
                getPointerIndex(event, mActivePointerId);
                if (mActivePointerId == INVALID_POINTER) {
                    // 如果切换了手指，那把mLastY换到最新手指的y坐标即可，核心就是让下面的 dy== 0，避免抖动
                    mLastY = y;
                    mActivePointerId = ACTIVE_POINTER;
                }
                float dy = y - mLastY;

                // 向上滑动，并且contentView平移到最大距离，显示周视图
                if (dy < 0 && mContentView.getTranslationY() == -mContentViewTranslateY) {
                    mLastY = y;
                    event.setAction(MotionEvent.ACTION_DOWN);
                    dispatchTouchEvent(event);
                    mWeekPager.setVisibility(VISIBLE);
                    mMonthView.setVisibility(INVISIBLE);
                    if (!isWeekView && mDelegate.mViewChangeListener != null) {
                        mDelegate.mViewChangeListener.onViewChange(false);
                    }
                    isWeekView = true;
                    return true;
                }
                hideWeek(false);

                // 手指向下滑动，并且contentView已经完全平移到底部
                if (dy > 0 && mContentView.getTranslationY() + dy >= 0) {
                    mContentView.setTranslationY(0);
                    translationViewPager();
                    mLastY = y;
                    if (mMonthViewExpandable) {
                        int calendarViewHeight = mCalendarView.getHeight();
                        if (calendarViewHeight < mCalendarLayoutHeight) {
                            if (calendarViewHeight + dy > mCalendarLayoutHeight) {
                                mCalendarView.monthViewExpandOrFold(mCalendarLayoutHeight - calendarViewHeight);
                            } else {
                                mCalendarView.monthViewExpandOrFold(dy);
                            }
                        } else {
                            // 月视图全屏展开后继续下来 OverScroll 效果
                            int deltaY = -(int) dy / mMonthView.calculateDamping();
                            int scrollY = mMonthView.getScrollY();
                            Log.d(TAG, "scrollY:" + scrollY);
                            int monthViewOverScrollHeight = mMonthView.getMonthViewOverScrollHeight();
                            if (Math.abs(scrollY) < monthViewOverScrollHeight) {
                                if (Math.abs(scrollY) + deltaY > monthViewOverScrollHeight) {
                                    mMonthView.scrollBy(0, monthViewOverScrollHeight - Math.abs(scrollY));
                                } else {
                                    mMonthView.scrollBy(0, deltaY);
                                }
                            }
                        }
                    }
                    return super.onTouchEvent(event);
                }
                // 手指向上滑
                if (dy < 0) {
                    //  向上滑动，并且contentView已经平移到最大距离，则contentView平移到最大的距离
                    if (mContentView.getTranslationY() + dy <= -mContentViewTranslateY) {
                        mContentView.setTranslationY(-mContentViewTranslateY);
                        translationViewPager();
                        mLastY = y;
                        return super.onTouchEvent(event);
                    }

                    if (mMonthViewExpandable) {
                        // 月视图支持全屏展开，并且月视图未完全收缩，则继续收缩月视图
                        if (mCalendarView.isMonthViewExpandingOrExpanded()) {
                            mLastY = y;
                            int scrollY = Math.abs(mMonthView.getScrollY());
                            if (scrollY > 0) {
                                int deltaY = Math.abs((int) dy / MIN_DAMPING);
                                mMonthView.scrollBy(0, Math.min(deltaY, scrollY));
                                return super.onTouchEvent(event);
                            }

                            int calendarViewHeight = mCalendarView.getHeight();
                            int monthViewInitialHeight = mCalendarView.getCalendarViewOriginHeight();
                            if (calendarViewHeight + dy < monthViewInitialHeight) {
                                mCalendarView.monthViewExpandOrFold(monthViewInitialHeight - calendarViewHeight);
                            } else {
                                mCalendarView.monthViewExpandOrFold(dy);
                            }
                            return super.onTouchEvent(event);
                        }
                    }
                }

                //否则按比例平移
                mContentView.setTranslationY(mContentView.getTranslationY() + dy);
                translationViewPager();
                mLastY = y;
                break;
            case MotionEvent.ACTION_CANCEL:

            case MotionEvent.ACTION_POINTER_UP:
                int pointerIndex = getPointerIndex(event, mActivePointerId);
                if (mActivePointerId == INVALID_POINTER)
                    break;
                mLastY = event.getY(pointerIndex);
                break;
            case MotionEvent.ACTION_UP:
                final VelocityTracker velocityTracker = mVelocityTracker;
                velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                float velocity = velocityTracker.getYVelocity();
                if (mMonthViewExpandable) {
                    // OverScroll 回弹
                    if (Math.abs(mMonthView.getScrollY()) > 0) {
                        mMonthView.smoothScrollToY(0);
                        break;
                    }

                    if (mCalendarView.isMonthViewExpandingOrExpanded()) {
                        float fingerSlideDistance = event.getY() - mDownY;
                        float monthViewExpandPercent = mDelegate.getMonthViewExpandPercent();
                        float absVelocity = Math.abs(velocity);
                        long duration = calculateDuration(absVelocity);
                        if (fingerSlideDistance > 0) {
                            // 向下滑动展开月视图
                            if (monthViewExpandPercent >= EXPAND_MONTH_VIEW_SLIDE_MAX_PERCENT
                                    || absVelocity >= EXPAND_MONTH_VIEW_MIN_VELOCITY) {
                                mCalendarView.expandMonthView(duration);
                            } else {
                                mCalendarView.foldMonthView(EXPAND_MONTH_VIEW_SPRING_ANIM_DURATION);
                            }
                        } else if (fingerSlideDistance < 0) {
                            // 向上滑动折叠月视图
                            if (monthViewExpandPercent <= 1 - EXPAND_MONTH_VIEW_SLIDE_MAX_PERCENT
                                    || absVelocity >= EXPAND_MONTH_VIEW_MIN_VELOCITY) {
                                mCalendarView.foldMonthView(duration);
                            } else {
                                mCalendarView.expandMonthView(EXPAND_MONTH_VIEW_SPRING_ANIM_DURATION);
                            }
                        }
                    }
                }

                if (mContentView.getTranslationY() == 0
                        || mContentView.getTranslationY() == mContentViewTranslateY) {
                    if (mMonthView.getVisibility() != View.VISIBLE) {
                        changeToMonthViewStatus();
                    }
                    break;
                }
                if (Math.abs(velocity) >= MONTH_WEEK_VIEW_SWITCH_MIN_VELOCITY) {
                    if (velocity < 0) {
                        changeToWeekViewStatus();
                    } else {
                        changeToMonthViewStatus();
                    }
                    return super.onTouchEvent(event);
                }
                if (event.getY() - mDownY > 0) {
                    changeToMonthViewStatus();
                } else {
                    changeToWeekViewStatus();
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (isMonthViewWeekViewSwitchAnimating) {
            return true;
        }
        if (mGestureMode == GESTURE_MODE_DISABLED) {
            return false;
        }
        if (mYearView == null ||
                mCalendarView == null || mCalendarView.getVisibility() == GONE ||
                mContentView == null ||
                mContentView.getVisibility() != VISIBLE) {
            return super.onInterceptTouchEvent(ev);
        }

        if (mCalendarShowMode == CALENDAR_SHOW_MODE_ONLY_MONTH_VIEW ||
                mCalendarShowMode == CALENDAR_SHOW_MODE_ONLY_WEEK_VIEW) {
            return false;
        }

        if (mYearView.getVisibility() == VISIBLE || mDelegate.isShowYearSelectedLayout) {
            return super.onInterceptTouchEvent(ev);
        }
        final int action = ev.getAction();
        float y = ev.getY();
        float x = ev.getX();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                int index = ev.getActionIndex();
                mActivePointerId = ev.getPointerId(index);
                mLastY = mDownY = y;
                mLastX = x;
                break;
            case MotionEvent.ACTION_MOVE:
                float dy = y - mLastY;
                float dx = x - mLastX;
                //  如果向上滚动，且ViewPager已经收缩，不拦截事件
                if (dy < 0 && mContentView.getTranslationY() == -mContentViewTranslateY) {
                    return false;
                }
                // 滑动距离过小，不拦截事件,竖直滑动距离小于横向滑动距离不拦截事件
                if (Math.abs(dy) < mTouchSlop || Math.abs(dy) < Math.abs(dx)) {
                    return false;
                }
                // 如果向下滚动，有 2 种情况处理 且y在ViewPager下方
                // 1、RecyclerView 或者其它滚动的View，当mContentView滚动到顶部时，拦截事件
                // 2、非滚动控件，直接拦截事件
                if (dy > 0 && mContentView.getTranslationY() == -mContentViewTranslateY
                        && y >= mDelegate.getCalendarItemHeight() + mDelegate.getWeekBarHeight()) {
                    if (!isScrollTop()) {
                        return false;
                    }
                }

                // 月视图支持展开时下滑依然需要拦截事件
                if (dy > 0 && mContentView.getTranslationY() == 0
                        && y >= CalendarUtil.dipToPx(getContext(), 98)) {
                    return mMonthViewExpandable;
                }
                if ((dy > 0 && mContentView.getTranslationY() <= 0)
                        || (dy < 0 && mContentView.getTranslationY() >= -mContentViewTranslateY)
                        || mMonthViewExpandable) {
                    mLastY = y;
                    return true;
                }
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    /**
     * 窗口大小发生变化，更新月视图展开的高度
     * @param w Current width of this view.
     * @param h Current height of this view.
     * @param oldWidth Old width of this view.
     * @param oldHeight Old height of this view.
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldWidth, int oldHeight) {
        super.onSizeChanged(w, h, oldWidth, oldHeight);
        if (getHeight() != mCalendarLayoutHeight) {
            updateCalendarViewExpandHeight(getHeight());
            if (isMonthViewExpanded()) {
                expandMonthView(0);
            }
        }
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        Parcelable parcelable = super.onSaveInstanceState();
        bundle.putParcelable("super", parcelable);
        bundle.putBoolean("isExpand", isMonthViewStatus());
        bundle.putBoolean("isMonthViewExpand", mCalendarView.isMonthViewExpand());
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        Bundle bundle = (Bundle) state;
        Parcelable superData = bundle.getParcelable("super");
        boolean isMonthViewExpand = bundle.getBoolean("isMonthViewExpand");
        if (isMonthViewExpand) {
            expandMonthView(0);
        } else {
            boolean isExpand = bundle.getBoolean("isExpand");
            if (isExpand) {
                changeToMonthViewStatus(0);
            } else {
                changeToWeekViewStatus(0);
            }
        }
        super.onRestoreInstanceState(superData);
    }

    /**
     * 初始化
     * @param delegate delegate
     */
    final void setup(CalendarViewDelegate delegate) {
        this.mDelegate = delegate;
        mItemHeight = mDelegate.getCalendarItemHeight();
        mMonthViewExpandable = mDelegate.monthViewExpandable();
        initCalendarPosition(delegate.mSelectedCalendar.isAvailable() ?
                delegate.mSelectedCalendar :
                delegate.createCurrentDate());
        updateContentViewTranslateY();
    }

    /**
     * 周视图切换为非全屏月视图模式
     */
    public void changeToMonthViewStatus() {
        changeToMonthViewStatus(ANIMATE_DURATION);
    }

    /**
     * 周视图切换为非全屏月视图模式
     * @param duration 时长
     */
    public void changeToMonthViewStatus(int duration) {
        if (isMonthViewWeekViewSwitchAnimating ||
                mCalendarShowMode == CALENDAR_SHOW_MODE_ONLY_WEEK_VIEW ||
                mContentView == null)
            return;
        if (mMonthView.getVisibility() != VISIBLE) {
            mWeekPager.setVisibility(GONE);
            onShowMonthView();
            isWeekView = false;
            mMonthView.setVisibility(VISIBLE);
        }
        post(new Runnable() {
            @Override
            public void run() {
                ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mContentView,
                        "translationY", mContentView.getTranslationY(), 0f);
                objectAnimator.setDuration(duration);
                objectAnimator.setInterpolator(mDelegate.getPathInterpolator());
                objectAnimator.addUpdateListener(animation -> {
                    float currentValue = (Float) animation.getAnimatedValue();
                    float percent = currentValue / mContentViewTranslateY;
                    mMonthView.setTranslationY(mViewPagerTranslateY * percent);
                    isMonthViewWeekViewSwitchAnimating = true;
                });
                objectAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        isMonthViewWeekViewSwitchAnimating = false;
                        if (mGestureMode == GESTURE_MODE_DISABLED) {
                            requestLayout();
                        }
                        hideWeek(true);
                        if (mDelegate.mViewChangeListener != null && isWeekView) {
                            mDelegate.mViewChangeListener.onViewChange(true);
                        }
                        isWeekView = false;
                        calendarStatusChanged(STATUS_MONTH_VIEW_FOLD);
                    }
                });
                objectAnimator.start();
            }
        });
    }

    /**
     * 非全屏月视图切换为周视图模式
     */
    @SuppressWarnings("UnusedReturnValue")
    public void changeToWeekViewStatus() {
        changeToWeekViewStatus(ANIMATE_DURATION);
    }

    /**
     * 非全屏月视图切换为周视图模式
     * @param duration 时长
     */
    public void changeToWeekViewStatus(int duration) {
        if (mGestureMode == GESTURE_MODE_DISABLED) {
            requestLayout();
        }
        if (isMonthViewWeekViewSwitchAnimating || mContentView == null) {
            return;
        }
        post(new Runnable() {
            @Override
            public void run() {
                ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mContentView,
                        "translationY", mContentView.getTranslationY(), -mContentViewTranslateY);
                objectAnimator.setDuration(duration);
                objectAnimator.addUpdateListener(animation -> {
                    float currentValue = (Float) animation.getAnimatedValue();
                    float percent = currentValue / mContentViewTranslateY;
                    mMonthView.setTranslationY(mViewPagerTranslateY * percent);
                    isMonthViewWeekViewSwitchAnimating = true;
                });
                objectAnimator.setInterpolator(mDelegate.getPathInterpolator());
                objectAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        isMonthViewWeekViewSwitchAnimating = false;
                        showWeek();
                        isWeekView = true;
                        calendarStatusChanged(STATUS_WEEK_VIEW);
                    }
                });
                objectAnimator.start();
            }
        });
    }

    /**
     * 非全屏月视图模式切换为全屏月视图模式
     * @param duration 展开月视图动画时间
     */
    public void expandMonthView(long duration) {
        mCalendarView.expandMonthView(duration);
    }

    /**
     * 全屏月视图模式切换为非全屏月视图
     * @param duration 月视图折叠动画时间
     */
    public void foldMontView(long duration) {
        mCalendarView.foldMonthView(duration);
    }

    /**
     * 显示年视图
     * @param year 当前选中年份
     * @param month 当前选中月份
     */
    public void showYearView(int year, int month) {
        if (mYearViewAnimating) {
            return;
        }
        postDelayed(() -> mYearViewAnimating = false, SHOW_YEAR_VIEW_SCALE_ANIM_DURATION);
        mYearViewAnimating = true;
        mYearView.setVisibility(View.VISIBLE);
        mDelegate.isShowYearSelectedLayout = true;
        mYearView.scrollToYear(year, false);
        if (isMonthViewExpanded()) {
            scaleYearView(true);
            scaleMonthView(true);
        } else if (isWeekViewStatus()) {
            scaleYearView(true);
            scaleMonthView(true);
        } else {
            showYearViewWithMonthAnimation(year, month);
        }
    }

    /**
     * 隐藏年视图
     */
    public void hideYearView() {
        hideYearView(null);
    }

    /**
     * 隐藏年视图
     */
    public void hideYearView(@Nullable AnimationEndListener animationEndListener) {
        if (mYearViewAnimating) {
            if (animationEndListener != null) {
                animationEndListener.onAnimationEnd(false);
            }
            return;
        }
        mYearViewAnimating = true;
        postDelayed(() -> {
            mYearViewAnimating = false;
            if (animationEndListener != null) {
                animationEndListener.onAnimationEnd(false);
            }
        }, SHOW_YEAR_VIEW_SCALE_ANIM_DURATION);
        if (isMonthViewExpanded()) {
            scaleYearView(false);
            scaleMonthView(false);
        } else if (isWeekViewStatus()) {
            scaleYearView(false);
            scaleMonthView(false);
        } else {
            hideYearViewWithMonthAnimation();
        }
    }

    /**
     * 隐藏日历
     */
    public void hideCalendarView() {
        if (mCalendarView == null) {
            return;
        }
        mCalendarView.setVisibility(GONE);
        if (!isMonthViewStatus()) {
            changeToMonthViewStatus(0);
        }
        requestLayout();
    }

    /**
     * 显示日历
     */
    public void showCalendarView() {
        mCalendarView.setVisibility(VISIBLE);
        requestLayout();
    }

    public void setModeBothMonthWeekView() {
        mCalendarShowMode = CALENDAR_SHOW_MODE_BOTH_MONTH_WEEK_VIEW;
        requestLayout();
    }

    public void setModeOnlyWeekView() {
        mCalendarShowMode = CALENDAR_SHOW_MODE_ONLY_WEEK_VIEW;
        requestLayout();
    }

    public void setModeOnlyMonthView() {
        mCalendarShowMode = CALENDAR_SHOW_MODE_ONLY_MONTH_VIEW;
        requestLayout();
    }

    public boolean isMonthViewExpanded() {
        return mCalendarView.isMonthViewExpand();
    }

    /**
     * 是否是月视图模式
     */
    public final boolean isMonthViewStatus() {
        return mCalendarStatus == STATUS_MONTH_VIEW_FOLD || mCalendarStatus == STATUS_MONTH_VIEW_EXPANDED;
    }

    public final boolean isWeekViewStatus() {
        return mCalendarStatus == STATUS_WEEK_VIEW;
    }

    public boolean isYearViewShow() {
        return mYearView.getVisibility() == View.VISIBLE;
    }

    /**
     * @return 是否正在执行年视图显示或隐藏动画
     */
    public boolean isYearViewAnimating() {
        return mYearViewAnimating;
    }

    /**
     * 设置监听年视图切换年份的 Listener
     */
    public void setOnYearViewYearChangeListener(YearViewPager.OnYearViewChangeListener onYearViewChangeListener) {
        mYearView.setOnYearViewChangeListener(onYearViewChangeListener);
    }

    /**
     * 初始化状态
     */
    final void initStatus() {
        if ((mDefaultCalendarMode == STATUS_WEEK_VIEW ||
                mCalendarShowMode == CALENDAR_SHOW_MODE_ONLY_WEEK_VIEW) &&
                mCalendarShowMode != CALENDAR_SHOW_MODE_ONLY_MONTH_VIEW) {
            if (mContentView == null) {
                mWeekPager.setVisibility(VISIBLE);
                mMonthView.setVisibility(GONE);
                return;
            }
            post(new Runnable() {
                @Override
                public void run() {
                    ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mContentView,
                            "translationY", mContentView.getTranslationY(), -mContentViewTranslateY);
                    objectAnimator.setDuration(0);
                    objectAnimator.addUpdateListener(animation -> {
                        float currentValue = (Float) animation.getAnimatedValue();
                        float percent = currentValue / mContentViewTranslateY;
                        mMonthView.setTranslationY(mViewPagerTranslateY * percent);
                        isMonthViewWeekViewSwitchAnimating = true;
                    });
                    objectAnimator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            isMonthViewWeekViewSwitchAnimating = false;
                            isWeekView = true;
                            showWeek();
                            if (mDelegate == null || mDelegate.mViewChangeListener == null) {
                                return;
                            }
                            mDelegate.mViewChangeListener.onViewChange(false);
                        }
                    });
                    objectAnimator.start();
                }
            });
        } else {
            if (mDelegate.mViewChangeListener == null) {
                return;
            }
            post(() -> mDelegate.mViewChangeListener.onViewChange(true));
        }
    }


    /**
     * 当前第几项被选中，更新平移量
     * @param selectPosition 月视图被点击的position
     */
    final void updateSelectPosition(int selectPosition) {
        int line = (selectPosition + 7) / 7;
        mViewPagerTranslateY = (line - 1) * mItemHeight;
    }

    /**
     * 设置选中的周，更新位置
     * @param week week
     */
    final void updateSelectWeek(int week) {
        mViewPagerTranslateY = (week - 1) * mItemHeight;
    }

    /**
     * 更新内容ContentView可平移的最大距离
     */
    void updateContentViewTranslateY() {
        CalendarDay calendar = mDelegate.mIndexCalendar;
        if (mDelegate.getMonthViewShowMode() == CalendarViewDelegate.MODE_ALL_MONTH) {
            mContentViewTranslateY = 5 * mItemHeight;
        } else {
            mContentViewTranslateY = CalendarUtil.getOriginMonthViewHeight(calendar.getYear(),
                    calendar.getMonth(), mItemHeight, mDelegate.getWeekStart())
                    - mItemHeight;
        }
        //已经显示周视图，则需要动态平移contentView的高度
        if (mWeekPager.getVisibility() == VISIBLE) {
            if (mContentView == null)
                return;
            mContentView.setTranslationY(-mContentViewTranslateY);
        }
    }

    /**
     * 更新日历项高度
     */
    final void updateCalendarItemHeight() {
        mItemHeight = mDelegate.getCalendarItemHeight();
        if (mContentView == null)
            return;
        CalendarDay calendar = mDelegate.mIndexCalendar;
        updateSelectWeek(CalendarUtil.getWeekFromDayInMonth(calendar, mDelegate.getWeekStart()));
        if (mDelegate.getMonthViewShowMode() == CalendarViewDelegate.MODE_ALL_MONTH) {
            mContentViewTranslateY = 5 * mItemHeight;
        } else {
            mContentViewTranslateY = CalendarUtil.getOriginMonthViewHeight(calendar.getYear(), calendar.getMonth(),
                    mItemHeight, mDelegate.getWeekStart()) - mItemHeight;
        }
        translationViewPager();
        if (mWeekPager.getVisibility() == VISIBLE) {
            mContentView.setTranslationY(-mContentViewTranslateY);
        }
    }

    private void showYearViewWithMonthAnimation(int year, int month) {
        mYearView.setAlpha(0);
        mYearView.setVisibility(View.VISIBLE);
        int monthViewExpandHeight = mDelegate.getMonthViewExpandHeight();
        int montViewHeight = CalendarUtil.getMonthViewHeight(year, month, mDelegate);
        int marginTop = montViewHeight - monthViewExpandHeight;
        setContentViewMarginTop(marginTop);
        post(() -> {
            mYearView.setPivotX(0);
            mYearView.setPivotY(0);
            int monthViewWidth = mMonthView.getWidth();
            final float yearViewScale = ((float) monthViewWidth) / BaseYearView.monthViewWidth;
            Pair<Float, Float> monthViewCoordinate = getMonthViewCoordinateInYearView(month);

            mYearView.animate()
                    .scaleX(yearViewScale)
                    .scaleY(yearViewScale)
                    .translationX(-monthViewCoordinate.first * yearViewScale)
                    .translationY(-((monthViewCoordinate.second + BaseYearView.monthViewTopLayoutHeight) * yearViewScale))
                    .setDuration(0)
                    .start();

            mYearView.animate()
                    .scaleX(1F)
                    .scaleY(1F)
                    .translationX(0)
                    .translationY(0)
                    .setDuration(SHOW_YEAR_VIEW_ANIMATE_DURATION)
                    .setInterpolator(getSpringInterpolator())
                    .start();

            ObjectAnimator yearViewAlphaAnimator = ObjectAnimator
                    .ofFloat(mYearView, "alpha", 0F, 1F)
                    .setDuration(SHOW_YEAR_VIEW_ALPHA_ANIM_DURATION);
            yearViewAlphaAnimator.setInterpolator(new PathInterpolator(0.25F, 0.1F, 0.25F, 1
            ));
            yearViewAlphaAnimator.start();

            final float monthViewScale = BaseYearView.monthViewWidth / ((float) monthViewWidth);
            mMonthWeekViewParent.setPivotX(0);
            mMonthWeekViewParent.setPivotY(0);
            mMonthWeekViewParent.animate()
                    .translationX(monthViewCoordinate.first)
                    .translationY(monthViewCoordinate.second)
                    .scaleX(monthViewScale)
                    .scaleY(monthViewScale)
                    .setDuration(SHOW_YEAR_VIEW_ANIMATE_DURATION)
                    .setInterpolator(getSpringInterpolator())
                    .start();

            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mMonthWeekViewParent, "alpha", 1F, 0F);
            objectAnimator.setDuration(SHOW_YEAR_VIEW_ALPHA_ANIM_DURATION);
            objectAnimator.setInterpolator(new PathInterpolator(0.25F, 0.1F, 0.25F, 1F));
            objectAnimator.start();
            hideContentView();
        });
    }

    private void hideYearViewWithMonthAnimation() {
        post(() -> {
            CalendarDay calendarDay = mCalendarView.getSelectedCalendar();
            mYearView.setPivotX(0);
            mYearView.setPivotY(0);
            final int year = calendarDay.getYear();
            final int month = calendarDay.getMonth();

            ObjectAnimator yearViewAlphaAnimator = ObjectAnimator
                    .ofFloat(mYearView, "alpha", 1F, 0F)
                    .setDuration(SHOW_YEAR_VIEW_ALPHA_ANIM_DURATION);
            yearViewAlphaAnimator
                    .setInterpolator(getAlphaInterpolator());
            yearViewAlphaAnimator.start();

            int monthViewWidth = mMonthView.getWidth();
            // 设置动画最初的缩放比
            final float yearViewScale = ((float) monthViewWidth) / BaseYearView.monthViewWidth;
            Pair<Float, Float> translation = getMonthViewCoordinateInYearView(month);

            mYearView.animate()
                    .translationX(-translation.first * yearViewScale)
                    .translationY(-((translation.second + BaseYearView.monthViewTopLayoutHeight) * yearViewScale))
                    .scaleX(yearViewScale)
                    .scaleY(yearViewScale)
                    .setDuration(SHOW_YEAR_VIEW_ANIMATE_DURATION)
                    .setInterpolator(getSpringInterpolator())
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mDelegate.isShowYearSelectedLayout = false;
                            mYearView.setVisibility(View.GONE);
                            mYearView.setAlpha(1F);
                            mYearView.setScaleX(1F);
                            mYearView.setScaleY(1F);
                            mYearView.setTranslationY(0);
                            mYearView.setTranslationX(0);
                            mYearView.animate().setListener(null);
                            setContentViewMarginTop(0);
                        }
                    })
                    .start();

            mMonthWeekViewParent.animate()
                    .translationX(translation.first)
                    .translationY(translation.second + BaseYearView.monthViewTopLayoutHeight)
                    .setDuration(0).start();

            mMonthWeekViewParent.animate()
                    .scaleX(1F)
                    .scaleY(1F)
                    .translationX(0)
                    .translationY(0)
                    .setDuration(SHOW_YEAR_VIEW_ANIMATE_DURATION)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            mMonthWeekViewParent.animate().setListener(null);
                            mMonthWeekViewParent.setScaleX(1F);
                            mMonthWeekViewParent.setScaleY(1F);
                        }
                    })
                    .setInterpolator(getSpringInterpolator())
                    .start();

            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mMonthWeekViewParent, "alpha", 0F, 1F);
            objectAnimator.setDuration(SHOW_YEAR_VIEW_ALPHA_ANIM_DURATION);
            objectAnimator.setInterpolator(getAlphaInterpolator());
            objectAnimator.start();

            showContentView(year, month);
        });
    }

    private void scaleMonthView(boolean show) {
        PathInterpolator scalePathInterpolator = getScalePathInterpolator();
        if (show) {
            mMonthWeekViewParent.setScaleX(1);
            mMonthWeekViewParent.setScaleY(1);
            mMonthWeekViewParent.animate()
                    .scaleY(0.9f)
                    .scaleX(0.9f)
                    .setDuration(SHOW_YEAR_VIEW_SCALE_ANIM_DURATION)
                    .setInterpolator(scalePathInterpolator).start();
        } else {
            mMonthWeekViewParent.animate()
                    .scaleX(1)
                    .scaleY(1)
                    .setInterpolator(scalePathInterpolator)
                    .setDuration(SHOW_YEAR_VIEW_SCALE_ANIM_DURATION)
                    .start();
        }
    }

    private void scaleYearView(boolean show) {
        PathInterpolator scalePathInterpolator = getScalePathInterpolator();
        PathInterpolator pathInterpolator = new PathInterpolator(0.25f, 0.1f, 0.25f, 1f);
        final float startScale = 1.1f;
        final float endScale = 1f;
        if (show) {
            mYearView.setScaleX(startScale);
            mYearView.setScaleY(startScale);
            mYearView.animate()
                    .scaleX(endScale)
                    .scaleY(endScale)
                    .setInterpolator(scalePathInterpolator)
                    .setDuration(SHOW_YEAR_VIEW_SCALE_ANIM_DURATION).start();

            ObjectAnimator objectAnimatorAlpha = ObjectAnimator.ofFloat(mYearView, "alpha", 0f, 1f);
            objectAnimatorAlpha.setDuration(SHOW_YEAR_VIEW_ALPHA_ANIM_DURATION);
            objectAnimatorAlpha.setInterpolator(pathInterpolator);
            objectAnimatorAlpha.start();
        } else {
            mYearView.animate()
                    .scaleX(startScale)
                    .scaleY(startScale)
                    .setInterpolator(scalePathInterpolator)
                    .setDuration(SHOW_YEAR_VIEW_SCALE_ANIM_DURATION)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            mYearView.setAlpha(1);
                            mYearView.setScaleX(1);
                            mYearView.setScaleY(1);
                            setYearViewVisibilityGone();
                            mYearView.animate().setListener(null);
                        }
                    }).start();

            mYearView.animate()
                    .alpha(0f)
                    .setInterpolator(pathInterpolator)
                    .setDuration(SHOW_YEAR_VIEW_ALPHA_ANIM_DURATION)
                    .start();
        }
    }

    private void setYearViewVisibilityGone() {
        mYearView.setVisibility(View.GONE);
        mDelegate.isShowYearSelectedLayout = false;
    }

    /**
     * 显示内容布局
     */
    private void showContentView(int year, int month) {
        if (mContentView == null)
            return;
        mContentView.setVisibility(VISIBLE);
        int montViewHeight = getWeekBarHeight() + CalendarUtil.getMonthViewHeight(year, month, mDelegate);
        int marginTop = montViewHeight - mYearView.getHeight();
        setContentViewMarginTop(marginTop);
        mContentView.animate()
                .translationY(0)
                .alpha(1F)
                .setDuration(SHOW_YEAR_VIEW_SCALE_ANIM_DURATION)
                .setInterpolator(getSpringInterpolator()).start();

        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mContentView, "alpha", 0F, 1F);
        objectAnimator.setInterpolator(getAlphaInterpolator());
        objectAnimator.setDuration(SHOW_YEAR_VIEW_SCALE_ANIM_DURATION);
        objectAnimator.start();
    }

    /**
     * 隐藏内容布局
     */
    private void hideContentView() {
        if (mContentView == null)
            return;
        float translateY = mContentView.getContext()
                .getResources()
                .getDimension(R.dimen.card_translate_y);
        setContentViewClickable(true);
        mContentView.animate()
                .translationY(translateY)
                .setDuration(SHOW_YEAR_VIEW_SCALE_ANIM_DURATION)
                .setInterpolator(getSpringInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mContentView.setVisibility(GONE);
                        mContentView.animate().setListener(null);
                        setContentViewClickable(false);
                    }
                });
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mContentView, "alpha", 1F, 0F);
        objectAnimator.setInterpolator(getAlphaInterpolator());
        objectAnimator.setDuration(SHOW_YEAR_VIEW_ALPHA_ANIM_DURATION);
        objectAnimator.start();
    }

    private void setContentViewClickable(boolean clickable) {
        if (mContentView instanceof IContentView) {
            IContentView contentView = (IContentView) mContentView;
            contentView.disabledTouchEvent(clickable);
        }
    }

    /**
     * 隐藏周视图
     */
    private void hideWeek(boolean isNotify) {
        if (isNotify) {
            onShowMonthView();
        }
        mWeekPager.setVisibility(GONE);
        mMonthView.setVisibility(VISIBLE);
    }

    /**
     * 显示周视图
     */
    private void showWeek() {
        onShowWeekView();
        if (mWeekPager != null && mWeekPager.getAdapter() != null) {
            mWeekPager.getAdapter().notifyDataSetChanged();
            mWeekPager.setVisibility(VISIBLE);
        }
        mMonthView.setVisibility(INVISIBLE);
    }

    /**
     * 周视图显示事件
     */
    private void onShowWeekView() {
        if (mWeekPager.getVisibility() == VISIBLE) {
            return;
        }
        if (mDelegate != null && mDelegate.mViewChangeListener != null && !isWeekView) {
            mDelegate.mViewChangeListener.onViewChange(false);
        }
    }

    /**
     * 周视图显示事件
     */
    private void onShowMonthView() {
        if (mMonthView.getVisibility() == VISIBLE) {
            return;
        }
        if (mDelegate != null && mDelegate.mViewChangeListener != null && isWeekView) {
            mDelegate.mViewChangeListener.onViewChange(true);
        }
    }

    /**
     * 平移ViewPager月视图
     */
    private void translationViewPager() {
        float percent = mContentView.getTranslationY() / mContentViewTranslateY;
        mMonthView.setTranslationY(mViewPagerTranslateY * percent);
    }

    /**
     * ContentView是否滚动到顶部 如果完全不适合，就复写这个方法
     * @return 是否滚动到顶部
     */
    private boolean isScrollTop() {
        if (mContentView instanceof CalendarScrollView) {
            return ((CalendarScrollView) mContentView).isScrollToTop();
        }
        if (mContentView instanceof RecyclerView)
            return ((RecyclerView) mContentView).computeVerticalScrollOffset() == 0;
        if (mContentView instanceof AbsListView) {
            boolean result = false;
            AbsListView listView = (AbsListView) mContentView;
            if (listView.getFirstVisiblePosition() == 0) {
                final View topChildView = listView.getChildAt(0);
                result = topChildView.getTop() == 0;
            }
            return result;
        }
        return mContentView.getScrollY() == 0;
    }

    private int getWeekBarHeight() {
        return mDelegate.getWeekBarHeight() + CalendarUtil.dipToPx(getContext(), 1);
    }

    private CalendarView findCalendarView() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            if (childView instanceof CalendarView) {
                return (CalendarView) childView;
            }
        }
        return null;
    }

    private int getH(int height, int monthHeight, int weekBarHeight) {
        int h;
        if (mCalendarShowMode == CALENDAR_SHOW_MODE_ONLY_MONTH_VIEW ||
                mCalendarView.getVisibility() == GONE) {
            h = height - (mCalendarView.getVisibility() == GONE ? 0 : mCalendarView.getHeight());
        } else if (mGestureMode == GESTURE_MODE_DISABLED && !isMonthViewWeekViewSwitchAnimating) {
            if (isMonthViewStatus()) {
                h = height - monthHeight;
            } else {
                h = height - weekBarHeight - mItemHeight;
            }
        } else {
            h = height - weekBarHeight - mItemHeight;
        }
        return h;
    }

    private int getPointerIndex(MotionEvent ev, int id) {
        int activePointerIndex = ev.findPointerIndex(id);
        if (activePointerIndex == -1) {
            mActivePointerId = INVALID_POINTER;
        }
        return activePointerIndex;
    }

    private void setContentViewMarginTop(int marginTop) {
        ViewGroup.LayoutParams layoutParams = mContentView.getLayoutParams();
        if (layoutParams instanceof MarginLayoutParams) {
            MarginLayoutParams marginLayoutParams = (MarginLayoutParams) layoutParams;
            marginLayoutParams.topMargin = marginTop;
            mContentView.setLayoutParams(marginLayoutParams);
        }
    }

    private PathInterpolator getSpringInterpolator() {
        if (mSpringInterpolator == null) {
            mSpringInterpolator = new PathInterpolator(0.13F, 1.02F, 0.29F, 1);
        }
        return mSpringInterpolator;
    }

    private PathInterpolator getAlphaInterpolator() {
        if (mAlphaInterpolator == null) {
            mAlphaInterpolator = new PathInterpolator(0.65F, 0F, 0.35F, 1F);
        }
        return mAlphaInterpolator;
    }

    private PathInterpolator getScalePathInterpolator() {
        if (mScalePathInterpolator == null) {
            mScalePathInterpolator = new PathInterpolator(0.26f, 1.1f, 0.24f, 1f);
        }
        return mScalePathInterpolator;
    }

    private Pair<Float, Float> getMonthViewCoordinateInYearView(int month) {
        if (monthViewCoordinateMap.isEmpty()) {
            initCoordinateMap();
        }
        float yearMonthViewWidth = mYearView.getWidth() / 3f;
        float yearMonthViewHeight = mYearView.getHeight() / 4f;
        Pair<Integer, Integer> monthCoordinate = monthViewCoordinateMap.get(month);
        float coordinateX = 0;
        float coordinateY = 0;
        if (monthCoordinate != null) {
            coordinateX = monthCoordinate.first * yearMonthViewWidth;
            coordinateY = monthCoordinate.second * yearMonthViewHeight;
        }
        return new Pair<>(coordinateX, coordinateY);
    }

    private void initCoordinateMap() {
        monthViewCoordinateMap.put(1, new Pair<>(0, 0));
        monthViewCoordinateMap.put(2, new Pair<>(1, 0));
        monthViewCoordinateMap.put(3, new Pair<>(2, 0));
        monthViewCoordinateMap.put(4, new Pair<>(0, 1));
        monthViewCoordinateMap.put(5, new Pair<>(1, 1));
        monthViewCoordinateMap.put(6, new Pair<>(2, 1));
        monthViewCoordinateMap.put(7, new Pair<>(0, 2));
        monthViewCoordinateMap.put(8, new Pair<>(1, 2));
        monthViewCoordinateMap.put(9, new Pair<>(2, 2));
        monthViewCoordinateMap.put(10, new Pair<>(0, 3));
        monthViewCoordinateMap.put(11, new Pair<>(1, 3));
        monthViewCoordinateMap.put(12, new Pair<>(2, 3));
    }

    /**
     * 设置日历视图展开后的最大高度
     * @param calendarLayoutExpandHeight 日历视图展开后的最大高度
     */
    private void updateCalendarViewExpandHeight(int calendarLayoutExpandHeight) {
        mCalendarLayoutHeight = calendarLayoutExpandHeight;
        mCalendarView.updateCalendarViewMaxHeight(mCalendarLayoutHeight);
    }

    /**
     * 初始化当前时间的位置
     * @param cur 当前日期时间
     */
    private void initCalendarPosition(CalendarDay cur) {
        int diff = CalendarUtil.getMonthViewStartDiff(cur, mDelegate.getWeekStart());
        int size = diff + cur.getDay() - 1;
        updateSelectPosition(size);
    }

    /**
     * 根据滑动速度计算月视图展开时间
     * @param velocity 手指滑动速度
     * @return 月视图展开时间
     */
    private long calculateDuration(float velocity) {
        return (long) (((EXPAND_MONTH_VIEW_ANIM_MIN_DURATION - EXPAND_MONTH_VIEW_ANIM_DURATION) / EXPAND_MONTH_VIEW_MAX_VELOCITY) * velocity + EXPAND_MONTH_VIEW_ANIM_DURATION);
    }

    private void calendarStatusChanged(@CalendarStatus int statusWeekView) {
        mCalendarStatus = statusWeekView;
        if (calendarStatusChangeListener != null) {
            calendarStatusChangeListener.onCalendarStatusChanged(mCalendarStatus);
        }
    }

    public void setCalendarStatusChangeListener(CalendarStatusChangeListener calendarStatusChangeListener) {
        this.calendarStatusChangeListener = calendarStatusChangeListener;
    }

    public @CalendarStatus int getCalendarStatus() {
        return mCalendarStatus;
    }

    /**
     * 如果有十分特别的ContentView，可以自定义实现这个接口
     */
    public interface CalendarScrollView {
        /**
         * 是否滚动到顶部
         * @return 是否滚动到顶部
         */
        boolean isScrollToTop();
    }

    public interface CalendarStatusChangeListener {
        void onCalendarStatusChanged(@CalendarStatus int status);
    }

    public interface AnimationEndListener {
        void onAnimationEnd(boolean success);
    }
}
