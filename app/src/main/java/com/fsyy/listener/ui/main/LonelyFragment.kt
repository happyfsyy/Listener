package com.fsyy.listener.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import cn.leancloud.AVUser
import com.fsyy.listener.R
import com.fsyy.listener.utils.LogUtils
import com.fsyy.listener.utils.SoftKeyboardUtils
import com.fsyy.listener.utils.extension.showToast
import kotlinx.android.synthetic.main.fragment_lonely.*

class LonelyFragment : Fragment(),View.OnClickListener{
    lateinit var viewModel: LonelyViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel=ViewModelProvider(this).get(LonelyViewModel::class.java)
        observeViewModel()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_lonely, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initListener()
    }
    override fun onClick(v: View?) {
        when(v?.id){
            lonely_open_layout.id->viewModel.open()
            lonely_private_layout.id->viewModel.private()
            lonely_buried_layout.id->viewModel.buried()
            lonely_submit.id->submit()
        }
    }
    private fun observeViewModel(){
        viewModel.type.observe(this, {type->
            when(type){
                LonelyViewModel.OPEN->{
                    clearSelection()
                    hideKeyboard()
                    lonely_content.hint=getString(R.string.lonely_open_hint)
                    lonely_open_img.setImageResource(R.drawable.tag_selected)
                }
                LonelyViewModel.PRIVATE->{
                    clearSelection()
                    hideKeyboard()
                    lonely_content.hint=getString(R.string.lonely_private_hint)
                    lonely_private_img.setImageResource(R.drawable.tag_selected)
                }
                LonelyViewModel.BURIED->{
                    clearSelection()
                    hideKeyboard()
                    lonely_content.hint=getString(R.string.lonely_buried_hint)
                    lonely_buried_img.setImageResource(R.drawable.tag_selected)
                }
            }
        })
    }
    private fun initListener(){
        lonely_open_layout.setOnClickListener(this)
        lonely_private_layout.setOnClickListener(this)
        lonely_buried_layout.setOnClickListener(this)
        lonely_submit.setOnClickListener(this)
    }
    private fun clearSelection(){
        lonely_open_img.setImageResource(R.drawable.tag)
        lonely_private_img.setImageResource(R.drawable.tag)
        lonely_buried_img.setImageResource(R.drawable.tag)
    }
    private fun hideKeyboard(){
        SoftKeyboardUtils.hideKeyboard(activity!!)
        lonely_tag.clearFocus()
    }
    private fun clearEditText(){
        lonely_content.setText("")
        lonely_tag.setText("")
    }
    private fun submit(){
        val content=lonely_content.text.toString().trim()
        val tag=lonely_tag.text.toString().trim()
        viewModel.publishPost(mapOf("author" to AVUser.currentUser(),
            "content" to content,"tag" to tag,"type" to viewModel.type.value)){
            LogUtils.e(it.toJSONString())
            clearEditText()
            hideKeyboard()
            getString(R.string.lonely_submit_success).showToast()
        }
    }
}