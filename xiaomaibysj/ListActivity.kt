package com.example.xiaomaibysj

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class ListActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        title = "已检测图片列表"

        dbHelper = DatabaseHelper(this)

        val listView: ListView = findViewById(R.id.list_view)
        val dataList = dbHelper.getAllData()
        val nameList = dataList.map { "name: " + it.name+ " 感染比例：" +String.format("%.2f", it.floatNum)+"% "+ it.floatNum?.let { it1 -> returnHealth(it1) } }
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, nameList)


        listView.adapter = adapter

        listView.onItemClickListener = OnItemClickListener { _, _, position, _ ->
            val data = dataList[position]
            val intent = Intent(this, DetailActivity::class.java).apply {
                putExtra("id", data.id)
                putExtra("name", data.name)
                putExtra("floatNum", data.floatNum)
                putExtra("beforeBitmap", data.beforeBitmap)
                putExtra("afterBitmap", data.afterBitmap)
            }
            startActivity(intent)
            finish()
        }

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
}