package com.example.timeplantest.fgments

import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.timeplantest.R
import com.example.timeplantest.adapter.WeekVpAdapter
import com.example.timeplantest.bean.CalendarBean
import com.example.timeplantest.bean.TaskBean
import com.example.timeplantest.weight.dialog.NameDialog
import com.ndhzs.timeselectview.TimeSelectView
import com.ndhzs.timeselectview.bean.TSViewDayBean
import com.ndhzs.timeselectview.bean.TSViewTaskBean

/**
 *@author 985892345
 *@email 2767465918@qq.com
 *@data 2021/5/3
 *@description
 */
class SecondFragment(activity: AppCompatActivity,
                     drawerLayout: DrawerLayout,
                     calendar: List<CalendarBean>,
                     currentWeekPage: Int,
                     currentDay: Int,
                     dayBeans: List<TSViewDayBean>,
                     onDataChange: ((isAddedOrDeleted: Boolean?, position: Int, taskBean: TaskBean, tsViewTaskBean: TSViewTaskBean) -> Unit)) : Fragment() {

    fun notifyItem(position: Int) {
        mTimeSelectView.notifyItemDataChanged(position)
    }

    fun getWeekLocation(): Rect {
        if (this::mWeekVp.isInitialized) {
            if (mWeekLocation.width() != mWeekVp.width) {
                val location = IntArray(2)
                mWeekVp.getLocationOnScreen(location)
                mWeekLocation.left = location[0]
                mWeekLocation.top = location[1]
                mWeekLocation.right = location[0] + mWeekVp.width
                mWeekLocation.bottom = location[1] + mWeekVp.height
            }
        }
        return mWeekLocation
    }

    /**
     * 解决 TimeSelectView 与 ViewPager2 的滑动冲突
     */
    fun touchEvent(ev: MotionEvent, viewPager2: ViewPager2) {
        if (this::mTimeSelectView.isInitialized) {
            viewPager2.isUserInputEnabled = !mTimeSelectView.isDealWithTouchEvent(ev, 1)
        }
    }

    private val mActivity = activity
    private val mDrawerLayout = drawerLayout
    private val mCalendar = calendar
    private val mCurrentWeekPage = currentWeekPage
    private val mCurrentDay = currentDay
    private val mDayBeans = dayBeans
    private val mOnDataChange = onDataChange
    private lateinit var mRootView: View
    private lateinit var mWeekVp: ViewPager2
    private lateinit var mWeekVpAdapter: WeekVpAdapter
    private lateinit var mTimeSelectView: TimeSelectView

    private val mWeekLocation = Rect()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        if (this::mRootView.isInitialized) {
            return mRootView
        }
        mRootView = inflater.inflate(R.layout.fragment2_second, container, false)
        initToolbar()
        initWeekView()
        initTimeSelectView()
        return mRootView
    }

    private fun initToolbar() {
        val toolbar: Toolbar = mRootView.findViewById(R.id.fg2_add_toolbar)
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

    private fun initWeekView() {
        mWeekVp = mRootView.findViewById(R.id.fg2_vp_day_view)
        mWeekVp.post {
            val location = IntArray(2)
            mWeekVp.getLocationOnScreen(location)
            mWeekLocation.left = location[0]
            mWeekLocation.top = location[1]
            mWeekLocation.right = location[0] + mWeekVp.width
            mWeekLocation.bottom = location[1] + mWeekVp.height
        }
        mWeekVpAdapter = WeekVpAdapter(mCalendar, mCurrentWeekPage, mCurrentDay)
        mWeekVp.adapter = mWeekVpAdapter
        mWeekVp.setCurrentItem(mCurrentWeekPage, false)
        mWeekVpAdapter.setOnClickListener {
            mTimeSelectView.setCurrentItem(it)
        }
    }

    private fun initTimeSelectView() {
        val currentItem = mCurrentWeekPage * 7 + mCurrentDay
        mTimeSelectView = mRootView.findViewById(R.id.fg2_timeSelectView)
        mTimeSelectView.initializeBean(mDayBeans, currentItem, currentItem)
        mTimeSelectView.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                mWeekVpAdapter.setWeekPosition(position / 7, position % 7)
                mWeekVp.currentItem = position / 7
            }
        })
        mTimeSelectView.setOnTSVClickListener {
            NameDialog(context!!, R.style.dialog, it.any1 as TaskBean, it) { taskBean, tsViewTaskBean ->
                mTimeSelectView.notifyItemRefresh()
                mOnDataChange.invoke(null, mTimeSelectView.getCurrentItem(), taskBean, tsViewTaskBean)
            }.show()
        }
        mTimeSelectView.setOnDataListener(object : TimeSelectView.OnDataChangeListener {
            override fun onDataAdd(newData: TSViewTaskBean) {
                val taskBean = TaskBean()
                taskBean.update(newData)
                newData.any1 = taskBean
                taskBean.save()
                mOnDataChange.invoke(true, mTimeSelectView.getCurrentItem(), taskBean, newData)
            }

            override fun onDataDelete(deletedData: TSViewTaskBean) {
                val taskBean = deletedData.any1 as TaskBean
                taskBean.delete()
                mOnDataChange.invoke(false, mTimeSelectView.getCurrentItem(), taskBean, deletedData)
            }

            override fun onDataAlter(alterData: TSViewTaskBean) {
                val taskBean = alterData.any1 as TaskBean
                taskBean.update(alterData)
                taskBean.save()
                mOnDataChange.invoke(null, mTimeSelectView.getCurrentItem(), taskBean, alterData)
            }
        })
    }
}