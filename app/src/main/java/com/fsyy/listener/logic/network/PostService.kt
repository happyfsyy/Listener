package com.fsyy.listener.logic.network

import cn.leancloud.AVObject
import cn.leancloud.AVQuery
import com.fsyy.listener.utils.extension.valuesOfAVObject

object PostService {
    fun genLoadQuery(limitNum:Int):AVQuery<AVObject> = AVQuery<AVObject>("Post").apply {
        //todo 根据updatedAt将Post进行排序，但是有个问题，每次点赞取消赞，都会更新updatedAt，我想设置为只有评论才更新updatedAt
        limit=limitNum
        include("author")
        orderByDescending("updatedAt")
    }
    /**
     * @param limitNum 加载的帖子数量，这里与预加载的数量保持一致。例如最开始加载10条，之后每次上拉也是10条
     * @param loadCount 第几次加载，1开始计数
     */
    fun genLoadMoreQuery(limitNum: Int,loadCount:Int):AVQuery<AVObject> =AVQuery<AVObject>("Post").apply {
        //todo 这里可以不用skip，直接获取list的最后一个数据的updatedAt，然后orderByDescending
        limit=limitNum
        include("author")
        orderByDescending("updatedAt")
        skip(limitNum*loadCount)
    }
    fun genPost(map: Map<String,Any?>)= valuesOfAVObject("Post",map)

    /**
     * 查询所有的帖子数量
     */
    fun genAllPostCountQuery(userId:String)=AVQuery<AVObject>("Post").apply {
        whereEqualTo("author",AVObject.createWithoutData("_User",userId))
    }
    fun genRecentPostQuery(userId: String)=AVQuery<AVObject>("Post").apply {
        include("author")
        limit=3
        orderByDescending("createdAt")
    }
}