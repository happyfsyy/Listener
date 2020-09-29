package com.fsyy.listener.logic.model

import java.io.Serializable
import java.util.*

/**
 * 作为Post,Comment和InnerComment的父类，只是简单的将公因子提取出来
 * @see Post
 * @see Comment
 * @param objectId 是Post和Comment各自的objectId
 * @param date 是createdDate，显示Post和Comment的创建时间
 * @param type 只有三种选择，分别是POST,COMMENT,INNER_COMMENT
 * @param isLike 代表当前用户是否点赞了这个帖子，或者这个评论
 * @param likeObjectId 只有当前用户点赞了，这个属性才有效，代表Like表中的这一项记录的id
 */
open class TreeHole(val objectId: String,val userId: String, val userName: String,
                    val content: String,val date: Date,val type:Int,
                    var likeCount:Int,var isLike:Boolean,var likeObjectId:String):Serializable{
    companion object{
        const val POST=1001
        const val COMMENT=1002
        const val INNER_COMMENT=1003
    }
}