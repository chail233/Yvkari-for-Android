package com.chail.yvkari.chat.data

enum class Role{
    User,
    Ai
}
data class MsgContent(
    val type: String,
    val content: String
)

interface BuildMsg{
    fun build(): String
}

data class UserMsg(
    val time: String,
    val content: MsgContent
): BuildMsg{
    override fun build(): String {
        return "[$time][${content.type}]${content.content}"
    }
}

data class AIMsg(
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
