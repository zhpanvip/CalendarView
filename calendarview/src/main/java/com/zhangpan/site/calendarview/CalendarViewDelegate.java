
package com.zhangpan.site.calendarview;

import static com.zhangpan.site.calendarview.BaseView.PAINT_CURRENT_DAY;
import static com.zhangpan.site.calendarview.BaseView.PAINT_CURRENT_DAY_LUNAR_DATE;
import static com.zhangpan.site.calendarview.BaseView.PAINT_CURRENT_MONTH_DATE;
import static com.zhangpan.site.calendarview.BaseView.PAINT_CURRENT_MONTH_LUNAR_DATE;
import static com.zhangpan.site.calendarview.BaseView.PAINT_CURRENT_SELECT_DATE;
import static com.zhangpan.site.calendarview.BaseView.PAINT_OTHER_MONTH_DATE;
import static com.zhangpan.site.calendarview.BaseView.PAINT_OTHER_MONTH_LUNAR_DATE;
import static com.zhangpan.site.calendarview.BaseView.PAINT_SELECT_LUNAR_DATE;
import static com.zhangpan.site.calendarview.BaseView.PAINT_WEEKEND_DATE;
import static com.zhangpan.site.calendarview.BaseView.PAINT_WEEKEND_LUNAR_DATE;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.PathInterpolator;

import androidx.annotation.IntDef;
import androidx.annotation.Nullable;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Google规范化的属性委托,
 * 代码量多，但是不影响阅读性
 */
public final class CalendarViewDelegate {

    private final TextPaint textPaint = new TextPaint();

    private static final String TAG = "CalendarViewDelegate";

    private static PathInterpolator pathInterpolator;

    /**
     * 周起始：周日
     */
    static final int WEEK_START_WITH_SUN = 1;

    /**
     * 周起始：周一
     */
    static final int WEEK_START_WITH_MON = 2;

    /**
     * 周起始：周六
     */
    static final int WEEK_START_WITH_SAT = 7;

    /**
     * 默认选择日期1号first_day_of_month
     */
    static final int FIRST_DAY_OF_MONTH = 0;

    /**
     * 跟随上个月last_select_day
     */
    static final int LAST_MONTH_VIEW_SELECT_DAY = 1;

    /**
     * 跟随上个月last_select_day_ignore_current忽视今天
     */
    static final int LAST_MONTH_VIEW_SELECT_DAY_IGNORE_CURRENT = 2;

    private final int showWeekPaddingLeft;

    private int mDefaultCalendarSelectDay;

    /**
     * 周起始
     */
    private int mWeekStart;

    /**
     * 全部显示
     */
    static final int MODE_ALL_MONTH = 0;
    /**
     * 仅显示当前月份
     */
    static final int MODE_ONLY_CURRENT_MONTH = 1;

    /**
     * 自适应显示，不会多出一行，但是会自动填充
     */
    static final int MODE_FIT_MONTH = 2;

    /**
     * 月份显示模式
     */
    private int mMonthViewShowMode;


    /**
     * 默认选择模式
     */
    static final int SELECT_MODE_DEFAULT = 0;

    /**
     * 单选模式
     */
    static final int SELECT_MODE_SINGLE = 1;

    /**
     * 范围选择模式
     */
    static final int SELECT_MODE_RANGE = 2;

    /**
     * 多选模式
     */
    static final int SELECT_MODE_MULTI = 3;

    /**
     * 选择模式
     */
    private int mSelectMode;

    /**
     * 月视图是否支持展开
     */
    private boolean monthViewExpandable;

    /**
     * 周 item 高度
     */
    private final int weekItemHeight;

    /**
     * 月视图偏移的高度
     */
    private int monthViewHeightOffset;

    /**
     * 月视图展开后的高度
     */
    private int monthViewExpandHeight;


    /**
     * 支持转换的最小农历年份
     */
    static final int MIN_YEAR = 1900;
    /**
     * 支持转换的最大农历年份
     */
    private static final int MAX_YEAR = 2099;

    /**
     * 各种字体颜色，看名字知道对应的地方
     */
    private int mCurDayTextColor;
    private final int mCurDayLunarTextColor;
    private final int mWeekTextColor;
    private int mSchemeTextColor;
    private int mSchemeLunarTextColor;
    private int mOtherMonthTextColor;
    private int mCurrentMonthTextColor;
    private int mSelectedTextColor;
    private int mSelectedLunarTextColor;
    private int mCurMonthLunarTextColor;
    private int mOtherMonthLunarTextColor;

    private final int weekendTextColor;

    private boolean preventLongPressedSelected;

    /**
     * 年视图一些padding
     */
    private final int mYearViewPadding;
    private int mYearViewPaddingLeft;
    private int mYearViewPaddingRight;

    /**
     * 年视图一些padding
     */
    private final int mYearViewMonthPaddingLeft;
    private final int mYearViewMonthPaddingRight;
    private final int mYearViewMonthPaddingTop;
    private final int mYearViewMonthPaddingBottom;

    /**
     * 日历内部左右padding
     */
    private int mCalendarPadding;

    /**
     * 日历内部左padding
     */
    private int mCalendarPaddingLeft;

    /**
     * 日历内部右padding
     */
    private int mCalendarPaddingRight;
    /**
     * 日历内部上 padding
     */
    private int mCalendarPaddingTop;

    private final int weekLinePaddingTop;

    /**
     * 年视图字体大小
     */
    private final int mYearViewMonthTextSize;
    private final int mYearViewDayTextSize;
    private final int mYearViewWeekTextSize;

    /**
     * 年视图月份高度和周的高度
     */
    private final int mYearViewMonthHeight;
    private final int mYearViewWeekHeight;


    /**
     * 年视图字体和标记颜色
     */
    private int mYearViewMonthTextColor;
    private int mYearViewDayTextColor;
    private int mYearViewSchemeTextColor;
    private final int mYearViewSelectTextColor;
    private final int mYearViewCurDayTextColor;
    private final int mYearViewWeekTextColor;

