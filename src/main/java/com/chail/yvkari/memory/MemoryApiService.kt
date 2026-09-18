package com.chail.yvkari.memory

import retrofit2.http.Body
import retrofit2.http.POST
import com.chail.yvkari.chat.data.Record

data class AddMemoryNode(
    val memory_node_id: String,
    val content: String,
    val event: String,
    val old_content: String
)
data class SearchMemoryNode(
    val memory_node_id: String,
    val content: String,
    val created_at: Long,
    val updated_at: Long
)
data class AddBody(
    val user_id: String,
    val messages: List<Record>,
    val memory_library_id: String = "5745f810bd324ae0894137da51dc57bf"
)

data class AddRes(
    val request_id: String,
    val memory_nodes: List<AddMemoryNode>
)

data class SearchBody(
    val user_id: String,
    val messages: List<Record>,
    val memory_library_id: String = "5745f810bd324ae0894137da51dc57bf"
)
data class SearchRes(
    val request_id: String,
    val memory_nodes: List<SearchMemoryNode>
)

interface MemoryApiService{
    @POST("/api/v2/apps/memory/add")
    suspend fun AddMemory(@Body req: AddBody): AddRes

    @POST("/api/v2/apps/memory/memory_nodes/search")
    suspend fun SearchMemory(@Body req: SearchBody): SearchRes
}