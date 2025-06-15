package com.zhangpan.site.calendarview;

import android.content.Context;
import android.graphics.Canvas;

import androidx.annotation.NonNull;

public class CalendarMonthView extends MonthView {

    public CalendarMonthView(Context context) {
        super(context);
    }

    @Override
    protected void onPreviewDraw(@NonNull Canvas canvas) {
        super.onPreviewDraw(canvas);
        getCalendarDrawer().onPreviewDraw(canvas, mLineCount);
    }

    /**
     * 绘制选中的日子
     * @param canvas canvas
     * @param calendarDay 日历日历calendar
     * @param x 日历Card x起点坐标
     * @param y 日历Card y起点坐标
     * @param hasScheme hasScheme 非标记的日期
     * @return true 则绘制onDrawScheme，因为这里背景色不是是互斥的
     */
    @Override
    protected boolean onDrawSelected(Canvas canvas, CalendarDay calendarDay, int x, int y, boolean hasScheme) {
        return getCalendarDrawer().onDrawSelected(canvas, calendarDay, x, y, hasScheme);
    }

    /**
     * 绘制标记的事件日子
     * @param canvas canvas
     * @param isSelected 是否选中
     * @param calendar 日历calendar
     * @param x 日历Card x起点坐标
     * @param y 日历Card y起点坐标
     */
    @Override
    protected void onDrawScheme(Canvas canvas, boolean isSelected, CalendarDay calendar, int x, int y) {
        getCalendarDrawer().onDrawScheme(canvas, isSelected, calendar, x, y);
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
    @Override
    protected void onDrawText(Canvas canvas, CalendarDay calendar, int x, int y, boolean hasScheme, boolean isSelected) {
        getCalendarDrawer().onDrawText(canvas, calendar, x, y, hasScheme, isSelected);
    }

    /**
     * 绘制周行前调用此方法
     * @param canvas canvas
     * @param weekStartDay 一周的第一天
     * @param lineNum 周行数 0-6
     */
    @Override
    protected void onBeforeDrawWeek(Canvas canvas, CalendarDay weekStartDay, int lineNum, int itemHeight) {
        super.onBeforeDrawWeek(canvas, weekStartDay, lineNum, itemHeight);
        getCalendarDrawer().onStartDrawWeek(canvas, weekStartDay, lineNum, itemHeight);
    }
}
