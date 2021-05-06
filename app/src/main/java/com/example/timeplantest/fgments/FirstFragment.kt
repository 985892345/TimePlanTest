package com.example.timeplantest.fgments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.timeplantest.R
import com.example.timeplantest.adapter.ExpandLvAdapter
import com.example.timeplantest.bean.ExpandBean
import com.example.timeplantest.bean.TaskBean
import com.example.timeplantest.weight.dialog.AddTaskDialog
import com.example.timeplantest.weight.timeselectview.bean.TSViewDayBean
import com.example.timeplantest.weight.timeselectview.bean.TSViewTaskBean
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet
import kotlin.collections.forEach

/**
 *@author 985892345
 *@email 2767465918@qq.com
 *@data 2021/5/3
 *@description
 */
class FirstFragment(activity: AppCompatActivity,
                    drawerLayout: DrawerLayout,
                    dayBean: TSViewDayBean,
                    onTaskBeanChanged: (isAddedOrDeleted: Boolean?, taskBean: TaskBean, tSViewTaskBean: TSViewTaskBean) -> Unit,
                    onFinished: ((taskBean: TaskBean, tSViewBean: TSViewTaskBean, isFinish: Boolean) -> Unit)) : Fragment() {

    fun notifyExpandListViewDataSetChange() {
        initializeData()
        mExpandAdapter.notifyDataSetChanged()
    }

    private val mActivity = activity
    private val mDrawerLayout = drawerLayout
    private val mDayBean = dayBean
    private val mOnTaskBeanChanged = onTaskBeanChanged
    private val mOnFinished = onFinished
    private lateinit var mRootView: View
    private lateinit var mExpandLv: ExpandableListView
    private lateinit var mExpandAdapter: ExpandLvAdapter
    private lateinit var mFloatButton: FloatingActionButton

    private var mData = ArrayList<ExpandBean>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mRootView = inflater.inflate(R.layout.fragment1_first, container, false)
        initToolbar()
        initExpandLv()
        initFloatButton()
        return mRootView
    }

    private fun initToolbar() {
        val toolbar: Toolbar = mRootView.findViewById(R.id.fg1_first_toolbar)
        //将ToolBar与ActionBar关联
        mActivity.setSupportActionBar(toolbar)
        //另外openDrawerContentDescRes 打开图片   closeDrawerContentDescRes 关闭图片
        val toggle = ActionBarDrawerToggle(
                mActivity, mDrawerLayout, toolbar, 0, 0
        )
        //初始化状态
        mDrawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    private fun initExpandLv() {
        initializeData()
        mExpandLv = mRootView.findViewById(R.id.fg1_expandLv)
        mExpandAdapter = ExpandLvAdapter(mData, onFinished = { taskBean, tSViewTaskBean, isFinish ->
            mOnFinished.invoke(taskBean, tSViewTaskBean, isFinish)
        }, onDeleted = { taskBean, tSViewTaskBean ->
            mOnTaskBeanChanged.invoke(false, taskBean, tSViewTaskBean)
            notifyExpandListViewDataSetChange()
        }, onClickItem = { taskBean, tSViewBean ->
            expandItemClick(taskBean, tSViewBean)
        })
        mExpandLv.setAdapter(mExpandAdapter)
    }

    private fun initializeData() {
        mData.clear()
        val tSViewTaskBeans = mDayBean.tSViewTaskBeans
        val groupNames = HashSet<String>()
        val correctTaskBeans = getCorrectChildren(tSViewTaskBeans)
        val expandBean = ExpandBean("全部任务")
        correctTaskBeans.forEach {
            val taskBean = (it.any1) as TaskBean
            groupNames.add(taskBean.groupName)
            expandBean.children.add(it.name)
            expandBean.time.add(it.startTime + "-" + it.endTime)
            expandBean.tSViewBean.add(it)
            expandBean.isFinish.add(taskBean.isFinish)
        }
        mData.add(expandBean)

        groupNames.forEach { name ->
            if (name.isNotEmpty()) {
                val expandBean2 = ExpandBean(name)
                correctTaskBeans.forEach {
                    val taskBean = it.any1 as TaskBean
                    if (name == taskBean.groupName) {
                        expandBean2.children.add(it.name)
                        expandBean2.time.add(it.startTime + "-" + it.endTime)
                        expandBean2.tSViewBean.add(it)
                        expandBean.isFinish.add(taskBean.isFinish)
                    }
                }
                mData.add(expandBean2)
            }
        }
    }

    private fun getCorrectChildren(taskBeans: MutableList<TSViewTaskBean>): MutableList<TSViewTaskBean> {
        taskBeans.sortWith(Comparator { o1, o2 ->
            val time1 = o1.startTime.split(":")
            val time2 = o2.startTime.split(":")
            return@Comparator (time1[0].toInt() * 60 + time1[1].toInt()) - (time2[0].toInt() * 60 + time2[1].toInt())
        })
        return taskBeans
    }

    private val mIsRepeat = { startTime: List<String>?, hour: Int, minute: Int, tSViewBean: TSViewTaskBean? ->
        var boolean = false
        val allTaskTime = mData[0].time
        var startHour = 0
        var startMinute = 0
        var skipPosition = -1
        if (tSViewBean != null) {
            for (i in 0 until mData[0].tSViewBean.size) {
                if (mData[0].tSViewBean[i] == tSViewBean) {
                    skipPosition = i
                }
            }
        }
        if (startTime != null) {
            startHour = startTime[0].toInt()
            startMinute = startTime[1].toInt()
        }
        var i = 0
        for (it in allTaskTime) {
            if (i == skipPosition) {
                continue
            }
            i++
            val time = it.split("-")
            val sTime = time[0].split(":")
            val eTime = time[1].split(":")
            val sH = sTime[0].toInt()
            val sM = sTime[1].toInt()
            val eH = eTime[0].toInt()
            val eM = eTime[1].toInt()
            if (startTime == null) {
                if (hour in sH..eH) {
                    if (hour == sH && minute < sM || hour == eH && minute >= eM) {
                        break
                    }
                    boolean = true
                    break
                }
            }else {
                if (hour in sH..eH) {
                    if (hour == sH && minute <= sM || hour == eH && minute > eM) {
                        break
                    }
                    boolean = true
                    break
                }
                if (sH in startHour..hour) {
                    if (sH == startHour && sM < startMinute || sH == hour && sM >= minute) {
                        break
                    }
                    boolean = true
                    break
                }
            }
        }
        boolean
    }
    private fun expandItemClick(taskBean: TaskBean, tSViewTaskBean: TSViewTaskBean) {
        AddTaskDialog(context!!,
                R.style.dialog,
                mIsRepeat,
                taskBean,
                tSViewTaskBean
        ) { _, _ ->
            mOnTaskBeanChanged.invoke(null, taskBean, tSViewTaskBean)
            notifyExpandListViewDataSetChange()
        }.show()
    }

    private fun initFloatButton() {
        mFloatButton = mRootView.findViewById(R.id.fg1_floatButton)
        mFloatButton.setOnClickListener {
            AddTaskDialog(context!!, R.style.dialog, mIsRepeat) { taskBean, tSViewTaskBean ->
                if (taskBean != null) {
                    mOnTaskBeanChanged.invoke(true, taskBean, tSViewTaskBean!!)
                    notifyExpandListViewDataSetChange()
                }
            }.show()
        }
    }
}
