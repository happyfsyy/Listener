package com.fsyy.listener.ui.main

import android.app.Activity
import android.app.Person
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import cn.leancloud.AVUser
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.fsyy.listener.R
import com.fsyy.listener.ui.homepage.HomePageActivity
import com.fsyy.listener.ui.settings.FeedbackActivity
import com.fsyy.listener.ui.settings.ProfileActivity
import com.fsyy.listener.utils.LogUtils
import com.fsyy.listener.utils.extension.startActivity
import com.fsyy.listener.utils.extension.startActivityForResult
import kotlinx.android.synthetic.main.fragment_personal.*

class PersonalFragment:Fragment(),View.OnClickListener{
    private lateinit var viewModel:PersonalViewModel
    companion object{
        const val PROFILE=1
    }
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
        initUserInfo()
        initListener()
        initObserver()
    }
    private fun initUserInfo(){
        LogUtils.e("用户的照片url是${AVUser.currentUser().getString("photoUrl")}")
        Glide.with(this).load(AVUser.currentUser().getString("photoUrl"))
            .apply(RequestOptions.bitmapTransform(CircleCrop()))
            .into(personal_user_photo)
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
        personal_index_layout.setOnClickListener(this)
        personal_feedback_layout.setOnClickListener(this)
    }
    private fun initObserver(){
        viewModel.currentUserLiveData.observe(activity!!){
            val result=it.getOrNull()
            if(result!=null){
                LogUtils.e("已经刷新了AVUser")
                initUserInfo()
            }
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            personal_user_layout.id->startActivityForResult<ProfileActivity>(PROFILE)
            personal_index_layout.id->activity?.startActivity<HomePageActivity>()
            personal_feedback_layout.id->activity?.startActivity<FeedbackActivity>()
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode==Activity.RESULT_OK){
            when(requestCode){
                PROFILE->{
                    val isModified=data?.getBooleanExtra("isModified",false)
                    if(isModified!!){
                        viewModel.fetchCurrentUser()
                    }
                }
            }
        }
    }
}