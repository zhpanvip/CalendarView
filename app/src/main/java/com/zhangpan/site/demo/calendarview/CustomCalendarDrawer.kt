package com.zhangpan.site.demo.calendarview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.text.TextPaint
import androidx.core.content.ContextCompat
import com.zhangpan.site.calendarview.BaseCalendarDrawer
import com.zhangpan.site.calendarview.BaseView
import com.zhangpan.site.calendarview.CalendarDay

/**
 * @Description: 绘制日历月视图与周视图
 * @Author: zhangpan
 * @Date: 2025/6/14 15:39
 */
class CustomCalendarDrawer(private var context: Context) : BaseCalendarDrawer(context) {

    companion object {
        private const val DAY_TYPE_HOLIDAY = "1"
        private const val DAY_TYPE_WORK = "2"
        private const val ELLIPSIS = "…"

        /**
         * 30% 透明度
         */
        private const val ALPHA_30 = 76
    }

    private var dp1 = context.resources.getDimension(R.dimen.dp_1)
    private var dp2 = context.resources.getDimension(R.dimen.dp_2)
    private var dp3 = context.resources.getDimension(R.dimen.dp_3)
    private var dp4 = context.resources.getDimension(R.dimen.dp_4)
    private var dp8 = context.resources.getDimension(R.dimen.dp_8)
    private var dp9 = context.resources.getDimension(R.dimen.dp_9)
    private var dp10 = context.resources.getDimension(R.dimen.dp_10)

    /**
     * 用于绘制周数与周分隔线
     */
    private val mWeekPaint: Paint

    /**
     * 绘制“班”“休”画笔
     */
    private val mMarkPaint = Paint()

    private val mSchemePadding: Float = dpToPx(context, 9f)
    private val rectF = RectF()
    private val rectRadius = context.resources.getDimension(R.dimen.fd_sys_radius_corner_smooth_mini)
//    private val workDayBitmap: Bitmap
//    private val whiteWorkDayBitmap: Bitmap
//    private val holidayBitmap: Bitmap
//    private val whiteHolidayBitmap: Bitmap

    private val eventRectPaint: Paint
    private val eventTextPaint: TextPaint
    private val eventMarkRectWidth = dp8
    private val eventMarkRectHeight = dp3
    private val eventMarkRectColor: Int = getColor(R.color.event_circle_color)
    private var eventMarkRectBottomPadding = 0F

    private val mTodayNumberColor: Int = getColor(R.color.white)

    private val mIsChineseLanguage: Boolean

    private val weekLineTextSize: Int
    private val weekLineTextSizeMarginStart: Float
    private val weekLineMarginStart: Float
    private val weekLineSolidColor = getColor(R.color.week_line_solid)

    private val eventMarkRadius = dp8
    private val eventViewRadius = dp3
    private var textMarginTop = 0f
    private var eventRectTopMargin = 0f

    private val eventViewHeight = dpToPx(context, 17f)

    private val mPadding = dp2
    private val expandSelectHeight = dpToPx(context, 49f)

    private val weekLineTextColor: Int = getColor(R.color.week_line_text_color)
    private val eventTextColor = getColor(R.color.black)
    private val selectedDayBackgroundColor = getColor(R.color.select_rect_background)
    private var canShowEventSize: Int = 0

