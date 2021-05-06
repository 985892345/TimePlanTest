package com.example.timeplantest.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.RemoteViews
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.timeplantest.R
import com.example.timeplantest.activity.MainActivity
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class AlarmService : Service() {

    fun cancelSend() {
        if (this::mTaskPendingIntent.isInitialized) {
            Toast.makeText(this, "已取消提醒", Toast.LENGTH_SHORT).show()
            val alarm = getSystemService(ALARM_SERVICE) as AlarmManager
            alarm.cancel(mTaskPendingIntent)
        }
    }

    fun stopFore() {
        mIsShowFore = false
        closeAllShake()
//        if (this::mManager.isInitialized) {
//            mManager.cancelAll()
//        }
        stopForeground(true)
    }

    @Volatile
    private var mIsShowFore = false
    private lateinit var mTaskName: String
    private lateinit var mEndTime: String
    private lateinit var mTaskPendingIntent: PendingIntent
    private lateinit var mRemoteViews: RemoteViews
    fun openTime(taskName: String?, startTime: String, endTime: String) {
        if (mIsShowFore) {
            if (taskName != mTaskName) {
                mRemoteViews.setTextViewText(R.id.service_tv_task_name, taskName)
            }
            if (endTime != mEndTime) {
                mRemoteViews.setTextViewText(R.id.service_tv_endTime, endTime)
                closeAllShake()
                openToEndTimeShake()
            }
            return
        }
        mIsShowFore = true

        mTaskName = taskName ?: "无名称"
        mEndTime = endTime
        val calendar = Calendar.getInstance()
        val nowHour = calendar.get(Calendar.HOUR_OF_DAY)
        val nowMinute = calendar.get(Calendar.MINUTE)
        val nowSecond = calendar.get(Calendar.SECOND)
        val sTime = startTime.split(":")
        val hour = sTime[0].toInt()
        val minute = sTime[1].toInt()
        val diffTime = ((hour - nowHour) * 3600 + (minute - nowMinute - 1) * 60 + (60 - nowSecond)) * 1000L


        val intent = Intent(this, AlarmService::class.java)

        if (diffTime > 1 * 1000) {
            mTaskPendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            val alarm = getSystemService(ALARM_SERVICE) as AlarmManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Log.d("123", "openTime(AlarmService.kt:64)-->>  ++++++++++++++++++")
                alarm.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + diffTime, mTaskPendingIntent)
            }else {
                alarm.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + diffTime, mTaskPendingIntent)
            }
            Toast.makeText(this, "已设置好${startTime}的提醒，还差${diffTime/1000}秒开始", Toast.LENGTH_LONG).show()
        }else {
            startService(intent)
            Toast.makeText(this, "你现在正处于任务中！", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate() {
        Toast.makeText(this, "后台服务已启动！", Toast.LENGTH_SHORT).show()
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        if (mIsShowFore) {
            Toast.makeText(this, "开始提醒", Toast.LENGTH_SHORT).show()
            setForegroundService()
            openToEndTimeShake()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private lateinit var mManager: NotificationManager
    private fun setForegroundService() {

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            //设定的通知渠道名称
            val channelName = "任务通知"
            //设置通知的重要程度
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            //构建通知渠道
            val channel = NotificationChannel("1", channelName, importance)
            //向系统注册通知渠道，注册后不能改变重要性以及其他通知行为
            mManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            mManager.createNotificationChannel(channel)
        }

        mRemoteViews = RemoteViews(this.packageName, R.layout.service_task)
        val btnBackgroundIntent = Intent(this, MainActivity::class.java)
        val btnBackgroundPendingIntent = PendingIntent.getActivity(this, 0, btnBackgroundIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        mRemoteViews.setOnClickPendingIntent(R.id.service_btn_background, btnBackgroundPendingIntent)

        val btnFinishTask = Intent("${this.packageName}_service_task_finish")
        val btnFinishPendingIntent = PendingIntent.getBroadcast(this, 1, btnFinishTask, PendingIntent.FLAG_UPDATE_CURRENT)
        mRemoteViews.setOnClickPendingIntent(R.id.service_btn_finish, btnFinishPendingIntent)

        mRemoteViews.setTextViewText(R.id.service_tv_task_name, mTaskName)
        mRemoteViews.setTextViewText(R.id.service_tv_endTime, mEndTime)


        //在创建的通知渠道上发送通知
        val builder = NotificationCompat.Builder(this, "1")
        builder.setSmallIcon(R.drawable.ic_alarm)
                .setContent(mRemoteViews)
                .setVibrate(longArrayOf(200, 800, 200, 800, 200, 800))
                .setAutoCancel(false)

        startForeground(1, builder.build())
    }

    private var mThreadPool = Executors.newScheduledThreadPool(1)
    private fun closeAllShake() {
        mThreadPool.shutdownNow()
        mThreadPool = Executors.newScheduledThreadPool(1)
    }

    private fun openToEndTimeShake() {
        val calendar = Calendar.getInstance()
        val nowHour = calendar.get(Calendar.HOUR_OF_DAY)
        val nowMinute = calendar.get(Calendar.MINUTE)
        val nowSecond = calendar.get(Calendar.SECOND)
        val eTime = mEndTime.split(":")
        val hour = eTime[0].toInt()
        val minute = eTime[1].toInt()
        val diffTime = (hour - nowHour) * 3600 + (minute - nowMinute - 1) * 60 + (60 - nowSecond)
        mThreadPool.schedule({
            mIsShowFore = false
            stopForeground(true)
            val intent = Intent("${this.packageName}_service_task_end")
            sendBroadcast(intent)
        }, diffTime.toLong(), TimeUnit.SECONDS)
    }

    override fun onBind(intent: Intent): IBinder {
        return AlarmBinder()
    }

    inner class AlarmBinder : Binder() {
        fun getService(): AlarmService {
            return this@AlarmService
        }
    }
}