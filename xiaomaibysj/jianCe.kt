package com.example.xiaomaibysj

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime

class jianCe : AppCompatActivity() {


//    中心的图片
    private lateinit var mainImage : ImageView
//    显示染病程度
    private lateinit var textViewRandomNumber: TextView

//    前后的Bitmap
    private lateinit var initialBitmap: Bitmap
    private lateinit var segedBitmap:Bitmap

//    相册读取
    private val pickImage = 100
//    拍摄照片
    private val takeImage = 101

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    private val context: Context = this


//    Button
    private lateinit var buttonPickImage:Button
    private lateinit var buttonTakeImage:Button
    private lateinit var buttonSendImage:Button
    private lateinit var buttonSaveImage:Button
    private lateinit var buttonShowBitmap:Button
    private lateinit var buttonShowSegedbitmap:Button



//    图片存储路径
    private var path1:String? = null
    private var path2:String? = null
    private var floatNum: Float? = null
    private lateinit var dbHelper:DatabaseHelper

    lateinit var takeUri: Uri
    lateinit var outPutImage:File



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        setContentView(R.layout.activity_jian_ce)

        dbHelper = DatabaseHelper(this)

        buttonPickImage = findViewById(R.id.button_pick_image)
        buttonTakeImage = findViewById(R.id.button_take_image)

        buttonSendImage = findViewById(R.id.button_send_image)
        buttonSendImage.isEnabled = false
        buttonSaveImage = findViewById(R.id.button_save_image)
        buttonSaveImage.isEnabled = false
        buttonShowBitmap = findViewById(R.id.button_showBeforeBitmap)
        buttonShowBitmap.isEnabled = false
        buttonShowSegedbitmap = findViewById(R.id.button_segBitmap)
        buttonShowSegedbitmap.isEnabled = false

        mainImage = findViewById(R.id.image_view_detect)
        textViewRandomNumber = findViewById(R.id.text_view_random_number)



        buttonPickImage.setOnClickListener {
            val gallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery,pickImage)
            buttonSendImage.isEnabled = true
        }

        buttonTakeImage.setOnClickListener {
            outPutImage = File(externalCacheDir,"output.jpg")
            if(outPutImage.exists()){
                outPutImage.delete()
            }
            outPutImage.createNewFile()
            takeUri = if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
                FileProvider.getUriForFile(this,packageName+".fileprovider",outPutImage)
            }else{
                Uri.fromFile(outPutImage)
            }
            val takeIntent = Intent("android.media.action.IMAGE_CAPTURE")
            takeIntent.putExtra(MediaStore.EXTRA_OUTPUT,takeUri)
            startActivityForResult(takeIntent,takeImage)
            buttonSendImage.isEnabled = true
        }

        buttonSendImage.setOnClickListener {


            coroutineScope.launch {
                //            其它按钮设置为不可用
                buttonSaveImage.isEnabled = false
                buttonShowBitmap.isEnabled = false
                buttonShowSegedbitmap.isEnabled = false
                buttonPickImage.isEnabled = false
                buttonTakeImage.isEnabled = false
                buttonSendImage.isEnabled = false

                textViewRandomNumber.text = ""
                buttonSaveImage.setText("正在分割检测中")
                buttonShowBitmap.setText("正在分割检测中")
                buttonShowSegedbitmap.setText("正在分割检测中")
                buttonPickImage.setText("正在分割检测中")
                buttonTakeImage.setText("正在分割检测中")
                buttonSendImage.setText("正在分割检测中")
                val bitmap = (mainImage.drawable as BitmapDrawable).bitmap
                initialBitmap = bitmap
                path1 = saveBitmapToFile(context, initialBitmap)
                sendImage(bitmap)
                buttonSaveImage.setText("保存")
                buttonShowBitmap.setText("原图")
                buttonShowSegedbitmap.setText("分割后")
                buttonPickImage.setText("选择图片")
                buttonTakeImage.setText("拍摄图片")
                buttonSendImage.setText("检测图片")
                buttonSaveImage.isEnabled = true
                buttonShowBitmap.isEnabled = true
                buttonShowSegedbitmap.isEnabled = true
                buttonPickImage.isEnabled = true
                buttonTakeImage.isEnabled = true
            }

        }

        buttonShowBitmap.setOnClickListener {
            mainImage.setImageBitmap(initialBitmap)
            buttonSendImage.isEnabled = true
            buttonSaveImage.isEnabled = false
            buttonSaveImage.setText("切换为“分割后”可保存")
        }

        buttonShowSegedbitmap.setOnClickListener {
            buttonSaveImage.isEnabled = true
            buttonSaveImage.setText("保存")
            mainImage.setImageBitmap(segedBitmap)
            buttonSendImage.isEnabled = false
        }

        buttonSaveImage.setOnClickListener {

//            弹出对话框提示用户输入name
            val alertDialog = AlertDialog.Builder(this)
            alertDialog.setTitle("请输入名称")
            var input = EditText(this)
            alertDialog.setView(input)

            alertDialog.setNeutralButton("自动生成并存储"){_,_->
                val nameDate = getCurrentDateTimeString()
                //                存储到数据库中
                val number= floatNum
                val data = DataModel(0,nameDate,number,path1,path2)
                dbHelper.addData(data)
                Toast.makeText(this,"图片保存成功",Toast.LENGTH_SHORT).show()
            }
            alertDialog.setPositiveButton("存储"){_,_->
                val name = input.text.toString().trim()

//                存储到数据库中
                val number= floatNum
                val data = DataModel(0,name,number,path1,path2)
                dbHelper.addData(data)
                Toast.makeText(this,"图片保存成功",Toast.LENGTH_SHORT).show()
            }

            alertDialog.setNegativeButton("取消"){ dialog,_->
                dialog.cancel()
            }

            alertDialog.show()
        }



    }

    private suspend fun sendImage(bitmap:Bitmap){
//        flask服务器的URL
        val serverUrl = "http://10.0.2.2:5000/upload"

        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG,100,stream)
        val byteArray = stream.toByteArray()

