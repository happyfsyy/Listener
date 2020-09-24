package com.fsyy.listener.ui.settings

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import cn.leancloud.AVFile
import cn.leancloud.AVUser
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.fsyy.listener.R
import com.fsyy.listener.utils.LogUtils
import com.fsyy.listener.utils.Uri2PathUtil
import com.fsyy.listener.utils.extension.showToast
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.fragment_personal.*
import java.io.File
import java.io.FileNotFoundException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs


class ProfileActivity : AppCompatActivity(),View.OnClickListener{
    companion object{
        const val TYPE_USERNAME=1
        const val TYPE_SIGN=2
        const val TAKE_PHOTO=111
        const val FROM_ALBUM=222
    }
    private val viewModel by lazy { ViewModelProvider(this).get(ProfileViewModel::class.java) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        initViews()
        initListener()
    }
    private fun initViews(){
        Glide.with(this).load(AVUser.currentUser().getString("photoUrl"))
            .apply(RequestOptions.bitmapTransform(CircleCrop()))
            .into(profile_photo_img)
        profile_header.setLeftListener {
            finishAct()
        }
        profile_username_text.text=AVUser.currentUser().username
        LogUtils.e("编辑页中，用户名是：${AVUser.currentUser().username}")
        val intro=AVUser.currentUser().getString("intro")
        if(intro=="")
            profile_sign_text.text=getString(R.string.profile_signature)
        else
            profile_sign_text.text=intro
    }
    private fun initListener(){
        profile_photo_layout.setOnClickListener(this)
        profile_username_layout.setOnClickListener(this)
        profile_sign_layout.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            profile_photo_layout.id -> showPopupWindow()
            profile_username_layout.id -> showDialog(
                getString(R.string.profile_edit_username),
                profile_username_text.text.toString(), TYPE_USERNAME
            )
            profile_sign_layout.id -> showDialog(
                getString(R.string.profile_edit_sign),
                profile_sign_text.text.toString(), TYPE_SIGN
            )
            viewModel.takePhoto.id -> requestPermission(TAKE_PHOTO)
            viewModel.albums.id -> requestPermission(FROM_ALBUM)
            viewModel.cancel.id -> dismissPopupWindow()
        }
    }
    private fun dismissPopupWindow(){
        val outAnimator = AnimatorInflater.loadAnimator(this, R.animator.popup_item_out)
        outAnimator.setTarget(viewModel.popupView)
        outAnimator.start()
        outAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?, isReverse: Boolean) {
                viewModel.popupWindow.dismiss()
            }
        })
    }
    private fun requestPermission(type: Int){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            !=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                type
            )
        }else{
            viewModel.popupWindow.dismiss()
            if(type== TAKE_PHOTO)
                takePhoto()
            else
                fromAlbum()
        }
    }
    private fun fromAlbum(){
        val intent=Intent(Intent.ACTION_PICK)
        intent.type="image/*"
        startActivityForResult(intent, FROM_ALBUM)
    }
    private fun takePhoto(){
        LogUtils.e("外部公共图片存储地址是${getExternalFilesDir(Environment.DIRECTORY_PICTURES)}")
        val dir=getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imgName="img_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(Date())}.jpg"
        viewModel.outputImage= File(dir, imgName)
        if(viewModel.outputImage.exists())
            viewModel.outputImage.delete()
        viewModel.outputImage.createNewFile()
        viewModel.imageUri=if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
            FileProvider.getUriForFile(
                this,
                "com.fsyy.listener.fileprovider",
                viewModel.outputImage
            )
        }else{
            Uri.fromFile(viewModel.outputImage)
        }
        val intent=Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, viewModel.imageUri)
        startActivityForResult(intent, TAKE_PHOTO)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(grantResults.isNotEmpty()&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
            when(requestCode){
                TAKE_PHOTO -> takePhoto()
                FROM_ALBUM -> fromAlbum()
            }
        }else{
            getString(R.string.write_external_denied).showToast()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            TAKE_PHOTO -> {
                if (resultCode == RESULT_OK) {
                    //todo 任何耗时操作都要显示进度条（可以是在popupWindow的进度条，进度条显示在popupWindow的中间）
                    // todo 存储avfile，avfile保存成功再将图片加载进图片，否则就不加载
                    viewModel.isModified=true
                    Glide.with(this).load(viewModel.imageUri)
                        .apply(RequestOptions.bitmapTransform(CircleCrop()))
                        .into(profile_photo_img)
                    LogUtils.e("拍照图片的uri是${viewModel.imageUri}")
                    LogUtils.e("uri得到的path是${viewModel.imageUri.path}")
                    LogUtils.e("我根据file获得本地路径是${viewModel.outputImage.absolutePath}")
                    LogUtils.e("工具类的绝对路径是${Uri2PathUtil.getImageRealPathFromContentUri(viewModel.imageUri)}")
                    viewModel.uploadImage(viewModel.outputImage.absolutePath) {
                        LogUtils.e("我上传图片成功了，你敢信")
                        //todo 在这里进行AVUser.save的回调，在回调完成之后，才把图片加载到view之中，并且让进度条消失
                        savePhoto(it)
                        //todo 在这里setResult
                    }
                }
            }
            FROM_ALBUM -> {
                if (resultCode == RESULT_OK && data != null) {
                    viewModel.isModified=true
                    data.data?.let { it ->
                        LogUtils.e("选取图片的uri是$it")
                        LogUtils.e("选取相册图片的绝对路径是${Uri2PathUtil.getImageRealPathFromContentUri(it)}")
                        Glide.with(this).load(it)
                            .apply(RequestOptions.bitmapTransform(CircleCrop()))
                            .into(profile_photo_img)
                        val absolutePath = Uri2PathUtil.getImageRealPathFromContentUri(it)
                        val file=File(absolutePath)
                        file.apply {
                            if (!this.exists()|| !this.isFile) {
                                LogUtils.e("我选取的这张照片不存在")
                                throw FileNotFoundException()
                            }else{
                                LogUtils.e("我选取的这张照片存在呀")

                            }
                        }
                        viewModel.uploadImage(Uri2PathUtil.getImageRealPathFromContentUri(it)!!){avfile->
                            LogUtils.e("上传图片成功了。。。。")
                            savePhoto(avfile)
                            //todo setResult
                        }
                    }
                }
            }
        }
    }

    private fun showPopupWindow(){
        viewModel.takePhoto.setOnClickListener(this)
        viewModel.albums.setOnClickListener(this)
        viewModel.cancel.setOnClickListener(this)
        val inAnimator=AnimatorInflater.loadAnimator(this, R.animator.popup_item_in)
        inAnimator.setTarget(viewModel.popupView)
        inAnimator.start()
        viewModel.popupWindow=PopupWindow(
            viewModel.popupView,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        ).apply {
            isFocusable=true
            isOutsideTouchable=false
            animationStyle=R.style.popupAnimation
            setBackgroundDrawable(ColorDrawable(Color.parseColor("#55000000")))
            isClippingEnabled=false
            showAtLocation(window.decorView, Gravity.TOP, 0, 0)
        }
    }
    private fun showDialog(header: String, content: String, type: Int){
        val view=LayoutInflater.from(this).inflate(R.layout.profile_dialog_view, null)
        val headerText:TextView=view.findViewById(R.id.profile_dialog_header)
        val contentEdit:EditText=view.findViewById(R.id.profile_dialog_edit)
        contentEdit.apply {
            requestFocus()
            setText(content)
            setSelection(content.length)
        }
        headerText.text=header
        AlertDialog.Builder(this, R.style.AlertDialog).apply {
            setView(view)
            setCancelable(false)
            setPositiveButton(getString(R.string.dialog_confirm)){ dialog, _->
                when (val contentText=contentEdit.text.toString().trim()) {
                    "" -> {
                        if (type == TYPE_USERNAME)
                            getString(R.string.profile_username_toast).showToast()
                        else
                            getString(R.string.profile_sign_toast).showToast()
                    }
                    content -> dialog.dismiss()
                    else -> {
                        if(type== TYPE_USERNAME)
                            //todo 可以改为viewmodel.saveUserName，observe userName，然后进行setText
                            saveUserName(contentText)
                        else
                            saveSign(contentText)
                        viewModel.isModified=true
                    }
                }
            }
            setNegativeButton(getString(R.string.dialog_cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            show()
        }
    }
    private fun saveUserName(contentText: String){
        profile_username_text.text=contentText
        AVUser.currentUser().put("username", contentText)
        AVUser.currentUser().saveEventually()
        //todo 需不需要显示进度条
    }
    private fun saveSign(contentText: String){
        profile_sign_text.text=contentText
        AVUser.currentUser().put("intro", contentText)
        AVUser.currentUser().saveEventually()
    }
    private fun savePhoto(avFile: AVFile){
        AVUser.currentUser().put("photoUrl", avFile.url)
        AVUser.currentUser().saveEventually()
    }

    override fun onBackPressed()=finishAct()
    private fun finishAct(){
        val intent=Intent()
        intent.putExtra("isModified",viewModel.isModified)
        LogUtils.e("当前的isModified是${viewModel.isModified}")
        setResult(RESULT_OK,intent)
        finish()
    }
}