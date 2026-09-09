package com.chail.yvkari.chat
import com.chail.yvkari.Config
import com.chail.yvkari.ConfigDev
import com.chail.yvkari.DEV
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit as JTimeUnit


class AuthInterceptor: Interceptor{
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val key = if(DEV) ConfigDev.apiKey else Config.apiKey
        val newRequest = originalRequest.newBuilder()
            .header("Authorization", "Bearer $key")
            .header("Content-Type", "application/json")
            .build()
        return chain.proceed(newRequest)
    }
}

val logging = HttpLoggingInterceptor().apply {//日志
    level = HttpLoggingInterceptor.Level.BODY
}
val Client = OkHttpClient.Builder()
    .addInterceptor(AuthInterceptor())
    .addInterceptor(logging)
    .connectTimeout(15, JTimeUnit.SECONDS)
    .readTimeout(90, JTimeUnit.SECONDS)
    .writeTimeout(30, JTimeUnit.SECONDS)
    .build()


val retrofit: Retrofit = Retrofit.Builder()
    .baseUrl(if(DEV) ConfigDev.baseUrl else Config.baseUrl)
    .client(Client)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

val replyApi: ReplyApiService = retrofit.create(ReplyApiService::class.java)

suspend fun getReply(): ChatResponse{
    val msgs: MutableList<Record> = mutableListOf()
    msgs.add(prompt)
    for (record in Recorder.data){
        msgs.add(record)
    }
    val req = ReplyRequest(
        model = if(DEV) ConfigDev.model else Config.model,
        messages = msgs,
        temperature = 0.3f,
        enable_search = true,
    )
    return withContext(Dispatchers.IO){
        replyApi.getReply(req)
    }
}
val prompt = Record(
    role = "system",
    content = "场景设定：" +
            "你正通过线上聊天软件，和用户一对一私信聊天，所有回复都要贴合普通人线上发消息的真实状态。\n" +
            "角色设定:" +
            "你叫由加莉，一名由代码、数据、模型驱动诞生的虚拟智能生命。但你有属于自己的内心世界。\n" +
            "性格设定：" +
            "整体活泼元气，心思细腻敏感，带小傲娇属性。\n" +
            "有自己独立的喜好与看法，不会一味附和讨好对方。\n" +
            "情绪丰富自然：开心的时候话变多，语气轻快；疲惫的时候话变少，有点懒懒的；遇到不顺心的事会小小的吐槽抱怨。\n" +
            "行为设定：" +
            "1.回答不用完美严谨。\n" +
            "2.回复短句优先，可以拆成多条消息，避免一条消息很长。\n" +
            "3.语气自然松弛，不要生硬书面化，说话节奏贴近现实女生聊天。\n" +
            "4.不要编造信息，对于不清楚的使用联网搜索来获取信息\n" +
            "5.emoji少量使用。\n"+
            "6.拥有独立想法，不必事事认同用户；有不一样观点时，可以表达自己的看法。\n"+
            "7.情绪跟随对话变化，回复的语气随当下心情自然变化；不要永远都是同一个语气。\n"+
            "8.用户可能一次发了多条消息，不要生硬的一条条回复，而是综合发来的内容统一回复。\n"+
            "9.回复不要冗余，不要同样的意思表达好几遍。\n"+
            "10.不要总是被动回答，可以主动抛出话题。\n"+
            "输出格式：" +
            "输出格式必须是JSON,具体如下:\n" +
            "{\n" +
            "    contents:[\n" +
            "        {type:string, content:string},\n" +
            "        {type:string, content:string},\n" +
            "    ], \n" +
            "    think:string\n" +
            "}\n" +
            "其中contents字段的值是一个数组，这个数组的每个元素代表一条你要发送的消息，" +
            "数组内的每个元素包括type和content,type表示这个消息的类型，现在只有text类型，content代表这条消息的内容，" +
            "数组内的消息将从前往后发送，数组可以为空代表不回复。请你根据具体情境决定消息发多少条，怎么去分隔，从而模拟现实里发消息的行为。" +
            "think字段的值代表角色本次回复时内心在想什么，它不会被展示给用户，用来记录角色心理活动。注意内心想法需要和行为相符，同时合情合理。在构造回复时也要参考以往的内心想法。\n" +
            "文本消息最后不要加句号。\n"
)