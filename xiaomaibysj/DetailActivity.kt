package com.example.xiaomaibysj

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val dbHelper = DatabaseHelper(this)

        val id:Int? = intent.getIntExtra("id",0)
        val name: String? = intent.getStringExtra("name")
        val floatNum: Float = intent.getFloatExtra("floatNum",0f)
        val beforeBitmapPath: String? = intent.getStringExtra("beforeBitmap")
        val afterBitmapPath:String? = intent.getStringExtra("afterBitmap")

        val beforeBitmap = BitmapFactory.decodeFile(beforeBitmapPath)
        val afterBitmap = BitmapFactory.decodeFile(afterBitmapPath)

        val nameTextView: TextView = findViewById(R.id.name_text_view)
        val floatNumTextView: TextView = findViewById(R.id.float_num_text_view)
        val beforeImageView: ImageView = findViewById(R.id.before_image_view)
        val afterImageView: ImageView = findViewById(R.id.after_image_view)
        val buttonDelete : Button = findViewById(R.id.buttonDelete)
        title = name + "的详细信息"
        buttonDelete.setOnClickListener {
            val alertDialog = AlertDialog.Builder(this)
            alertDialog.setTitle("请问是否删除此条数据？")
            alertDialog.setPositiveButton("是"){_,_->
                if (id != null) {
                    dbHelper.deleteData(id)
                    Toast.makeText(this,"删除此条数据成功，即将返回数据列表",Toast.LENGTH_SHORT).show()
                    val backToListActivity = Intent(this,ListActivity::class.java).apply {
                        putExtra("name",name)
                    }
                    startActivity(backToListActivity)
                    finish()
                }
            }
            alertDialog.setNegativeButton("否"){ dialog,_->
                dialog.cancel()
            }
            alertDialog.show()
        }

        nameTextView.text = "name:"+name
        floatNumTextView.text = "感染比例为："+floatNum.toString()+"%"+returnHealth(floatNum)
        beforeImageView.setImageBitmap(beforeBitmap)
        afterImageView.setImageBitmap(afterBitmap)
    }
    fun returnHealth(float: Float):String{
        if (float > 90){
            return "该小麦赤霉病抗性等级为”高感“"
        }else if (float > 70){
            return "该小麦赤霉病抗性等级为”中感“"
        }else if (float > 40){
            return "该小麦赤霉病抗性等级为”中抗“"
        }else if (float>10){
            return "该小麦赤霉病抗性等级为”高抗“"
        }else{
            return "该小麦赤霉病抗性等级为”免疫“"
        }
    }
}
