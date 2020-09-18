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

typealias OnLikeClickListener=(view:View,pos:Int)->Unit