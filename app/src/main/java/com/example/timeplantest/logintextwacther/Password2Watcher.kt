package com.example.timeplantest.logintextwacther

import android.text.Editable
import com.google.android.material.textfield.TextInputLayout

/**
 *@author 985892345
 *@email 2767465918@qq.com
 *@data 2021/5/3
 *@description
 */
class Password2Watcher(password: String, layout1: TextInputLayout, layout2: TextInputLayout) : BaseTextWatcher(layout2) {

    private val mPassword = password
    private val mPreLayout = layout1

    override fun afterTextChanged(s: Editable) {
        val password2 = s.toString()
        if (mPassword.length < 6) {
            mPreLayout.error = "密码必须大于5位！"
        }
        if (s.length >= password2.length && !password2.contentEquals(mPassword)) {
            mLayout.error = null
            mLayout.error = "密码不相同！"
        } else if (s.length <= mLayout.counterMaxLength) {
            mLayout.error = null
        }
    }
}