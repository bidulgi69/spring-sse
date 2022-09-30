package kr.dove.communication.api

data class Message(
    val id: String? = null,
    val sender: String = "anonymous",
    val message: String = "",
)
