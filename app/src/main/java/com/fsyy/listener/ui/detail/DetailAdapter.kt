package com.fsyy.listener.ui.detail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fsyy.listener.R
import com.fsyy.listener.logic.model.Comment
import com.fsyy.listener.logic.model.Post
import com.fsyy.listener.logic.model.TreeHole
import com.fsyy.listener.ui.main.CommentViewHolder
import com.fsyy.listener.ui.main.PostViewHolder
import com.fsyy.listener.ui.main.TreeHoleViewHolder
import com.fsyy.listener.utils.extension.displayDate
import com.fsyy.listener.utils.listener.OnLikeClickListener

class DetailAdapter(private val list:List<TreeHole>):RecyclerView.Adapter<TreeHoleViewHolder>() {
    override fun getItemViewType(position: Int): Int {
        val treeHole=list[position]
        return treeHole.type
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TreeHoleViewHolder =if(viewType==TreeHole.POST){
        val view=LayoutInflater.from(parent.context).inflate(R.layout.post_item,parent,false)
        PostViewHolder(view).apply {
            likeImg.setOnClickListener {
                postLikeClickListener(it,adapterPosition)
            }
        }
    }else{
        val view=LayoutInflater.from(parent.context).inflate(R.layout.comment_item,parent,false)
        CommentViewHolder(view).apply {
            likeImg.setOnClickListener {
                commentLikeClickListener(it,adapterPosition)
            }
        }
    }

    override fun onBindViewHolder(holder: TreeHoleViewHolder, position: Int) {
        when(holder){
            is PostViewHolder->{
                //todo 加载用户头像
                val post=list[position] as Post
                holder.userName.text=post.userName
                holder.content.text=post.content
                if(post.tag==""){
                    holder.tag.visibility= View.GONE
                }else{
                    holder.tag.visibility= View.VISIBLE
                    holder.tag.text="#"+post.tag
                }
                holder.date.text=post.date.displayDate()
                holder.commentCount.text=post.commentCount.toString()
                holder.likeCount.text=post.likeCount.toString()
                if(post.isLike)
                    holder.likeImg.setImageResource(R.drawable.energy_selected)
                else
                    holder.likeImg.setImageResource(R.drawable.energy)

            }
            is CommentViewHolder->{
                val comment=list[position] as Comment
                holder.userName.text=comment.userName
                holder.likeCount.text=comment.likeCount.toString()
                holder.content.text=comment.content
                holder.date.text=comment.date.displayDate()
                if(comment.isLike)
                    holder.likeImg.setImageResource(R.drawable.like_selected)
                else
                    holder.likeImg.setImageResource(R.drawable.like)
            }
        }
    }

    override fun getItemCount(): Int=list.size

    private lateinit var postLikeClickListener:OnLikeClickListener
    private lateinit var commentLikeClickListener: OnLikeClickListener
    fun setOnPostLikeClickListener(listener:OnLikeClickListener){
        this.postLikeClickListener=listener
    }
    fun setOnCommentLikeClickListener(listener:OnLikeClickListener){
        this.commentLikeClickListener=listener
    }
}