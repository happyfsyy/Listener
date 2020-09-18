package com.fsyy.listener.utils.extension

import android.app.Activity
import android.content.Intent

inline fun <reified T> Activity.startActivity(){
    val intent=Intent(this,T::class.java)
    startActivity(intent)
}
inline fun <reified T> Activity.startActivity(block:Intent.()->Unit){
    val intent=Intent(this,T::class.java)
    intent.block()
    startActivity(intent)
}