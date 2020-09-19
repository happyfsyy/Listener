package com.fsyy.listener.logic.network

import cn.leancloud.AVObject
import cn.leancloud.AVQuery
import cn.leancloud.AVUser
import com.fsyy.listener.utils.LogUtils

object LikeService {
    fun genPostLikeQuery(posts:List<AVObject>)=AVQuery<AVObject>("Like").apply {
        LogUtils.e("执行like的查询")
        whereContainedIn("post",posts)
        whereEqualTo("user",AVUser.currentUser())
    }
    fun genCommentLikeQuery(comments:List<AVObject>)=AVQuery<AVObject>("Like").apply {
        whereContainedIn("comment",comments)
        whereEqualTo("user",AVUser.currentUser())
    }
}