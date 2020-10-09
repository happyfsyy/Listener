package com.fsyy.listener.utils

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import com.fsyy.listener.R

object PopupUtil {
    fun showPopupWindow(contentView:View,parent:View):PopupWindow= PopupWindow(contentView,ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)
        .apply {
            isFocusable=true
            isOutsideTouchable=true
            isClippingEnabled=false
            animationStyle=R.style.popupAnimation
            setBackgroundDrawable(ColorDrawable(Color.parseColor("#55000000")))
            showAtLocation(parent,Gravity.BOTTOM,0,0)
        }
}