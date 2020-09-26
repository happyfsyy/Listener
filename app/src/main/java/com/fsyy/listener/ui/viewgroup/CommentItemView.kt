package com.fsyy.listener.ui.viewgroup

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.fsyy.listener.R
import com.fsyy.listener.logic.model.Comment
import com.fsyy.listener.ui.detail.DetailActivity
import com.fsyy.listener.utils.extension.displayDate
import com.fsyy.listener.utils.extension.startActivity
import kotlinx.android.synthetic.main.comment_item.view.*

class CommentItemView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    private lateinit var comment:Comment
    init {
        LayoutInflater.from(context).inflate(R.layout.comment_item,this)
        comment_inner_post_content.visibility=View.VISIBLE
        setOnClickListener {
            (context as Activity).startActivity<DetailActivity> {
                putExtra("post",comment.post)
            }
        }
    }
    fun setComment(param: Comment){
        comment=param
        inflateContent()
    }
    private fun inflateContent(){
        comment_user_name.text=comment.userName
        //todo photo
        comment_like_count.text=comment.likeCount.toString()
        comment_content.text=comment.content
        comment_date.text=comment.date.displayDate()
        if(comment.isLike)
            comment_like_img.setImageResource(R.drawable.like_selected)
        else
            comment_like_img.setImageResource(R.drawable.like)
    }

}