package com.zhangpan.site.calendarview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import androidx.core.content.ContextCompat;

/**
 * @Description:
 * @Author: zhangpan
 * @Date: 2024/9/4 13:46
 */
public class CalendarDrawer extends BaseCalendarDrawer {

    private float mPadding;
    private final RectF rectF = new RectF();
    private int rectRadius;

    protected static float EVENT_CIRCLE_RADIUS;

    protected int mTodayNumberColor;
    protected int mEventCircleColor;

    public CalendarDrawer(Context context) {
        super(context);
    }

    @Override
    public void setDrawData(CalendarViewDelegate delegate, boolean isMonthView, int itemWidth) {
        mDelegate = delegate;
        mItemHeight = delegate.getCalendarItemHeight();
        mItemWidth = itemWidth;
        EVENT_CIRCLE_RADIUS = dpToPx(mContext, 8);
        mPadding = dpToPx(mContext, 2);
        rectRadius = mContext.getResources().getDimensionPixelSize(R.dimen.radius_corner_smooth_mini);
        mEventCircleColor = ContextCompat.getColor(mContext, R.color.event_circle_color);
        mTodayNumberColor = ContextCompat.getColor(mContext, R.color.color_primary_red);
        Paint mEventCirclePaint = new Paint();
        mEventCirclePaint.setAntiAlias(true);
        Paint paint = mDelegate.switchTextPaint(BaseView.PAINT_CURRENT_MONTH_DATE);
        Paint.FontMetrics metrics = paint.getFontMetrics();
        mTextBaseLine = mItemHeight / 2f - metrics.descent + (metrics.bottom - metrics.top) / 2;
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
    @SuppressWarnings("unused")
    @Override
    public boolean onDrawSelected(Canvas canvas, CalendarDay calendarDay, int x, int y, boolean hasScheme) {
        mSelectedPaint.setStyle(Paint.Style.FILL);
        rectF.left = x + mPadding;
        rectF.top = y;
        rectF.right = x + mItemWidth - mPadding;
        rectF.bottom = y + mItemHeight;
        if (calendarDay.isCurrentDay()) {
            mSelectedPaint.setColor(mDelegate.getSelectedThemeColor());
        } else {
            mSelectedPaint.setColor(ContextCompat.getColor(mContext, R.color.color_primary_red));
        }
        canvas.drawRoundRect(rectF, rectRadius, rectRadius, mSelectedPaint);
        return true;
    }

    /**
     * 绘制标记的事件日子
     * @param canvas canvas
     * @param isSelected 是否选中
     * @param calendar 日历calendar
     * @param x 日历Card x起点坐标
     * @param y 日历Card y起点坐标
     */
    @Override
    public void onDrawScheme(Canvas canvas, boolean isSelected, CalendarDay calendar, int x, int y) {

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
    @Override
    public void onDrawText(Canvas canvas, CalendarDay calendar, int x, int y, boolean hasScheme, boolean isSelected) {
        int cx = x + mItemWidth / 2;
        int top = y - mItemHeight / 6;

        boolean isInRange = isInRange(calendar);

        if (isSelected) {
            Paint paint = mDelegate.switchTextPaint(BaseView.PAINT_CURRENT_SELECT_DATE);
            canvas.drawText(String.valueOf(calendar.getDay()), cx, mTextBaseLine + top, paint);
            mDelegate.switchTextPaint(BaseView.PAINT_SELECT_LUNAR_DATE);
            canvas.drawText(calendar.getLunar(), cx, mTextBaseLine + y + mItemHeight / 10f, paint);
        } else {
            Paint paint;
            if (calendar.isCurrentDay()) {
                paint = mDelegate.switchTextPaint(BaseView.PAINT_CURRENT_DAY);
            } else if (calendar.isCurrentMonth() && isInRange) {
                paint = mDelegate.switchTextPaint(BaseView.PAINT_CURRENT_MONTH_DATE);
            } else {
                paint = mDelegate.switchTextPaint(BaseView.PAINT_OTHER_MONTH_DATE);
            }
            canvas.drawText(String.valueOf(calendar.getDay()), cx, mTextBaseLine + top, paint);

            Paint lunarPaint;
            if (calendar.isCurrentDay() && isInRange) {
                lunarPaint = mDelegate.switchTextPaint(BaseView.PAINT_CURRENT_DAY_LUNAR_DATE);
            } else if (calendar.isCurrentMonth()) {
                lunarPaint = mDelegate.switchTextPaint(BaseView.PAINT_CURRENT_MONTH_LUNAR_DATE);
            } else {
                lunarPaint = mDelegate.switchTextPaint(BaseView.PAINT_OTHER_MONTH_LUNAR_DATE);
            }
            canvas.drawText(calendar.getLunar(), cx, mTextBaseLine + y + mItemHeight / 10f, lunarPaint);
        }
    }
}
