package com.fsyy.listener.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentTransaction
import com.fsyy.listener.R
import com.fsyy.listener.utils.LogUtils
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(),View.OnClickListener{
    private var encounterFragment: EncounterFragment?=null
    private var lonelyFragment: LonelyFragment?=null
    private var personalFragment: PersonalFragment?=null
    private val manager=supportFragmentManager
    companion object{
        const val WOOD=0
        const val LEAF=1
        const val TREE=2
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initListener()
        setTab(LEAF)
        test()
    }

    /**
     * 这里是测试一些基本属性
     */
    private fun test(){
        val resourceId=resources.getIdentifier("status_bar_height","dimen","android")
        LogUtils.e("density: ${resources.displayMetrics.density}")
        LogUtils.e("status高度: ${resources.getDimensionPixelSize(resourceId)}")

        val date=Date()
        val simpleDate=SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
        LogUtils.e("日期:",simpleDate.format(date))
    }
    private fun initListener(){
        wood.setOnClickListener(this)
        leaf.setOnClickListener(this)
        tree.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            wood.id->setTab(WOOD)
            leaf.id->setTab(LEAF)
            tree.id->setTab(TREE)
        }
    }
    private fun clearSelection(){
        wood.setImageResource(R.drawable.wood)
        leaf.setImageResource(R.drawable.leaf)
        tree.setImageResource(R.drawable.tree)
    }
    private fun hideFragments(transaction:FragmentTransaction){
        encounterFragment?.let { transaction.hide(it) }
        lonelyFragment?.let { transaction.hide(it) }
        personalFragment?.let { transaction.hide(it) }
    }
    private fun setTab(index:Int){
        val transaction=manager.beginTransaction()
        clearSelection()
        hideFragments(transaction)
        when(index){
            WOOD->{
                wood.setImageResource(R.drawable.wood_selected)
                if(encounterFragment==null){
                    encounterFragment= EncounterFragment()
                    transaction.add(R.id.contentLayout,encounterFragment!!)
                }else{
                    transaction.show(encounterFragment!!)
                }
            }
            LEAF->{
                leaf.setImageResource(R.drawable.leaf_selected)
                if(lonelyFragment==null){
                    lonelyFragment= LonelyFragment()
                    transaction.add(R.id.contentLayout,lonelyFragment!!)
                }else{
                    transaction.show(lonelyFragment!!)
                }
            }
            TREE->{
                tree.setImageResource(R.drawable.tree_selected)
                if(personalFragment==null){
                    personalFragment= PersonalFragment()
                    transaction.add(R.id.contentLayout,personalFragment!!)
                }else{
                    transaction.show(personalFragment!!)
                }
            }
        }
        transaction.commit()
    }
}