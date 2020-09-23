package com.fsyy.listener.ui.main

import android.app.Person
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import cn.leancloud.AVUser
import com.fsyy.listener.R
import com.fsyy.listener.ui.settings.FeedbackActivity
import com.fsyy.listener.ui.settings.ProfileActivity
import com.fsyy.listener.utils.LogUtils
import com.fsyy.listener.utils.extension.startActivity
import kotlinx.android.synthetic.main.fragment_personal.*

class PersonalFragment:Fragment(),View.OnClickListener{
    private lateinit var viewModel:PersonalViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel=ViewModelProvider(this).get(PersonalViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_personal,container,false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initViews()
        initListener()
        initObserver()
    }
    private fun initViews(){
        personal_userName.text= AVUser.currentUser().username
        LogUtils.e("个人页中，用户名是：${AVUser.currentUser().username}")
        val intro= AVUser.currentUser().getString("intro")
        if(intro=="")
            personal_user_intro.text=getString(R.string.profile_signature)
        else
            personal_user_intro.text=intro
    }
    private fun initListener(){
        personal_user_layout.setOnClickListener(this)
        personal_feedback_layout.setOnClickListener(this)
    }
    private fun initObserver(){
        viewModel.currentUserLiveData.observe(activity!!){
            val result=it.getOrNull()
            if(result!=null)
                LogUtils.e("已经刷新了AVUser")
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            personal_user_layout.id->activity?.startActivity<ProfileActivity>()
            personal_feedback_layout.id->activity?.startActivity<FeedbackActivity>()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchCurrentUser()
    }
}