package com.fsyy.listener.ui.detail

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fsyy.listener.R
import com.fsyy.listener.logic.model.Comment
import com.fsyy.listener.logic.model.Post
import com.fsyy.listener.logic.model.TreeHole
import com.fsyy.listener.ui.main.CommentViewHolder
import com.fsyy.listener.ui.main.PostViewHolder
import com.fsyy.listener.ui.main.TreeHoleViewHolder
import com.fsyy.listener.utils.LogUtils
import com.fsyy.listener.utils.extension.displayDate
import com.fsyy.listener.utils.listener.OnInnerCommentClickListener
import com.fsyy.listener.utils.listener.OnInnerCommentLoadMoreListener
import com.fsyy.listener.utils.listener.OnItemClickListener
import com.fsyy.listener.utils.listener.OnLikeClickListener

class DetailAdapter(private val list:List<TreeHole>,private val context: Context):RecyclerView.Adapter<TreeHoleViewHolder>() {
    override fun getItemViewType(position: Int): Int {
        val treeHole=list[position]
        return treeHole.type
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TreeHoleViewHolder =if(viewType==TreeHole.POST){
        val view=LayoutInflater.from(parent.context).inflate(R.layout.post_item,parent,false)
        PostViewHolder(view).apply {
            itemView.setOnClickListener {
                itemClickListener(it,adapterPosition)
            }
            likeImg.setOnClickListener {
                postLikeClickListener(it,adapterPosition)
            }
        }
    }else{
        val view=LayoutInflater.from(parent.context).inflate(R.layout.comment_item,parent,false)
        CommentViewHolder(view).apply {
            itemView.setOnClickListener{
                itemClickListener(it,adapterPosition)
            }
            likeImg.setOnClickListener {
                commentLikeClickListener(it,adapterPosition)
            }
        }
    }

    override fun onBindViewHolder(holder: TreeHoleViewHolder, position: Int) {
        LogUtils.e("BindViewHolder")
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
                //todo photo
                holder.likeCount.text=comment.likeCount.toString()
                holder.content.text=comment.content
                holder.date.text=comment.date.displayDate()
                if(comment.isLike)
                    holder.likeImg.setImageResource(R.drawable.like_selected)
                else
                    holder.likeImg.setImageResource(R.drawable.like)
                if(comment.innerCommentList.size==0) {
                    holder.innerLayout.visibility = View.GONE
                }else{
                    holder.innerLayout.visibility=View.VISIBLE
                    holder.innerLayout.removeAllViews()
                    for(i in 0 until comment.innerCommentList.size){
                        val textView=genTextView(context,holder.innerLayout,holder.adapterPosition,i)
                        holder.innerLayout.addView(textView)
                    }
                    val loadMoreTextView=genLoadMoreTextView(context,holder.innerLayout,holder.adapterPosition)
                    if(comment.commentCount>2&&comment.innerCommentList.size<comment.commentCount){
                        loadMoreTextView.visibility=View.VISIBLE
                        holder.innerLayout.addView(loadMoreTextView)
                    }else{
                        loadMoreTextView.visibility=View.GONE
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int=list.size

    private lateinit var postLikeClickListener:OnLikeClickListener
    private lateinit var commentLikeClickListener: OnLikeClickListener
    private lateinit var itemClickListener:OnItemClickListener
    fun setOnPostLikeClickListener(listener:OnLikeClickListener){
        this.postLikeClickListener=listener
    }
    fun setOnCommentLikeClickListener(listener:OnLikeClickListener){
        this.commentLikeClickListener=listener
    }
    fun setOnItemClickListener(listener:OnItemClickListener){
        this.itemClickListener=listener
    }

    private lateinit var innerCommentClickListener:OnInnerCommentClickListener
    private lateinit var innerCommentLoadMoreListener:OnInnerCommentLoadMoreListener
    fun setOnInnerCommentClickListener(listener:OnInnerCommentClickListener){
        this.innerCommentClickListener=listener
    }
    fun setOnInnerCommentLoadMoreListener(listener: OnInnerCommentLoadMoreListener){
        this.innerCommentLoadMoreListener=listener
    }

    private fun genTextView(context:Context,parent:ViewGroup,pos:Int,index:Int):TextView{
        val textView=LayoutInflater.from(context).inflate(R.layout.inner_comment_item,parent,false) as TextView
        val comment=list[pos] as Comment
        val innerComment=comment.innerCommentList[index]
        //todo setbg drawable spannableString
        val text="${innerComment.userName}回复${innerComment.toUserName}:${innerComment.content} ${innerComment.date.displayDate()}"
        textView.text=text
        textView.setOnClickListener {
            innerCommentClickListener(pos,index)
        }
        return textView
    }
    private fun genLoadMoreTextView(context: Context,parent: ViewGroup,pos:Int):TextView{
        val textView=LayoutInflater.from(context).inflate(R.layout.inner_comment_more,parent,false) as TextView
        textView.setOnClickListener {
            innerCommentLoadMoreListener(it,pos)
        }
        return textView
    }
}