
package com.zhangpan.site.calendarview;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

/**
 * 星期栏，如果你要使用星期栏自定义，切记XML使用 merge，不要使用LinearLayout
 */
public class WeekBar extends LinearLayout {
    private CalendarViewDelegate mDelegate;

    public WeekBar(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.cv_week_bar, this, true);
    }

    /**
     * 传递属性
     * @param delegate delegate
     */
    void setup(CalendarViewDelegate delegate) {
        this.mDelegate = delegate;
        setTextSize(mDelegate.getWeekTextSize());
        // setTextColor(delegate.getWeekTextColor());
        setBackgroundColor(delegate.getWeekBackground());
        setPadding(delegate.getCalendarPaddingLeft(), 0, delegate.getCalendarPaddingRight(), 0);
    }

    /**
     * 设置文本颜色，使用自定义布局需要重写这个方法，避免出问题
     * 如果这里报错了，请确定你自定义XML文件跟布局是不是使用merge，而不是LinearLayout
     * @param color color
     */
    protected void setTextColor(int color) {
        for (int i = 0; i < getChildCount(); i++) {
            ((TextView) getChildAt(i)).setTextColor(color);
        }
    }


    /**
     * 设置文本大小
     * @param size size
     */
    protected void setTextSize(int size) {
        for (int i = 0; i < getChildCount(); i++) {
            ((TextView) getChildAt(i)).setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
        }
    }

    /**
     * 日期选择事件，这里提供这个回调，可以方便定制WeekBar需要
     * @param calendar calendar 选择的日期
     * @param weekStart 周起始
     * @param isClick isClick 点击
     */
    @SuppressWarnings("unused")
    protected void onDateSelected(CalendarDay calendar, int weekStart, boolean isClick) {

    }

    /**
     * 当周起始发生变化，使用自定义布局需要重写这个方法，避免出问题
     * @param weekStart 周起始
     */
    protected void onWeekStartChange(int weekStart) {
        int sat, sun;
        if (weekStart == CalendarViewDelegate.WEEK_START_WITH_SUN) {
            sat = 6; sun = 0;
        } else if (weekStart == CalendarViewDelegate.WEEK_START_WITH_MON) {
            sat = 5; sun = 6;
        } else {
            sat = 0; sun = 1;
        }
        for (int i = 0; i < getChildCount(); i++) {
            TextView textView = (TextView) getChildAt(i);
            textView.setText(getWeekString(i, weekStart));
            if (i == sat || i == sun) {
                textView.setTextColor(ContextCompat.getColor(getContext(), R.color.weekend_text_color));
            } else {
                textView.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
            }
        }
    }


    /**
     * 通过View的位置和周起始获取星期的对应坐标
     * @param calendar calendar
     * @param weekStart weekStart
     * @return 通过View的位置和周起始获取星期的对应坐标
     */
    @SuppressWarnings("unused")
    protected int getViewIndexByCalendar(CalendarDay calendar, int weekStart) {
        int week = calendar.getWeek() + 1;
        if (weekStart == CalendarViewDelegate.WEEK_START_WITH_SUN) {
            return week - 1;
        }
        if (weekStart == CalendarViewDelegate.WEEK_START_WITH_MON) {
            return week == CalendarViewDelegate.WEEK_START_WITH_SUN ? 6 : week - 2;
        }
        return week == CalendarViewDelegate.WEEK_START_WITH_SAT ? 0 : week;
    }

    /**
     * 或者周文本，这个方法仅供父类使用
     * @param index index
     * @param weekStart weekStart
     * @return 或者周文本
     */
    private String getWeekString(int index, int weekStart) {
        String[] weeks = getContext().getResources().getStringArray(R.array.week_string_array);

        if (weekStart == CalendarViewDelegate.WEEK_START_WITH_SUN) {
            return weeks[index];
        }
        if (weekStart == CalendarViewDelegate.WEEK_START_WITH_MON) {
            return weeks[index == 6 ? 0 : index + 1];
        }
        return weeks[index == 0 ? 6 : index - 1];
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mDelegate != null) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(mDelegate.getWeekBarHeight(), MeasureSpec.EXACTLY);
        } else {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(CalendarUtil.dipToPx(getContext(), 40), MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
