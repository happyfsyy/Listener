package com.fsyy.listener.logic.network

import cn.leancloud.AVObject
import cn.leancloud.AVQuery
import cn.leancloud.AVUser
import com.fsyy.listener.utils.LogUtils
import com.fsyy.listener.utils.extension.valuesOfAVObject

object CommentService {
    /**
     * @param objectId 帖子的ObjectId
     */
    fun genLoadQuery(limitNum:Int,loadCount:Int,objectId:String)=AVQuery<AVObject>("Comment").apply {
        LogUtils.e("执行CommentService的loadQuery")
        limit=limitNum
        whereEqualTo("post",AVObject.createWithoutData("Post",objectId))
        whereEqualTo("isInner",false)
        LogUtils.e("genLoadQuery()中的loadCount是$loadCount,跳过${limitNum*loadCount}条数据")
        skip(limitNum*loadCount)
        include("fromAuthor")
        orderByAscending("floor")
    }

    fun genComment(map:Map<String,Any?>)= valuesOfAVObject("Comment",map)

    fun genLoadInnerQuery(limitNum: Int,loadCount: Int,objectId: String)=AVQuery<AVObject>("Comment").apply {
        whereEqualTo("post",AVObject.createWithoutData("Post",objectId))
        whereEqualTo("isInner",true)
        //floor范围是limitNum*loadCount,到limit*(loadCount+1)
        //innerFloor范围是1和2
        whereGreaterThan("floor",limitNum*loadCount)
        whereLessThanOrEqualTo("floor",limitNum*(loadCount+1))
        whereContainedIn("innerFloor", listOf(1,2))
        include("fromAuthor")
        include("toAuthor")
        orderByAscending("floor")
        orderByAscending("innerFloor")
    }
}