package com.fsyy.listener.logic.model

import java.util.*
import kotlin.collections.ArrayList

/**
 * 帖子的评论，为DetailActivity设计的
 * @param floor 代表的是Comment在Post表中的第几楼，虽然不在界面中显示。这个属性是为了楼中楼设立的，楼中楼与评论的floor是相同的，都是同一层。
 * @param innerCommentList 指的是innerComment的内容，每个innerComment描述为一个textView的内容，最多显示两个textView
 * @param post 专为'个人主页'页面设计的，需要点击之后跳转到详情页面
 * @param innerText 专为'个人主页'页面设计，代表内部显示的文字
 */
class Comment(objectId:String, userId:String, val photoUrl:String, userName:String,
              likeCount:Int, content:String, date: Date, var commentCount:Int,
              val floor:Int, val isInner:Boolean=false,isLike:Boolean=false,likeObjectId:String="",
              val innerCommentList:ArrayList<InnerComment> =ArrayList(),var post: Post?=null,var innerText:String)
    :TreeHole(objectId,userId,userName,content,date,COMMENT,likeCount,isLike,likeObjectId)



