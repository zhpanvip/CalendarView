
package com.zhangpan.site.calendarview;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 日历对象
 */
@SuppressWarnings("unused")
public final class CalendarDay implements Serializable, Comparable<CalendarDay> {
    private static final long serialVersionUID = 141315161718191143L;

    private CalendarDay() {
    }

    /**
     * 年
     */
    private int year;

    /**
     * 月1-12
     */
    private int month;

    /**
     * 如果是闰月，则返回闰月
     */
    private int leapMonth;

    /**
     * 日1-31
     */
    private int day;

    /**
     * 是否是闰年
     */
    private boolean isLeapYear;

    /**
     * 是否是本月,这里对应的是月视图的本月，而非当前月份，请注意
     */
    private boolean isCurrentMonth;

    /**
     * 是否是今天
     */
    private boolean isCurrentDay;

    /**
     * 农历日期
     */
    private String lunar;


    /**
     * 24节气
     */
    private String solarTerm;


    /**
     * 公历节日
     */
    private String gregorianFestival;

    private String specialFestival;

    /**
     * 传统农历节日
     */
    private String traditionFestival;

    /**
     * 后台配置的节日
     */
    private String serverFestival;

    /**
     * 计划，可以用来标记当天是否有任务,这里是默认的，如果使用多标记，请使用下面API
     */
    private String scheme;

    /**
     * 各种自定义标记颜色、没有则选择默认颜色，如果使用多标记，请使用下面API
     */
    private int schemeColor;


    /**
     * 日历事件
     */
    private List<CalendarEvent> calendarEvents = new ArrayList<>();

    /**
     * 是否是周末
     */
    private boolean isWeekend;

    /**
     * 星期,0-6 对应周日到周一
     */
    private int week;

    /**
     * 获取完整的农历日期
     */
    private CalendarDay lunarCalendar;


    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public boolean isCurrentMonth() {
        return isCurrentMonth;
    }

    public void setServerFestival(String serverFestival) {
        this.serverFestival = serverFestival;
    }

    public String getServerFestival() {
        return serverFestival;
    }

    public String getSpecialFestival() {
        return specialFestival;
    }

    public void setSpecialFestival(String specialFestival) {
        this.specialFestival = specialFestival;
    }

    public void setCurrentMonth(boolean currentMonth) {
        this.isCurrentMonth = currentMonth;
    }

    public boolean isCurrentWeek() {
        return Calendar.getInstance().get(Calendar.WEEK_OF_YEAR) == getWeekNum();
    }

    public boolean isCurrentDay() {
        return isCurrentDay;
    }

    public void setCurrentDay(boolean currentDay) {
        isCurrentDay = currentDay;
    }


    public String getLunar() {
        return lunar;
    }

    public void setLunar(String lunar) {
        this.lunar = lunar;
    }


    public String getScheme() {
        return scheme;
    }


    public void setScheme(String scheme) {
        this.scheme = scheme;
    }


    public int getSchemeColor() {
        return schemeColor;
    }

    public void setSchemeColor(int schemeColor) {
        this.schemeColor = schemeColor;
    }

    /**
     * 顺序：农历传统节日 > 公历（元旦、情人节、妇女节等） > 特殊节日（父亲节、母亲节、感恩节等）
     * > 节气 > 后台配置的节日 > 农历日期
     * @return 日历农历位置显示的文案
     */
    public String getLunarText() {
        if (!TextUtils.isEmpty(traditionFestival)) {
            return traditionFestival;
        } else if (!TextUtils.isEmpty(gregorianFestival)) {
            return gregorianFestival;
        } else if (!TextUtils.isEmpty(specialFestival)) {
            return specialFestival;
        } else if (!TextUtils.isEmpty(solarTerm)) {
            return solarTerm;
        } else if (!TextUtils.isEmpty(serverFestival)) {
            return serverFestival;
        }
        return lunar;
    }


    public List<CalendarEvent> getEvents() {
        return calendarEvents;
    }

    public void setEvents(List<CalendarEvent> calendarEvents) {
        this.calendarEvents = calendarEvents;
    }


    public void addEvent(CalendarEvent calendarEvent) {
        if (calendarEvents == null) {
            calendarEvents = new ArrayList<>();
        }
        calendarEvents.add(calendarEvent);
    }

    public void addEvent(int schemeColor, String scheme) {
        if (calendarEvents == null) {
            calendarEvents = new ArrayList<>();
        }
        calendarEvents.add(new CalendarEvent(schemeColor, scheme));
    }

    public void addEvent(int type, int schemeColor, String scheme) {
        if (calendarEvents == null) {
            calendarEvents = new ArrayList<>();
        }
        calendarEvents.add(new CalendarEvent(type, schemeColor, scheme));
    }

    public void addEvent(int type, int schemeColor, String scheme, String other) {
        if (calendarEvents == null) {
            calendarEvents = new ArrayList<>();
        }
        calendarEvents.add(new CalendarEvent(type, schemeColor, scheme, other));
    }

    public void addEvent(int schemeColor, String scheme, String other) {
        if (calendarEvents == null) {
            calendarEvents = new ArrayList<>();
        }
        calendarEvents.add(new CalendarEvent(schemeColor, scheme, other));
    }

    public boolean isWeekend() {
        return isWeekend;
    }

    public void setWeekend(boolean weekend) {
        isWeekend = weekend;
    }

