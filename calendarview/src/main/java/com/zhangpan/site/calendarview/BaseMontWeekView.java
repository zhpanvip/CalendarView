package com.zhangpan.site.calendarview;

import android.content.Context;
import android.graphics.Canvas;

/**
 * @Description: 周视图、月视图共用逻辑
 * @Author: zhangpan
 * @Date: 2024/9/4 10:34
 */
public abstract class BaseMontWeekView extends BaseView {

    public BaseMontWeekView(Context context) {
        super(context);
    }


    protected BaseCalendarDrawer getCalendarDrawer() {
        BaseCalendarDrawer calendarDrawer = mDelegate.getCalendarDrawer();
        calendarDrawer.setItemWidth(mItemWidth);
        return calendarDrawer;
    }

    /**
     * 绘制选中的日期
     *
     * @param canvas    canvas
     * @param calendar  日历日历calendar
     * @param x         日历Card x起点坐标
     * @param hasScheme hasScheme 非标记的日期
     * @return 是否绘制 onDrawScheme
     */
    protected abstract boolean onDrawSelected(Canvas canvas, CalendarDay calendar, int x, int y, boolean hasScheme);

    /**
     * 绘制标记的日期
     *
     * @param canvas   canvas
     * @param calendar 日历calendar
     * @param x        日历Card x起点坐标
     */
    protected abstract void onDrawScheme(Canvas canvas, boolean isSelected, CalendarDay calendar, int x, int y);


    /**
     * 绘制日历文本
     *
     * @param canvas     canvas
     * @param calendar   日历calendar
     * @param x          日历Card x起点坐标
     * @param hasScheme  是否是标记的日期
     * @param isSelected 是否选中
     */
    protected abstract void onDrawText(Canvas canvas, CalendarDay calendar, int x, int y, boolean hasScheme, boolean isSelected);
}
