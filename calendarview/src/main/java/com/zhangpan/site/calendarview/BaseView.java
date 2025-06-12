
package com.zhangpan.site.calendarview;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

/**
 * 基本的日历View，派生出MonthView 和 WeekView
 */

public abstract class BaseView extends View implements View.OnClickListener, View.OnLongClickListener {


    /**
     * 对应当前月份的日期画笔，周末除外
     */
    public static final int PAINT_CURRENT_MONTH_DATE = 0;
    /**
     * 对应当前选中的日期画笔
     */
    public static final int PAINT_CURRENT_SELECT_DATE = 1;
    /**
     * 对应今日未选中时画笔
     */
    public static final int PAINT_CURRENT_DAY = 2;
    /**
     * 对应非本月日期画笔
     */
    public static final int PAINT_OTHER_MONTH_DATE = 3;
    /**
     * 对应今日未选中时农历画笔
     */
    public static final int PAINT_CURRENT_DAY_LUNAR_DATE = 4;
    /**
     * 对应当前月份农历日期画笔
     */
    public static final int PAINT_CURRENT_MONTH_LUNAR_DATE = 5;
    /**
     * 对应非本月日期画笔
     */
    public static final int PAINT_OTHER_MONTH_LUNAR_DATE = 6;
    /**
     * 对应当前选中的农历日期画笔
     */
    public static final int PAINT_SELECT_LUNAR_DATE = 7;
    /**
     * 对应周末日期画笔
     */
    public static final int PAINT_WEEKEND_DATE = 8;
    /**
     * 对应周末农历日期画笔
     */
    public static final int PAINT_WEEKEND_LUNAR_DATE = 9;


    CalendarViewDelegate mDelegate;

    /**
     * 当前月份日期的笔
     */
    protected Paint mCurMonthTextPaint = new Paint();

    /**
     * 其它月份日期颜色
     */
    protected Paint mOtherMonthTextPaint = new Paint();

    /**
     * 当前月份农历文本颜色
     */
    protected Paint mCurMonthLunarTextPaint = new Paint();

    /**
     * 当前月份农历文本颜色
     */
    protected Paint mSelectedLunarTextPaint = new Paint();

    /**
     * 其它月份农历文本颜色
     */
    protected Paint mOtherMonthLunarTextPaint = new Paint();

    /**
     * 其它月份农历文本颜色
     */
    protected Paint mSchemeLunarTextPaint = new Paint();

    /**
     * 标记的日期背景颜色画笔
     */
    protected Paint mSchemePaint = new Paint();

    /**
     * 被选择的日期背景色
     */
    protected Paint mSelectedPaint = new Paint();

    /**
     * 标记的文本画笔
     */
    protected Paint mSchemeTextPaint = new Paint();

    /**
     * 选中的文本画笔
     */
    protected Paint mSelectTextPaint = new Paint();

    /**
     * 当前日期文本颜色画笔
     */
    protected Paint mCurDayTextPaint = new Paint();

    /**
     * 当前日期文本颜色画笔
     */
    protected Paint mCurDayLunarTextPaint = new Paint();

    /**
     * 日历布局，需要在日历下方放自己的布局
     */
    CalendarLayout mParentLayout;

    /**
     * 日历项
     */
    protected List<CalendarDay> mItems;

    // /**
    //  * 每一项的高度
    //  */
    // protected int mItemHeight;

    /**
     * 每一项的宽度
     */
    protected int mItemWidth;

    /**
     * Text的基线
     */
    protected float mTextBaseLine;

    /**
     * 点击的x、y坐标
     */
    protected float mX, mY;

    /**
     * 是否点击
     */
    boolean isClick = true;

    int pointerCount = 0;

    /**
     * 字体大小
     */
    static final int TEXT_SIZE = 14;

    /**
     * 当前点击项
     */
    int mCurrentItem = -1;

    /**
     * 周起始
     */
    int mWeekStartWidth;


    public BaseView(Context context) {
        this(context, null);
    }

