package com.fsyy.listener.logic.model

import java.util.*

/**
 * @param toUserName 被回复的用户的userName
 * @param floor 在Post中的floor，与对应的Comment的floor相同
 * @param innerFloor 在Comment中的floor，根据评论时间排序也可，这里的InnerFloor，只是为了查询的时候只查询2个
 */
class InnerComment(objectId:String,fromUserId:String,fromUserName:String,val toUserName:String,content:String,date:Date,
                   val floor:Int,val innerFloor:Int,
                   likeCount:Int=0,isLike:Boolean=false,likeObjectId:String="")
    :TreeHole(objectId ,fromUserId ,fromUserName,content,date, INNER_COMMENT,likeCount ,isLike ,likeObjectId)