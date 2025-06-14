package com.zhangpan.site.demo.calendarview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.util.Log
import com.zhangpan.site.calendarview.CalendarDay
import com.zhangpan.site.calendarview.CalendarUtil
import com.zhangpan.site.calendarview.YearView
import java.util.Calendar

class CustomYearView(context: Context) : YearView(context) {
    private var currentYear = 0
    private var currentMonth = 0
    private var rectWidth = CalendarUtil.dipToPx(context, 6f)
    private var rectHeight = CalendarUtil.dipToPx(context, 1f)
    private var rect = Rect()
    private var monthArray: Array<String>

    init {
        val instance = Calendar.getInstance()
        currentYear = instance.get(Calendar.YEAR)
        currentMonth = instance.get(Calendar.MONTH) + 1
        monthArray = getContext().resources.getStringArray(com.zhangpan.site.calendarview.R.array.lunar_first_of_month)
    }

    private val mTextPadding = CalendarUtil.dipToPx(context, 3f)

    override fun onDrawMonth(canvas: Canvas, year: Int, month: Int, x: Int, y: Int, width: Int, height: Int) {
        val text = context
            .resources
            .getStringArray(com.zhangpan.site.calendarview.R.array.month_string_array)[month - 1]

        if (currentMonth == month && currentYear == year) {
            mMonthTextPaint.color = mDelegate.yearViewCurDayTextColor
        } else {
            mMonthTextPaint.color = mDelegate.yearViewMonthTextColor
        }
        canvas.drawText(text,
            x + mItemWidth / 2f - mTextPadding,
            y + mMonthTextBaseLine,
            mMonthTextPaint)
    }

    override fun onDrawWeek(canvas: Canvas, week: Int, x: Int, y: Int, width: Int, height: Int) {
        val text = context.resources.getStringArray(com.zhangpan.site.calendarview.R.array.year_view_week_string_array)[week]
        canvas.drawText(text,
            x + width / 2f,
            y + mWeekTextBaseLine,
            mWeekTextPaint)
    }

    override fun onDrawSelected(canvas: Canvas, calendar: CalendarDay, x: Int, y: Int, hasScheme: Boolean): Boolean {
        Log.d("onDrawSelected", "CalendarDay:${calendar.year} ${calendar.month} ${calendar.day}")
        return false
    }

    override fun onDrawScheme(canvas: Canvas, calendar: CalendarDay, x: Int, y: Int) {
    }

    override fun onDrawText(canvas: Canvas, calendar: CalendarDay, x: Int, y: Int, hasScheme: Boolean, isSelected: Boolean) {
        val baselineY = mTextBaseLine + y
        val cx = x + mItemWidth / 2
        canvas.drawText(calendar.day.toString(), cx.toFloat(), baselineY,
            if (calendar.isCurrentDay) mCurDayTextPaint else mCurMonthTextPaint)
        if (isLunarDay1(calendar.lunar)) {
            val start = x + (mItemWidth - rectWidth) / 2
            val top = y + mItemHeight - rectHeight
            var bottom = top + rectHeight
            if (monthArray.firstOrNull() == calendar.lunar) {
                bottom = top + (rectHeight * 2)
            }
            rect.set(start, top, start + rectWidth, bottom)
            mMonthTextPaint.color = mDelegate.yearViewCurDayTextColor
            canvas.drawRect(rect, mMonthTextPaint)
        }
    }

    private fun isLunarDay1(lunar: String): Boolean {
        return monthArray.contains(lunar)
    }
}
