package com.fsyy.listener.ui

import android.app.Application
import android.content.Context
import cn.leancloud.AVOSCloud
import com.fsyy.listener.rongcloud.network.HttpUtil
import io.rong.imkit.RongIM

class MyApplication :Application(){
    companion object{
        lateinit var context:Context
        const val appkey1="k51hidwqkvqbb"
        const val appSecret1="lPoZqlncWZU"
    }
    override fun onCreate() {
        super.onCreate()
        context =applicationContext
        AVOSCloud.initialize(this,"EujCJaz5mTgtmx1SrU1S31Yn-gzGzoHsz","F5KDdcrdQw4dsBBLM7fSMbcj","https://eujcjaz5.lc-cn-n1-shared.com")
        RongIM.init(this,"k51hidwqkvqbb")


    }
}