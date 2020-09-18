package com.fsyy.listener.ui.settings

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.fsyy.listener.R
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity(),View.OnClickListener{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        initListener()
    }
    private fun initListener(){
        profile_photo_layout.setOnClickListener(this)
        profile_username_layout.setOnClickListener(this)
        profile_sign_layout.setOnClickListener(this)
    }

    override fun onClick(v: View?) {

    }
}