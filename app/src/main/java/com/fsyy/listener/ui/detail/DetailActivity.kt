package com.fsyy.listener.ui.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ViewConfiguration
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.leancloud.AVObject
import cn.leancloud.AVUser
import com.fsyy.listener.R
import com.fsyy.listener.logic.model.Comment
import com.fsyy.listener.logic.model.InnerComment
import com.fsyy.listener.logic.model.Post
import com.fsyy.listener.logic.model.TreeHole
import com.fsyy.listener.logic.network.Network
import com.fsyy.listener.utils.LogUtils
import com.fsyy.listener.utils.SoftKeyboardUtils
import com.fsyy.listener.utils.extension.showToast
import com.fsyy.listener.utils.extension.toComment
import com.fsyy.listener.utils.extension.toInnerComment
import com.fsyy.listener.utils.extension.valuesOfAVObject
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.fragment_encounter.*
import java.util.*
import kotlin.collections.ArrayList

class DetailActivity : AppCompatActivity() {
    private lateinit var adapter: DetailAdapter
    private val viewModel by lazy { ViewModelProvider(this).get(DetailViewModel::class.java) }
    private val limit=2
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        LogUtils.e("Detail：intent.get()是${intent.getSerializableExtra("post") as Post}")
        viewModel.post=intent.getSerializableExtra("post") as Post
        LogUtils.e(viewModel.post.toString())
        if (viewModel.post.commentCount>0){
            loadComment()
        }
        initObserver()
        initAdapter()
        initRecyclerView()
        initListener()
        refreshData()
    }
    private fun initObserver(){
//        viewModel.commentLiveData.observe(this){
//            val commentAVList=it.getOrNull()
//            LogUtils.e("评论的size大小是${commentAVList?.size?:0}")
//            if(commentAVList!=null){
//                for(avObject in commentAVList){
//                    viewModel.dataList.add(avObject.toComment())
//                }
//                viewModel.loadLikes(commentAVList)
//            }
//        }
        viewModel.allCommentsLiveData.observe(this){
            val allComments=it.getOrNull()
            if(allComments!=null){
                val commentAVList=allComments.commentList
                val innerCommentAVList=allComments.innerCommentList
                //TODO 加载comment，将innerCmmentList数据加入到Comment类中，加载like
                for(avObject in commentAVList){
                    viewModel.dataList.add(avObject.toComment())
                }
                viewModel.loadLikes(commentAVList)
                val innerCommentList=ArrayList<InnerComment>()
                for(avObject in innerCommentAVList){
                    innerCommentList.add(avObject.toInnerComment())
                }
                for(innerComment in innerCommentList){
                    val floor=innerComment.floor
                    val range=1 until viewModel.dataList.size
                    for(i in range){
                        val comment=viewModel.dataList[i] as Comment
                        if(comment.floor==floor){
                            //todo 这里的comment是viewmodel.dataList[i]么？因为已经强制转化了，是引用传递么
                            comment.innerCommentList.add(innerComment)
                            break
                        }
                    }
                }

            }
        }
        viewModel.likesLiveData.observe(this){
            val likes=it.getOrNull()
            detail_swipe_refresh.isRefreshing=false
            if(likes!=null){
                //根据objectId，查询对应的comment，就将它的isLike设置为true
                val startPos=limit*viewModel.loadCountLiveData.value!!+1
                val range= startPos until viewModel.dataList.size
                for(like in likes){
                    val commentId=like.getAVObject<AVObject>("comment").objectId
                    //todo 将这里的范围根据loadCount重新设置，adapter的时候，就可以只notifyItemRangeChanged

                    for(i in range){
                        if(commentId==viewModel.dataList[i].objectId){
                            viewModel.dataList[i].isLike=true
                            viewModel.dataList[i].likeObjectId=like.objectId
                        }
                    }
                }
                adapter.notifyItemRangeChanged(startPos,viewModel.dataList.size-startPos)
            }
        }

        viewModel.newPostLiveData.observe(this){ result ->
            val newPost=result.getOrNull()
            if(newPost!=null){
                val commentCount=newPost.getInt("commentCount")
                val floor=commentCount+1
               viewModel.publishComment(mapOf("fromAuthor" to AVUser.currentUser(),
                   "content" to detail_edit.text.toString(),"post" to AVObject.createWithoutData("Post",viewModel.post.objectId),
                   "floor" to floor)){comment->
                   //todo 测试如果不是空的AVObject，而是带有数据的AVObject，例如likeCount=0的，然后increment之后save，这期间被人点赞了，save之后，那么服务器的likeCount是多少
                   val emptyPost=AVObject.createWithoutData("Post",viewModel.post.objectId)
                   emptyPost.increment("commentCount")
                   emptyPost.saveEventually()
                   //todo 监测saveInBackground之后的数据包括author的username吗
                   LogUtils.e("我要将发布的评论转化为本地的Comment了，会不会出错，因为没include author")
                   viewModel.dataList.add(comment.toComment())
                   adapter.notifyDataSetChanged()
                   clearUI()
                   "发布成功了".showToast()
               }
            }
        }
    }
    private fun loadComment(){
        LogUtils.e("loadComment()中的loadCount是0")
        viewModel.loadComment(limit,viewModel.post.objectId,0)
    }
    private fun loadMoreComment(){
        LogUtils.e("加载更多评论")
        viewModel.plusLoadCount()
        viewModel.loadComment(limit,viewModel.post.objectId,viewModel.loadCountLiveData.value!!)
    }
    private fun initAdapter(){
        viewModel.dataList.add(viewModel.post)
        adapter= DetailAdapter(viewModel.dataList)
        adapter.setOnPostLikeClickListener { _, _ ->
            updateUI(0,TreeHole.POST)
        }
        adapter.setOnCommentLikeClickListener { _, pos ->
            updateUI(pos,TreeHole.COMMENT)
        }
    }
    private fun updateUI(pos:Int,type:Int){
        val isLike=viewModel.dataList[pos].isLike
        if(isLike){
            viewModel.dataList[pos].isLike=false
            viewModel.dataList[pos].likeCount--
            AVObject.createWithoutData("Like",viewModel.dataList[pos].likeObjectId).apply {
                deleteEventually()
            }
            val treeHoleAV:AVObject = if(type==TreeHole.POST)
                AVObject.createWithoutData("Post",viewModel.dataList[pos].objectId)
            else
                AVObject.createWithoutData("Comment",viewModel.dataList[pos].objectId)
            treeHoleAV.decrement("likeCount")
            treeHoleAV.saveEventually()
        }else{
            viewModel.dataList[pos].isLike=true
            viewModel.dataList[pos].likeCount++
            if(type==TreeHole.COMMENT){
                AVObject("Like").apply {
                    put("user",AVUser.currentUser())
                    put("comment",AVObject.createWithoutData("Comment",viewModel.dataList[pos].objectId))
                    saveEventually()
                }
                AVObject.createWithoutData("Comment",viewModel.dataList[pos].objectId).apply {
                    increment("likeCount")
                    saveEventually()
                }
            }else{
                AVObject("Like").apply {
                    put("user",AVUser.currentUser())
                    put("post",AVObject.createWithoutData("Post",viewModel.dataList[pos].objectId))
                    saveEventually()
                }
                AVObject.createWithoutData("Post",viewModel.dataList[pos].objectId).apply {
                    increment("likeCount")
                    saveEventually()
                }
            }
        }
        adapter.notifyItemChanged(pos)
    }
    private fun initRecyclerView(){
        detail_recyclerview.addOnScrollListener(object :RecyclerView.OnScrollListener(){
            var isPullUp=false
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                val layoutManager=recyclerView.layoutManager as LinearLayoutManager
                val lastVisiblePos=layoutManager.findLastVisibleItemPosition()
                if(newState==RecyclerView.SCROLL_STATE_IDLE&&lastVisiblePos+1==viewModel.dataList.size&&isPullUp
                    &&viewModel.dataList.size==(viewModel.loadCountLiveData.value!!+1)*limit +1){
                    LogUtils.e("执行上拉加载更多")
                    loadMoreComment()
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                LogUtils.e("dy=$dy,touchslop=${ViewConfiguration.get(this@DetailActivity).scaledTouchSlop}")
                if(dy<-ViewConfiguration.get(this@DetailActivity).scaledTouchSlop){
                    isPullUp=true
                }
            }
        })
        detail_recyclerview.layoutManager=LinearLayoutManager(this)
        detail_recyclerview.adapter=adapter
    }
    private fun initListener(){
        detail_swipe_refresh.setOnRefreshListener {
            LogUtils.e("下拉刷新了")
            refreshData()
        }
        detail_submit.setOnClickListener {
            viewModel.fetchNewPost(viewModel.post.objectId)
        }
    }
    private fun refreshData(){
        //todo 这里应该是刷新整个页面并且替换当前intent获得的AVObject

        viewModel.dataList.clear()
        viewModel.dataList.add(viewModel.post)
        viewModel.clearLoadCount()
        LogUtils.e("执行refreshData()")
        loadComment()
        detail_swipe_refresh.isRefreshing=true
    }
    private fun clearUI(){
        detail_edit.setText("")
        detail_edit.clearFocus()
        SoftKeyboardUtils.hideKeyboard(this)
    }
}