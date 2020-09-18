package com.fsyy.listener.logic

import androidx.lifecycle.liveData
import cn.leancloud.AVObject
import com.fsyy.listener.R
import com.fsyy.listener.logic.network.Network
import com.fsyy.listener.ui.MyApplication
import com.fsyy.listener.utils.LogUtils
import com.fsyy.listener.utils.extension.showToast
import kotlinx.coroutines.Dispatchers
import java.lang.Exception
import kotlin.coroutines.CoroutineContext

object Repository {
    fun publishPost(map:Map<String,Any?>,success:(avObject:AVObject)->Unit)=Network.publishPost(map,success)

    fun loadPosts(limit: Int)= query(Dispatchers.Main){
        val response=Network.loadPosts(limit)
        Result.success(response)
    }
    fun loadMorePosts(limit: Int,loadCount:Int)= query(Dispatchers.Main){
        val response=Network.loadMorePosts(limit,loadCount)
        Result.success(response)
    }
    fun loadLikes(post:List<AVObject>)= query(Dispatchers.Main){
        val response=Network.loadLikes(post)
        Result.success(response)
    }


    fun loadComment(limit: Int,objectId:String)= query(Dispatchers.Main){
        val response=Network.loadComment(limit,objectId)
        Result.success(response)
    }

    /**
     * 这里只是简化了方法，并且对查询结果进行了LiveData的包装
     */
    private fun <T> query(coroutineContext: CoroutineContext,block:suspend ()->Result<T>)= liveData<Result<T>>(coroutineContext) {
        LogUtils.e("执行Repository")
        val result=try{
            block()
        }catch (e:Exception){
            MyApplication.context.getString(R.string.failure_text).showToast()
            LogUtils.e("Repository: ${e.printStackTrace()}")
            Result.failure<T>(e)
        }
        emit(result)
    }
}