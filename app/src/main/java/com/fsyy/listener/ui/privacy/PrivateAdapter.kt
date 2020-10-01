package com.fsyy.listener.ui.privacy

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter

class PrivateAdapter(fm: FragmentManager):FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getCount(): Int {
        return 10
    }

    override fun getItem(position: Int): Fragment {
        val fragment=TreeHoleFragment()
        fragment.arguments= Bundle().apply {
            putInt("params",position)
        }
        return fragment
    }
}