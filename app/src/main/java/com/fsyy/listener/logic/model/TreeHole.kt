package com.fsyy.listener.logic.model

import java.io.Serializable
import java.util.*

//todo 还有一个isLike需要提取出来
open class TreeHole(val objectId: String,val userId: String,val photoUrl: String,
                    val userName: String,var likeCount: Int,val content: String,val date: Date,val type:Int):Serializable{
    companion object{
        const val POST=1
        const val COMMENT=2
    }
}