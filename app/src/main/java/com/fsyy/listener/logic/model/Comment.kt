package com.fsyy.listener.logic.model

import java.util.*

/**
 * @param floor 代表的是Comment在Post表中的第几楼，虽然不在界面中显示。这个属性是为了楼中楼设立的，楼中楼与评论的floor是相同的，都是同一层。
 * @param isInner 默认是false，代表我的Comment都只是Post的Comment，不是Comment的Comment
 * @param innerFloor 默认是0，因为只有isInner为true的时候，我才会去用innerFloor这个属性，其他时候并不管它
 */
class Comment(objectId:String, userId:String, photoUrl:String, userName:String,
              likeCount:Int, content:String, date: Date,
              commentCount:Int,
              val floor:Int,val isInner:Boolean=false,val innerFloor:Int=0)
    :TreeHole(objectId,userId,photoUrl,userName,likeCount,content,date,commentCount,COMMENT)