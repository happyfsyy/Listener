package com.fsyy.listener.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.fsyy.listener.R
import com.fsyy.listener.ui.settings.FeedbackActivity
import com.fsyy.listener.ui.settings.ProfileActivity
import com.fsyy.listener.utils.extension.startActivity
import kotlinx.android.synthetic.main.fragment_personal.*

class PersonalFragment:Fragment(),View.OnClickListener{
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_personal,container,false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initListener()
    }

    private fun initListener(){
        personal_user_layout.setOnClickListener(this)
        personal_feedback_layout.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            personal_user_layout.id->activity?.startActivity<ProfileActivity>()
            personal_feedback_layout.id->activity?.startActivity<FeedbackActivity>()
        }
    }
}