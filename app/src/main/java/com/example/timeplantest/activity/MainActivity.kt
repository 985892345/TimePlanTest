package com.example.timeplantest.activity

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.example.timeplantest.R
import com.example.timeplantest.adapter.FgVpAdapter
import com.example.timeplantest.bean.CalendarBean
import com.example.timeplantest.bean.TaskBean
import com.example.timeplantest.fgments.FirstFragment
import com.example.timeplantest.fgments.SecondFragment
import com.example.timeplantest.fgments.ThirdFragment
import com.example.timeplantest.weight.drawerLayout.InDrawerLayout
import com.example.timeplantest.weight.timeselectview.TimeSelectView
import com.example.timeplantest.weight.timeselectview.bean.TSViewDayBean
import com.example.timeplantest.weight.timeselectview.bean.TSViewTaskBean
import com.example.timeplantest.weight.timeselectview.utils.TSViewLongClick
import com.example.timeplantest.weight.timeselectview.utils.TSViewTimeUtil
import com.example.timeplantest.weight.timeselectview.utils.tscrollview.TScrollViewTouchEvent
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.litepal.LitePal
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs

class MainActivity : BaseActivity(), View.OnClickListener {

    private lateinit var mDrawerLayout: DrawerLayout
    private lateinit var mFgViewPager: ViewPager2
    private lateinit var mFgVpAdapter: FgVpAdapter
    private lateinit var mNavigationView: BottomNavigationView
    private val mFragments= ArrayList<Fragment>()
    private lateinit var mFirstFg: FirstFragment
    private lateinit var mSecondFg: SecondFragment
    private lateinit var mThirdFg: ThirdFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        LitePal.initialize(this)

