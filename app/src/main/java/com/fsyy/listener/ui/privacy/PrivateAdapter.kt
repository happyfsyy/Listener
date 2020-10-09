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