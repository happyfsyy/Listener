package com.fsyy.listener.utils.extension

import cn.leancloud.AVObject
import cn.leancloud.AVUser
import com.fsyy.listener.logic.model.Comment
import com.fsyy.listener.logic.model.InnerComment
import com.fsyy.listener.logic.model.Post


fun AVObject.toPost():Post{
    val objectId=objectId
    val author=getAVObject("author") as AVUser
    val userId=author.objectId
    //todo 获取用户头像url
    val userName=author.getString("username")
    val content=getString("content")
    val tag=getString("tag")
    val date=createdAt
    val likeCount=getInt("likeCount")
    val commentCount=getInt("commentCount")
    return Post(objectId,userId,"",userName,content,date,likeCount,commentCount,tag)
}
fun AVObject.toComment():Comment {
    val objectId=objectId
    val author=getAVObject("fromAuthor") as AVUser
    val userId=author.objectId
    //todo 用户头像
    val userName=author.username
    val likeCount=getInt("likeCount")
    val content=getString("content")
    val date=createdAt
    val commentCount=getInt("commentCount")
    val floor=getInt("floor")
    return Comment(objectId,userId,"",userName,likeCount,content,date,commentCount,floor)
}
fun AVObject.toInnerComment():InnerComment{
    val objectId=objectId
    val fromUser=getAVObject<AVObject>("fromAuthor") as AVUser
    val fromUserId=fromUser.objectId
    val fromUserName=fromUser.username
    val toUser=getAVObject<AVObject>("toAuthor") as AVUser
    val toUserName=toUser.username
    val content=getString("content")
    val date=createdAt
    val floor=getInt("floor")
    val innerFloor=getInt("innerFloor")
    return InnerComment(objectId,fromUserId,fromUserName,toUserName,content,date,floor,innerFloor)
}
fun valuesOfAVObject(className: String,map:Map<String,Any?>)=AVObject(className).apply {
    for((key,value) in map){
        put(key,value)
    }
}

