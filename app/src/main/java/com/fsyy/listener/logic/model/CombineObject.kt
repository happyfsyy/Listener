package com.fsyy.listener.logic.model

import cn.leancloud.AVObject
import cn.leancloud.AVUser

/**
 * 查询某个帖子获得的所有的评论，包括帖子评论以及每个评论下的楼中楼（只查询2条）
 */
class AllComments(val commentList:List<AVObject>,val innerCommentList:List<AVObject>)

/**
 * 对应主页界面的所有数据，这几个查询可以并行同时查询
 */
class AllHomeData(val commentCount:Int,val commentList: List<AVObject>,val postCount:Int,val postList: List<AVObject>)