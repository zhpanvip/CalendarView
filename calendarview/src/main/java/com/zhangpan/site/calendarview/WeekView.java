
package com.zhangpan.site.calendarview;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;

import androidx.annotation.NonNull;

/**
 * 周视图，因为日历UI采用热插拔实现，所以这里必须继承实现，达到UI一致即可
 */
public abstract class WeekView extends BaseWeekView {

    public WeekView(Context context) {
        super(context);
    }

    /**
     * 绘制日历文本
     * @param canvas canvas
     */
    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        if (mItems.isEmpty())
            return;
        mItemWidth = (getWidth() -
                mDelegate.getCalendarPaddingLeft() -
                mDelegate.getCalendarPaddingRight()) / 7;
        onPreviewDraw(canvas);
        for (int i = 0; i < mItems.size(); i++) {
            int x = i * mItemWidth + mDelegate.getCalendarPaddingLeft();
            int y = mDelegate.getCalendarPaddingTop();
            onLoopStart(x);
            CalendarDay calendar = mItems.get(i);
            if (i == 0) {
                onBeforeDrawWeek(canvas, calendar, 0, getItemHeight());
            }
            boolean isSelected = i == mCurrentItem;
            boolean hasScheme = calendar.hasScheme();
            if (hasScheme) {
                boolean isDrawSelected = false;//是否继续绘制选中的onDrawScheme
                if (isSelected) {
                    isDrawSelected = onDrawSelected(canvas, calendar, x, y, true);
                }
                if (isDrawSelected || !isSelected) {
                    //将画笔设置为标记颜色
                    mSchemePaint.setColor(calendar.getSchemeColor() != 0 ?
                            calendar.getSchemeColor() : mDelegate.getSchemeThemeColor());
                    onDrawScheme(canvas, isSelected, calendar, x, y);
                }
            } else {
                if (isSelected) {
                    onDrawSelected(canvas, calendar, x, y, false);
                }
            }
            onDrawText(canvas, calendar, x, y, hasScheme, isSelected);
        }
        onFinishDraw(canvas, 1, getItemHeight());
    }

    @Override
    public void onClick(View v) {
        if (!isClick) {
            return;
        }
        CalendarDay calendar = getIndex();
        if (calendar == null) {
            return;
        }
        if (onCalendarIntercept(calendar)) {
            mDelegate.mCalendarInterceptListener.onCalendarInterceptClick(calendar, true);
            return;
        }
        if (!isInRange(calendar)) {
            if (mDelegate.mCalendarSelectListener != null) {
                mDelegate.mCalendarSelectListener.onCalendarOutOfRange(calendar);
            }
            return;
        }

        mCurrentItem = mItems.indexOf(calendar);

        if (mDelegate.mInnerListener != null) {
            mDelegate.mInnerListener.onWeekDateSelected(calendar, true);
        }
        if (mParentLayout != null) {
            int i = CalendarUtil.getWeekFromDayInMonth(calendar, mDelegate.getWeekStart());
            mParentLayout.updateSelectWeek(i);
        }

        if (mDelegate.mCalendarSelectListener != null) {
            mDelegate.mCalendarSelectListener.onCalendarSelect(calendar, true);
        }

        invalidate();
    }


    @Override
    public boolean onLongClick(View v) {
        if (mDelegate.mCalendarLongClickListener == null)
            return false;
        if (!isClick) {
            return false;
        }
        CalendarDay calendar = getIndex();
        if (calendar == null) {
            return false;
        }
        if (onCalendarIntercept(calendar)) {
            mDelegate.mCalendarInterceptListener.onCalendarInterceptClick(calendar, true);
            return true;
        }
        boolean isCalendarInRange = isInRange(calendar);

        if (!isCalendarInRange) {
            if (mDelegate.mCalendarLongClickListener != null) {
                mDelegate.mCalendarLongClickListener.onCalendarLongClickOutOfRange(calendar);
            }
            return true;
        }

        if (mDelegate.isPreventLongPressedSelected()) {//如果启用拦截长按事件不选择日期
            if (mDelegate.mCalendarLongClickListener != null) {
                mDelegate.mCalendarLongClickListener.onCalendarLongClick(calendar);
            }
            return true;
        }

        mCurrentItem = mItems.indexOf(calendar);

        mDelegate.mIndexCalendar = mDelegate.mSelectedCalendar;

        if (mDelegate.mInnerListener != null) {
            mDelegate.mInnerListener.onWeekDateSelected(calendar, true);
        }
        if (mParentLayout != null) {
            int i = CalendarUtil.getWeekFromDayInMonth(calendar, mDelegate.getWeekStart());
            mParentLayout.updateSelectWeek(i);
        }

        if (mDelegate.mCalendarSelectListener != null) {
            mDelegate.mCalendarSelectListener.onCalendarSelect(calendar, true);
        }

        if (mDelegate.mCalendarLongClickListener != null) {
            mDelegate.mCalendarLongClickListener.onCalendarLongClick(calendar);
        }

        invalidate();
        return true;
    }
}
