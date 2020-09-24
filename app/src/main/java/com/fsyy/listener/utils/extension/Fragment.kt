package com.fsyy.listener.utils.extension

import android.content.Intent
import androidx.fragment.app.Fragment

inline fun <reified T> Fragment.startActivityForResult(requestCode:Int,noinline block: (Intent.() -> Unit)?=null){
    val intent= Intent(activity,T::class.java)
    block?.let { intent.it() }
    startActivityForResult(intent,requestCode)
}