    /**
     * 星期栏的背景、线的背景、年份背景
     */
    private final int mWeekLineBackground;
    private final int mYearViewBackground;
    private final int mWeekBackground;

    /**
     * 星期栏Line margin
     */
    private final int mWeekLineMargin;

    /**
     * 星期栏字体大小
     */
    private final int mWeekTextSize;

    /**
     * 标记的主题色和选中的主题色
     */
    private int mSchemeThemeColor, mSelectedThemeColor;


    /**
     * 自定义的日历路径
     */
    private final String mMonthViewClassPath;

    /**
     * 月视图类
     */
    private Class<?> mMonthViewClass;

    /**
     * 自定义周视图路径
     */
    private final String mWeekViewClassPath;

    /**
     * 周视图类
     */
    private Class<?> mWeekViewClass;
    private Class<?> mCalendarDrawer;

    /**
     * 月视图当前展开百分比
     */
    private float monthViewExpandPercent;

    public boolean isShowWeekNum() {
        return showWeekNum;
    }

    public void setShowWeekNum(boolean showWeekNum) {
        this.showWeekNum = showWeekNum;
    }

    public int getShowWeekPaddingLeft() {
        return showWeekPaddingLeft;
    }

    private boolean showWeekNum = false;

    /**
     * 自定义年视图路径
     */
    private final String mYearViewClassPath;

    private final String mCalendarDrawerPath;

    /**
     * 周视图类
     */
    private Class<?> mYearViewClass;

    /**
     * 自定义周栏路径
     */
    private final String mWeekBarClassPath;

    /**
     * 自定义周栏
     */
    private Class<?> mWeekBarClass;

    /**
     * 年月视图是否打开
     */
    boolean isShowYearSelectedLayout;

    /**
     * 标记文本
     */
    private String mSchemeText;

    /**
     * 最小年份和最大年份
     */
    private int mMinYear, mMaxYear;

    /**
     * 最小年份和最大年份对应最小月份和最大月份
     * when you want set 2015-07 to 2017-08
     */
    private int mMinYearMonth, mMaxYearMonth;

    /**
     * 最小年份和最大年份对应最小天和最大天数
     * when you want set like 2015-07-08 to 2017-08-30
     */
    private int mMinYearDay, mMaxYearDay;

    /**
     * 日期和农历文本大小
     */
    private final int mDayTextSize;
    private final int mLunarTextSize;

    /**
     * 是否是全屏日历
     */
    private final boolean isFullScreenCalendar;

    /**
     * 星期栏的高度
     */
    private final int mWeekBarHeight;

    /**
     * 今天的日子
     */
    private CalendarDay mCurrentDate;


    private boolean mMonthViewScrollable,
            mWeekViewScrollable,
            mYearViewScrollable;

    /**
     * 当前月份和周视图的item位置
     */
    int mCurrentMonthViewItem;


    /**
     * 标记的日期,数量巨大，请使用这个
     */
    Map<String, CalendarDay> mSchemeDatesMap;

    /**
     * 点击Padding位置事件
     */
    CalendarView.OnClickCalendarPaddingListener mClickCalendarPaddingListener;

    /**
     * 日期拦截事件
     */
    CalendarView.OnCalendarInterceptListener mCalendarInterceptListener;

    /**
     * 日期选中监听
     */
    CalendarView.OnCalendarSelectListener mCalendarSelectListener;

    /**
     * 范围选择
     */
    CalendarView.OnCalendarRangeSelectListener mCalendarRangeSelectListener;


    /**
     * 多选选择事件
     */
    CalendarView.OnCalendarMultiSelectListener mCalendarMultiSelectListener;

    /**
     * 外部日期长按事件
     */
    CalendarView.OnCalendarLongClickListener mCalendarLongClickListener;

    /**
     * 内部日期切换监听，用于内部更新计算
     */
    CalendarView.OnInnerDateSelectedListener mInnerListener;

    /**
     * 快速年份切换
     */
    CalendarView.OnYearChangeListener mYearChangeListener;


    /**
     * 月份切换事件
     */
    private final List<CalendarView.OnMonthChangeListener> mMonthChangeListeners = new ArrayList<>();

    /**
     * 周视图改变事件
     */
    CalendarView.OnWeekChangeListener mWeekChangeListener;

    /**
     * 视图改变事件
     */
    CalendarView.OnViewChangeListener mViewChangeListener;


    /**
     * 年视图改变事件
     */
    CalendarView.OnYearViewChangeListener mYearViewChangeListener;

    /**
     * 保存选中的日期
     */
    CalendarDay mSelectedCalendar;

    /**
     * 保存标记位置
     */
    CalendarDay mIndexCalendar;

    /**
     * 多选日历
     */
    Map<String, CalendarDay> mSelectedCalendars = new HashMap<>();

    private int mMaxMultiSelectSize;

    /**
     * 选择范围日历
     */
    CalendarDay mSelectedStartRangeCalendar, mSelectedEndRangeCalendar;

    private int mMinSelectRange, mMaxSelectRange;

