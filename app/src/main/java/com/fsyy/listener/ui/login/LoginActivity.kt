package com.fsyy.listener.ui.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import cn.leancloud.AVUser
import com.fsyy.listener.R
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }
    //todo 发送验证码，检测手机号正确与否，然后注册登录
    fun login(view: View){
        AVUser.signUpOrLoginByMobilePhoneInBackground("+8615900543111","262497").subscribe(object :
            Observer<AVUser> {
            override fun onSubscribe(d: Disposable) {
            }
            override fun onNext(t: AVUser) {
                Log.e("》》》》》》","登录成功")
            }
            override fun onError(e: Throwable) {
                e.printStackTrace()
                Log.e("》》》》》》","登录失败")
            }
            override fun onComplete() {
            }
        })
    }
}