
package com.zhangpan.site.calendarview;

import android.content.Context;
import android.graphics.Canvas;

/**
 * 默认年视图
 */

public class DefaultYearView extends YearView {

    private final int mTextPadding;

    public DefaultYearView(Context context) {
        super(context);
        mTextPadding = CalendarUtil.dipToPx(context, 3);
    }

    @Override
    protected void onDrawMonth(Canvas canvas, int year, int month, int x, int y, int width, int height) {

        String text = getContext()
                .getResources()
                .getStringArray(R.array.month_string_array)[month - 1];

        canvas.drawText(text,
                x + mItemWidth / 2f - mTextPadding,
                y + mMonthTextBaseLine,
                mMonthTextPaint);
    }

    @Override
    protected void onDrawWeek(Canvas canvas, int week, int x, int y, int width, int height) {
        String text = getContext().getResources().getStringArray(R.array.year_view_week_string_array)[week];
        canvas.drawText(text,
                x + width / 2f,
                y + mWeekTextBaseLine,
                mWeekTextPaint);
    }


    @Override
    protected boolean onDrawSelected(Canvas canvas, CalendarDay calendar, int x, int y, boolean hasScheme) {
        return false;
    }

    @Override
    protected void onDrawScheme(Canvas canvas, CalendarDay calendar, int x, int y) {

    }

    @Override
    protected void onDrawText(Canvas canvas, CalendarDay calendar, int x, int y, boolean hasScheme, boolean isSelected) {
        float baselineY = mTextBaseLine + y;
        int cx = x + mItemWidth / 2;

        if (isSelected) {
            canvas.drawText(String.valueOf(calendar.getDay()),
                    cx,
                    baselineY,
                    hasScheme ? mSchemeTextPaint : mSelectTextPaint);
        } else if (hasScheme) {
            canvas.drawText(String.valueOf(calendar.getDay()),
                    cx,
                    baselineY,
                    calendar.isCurrentDay() ? mCurDayTextPaint :
                            calendar.isCurrentMonth() ? mSchemeTextPaint : mOtherMonthTextPaint);

        } else {
            canvas.drawText(String.valueOf(calendar.getDay()), cx, baselineY,
                    calendar.isCurrentDay() ? mCurDayTextPaint :
                            calendar.isCurrentMonth() ? mCurMonthTextPaint : mOtherMonthTextPaint);
        }
    }
}
