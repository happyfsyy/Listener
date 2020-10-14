/*
 * 项目名：Listener
 * 作者：@happy_fsyy
 * 联系我：https://github.com/happyfsyy
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

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