package com.chail.yvkari.chat
import retrofit2.http.POST
import retrofit2.http.Body

data class ResponseFormat(
    val type: String
)

data class TokenDetail(
    val cached_tokens: Int
)
data class Usage(
    val prompt_tokens: Int,
    val completion_tokens: Int,
    val total_tokens: Int,
    val prompt_tokens_details: TokenDetail
)


data class MessageContent(
    val contents: List<MsgContent>,
    val think: String
)
data class Message(
    val role: String,
    val content: String
)
data class Choice(
    val message: Message,
    val finish_reason: String,
    val index: Int,
    val logprobs: String?
)
data class ChatResponse(
    val id: String,
    val model: String,
    val system_fingerprint: String?,    //null
    val created: Int,
    val `object`: String,
    val usage: Usage,
    val choices: List<Choice>
)
data class ReplyRequest(
    val model: String,
    val messages: List<Record>,
    val temperature: Float,
    val enable_search: Boolean,
    val response_format: ResponseFormat = ResponseFormat(type = "json_object")
)

interface ReplyApiService {
    @POST("/compatible-mode/v1/chat/completions")
    suspend fun getReply(@Body req: ReplyRequest): ChatResponse
}