//        将将字节数组写入文件
        val file = File.createTempFile("image",".png",cacheDir)
        val fileOutputStream = FileOutputStream(file)
        fileOutputStream.write(byteArray)
        fileOutputStream.close()


        withContext(Dispatchers.IO){
            val client = OkHttpClient()
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG,100,byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()

//        创建请求以将图像发送给Flask服务器
            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file",file.name,byteArray.toRequestBody("image/png".toMediaType()))
                .build()

            val request = Request.Builder()
                .url(serverUrl)
                .post(requestBody)
                .build()

//        处理返回的JSON数据
            client.newCall(request).execute().use { response ->
                val responseBody: ResponseBody? = response.body
                if (responseBody != null && response.isSuccessful){
                    val gson = Gson()
                    val responseData = gson.fromJson(responseBody.string(),ResponsData::class.java)
//                    将图像数据base64编码字节符解码为字节数组
                    val imageData :ByteArray = Base64.decode(responseData.croppedImage, Base64.DEFAULT)


                    val croppedImage = BitmapFactory.decodeByteArray(imageData,0,imageData.size)
                    val width5 = croppedImage.width*5
                    val height5 = croppedImage.height*5
                    val scaledBitmap = Bitmap.createScaledBitmap(croppedImage, width5, height5, false)

//                更新UI
                    runOnUiThread{
                        segedBitmap = scaledBitmap
                        path2 = saveBitmapToFile(context,segedBitmap)
                        floatNum = responseData.randomNumber
                        mainImage.setImageBitmap(scaledBitmap)
                        textViewRandomNumber.text = "感病比例是"+responseData.randomNumber.toString()+"%"+"属于"+returnHealth(responseData.randomNumber)+"类型"

                    }
                }

            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == pickImage){
            val imageUri: Uri? = data?.data
            mainImage.setImageURI(imageUri)
        }
        if (resultCode == RESULT_OK && requestCode == takeImage){
            val bitmapTake:Bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(takeUri))
            mainImage.setImageBitmap(bitmapTake)
        }
    }

    fun saveBitmapToFile(context: Context, bitmap: Bitmap): String? {
        // 获取应用程序的可写目录
        val directory = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        // 为文件生成唯一的文件名
        val fileName = "image_${System.currentTimeMillis()}.jpg"

        // 创建文件对象
        val file = File(directory, fileName)

        try {
            // 保存 Bitmap 到文件系统中
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            outputStream.close()

            // 获取文件的绝对路径
            return file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentDateTimeString(): String {
        val currentDateTime = LocalDateTime.now()

        val year = currentDateTime.year
        val month = currentDateTime.monthValue
        val day = currentDateTime.dayOfMonth

        val hour = currentDateTime.hour
        val minute = currentDateTime.minute
        val second = currentDateTime.second

        return "$year-$month-$day-$hour:$minute:$second"
    }

    fun returnHealth(float: Float):String{
        if (float > 90){
            return "高感"
        }else if (float > 70){
            return "中感"
        }else if (float > 40){
            return "中抗"
        }else if (float>10){
            return "高抗"
        }else{
            return "免疫"
        }
    }

    data class ResponsData(
        @SerializedName("cropped_image") val croppedImage:String,
        @SerializedName("random_number") val randomNumber: Float
    )
}