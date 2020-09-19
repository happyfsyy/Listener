package com.fsyy.listener.ui

import android.app.Application
import android.content.Context
import cn.leancloud.AVOSCloud

class MyApplication :Application(){
    companion object{
        lateinit var context:Context
    }
    override fun onCreate() {
        super.onCreate()
        context =applicationContext
        AVOSCloud.initialize(this,"EujCJaz5mTgtmx1SrU1S31Yn-gzGzoHsz","F5KDdcrdQw4dsBBLM7fSMbcj","https://eujcjaz5.lc-cn-n1-shared.com")
    }
}