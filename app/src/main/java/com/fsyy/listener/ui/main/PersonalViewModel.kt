package com.fsyy.listener.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.fsyy.listener.logic.Repository

class PersonalViewModel :ViewModel(){
    private val currentUserParamsLiveData=MutableLiveData<Any?>()
    val currentUserLiveData=Transformations.switchMap(currentUserParamsLiveData){
        Repository.fetchCurrentUser()
    }
    fun fetchCurrentUser(){
        currentUserParamsLiveData.value=currentUserParamsLiveData.value
    }
}