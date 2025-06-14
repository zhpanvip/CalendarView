package com.zhangpan.site.calendarview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import androidx.core.content.ContextCompat

/**
 * @Description:
 * @Author: zhangpan
 * @Date: 2024/9/4 13:46
 */
class CalendarDrawer(context: Context) : BaseCalendarDrawer(context) {
    private var mPadding = 0f
    private val rectF = RectF()
    private var rectRadius = 0

    private var mTodayNumberColor: Int = 0
    private var mEventCircleColor: Int = 0

    override fun setDrawData(delegate: CalendarViewDelegate, isMonthView: Boolean, itemWidth: Int) {
        mDelegate = delegate
        mItemHeight = delegate.calendarItemHeight
        mItemWidth = itemWidth
        EVENT_CIRCLE_RADIUS = dpToPx(mContext, 8f)
        mPadding = dpToPx(mContext, 2f)
        rectRadius = mContext.resources.getDimensionPixelSize(R.dimen.radius_corner_smooth_mini)
        mEventCircleColor = ContextCompat.getColor(mContext, R.color.event_circle_color)
        mTodayNumberColor = ContextCompat.getColor(mContext, R.color.color_primary_blue)
        val mEventCirclePaint = Paint()
        mEventCirclePaint.isAntiAlias = true
        val paint = mDelegate.switchTextPaint(BaseView.PAINT_CURRENT_MONTH_DATE)
        val metrics = paint.fontMetrics
        mTextBaseLine = mItemHeight / 2f - metrics.descent + (metrics.bottom - metrics.top) / 2
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
    @Suppress("unused")
    override fun onDrawSelected(
        canvas: Canvas,
        calendarDay: CalendarDay,
        x: Int,
        y: Int,
        hasScheme: Boolean
    ): Boolean {
        mSelectedPaint.style = Paint.Style.FILL
        rectF.left = x + mPadding
        rectF.top = y.toFloat()
        rectF.right = x + mItemWidth - mPadding
        rectF.bottom = (y + mItemHeight).toFloat()
        if (calendarDay.isCurrentDay) {
            mSelectedPaint.color = mDelegate.selectedThemeColor
        } else {
            mSelectedPaint.color = ContextCompat.getColor(mContext, R.color.color_primary_blue)
        }
        canvas.drawRoundRect(rectF, rectRadius.toFloat(), rectRadius.toFloat(), mSelectedPaint)
        return true
    }

    /**
     * 绘制标记的事件日子
     * @param canvas canvas
     * @param isSelected 是否选中
     * @param calendar 日历calendar
     * @param x 日历Card x起点坐标
     * @param y 日历Card y起点坐标
     */
    override fun onDrawScheme(
        canvas: Canvas,
        isSelected: Boolean,
        calendar: CalendarDay,
        x: Int,
        y: Int
    ) {
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
    override fun onDrawText(
        canvas: Canvas,
        calendar: CalendarDay,
        x: Int,
        y: Int,
        hasScheme: Boolean,
        isSelected: Boolean
    ) {
        val cx = x + mItemWidth / 2
        val top = y - mItemHeight / 10

        val isInRange = isInRange(calendar)

        if (isSelected) {
            val paint = mDelegate.switchTextPaint(BaseView.PAINT_CURRENT_SELECT_DATE)
            canvas.drawText(calendar.day.toString(), cx.toFloat(), mTextBaseLine + top, paint)
            mDelegate.switchTextPaint(BaseView.PAINT_SELECT_LUNAR_DATE)
            canvas.drawText(
                calendar.lunar,
                cx.toFloat(),
                mTextBaseLine + y + mItemHeight / 10f,
                paint
            )
        } else {
            val paint = if (calendar.isCurrentDay) {
                mDelegate.switchTextPaint(BaseView.PAINT_CURRENT_DAY)
            } else if (calendar.isCurrentMonth && isInRange) {
                mDelegate.switchTextPaint(BaseView.PAINT_CURRENT_MONTH_DATE)
            } else {
                mDelegate.switchTextPaint(BaseView.PAINT_OTHER_MONTH_DATE)
            }
            canvas.drawText(calendar.day.toString(), cx.toFloat(), mTextBaseLine + top, paint)
            val lunarPaint = if (calendar.isCurrentDay && isInRange) {
                mDelegate.switchTextPaint(BaseView.PAINT_CURRENT_DAY_LUNAR_DATE)
            } else if (calendar.isCurrentMonth) {
                mDelegate.switchTextPaint(BaseView.PAINT_CURRENT_MONTH_LUNAR_DATE)
            } else {
                mDelegate.switchTextPaint(BaseView.PAINT_OTHER_MONTH_LUNAR_DATE)
            }
            canvas.drawText(
                calendar.lunar,
                cx.toFloat(),
                mTextBaseLine + y + mItemHeight / 10f,
                lunarPaint
            )
        }
    }

    companion object {
        private var EVENT_CIRCLE_RADIUS: Float = 0f
    }
}
