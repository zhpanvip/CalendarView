
package com.zhangpan.site.calendarview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;

import androidx.annotation.NonNull;

/**
 * 月视图基础控件,可自由继承实现
 * 可通过此扩展各种视图如：MonthView、RangeMonthView、MultiMonthView
 */
public abstract class BaseMonthView extends BaseMontWeekView {

    @SuppressWarnings("unused")
    protected static final String TAG = "BaseMonthView";

    MonthViewPager mMonthViewPager;

    /**
     * 当前日历卡年份
     */
    protected int mYear;

    /**
     * 当前日历卡月份
     */
    protected int mMonth;


    /**
     * 日历的行数
     */
    protected int mLineCount;

    /**
     * 日历高度
     */
    protected int mHeight;


    /**
     * 下个月偏移的数量
     */
    protected int mNextDiff;


    public BaseMonthView(Context context) {
        super(context);
    }

    /**
     * 初始化日期
     *
     * @param year  year
     * @param month month
     */
    final void initMonthWithDate(int year, int month) {
        mYear = year;
        mMonth = month;
        initCalendar();
        mHeight = CalendarUtil.getMonthViewHeight(year, month, mDelegate);
    }

    /**
     * 初始化日历
     */
    @SuppressLint("WrongConstant")
    private void initCalendar() {

        mNextDiff = CalendarUtil.getMonthEndDiff(mYear, mMonth, mDelegate.getWeekStart());
        int preDiff = CalendarUtil.getMonthViewStartDiff(mYear, mMonth, mDelegate.getWeekStart());
        int monthDayCount = CalendarUtil.getMonthDaysCount(mYear, mMonth);

        mItems = CalendarUtil.initCalendarForMonthView(mYear, mMonth, mDelegate.getCurrentDay(), mDelegate.getWeekStart());

        if (mItems.contains(mDelegate.getCurrentDay())) {
            mCurrentItem = mItems.indexOf(mDelegate.getCurrentDay());
        } else {
            mCurrentItem = mItems.indexOf(mDelegate.mSelectedCalendar);
        }

        if (mCurrentItem > 0 &&
                mDelegate.mCalendarInterceptListener != null &&
                mDelegate.mCalendarInterceptListener.onCalendarIntercept(mDelegate.mSelectedCalendar)) {
            mCurrentItem = -1;
        }

        if (mDelegate.getMonthViewShowMode() == CalendarViewDelegate.MODE_ALL_MONTH) {
            mLineCount = 6;
        } else {
            mLineCount = (preDiff + monthDayCount + mNextDiff) / 7;
        }
        addSchemesFromMap();
        invalidate();
    }

    /**
     * 获取点击选中的日期
     *
     * @return return
     */
    protected CalendarDay getIndex() {
        if (mItemWidth == 0 || getItemHeight() == 0) {
            return null;
        }
        if (mX <= mDelegate.getCalendarPaddingLeft() || mX >= getWidth() - mDelegate.getCalendarPaddingRight()) {
            onClickCalendarPadding();
            return null;
        }
        int indexX = (int) (mX - mDelegate.getCalendarPaddingLeft()) / mItemWidth;
        if (indexX >= 7) {
            indexX = 6;
        }
        int indexY = (int) mY / getItemHeight();
        int position = indexY * 7 + indexX;// 选择项
        if (position >= 0 && position < mItems.size()) {
            return mItems.get(position);
        }
        return null;
    }

    private void onClickCalendarPadding() {
        if (mDelegate.mClickCalendarPaddingListener == null) {
            return;
        }
        CalendarDay calendar = null;
        int indexX = (int) (mX - mDelegate.getCalendarPaddingLeft()) / mItemWidth;
        if (indexX >= 7) {
            indexX = 6;
        }
        int indexY = (int) mY / getItemHeight();
        int position = indexY * 7 + indexX;// 选择项
        if (position >= 0 && position < mItems.size()) {
            calendar = mItems.get(position);
        }
        if (calendar == null) {
            return;
        }
        mDelegate.mClickCalendarPaddingListener.onClickCalendarPadding(mX, mY, true, calendar,
                getClickCalendarPaddingObject(mX, mY, calendar));
    }

    /**
     * 获取点击事件处的对象
     *
     * @param x                x
     * @param y                y
     * @param adjacentCalendar adjacent calendar
     * @return obj can as null
     */
    @SuppressWarnings("unused")
    protected Object getClickCalendarPaddingObject(float x, float y, CalendarDay adjacentCalendar) {
        return null;
    }

    /**
     * 记录已经选择的日期
     *
     * @param calendar calendar
     */
    final void setSelectedCalendar(CalendarDay calendar) {
        mCurrentItem = mItems.indexOf(calendar);
    }


    /**
     * 更新显示模式
     */
    final void updateShowMode() {
        mLineCount = CalendarUtil.getMonthViewLineCount(mYear, mMonth,
                mDelegate.getWeekStart(), mDelegate.getMonthViewShowMode());
        mHeight = CalendarUtil.getMonthViewHeight(mYear, mMonth, mDelegate);
        invalidate();
    }

    /**
     * 更新周起始
     */
    final void updateWeekStart() {
        initCalendar();
        mHeight = CalendarUtil.getMonthViewHeight(mYear, mMonth, mDelegate);
    }

    @Override
    void updateItemHeight() {
        super.updateItemHeight();
        mHeight = CalendarUtil.getMonthViewHeight(mYear, mMonth, mDelegate);
//        mDelegate.getCalendarDrawer().updateItemHeight(getItemHeight());
    }

    @Override
    protected int getItemHeight() {
        int calendarItemHeight = mDelegate.getCalendarItemHeight();
        int calendarViewInitialHeight = mLineCount * calendarItemHeight;
        float calendarViewHeightOffset = mDelegate.getMonthViewHeightOffset(calendarViewInitialHeight);
        float itemOffset = 0;
        if (mLineCount != 0) {
            itemOffset = calendarViewHeightOffset / mLineCount;
        }
        return super.getItemHeight() + (int) itemOffset;
    }

    @Override
    void updateCurrentDate() {
        if (mItems == null)
            return;
        if (mItems.contains(mDelegate.getCurrentDay())) {
            for (CalendarDay a : mItems) {//添加操作
                a.setCurrentDay(false);
            }
            int index = mItems.indexOf(mDelegate.getCurrentDay());
            mItems.get(index).setCurrentDay(true);
        }
        invalidate();
    }


    /**
     * 获取选中的下标
     *
     * @param calendar calendar
     * @return 获取选中的下标
     */
    protected final int getSelectedIndex(CalendarDay calendar) {
        return mItems.indexOf(calendar);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mLineCount != 0) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(mHeight, MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 开始绘制前的钩子，这里做一些初始化的操作，每次绘制只调用一次，性能高效
     * 没有需要可忽略不实现
     * 例如：
     * 1、需要绘制圆形标记事件背景，可以在这里计算半径
     * 2、绘制矩形选中效果，也可以在这里计算矩形宽和高
     */
    protected void onPreviewDraw(@NonNull Canvas canvas) {
        super.onPreviewDraw(canvas);
    }


    /**
     * 循环绘制开始的回调，不需要可忽略
     * 绘制每个日历项的循环，用来计算baseLine、圆心坐标等都可以在这里实现
     *
     * @param x 日历Card x起点坐标
     * @param y 日历Card y起点坐标
     */
    @SuppressWarnings("unused")
    protected void onLoopStart(int x, int y) {

    }
}
