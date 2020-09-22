package com.fsyy.listener.logic.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable
import java.util.*

/**
 * 这里的post并不对应服务器数据库中的post，只是在本地数据库方便使用
 * 对应的是界面上的元素
 */
class Post(objectId:String, userId:String, val photoUrl:String, userName:String, content:String,
            date: Date, likeCount:Int, var commentCount:Int,
            val tag:String,isLike:Boolean=false,likeObjectId:String="")
            :TreeHole(objectId,userId,userName, content, date, POST,likeCount,isLike,likeObjectId), Serializable{

    override fun toString(): String {
        return "objectid=$objectId,userid=$userId,username=$userName," +
                "content=$content,tag=$tag,likecount=$likeCount,commentcount=$commentCount"
    }

}

