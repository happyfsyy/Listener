package com.fsyy.listener.utils

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

object SoftKeyboardUtils {
    fun hideKeyboard(activity: Activity){
        val imm=activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(activity.window.decorView.windowToken,0)
    }
    fun showKeyboard(activity: Activity,editText: EditText){
        val imm=activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editText,0)
    }
}