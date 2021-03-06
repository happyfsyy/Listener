/*
 * 项目名：Listener
 * 作者：@happy_fsyy
 * 联系我：https://github.com/happyfsyy
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.fsyy.listener.ui.privacy

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.fsyy.listener.logic.model.PrivatePost

class PrivateAdapter(activity: PrivateActivity, private val postList:List<PrivatePost>):FragmentStateAdapter(activity) {
    override fun getItemCount():Int=postList.size

    override fun createFragment(position: Int): Fragment {
        return TreeHoleFragment().apply {
            arguments =Bundle().apply {
                putSerializable(PrivateActivity.POST,postList[position])
            }
        }
    }
}