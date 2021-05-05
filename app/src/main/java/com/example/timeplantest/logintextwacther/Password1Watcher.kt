package com.example.timeplantest.logintextwacther

import android.text.Editable
import com.google.android.material.textfield.TextInputLayout

/**
 *@author 985892345
 *@email 2767465918@qq.com
 *@data 2021/5/3
 *@description
 */
class Password1Watcher(password: TextInputLayout) : BaseTextWatcher(password) {

    override fun afterTextChanged(s: Editable) {
        super.afterTextChanged(s)

        val text = s.toString()
        if (s.length > 5 && !text.matches(Regex(".*[a-zA-Z]+.*"))) {
            mLayout.error = null //每次都会设置一个新的报错，必须把前一个报错给删掉
            mLayout.error = "密码必须包含字母！"
        }else if (s.length <= mLayout.counterMaxLength && text.matches(Regex(".*[a-zA-Z]+.*"))) {
            mLayout.error = null
        }
    }
}