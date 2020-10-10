package com.fsyy.listener.utils

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import com.fsyy.listener.R

object PopupUtil {
    fun showPopupWindow(contentView:View,parent:View,alphaAnimation:Boolean=true):PopupWindow{
        val popupWindow=PopupWindow(contentView,ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)
            .apply {
                isFocusable=true
                isOutsideTouchable=true
                isClippingEnabled=false
                setBackgroundDrawable(ColorDrawable(Color.parseColor("#55000000")))
            }
        if(alphaAnimation){
            popupWindow.animationStyle=R.style.popupAnimation
        }
        popupWindow.showAtLocation(parent,Gravity.TOP,0,0)
        return popupWindow
    }
}