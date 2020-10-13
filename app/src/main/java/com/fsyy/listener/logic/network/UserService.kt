package com.fsyy.listener.logic.network

import cn.leancloud.AVQuery
import cn.leancloud.AVUser

object UserService {
    fun genUserQuery(objectId:String): AVQuery<AVUser> =AVUser.getQuery().apply {
        whereEqualTo("objectId",objectId)
    }
}