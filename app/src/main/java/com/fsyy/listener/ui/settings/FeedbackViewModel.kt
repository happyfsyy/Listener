package com.fsyy.listener.ui.settings

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.fsyy.listener.logic.Repository

class FeedbackViewModel :ViewModel(){
    companion object{
        const val IMG1=10
        const val IMG2=20
        const val IMG3=30
    }
    var clickedImgId= IMG1
    var imgUri1:Uri?=null
    var imgUri2:Uri?=null
    var imgUri3:Uri?=null

    private val pathsLiveData=MutableLiveData<ArrayList<String>>()
    val imgUrlLiveData=Transformations.switchMap(pathsLiveData){
        Repository.uploadImages(it)
    }
    fun uploadImages(path0:String?,path1:String?,path2:String?){
        val paths=ArrayList<String>()
        path0?.let { paths.add(path0) }
        path1?.let { paths.add(path1) }
        path2?.let { paths.add(path2) }
        pathsLiveData.value=paths
    }
}