    init {
        val resources = context.resources
        weekLineTextSize = resources.getDimensionPixelSize(R.dimen.week_line_text_size)
        weekLineTextSizeMarginStart = resources.getDimension(R.dimen.week_line_text_margin_start)
        weekLineMarginStart = resources.getDimension(R.dimen.week_line_margin_start)
        eventRectTopMargin = dp2

        eventRectPaint = Paint()
        eventRectPaint.isAntiAlias = true

        eventTextPaint = TextPaint()
        eventTextPaint.textSize = dp10
        eventTextPaint.color = eventTextColor
        eventTextPaint.isFakeBoldText = false
        eventTextPaint.textAlign = Paint.Align.LEFT
        eventTextPaint.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL))

        mMarkPaint.textSize = dp8
        mMarkPaint.isAntiAlias = true
        mMarkPaint.isFakeBoldText = true
        mWeekPaint = Paint()
        mWeekPaint.isAntiAlias = true
        mWeekPaint.textSize = weekLineTextSize.toFloat()
        mWeekPaint.strokeWidth = resources.getDimension(R.dimen.calendar_line_stroke_width)

        mIsChineseLanguage = true
        if (mIsChineseLanguage) {
            eventMarkRectBottomPadding = dp9
            textMarginTop = context.resources.getDimension(R.dimen.calendar_item_text_margin_top_cn)
        } else {
            textMarginTop = context.resources.getDimension(R.dimen.calendar_item_text_margin_top)
            eventMarkRectBottomPadding = dpToPx(context, 15f)
        }
    }

    override fun onPreviewDraw(canvas: Canvas?, lineCount: Int) {
        super.onPreviewDraw(canvas, lineCount)
        canShowEventSize = canShowMaxEventSize(lineCount)
    }

    /**
     * 绘制选中日期背景
     * @param canvas canvas
     * @param calendarDay 日历日历calendar
     * @param x 日历Card x起点rr坐标
     * @param y 日历Card y起点坐标
     * @param hasScheme hasScheme 非标记的日期
     * @return true 则绘制onDrawScheme，因为这里背景色不是是互斥的
     */
    override fun onDrawSelected(canvas: Canvas, calendarDay: CalendarDay, x: Int, y: Int, hasScheme: Boolean): Boolean {
        val monthViewExpandPercent = mDelegate!!.monthViewExpandPercent
        val calendarItemHeight = mDelegate!!.calendarItemHeight - dp2
        val paddingTop = (mDelegate!!.calendarItemHeight - calendarItemHeight + dp1) / 2f
        val height = (expandSelectHeight - calendarItemHeight) * monthViewExpandPercent + calendarItemHeight
        rectF.set((x + mPadding), y.toFloat() + paddingTop, (x + mItemWidth - mPadding), (y + height))
        if (calendarDay.isCurrentDay) {
            mSelectedPaint.color = mDelegate!!.selectedThemeColor
        } else {
            mSelectedPaint.color = selectedDayBackgroundColor
        }
        canvas.drawRoundRect(rectF, rectRadius, rectRadius, mSelectedPaint)
        return true
    }

    /**
     * 绘制标记，如 “班” “休”
     * @param canvas canvas
     * @param isSelected 是否选中
     * @param calendar 日历calendar
     * @param x 日历Card x起点坐标
     * @param y 日历Card y起点坐标
     */
    override fun onDrawScheme(canvas: Canvas, isSelected: Boolean, calendar: CalendarDay, x: Int, y: Int) {
        if (mIsChineseLanguage) {
//            if (DAY_TYPE_HOLIDAY == calendar.scheme) {
//                var bitmap = holidayBitmap
//                if (calendar.isCurrentDay && isSelected) {
//                    bitmap = whiteHolidayBitmap
//                }
//                canvas.drawBitmap(bitmap, x + mItemWidth - mPadding - mSchemePadding - bitmap.width / 2f, y + mPadding + mSchemePadding,
//                    mMarkPaint)
//            } else if (DAY_TYPE_WORK == calendar.scheme) {
//                var bitmap = workDayBitmap
//                if (calendar.isCurrentDay && isSelected) {
//                    bitmap = whiteWorkDayBitmap
//                }
//                canvas.drawBitmap(bitmap, x + mItemWidth - mPadding - mSchemePadding - bitmap.width / 2f, y + mPadding + mSchemePadding,
//                    mMarkPaint)
//            }
        }
    }

    /**
     * 绘制日期、农历日期、节日等文本
     * @param canvas canvas
     * @param calendar 日历calendar
     * @param x 日历Card x起点坐标
     * @param y 日历Card y起点坐标
     * @param hasScheme 是否是标记的日期
     * @param isSelected 是否选中
     */
    override fun onDrawText(canvas: Canvas, calendar: CalendarDay, x: Int, y: Int, hasScheme: Boolean, isSelected: Boolean) {
        val monthTextPaint = mDelegate!!.switchTextPaint(BaseView.PAINT_CURRENT_MONTH_DATE)
        val metrics = monthTextPaint.fontMetrics
        val height = metrics.descent - metrics.ascent
        val dateTextBaseLine = (metrics.ascent + metrics.descent) / 2 + height + textMarginTop
        val dateTextBaseLineY = y + dateTextBaseLine
        val lunarPaint = mDelegate!!.switchTextPaint(BaseView.PAINT_SELECT_LUNAR_DATE)
        val fontMetrics = lunarPaint.fontMetrics
        val lunarTextBaseLine = (fontMetrics.ascent + fontMetrics.descent) / 2
        val lunarTextBaseLineY = y + expandSelectHeight + lunarTextBaseLine - dp4

        val cx = x + mItemWidth / 2f
        val isInRange = isInRange(calendar)

        // 绘制日期
        if (isSelected) {
            val selectedPaint = mDelegate!!.switchTextPaint(BaseView.PAINT_CURRENT_SELECT_DATE)
            canvas.drawText(calendar.day.toString(), cx, dateTextBaseLineY,
                selectedPaint)
        } else {
            val datePaint: Paint = if (calendar.isCurrentDay) {
                mDelegate!!.switchTextPaint(BaseView.PAINT_CURRENT_DAY)
            } else if (calendar.isWeekend) {
                mDelegate!!.switchTextPaint(BaseView.PAINT_WEEKEND_DATE)
            } else if (calendar.isCurrentMonth && isInRange) {
                mDelegate!!.switchTextPaint(BaseView.PAINT_CURRENT_MONTH_DATE)
            } else {
                mDelegate!!.switchTextPaint(BaseView.PAINT_OTHER_MONTH_DATE)
            }

            canvas.drawText(calendar.day.toString(), cx, dateTextBaseLineY, datePaint)
        }

        // 绘制农历日期、节日
        if (mIsChineseLanguage) {
            val lunarText = if (mDelegate!!.monthViewExpandPercent > 0.6) {
                calendar.lunar
            } else {
                calendar.lunarText
            }
            if (isSelected) {
                val selectLunarPaint = mDelegate!!.switchTextPaint(BaseView.PAINT_SELECT_LUNAR_DATE)
                canvas.drawText(lunarText, cx, lunarTextBaseLineY, selectLunarPaint)
            } else {
                val paint = if (calendar.isCurrentDay && isInRange) {
                    mDelegate!!.switchTextPaint(BaseView.PAINT_CURRENT_DAY_LUNAR_DATE)
                } else if (calendar.isWeekend) {
                    mDelegate!!.switchTextPaint(BaseView.PAINT_WEEKEND_LUNAR_DATE)
                } else if (calendar.isCurrentMonth) {
                    mDelegate!!.switchTextPaint(BaseView.PAINT_CURRENT_MONTH_LUNAR_DATE)
                } else {
                    mDelegate!!.switchTextPaint(BaseView.PAINT_OTHER_MONTH_LUNAR_DATE)
                }
                canvas.drawText(lunarText, cx, lunarTextBaseLineY, paint)
            }
        }

        startDrawEvent(canvas, calendar, x, y, isSelected)
    }

    /**
     * 绘制日历事件
     */
    private fun startDrawEvent(canvas: Canvas, calendar: CalendarDay, x: Int, y: Int, isSelected: Boolean) {
        val calendarEvents = calendar.events
        if (calendarEvents != null && calendarEvents.isNotEmpty()) {
            val calendarInitHeight = mDelegate!!.calendarItemHeight
            val originBottom = y + calendarInitHeight - eventMarkRectBottomPadding
            val originTop = originBottom - eventMarkRectHeight
            val eventMarkColor = if (isSelected) {
                mTodayNumberColor
            } else {
                eventMarkRectColor
            }
            if (mDelegate!!.monthViewExpandPercent <= 0f) {
                val curLeft = x + mItemWidth / 2f - eventMarkRectWidth / 2f
                val curRight = x + mItemWidth / 2f + eventMarkRectWidth / 2f
                eventRectPaint.color = eventMarkColor
                rectF.set(curLeft, originTop, curRight, originBottom)
                canvas.drawRoundRect(rectF, eventMarkRadius, eventMarkRadius, eventRectPaint)
            } else {
                calendarEvents.forEachIndexed { index, calendarEvent ->
                    if (index < canShowEventSize) {
                        drawEventRect(canvas, x, y, index, calendarEvent, originTop, eventMarkColor)
                    }
                }
            }
        }
    }

    private fun drawEventRect(canvas: Canvas, x: Int, y: Int, index: Int, calendarEvent: CalendarDay.CalendarEvent, originTop: Float, startColor: Int) {
        val monthViewExpandPercent = mDelegate!!.monthViewExpandPercent
        val maxWidth = mItemWidth - dp2
        val curWidth = (maxWidth - eventMarkRectWidth) * monthViewExpandPercent + eventMarkRectWidth
        // event rect horizontal margin
        val marginHorizontal = (mItemWidth - curWidth) / 2f
        val endTop = y + expandSelectHeight + index * (eventViewHeight + eventRectTopMargin) + eventRectTopMargin
        val curTop = (endTop - originTop) * monthViewExpandPercent + originTop
        val curBottom = curTop + (eventViewHeight - eventMarkRectHeight) * monthViewExpandPercent + eventMarkRectHeight

        rectF.set(x + marginHorizontal, curTop, x + mItemWidth - marginHorizontal, curBottom)
        eventRectPaint.setColor(interpolateColors(monthViewExpandPercent, startColor, calendarEvent.eventColor))
        canvas.drawRoundRect(rectF, eventViewRadius, eventViewRadius, eventRectPaint)
        if (monthViewExpandPercent > 0.1) {
            eventTextPaint.textSize = dp9 * monthViewExpandPercent
            val fm = eventTextPaint.getFontMetrics()
            val height = fm.descent - fm.ascent
            val textWithEllipsis = getTextWithEllipsis(calendarEvent.eventName, eventTextPaint, curWidth)
            val offset = dpToPx(context, 2 * monthViewExpandPercent)
            canvas.drawText(textWithEllipsis, x + marginHorizontal + offset, curTop + ((curBottom - curTop) + height) / 2 - offset, eventTextPaint)
        }
    }

    override fun onStartDrawWeek(canvas: Canvas, weekStartDay: CalendarDay, lineNum: Int, itemHeight: Int) {
        super.onStartDrawWeek(canvas, weekStartDay, lineNum, itemHeight)
        mItemHeight = itemHeight
        val weekNum = weekStartDay.weekNum
        if (mDelegate!!.isShowWeekNum) {
            mWeekPaint.color = weekLineSolidColor
            canvas.drawLine(
                weekLineMarginStart,
                (lineNum * itemHeight).toFloat() + mDelegate!!.calendarPaddingTop,
                mLineWidth.toFloat(),
                (lineNum * itemHeight).toFloat() + mDelegate!!.calendarPaddingTop,
                mWeekPaint
            )

            mWeekPaint.color = if (weekStartDay.isCurrentWeek) {
                mDelegate!!.selectedThemeColor
            } else {
                weekLineTextColor
            }
            canvas.drawText(
                weekNum.toString(),
                weekLineTextSizeMarginStart,
                (lineNum * itemHeight + weekLineTextSize / 2.3f) + mDelegate!!.calendarPaddingTop,
                mWeekPaint
            )
        }
    }

    /**
     * 计算最多可以显示几条日程
     */
    private fun canShowMaxEventSize(lineCount: Int): Int {
        return if (lineCount == 0) {
            ((mItemHeight - expandSelectHeight) / eventViewHeight).toInt()
        } else {
            val maxLineHeight = (mDelegate!!.monthViewExpandHeight / lineCount * 1F)
            ((maxLineHeight - expandSelectHeight) / (eventViewHeight + eventRectTopMargin)).toInt()
        }
    }

    private fun interpolateColors(fraction: Float, startColor: Int, endColor: Int): Int {
        val startA = Color.alpha(startColor)
        val startR = Color.red(startColor)
        val startG = Color.green(startColor)
        val startB = Color.blue(startColor)

        val endA = ALPHA_30
        val endR = Color.red(endColor)
        val endG = Color.green(endColor)
        val endB = Color.blue(endColor)

        val finalA = startA + (fraction * (endA - startA)).toInt()
        val finalR = startR + (fraction * (endR - startR)).toInt()
        val finalG = startG + (fraction * (endG - startG)).toInt()
        val finalB = startB + (fraction * (endB - startB)).toInt()

        return Color.argb(finalA, finalR, finalG, finalB)
    }

    private fun getColor(color: Int): Int {
        return ContextCompat.getColor(mContext, color)
    }

    private fun getTextWithEllipsis(text: String, paint: TextPaint, width: Float): String {
        val textWidth = paint.measureText(text)
        if (textWidth <= width) {
            return text
        } else {
            val ellipsisWidth = paint.measureText(ELLIPSIS)
            val availableWidth = (width - ellipsisWidth).toInt()
            var availableLength = 0
            for (i in text.indices) {
                val charWidth = paint.measureText(text.substring(0, i + 1))
                if (charWidth <= availableWidth) {
                    availableLength = i + 1
                } else {
                    break
                }
            }
            return text.substring(0, availableLength) + ELLIPSIS
        }
    }
}
