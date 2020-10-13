package com.fsyy.listener.rongcloud.network

import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import kotlin.random.Random

object HttpUtil {
    fun getUserToken(appKey:String,appSecret:String,params:Map<String,String>,callback:okhttp3.Callback){
        val path = "https://api.cn.ronghub.com/user/getToken.json";
        val nonce= (1..1000).random().toString()
        val timeStamp=System.currentTimeMillis().toString()
        val signature=SHA1Tool.SHA1(appSecret+nonce+timeStamp)
        val client=OkHttpClient()
        val requestBody=FormBody.Builder().run {
            for((key,value) in params){
                add(key,value)
            }
            build()
        }
        val request=Request.Builder().url(path)
            .addHeader("RC-App-Key",appKey)
            .addHeader("RC-Nonce",nonce)
            .addHeader("RC-Timestamp",timeStamp)
            .addHeader("RC-Signature",signature)
            .post(requestBody)
            .build()
        client.newCall(request).enqueue(callback)
    }
}