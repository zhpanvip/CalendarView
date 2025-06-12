package com.zhangpan.site.calendarview;

import android.content.Context;
import android.graphics.Canvas;

public class CalendarWeekView extends WeekView {

    public CalendarWeekView(Context context) {
        super(context);
    }

    /**
     * @param canvas canvas
     * @param calendarDay 日历日历calendar
     * @param x 日历Card x起点坐标
     * @param hasScheme hasScheme 非标记的日期
     * @return true 则绘制onDrawScheme，因为这里背景色不是是互斥的
     */
    @Override
    protected boolean onDrawSelected(Canvas canvas, CalendarDay calendarDay, int x, int y, boolean hasScheme) {
        getCalendarDrawer().onDrawSelected(canvas, calendarDay, x, y, hasScheme);
        return true;
    }

    private BaseCalendarDrawer getCalendarDrawer() {
        return getCalendarDrawer(false);
    }

    @Override
    protected void onDrawScheme(Canvas canvas, boolean isSelected, CalendarDay calendar, int x, int y) {
        getCalendarDrawer().onDrawScheme(canvas, isSelected, calendar, x, y);
    }

    @Override
    protected void onDrawText(Canvas canvas, CalendarDay calendar, int x, int y, boolean hasScheme, boolean isSelected) {
        getCalendarDrawer().onDrawText(canvas, calendar, x, y, hasScheme, isSelected);
    }

    @Override
    protected void onBeforeDrawWeek(Canvas canvas, CalendarDay weekStartDay, int lineNum, int itemHeight) {
        super.onBeforeDrawWeek(canvas, weekStartDay, lineNum, itemHeight);
        getCalendarDrawer().onStartDrawWeek(canvas, weekStartDay, lineNum, itemHeight);
    }
}