    CalendarViewDelegate(Context context, @Nullable AttributeSet attrs) {
        textPaint.setAntiAlias(true);
        textPaint.setStyle(Paint.Style.FILL);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CalendarView);
        LunarCalendar.init(context);
        showWeekPaddingLeft = context.getResources().getDimensionPixelSize(R.dimen.show_week_padding_right);
        int defaultItemHeight = context.getResources().getDimensionPixelSize(R.dimen.default_calendar_item_height);
        weekLinePaddingTop = context.getResources().getDimensionPixelSize(R.dimen.week_line_padding_top);
        weekItemHeight = array.getDimensionPixelSize(R.styleable.CalendarView_week_item_height, defaultItemHeight);
        monthViewExpandable = array.getBoolean(R.styleable.CalendarView_month_view_expandable, false);
        mCalendarPadding = (int) array.getDimension(R.styleable.CalendarView_calendar_padding, 0);
        mCalendarPaddingLeft = (int) array.getDimension(R.styleable.CalendarView_calendar_padding_left, 0);
        mCalendarPaddingRight = (int) array.getDimension(R.styleable.CalendarView_calendar_padding_right, 0);
        mCalendarPaddingTop = array.getDimensionPixelSize(R.styleable.CalendarView_calendar_padding_top, 0);
        if (mCalendarPadding != 0) {
            mCalendarPaddingLeft = mCalendarPadding;
            mCalendarPaddingRight = mCalendarPadding;
        }
        mSchemeTextColor = array.getColor(R.styleable.CalendarView_scheme_text_color, 0xFFFFFFFF);
        mSchemeLunarTextColor = array.getColor(R.styleable.CalendarView_scheme_lunar_text_color, 0xFFe1e1e1);
        mSchemeThemeColor = array.getColor(R.styleable.CalendarView_scheme_theme_color, 0x50CFCFCF);
        mMonthViewClassPath = array.getString(R.styleable.CalendarView_month_view);
        mCalendarDrawerPath = array.getString(R.styleable.CalendarView_calendar_drawer);
        mYearViewClassPath = array.getString(R.styleable.CalendarView_year_view);
        mWeekViewClassPath = array.getString(R.styleable.CalendarView_week_view);
        mWeekBarClassPath = array.getString(R.styleable.CalendarView_week_bar_view);
        mWeekTextSize = array.getDimensionPixelSize(R.styleable.CalendarView_week_text_size,
                CalendarUtil.dipToPx(context, 14));
        mWeekBarHeight = (int) array.getDimension(R.styleable.CalendarView_week_bar_height,
                CalendarUtil.dipToPx(context, 40));
        mWeekLineMargin = (int) array.getDimension(R.styleable.CalendarView_week_line_margin,
                CalendarUtil.dipToPx(context, 0));

        mSchemeText = array.getString(R.styleable.CalendarView_scheme_text);
        if (TextUtils.isEmpty(mSchemeText)) {
            mSchemeText = "";
        }

        mMonthViewScrollable = array.getBoolean(R.styleable.CalendarView_month_view_scrollable, true);
        mWeekViewScrollable = array.getBoolean(R.styleable.CalendarView_week_view_scrollable, true);
        mYearViewScrollable = array.getBoolean(R.styleable.CalendarView_year_view_scrollable, true);

        mDefaultCalendarSelectDay = array.getInt(R.styleable.CalendarView_month_view_auto_select_day,
                FIRST_DAY_OF_MONTH);

        mMonthViewShowMode = array.getInt(R.styleable.CalendarView_month_view_show_mode, MODE_ALL_MONTH);
        mWeekStart = array.getInt(R.styleable.CalendarView_week_start_with, WEEK_START_WITH_SUN);
        mSelectMode = array.getInt(R.styleable.CalendarView_select_mode, SELECT_MODE_DEFAULT);
        mMaxMultiSelectSize = array.getInt(R.styleable.CalendarView_max_multi_select_size, Integer.MAX_VALUE);
        mMinSelectRange = array.getInt(R.styleable.CalendarView_min_select_range, -1);
        mMaxSelectRange = array.getInt(R.styleable.CalendarView_max_select_range, -1);
        setSelectRange(mMinSelectRange, mMaxSelectRange);

        mWeekBackground = array.getColor(R.styleable.CalendarView_week_background, Color.WHITE);
        mWeekLineBackground = array.getColor(R.styleable.CalendarView_week_line_background, Color.TRANSPARENT);
        mYearViewBackground = array.getColor(R.styleable.CalendarView_year_view_background, Color.WHITE);
        mWeekTextColor = array.getColor(R.styleable.CalendarView_week_text_color, 0xFF333333);

        weekendTextColor = array.getColor(R.styleable.CalendarView_weekend_text_color, 0xFF111111);
        mCurDayTextColor = array.getColor(R.styleable.CalendarView_current_day_text_color, Color.RED);
        mCurDayLunarTextColor = array.getColor(R.styleable.CalendarView_current_day_lunar_text_color, Color.RED);

        mSelectedThemeColor = array.getColor(R.styleable.CalendarView_selected_theme_color, 0x50CFCFCF);
        mSelectedTextColor = array.getColor(R.styleable.CalendarView_selected_text_color, 0xFF111111);

        mSelectedLunarTextColor = array.getColor(R.styleable.CalendarView_selected_lunar_text_color, 0xFF111111);
        mCurrentMonthTextColor = array.getColor(R.styleable.CalendarView_current_month_text_color, 0xFF111111);
        mOtherMonthTextColor = array.getColor(R.styleable.CalendarView_other_month_text_color, 0xFFe1e1e1);

        mCurMonthLunarTextColor = array.getColor(R.styleable.CalendarView_current_month_lunar_text_color, 0xffe1e1e1);
        mOtherMonthLunarTextColor = array.getColor(R.styleable.CalendarView_other_month_lunar_text_color, 0xffe1e1e1);
        mMinYear = array.getInt(R.styleable.CalendarView_min_year, 1971);
        mMaxYear = array.getInt(R.styleable.CalendarView_max_year, 2055);
        mMinYearMonth = array.getInt(R.styleable.CalendarView_min_year_month, 1);
        mMaxYearMonth = array.getInt(R.styleable.CalendarView_max_year_month, 12);
        mMinYearDay = array.getInt(R.styleable.CalendarView_min_year_day, 1);
        mMaxYearDay = array.getInt(R.styleable.CalendarView_max_year_day, -1);

