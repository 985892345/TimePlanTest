package com.example.timeplantest.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.example.timeplantest.R
import java.lang.Thread.sleep
import kotlin.concurrent.thread

class StartActivity : BaseActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        val window = this.window
        window.statusBarColor = Color.TRANSPARENT
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        val textView = findViewById<TextView>(R.id.version)
        val packageManager = this.packageManager
        try {
            val packageInfo = packageManager.getPackageInfo(this.packageName, 0)
            textView.text = "V " + packageInfo.versionName
        }catch (e: PackageManager.NameNotFoundException) {
            textView.text = "V ?"
        }

        thread {
            sleep(1000)
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}