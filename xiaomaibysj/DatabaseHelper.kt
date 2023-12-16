package com.example.xiaomaibysj


import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 3
        private const val DATABASE_NAME = "SampleDatabase"
        private const val TABLE_NAME = "Images"
        private const val KEY_ID = "id"
        private const val KEY_NAME = "name"
        private const val KEY_FLOAT_NUM = "floatNum"
        private const val KEY_BEFORE_BITMAP = "beforeBitmap"
        private const val KEY_AFTER_BITMAP = "afterBitmap"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_TABLE = "CREATE TABLE $TABLE_NAME ($KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT, $KEY_NAME TEXT, $KEY_FLOAT_NUM REAL, $KEY_BEFORE_BITMAP STRING, $KEY_AFTER_BITMAP STRING)"
        db.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun addData(data: DataModel): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(KEY_NAME, data.name)
            put(KEY_FLOAT_NUM, data.floatNum)
            put(KEY_BEFORE_BITMAP, data.beforeBitmap)
            put(KEY_AFTER_BITMAP, data.afterBitmap)
        }
        val success = db.insert(TABLE_NAME, null, contentValues)
        db.close()
        return success
    }
    fun deleteData(id: Int): Boolean {
        val db = this.writableDatabase
        val whereClause = "$KEY_ID = ?"
        val whereArgs = arrayOf(id.toString())
        val success = db.delete(TABLE_NAME, whereClause, whereArgs) > 0
        db.close()
        return success
    }


    @SuppressLint("Range")
    fun getAllData(): ArrayList<DataModel> {
        val dataList = ArrayList<DataModel>()
        val selectQuery = "SELECT * FROM $TABLE_NAME "
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery(selectQuery, null)
        if (cursor.moveToFirst()) {
            do {
//                val id = cursor.getInt(cursor.getColumnIndex(KEY_ID))
                val id = cursor.getInt(cursor.getColumnIndex(KEY_ID))
                val name = cursor.getString(cursor.getColumnIndex(KEY_NAME))
                val floatNum = cursor.getFloat(cursor.getColumnIndex(KEY_FLOAT_NUM))
                val beforeBitmap = cursor.getString(cursor.getColumnIndex(KEY_BEFORE_BITMAP))
                val afterBitmap = cursor.getString(cursor.getColumnIndex(KEY_AFTER_BITMAP))

                val data = DataModel(id, name, floatNum, beforeBitmap, afterBitmap)
                dataList.add(data)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return dataList
    }
}