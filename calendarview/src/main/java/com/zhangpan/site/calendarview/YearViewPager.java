
package com.zhangpan.site.calendarview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;


/**
 * 年份+月份选择布局
 * ViewPager + RecyclerView
 */
public final class YearViewPager extends ViewPager {
    private int mYearCount;
    private boolean isUpdateYearView;
    private CalendarViewDelegate mDelegate;
    private YearRecyclerView.OnMonthSelectedListener mListener;
    private OnYearViewChangeListener yearViewChangeListener;

    public YearViewPager(Context context) {
        this(context, null);
    }

    public YearViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    void setup(CalendarViewDelegate delegate) {
        this.mDelegate = delegate;
        this.mYearCount = mDelegate.getMaxYear() - mDelegate.getMinYear() + 1;
        setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return mYearCount;
            }

            @Override
            public int getItemPosition(@NonNull Object object) {
                return isUpdateYearView ? POSITION_NONE : super.getItemPosition(object);
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
                return view == object;
            }

            @NonNull
            @Override
            public Object instantiateItem(@NonNull ViewGroup container, int position) {
                YearRecyclerView view = new YearRecyclerView(getContext());
                container.addView(view);
                view.setup(mDelegate);
                view.setOnMonthSelectedListener(mListener);
                int year = position + mDelegate.getMinYear();
                view.init(year);
                view.setTag(year);
                return view;
            }

            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                container.removeView((View) object);
            }
        });
        setCurrentItem(mDelegate.getCurrentDay().getYear() - mDelegate.getMinYear());

        addOnPageChangeListener(new OnPageChangeAbstractListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (yearViewChangeListener != null) {
                    yearViewChangeListener.onYearViewChanged(position + mDelegate.getMinYear());
                }
            }
        });
    }

    @Override
    public void setCurrentItem(int item) {
        setCurrentItem(item, false);
    }

    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        if (Math.abs(getCurrentItem() - item) > 1) {
            super.setCurrentItem(item, false);
        } else {
            super.setCurrentItem(item, false);
        }
    }

    public void setOnYearViewChangeListener(OnYearViewChangeListener yearViewChangeListener) {
        this.yearViewChangeListener = yearViewChangeListener;
    }

    /**
     * 通知刷新
     */
    void notifyDataSetChanged() {
        this.mYearCount = mDelegate.getMaxYear() - mDelegate.getMinYear() + 1;
        if (getAdapter() != null) {
            getAdapter().notifyDataSetChanged();
        }
    }

    /**
     * 滚动到某年
     * @param year year
     * @param smoothScroll smoothScroll
     */
    void scrollToYear(int year, boolean smoothScroll) {
        setCurrentItem(year - mDelegate.getMinYear(), smoothScroll);
    }

    /**
     * 更新日期范围
     */
    void updateRange() {
        isUpdateYearView = true;
        notifyDataSetChanged();
        isUpdateYearView = false;
    }

    /**
     * 更新界面
     */
    void update() {
        for (int i = 0; i < getChildCount(); i++) {
            YearRecyclerView view = (YearRecyclerView) getChildAt(i);
            view.notifyAdapterDataSetChanged();
        }
    }


    /**
     * 更新周起始
     */
    void updateWeekStart() {
        for (int i = 0; i < getChildCount(); i++) {
            YearRecyclerView view = (YearRecyclerView) getChildAt(i);
            view.updateWeekStart();
            view.notifyAdapterDataSetChanged();
        }
    }

    /**
     * 更新字体颜色大小
     */
    void updateStyle() {
        for (int i = 0; i < getChildCount(); i++) {
            YearRecyclerView view = (YearRecyclerView) getChildAt(i);
            view.updateStyle();
        }
    }

    void setOnMonthSelectedListener(YearRecyclerView.OnMonthSelectedListener listener) {
        this.mListener = listener;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //heightMeasureSpec = MeasureSpec.makeMeasureSpec(getHeight(getContext(), this), MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return mDelegate.isYearViewScrollable() && super.onTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mDelegate.isYearViewScrollable() && super.onInterceptTouchEvent(ev);
    }

    public interface OnYearViewChangeListener {
        void onYearViewChanged(int year);
    }
}
