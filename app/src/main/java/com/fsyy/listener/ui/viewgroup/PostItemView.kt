package com.fsyy.listener.ui.viewgroup

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.fsyy.listener.R
import com.fsyy.listener.logic.model.Post
import com.fsyy.listener.ui.detail.DetailActivity
import com.fsyy.listener.utils.extension.displayDate
import com.fsyy.listener.utils.extension.startActivity
import kotlinx.android.synthetic.main.post_item.view.*

class PostItemView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    private lateinit var post:Post
    init {
        LayoutInflater.from(context).inflate(R.layout.post_item,this)
        //todo 这里需不需要initView，通过view.findViewById

        setOnClickListener {
            (context as Activity).startActivity<DetailActivity> {
                putExtra("post",post)
            }
        }
    }
    fun setPost(post:Post){
        this.post=post
        inflateContent()
    }
    private fun inflateContent(){
        post_userName.text=post.userName
        //todo 加载用户头像
        post_content.text=post.content
        if(post.tag==""){
            post_tag.visibility= View.GONE
        }else{
            post_tag.visibility= View.VISIBLE
            post_tag.text="#"+post.tag
        }
        post_date.text=post.date.displayDate()
        post_comment_count.text=post.commentCount.toString()
        post_like_count.text=post.likeCount.toString()
        if(post.isLike)
            post_like_img.setImageResource(R.drawable.energy_selected)
        else
            post_like_img.setImageResource(R.drawable.energy)
    }
}