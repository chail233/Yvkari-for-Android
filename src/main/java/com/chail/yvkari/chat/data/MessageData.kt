package com.chail.yvkari.chat.data

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "message_data")
data class Message(//用于UI的数据
    @PrimaryKey(autoGenerate = true) val id: Long=0,
    val role: Role,
    val content: String,
    val time: String,
    val timeStamp: Long
)


@Dao
interface MessageDataDao{
    @Query("SELECT * FROM message_data ORDER BY -timeStamp DESC")
    fun getAllMessage(): Flow<List<Message>>

    @Insert
    suspend fun insertMessage(msg: Message)

    @Query("DELETE FROM message_data")
    suspend fun clearAll()
}


