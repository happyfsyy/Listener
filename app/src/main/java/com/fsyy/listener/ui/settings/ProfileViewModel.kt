package com.fsyy.listener.ui.settings

import android.net.Uri
import android.view.LayoutInflater
import android.widget.Button
import android.widget.PopupWindow
import androidx.lifecycle.ViewModel
import cn.leancloud.AVFile
import cn.leancloud.AVObject
import com.fsyy.listener.R
import com.fsyy.listener.logic.network.Network
import com.fsyy.listener.ui.MyApplication
import java.io.File

class ProfileViewModel:ViewModel() {
    val popupView by lazy { LayoutInflater.from(MyApplication.context).inflate(R.layout.popup_photo_item,null)}
    val takePhoto: Button by lazy {  popupView.findViewById(R.id.profile_takePhoto)}
    val albums: Button by lazy {  popupView.findViewById(R.id.profile_albums) }
    val cancel: Button by lazy {  popupView.findViewById(R.id.profile_cancel)}
    var isModified=false
    lateinit var popupWindow:PopupWindow
    lateinit var outputImage:File
    lateinit var imageUri:Uri
    fun uploadPhoto(path:String,success:(avObject:AVObject)->Unit)=Network.uploadPhoto(path,success)
}