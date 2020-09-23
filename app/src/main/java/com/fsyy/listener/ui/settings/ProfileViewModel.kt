package com.fsyy.listener.ui.settings

import android.view.LayoutInflater
import android.widget.Button
import android.widget.PopupWindow
import androidx.lifecycle.ViewModel
import com.fsyy.listener.R
import com.fsyy.listener.ui.MyApplication

class ProfileViewModel:ViewModel() {
    val popupView by lazy { LayoutInflater.from(MyApplication.context).inflate(R.layout.popup_photo_item,null)}
    val takePhoto: Button by lazy {  popupView.findViewById(R.id.profile_takePhoto)}
    val albums: Button by lazy {  popupView.findViewById(R.id.profile_albums) }
    val cancel: Button by lazy {  popupView.findViewById(R.id.profile_cancel)}
    lateinit var popupWindow:PopupWindow

}