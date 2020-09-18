package com.fsyy.listener.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cn.leancloud.AVObject
import com.fsyy.listener.logic.Repository

class LonelyViewModel :ViewModel(){
    companion object{
        const val OPEN=0
        const val PRIVATE=1
        const val BURIED=2
    }
    val type:LiveData<Int>
        get() = _type
    private val _type=MutableLiveData<Int>()
    init {
        _type.value= OPEN
    }
    fun open(){
        _type.value= OPEN
    }
    fun private(){
        _type.value= PRIVATE
    }
    fun buried(){
        _type.value= BURIED
    }
    fun publishPost(map:Map<String,Any?>,success:(avObject: AVObject)->Unit)=Repository.publishPost(map,success)

}