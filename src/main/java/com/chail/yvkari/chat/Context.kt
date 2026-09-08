package com.chail.yvkari.chat

enum class Role{
    User,
    Ai
}
enum class MsgType{
    Text,
    Image
}
data class MsgContent(
    val type: MsgType,
    val content: String
)

interface BuildMsg{
    fun build(): String
}

data class UserMsg(
    val role: Role,
    val time: String,
    val content: MsgContent
): BuildMsg{
    override fun build(): String {
        val type = if (content.type== MsgType.Text) "文本" else "图片"
        return "[$time][$type]${content.content}"
    }
}

data class AIMsg(
    val role: Role,
    val time: String,
    val content: List<MsgContent>,
    val think: String,
    val tokens: Int
): BuildMsg{
    override fun build(): String {
        var text = "[$time][内心:$think]\n"
        for(item in content){
            text += "[${item.type}]${item.content}\n"
        }
        return text
    }
}
