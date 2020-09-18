package com.fsyy.listener.ui.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.fsyy.listener.logic.Repository
import com.fsyy.listener.logic.model.CommentLoadMoreParams
import com.fsyy.listener.logic.model.Post
import com.fsyy.listener.logic.model.TreeHole
import com.fsyy.listener.utils.LogUtils

class DetailViewModel:ViewModel() {
    private val loadLiveData=MutableLiveData<CommentLoadMoreParams>()
    val dataList=ArrayList<TreeHole>()
    lateinit var post: Post
    val commentLiveData=Transformations.switchMap(loadLiveData){
        LogUtils.e("执行DetailViewModel的Transformation")
        Repository.loadComment(it.limit,it.objectId)
    }

    fun loadComment(limit:Int,objectId:String){
        val params=CommentLoadMoreParams(objectId,limit,0)
        loadLiveData.value=params
    }
}