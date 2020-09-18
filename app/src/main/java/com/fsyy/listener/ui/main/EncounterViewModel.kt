package com.fsyy.listener.ui.main

import androidx.lifecycle.*
import cn.leancloud.AVObject
import com.fsyy.listener.logic.Repository
import com.fsyy.listener.logic.model.Post
import com.fsyy.listener.logic.model.PostLoadMoreParams
import com.fsyy.listener.utils.LogUtils

class EncounterViewModel :ViewModel(){
    /**
     * 所有Post的List
     */
    val postList=ArrayList<Post>()

    /**
     * 单纯记录上拉加载了多少次
     */
    val loadCountLiveData:LiveData<Int>
        get()=_loadCountLiveData
    private val _loadCountLiveData=MutableLiveData<Int>().apply { value=0 }
    fun clearLoadCount(){
        _loadCountLiveData.value=0
    }
    fun plusLoadCount(){
        val loadCount=_loadCountLiveData.value?:0
        _loadCountLiveData.value=loadCount+1
    }

    /**
     * 加载帖子方法的参数limit
     */
    private val limitLiveData=MutableLiveData<Int>()
    fun loadPosts(limit:Int){
        limitLiveData.value=limit
    }
    val postsLiveData:LiveData<Result<List<AVObject>>> =Transformations.switchMap(limitLiveData){
        LogUtils.e("执行post的SwitchMap")
        Repository.loadPosts(it)
    }

    /**
     * 加载更多帖子的参数，只是将limit和loadCount封装在一个类中而已
     */
    private val paramsLiveData=MutableLiveData<PostLoadMoreParams>()
    fun loadMorePosts(limit: Int,loadCount:Int){
        paramsLiveData.value= PostLoadMoreParams(limit,loadCount)
    }
    val morePostsLiveData:LiveData<Result<List<AVObject>>> =Transformations.switchMap(paramsLiveData){
        Repository.loadMorePosts(it.limit,it.loadCount)
    }

    /**
     * 加载
     */
    private val paramPostsLiveData=MutableLiveData<List<AVObject>>()
    fun loadLikes(paramPosts:List<AVObject>){
        paramPostsLiveData.value=paramPosts
    }
    val likesLiveData:LiveData<Result<List<AVObject>>> =Transformations.switchMap(paramPostsLiveData){
        LogUtils.e("执行like的SwitchMap")
        Repository.loadLikes(it)
    }
}