    public BaseView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initPaint(context);
    }

    /**
     * 初始化配置
     * @param context context
     */
    private void initPaint(Context context) {
        mCurMonthTextPaint.setAntiAlias(true);
        mCurMonthTextPaint.setTextAlign(Paint.Align.CENTER);
        mCurMonthTextPaint.setColor(0xFF111111);
        mCurMonthTextPaint.setFakeBoldText(true);
        mCurMonthTextPaint.setTextSize(CalendarUtil.dipToPx(context, TEXT_SIZE));

        mOtherMonthTextPaint.setAntiAlias(true);
        mOtherMonthTextPaint.setTextAlign(Paint.Align.CENTER);
        mOtherMonthTextPaint.setColor(0xFFe1e1e1);
        mOtherMonthTextPaint.setFakeBoldText(true);
        mOtherMonthTextPaint.setTextSize(CalendarUtil.dipToPx(context, TEXT_SIZE));

        mCurMonthLunarTextPaint.setAntiAlias(true);
        mCurMonthLunarTextPaint.setTextAlign(Paint.Align.CENTER);

        mSelectedLunarTextPaint.setAntiAlias(true);
        mSelectedLunarTextPaint.setTextAlign(Paint.Align.CENTER);

        mOtherMonthLunarTextPaint.setAntiAlias(true);
        mOtherMonthLunarTextPaint.setTextAlign(Paint.Align.CENTER);

        mSchemeLunarTextPaint.setAntiAlias(true);
        mSchemeLunarTextPaint.setTextAlign(Paint.Align.CENTER);

        mSchemeTextPaint.setAntiAlias(true);
        mSchemeTextPaint.setStyle(Paint.Style.FILL);
        mSchemeTextPaint.setTextAlign(Paint.Align.CENTER);
        mSchemeTextPaint.setColor(0xffed5353);
        mSchemeTextPaint.setFakeBoldText(true);
        mSchemeTextPaint.setTextSize(CalendarUtil.dipToPx(context, TEXT_SIZE));

        mSelectTextPaint.setAntiAlias(true);
        mSelectTextPaint.setStyle(Paint.Style.FILL);
        mSelectTextPaint.setTextAlign(Paint.Align.CENTER);
        mSelectTextPaint.setColor(0xffed5353);
        mSelectTextPaint.setFakeBoldText(true);
        mSelectTextPaint.setTextSize(CalendarUtil.dipToPx(context, TEXT_SIZE));

        mSchemePaint.setAntiAlias(true);
        mSchemePaint.setStyle(Paint.Style.FILL);
        mSchemePaint.setStrokeWidth(2);
        mSchemePaint.setColor(0xffefefef);

        mCurDayTextPaint.setAntiAlias(true);
        mCurDayTextPaint.setTextAlign(Paint.Align.CENTER);
        mCurDayTextPaint.setColor(Color.RED);
        mCurDayTextPaint.setFakeBoldText(true);
        mCurDayTextPaint.setTextSize(CalendarUtil.dipToPx(context, TEXT_SIZE));

        mCurDayLunarTextPaint.setAntiAlias(true);
        mCurDayLunarTextPaint.setTextAlign(Paint.Align.CENTER);
        mCurDayLunarTextPaint.setColor(Color.RED);
        mCurDayLunarTextPaint.setFakeBoldText(true);
        mCurDayLunarTextPaint.setTextSize(CalendarUtil.dipToPx(context, TEXT_SIZE));

        mSelectedPaint.setAntiAlias(true);
        mSelectedPaint.setStyle(Paint.Style.FILL);
        mSelectedPaint.setStrokeWidth(2);

        setOnClickListener(this);
        setOnLongClickListener(this);
    }

    /**
     * 初始化所有UI配置
     * @param delegate delegate
     */
    void setup(CalendarViewDelegate delegate) {
        this.mDelegate = delegate;
        mWeekStartWidth = mDelegate.getWeekStart();
        updateStyle();
        updateItemHeight();

        initPaint();
    }


    final void updateStyle() {
        if (mDelegate == null) {
            return;
        }
        this.mCurDayTextPaint.setColor(mDelegate.getCurDayTextColor());
        this.mCurDayLunarTextPaint.setColor(mDelegate.getCurDayLunarTextColor());
        this.mCurMonthTextPaint.setColor(mDelegate.getCurrentMonthTextColor());
        this.mOtherMonthTextPaint.setColor(mDelegate.getOtherMonthTextColor());
        this.mCurMonthLunarTextPaint.setColor(mDelegate.getCurrentMonthLunarTextColor());
        this.mSelectedLunarTextPaint.setColor(mDelegate.getSelectedLunarTextColor());
        this.mSelectTextPaint.setColor(mDelegate.getSelectedTextColor());
        this.mOtherMonthLunarTextPaint.setColor(mDelegate.getOtherMonthLunarTextColor());
        this.mSchemeLunarTextPaint.setColor(mDelegate.getSchemeLunarTextColor());
        this.mSchemePaint.setColor(mDelegate.getSchemeThemeColor());
        this.mSchemeTextPaint.setColor(mDelegate.getSchemeTextColor());
        this.mCurMonthTextPaint.setTextSize(mDelegate.getDayTextSize());
        this.mOtherMonthTextPaint.setTextSize(mDelegate.getDayTextSize());
        this.mCurDayTextPaint.setTextSize(mDelegate.getDayTextSize());
        this.mSchemeTextPaint.setTextSize(mDelegate.getDayTextSize());
        this.mSelectTextPaint.setTextSize(mDelegate.getDayTextSize());

        this.mCurMonthLunarTextPaint.setTextSize(mDelegate.getLunarTextSize());
        this.mSelectedLunarTextPaint.setTextSize(mDelegate.getLunarTextSize());
        this.mCurDayLunarTextPaint.setTextSize(mDelegate.getLunarTextSize());
        this.mOtherMonthLunarTextPaint.setTextSize(mDelegate.getLunarTextSize());
        this.mSchemeLunarTextPaint.setTextSize(mDelegate.getLunarTextSize());

        this.mSelectedPaint.setStyle(Paint.Style.FILL);
        this.mSelectedPaint.setColor(mDelegate.getSelectedThemeColor());
    }

    void updateItemHeight() {
        Paint.FontMetrics metrics = mCurMonthTextPaint.getFontMetrics();
        mTextBaseLine = getItemHeight() / 2f - metrics.descent + (metrics.bottom - metrics.top) / 2;
    }

    protected int getItemHeight() {
        return mDelegate.getCalendarItemHeight();
    }


    /**
     * 移除事件
     */
    final void removeSchemes() {
        for (CalendarDay a : mItems) {
            a.setScheme("");
            a.setSchemeColor(0);
            a.setEvents(null);
        }
    }

    /**
     * 添加事件标记，来自Map
     */
    final void addSchemesFromMap() {
        if (mDelegate.mSchemeDatesMap == null || mDelegate.mSchemeDatesMap.isEmpty()) {
            return;
        }
        for (CalendarDay a : mItems) {
            if (mDelegate.mSchemeDatesMap.containsKey(a.toString())) {
                CalendarDay d = mDelegate.mSchemeDatesMap.get(a.toString());
                if (d == null) {
                    continue;
                }
                a.setScheme(TextUtils.isEmpty(d.getScheme()) ? mDelegate.getSchemeText() : d.getScheme());
                a.setSchemeColor(d.getSchemeColor());
                a.setEvents(d.getEvents());
                a.setServerFestival(d.getServerFestival());
            } else {
                a.setScheme("");
                a.setSchemeColor(0);
                a.setEvents(null);
                a.setServerFestival("");
            }
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        pointerCount = event.getPointerCount();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mX = event.getX();
                mY = event.getY();
                isClick = true;
                break;
            case MotionEvent.ACTION_MOVE:
                float mDY;
                if (isClick) {
                    mDY = event.getY() - mY;
                    isClick = Math.abs(mDY) <= 50;
                }
                break;
            case MotionEvent.ACTION_UP:
                mX = event.getX();
                mY = event.getY();
                break;
        }
        return super.onTouchEvent(event);
    }


    /**
     * 开始绘制前的钩子，这里做一些初始化的操作，每次绘制只调用一次，性能高效
     * 没有需要可忽略不实现
     * 例如：
     * 1、需要绘制圆形标记事件背景，可以在这里计算半径
     * 2、绘制矩形选中效果，也可以在这里计算矩形宽和高
     */
    protected void onPreviewDraw(@NonNull Canvas canvas) {
    }

    protected void onFinishDraw(@NonNull Canvas canvas, int lineCount, int lineHeight) {
    }

    /**
     * 绘制周行前调用此方法
     * @param canvas canvas
     * @param weekStartDay 一周的第一天
     * @param lineNum 周行数 0-6
     */
    protected void onBeforeDrawWeek(Canvas canvas, CalendarDay weekStartDay, int lineNum, int lineHeight) {

    }

    /**
     * 是否是选中的
     * @param calendar calendar
     * @return true or false
     */
    protected boolean isSelected(CalendarDay calendar) {
        return mItems != null && mItems.indexOf(calendar) == mCurrentItem;
    }


    /**
     * 更新事件
     */
    final void update() {
        if (mDelegate.mSchemeDatesMap == null || mDelegate.mSchemeDatesMap.isEmpty()) {//清空操作
            removeSchemes();
            invalidate();
            return;
        }
        addSchemesFromMap();
        invalidate();
    }


    /**
     * 是否拦截日期，此设置续设置mCalendarInterceptListener
     * @param calendar calendar
     * @return 是否拦截日期
     */
    protected final boolean onCalendarIntercept(CalendarDay calendar) {
        return mDelegate.mCalendarInterceptListener != null &&
                mDelegate.mCalendarInterceptListener.onCalendarIntercept(calendar);
    }

    /**
     * 是否在日期范围内
     * @param calendar calendar
     * @return 是否在日期范围内
     */
    protected final boolean isInRange(CalendarDay calendar) {
        return mDelegate != null && CalendarUtil.isCalendarInRange(calendar, mDelegate);
    }

    /**
     * 跟新当前日期
     */
    abstract void updateCurrentDate();

    /**
     * 销毁
     */
    protected void onDestroy() {
    }

    @SuppressWarnings("unused")
    protected int getWeekStartWith() {
        return mDelegate != null ? mDelegate.getWeekStart() : CalendarViewDelegate.WEEK_START_WITH_SUN;
    }


    protected int getCalendarPaddingLeft() {
        return mDelegate != null ? mDelegate.getCalendarPaddingLeft() : 0;
    }


    protected int getCalendarPaddingRight() {
        return mDelegate != null ? mDelegate.getCalendarPaddingRight() : 0;
    }


    /**
     * 初始化画笔相关
     */
    protected void initPaint() {

    }
}
