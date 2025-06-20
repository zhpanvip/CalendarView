package com.zhangpan.site.calendarview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint

/**
 * @Description:
 * @Author: zhangpan
 * @Date: 2024/9/6 15:00
 */
abstract class BaseCalendarDrawer(context: Context,var delegate: CalendarViewDelegate) {
    protected var mContext: Context = context.applicationContext

    protected var mSelectedPaint: Paint = Paint()
    protected var mTextBaseLine: Float = 0f

    protected var mItemWidth: Int = 0
    protected var mItemHeight: Int = 0
    protected var mLineWidth: Int = 0

    init {
        mSelectedPaint.style = Paint.Style.FILL
        mSelectedPaint.isAntiAlias = true
        mItemHeight = delegate.calendarItemHeight
    }


    open fun setItemWidth(itemWidth: Int) {
        mItemWidth = itemWidth
        mLineWidth = itemWidth * 7 + delegate.showWeekPaddingLeft
    }

    open fun onPreviewDraw(canvas: Canvas?, lineCount: Int) {
        // 更新 ItemView 高度
        mItemHeight = getItemHeight(lineCount)
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
    abstract fun onDrawText(
        canvas: Canvas,
        calendar: CalendarDay,
        x: Int,
        y: Int,
        hasScheme: Boolean,
        isSelected: Boolean
    )

    /**
     * 绘制选中的日子
     * @param canvas canvas
     * @param calendarDay 日历日历calendar
     * @param x 日历Card x起点坐标
     * @param y 日历Card y起点坐标
     * @param hasScheme hasScheme 非标记的日期
     * @return true 则绘制onDrawScheme，因为这里背景色不是是互斥的
     */
    @Suppress("unused")
    abstract fun onDrawSelected(
        canvas: Canvas,
        calendarDay: CalendarDay,
        x: Int,
        y: Int,
        hasScheme: Boolean
    ): Boolean

    /**
     * 绘制标记的事件日子
     * @param canvas canvas
     * @param isSelected 是否选中
     * @param calendar 日历calendar
     * @param x 日历Card x起点坐标
     * @param y 日历Card y起点坐标
     */
    abstract fun onDrawScheme(
        canvas: Canvas,
        isSelected: Boolean,
        calendar: CalendarDay,
        x: Int,
        y: Int
    )


    /**
     * 绘制周行前调用此方法
     * @param canvas canvas
     * @param weekStartDay 一周的第一天
     * @param lineNum 周行数 0-6
     */
    open fun onStartDrawWeek(
        canvas: Canvas,
        weekStartDay: CalendarDay,
        lineNum: Int,
        itemHeight: Int
    ) {
    }

    /**
     * dp转px
     * @param context context
     * @param dpValue dp
     * @return px
     */
    protected fun dpToPx(context: Context, dpValue: Float): Float {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale)
    }

    /**
     * 是否在日期范围内
     * @param calendar calendar
     * @return 是否在日期范围内
     */
    protected fun isInRange(calendar: CalendarDay): Boolean {
        return CalendarUtil.isCalendarInRange(calendar, delegate)
    }

    private fun getItemHeight(lineCount: Int): Int {
        val calendarItemHeight: Int = delegate.calendarItemHeight
        val calendarViewInitialHeight: Int = lineCount * calendarItemHeight
        val calendarViewHeightOffset: Float =
            delegate.getMonthViewHeightOffset(calendarViewInitialHeight).toFloat()
        var itemOffset = 0f
        if (lineCount != 0) {
            itemOffset = calendarViewHeightOffset / lineCount
        }
        return calendarItemHeight + itemOffset.toInt()
    }
}
