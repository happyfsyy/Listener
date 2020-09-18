package com.fsyy.listener.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.leancloud.AVObject
import cn.leancloud.AVQuery
import cn.leancloud.AVUser
import com.fsyy.listener.R
import com.fsyy.listener.logic.model.Post
import com.fsyy.listener.ui.detail.DetailActivity
import com.fsyy.listener.utils.extension.toPost
import com.fsyy.listener.utils.LogUtils
import com.fsyy.listener.utils.extension.startActivity
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_encounter.*
import retrofit2.http.POST

class EncounterFragment : Fragment() {
    private lateinit var viewModel: EncounterViewModel
    private lateinit var adapter: EncounterAdapter
    private val limit=20
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel=ViewModelProvider(this).get(EncounterViewModel::class.java)
        observeViewModel()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_encounter, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        refreshPosts()
        initSwipeRefresh()
        initAdapter()
        initRecyclerView()
    }

    private fun observeViewModel(){
        viewModel.postsLiveData.observe(this, Observer {
            encounter_swipeRefresh.isRefreshing=false
            val posts=it.getOrNull()
            //else已经在Repository中处理过，这里只需要处理posts!=null的情况
            if(posts!=null){
                viewModel.postList.clear()
                for(post in posts){
                    viewModel.postList.add(post.toPost())
                }
                //查询用户是否点赞过
                viewModel.loadLikes(posts)
            }
        })
        viewModel.morePostsLiveData.observe(this, Observer {
            val posts=it.getOrNull()
            if(posts!=null){
                for(post in posts){
                    viewModel.postList.add(post.toPost())
                }
                viewModel.loadLikes(posts)
            }
        })
        viewModel.likesLiveData.observe(this, Observer {
            val likes=it.getOrNull()
            if(likes!=null){
                LogUtils.e("like表的数量${likes.size}")
                for(like in likes){
                    //查询like表中的所有数据，与刚刚加载的几个post进行对比，判断我是否点赞过
                    //todo 这里没有include的话，那么getPost有可能是null，测试一下
                    val postId=like.getAVObject<AVObject>("post").objectId
                    val range= limit*viewModel.loadCountLiveData.value!! until viewModel.postList.size
                    for(i in range){
                        if(postId==viewModel.postList[i].objectId){
                            viewModel.postList[i].isLike=true
                            viewModel.postList[i].likeObjectId=like.objectId
                        }
                    }
                }
                adapter.notifyDataSetChanged()
            }
        })
    }
    private fun refreshPosts(){
        viewModel.clearLoadCount()
        viewModel.loadPosts(limit)
        encounter_swipeRefresh.isRefreshing=true
    }
    private fun loadMorePosts(){
        viewModel.plusLoadCount()
        viewModel.loadMorePosts(limit,viewModel.loadCountLiveData.value!!)
    }
    private fun initSwipeRefresh(){
        encounter_swipeRefresh.setOnRefreshListener {
            refreshPosts()
        }
    }
    private fun initAdapter(){
        adapter=EncounterAdapter(viewModel.postList)
        adapter.setOnItemClickListener{ _, pos->
            val post=viewModel.postList[pos]
            activity?.startActivity<DetailActivity> {
                putExtra("post",post)
            }
        }
        adapter.setOnLikeClickListener { _, pos ->
            updateUI(pos)
            updateServer(pos)
        }
    }
    private fun initRecyclerView(){
        encounter_recyclerview.addOnScrollListener(object :RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                val layoutManager=recyclerView.layoutManager as LinearLayoutManager
                val lastVisibleItemPos=layoutManager.findLastVisibleItemPosition()
                LogUtils.e("滑动监测：lastVisibleItemPos=$lastVisibleItemPos,itemCount=${adapter.itemCount}")
                if(newState==RecyclerView.SCROLL_STATE_IDLE && lastVisibleItemPos+1==adapter.itemCount
                    &&adapter.itemCount==(viewModel.loadCountLiveData.value!!+1)*limit){
                    LogUtils.e("执行加载更多")
                    loadMorePosts()
                }
            }
        })
        encounter_recyclerview.layoutManager=LinearLayoutManager(activity)
        encounter_recyclerview.adapter=adapter
    }
    private fun updateUI(pos:Int){
        //根据like的状态，变更post的状态，更新adapter
        val post=viewModel.postList[pos]
        if(post.isLike){
            post.isLike=false
            post.likeCount--
        }else{
            post.isLike=true
            post.likeCount++
        }
        adapter.notifyItemChanged(pos)
    }
    private fun updateServer(pos: Int){
        // 更新like表，删除like表中的这一行数据，saveEventually，更新post表的likecount数据
        val post=viewModel.postList[pos]
        if(post.isLike){
            LogUtils.e("我要开始将like保存进like表了")
            val like=AVObject("Like")
            val postAvObject=AVObject.createWithoutData("Post",post.objectId)
            like.put("user",AVUser.currentUser())
            like.put("post",postAvObject)
            like.saveEventually()

            LogUtils.e("我要开始将点赞数量保存进Post表了")
            postAvObject.increment("likeCount")
            postAvObject.saveEventually()
        }else{
            val like=AVObject.createWithoutData("Like",post.likeObjectId)
            like.deleteEventually()
            val postAvObject=AVObject.createWithoutData("Post",post.objectId)
            postAvObject.decrement("likeCount")
            postAvObject.saveEventually()
        }
    }
}