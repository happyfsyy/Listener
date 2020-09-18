package com.fsyy.listener.utils.extension

import android.widget.Toast
import com.fsyy.listener.ui.MyApplication

fun String.showToast(){
    Toast.makeText(MyApplication.context,this,Toast.LENGTH_SHORT).show()
}