package com.fsyy.listener.utils.extension

import cn.leancloud.AVObject
import cn.leancloud.AVUser
import com.fsyy.listener.logic.model.Comment
import com.fsyy.listener.logic.model.Post


fun AVObject.toPost():Post{
    val objectId=objectId
    val author=getAVObject("author") as AVUser
    val userId=author.objectId
    //todo 获取用户头像url
    val userName=author.getString("username")
    val content=getString("content")
    val tag=getString("tag")
    //todo 这里的日期可改为updatedAt
    val date=createdAt
    val likeCount=getInt("likeCount")
    val commentCount=getInt("commentCount")
    return Post(objectId,userId,"",userName,content,tag,date,likeCount,commentCount)
}
fun AVObject.toComment():Comment {
    val objectId=objectId
    val author=getAVObject("fromAuthor") as AVUser
    val userId=author.objectId
    //todo 用户头像
    val userName=author.getString("username")
    val likeCount=getInt("likeCount")
    val content=getString("content")
    val date=createdAt
    return Comment(objectId,userId,"",userName,likeCount,content,date)
}
fun valuesOfAVObject(className: String,map:Map<String,Any?>)=AVObject(className).apply {
    for((key,value) in map){
        put(key,value)
    }
}
