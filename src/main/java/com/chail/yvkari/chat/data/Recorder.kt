package com.chail.yvkari.chat.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.chail.yvkari.Config
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

data class Record(
    @SerializedName("role")
    val role: String,
    @SerializedName("content")//防止被混淆
    val content: String
)

object Recorder {
    private var data: MutableList<Record> = mutableListOf()
    private var memoryBuff = mutableListOf<Record>()
    private var midMem: String = ""
    private val mutex = Mutex()//防止并发操作引发bug
    private val gson = Gson()
    private lateinit var sp: SharedPreferences

    fun init(context: Context){
        this.sp = context.getSharedPreferences("record", Context.MODE_PRIVATE)
    }
    suspend fun push(msg: Record) {
        mutex.withLock {
            val lim = Config.recordLimit
            while (data.size > lim) {
                memoryBuff.add(data[0])
                data.removeAt(0)
            }
            data.add(msg)
        }
        save()
    }

    suspend fun getList(): List<Record> = mutex.withLock {
        data.toList()
    }
    suspend fun getMem(): List<Record> = mutex.withLock {
        memoryBuff.toList()
    }

    suspend fun getMidMem(): String = mutex.withLock {
        midMem
    }
    suspend fun setMidmem(text: String){
        mutex.withLock {
            midMem = text
        }
    }
    fun getMemSize(): Int{
        return memoryBuff.size
    }
    suspend fun clearMem(){
        mutex.withLock {
            memoryBuff.clear()
        }
    }
    suspend fun clear(){
        mutex.withLock{
            data.clear()
        }
        save()
    }

    fun save(){
        val dataStr = gson.toJson(data)
        val memStr = gson.toJson(memoryBuff)
        val midMemStr = gson.toJson(midMem)
        sp.edit { putString("records", dataStr) }
        sp.edit{putString("memory_buff", memStr)}
        sp.edit{putString("mid_memory", midMemStr)}
    }

    fun load(){
        val dataStr = sp.getString("records", "[]")?:"[]"
        val memStr = sp.getString("memory_buff", "[]")?:"[]"
        midMem = sp.getString("mid_memory", "")?:""
        val type = TypeToken.getParameterized(List::class.java, Record::class.java).type
        data =  gson.fromJson(dataStr, type)
        memoryBuff = gson.fromJson(memStr, type)
    }
}