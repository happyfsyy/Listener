package com.fsyy.listener.logic.model

import java.io.Serializable
import java.util.*

/**
 * 作为Post和Comment的父类，只是简单的将公因子提取出来
 * @param objectId 是Post和Comment各自的objectId
 * @param date 是createdDate，显示Post和Comment的创建时间
 * @param type 只有两种选择，分别是POST和COMMENT
 * @param commentCount 对于Post来说代表的是帖子的评论数量。对于Comment来说，代表的是Comment的楼中楼数量。
 * @param isLike 代表当前用户是否点赞了这个帖子，或者这个评论
 * @param likeObjectId 只有当前用户点赞了，这个属性才有效，代表Like表中的这一项记录的id
 */
open class TreeHole(val objectId: String,val userId: String,val photoUrl: String, val userName: String,var likeCount: Int,
                    val content: String,val date: Date,var commentCount:Int,val type:Int,
                    var isLike:Boolean=false,var likeObjectId:String=""):Serializable{
    companion object{
        const val POST=1
        const val COMMENT=2
    }
}