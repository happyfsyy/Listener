package com.fsyy.listener.ui.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import cn.leancloud.AVObject
import cn.leancloud.AVUser
import com.fsyy.listener.R
import com.fsyy.listener.logic.model.Comment
import com.fsyy.listener.logic.model.Post
import com.fsyy.listener.utils.LogUtils
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
                //todo 变为comment
                for(avObject in commentAVList){
                    viewModel.dataList.add(avObject.toComment())
                }
                adapter.notifyDataSetChanged()
            }
        }
    }
    private fun loadComment(){
        viewModel.loadComment(10,viewModel.post.objectId)
    }
    private fun initAdapter(){
        viewModel.dataList.add(viewModel.post)
        adapter= DetailAdapter(viewModel.dataList)
    }
    private fun initRecyclerView(){
        detail_recyclerview.layoutManager=LinearLayoutManager(this)
        detail_recyclerview.adapter=adapter
    }
    private fun initListener(){
        detail_swipe_refresh.setOnRefreshListener {
            refreshData()
        }
        detail_submit.setOnClickListener {
            //数据不为空，上传到comment
            //todo 这里暂时只做假数据，就是将这些直接显示，然后采用saveEventually
            val comment=Comment("",AVUser.currentUser().objectId,"",
                AVUser.currentUser().getString("username"),0,detail_edit.text.toString(), Date())
            val commentAV= valuesOfAVObject("Comment", mapOf("fromAuthor" to AVUser.currentUser(),
                "content" to detail_edit.text.toString(),"post" to AVObject.createWithoutData("Post",viewModel.post.objectId),
                "toAuthor" to AVObject.createWithoutData("_User",viewModel.post.userId)))
            commentAV.saveEventually()

            viewModel.dataList.add(comment)
            adapter.notifyDataSetChanged()
        }
    }
    private fun refreshData(){
        //todo 这里应该是刷新整个页面并且替换当前intent获得的AVObject
        viewModel.dataList.clear()
        viewModel.dataList.add(viewModel.post)
        loadComment()
        detail_swipe_refresh.isRefreshing=true
    }

}