        mDayTextSize = array.getDimensionPixelSize(R.styleable.CalendarView_day_text_size,
                CalendarUtil.dipToPx(context, 16));
        mLunarTextSize = array.getDimensionPixelSize(R.styleable.CalendarView_lunar_text_size,
                CalendarUtil.dipToPx(context, 10));

        isFullScreenCalendar = array.getBoolean(R.styleable.CalendarView_calendar_match_parent, false);

        //年视图相关
        mYearViewMonthTextSize = array.getDimensionPixelSize(R.styleable.CalendarView_year_view_month_text_size,
                CalendarUtil.dipToPx(context, 18));
        mYearViewDayTextSize = array.getDimensionPixelSize(R.styleable.CalendarView_year_view_day_text_size,
                CalendarUtil.dipToPx(context, 7));
        mYearViewMonthTextColor = array.getColor(R.styleable.CalendarView_year_view_month_text_color, 0xFF111111);
        mYearViewDayTextColor = array.getColor(R.styleable.CalendarView_year_view_day_text_color, 0xFF111111);
        mYearViewSchemeTextColor = array.getColor(R.styleable.CalendarView_year_view_scheme_color, mSchemeThemeColor);
        mYearViewWeekTextColor = array.getColor(R.styleable.CalendarView_year_view_week_text_color, 0xFF333333);
        mYearViewCurDayTextColor = array.getColor(R.styleable.CalendarView_year_view_current_day_text_color, mCurDayTextColor);
        mYearViewSelectTextColor = array.getColor(R.styleable.CalendarView_year_view_select_text_color, 0xFF333333);
        mYearViewWeekTextSize = array.getDimensionPixelSize(R.styleable.CalendarView_year_view_week_text_size,
                CalendarUtil.dipToPx(context, 8));
        mYearViewMonthHeight = array.getDimensionPixelSize(R.styleable.CalendarView_year_view_month_height,
                CalendarUtil.dipToPx(context, 32));
        mYearViewWeekHeight = array.getDimensionPixelSize(R.styleable.CalendarView_year_view_week_height,
                CalendarUtil.dipToPx(context, 0));

        mYearViewPadding = (int) array.getDimension(R.styleable.CalendarView_year_view_padding,
                CalendarUtil.dipToPx(context, 12));
        mYearViewPaddingLeft = (int) array.getDimension(R.styleable.CalendarView_year_view_padding_left,
                CalendarUtil.dipToPx(context, 12));
        mYearViewPaddingRight = (int) array.getDimension(R.styleable.CalendarView_year_view_padding_right,
                CalendarUtil.dipToPx(context, 12));

        if (mYearViewPadding != 0) {
            mYearViewPaddingLeft = mYearViewPadding;
            mYearViewPaddingRight = mYearViewPadding;
        }

        mYearViewMonthPaddingTop = (int) array.getDimension(R.styleable.CalendarView_year_view_month_padding_top,
                CalendarUtil.dipToPx(context, 4));
        mYearViewMonthPaddingBottom = (int) array.getDimension(R.styleable.CalendarView_year_view_month_padding_bottom,
                CalendarUtil.dipToPx(context, 4));

        mYearViewMonthPaddingLeft = (int) array.getDimension(R.styleable.CalendarView_year_view_month_padding_left,
                CalendarUtil.dipToPx(context, 4));
        mYearViewMonthPaddingRight = (int) array.getDimension(R.styleable.CalendarView_year_view_month_padding_right,
                CalendarUtil.dipToPx(context, 4));

