package com.fsyy.listener.ui.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.leancloud.AVObject
import cn.leancloud.AVUser
import com.fsyy.listener.R
import com.fsyy.listener.logic.model.Comment
import com.fsyy.listener.logic.model.Post
import com.fsyy.listener.logic.model.TreeHole
import com.fsyy.listener.logic.network.Network
import com.fsyy.listener.utils.LogUtils
import com.fsyy.listener.utils.SoftKeyboardUtils
import com.fsyy.listener.utils.extension.showToast
import com.fsyy.listener.utils.extension.toComment
import com.fsyy.listener.utils.extension.valuesOfAVObject
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.fragment_encounter.*
import java.util.*

class DetailActivity : AppCompatActivity() {
    private lateinit var adapter: DetailAdapter
    private val viewModel by lazy { ViewModelProvider(this).get(DetailViewModel::class.java) }
    private val limit=10
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        LogUtils.e("Detail：intent.get()是${intent.getSerializableExtra("post") as Post}")
        viewModel.post=intent.getSerializableExtra("post") as Post
        LogUtils.e(viewModel.post.toString())
        loadComment()
        initObserver()
        initAdapter()
        initRecyclerView()
        initListener()
        refreshData()
    }
    private fun initObserver(){
        viewModel.commentLiveData.observe(this){
            val commentAVList=it.getOrNull()
            detail_swipe_refresh.isRefreshing=false
            LogUtils.e("评论的size大小是${commentAVList?.size?:0}")
            if(commentAVList!=null){
                for(avObject in commentAVList){
                    viewModel.dataList.add(avObject.toComment())
                }
//                adapter.notifyDataSetChanged()
                viewModel.loadLikes(commentAVList)
            }
        }
        viewModel.likesLiveData.observe(this){
            val likes=it.getOrNull()
            if(likes!=null){
                //根据objectId，查询对应的comment，就将它的isLike设置为true
                for(like in likes){
                    val commentId=like.getAVObject<AVObject>("comment").objectId
                    //todo 将这里的范围根据loadCount重新设置，adapter的时候，就可以只notifyItemRangeChanged
                    val range= limit*viewModel.loadCountLiveData.value!!+1 until viewModel.dataList.size

                    for(i in range){
                        if(commentId==viewModel.dataList[i].objectId){
                            viewModel.dataList[i].isLike=true
                            viewModel.dataList[i].likeObjectId=like.objectId
                        }
                    }
                }
                adapter.notifyDataSetChanged()
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
        viewModel.loadComment(limit,viewModel.post.objectId,0)
    }
    private fun loadMoreComment(){
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
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                val layoutManager=recyclerView.layoutManager as LinearLayoutManager
                val lastVisiblePos=layoutManager.findLastVisibleItemPosition()
                if(newState==RecyclerView.SCROLL_STATE_IDLE&&lastVisiblePos+1==viewModel.dataList.size
                    &&viewModel.dataList.size==(viewModel.loadCountLiveData.value!!+1)*limit +1){
                    loadMoreComment()
                }
            }
        })
        detail_recyclerview.layoutManager=LinearLayoutManager(this)
        detail_recyclerview.adapter=adapter
    }
    private fun initListener(){
        detail_swipe_refresh.setOnRefreshListener {
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
        loadComment()
        detail_swipe_refresh.isRefreshing=true
    }
    private fun clearUI(){
        detail_edit.setText("")
        detail_edit.clearFocus()
        SoftKeyboardUtils.hideKeyboard(this)
    }
}