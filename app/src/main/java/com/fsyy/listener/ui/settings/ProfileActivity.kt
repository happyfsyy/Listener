package com.fsyy.listener.ui.settings

import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.AnimatorListenerAdapter
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cn.leancloud.AVUser
import com.fsyy.listener.R
import com.fsyy.listener.utils.LogUtils
import com.fsyy.listener.utils.extension.showToast
import kotlinx.android.synthetic.main.activity_profile.*


class ProfileActivity : AppCompatActivity(),View.OnClickListener{
    companion object{
        const val USERNAME=1
        const val SIGN=2
    }
    private val viewModel by lazy { ViewModelProvider(this).get(ProfileViewModel::class.java) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        initViews()
        initListener()
    }
    private fun initViews(){
        profile_username_text.text=AVUser.currentUser().username
        LogUtils.e("编辑页中，用户名是：${AVUser.currentUser().username}")
        val intro=AVUser.currentUser().getString("intro")
        if(intro=="")
            profile_sign_text.text=getString(R.string.profile_signature)
        else
            profile_sign_text.text=intro
    }
    private fun initListener(){
        profile_photo_layout.setOnClickListener(this)
        profile_username_layout.setOnClickListener(this)
        profile_sign_layout.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            profile_photo_layout.id->showPopupWindow()
            profile_username_layout.id -> showDialog(getString(R.string.profile_edit_username),
                profile_username_text.text.toString(), USERNAME)
            profile_sign_layout.id -> showDialog(getString(R.string.profile_edit_sign),
                profile_sign_text.text.toString(), SIGN)
            viewModel.takePhoto.id->"拍照啦".showToast()
            viewModel.albums.id->"取照片".showToast()
            viewModel.cancel.id -> {
                val outAnimator = AnimatorInflater.loadAnimator(this, R.animator.popup_item_out)
                outAnimator.setTarget(viewModel.popupView)
                outAnimator.start()
                outAnimator.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?, isReverse: Boolean) {
                        viewModel.popupWindow.dismiss()
                    }
                })
            }
        }
    }
    private fun showPopupWindow(){
        viewModel.takePhoto.setOnClickListener(this)
        viewModel.albums.setOnClickListener(this)
        viewModel.cancel.setOnClickListener(this)
        val inAnimator=AnimatorInflater.loadAnimator(this,R.animator.popup_item_in)
        inAnimator.setTarget(viewModel.popupView)
        inAnimator.start()
        viewModel.popupWindow=PopupWindow(viewModel.popupView,ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT).apply {
            isFocusable=true
            isOutsideTouchable=false
            animationStyle=R.style.popupAnimation
            setBackgroundDrawable(ColorDrawable(Color.parseColor("#55000000")))
            isClippingEnabled=false
            showAtLocation(window.decorView,Gravity.TOP,0,0)
        }
    }
    private fun showDialog(header: String, content: String, type: Int){
        val view=LayoutInflater.from(this).inflate(R.layout.profile_dialog_view, null)
        val headerText:TextView=view.findViewById(R.id.profile_dialog_header)
        val contentEdit:EditText=view.findViewById(R.id.profile_dialog_edit)
        contentEdit.apply {
            requestFocus()
            setText(content)
            setSelection(content.length)
        }
        headerText.text=header
        AlertDialog.Builder(this,R.style.AlertDialog).apply {
            setView(view)
            setCancelable(false)
            setPositiveButton(getString(R.string.dialog_confirm)){ dialog, _->
                when (val contentText=contentEdit.text.toString().trim()) {
                    "" -> {
                        if (type == USERNAME)
                            getString(R.string.profile_username_toast).showToast()
                        else
                            getString(R.string.profile_sign_toast).showToast()
                    }
                    content -> dialog.dismiss()
                    else -> {
                        if(type== USERNAME)
                            saveUserName(contentText)
                        else
                            saveSign(contentText)
                    }
                }
            }
            setNegativeButton(getString(R.string.dialog_cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            show()
        }

    }
    private fun saveUserName(contentText: String){
        profile_username_text.text=contentText
        AVUser.currentUser().put("username",contentText)
        AVUser.currentUser().saveEventually()
        //todo 需不需要显示进度条
    }
    private fun saveSign(contentText: String){
        profile_sign_text.text=contentText
        AVUser.currentUser().put("intro",contentText)
        AVUser.currentUser().saveEventually()
    }

}