        if (mMinYear <= MIN_YEAR) mMinYear = MIN_YEAR;
        if (mMaxYear >= MAX_YEAR) mMaxYear = MAX_YEAR;
        array.recycle();
        init();
    }

    private void init() {
        mCurrentDate = CalendarDay.obtain();
        Date d = new Date();
        mCurrentDate.setYear(CalendarUtil.getDate("yyyy", d));
        mCurrentDate.setMonth(CalendarUtil.getDate("MM", d));
        mCurrentDate.setDay(CalendarUtil.getDate("dd", d));
        mCurrentDate.setCurrentDay(true);
        LunarCalendar.setupLunarCalendar(mCurrentDate);
        setRange(mMinYear, mMinYearMonth, mMaxYear, mMaxYearMonth);

        try {
            mWeekBarClass = TextUtils.isEmpty(mWeekBarClassPath) ?
                    WeekBar.class : Class.forName(mWeekBarClassPath);
        } catch (Exception e) {
            Log.e(TAG, "error:" + e.getMessage());
        }

        try {
            mYearViewClass = TextUtils.isEmpty(mYearViewClassPath) ?
                    DefaultYearView.class : Class.forName(mYearViewClassPath);
        } catch (Exception e) {
            Log.e(TAG, "error:" + e.getMessage());
        }
        try {
            mMonthViewClass = TextUtils.isEmpty(mMonthViewClassPath) ?
                    CalendarMonthView.class : Class.forName(mMonthViewClassPath);
        } catch (Exception e) {
            Log.e(TAG, "error:" + e.getMessage());
        }
        try {
            mWeekViewClass = TextUtils.isEmpty(mWeekViewClassPath) ?
                    CalendarWeekView.class : Class.forName(mWeekViewClassPath);
        } catch (Exception e) {
            Log.e(TAG, "error:" + e.getMessage());
        }
        try {
            mCalendarDrawer = TextUtils.isEmpty(mCalendarDrawerPath) ?
                    CalendarDrawer.class : Class.forName(mCalendarDrawerPath);
        } catch (Exception e) {
            Log.e(TAG, "error:" + e.getMessage());
        }
    }


    private void setRange(int minYear, int minYearMonth,
            int maxYear, int maxYearMonth) {
        this.mMinYear = minYear;
        this.mMinYearMonth = minYearMonth;
        this.mMaxYear = maxYear;
        this.mMaxYearMonth = maxYearMonth;
        if (this.mMaxYear < mCurrentDate.getYear()) {
            this.mMaxYear = mCurrentDate.getYear();
        }
        if (this.mMaxYearDay == -1) {
            this.mMaxYearDay = CalendarUtil.getMonthDaysCount(this.mMaxYear, mMaxYearMonth);
        }
        int y = mCurrentDate.getYear() - this.mMinYear;
        mCurrentMonthViewItem = 12 * y + mCurrentDate.getMonth() - this.mMinYearMonth;
    }

    void setRange(int minYear, int minYearMonth, int minYearDay,
            int maxYear, int maxYearMonth, int maxYearDay) {
        this.mMinYear = minYear;
        this.mMinYearMonth = minYearMonth;
        this.mMinYearDay = minYearDay;
        this.mMaxYear = maxYear;
        this.mMaxYearMonth = maxYearMonth;
        this.mMaxYearDay = maxYearDay;

        if (this.mMaxYearDay == -1) {
            this.mMaxYearDay = CalendarUtil.getMonthDaysCount(this.mMaxYear, mMaxYearMonth);
        }
        int y = mCurrentDate.getYear() - this.mMinYear;
        mCurrentMonthViewItem = 12 * y + mCurrentDate.getMonth() - this.mMinYearMonth;
    }

    List<CalendarView.OnMonthChangeListener> getMonthChangeListeners() {
        return mMonthChangeListeners;
    }

    void registerMonthChangeListener(CalendarView.OnMonthChangeListener monthChangeListener) {
        if (!mMonthChangeListeners.contains(monthChangeListener)) {
            mMonthChangeListeners.add(monthChangeListener);
        }
    }

    void unregisterMonthChangeListener(CalendarView.OnMonthChangeListener monthChangeListener) {
        mMonthChangeListeners.remove(monthChangeListener);
    }

    boolean monthViewExpandable() {
        return monthViewExpandable;
    }

    void setMonthViewExpandable(boolean monthViewExpandable) {
        this.monthViewExpandable = monthViewExpandable;
    }

    String getSchemeText() {
        return mSchemeText;
    }

    int getCurDayTextColor() {
        return mCurDayTextColor;
    }

    int getCurDayLunarTextColor() {
        return mCurDayLunarTextColor;
    }

    @SuppressWarnings("unused")
    int getWeekTextColor() {
        return mWeekTextColor;
    }

    int getSchemeTextColor() {
        return mSchemeTextColor;
    }

    int getSchemeLunarTextColor() {
        return mSchemeLunarTextColor;
    }

    int getOtherMonthTextColor() {
        return mOtherMonthTextColor;
    }

    int getCurrentMonthTextColor() {
        return mCurrentMonthTextColor;
    }

    int getSelectedTextColor() {
        return mSelectedTextColor;
    }

    int getSelectedLunarTextColor() {
        return mSelectedLunarTextColor;
    }

    public int getCurrentMonthLunarTextColor() {
        return mCurMonthLunarTextColor;
    }

    int getOtherMonthLunarTextColor() {
        return mOtherMonthLunarTextColor;
    }

    int getSchemeThemeColor() {
        return mSchemeThemeColor;
    }

    public int getSelectedThemeColor() {
        return mSelectedThemeColor;
    }

    int getWeekBackground() {
        return mWeekBackground;
    }

    int getYearViewBackground() {
        return mYearViewBackground;
    }

    int getWeekLineBackground() {
        return mWeekLineBackground;
    }

    int getWeekLineMargin() {
        return mWeekLineMargin;
    }

    Class<?> getMonthViewClass() {
        return mMonthViewClass;
    }

    Class<?> getWeekViewClass() {
        return mWeekViewClass;
    }

    Class<?> getWeekBarClass() {
        return mWeekBarClass;
    }

    Class<?> getYearViewClass() {
        return mYearViewClass;
    }

    Class<?> getCalendarDrawerClass() {
        return mCalendarDrawer;
    }

    String getYearViewClassPath() {
        return mYearViewClassPath;
    }

    int getWeekBarHeight() {
        return mWeekBarHeight;
    }

    int getMinYear() {
        return mMinYear;
    }

    int getMaxYear() {
        return mMaxYear;
    }

    int getDayTextSize() {
        return mDayTextSize;
    }

    int getLunarTextSize() {
        return mLunarTextSize;
    }

    public int getCalendarItemHeight() {
        return weekItemHeight;
    }

    /**
     * 获取月视图展开增加的高度
     * @param originMonthViewHeight 月视图未展开前初始高度
     * @return 月视图展开增加的高度
     */
    public int getMonthViewHeightOffset(int originMonthViewHeight) {
        return (int) ((monthViewExpandHeight - originMonthViewHeight) * monthViewExpandPercent);
    }

    int getMinYearMonth() {
        return mMinYearMonth;
    }

    int getMaxYearMonth() {
        return mMaxYearMonth;
    }


    int getYearViewMonthTextSize() {
        return mYearViewMonthTextSize;
    }

    public int getYearViewMonthTextColor() {
        return mYearViewMonthTextColor;
    }

    int getYearViewWeekTextSize() {
        return mYearViewWeekTextSize;
    }

    int getYearViewWeekTextColor() {
        return mYearViewWeekTextColor;
    }

    int getYearViewSelectTextColor() {
        return mYearViewSelectTextColor;
    }

    public int getYearViewCurDayTextColor() {
        return mYearViewCurDayTextColor;
    }

    @SuppressWarnings("unused")
    int getYearViewPadding() {
        return mYearViewPadding;
    }

    int getYearViewPaddingLeft() {
        return mYearViewPaddingLeft;
    }

    int getYearViewPaddingRight() {
        return mYearViewPaddingRight;
    }


    int getYearViewMonthPaddingLeft() {
        return mYearViewMonthPaddingLeft;
    }

    int getYearViewMonthPaddingRight() {
        return mYearViewMonthPaddingRight;
    }

    int getYearViewMonthPaddingTop() {
        return mYearViewMonthPaddingTop;
    }

    int getYearViewMonthPaddingBottom() {
        return mYearViewMonthPaddingBottom;
    }

    int getYearViewWeekHeight() {
        return mYearViewWeekHeight;
    }

    int getYearViewMonthHeight() {
        return mYearViewMonthHeight;
    }

    int getYearViewDayTextColor() {
        return mYearViewDayTextColor;
    }

    int getYearViewDayTextSize() {
        return mYearViewDayTextSize;
    }

    int getYearViewSchemeTextColor() {
        return mYearViewSchemeTextColor;
    }

    int getMonthViewShowMode() {
        return mMonthViewShowMode;
    }

    void setMonthViewShowMode(int monthViewShowMode) {
        this.mMonthViewShowMode = monthViewShowMode;
    }

    void setTextColor(int curDayTextColor, int curMonthTextColor, int otherMonthTextColor, int curMonthLunarTextColor, int otherMonthLunarTextColor) {
        mCurDayTextColor = curDayTextColor;
        mOtherMonthTextColor = otherMonthTextColor;
        mCurrentMonthTextColor = curMonthTextColor;
        mCurMonthLunarTextColor = curMonthLunarTextColor;
        mOtherMonthLunarTextColor = otherMonthLunarTextColor;
    }

    void setSchemeColor(int schemeColor, int schemeTextColor, int schemeLunarTextColor) {
        this.mSchemeThemeColor = schemeColor;
        this.mSchemeTextColor = schemeTextColor;
        this.mSchemeLunarTextColor = schemeLunarTextColor;
    }

    void setYearViewTextColor(int yearViewMonthTextColor, int yearViewDayTextColor, int yarViewSchemeTextColor) {
        this.mYearViewMonthTextColor = yearViewMonthTextColor;
        this.mYearViewDayTextColor = yearViewDayTextColor;
        this.mYearViewSchemeTextColor = yarViewSchemeTextColor;
    }

    void setSelectColor(int selectedColor, int selectedTextColor, int selectedLunarTextColor) {
        this.mSelectedThemeColor = selectedColor;
        this.mSelectedTextColor = selectedTextColor;
        this.mSelectedLunarTextColor = selectedLunarTextColor;
    }

    void setThemeColor(int selectedThemeColor, int schemeColor) {
        this.mSelectedThemeColor = selectedThemeColor;
        this.mSchemeThemeColor = schemeColor;
    }

    boolean isMonthViewScrollable() {
        return mMonthViewScrollable;
    }

    boolean isWeekViewScrollable() {
        return mWeekViewScrollable;
    }

    boolean isYearViewScrollable() {
        return mYearViewScrollable;
    }

    void setMonthViewScrollable(boolean monthViewScrollable) {
        this.mMonthViewScrollable = monthViewScrollable;
    }

    void setWeekViewScrollable(boolean weekViewScrollable) {
        this.mWeekViewScrollable = weekViewScrollable;
    }

    void setYearViewScrollable(boolean yearViewScrollable) {
        this.mYearViewScrollable = yearViewScrollable;
    }

    public int getWeekStart() {
        return mWeekStart;
    }

    void setWeekStart(int mWeekStart) {
        this.mWeekStart = mWeekStart;
    }

    void setDefaultCalendarSelectDay(int defaultCalendarSelect) {
        this.mDefaultCalendarSelectDay = defaultCalendarSelect;
    }

    int getDefaultCalendarSelectDay() {
        return mDefaultCalendarSelectDay;
    }

    int getWeekTextSize() {
        return mWeekTextSize;
    }

    /**
     * 选择模式
     * @return 选择模式
     */
    int getSelectMode() {
        return mSelectMode;
    }

    /**
     * 设置选择模式
     * @param mSelectMode mSelectMode
     */
    void setSelectMode(int mSelectMode) {
        this.mSelectMode = mSelectMode;
    }

    int getMinSelectRange() {
        return mMinSelectRange;
    }

    int getMaxSelectRange() {
        return mMaxSelectRange;
    }

    int getMaxMultiSelectSize() {
        return mMaxMultiSelectSize;
    }

    void setMaxMultiSelectSize(int maxMultiSelectSize) {
        this.mMaxMultiSelectSize = maxMultiSelectSize;
    }

    void setSelectRange(int minRange, int maxRange) {
        if (minRange > maxRange && maxRange > 0) {
            mMaxSelectRange = minRange;
            mMinSelectRange = minRange;
            return;
        }
        if (minRange <= 0) {
            mMinSelectRange = -1;
        } else {
            mMinSelectRange = minRange;
        }
        if (maxRange <= 0) {
            mMaxSelectRange = -1;
        } else {
            mMaxSelectRange = maxRange;
        }
    }

    CalendarDay getCurrentDay() {
        return mCurrentDate;
    }

    void updateCurrentDay() {
        Date d = new Date();
        mCurrentDate.setYear(CalendarUtil.getDate("yyyy", d));
        mCurrentDate.setMonth(CalendarUtil.getDate("MM", d));
        mCurrentDate.setDay(CalendarUtil.getDate("dd", d));
        LunarCalendar.setupLunarCalendar(mCurrentDate);
    }

    @SuppressWarnings("unused")
    int getCalendarPadding() {
        return mCalendarPadding;
    }

    void setCalendarPadding(int mCalendarPadding) {
        this.mCalendarPadding = mCalendarPadding;
        mCalendarPaddingLeft = mCalendarPadding;
        mCalendarPaddingRight = mCalendarPadding;
    }

    int getCalendarPaddingLeft() {
        if (showWeekNum) {
            return mCalendarPaddingLeft + showWeekPaddingLeft;
        }
        return mCalendarPaddingLeft;
    }

    void setCalendarPaddingLeft(int mCalendarPaddingLeft) {
        this.mCalendarPaddingLeft = mCalendarPaddingLeft;
    }


    int getCalendarPaddingRight() {
        return mCalendarPaddingRight;
    }

    void setCalendarPaddingRight(int mCalendarPaddingRight) {
        this.mCalendarPaddingRight = mCalendarPaddingRight;
    }

    void setCalendarPaddingTop(int calendarPaddingTop) {
        mCalendarPaddingTop = calendarPaddingTop;
    }

    public int getCalendarPaddingTop() {
        if (showWeekNum) {
            return mCalendarPaddingTop + weekLinePaddingTop;
        }
        return mCalendarPaddingTop;
    }

    void setPreventLongPressedSelected(boolean preventLongPressedSelected) {
        this.preventLongPressedSelected = preventLongPressedSelected;
    }

    void setMonthViewClass(Class<?> monthViewClass) {
        this.mMonthViewClass = monthViewClass;
    }

    void setWeekBarClass(Class<?> weekBarClass) {
        this.mWeekBarClass = weekBarClass;
    }

    void setWeekViewClass(Class<?> weekViewClass) {
        this.mWeekViewClass = weekViewClass;
    }

    void setCalendarDrawerClass(Class<? extends BaseCalendarDrawer> calendarDrawerClass) {
        this.mCalendarDrawer = calendarDrawerClass;
    }

    boolean isPreventLongPressedSelected() {
        return preventLongPressedSelected;
    }

    void clearSelectedScheme() {
        mSelectedCalendar.clearScheme();
    }

    int getMinYearDay() {
        return mMinYearDay;
    }

    int getMaxYearDay() {
        return mMaxYearDay;
    }

    boolean isFullScreenCalendar() {
        return isFullScreenCalendar;
    }

    void updateSelectCalendarScheme() {
        if (mSchemeDatesMap != null && !mSchemeDatesMap.isEmpty()) {
            String key = mSelectedCalendar.toString();
            if (mSchemeDatesMap.containsKey(key)) {
                CalendarDay d = mSchemeDatesMap.get(key);
                mSelectedCalendar.mergeScheme(d, getSchemeText());
            }
        } else {
            clearSelectedScheme();
        }
    }

    void updateCalendarScheme(CalendarDay targetCalendar) {
        if (targetCalendar == null) {
            return;
        }
        if (mSchemeDatesMap == null || mSchemeDatesMap.isEmpty()) {
            return;
        }
        String key = targetCalendar.toString();
        if (mSchemeDatesMap.containsKey(key)) {
            CalendarDay d = mSchemeDatesMap.get(key);
            targetCalendar.mergeScheme(d, getSchemeText());
        }
    }

    CalendarDay createCurrentDate() {
        CalendarDay calendar = CalendarDay.obtain();
        calendar.setYear(mCurrentDate.getYear());
        calendar.setWeek(mCurrentDate.getWeek());
        calendar.setMonth(mCurrentDate.getMonth());
        calendar.setDay(mCurrentDate.getDay());
        calendar.setCurrentDay(true);
        LunarCalendar.setupLunarCalendar(calendar);
        return calendar;
    }

    CalendarDay getMinRangeCalendar() {
        CalendarDay calendar = CalendarDay.obtain();
        calendar.setYear(mMinYear);
        calendar.setMonth(mMinYearMonth);
        calendar.setDay(mMinYearDay);
        calendar.setCurrentDay(calendar.equals(mCurrentDate));
        LunarCalendar.setupLunarCalendar(calendar);
        return calendar;
    }

    @SuppressWarnings("unused")
    CalendarDay getMaxRangeCalendar() {
        CalendarDay calendar = CalendarDay.obtain();
        calendar.setYear(mMaxYear);
        calendar.setMonth(mMaxYearMonth);
        calendar.setDay(mMaxYearDay);
        calendar.setCurrentDay(calendar.equals(mCurrentDate));
        LunarCalendar.setupLunarCalendar(calendar);
        return calendar;
    }

    /**
     * 添加事件标记，来自Map
     */
    void addSchemesFromMap(List<CalendarDay> mItems) {
        if (mSchemeDatesMap == null || mSchemeDatesMap.isEmpty()) {
            return;
        }
        for (CalendarDay a : mItems) {
            if (mSchemeDatesMap.containsKey(a.toString())) {
                CalendarDay d = mSchemeDatesMap.get(a.toString());
                if (d == null) {
                    continue;
                }
                a.setScheme(TextUtils.isEmpty(d.getScheme()) ? getSchemeText() : d.getScheme());
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

    /**
     * 添加数据
     * @param mSchemeDates mSchemeDates
     */
    void addSchemes(Map<String, CalendarDay> mSchemeDates) {
        if (mSchemeDates == null || mSchemeDates.isEmpty()) {
            return;
        }
        if (this.mSchemeDatesMap == null) {
            this.mSchemeDatesMap = new HashMap<>();
        }
        for (String key : mSchemeDates.keySet()) {
            this.mSchemeDatesMap.remove(key);
            CalendarDay calendar = mSchemeDates.get(key);
            if (calendar == null) {
                continue;
            }
            this.mSchemeDatesMap.put(key, calendar);
        }
    }

    /**
     * 清楚选择
     */
    void clearSelectRange() {
        mSelectedStartRangeCalendar = null;
        mSelectedEndRangeCalendar = null;
    }

    /**
     * 获得选中范围
     * @return 选中范围
     */
    List<CalendarDay> getSelectCalendarRange() {
        if (mSelectMode != SELECT_MODE_RANGE) {
            return null;
        }
        List<CalendarDay> calendars = new ArrayList<>();
        if (mSelectedStartRangeCalendar == null ||
                mSelectedEndRangeCalendar == null) {
            return calendars;
        }
        final long ONE_DAY = 1000 * 3600 * 24;
        java.util.Calendar date = java.util.Calendar.getInstance();

        date.set(mSelectedStartRangeCalendar.getYear(),
                mSelectedStartRangeCalendar.getMonth() - 1,
                mSelectedStartRangeCalendar.getDay());//

        long startTimeMills = date.getTimeInMillis();//获得起始时间戳

        date.set(mSelectedEndRangeCalendar.getYear(),
                mSelectedEndRangeCalendar.getMonth() - 1,
                mSelectedEndRangeCalendar.getDay());//
        long endTimeMills = date.getTimeInMillis();
        for (long start = startTimeMills; start <= endTimeMills; start += ONE_DAY) {
            date.setTimeInMillis(start);
            CalendarDay calendar = CalendarDay.obtain();
            calendar.setYear(date.get(java.util.Calendar.YEAR));
            calendar.setMonth(date.get(java.util.Calendar.MONTH) + 1);
            calendar.setDay(date.get(java.util.Calendar.DAY_OF_MONTH));
            LunarCalendar.setupLunarCalendar(calendar);
            updateCalendarScheme(calendar);
            if (mCalendarInterceptListener != null &&
                    mCalendarInterceptListener.onCalendarIntercept(calendar)) {
                continue;
            }

            calendars.add(calendar);
        }
        addSchemesFromMap(calendars);
        return calendars;
    }

    void setMonthViewExpandHeight(int monthViewExpandHeight) {
        this.monthViewExpandHeight = monthViewExpandHeight;
    }

   public int getMonthViewExpandHeight() {
        return monthViewExpandHeight;
    }

    public float getMonthViewExpandPercent() {
        return monthViewExpandPercent;
    }

    void onMonthViewExpandOrFold(float distanceY, int monthViewOriginHeight) {
        int currentOffset = (int) (monthViewHeightOffset + distanceY);
        if (currentOffset > monthViewExpandHeight) {
            currentOffset = monthViewExpandHeight;
        }
        setMontViewOffset(currentOffset, monthViewOriginHeight);
    }

    void setMontViewOffset(int offset, int monthViewInitialHeight) {
        monthViewHeightOffset = offset;
        calculateExpandPercent(monthViewInitialHeight);
    }

    /**
     * 切换文字画笔
     * @param paintType 画笔类型
     * @return 画笔类型对应的画笔
     */
    public Paint switchTextPaint(@CalendarPaintStyle int paintType) {
        switch (paintType) {
            case PAINT_CURRENT_MONTH_DATE:
                setDatePaint();
                textPaint.setColor(getCurrentMonthTextColor());
                break;
            case PAINT_CURRENT_SELECT_DATE:
                setDatePaint();
                textPaint.setColor(getSelectedTextColor());
                break;
            case PAINT_CURRENT_DAY:
                setDatePaint();
                textPaint.setColor(getCurDayTextColor());
                break;
            case PAINT_OTHER_MONTH_DATE:
                setDatePaint();
                textPaint.setColor(getOtherMonthTextColor());
                break;
            case PAINT_CURRENT_DAY_LUNAR_DATE:
                textPaint.setColor(getCurDayLunarTextColor());
                setLunarDatePaint();
                break;
            case PAINT_CURRENT_MONTH_LUNAR_DATE:
                textPaint.setColor(getCurrentMonthLunarTextColor());
                setLunarDatePaint();
                break;
            case PAINT_OTHER_MONTH_LUNAR_DATE:
                textPaint.setColor(getOtherMonthLunarTextColor());
                setLunarDatePaint();
                break;
            case PAINT_SELECT_LUNAR_DATE:
                setLunarDatePaint();
                textPaint.setColor(getSelectedLunarTextColor());
                break;
            case PAINT_WEEKEND_DATE:
                setDatePaint();
                textPaint.setColor(weekendTextColor);
                break;
            case PAINT_WEEKEND_LUNAR_DATE:
                setLunarDatePaint();
                textPaint.setColor(weekendTextColor);
                break;
        }
        return textPaint;
    }

    private void setLunarDatePaint() {
        textPaint.setTextSize(getLunarTextSize());
        textPaint.setFakeBoldText(false);
        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    private void setDatePaint() {
        textPaint.setTextSize(getDayTextSize());
        textPaint.setFakeBoldText(true);
        textPaint.setTextAlign(Paint.Align.CENTER);
    }


    private void calculateExpandPercent(int monthViewInitialHeight) {
        int maxExpandHeight = monthViewExpandHeight - monthViewInitialHeight;
        if (maxExpandHeight > 0) {
            monthViewExpandPercent = (float) monthViewHeightOffset / maxExpandHeight;
            if (monthViewExpandPercent > 1) {
                monthViewExpandPercent = 1;
            }
        } else {
            monthViewExpandPercent = 0;
        }
        // Log.d(TAG, "monthViewExpandPercent:" + monthViewExpandPercent);
    }

    public TextPaint getTextPaint() {
        return textPaint;
    }

    public void updateMonthViewOffset(int monthViewOriginHeight) {
        monthViewHeightOffset = (int) ((monthViewExpandHeight - monthViewOriginHeight) * monthViewExpandPercent);
    }

    @IntDef({PAINT_CURRENT_MONTH_DATE, PAINT_CURRENT_SELECT_DATE,
            PAINT_CURRENT_DAY, PAINT_OTHER_MONTH_DATE,
            PAINT_CURRENT_DAY_LUNAR_DATE, PAINT_CURRENT_MONTH_LUNAR_DATE,
            PAINT_OTHER_MONTH_LUNAR_DATE, PAINT_SELECT_LUNAR_DATE,
            PAINT_WEEKEND_DATE, PAINT_WEEKEND_LUNAR_DATE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface CalendarPaintStyle {

    }

    public PathInterpolator getPathInterpolator() {
        if (pathInterpolator == null) {
            pathInterpolator = new PathInterpolator(0.2f, 0f, 0.2f, 1f);
        }
        return pathInterpolator;
    }
}
