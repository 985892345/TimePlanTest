package com.example.timeplantest.fgments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.timeplantest.R

/**
 *@author 985892345
 *@email 2767465918@qq.com
 *@data 2021/5/3
 *@description
 */
class ThirdFragment(activity: AppCompatActivity, drawerLayout: DrawerLayout) : Fragment() {

    private val mActivity = activity
    private val mDrawerLayout = drawerLayout
    private lateinit var mRootView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mRootView = inflater.inflate(R.layout.fragment3_third, container, false)
        initToolbar()
        return mRootView
    }

    private fun initToolbar() {
        val toolbar: Toolbar = mRootView.findViewById(R.id.fg3_set_toolbar)
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
}