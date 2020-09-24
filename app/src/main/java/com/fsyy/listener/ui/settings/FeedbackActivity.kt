package com.fsyy.listener.ui.settings

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.fsyy.listener.R
import com.fsyy.listener.utils.extension.showToast
import kotlinx.android.synthetic.main.activity_feedback.*

class FeedbackActivity : AppCompatActivity(),View.OnClickListener{
    companion object{
        const val FROM_ALBUM=1
    }
    private val viewModel=ViewModelProvider(this).get(FeedbackViewModel::class.java)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback)
        initViews()
    }
    private fun initViews(){
        feedback_img1.setOnClickListener(this)
        feedback_img2.setOnClickListener(this)
        feedback_img3.setOnClickListener(this)
        feedback_submit.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            feedback_img1.id->{
                viewModel.clickedImgId=FeedbackViewModel.IMG1
                requestPermission()
            }
            feedback_img2.id->{
                viewModel.clickedImgId=FeedbackViewModel.IMG2
                requestPermission()
            }
            feedback_img3.id->{
                viewModel.clickedImgId=FeedbackViewModel.IMG3
                requestPermission()
            }
            feedback_submit.id->submit()
        }
    }
    private fun requestPermission(){
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)
            !=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), FROM_ALBUM)
        }else{
            fromAlbum()
        }
    }
    private fun fromAlbum(){
        val intent=Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, FROM_ALBUM)
    }
    private fun submit(){
        val content=feedback_edit.text.toString().trim()
        if(content.isEmpty()) {
            getString(R.string.edit_content_toast).showToast()
        }else{
            //todo 上传三张图片
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode== FROM_ALBUM){
            if(grantResults.isNotEmpty()&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                fromAlbum()
            }else{
                getString(R.string.write_external_denied).showToast()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            FROM_ALBUM->{
                if(resultCode== RESULT_OK&&data!=null){
                    data.data?.let {
                        loadImage(it)
                    }
                }
            }
        }
    }
    private fun loadImage(uri:Uri) {
        when (viewModel.clickedImgId) {
            FeedbackViewModel.IMG1 -> {
                viewModel.imgUri1=uri
                Glide.with(this).load(uri)
                    .apply(RequestOptions.bitmapTransform(CircleCrop()))
                    .into(feedback_img1)
            }
            FeedbackViewModel.IMG2 -> {
                viewModel.imgUri2=uri
                Glide.with(this).load(uri)
                    .apply(RequestOptions.bitmapTransform(CircleCrop()))
                    .into(feedback_img2)
            }
            FeedbackViewModel.IMG3 -> {
                viewModel.imgUri3=uri
                Glide.with(this).load(uri)
                    .apply(RequestOptions.bitmapTransform(CircleCrop()))
                    .into(feedback_img3)
            }
        }
    }
}