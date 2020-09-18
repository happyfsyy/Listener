package com.fsyy.listener.ui

import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import cn.leancloud.AVUser
import com.fsyy.listener.R
import com.fsyy.listener.ui.main.MainActivity
import com.fsyy.listener.ui.login.LoginActivity
import com.fsyy.listener.utils.extension.startActivity


class SplashActivity : AppCompatActivity() {
    private val handler=Handler()
    private val runnable= Runnable {
        if (AVUser.currentUser() == null)
            startActivity<LoginActivity>()
        else
            startActivity<MainActivity>()
        finish()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        //todo 检查网络状况
        handler.postDelayed(runnable,2000)
    }

    override fun onBackPressed() {
        handler.removeCallbacks(runnable)
        finish()
    }
}