package com.example.timeplantest.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.timeplantest.R
import com.example.timeplantest.bean.CalendarBean
import com.example.timeplantest.weight.weekview.DayView

/**
 *@author 985892345
 *@email 2767465918@qq.com
 *@data 2021/5/4
 *@description
 */
class WeekVpAdapter(calendars: List<CalendarBean>, currentWeekPage: Int, currentDay: Int) : RecyclerView.Adapter<WeekVpAdapter.WeekViewViewHolder>() {


    fun setOnClickListener(onClick: (position: Int) -> Unit) {
        mOnClickListener = onClick
    }

    fun setWeekPosition(weekPage: Int, dayPosition: Int) {
        val list = listOf(NOTIFY_WEEK_POSITION, dayPosition)
        notifyItemChanged(weekPage, list)
    }

    companion object {
        /**
         * 用于在[onBindViewHolder]更新周数的位置
         */
        const val NOTIFY_WEEK_POSITION = 0
    }

    private val mCalendars = calendars
    private val mCurrentWeekPage = currentWeekPage
    private val mCurrentDay = currentDay
    private var mOnClickListener: ((position: Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeekViewViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.vp_item_week, parent, false)
        return WeekViewViewHolder(view)
    }

    override fun onBindViewHolder(holder: WeekViewViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
        }else {
            payloads.forEach {
                val list = it as List<*>
                when (list[0] as Int) {
                    NOTIFY_WEEK_POSITION -> {
                        holder.mDayView.setMovePosition(list[1] as Int)
                    }
                }
            }
        }
    }

    override fun onBindViewHolder(holder: WeekViewViewHolder, position: Int) {
        holder.mDayView.setDate(mCalendars[position].days.toTypedArray())
        holder.mDayView.setRectDays(mCalendars[position].dayOff.toTypedArray())
        holder.mDayView.setCalender(mCalendars[position].lunarCalendar.toTypedArray())
        if (position != mCurrentWeekPage) {
            holder.mDayView.setCirclePosition(-1)
        }else {
            holder.mDayView.setCirclePosition(mCurrentDay)
        }
        holder.mDayView.setOnWeekClickListener { p ->
            mOnClickListener?.invoke(position * 7 + p)
        }
    }

    override fun getItemCount(): Int {
        return mCalendars.size
    }

    inner class WeekViewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mDayView: DayView = itemView.findViewById(R.id.vp_item_day)
    }
}