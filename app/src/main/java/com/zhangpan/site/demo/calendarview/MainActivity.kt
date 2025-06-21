package com.zhangpan.site.demo.calendarview

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.zhangpan.site.calendarview.CalendarLayout
import com.zhangpan.site.calendarview.CalendarView
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private lateinit var tvDate: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val instance = Calendar.getInstance()
        val calendarLayout = findViewById<CalendarLayout>(R.id.calendar_layout)
        val calendarView = findViewById<CalendarView>(R.id.calendar_view)
        tvDate = findViewById(R.id.tv_date)
        setCalendarTitle(instance.get(Calendar.YEAR), instance.get(Calendar.MONTH) + 1)
        calendarView.registerMonthChangeListener { year, month ->
            setCalendarTitle(year, month)
        }
        tvDate.setOnClickListener {
            calendarLayout.toggleYearView()
        }
    }

    private fun setCalendarTitle(year: Int, month: Int) {
        tvDate.text = getString(R.string.date_title, year, month)
    }
}