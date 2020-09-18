package com.fsyy.listener.logic.network

import cn.leancloud.AVObject
import cn.leancloud.AVQuery
import com.fsyy.listener.R
import com.fsyy.listener.ui.MyApplication
import com.fsyy.listener.utils.LogUtils
import com.fsyy.listener.utils.extension.showToast
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object Network {
    fun publishPost(map: Map<String,Any?>,success: (avObject: AVObject) -> Unit)=PostService.genPost(map).saveAV(success)

    suspend fun loadPosts(limit:Int):List<AVObject> =PostService.genLoadQuery(limit).queryAV()
    suspend fun loadMorePosts(limit: Int,loadCount:Int):List<AVObject> =PostService.genLoadMoreQuery(limit,loadCount).queryAV()
    suspend fun loadLikes(posts:List<AVObject>)=LikeService.genPostLikeQuery(posts).queryAV()

    suspend fun loadComment(limit: Int,objectId:String)=CommentService.genLoadQuery(limit,objectId).queryAV()


    private suspend fun AVQuery<AVObject>.queryAV():List<AVObject>{
        LogUtils.e("执行Network的queryAV")
        return suspendCoroutine<List<AVObject>>{
            findInBackground().subscribe(object : Observer<List<AVObject>> {
                override fun onSubscribe(d: Disposable) {
                }
                override fun onNext(t: List<AVObject>) {
                    it.resume(t)
                }
                override fun onError(e: Throwable) {
                    //todo 当前网络状况不佳，异常也可以这里处理，但是就变成和回调一样了
                    it.resumeWithException(e)
                }
                override fun onComplete() {
                }
            })
        }
    }
    private fun AVObject.saveAV(success:(avObject:AVObject)->Unit)=saveInBackground().subscribe(object :Observer<AVObject>{
        override fun onSubscribe(d: Disposable) {
        }
        override fun onNext(t: AVObject) {
            success(t)
        }
        override fun onError(e: Throwable) {
            //todo 网络状况不佳，发布出现异常
            MyApplication.context.resources.getString(R.string.failure_text).showToast()
            LogUtils.e("Network: ${e.stackTraceToString()}")
        }
        override fun onComplete() {
        }
    })

}