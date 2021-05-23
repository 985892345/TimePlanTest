package com.example.timeplantest.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.timeplantest.R
import com.example.timeplantest.bean.ExpandBean
import com.example.timeplantest.bean.TaskBean
import com.ndhzs.timeselectview.bean.TSViewTaskBean
import java.util.*

/**
 *@author 985892345
 *@email 2767465918@qq.com
 *@data 2021/5/4
 *@description
 */
class ExpandLvAdapter(data: List<ExpandBean>,
                      onFinished: ((taskBean: TaskBean, tSViewBean: TSViewTaskBean, isFinish: Boolean) -> Unit),
                      onDeleted: ((taskBean: TaskBean, tSViewBean: TSViewTaskBean) -> Unit),
                      onClickItem: ((taskBean: TaskBean, tSViewBean: TSViewTaskBean) -> Unit)) : BaseExpandableListAdapter() {

    private val mData = data
    private val mOnFinished = onFinished
    private val mOnDeleted = onDeleted
    private val mOnClickItem = onClickItem

    override fun getGroupCount(): Int {
        return mData.size
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return mData[groupPosition].children.size
    }

    override fun getGroup(groupPosition: Int): Any {
        return mData[groupPosition].groupName
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        return mData[groupPosition].children[childPosition]
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return (groupPosition * 100 + childPosition).toLong()
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup): View {
        val holder: GroupViewHolder
        val newConvertView: View
        if (convertView == null) {
            holder = GroupViewHolder()
            newConvertView = LayoutInflater.from(parent.context).inflate(R.layout.fg1_expand_group, parent, false)
            holder.mImgArrow = newConvertView.findViewById(R.id.fg1_expand_group_img)
            holder.mTvTitle = newConvertView.findViewById(R.id.fg1_expand_group_text)
            newConvertView.tag = holder
        }else {
            holder = convertView.tag as GroupViewHolder
            newConvertView = convertView
        }

        //判断是否已经打开列表
        if (isExpanded) {
            holder.mImgArrow!!.setBackgroundResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
            holder.mTvTitle!!.setTextColor(0xFF000000.toInt())
        }else {
            holder.mImgArrow!!.setBackgroundResource(R.drawable.ic_baseline_keyboard_arrow_right_24)
            holder.mTvTitle!!.setTextColor(0xFF6B6B6B.toInt())
        }
        holder.mTvTitle!!.text = mData[groupPosition].groupName
        return newConvertView
    }


    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup): View {
        val childViewHolder: ChildViewHolder
        val newConvertView: View
        if (convertView == null) {
            newConvertView = LayoutInflater.from(parent.context).inflate(R.layout.fg1_expand_child, parent, false)
            childViewHolder = ChildViewHolder()
            childViewHolder.mCbIsFinish = newConvertView.findViewById(R.id.fg1_expand_child_checkbox)
            childViewHolder.mTvName = newConvertView.findViewById(R.id.fg1_expand_child_name)
            childViewHolder.mTvTime = newConvertView.findViewById(R.id.fg1_expand_child_time)
            childViewHolder.mBtnDelete = newConvertView.findViewById(R.id.fg1_expand_child_btn_delete)
            childViewHolder.mBtnItem = newConvertView.findViewById(R.id.fg1_expand_child_btn_item)
            newConvertView.tag = childViewHolder
        }else {
            childViewHolder = convertView.tag as ChildViewHolder
            newConvertView = convertView
        }
        val cbIsFinish = childViewHolder.mCbIsFinish!!
        val tvName = childViewHolder.mTvName!!
        val tvTime = childViewHolder.mTvTime!!
        val btnDeleted = childViewHolder.mBtnDelete!!
        val btnItem = childViewHolder.mBtnItem!!

        if (mData[groupPosition].isFinish.size <= childPosition) {
            mData[groupPosition].isFinish.add(false)
        }
        cbIsFinish.isChecked = mData[groupPosition].isFinish[childPosition]
        tvName.text = mData[groupPosition].children[childPosition]
        tvTime.text = mData[groupPosition].time[childPosition]
        //判断是否已经过时
        val isToTime = isToTime(mData[groupPosition].time[childPosition])
        if (mData[groupPosition].isFinish[childPosition] || isToTime) {
            setTextColor(tvName, tvTime, false)
        }else {
            setTextColor(tvName, tvTime, true)
        }
        cbIsFinish.setOnClickListener {
            val tSViewBean = mData[groupPosition].tSViewBean[childPosition]
            val taskBean = tSViewBean.any1 as TaskBean
            val isFinish = childViewHolder.mCbIsFinish!!.isChecked
            taskBean.isFinish = isFinish
            taskBean.save()
            if (isFinish) {
                setTextColor(tvName, tvTime, false)
            }else if (!isToTime) {
                setTextColor(tvName, tvTime, true)
            }
            mOnFinished.invoke(taskBean, tSViewBean, isFinish)
        }
        btnDeleted.setOnClickListener {
            val tSViewBean = mData[groupPosition].tSViewBean[childPosition]
            val taskBean = tSViewBean.any1 as TaskBean
            taskBean.delete()
            mOnDeleted.invoke(taskBean, tSViewBean)
        }
        btnItem.setOnClickListener {
            val tSViewBean = mData[groupPosition].tSViewBean[childPosition]
            val taskBean = tSViewBean.any1 as TaskBean
            mOnClickItem.invoke(taskBean, tSViewBean)
        }
        return newConvertView
    }

    private fun setTextColor(tvName: TextView, tvTime: TextView, isHighLighted: Boolean) {
        if (isHighLighted) {
            tvName.setTextColor(0xFF575757.toInt())
            tvTime.setTextColor(0xFF575757.toInt())
        }else {
            tvName.setTextColor(0xFFB6B6B6.toInt())
            tvTime.setTextColor(0xFFB6B6B6.toInt())
        }
    }

    private fun isToTime(time: String): Boolean {
        val time1 = time.split("-")
        val endTime = time1[1].split(":")
        val endH = endTime[0].toInt()
        val endM = endTime[1].toInt()
        val calender = Calendar.getInstance()
        val nowH = calender.get(Calendar.HOUR_OF_DAY)
        val nowM = calender.get(Calendar.MINUTE)
        return if (endH < nowH) {
            true
        }else endH == nowH && endM < nowM
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }

    class GroupViewHolder {
        var mImgArrow: ImageView? = null
        var mTvTitle: TextView? = null
    }

    class ChildViewHolder {
        var mCbIsFinish: CheckBox? = null
        var mTvName: TextView? = null
        var mTvTime: TextView? = null
        var mBtnDelete: Button? = null
        var mBtnItem: Button? = null
    }
}