
package com.zhangpan.site.calendarview;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Constructor;

final class YearViewAdapter extends BaseRecyclerAdapter<Month> {
    private CalendarViewDelegate mDelegate;
    private int mItemWidth, mItemHeight;

    YearViewAdapter(Context context) {
        super(context);
    }

    void setup(CalendarViewDelegate delegate) {
        this.mDelegate = delegate;
    }


    void setYearViewSize(int width, int height) {
        this.mItemWidth = width;
        this.mItemHeight = height;
    }

    @Override
    RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        BaseYearView yearView;
        if (TextUtils.isEmpty(mDelegate.getYearViewClassPath())) {
            yearView = new DefaultYearView(mContext);
        } else {
            try {
                Constructor<?> constructor = mDelegate.getYearViewClass().getConstructor(Context.class);
                yearView = (BaseYearView) constructor.newInstance(mContext);
            } catch (Exception e) {
                Log.e(Constants.TAG, "error:" + e.getMessage());
                yearView = new DefaultYearView(mContext);
            }
        }
        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.MATCH_PARENT);
        yearView.setLayoutParams(params);
        return new YearViewHolder(yearView, mDelegate);
    }

    @Override
    void onBindViewHolder(RecyclerView.ViewHolder holder, Month item, int position) {
        YearViewHolder h = (YearViewHolder) holder;
        BaseYearView view = h.mYearView;
        view.init(item.getYear(), item.getMonth());
        view.measureSize(mItemWidth, mItemHeight);
    }

    private static class YearViewHolder extends RecyclerView.ViewHolder {
        BaseYearView mYearView;

        YearViewHolder(View itemView, CalendarViewDelegate delegate) {
            super(itemView);
            mYearView = (BaseYearView) itemView;
            mYearView.setup(delegate);
        }
    }
}
