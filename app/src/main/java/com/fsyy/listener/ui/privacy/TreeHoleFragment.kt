package com.fsyy.listener.ui.privacy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.fsyy.listener.R
import com.fsyy.listener.logic.model.PrivatePost
import kotlinx.android.synthetic.main.comment_item.*
import kotlinx.android.synthetic.main.fragment_tree_hole.*

class TreeHoleFragment:Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tree_hole,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val post:PrivatePost=arguments?.get(PrivateActivity.POST) as PrivatePost
        tree_hole_date_text.text=post.date
        tree_hole_content_text.text=post.content
    }
}