    public int getWeek() {
        return week;
    }

    public int getWeekNum() {
        Calendar instance = Calendar.getInstance();
        instance.set(year, month - 1, day);
        return instance.get(Calendar.WEEK_OF_YEAR);
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public CalendarDay getLunarCalendar() {
        return lunarCalendar;
    }

    public void setLunarCalendar(CalendarDay lunarCalendar) {
        this.lunarCalendar = lunarCalendar;
    }

    public String getSolarTerm() {
        return solarTerm;
    }

    public void setSolarTerm(String solarTerm) {
        this.solarTerm = solarTerm;
    }

    public String getGregorianFestival() {
        return gregorianFestival;
    }

    public void setGregorianFestival(String gregorianFestival) {
        this.gregorianFestival = gregorianFestival;
    }


    public int getLeapMonth() {
        return leapMonth;
    }

    public void setLeapMonth(int leapMonth) {
        this.leapMonth = leapMonth;
    }

    public boolean isLeapYear() {
        return isLeapYear;
    }

    public void setLeapYear(boolean leapYear) {
        isLeapYear = leapYear;
    }

    public String getTraditionFestival() {
        return traditionFestival;
    }

    public void setTraditionFestival(String traditionFestival) {
        this.traditionFestival = traditionFestival;
    }


    public static CalendarDay obtain() {
        return new CalendarDay();
    }

    public boolean hasScheme() {
        if (calendarEvents != null && !calendarEvents.isEmpty()) {
            return true;
        }
        return !TextUtils.isEmpty(scheme);
    }

    /**
     * 是否是相同月份
     * @param calendar 日期
     * @return 是否是相同月份
     */
    public boolean isSameMonth(CalendarDay calendar) {
        return year == calendar.getYear() && month == calendar.getMonth();
    }

    /**
     * 比较日期
     * @param calendar 日期
     * @return <0 0 >0
     */
    public int compareTo(CalendarDay calendar) {
        if (calendar == null) {
            return 1;
        }
        return toString().compareTo(calendar.toString());
    }

    /**
     * 运算差距多少天
     * @param calendar calendar
     * @return 运算差距多少天
     */
    public int differ(CalendarDay calendar) {
        return CalendarUtil.differ(this, calendar);
    }

    /**
     * 日期是否可用
     * @return 日期是否可用
     */
    public boolean isAvailable() {
        return year > 0 & month > 0 & day > 0 & day <= 31 & month <= 12 & year >= 1900 & year <= 2099;
    }

    /**
     * 获取当前日历对应时间戳
     * @return getTimeInMillis
     */
    public long getTimeInMillis() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        return calendar.getTimeInMillis();
    }

    public Calendar toCalendar() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day);
        return calendar;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof CalendarDay) {
            if (((CalendarDay) o).getYear() == year && ((CalendarDay) o).getMonth() == month && ((CalendarDay) o).getDay() == day)
                return true;
        }
        return super.equals(o);
    }

    @NonNull
    @Override
    public String toString() {
        return year + "" + (month < 10 ? "0" + month : month) + (day < 10 ? "0" + day : day);
    }

    void mergeScheme(CalendarDay calendar, String defaultScheme) {
        if (calendar == null)
            return;
        setScheme(TextUtils.isEmpty(calendar.getScheme()) ?
                defaultScheme : calendar.getScheme());
        setSchemeColor(calendar.getSchemeColor());
        setEvents(calendar.getEvents());
        setServerFestival(calendar.getServerFestival());
    }

    void clearScheme() {
        setScheme("");
        setSchemeColor(0);
        setEvents(null);
    }

    /**
     * 事件标记服务，现在多类型的事务标记建议使用这个
     */
    public final static class CalendarEvent implements Serializable {

        private long eventId;
        private int eventType;
        private int eventColor;
        private String eventName = "";
        private String other;
        private Object obj;

        public CalendarEvent() {
        }

        public CalendarEvent(int eventType, int eventColor, String eventName, String other) {
            this.eventType = eventType;
            this.eventColor = eventColor;
            this.eventName = eventName;
            this.other = other;
        }

        public CalendarEvent(int eventType, int eventColor, String eventName) {
            this.eventType = eventType;
            this.eventColor = eventColor;
            this.eventName = eventName;
        }

        public CalendarEvent(int eventColor, String eventName) {
            this.eventColor = eventColor;
            this.eventName = eventName;
        }

        public CalendarEvent(int eventColor, String eventName, String other) {
            this.eventColor = eventColor;
            this.eventName = eventName;
            this.other = other;
        }

        public long getEventId() {
            return eventId;
        }

        public void setEventId(long eventId) {
            this.eventId = eventId;
        }

        public int getEventColor() {
            return eventColor;
        }

        public void setEventColor(int eventColor) {
            this.eventColor = eventColor;
        }

        public String getEventName() {
            if (eventName == null) {
                return "";
            }
            return eventName;
        }

        public void setEventName(String eventName) {
            this.eventName = eventName;
        }

        public String getOther() {
            return other;
        }

        public void setOther(String other) {
            this.other = other;
        }

        public int getEventType() {
            return eventType;
        }

        public void setEventType(int eventType) {
            this.eventType = eventType;
        }

        public Object getObj() {
            return obj;
        }

        public void setObj(Object obj) {
            this.obj = obj;
        }
    }
}
