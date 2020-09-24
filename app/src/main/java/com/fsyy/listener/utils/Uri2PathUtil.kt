package com.fsyy.listener.utils

import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import com.fsyy.listener.ui.MyApplication


object Uri2PathUtil {
    fun getImageRealPathFromContentUri(uri: Uri):String?{
        val column=MediaStore.MediaColumns.DATA
        val projection= arrayOf(column)
        var cursor:Cursor?=null
        try{
            cursor=MyApplication.context.contentResolver.query(uri, projection,
                null, null, null)
            if(cursor!=null&&cursor.moveToFirst()){
                val columnIndex=cursor.getColumnIndexOrThrow(column)
                return cursor.getString(columnIndex)
            }
        }catch (e: Exception){
            e.printStackTrace()
        }finally {
            cursor?.close()
        }
        return null
    }
}