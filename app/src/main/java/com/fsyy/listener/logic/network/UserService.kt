package com.fsyy.listener.logic.network

import cn.leancloud.AVUser

object UserService {
    fun genUserQuery(objectId:String)=AVUser.getQuery().apply {
        whereEqualTo("objectId",objectId)
    }
}