        initDrawerLayout()
        initFragments()
        initViewPager()
        initBottomNavigationView()
    }

    private fun initDrawerLayout() {
        mDrawerLayout = findViewById(R.id.drawer_layout)
        InDrawerLayout(this, mDrawerLayout)
        val mHeadImg: ImageView = findViewById(R.id.main_left_img_head)
        val mTv1: TextView = findViewById(R.id.main_left_1)
        val mTv2: TextView = findViewById(R.id.main_left_2)
        val mTv3: TextView = findViewById(R.id.main_left_3)
        val mTv4: TextView = findViewById(R.id.main_left_4)

        mHeadImg.setOnClickListener(this)
        mTv1.setOnClickListener(this)
        mTv2.setOnClickListener(this)
        mTv3.setOnClickListener(this)
        mTv4.setOnClickListener(this)
    }

    private fun initFragments() {
        val days1 = listOf("25", "26", "27", "28", "29", "30", "1")
        val dayOff1 = listOf("班", "", "", "", "", "", "休")
        val lunarCalendar1 = listOf("十四", "十五", "十六", "十七", "十八", "十九", "劳动节")
        val days2 = listOf("2", "3", "4", "5", "6", "7", "8")
        val dayOff2 = listOf("休", "休", "休", "休", "", "", "班")
        val lunarCalendar2 = listOf("廿二", "廿三", "青年节", "立夏", "廿五", "廿六", "廿七")
        val days3 = listOf("9", "10", "11", "12", "13", "14", "15")
        val dayOff3 = listOf("", "", "", "", "", "", "")
        val lunarCalendar3 = listOf("母亲节", "廿九", "三十", "四月", "初二", "初三", "初四")

        val calendar = listOf(
                CalendarBean(days1, dayOff1, lunarCalendar1),
                CalendarBean(days2, dayOff2, lunarCalendar2),
                CalendarBean(days3, dayOff3, lunarCalendar3),
        )
        val currentPage = 1
        val sdf = SimpleDateFormat("yyyy-M-d")
        val currentDay = getDiffDate("2021-5-2", sdf.format(Date()))
        val dayBeans = getDayBeans(3 * 7)
        mFirstFg = FirstFragment(this, mDrawerLayout, dayBeans[currentPage * 7 + currentDay]) { isAddedOrDeleted, _, tSViewTaskBean ->
            if (isAddedOrDeleted != null) {
                if (isAddedOrDeleted) {
                    dayBeans[currentPage * 7 + currentDay].tSViewTaskBeans.add(tSViewTaskBean)
                }else {
                    dayBeans[currentPage * 7 + currentDay].tSViewTaskBeans.remove(tSViewTaskBean)
                }
            }
            mSecondFg.notifyItem(currentPage * 7 + currentDay)
        }
        mSecondFg = SecondFragment(this, mDrawerLayout, calendar, currentPage, currentDay, dayBeans) { isAddedOrDeleted, position, _, tSViewTaskBean ->
            if (position == currentPage * 7 + currentDay) {
                //TimeSelectView内部实现了对外部数组的删除操作
                mFirstFg.notifyExpandListViewDataSetChange()
            }
        }
        mThirdFg = ThirdFragment(this, mDrawerLayout)

        mFragments.add(mFirstFg)
        mFragments.add(mSecondFg)
        mFragments.add(mThirdFg)
    }

    private fun getDayBeans(size: Int): MutableList<TSViewDayBean> {
        val share = getSharedPreferences("launch", MODE_PRIVATE)
        val isFirstLaunch = share.getBoolean("isFirstLaunch", true)
        val beans = ArrayList<TSViewDayBean>()
        if (isFirstLaunch) {
            val edit = share.edit()
            edit.putBoolean("isFirstLaunch", false)
            edit.apply()
            val calendar = Calendar.getInstance()
            val sdf = SimpleDateFormat("yyyy-M-d")
            calendar.time = sdf.parse("2021-5-2")!!
            repeat(size) {
                beans.add(TSViewDayBean(calendar.time))
                calendar.add(Calendar.DATE, 1)
            }
            return beans
        }else {
            repeat(size) {
                val date = TSViewTimeUtil.getDate("2021-5-2", it)
                val taskBeans = LitePal.where("date=?", date).find(TaskBean::class.java)
                val tSViewTaskBean = LinkedList<TSViewTaskBean>()
                taskBeans.forEach { taskBean ->
                    tSViewTaskBean.add(taskBean.obtainTSViewTaskBean())
                }
                beans.add(TSViewDayBean(date, tSViewTaskBean))
            }
            beans.sortWith(Comparator{ o1, o2 ->
                val diffDay1 = getDiffDate("2021-5-2", o1.date)
                val diffDay2 = getDiffDate("2021-5-2", o2.date)
                return@Comparator diffDay1 - diffDay2
            })
            return beans
        }
    }

    /**
     * 获取两个日期之间的间隔天数
     * @return 两个日期之间的间隔天数
     */
    private fun getDiffDate(start: String, end: String): Int {
        val sdf = SimpleDateFormat("yyyy-M-d")
        try {
            val startDate = sdf.parse(start)
            val endDate = sdf.parse(end)
            val fromCalendar = Calendar.getInstance()
            fromCalendar.time = startDate
            fromCalendar[Calendar.HOUR_OF_DAY] = 0
            fromCalendar[Calendar.MINUTE] = 0
            fromCalendar[Calendar.SECOND] = 0
            fromCalendar[Calendar.MILLISECOND] = 0
            val toCalendar = Calendar.getInstance()
            toCalendar.time = endDate
            toCalendar[Calendar.HOUR_OF_DAY] = 0
            toCalendar[Calendar.MINUTE] = 0
            toCalendar[Calendar.SECOND] = 0
            toCalendar[Calendar.MILLISECOND] = 0
            return ((toCalendar.time.time - fromCalendar.time.time) / (1000 * 60 * 60 * 24)).toInt()
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return 0
    }

    private fun initViewPager() {
        mFgViewPager = findViewById(R.id.main_vp_fragments)
        mFgVpAdapter = FgVpAdapter(this, mFragments)
        mFgViewPager.adapter = mFgVpAdapter
        mFgViewPager.offscreenPageLimit = 2
        mFgViewPager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                mNavigationView.menu.getItem(position).isChecked = true
            }
        })
    }

    private var mExitTime: Long = 0
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis() - mExitTime > 2000) {
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show()
                mExitTime = System.currentTimeMillis()
            }else {
                finish()
            }
            return false
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun initBottomNavigationView() {
        mNavigationView = findViewById(R.id.main_nav)
        mNavigationView.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_now -> {
                    mFgViewPager.currentItem = 0
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_add -> {
                    mFgViewPager.currentItem = 1
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_set -> {
                    mFgViewPager.currentItem = 2
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        })
    }

    private var mInitialX = 0
    private var mInitialY = 0
    private var weekLocation: Rect? = null
    private var timeViewLocation: Rect? = null
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val x = ev.rawX.toInt()
        val y = ev.rawY.toInt()
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                mInitialX = x
                mInitialY = y
                TSViewLongClick.sIsLongClickCount = 0
                weekLocation = mSecondFg.getWeekLocation()
                timeViewLocation = mSecondFg.getTimeSelectViewLocation()
                mFgViewPager.isUserInputEnabled = true
                if (mFgViewPager.currentItem == 1) {
                    if (y in weekLocation!!.top..weekLocation!!.bottom) {
                        mFgViewPager.isUserInputEnabled = false
                    }
                }
            }
            MotionEvent.ACTION_MOVE -> {
                when (mFgViewPager.currentItem) {
                    1 -> {
                        if (mInitialY in timeViewLocation!!.top..timeViewLocation!!.bottom) {
                            if (abs(x - mInitialX) <= TScrollViewTouchEvent.MOVE_THRESHOLD || abs(y - mInitialY) <= TScrollViewTouchEvent.MOVE_THRESHOLD) {
                                mFgViewPager.isUserInputEnabled = false
                            }else {
                                mFgViewPager.isUserInputEnabled = !TSViewLongClick.sHasLongClick
                            }
                        }
                    }
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.main_left_img_head -> {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
            R.id.main_left_1, R.id.main_left_2, R.id.main_left_3, R.id.main_left_4 -> {
                Toast.makeText(this, "。。。。。。", Toast.LENGTH_SHORT).show()
            }
        }
    }
}