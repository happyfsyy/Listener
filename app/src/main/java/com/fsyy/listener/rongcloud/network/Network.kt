package com.fsyy.listener.rongcloud.network

import android.content.Context
import android.util.Log
import cn.leancloud.AVUser
import com.fsyy.listener.ui.MyApplication
import com.fsyy.listener.utils.LogUtils
import com.google.gson.Gson
import io.rong.imlib.RongIMClient
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

object Network {
    fun getToken(){
        val params= mapOf("userId" to AVUser.currentUser().objectId,"name" to AVUser.currentUser().username,
            "portraitUri" to AVUser.currentUser().getString("photoUrl"))
        HttpUtil.getUserToken(MyApplication.appkey1, MyApplication.appSecret1, params,
            object : Callback{
                override fun onFailure(call: Call, e: IOException) {
                }
                override fun onResponse(call: Call, response: Response) {
                    val responseData=response.body?.string()
                    val tokenResponse=Gson().fromJson(responseData,TokenResponse::class.java)
                    MyApplication.context.getSharedPreferences("token",Context.MODE_PRIVATE).edit().apply {
                        putString("token",tokenResponse.token)
                        apply()
                    }
                    connectIM(tokenResponse.token)
                }
            })
    }
    fun connectIM(token:String){
        RongIMClient.connect(token, object : RongIMClient.ConnectCallback() {
            override fun onSuccess(p0: String?) {
                LogUtils.e("连接成功:$p0")
            }
            override fun onError(p0: RongIMClient.ConnectionErrorCode?) {
                LogUtils.e("链接失败，错误码是$p0")
                if(p0==RongIMClient.ConnectionErrorCode.RC_CONN_TOKEN_INCORRECT){
                    getToken()
                }
            }
            override fun onDatabaseOpened(p0: RongIMClient.DatabaseOpenStatus?) {
                LogUtils.e("消息数据库打开,可以进入到主界面")
            }
        })
    }
}