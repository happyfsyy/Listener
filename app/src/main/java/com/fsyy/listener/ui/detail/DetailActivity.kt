package com.fsyy.listener.ui.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
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
        //todo 评论之后都要弹出进度条，开始转啊转的，然后是max的评论发布成功

        LogUtils.e("Detail：intent.get()是${intent.getSerializableExtra("post") as Post}")
        viewModel.post=intent.getSerializableExtra("post") as Post
        viewModel.toUserNameLiveData.value="楼主"
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
        viewModel.allCommentsLiveData.observe(this){
            val allComments=it.getOrNull()
            if(allComments!=null){
                val commentAVList=allComments.commentList
                val innerCommentAVList=allComments.innerCommentList
                for(avObject in commentAVList){
                    viewModel.dataList.add(avObject.toComment())
                }
                LogUtils.e("执行loadLikes方法前，datalist.size是${viewModel.dataList.size}")
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
                            comment.innerCommentList.add(innerComment)
                            break
                        }
                    }
                }

            }
        }
        viewModel.likesLiveData.observe(this){
            LogUtils.e("已经获得评论的点赞情况")
            val likes=it.getOrNull()
            if(likes!=null){
                //根据objectId，查询对应的comment，就将它的isLike设置为true

                val startPos=if(viewModel.loadCountLiveData.value==0){
                    0
                }else{
                    limit*viewModel.loadCountLiveData.value!!+1
                }
                val range= startPos until viewModel.dataList.size
                LogUtils.e("当前数据的大小是${viewModel.dataList.size}")
                LogUtils.e("startPos是$startPos,datalist.size-startPost是${viewModel.dataList.size-startPos}")
                for(like in likes){
                    val commentId=like.getAVObject<AVObject>("comment").objectId
                    for(i in range){
                        if(commentId==viewModel.dataList[i].objectId){
                            viewModel.dataList[i].isLike=true
                            viewModel.dataList[i].likeObjectId=like.objectId
                            break
                        }
                    }
                }
//                adapter.notifyItemRangeChanged(startPos,viewModel.dataList.size-startPos)
                adapter.notifyDataSetChanged()
            }
            detail_swipe_refresh.isRefreshing=false
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

        viewModel.toUserNameLiveData.observe(this){
            LogUtils.e("Observe了，value是${viewModel.toUserNameLiveData.value}")
            detail_edit.hint=String.format(getString(R.string.detail_edit_hint),viewModel.toUserNameLiveData.value)
        }
        viewModel.newCommentLiveData.observe(this){ it ->
            val newComment=it.getOrNull()
            if(newComment!=null){
                val commentCount=newComment.getInt("commentCount")
                val requestedInnerFloor=commentCount+1
                val floor=viewModel.floorLiveData.value!!
                val innerFloor=viewModel.innerFloorLiveData.value!!
                val comment=viewModel.dataList[floor] as Comment
                val toAuthor=if(innerFloor==0){
                    AVObject.createWithoutData("_User",comment.userId)
                }else{
                    AVObject.createWithoutData("_User",comment.innerCommentList[innerFloor].userId)
                }
                val content=detail_edit.text.toString()
                viewModel.publishComment(mapOf("fromAuthor" to AVUser.currentUser(), "toAuthor" to toAuthor,
                "content" to content,"post" to AVObject.createWithoutData("Post",viewModel.post.objectId),
                "floor" to floor,"innerFloor" to requestedInnerFloor,"isInner" to true)){innerComment->
                    //todo
                    val commentAV=AVObject.createWithoutData("Comment",comment.objectId)
                    commentAV.increment("commentCount")
                    commentAV.saveEventually()
                    comment.commentCount++
                    //TODO 保存好之后，查询这个评论的所有innerComment，展示出来

                    viewModel.loadAllInnerComments(viewModel.post.objectId,floor)
                }
            }
        }
        viewModel.allInnerComments.observe(this){
            val innerComments=it.getOrNull()
            if(innerComments!=null){
                //todo list更新，对应floor的adapter更新
                val floor=viewModel.floorLiveData.value!!
                val comment:Comment=viewModel.dataList[floor] as Comment
                comment.innerCommentList.clear()
                for(innerCommentAV in innerComments){
                    comment.innerCommentList.add(innerCommentAV.toInnerComment())
                }
                clearUI()
                adapter.notifyItemChanged(floor)
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
        adapter= DetailAdapter(viewModel.dataList,this)
        adapter.setOnPostLikeClickListener { _, _ ->
            updateUI(0,TreeHole.POST)
        }
        adapter.setOnCommentLikeClickListener { _, pos ->
            updateUI(pos,TreeHole.COMMENT)
        }
        adapter.setOnItemClickListener { view, pos ->
            LogUtils.e("pos是$pos")
            if(pos==0){
                viewModel.toUserNameLiveData.value="楼主"
                viewModel.floorLiveData.value=0
                viewModel.innerFloorLiveData.value=0
            }else{
                viewModel.floorLiveData.value=pos
                viewModel.innerFloorLiveData.value=0
                val comment=viewModel.dataList[pos] as Comment
                viewModel.toUserNameLiveData.value=comment.userName
            }
        }
        adapter.setOnInnerCommentClickListener { pos, index ->
            //todo 获取username，更改editText的hint，
            LogUtils.e("pos是$pos,index是$index")
            viewModel.floorLiveData.value=pos
            viewModel.innerFloorLiveData.value=index
            val comment=viewModel.dataList[pos] as Comment
            viewModel.toUserNameLiveData.value=comment.innerCommentList[index].userName
        }
        adapter.setOnInnerCommentLoadMoreListener {view,floor->
            //todo 获取这个floor的所有innerComment
            viewModel.floorLiveData.value=floor
            viewModel.loadAllInnerComments(viewModel.post.objectId,floor)

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
                val comment=AVObject.createWithoutData("Comment",viewModel.dataList[pos].objectId)
                viewModel.saveLike(mapOf("user" to AVUser.currentUser(),"comment" to comment)){
                    viewModel.dataList[pos].likeObjectId=it.objectId
                    comment.increment("likeCount")
                    comment.saveEventually()
                }
            }else{
                val post=AVObject.createWithoutData("Post",viewModel.dataList[pos].objectId)
                viewModel.saveLike(mapOf("user" to AVUser.currentUser(),"post" to post)){
                    viewModel.dataList[pos].likeObjectId=it.objectId
                    post.increment("likeCount")
                    post.saveEventually()
                }
            }
        }
        adapter.notifyItemChanged(pos)
    }
    private fun initRecyclerView(){
        detail_recyclerview.addOnScrollListener(object :RecyclerView.OnScrollListener(){
            var lastVisiblePos=0
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                LogUtils.e("滑动状态更改,newState=$newState")
                if(newState==RecyclerView.SCROLL_STATE_IDLE&&lastVisiblePos+1==viewModel.dataList.size
                    &&viewModel.dataList.size==(viewModel.loadCountLiveData.value!!+1)*limit +1){
                    LogUtils.e("执行上拉加载更多")
                    loadMoreComment()
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                LogUtils.e("dy=$dy,touchslop=${ViewConfiguration.get(this@DetailActivity).scaledTouchSlop}")
                val layoutManager=recyclerView.layoutManager as LinearLayoutManager
                lastVisiblePos=layoutManager.findLastVisibleItemPosition()
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
            //todo 提交的时候，获取最新的comment
            if(viewModel.floorLiveData.value==0) {
                viewModel.fetchNewPost(viewModel.post.objectId)
            }else {
                val comment=viewModel.dataList[viewModel.floorLiveData.value!!]
                viewModel.fetchNewComment(comment.objectId)
            }
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