package com.fsyy.listener.utils.listener

import android.view.View

/**
 * toolbar的左边和右边按钮的监听器
 */
typealias ToolBarClickListener=(view:View)->Unit
/**
 * 遇见页面，帖子点击的监听器
 */
typealias OnItemClickListener=(view:View,pos:Int)->Unit
/**
 * 点赞的监听器
 */
typealias OnLikeClickListener=(view:View,pos:Int)->Unit
/**
 * 帖子详情的InnerComment点击监听器
 * @param pos 是在整个帖子的位置，其实就是floor
 * @param index 是在评论内部的位置，也就是innerFloor
 */
typealias OnInnerCommentClickListener=(pos:Int,index:Int)->Unit
typealias OnInnerCommentLoadMoreListener=(view:View,pos:Int)->Unit