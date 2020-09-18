package com.fsyy.listener.ui.main

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fsyy.listener.R
import com.fsyy.listener.logic.model.Post
import com.fsyy.listener.utils.extension.displayDate
import com.fsyy.listener.utils.listener.OnItemClickListener
import com.fsyy.listener.utils.listener.OnLikeClickListener

class EncounterAdapter(val list:List<Post>):RecyclerView.Adapter<PostViewHolder>(){
    private lateinit var onItemClickListener:OnItemClickListener
    private lateinit var onLikeClickListener: OnLikeClickListener
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val itemView=LayoutInflater.from(parent.context).inflate(R.layout.post_item,parent,false)
        val viewHolder=PostViewHolder(itemView)
        viewHolder.content.maxLines=3
        viewHolder.content.ellipsize=TextUtils.TruncateAt.END
        viewHolder.itemView.setOnClickListener {
            onItemClickListener(it,viewHolder.adapterPosition)
        }
        viewHolder.likeImg.setOnClickListener {
            onLikeClickListener(it,viewHolder.adapterPosition)
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post=list[position]
        holder.userName.text=post.userName
        //todo 加载用户头像
        holder.content.text=post.content
        if(post.tag==""){
            holder.tag.visibility=View.GONE
        }else{
            holder.tag.visibility=View.VISIBLE
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

    override fun getItemCount(): Int =list.size
    fun setOnItemClickListener(listener:OnItemClickListener){
        this.onItemClickListener=listener
    }
    fun setOnLikeClickListener(listener:OnLikeClickListener){
        this.onLikeClickListener=listener
    }
}