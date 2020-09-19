package com.fsyy.listener.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import cn.leancloud.AVObject
import com.fsyy.listener.logic.Repository
import com.fsyy.listener.logic.model.CommentLoadMoreParams
import com.fsyy.listener.logic.model.Post
import com.fsyy.listener.logic.model.TreeHole
import com.fsyy.listener.logic.network.Network
import com.fsyy.listener.utils.LogUtils

class DetailViewModel:ViewModel() {
    val dataList=ArrayList<TreeHole>()
    lateinit var post: Post

    /**
     * 记录上拉记载了多少次,从1开始计数
     */
    val loadCountLiveData: LiveData<Int>
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
     * 根据帖子的objectId，和查询的数量，获取Comment，只需要管擦commentLiveData即可
     */
    private val loadLiveData=MutableLiveData<CommentLoadMoreParams>()
    val commentLiveData=Transformations.switchMap(loadLiveData){
        LogUtils.e("执行DetailViewModel的Transformation")
        Repository.loadComment(it.limit,it.loadCount,it.objectId)
    }
    fun loadComment(limit:Int,objectId:String,loadCount:Int){
        val params=CommentLoadMoreParams(objectId,limit,loadCount)
        loadLiveData.value=params
    }
    /**
     * 提交评论的时候，根据postId获取最新的Post的commentCount，这样才可以得到floor
     */
    private val postIdLiveData=MutableLiveData<String>()
    fun fetchNewPost(postId:String){
        postIdLiveData.value=postId
    }
    val newPostLiveData=Transformations.switchMap(postIdLiveData){
        Repository.fetchNewPost(it)
    }

    /**
     * 根据获得评论，去查询对应的like表
     */
    private val commentsLiveData=MutableLiveData<List<AVObject>>()
    fun loadLikes(comments:List<AVObject>){
        commentsLiveData.value=comments
    }
    val likesLiveData=Transformations.switchMap(commentsLiveData){
        Repository.loadCommentLikes(it)
    }

    fun publishComment(map:Map<String,Any?>,success: (avObject: AVObject) -> Unit)=Network.publishComment(map,success)
}