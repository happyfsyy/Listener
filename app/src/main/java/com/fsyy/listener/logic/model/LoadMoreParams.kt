package com.fsyy.listener.logic.model

/**
 * 专为上拉加载设置的类
 * @param limit 加载的帖子数量，这里与预加载的数量保持一致。例如最开始加载10条，之后每次上拉也是10条
 * @param loadCount 第几次加载，1开始计数
 */
data class PostLoadMoreParams(val limit: Int,val loadCount:Int)

/**
 * @param objectId post的objectId
 */
data class CommentLoadMoreParams(val objectId:String,val limit: Int,val loadCount: Int)