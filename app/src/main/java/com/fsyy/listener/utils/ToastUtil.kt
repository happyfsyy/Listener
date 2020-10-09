package com.fsyy.listener.utils

import android.view.Gravity
import android.widget.Toast
import com.fsyy.listener.ui.MyApplication
import com.fsyy.listener.ui.viewgroup.ToastLayout

object ToastUtil {
    fun showCenterToast(imgId:Int,content:String){
        val toast=Toast(MyApplication.context)
        val toastLayout=ToastLayout(MyApplication.context,imgId,content)
        toast.view=toastLayout
        toast.setGravity(Gravity.CENTER,0,0)
        toast.show()
    }
}