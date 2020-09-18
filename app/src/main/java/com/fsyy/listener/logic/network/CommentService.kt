package com.fsyy.listener.logic.network

import cn.leancloud.AVObject
import cn.leancloud.AVQuery
import cn.leancloud.AVUser
import com.fsyy.listener.utils.LogUtils

object CommentService {
    /**
     * @param objectId 帖子的ObjectId
     */
    fun genLoadQuery(limitNum:Int,objectId:String)=AVQuery<AVObject>("Comment").apply {
        LogUtils.e("执行CommentService的loadQuery")
        //todo 这里先不考虑楼中楼的情况，只将评论按照createdAt升序排列
        limit=limitNum
        whereEqualTo("post",AVObject.createWithoutData("Post",objectId))
        include("fromAuthor")
        orderByAscending("createdAt")
    }

    fun genLoadMoreQuery(limitNum: Int,loadCount:Int,objectId:String)=AVQuery<AVObject>("Comment").apply {
        limit=limitNum
        whereEqualTo("post",AVObject.createWithoutData("Post",objectId))
        skip(limitNum*loadCount)
        include("fromAuthor")
        orderByAscending("createdAt")
    }

    fun genComment(){

    }
}