package com.fsyy.listener.ui.homepage

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import cn.leancloud.AVUser
import com.fsyy.listener.logic.Repository

class HomePageViewModel:ViewModel() {
    lateinit var userId:String
    val userIdLiveData=MutableLiveData<String>()
    fun getData(userId:String){
        userIdLiveData.value=userId
    }
    val allDataLiveData=Transformations.switchMap(userIdLiveData){
        Repository.loadAllHomeData(it)
    }
}