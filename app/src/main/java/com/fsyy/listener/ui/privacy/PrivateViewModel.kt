package com.fsyy.listener.ui.privacy

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.fsyy.listener.logic.Repository
import com.fsyy.listener.logic.model.PrivatePost

class PrivateViewModel:ViewModel() {
    val postList=ArrayList<PrivatePost>()

    private val userIdLiVeData=MutableLiveData<String>()
    fun getPrivatePosts(userId:String){
        userIdLiVeData.value=userId
    }
    val postsLiveData=Transformations.switchMap(userIdLiVeData){
        Repository.getPrivatePosts(it)
    }
}