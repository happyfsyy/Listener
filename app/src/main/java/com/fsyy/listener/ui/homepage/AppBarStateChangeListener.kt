package com.fsyy.listener.ui.homepage

import com.fsyy.listener.utils.LogUtils
import com.google.android.material.appbar.AppBarLayout
import kotlin.math.abs

abstract class AppBarStateChangeListener :AppBarLayout.OnOffsetChangedListener{
    private var mCurrentState= STATE_EXPANDED
    companion object{
        const val STATE_EXPANDED=1 //扩展
        const val STATE_INTERMEDIATE=2 //中间
        const val STATE_COLLAPSED=3 //折叠
    }
    override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
        LogUtils.e("垂直方向的偏移量是$verticalOffset")
        LogUtils.e("appBarLayout的totalScrollRange是${appBarLayout.totalScrollRange}")
        var percent= abs(verticalOffset)/appBarLayout.totalScrollRange.toFloat()
        if(verticalOffset==0){
            if(mCurrentState!= STATE_EXPANDED){
                onStateChanged(appBarLayout,STATE_EXPANDED)
                mCurrentState= STATE_EXPANDED
            }
        }else if(abs(verticalOffset)>=appBarLayout.totalScrollRange){
            if(mCurrentState!= STATE_COLLAPSED){
                onStateChanged(appBarLayout, STATE_COLLAPSED)
                mCurrentState= STATE_COLLAPSED
            }
        }else{
            onPercentChanged(percent)
            if(mCurrentState!= STATE_INTERMEDIATE){
                onStateChanged(appBarLayout, STATE_INTERMEDIATE)
                mCurrentState= STATE_INTERMEDIATE
            }
        }
    }
    abstract fun onStateChanged(appBarLayout: AppBarLayout,state:Int)
    abstract fun onPercentChanged(percent:Float)

}