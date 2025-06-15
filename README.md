该仓库代码源自 [huanghaibin-dev/CalendarView](https://github.com/huanghaibin-dev/CalendarView) ，在其代码基础上对周视图、月视图、年视图的绘制逻辑进行了重构。并新增了月视图全屏展开效果的支持,以及新增了月视图切换年视图的动效。

## 一、快速开始

1.在你项目的 root settings.gradle 添加以下代码：

    dependencyResolutionManagement {
    	repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    	repositories {
    		mavenCentral()
    		maven { url 'https://jitpack.io' }
    	}
    }

2.在模块的gradle中添加依赖：

    dependencies {
        implementation 'com.github.zhpanvip:CalendarView:Tag'
    }

Tag: [![](https://p0-xtjj-private.juejin.cn/tos-cn-i-73owjymdk6/d1ece49a74034bb38c74dac7ef0d424c~tplv-73owjymdk6-jj-mark-v1:0:0:0:0:5o6Y6YeR5oqA5pyv56S-5Yy6IEAg6LWM5LiA5YyF6L6j5p2h:q75.awebp?policy=eyJ2bSI6MywidWlkIjoiMjczNTI0MDY1OTM1OTQ0OCJ9\&rk3s=e9ecf3d6\&x-orig-authkey=f32326d3454f2ac7e96d3d06cdbb035152127018\&x-orig-expires=1750080584\&x-orig-sign=R6SqXr7YO5LHJN35HVFNeZOwPs0%3D)](https://jitpack.io/#zhpanvip/CalendarView)

3.xml中添加以下代码：

```xml
<com.zhangpan.site.calendarview.CalendarLayout
android:layout_width="match_parent"
android:layout_height="match_parent"
android:layout_marginTop="20dp"
app:layout_constraintBottom_toBottomOf="parent"
app:layout_constraintEnd_toEndOf="parent"
app:layout_constraintStart_toStartOf="parent"
app:calendar_content_view_id="@id/scroll_view"
app:layout_constraintTop_toTopOf="parent" >

        <com.zhangpan.site.calendarview.CalendarView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            app:calendar_padding="10dp"
            app:current_day_lunar_text_color="@color/select_rect_background"
            app:current_day_text_color="@color/select_rect_background"
            app:current_month_lunar_text_color="@color/black"
            app:current_month_text_color="@color/black"
            app:day_text_size="19dp"
            app:lunar_text_size="10dp"
            app:max_year="2099"
            app:min_year="1900"
            app:month_view_expandable="true"
            app:month_view_show_mode="mode_only_current"
            app:other_month_lunar_text_color="@color/black"
            app:other_month_text_color="@color/black"
            app:selected_lunar_text_color="@color/white"
            app:selected_text_color="@color/white"
            app:selected_theme_color="@color/select_rect_background"
            app:week_background="@color/white"
            app:week_bar_height="@dimen/calendar_week_bar_height"
            app:week_item_height="@dimen/calendar_item_height"
            app:week_start_with="mon"
            app:week_text_color="@color/black"
            app:week_text_size="14dp"
            app:week_view_scrollable="true"
            app:weekend_text_color="@color/weekend_text_color"
            app:year_view_current_day_text_color="@color/select_rect_background"
            app:year_view_day_text_color="?attr/colorOnSurface"
            app:year_view_day_text_size="10dp"
            app:year_view_month_text_color="?attr/colorOnSurface"
            app:year_view_week_height="14dp"
            app:year_view_week_text_color="#3E3E3E"
            app:year_view_week_text_size="10dp" />

        <androidx.core.widget.NestedScrollView
            android:id="@+id/scroll_view"
            android:background="#4c4f76"
            android:layout_width="match_parent"
            android:layout_height="match_parent"/>

    </com.zhangpan.site.calendarview.CalendarLayout>
```

通过以上代码可以实现默认的日历视图效果，效果如下：

![calendar](https://p0-xtjj-private.juejin.cn/tos-cn-i-73owjymdk6/95c4825a0e9a4d988841abd6d6edaa03~tplv-73owjymdk6-jj-mark-v1:0:0:0:0:5o6Y6YeR5oqA5pyv56S-5Yy6IEAg6LWM5LiA5YyF6L6j5p2h:q75.awebp?policy=eyJ2bSI6MywidWlkIjoiMjczNTI0MDY1OTM1OTQ0OCJ9\&rk3s=e9ecf3d6\&x-orig-authkey=f32326d3454f2ac7e96d3d06cdbb035152127018\&x-orig-expires=1750080296\&x-orig-sign=uBP%2BazsWgkUOemTj5wYIamZsiSY%3D)

## 二、自定义样式

CalendarView 提供了强大的扩展能力，你可以通过继承 BaseCalendarDrawer 实现任意的日历样式，可以通过继承

### 1.自定义周视图/月视图/月视图展开效果

你可以通过继承 BaseCalendarDrawer，来绘制任意样式的日历效果。BaseCalendarDrawer 承担了周视图、月视图以及月视图展开动效的绘制逻辑。下面代码展示了如何自定义 CalendarDrawer:

```kotlin
class CustomCalendarDrawer(private var context: Context, delegate: CalendarViewDelegate) :
    BaseCalendarDrawer(context, delegate) {

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
    private val rectRadius =
        context.resources.getDimension(R.dimen.fd_sys_radius_corner_smooth_mini)
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
    override fun onDrawSelected(
        canvas: Canvas,
        calendarDay: CalendarDay,
        x: Int,
        y: Int,
        hasScheme: Boolean
    ): Boolean {
        val monthViewExpandPercent = delegate.monthViewExpandPercent
        val calendarItemHeight = delegate.calendarItemHeight - dp2
        val paddingTop = (delegate.calendarItemHeight - calendarItemHeight + dp1) / 2f
        val height =
            (expandSelectHeight - calendarItemHeight) * monthViewExpandPercent + calendarItemHeight
        rectF.set(
            (x + mPadding),
            y.toFloat() + paddingTop,
            (x + mItemWidth - mPadding),
            (y + height)
        )
        if (calendarDay.isCurrentDay) {
            mSelectedPaint.color = delegate.selectedThemeColor
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
    override fun onDrawScheme(
        canvas: Canvas,
        isSelected: Boolean,
        calendar: CalendarDay,
        x: Int,
        y: Int
    ) {
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
    override fun onDrawText(
        canvas: Canvas,
        calendar: CalendarDay,
        x: Int,
        y: Int,
        hasScheme: Boolean,
        isSelected: Boolean
    ) {
        val monthTextPaint = delegate.switchTextPaint(BaseView.PAINT_CURRENT_MONTH_DATE)
        val metrics = monthTextPaint.fontMetrics
        val height = metrics.descent - metrics.ascent
        val dateTextBaseLine = (metrics.ascent + metrics.descent) / 2 + height + textMarginTop
        val dateTextBaseLineY = y + dateTextBaseLine
        val lunarPaint = delegate.switchTextPaint(BaseView.PAINT_SELECT_LUNAR_DATE)
        val fontMetrics = lunarPaint.fontMetrics
        val lunarTextBaseLine = (fontMetrics.ascent + fontMetrics.descent) / 2
        val lunarTextBaseLineY = y + expandSelectHeight + lunarTextBaseLine - dp4

        val cx = x + mItemWidth / 2f
        val isInRange = isInRange(calendar)

        // 绘制日期
        if (isSelected) {
            val selectedPaint = delegate.switchTextPaint(BaseView.PAINT_CURRENT_SELECT_DATE)
            canvas.drawText(
                calendar.day.toString(), cx, dateTextBaseLineY,
                selectedPaint
            )
        } else {
            val datePaint: Paint = if (calendar.isCurrentDay) {
                delegate.switchTextPaint(BaseView.PAINT_CURRENT_DAY)
            } else if (calendar.isWeekend) {
                delegate.switchTextPaint(BaseView.PAINT_WEEKEND_DATE)
            } else if (calendar.isCurrentMonth && isInRange) {
                delegate.switchTextPaint(BaseView.PAINT_CURRENT_MONTH_DATE)
            } else {
                delegate.switchTextPaint(BaseView.PAINT_OTHER_MONTH_DATE)
            }

            canvas.drawText(calendar.day.toString(), cx, dateTextBaseLineY, datePaint)
        }

        // 绘制农历日期、节日
        if (mIsChineseLanguage) {
            val lunarText = if (delegate.monthViewExpandPercent > 0.6) {
                calendar.lunar
            } else {
                calendar.lunarText
            }
            if (isSelected) {
                val selectLunarPaint = delegate.switchTextPaint(BaseView.PAINT_SELECT_LUNAR_DATE)
                canvas.drawText(lunarText, cx, lunarTextBaseLineY, selectLunarPaint)
            } else {
                val paint = if (calendar.isCurrentDay && isInRange) {
                    delegate.switchTextPaint(BaseView.PAINT_CURRENT_DAY_LUNAR_DATE)
                } else if (calendar.isWeekend) {
                    delegate.switchTextPaint(BaseView.PAINT_WEEKEND_LUNAR_DATE)
                } else if (calendar.isCurrentMonth) {
                    delegate.switchTextPaint(BaseView.PAINT_CURRENT_MONTH_LUNAR_DATE)
                } else {
                    delegate.switchTextPaint(BaseView.PAINT_OTHER_MONTH_LUNAR_DATE)
                }
                canvas.drawText(lunarText, cx, lunarTextBaseLineY, paint)
            }
        }

        startDrawEvent(canvas, calendar, x, y, isSelected)
    }

    /**
     * 绘制日历事件
     */
    private fun startDrawEvent(
        canvas: Canvas,
        calendar: CalendarDay,
        x: Int,
        y: Int,
        isSelected: Boolean
    ) {
        val calendarEvents = calendar.events
        if (calendarEvents != null && calendarEvents.isNotEmpty()) {
            val calendarInitHeight = delegate.calendarItemHeight
            val originBottom = y + calendarInitHeight - eventMarkRectBottomPadding
            val originTop = originBottom - eventMarkRectHeight
            val eventMarkColor = if (isSelected) {
                mTodayNumberColor
            } else {
                eventMarkRectColor
            }
            if (delegate.monthViewExpandPercent <= 0f) {
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

    private fun drawEventRect(
        canvas: Canvas,
        x: Int,
        y: Int,
        index: Int,
        calendarEvent: CalendarDay.CalendarEvent,
        originTop: Float,
        startColor: Int
    ) {
        val monthViewExpandPercent = delegate.monthViewExpandPercent
        val maxWidth = mItemWidth - dp2
        val curWidth = (maxWidth - eventMarkRectWidth) * monthViewExpandPercent + eventMarkRectWidth
        // event rect horizontal margin
        val marginHorizontal = (mItemWidth - curWidth) / 2f
        val endTop =
            y + expandSelectHeight + index * (eventViewHeight + eventRectTopMargin) + eventRectTopMargin
        val curTop = (endTop - originTop) * monthViewExpandPercent + originTop
        val curBottom =
            curTop + (eventViewHeight - eventMarkRectHeight) * monthViewExpandPercent + eventMarkRectHeight

        rectF.set(x + marginHorizontal, curTop, x + mItemWidth - marginHorizontal, curBottom)
        eventRectPaint.setColor(
            interpolateColors(
                monthViewExpandPercent,
                startColor,
                calendarEvent.eventColor
            )
        )
        canvas.drawRoundRect(rectF, eventViewRadius, eventViewRadius, eventRectPaint)
        if (monthViewExpandPercent > 0.1) {
            eventTextPaint.textSize = dp9 * monthViewExpandPercent
            val fm = eventTextPaint.getFontMetrics()
            val height = fm.descent - fm.ascent
            val textWithEllipsis =
                getTextWithEllipsis(calendarEvent.eventName, eventTextPaint, curWidth)
            val offset = dpToPx(context, 2 * monthViewExpandPercent)
            canvas.drawText(
                textWithEllipsis,
                x + marginHorizontal + offset,
                curTop + ((curBottom - curTop) + height) / 2 - offset,
                eventTextPaint
            )
        }
    }

    override fun onStartDrawWeek(
        canvas: Canvas,
        weekStartDay: CalendarDay,
        lineNum: Int,
        itemHeight: Int
    ) {
        super.onStartDrawWeek(canvas, weekStartDay, lineNum, itemHeight)
        mItemHeight = itemHeight
        val weekNum = weekStartDay.weekNum
        if (delegate.isShowWeekNum) {
            mWeekPaint.color = weekLineSolidColor
            canvas.drawLine(
                weekLineMarginStart,
                (lineNum * itemHeight).toFloat() + delegate.calendarPaddingTop,
                mLineWidth.toFloat(),
                (lineNum * itemHeight).toFloat() + delegate.calendarPaddingTop,
                mWeekPaint
            )

            mWeekPaint.color = if (weekStartDay.isCurrentWeek) {
                delegate.selectedThemeColor
            } else {
                weekLineTextColor
            }
            canvas.drawText(
                weekNum.toString(),
                weekLineTextSizeMarginStart,
                (lineNum * itemHeight + weekLineTextSize / 2.3f) + delegate.calendarPaddingTop,
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
            val maxLineHeight = (delegate.monthViewExpandHeight / lineCount * 1F)
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
```

完成自定义 CalendarDrawer 后在 xml 文件中给 CalendarView 添加自定义属性 app:calendar\_drawer="com.zhangpan.site.demo.calendarview\.CustomCalendarDrawer" 即可。

### 2.自定义年视图样式

通过继承 BaseYearView 来绘制需要的年视图样式，示例代码如下：

```
class CustomYearView(context: Context) : BaseYearView(context) {
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
        monthArray = getContext().resources.getStringArray(R.array.lunar_first_of_month)
    }

    private val mTextPadding = CalendarUtil.dipToPx(context, 3f)

    override fun onDrawMonth(
        canvas: Canvas,
        year: Int,
        month: Int,
        x: Int,
        y: Int,
        width: Int,
        height: Int
    ) {
        val text = context
            .resources
            .getStringArray(R.array.month_string_array)[month - 1]

        if (currentMonth == month && currentYear == year) {
            mMonthTextPaint.color = mDelegate.yearViewCurDayTextColor
        } else {
            mMonthTextPaint.color = mDelegate.yearViewMonthTextColor
        }
        canvas.drawText(
            text,
            x + mItemWidth / 2f - mTextPadding,
            y + mMonthTextBaseLine,
            mMonthTextPaint
        )
    }

    override fun onDrawWeek(canvas: Canvas, week: Int, x: Int, y: Int, width: Int, height: Int) {
        val text = context.resources.getStringArray(R.array.year_view_week_string_array)[week]
        canvas.drawText(
            text,
            x + width / 2f,
            y + mWeekTextBaseLine,
            mWeekTextPaint
        )
    }

    override fun onDrawSelected(
        canvas: Canvas,
        calendar: CalendarDay,
        x: Int,
        y: Int,
        hasScheme: Boolean
    ): Boolean {
        Log.d("onDrawSelected", "CalendarDay:${calendar.year} ${calendar.month} ${calendar.day}")
        return false
    }

    override fun onDrawScheme(canvas: Canvas, calendar: CalendarDay, x: Int, y: Int) {
    }

    override fun onDrawText(
        canvas: Canvas,
        calendar: CalendarDay,
        x: Int,
        y: Int,
        hasScheme: Boolean,
        isSelected: Boolean
    ) {
        val baselineY = mTextBaseLine + y
        val cx = x + mItemWidth / 2
        canvas.drawText(
            calendar.day.toString(), cx.toFloat(), baselineY,
            if (calendar.isCurrentDay) mCurDayTextPaint else mCurMonthTextPaint
        )
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
```


