package com.example.timeplantest.activity

import android.content.*
import android.graphics.Rect
import android.os.Bundle
import android.os.IBinder
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
import com.example.timeplantest.service.AlarmService
import com.example.timeplantest.weight.drawerLayout.InDrawerLayout
import com.ndhzs.timeselectview.bean.TSViewDayBean
import com.ndhzs.timeselectview.bean.TSViewTaskBean
import com.ndhzs.timeselectview.utils.TSViewLongClick
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ndhzs.timeselectview.TimeSelectView
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
    private val mFragments = ArrayList<Fragment>()
    private lateinit var mFirstFg: FirstFragment
    private lateinit var mSecondFg: SecondFragment
    private lateinit var mThirdFg: ThirdFragment

    private lateinit var mAlarmService: AlarmService

    private val mServiceConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            mAlarmService = (service as AlarmService.AlarmBinder).getService()
            sendForeground()
            registerReceiver(object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    (mPreTaskBean.any1 as TaskBean).isFinish = true
                    mAlarmService.stopFore()
                    mFirstFg.notifyExpandListViewDataSetChange()
                    mSecondFg.notifyItem(mCurrentPage * 7 + mCurrentDay)
                    sendForeground()
                }
            }, IntentFilter("${this@MainActivity.packageName}_service_task_finish"))

            registerReceiver(object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    mFirstFg.notifyExpandListViewDataSetChange()
                    sendForeground()
                }
            }, IntentFilter("${this@MainActivity.packageName}_service_task_end"))
        }

        override fun onServiceDisconnected(name: ComponentName) {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        LitePal.initialize(this)
        val intent = Intent(this, AlarmService::class.java)
        bindService(intent, mServiceConnection, BIND_AUTO_CREATE)

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

    private var mCurrentPage = 0
    private var mCurrentDay = 0
    private lateinit var mNowDayBean: MutableList<TSViewTaskBean>
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
        val sdf = SimpleDateFormat("yyyy-M-d")
        val diffDay = getDiffDate("2021-4-25", sdf.format(Date()))
        mCurrentPage = 2
        mCurrentDay = 6
        val dayBeans = getDayBeans(3 * 7)
        mNowDayBean = dayBeans[mCurrentPage * 7 + mCurrentDay].tSViewTaskBeans
        mFirstFg = FirstFragment(this, mDrawerLayout, dayBeans[mCurrentPage * 7 + mCurrentDay], onFinished = { taskBean, tSViewBean, isFinish ->
            sendForeground()
        }, onTaskBeanChanged = { isAddedOrDeleted, _, tSViewTaskBean ->
            if (isAddedOrDeleted != null) {
                if (isAddedOrDeleted) {
                    mNowDayBean.add(tSViewTaskBean)
                }else {
                    mNowDayBean.remove(tSViewTaskBean)
                }
            }
            mSecondFg.notifyItem(mCurrentPage * 7 + mCurrentDay)
            sendForeground()
        })
        mSecondFg = SecondFragment(this, mDrawerLayout, calendar, mCurrentPage, mCurrentDay, dayBeans) { isAddedOrDeleted, position, _, tSViewTaskBean ->
            if (position == mCurrentPage * 7 + mCurrentDay) {
                //TimeSelectView内部实现了对外部数组的删除操作
                mFirstFg.notifyExpandListViewDataSetChange()
                sendForeground()
            }
        }
        mThirdFg = ThirdFragment(this, mDrawerLayout)

        mFragments.add(mFirstFg)
        mFragments.add(mSecondFg)
        mFragments.add(mThirdFg)
    }

    private lateinit var mPreTaskBean: TSViewTaskBean
    private fun sendForeground() {
        val calendar = Calendar.getInstance()
        val nowH = calendar.get(Calendar.HOUR_OF_DAY)
        val nowM = calendar.get(Calendar.MINUTE)
        val showBean = getShowBean(nowH, nowM)
        if (showBean != null) {
            mPreTaskBean = showBean
            mAlarmService.openTime(showBean.name, showBean.startTime, showBean.endTime)
        }else {
            mAlarmService.stopFore()
        }
    }

    private fun getShowBean(nowHour: Int, nowMinute: Int): TSViewTaskBean? {
        val cacheList = TreeMap<Int, TSViewTaskBean>()
        for (it in mNowDayBean) {
            val startTimeList = it.startTime.split(":")
            val endTimeList = it.endTime.split(":")
            val sH = startTimeList[0].toInt()
            val sM = startTimeList[1].toInt()
            val eH = endTimeList[0].toInt()
            val eM = endTimeList[1].toInt()
            val nowTime = nowHour * 60 + nowMinute
            val startTime = sH * 60 + sM
            val endTime = eH * 60 + eM
            val isFinish = (it.any1 as TaskBean).isFinish
            if (nowTime in startTime..endTime && !isFinish) {
                return it
            }else if (nowTime < startTime && !isFinish) {
                cacheList[startTime] = it
            }
        }
        val firstMap = cacheList.firstEntry()
        if (firstMap != null) {
            return firstMap.value
        }
        return null
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
            calendar.time = sdf.parse("2021-4-25")!!
            repeat(size) {
                beans.add(TSViewDayBean(calendar.time))
                calendar.add(Calendar.DATE, 1)
            }
            return beans
        }else {
            repeat(size) {
                val date = getDate("2021-4-25", it)
                val taskBeans = LitePal.where("date=?", date).find(TaskBean::class.java)
                val tSViewTaskBean = LinkedList<TSViewTaskBean>()
                taskBeans.forEach { taskBean ->
                    tSViewTaskBean.add(taskBean.obtainTSViewTaskBean())
                }
                beans.add(TSViewDayBean(date, tSViewTaskBean))
            }
            beans.sortWith(Comparator{ o1, o2 ->
                val diffDay1 = getDiffDate("2021-4-25", o1.date)
                val diffDay2 = getDiffDate("2021-4-25", o2.date)
                return@Comparator diffDay1 - diffDay2
            })
            return beans
        }
    }

    private fun getDate(firstDate: String, diff: Int): String {
        val sdf = SimpleDateFormat("yyyy-M-d")
        val date = sdf.parse(firstDate)
        val calendar = Calendar.getInstance()
        calendar.time = date!!
        calendar.add(Calendar.DATE, diff)
        return sdf.format(calendar.time)
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
        mFgViewPager.offscreenPageLimit = 1
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

    override fun onDestroy() {
        super.onDestroy()
        unbindService(mServiceConnection)
        mAlarmService.stopFore()
        mAlarmService.stopSelf()
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

    private var weekLocation: Rect? = null
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val y = ev.rawY.toInt()
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                weekLocation = mSecondFg.getWeekLocation()
            }
        }
        if (mFgViewPager.currentItem == 1) {
            if (y in weekLocation!!.top..weekLocation!!.bottom) {
                mFgViewPager.isUserInputEnabled = false
            }else {
                mSecondFg.touchEvent(ev, mFgViewPager)
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