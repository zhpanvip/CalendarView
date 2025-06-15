package com.zhangpan.site.calendarview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;


/**
 * @Description:
 * @Author: zhangpan
 * @Date: 2024/9/6 15:00
 */
public abstract class BaseCalendarDrawer {

    protected Context mContext;

    protected Paint mSelectedPaint = new Paint();
    protected float mTextBaseLine;

    protected CalendarViewDelegate mDelegate;

    protected int mItemWidth;
    protected int mItemHeight;
    protected int mLineWidth;

    protected boolean isMonthView;

    public BaseCalendarDrawer(Context context) {
        mContext = context.getApplicationContext();

        mSelectedPaint.setStyle(Paint.Style.FILL);
        mSelectedPaint.setAntiAlias(true);
    }


    public void setDrawData(
            CalendarViewDelegate delegate,
            boolean isMonthView,
            int itemWidth) {
        mDelegate = delegate;
        mItemHeight = delegate.getCalendarItemHeight();
        mItemWidth = itemWidth;
        mLineWidth = itemWidth * 7 + mDelegate.getShowWeekPaddingLeft();
        this.isMonthView = isMonthView;
    }

    public void onPreviewDraw(Canvas canvas, int lineCount) {

    }

    public void updateItemHeight(int itemHeight) {
        mItemHeight = itemHeight;
    }

    /**
     * 绘制文本
     * @param canvas canvas
     * @param calendar 日历calendar
     * @param x 日历Card x起点坐标
     * @param y 日历Card y起点坐标
     * @param hasScheme 是否是标记的日期
     * @param isSelected 是否选中
     */
    public abstract void onDrawText(Canvas canvas, CalendarDay calendar, int x, int y, boolean hasScheme, boolean isSelected);

    /**
     * 绘制选中的日子
     * @param canvas canvas
     * @param calendarDay 日历日历calendar
     * @param x 日历Card x起点坐标
     * @param y 日历Card y起点坐标
     * @param hasScheme hasScheme 非标记的日期
     * @return true 则绘制onDrawScheme，因为这里背景色不是是互斥的
     */
    @SuppressWarnings("unused")
    public abstract boolean onDrawSelected(Canvas canvas, CalendarDay calendarDay, int x, int y, boolean hasScheme);

    /**
     * 绘制标记的事件日子
     * @param canvas canvas
     * @param isSelected 是否选中
     * @param calendar 日历calendar
     * @param x 日历Card x起点坐标
     * @param y 日历Card y起点坐标
     */
    public abstract void onDrawScheme(Canvas canvas, boolean isSelected, CalendarDay calendar, int x, int y);


    /**
     * 绘制周行前调用此方法
     * @param canvas canvas
     * @param weekStartDay 一周的第一天
     * @param lineNum 周行数 0-6
     */
    public void onStartDrawWeek(Canvas canvas, CalendarDay weekStartDay, int lineNum, int itemHeight) {

    }

    /**
     * dp转px
     * @param context context
     * @param dpValue dp
     * @return px
     */
    protected float dpToPx(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (dpValue * scale);
    }

    /**
     * 是否在日期范围内
     * @param calendar calendar
     * @return 是否在日期范围内
     */
    protected final boolean isInRange(CalendarDay calendar) {
        return mDelegate != null && CalendarUtil.isCalendarInRange(calendar, mDelegate);
    }
}
