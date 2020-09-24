package com.fsyy.listener.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import com.fsyy.listener.ui.MyApplication
import java.io.File
import kotlin.math.roundToInt

object BitmapUtils {
    fun rotateIfRequired(bitmap: Bitmap,outputImage: File):Bitmap{
        LogUtils.e("图片的path是${outputImage.path}")
        val exif=ExifInterface(outputImage.path)
        val orientation=exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,ExifInterface.ORIENTATION_NORMAL)
        return when(orientation){
            ExifInterface.ORIENTATION_ROTATE_90-> rotateBitmap(bitmap,90)
            ExifInterface.ORIENTATION_ROTATE_180-> rotateBitmap(bitmap,180)
            ExifInterface.ORIENTATION_ROTATE_270-> rotateBitmap(bitmap,270)
            else->bitmap
        }
    }
    fun rotateBitmap(bitmap: Bitmap,degree:Int):Bitmap{
        val matrix=Matrix().apply {
            postRotate(degree.toFloat())
        }
        val rotatedBitmap=Bitmap.createBitmap(bitmap,0,0,bitmap.width,bitmap.height,matrix,true)
        bitmap.recycle()
        return rotatedBitmap
    }
    private fun calculateInSampleSize(options:BitmapFactory.Options, reqWidth:Int, reqHeight:Int):Int {
        val bitmapWidth=options.outWidth
        val bitmapHeight=options.outHeight
        var inSampleSize=1
        if(bitmapHeight>reqHeight||bitmapWidth>reqWidth) {
            val heightRatio = (bitmapHeight.toFloat() / reqHeight).roundToInt()
            val widthRatio = (bitmapWidth.toFloat() / reqWidth).roundToInt()
            inSampleSize = if (heightRatio > widthRatio) heightRatio else widthRatio
        }
        return inSampleSize
    }
    fun decodeBitmapFromUri(imgUri:Uri,reqWidth: Int,reqHeight: Int):Bitmap{
        val options=BitmapFactory.Options()
        options.inJustDecodeBounds=true
        val fd=MyApplication.context.contentResolver.openFileDescriptor(imgUri,"r")
        BitmapFactory.decodeFileDescriptor(fd?.fileDescriptor,null,options)
        val inSampleSize= calculateInSampleSize(options,reqWidth,reqHeight)
        options.inSampleSize=inSampleSize
        options.inJustDecodeBounds=false
        return BitmapFactory.decodeFileDescriptor(fd?.fileDescriptor,null,options)
    }
}