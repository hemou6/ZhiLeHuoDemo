package com.example.zhilehuodemo


import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.zhilehuodemo.Cache.UserInfoManager
import com.example.zhilehuodemo.Uitls.forceToTwo
import com.example.zhilehuodemo.View.ChooseDialog
import com.example.zhilehuodemo.View.PhotoPickerDialog
import com.example.zhilehuodemo.databinding.ActivityInformationBinding
import com.github.gzuliyujiang.wheelpicker.DatePicker
import com.github.gzuliyujiang.wheelpicker.annotation.DateMode
import com.github.gzuliyujiang.wheelpicker.contract.OnDatePickedListener
import com.github.gzuliyujiang.wheelpicker.entity.DateEntity
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class InformationActivity : AppCompatActivity(){
    private lateinit var binding:ActivityInformationBinding
    private val toolbar:LinearLayout by lazy { binding.toolbar }
    private val userAvatar:ShapeableImageView by lazy { binding.userAvatar }
    private val sexGroup:RadioGroup by lazy { binding.sexGroup }
    private val userName:EditText by lazy { binding.userName }
    private val selectBirthday:RelativeLayout by lazy { binding.selectBirth }
    private val boyBtn:RadioButton by lazy { binding.boyBtn }
    private val girlBtn:RadioButton by lazy { binding.girlBtn }
    private val userBirthDay:TextView by lazy { binding.userBirthday }
    private val back:ImageView by lazy { binding.back }
    private val commit:MaterialButton by lazy { binding.commit }


    private var currentPhotoUri: Uri?=null

    companion object{
        private const val REQUEST_CAMERA = 1001
        private const val REQUEST_GALLERY = 1002
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityInformationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, v.paddingTop, systemBars.right, systemBars.bottom)
            toolbar.setPadding(toolbar.paddingLeft,systemBars.top,toolbar.paddingRight,toolbar.paddingBottom)
            WindowInsetsCompat.CONSUMED
        }
        initData()
        initListener()
    }

    private fun initData(){
        Glide.with(this)
            .load(UserInfoManager.userAvatar)
            .error(R.drawable.ic_error_vector)
            .into(userAvatar)

        if(UserInfoManager.userSex){
            boyBtn.isChecked=true
        }else{
            girlBtn.isChecked=true
        }

        userBirthDay.text=UserInfoManager.userBirth
        userName.setText(UserInfoManager.userName)
    }

    private fun initListener(){
        back.setOnClickListener{
            finish()
        }

        selectBirthday.setOnClickListener{
            showTimePicker()
        }

        commit.setOnClickListener {
            saveData()
            finish()
            Toast.makeText(CoreApplication.appContext,"保存成功",Toast.LENGTH_SHORT).show()
        }

        userAvatar.setOnClickListener {
            PhotoPickerDialog(this,
                onCameraClick = {checkCameraPermission()},
                onGalleryClick = {checkGalleryPermission()}
                ).show()
        }
    }

    private fun showTimePicker(){
        val picker=DatePicker(this)
        picker.wheelLayout.apply {
            setDateMode(DateMode.YEAR_MONTH_DAY)
            setRange(DateEntity.target(1900,1,1),DateEntity.target(2100,12,31))
            setDateLabel("年","月","日")
        }
        picker.titleView.text="生日"
        picker.setHeight(resources.displayMetrics.heightPixels/3)
        picker.setOnDatePickedListener(object : OnDatePickedListener {
            override fun onDatePicked(year: Int, month: Int, day: Int) {
                userBirthDay.text="$year-${month.forceToTwo()}-${day.forceToTwo()}"
            }
        })
        picker.show()
    }

    private fun saveData(){
        UserInfoManager.userName=userName.text.toString()
        UserInfoManager.userBirth=userBirthDay.text.toString()
        UserInfoManager.userSex=boyBtn.isChecked
        currentPhotoUri?.let { UserInfoManager.userAvatar=it.toString() }
    }

    private fun checkCameraPermission(){
        when {
            // Android 10 及以上版本
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                when {
                    ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                            == PackageManager.PERMISSION_GRANTED -> openCamera()

                    else -> {
                        val dialog=ChooseDialog(this,
                            "提示",
                            "适趣我爱说申请访问\n相机权限用于修改头像",
                            onSure = {
                                ActivityCompat.requestPermissions(
                                    this,
                                    arrayOf(Manifest.permission.CAMERA),
                                    REQUEST_CAMERA
                                )
                            }
                        )
                        dialog.show()
                    }
                }
            }
            // Android 10 以下版本可能需要额外的存储权限
            else -> {
                when {
                    ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                            == PackageManager.PERMISSION_GRANTED &&
                            ContextCompat.checkSelfPermission(
                                this,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                            )
                            == PackageManager.PERMISSION_GRANTED -> openCamera()

                    else -> {
                        ChooseDialog(this,
                            "提示",
                            "适趣我爱说申请访问\n相机权限用于修改头像",
                            onSure = {
                                ActivityCompat.requestPermissions(
                                    this,
                                    arrayOf(
                                        Manifest.permission.CAMERA,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                                    ),
                                    REQUEST_CAMERA
                                )
                            }
                        ).show()
                    }
                }
            }
        }
    }

    private fun checkGalleryPermission(){
        when {
            // 对于 Android 13 (API 33) 及以上版本
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                when {
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                            == PackageManager.PERMISSION_GRANTED -> openGallery()

                    else -> {
                        ChooseDialog(this,
                            "提示",
                            "适趣我爱说申请访问\n存储权限用于修改头像",
                            onSure = {
                                ActivityCompat.requestPermissions(
                                    this,
                                    arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                                    REQUEST_GALLERY
                                )
                            }
                        ).show()
                    }
                }
            }
            // 对于 Android 10 及以上版本
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                when {
                    ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                            == PackageManager.PERMISSION_GRANTED -> openGallery()

                    else -> {
                        ChooseDialog(this,
                            "提示",
                            "适趣我爱说申请访问\n存储权限用于修改头像",
                            onSure = {
                                ActivityCompat.requestPermissions(
                                    this,
                                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                                    REQUEST_GALLERY
                                )
                            }
                        ).show()
                    }
                }
            }
            // 对于 Android 10 以下版本
            else -> {
                when {
                    ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                            == PackageManager.PERMISSION_GRANTED -> openGallery()

                    else -> {
                        ChooseDialog(this,
                            "提示",
                            "适趣我爱说申请访问\n存储权限用于修改头像",
                            onSure = {
                                ActivityCompat.requestPermissions(
                                    this,
                                    arrayOf(
                                        Manifest.permission.READ_EXTERNAL_STORAGE,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                                    ),
                                    REQUEST_GALLERY
                                )
                            }
                        ).show()
                    }
                }
            }
        }
    }

    private fun openCamera() {
        val photoFile = createImageFile()
        currentPhotoUri = FileProvider.getUriForFile(
            this,
            "${packageName}.fileprovider",
            photoFile
        )
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { intent ->
            intent.putExtra(MediaStore.EXTRA_OUTPUT, currentPhotoUri)
            intent.resolveActivity(packageManager)?.let {
                startActivityForResult(intent, REQUEST_CAMERA)
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_GALLERY)
    }

    private fun createImageFile():File{
        val timeStamp=SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir=getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".png",
            storageDir
        )
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            REQUEST_CAMERA->if(grantResults.isNotEmpty()&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                openCamera()
            }
            REQUEST_GALLERY->if (grantResults.isNotEmpty()&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                openGallery()
            }
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode!= RESULT_OK)return
        when(requestCode){
            REQUEST_CAMERA->currentPhotoUri?.let { loadAvatarUri(it) }
            REQUEST_GALLERY->data?.data?.let { loadAvatarUri(it) }
        }
    }

    private fun loadAvatarUri(photoUri: Uri){
        Glide.with(this)
            .load(photoUri)
            .into(userAvatar)
        currentPhotoUri=photoUri
    }
}