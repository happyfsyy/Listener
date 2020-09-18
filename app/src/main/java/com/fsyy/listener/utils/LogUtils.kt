package com.fsyy.listener.utils

import android.util.Log

object LogUtils {
    val tag="我自己打印的Log"
    const val isDebug=true
    fun e(msg:String){
        if(isDebug)
            Log.e(tag,msg)
    }
    fun e(tag:String,msg: String){
        if(isDebug)
            Log.e(tag,msg)
    }
}