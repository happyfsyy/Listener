package com.fsyy.listener.logic.model

import java.util.*

//todo 还有isInner，Floor
class Comment(objectId:String, userId:String, photoUrl:String, userName:String, likeCount:Int, content:String, date: Date)
    :TreeHole(objectId,userId,photoUrl,userName,likeCount,content,date,COMMENT)