package com.example.xiaomaibysj

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        全屏显示
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_main)

//        设置Activity标题
        title = "小麦赤霉病抗性鉴定系统"

//        定义两个Button
        val button_start_app = findViewById<Button>(R.id.start_app)
        val button_end_app = findViewById<Button>(R.id.end_app)

//        设置按钮点击事件。进入DetectActivity
        button_start_app.setOnClickListener {
            val startApp = Intent(this,DetectActivity::class.java)
            startActivity(startApp)
        }

//        设置按钮点击事件，退出APP
        button_end_app.setOnClickListener {
            endApp()
        }


    }

//    设置菜单
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu,menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_send_email -> {
                val intent1 = Intent(this, EmailActivity::class.java)
                startActivity(intent1)
                return true
            }
            R.id.menu_information -> {
                val intent2 = Intent(this,AboutWheatActivity::class.java)
                startActivity(intent2)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }


    //    点击退出APP的方法
    private fun endApp(){
//        创建对话形式
        val builder = AlertDialog.Builder(this,R.style.MyAlertDialogStyle)
        builder.setCancelable(false)
//        设置弹窗提示的标题和内容
        builder.setTitle("退出检测")
        builder.setTitle("请问是否要退出检测？")
//        是
        builder.setPositiveButton("是"){
            dialog,which ->
            finish()
        }
//        否
        builder.setNegativeButton("否"){
            dialog,which ->
            dialog.dismiss()
        }
//        创建显示界面
        val dialog = builder.create()
        dialog.setOnShowListener{
            val positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
            positiveButton.setBackgroundColor(ContextCompat.getColor(this,R.color.golden))
            positiveButton.setTextColor(ContextCompat.getColor(this,R.color.black))
            val negativeButton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
            negativeButton.setBackgroundColor(ContextCompat.getColor(this, R.color.golden))
            negativeButton.setTextColor(ContextCompat.getColor(this,R.color.black))
        }
        dialog.show()
    }

}