package com.fsyy.listener.ui.viewgroup

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.fsyy.listener.R
import kotlinx.android.synthetic.main.toast_layout.view.*

class ToastLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    init {
        LayoutInflater.from(context).inflate(R.layout.toast_layout,this)
    }
    constructor(context: Context,imgId:Int,text:String) : this(context) {
        toast_img.setImageResource(imgId)
        toast_text.text=text
    }
}