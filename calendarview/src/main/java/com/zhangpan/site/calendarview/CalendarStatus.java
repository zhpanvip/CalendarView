package com.zhangpan.site.calendarview;



import static com.zhangpan.site.calendarview.CalendarLayout.STATUS_MONTH_VIEW_EXPANDED;
import static com.zhangpan.site.calendarview.CalendarLayout.STATUS_MONTH_VIEW_FOLD;
import static com.zhangpan.site.calendarview.CalendarLayout.STATUS_WEEK_VIEW;

import androidx.annotation.IntDef;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@IntDef({STATUS_WEEK_VIEW, STATUS_MONTH_VIEW_FOLD, STATUS_MONTH_VIEW_EXPANDED})
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.PARAMETER, ElementType.FIELD, ElementType.METHOD})
public @interface CalendarStatus {}

