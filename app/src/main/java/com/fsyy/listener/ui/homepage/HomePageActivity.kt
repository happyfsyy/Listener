package com.fsyy.listener.ui.homepage

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.fsyy.listener.R
import com.fsyy.listener.utils.LogUtils
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.activity_home_page.*

class HomePageActivity : AppCompatActivity() {
    private lateinit var viewModel: HomePageViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)
        initParams()
        initViews()
        viewModel.userId=intent.getStringExtra("userId")!!
        viewModel.getData(viewModel.userId)
    }
    private fun initParams(){
        LogUtils.e("collapsingToolbarLayout的高度是${home_collapsing_layout.height},toolbar的高度是${home_toolbar.height}")
        setSupportActionBar(home_toolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setDisplayShowTitleEnabled(false)
        }
        home_collapsing_layout.title="Ale246"
        viewModel=ViewModelProvider(this).get(HomePageViewModel::class.java)
    }
    private fun initViews(){
        home_appbar_layout.addOnOffsetChangedListener(object : AppBarStateChangeListener() {
            override fun onStateChanged(appBarLayout: AppBarLayout, state: Int) {
                when(state){
                    STATE_EXPANDED->{
                        home_data_layout.visibility=View.VISIBLE
                        home_collapsing_layout.isTitleEnabled=false
                    }
                    STATE_INTERMEDIATE-> {
                        home_data_layout.visibility=View.VISIBLE
                        home_collapsing_layout.isTitleEnabled = false
                    }
                    STATE_COLLAPSED->{
                        home_data_layout.visibility=View.INVISIBLE
                        home_collapsing_layout.isTitleEnabled=true
                    }
                }
            }
            override fun onPercentChanged(percent: Float) {
                LogUtils.e("透明度是alpha=${1-percent}")
                home_data_layout.alpha=(1-percent)*0.9f
            }
        })
        viewModel.allDataLiveData.observe(this){
            val result=it.getOrNull()
            if(result!=null){

            }
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home->{
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}