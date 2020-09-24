package com.fsyy.listener.ui.settings

import android.net.Uri
import androidx.lifecycle.ViewModel

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
}