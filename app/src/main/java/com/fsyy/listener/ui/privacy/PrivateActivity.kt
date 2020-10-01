package com.fsyy.listener.ui.privacy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.fsyy.listener.R
import kotlinx.android.synthetic.main.activity_private.*

class PrivateActivity : AppCompatActivity() {
    private lateinit var adapter: PrivateAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_private)
        initParams()
    }
    private fun initParams(){
        adapter= PrivateAdapter(supportFragmentManager)
        private_viewpager2.pageMargin=20
        private_viewpager2.adapter=adapter
    }
}