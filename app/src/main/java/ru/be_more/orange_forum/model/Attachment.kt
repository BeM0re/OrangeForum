package ru.be_more.orange_forum.model

data class Attachment(
    var url: String,
    var duration: String?
) : ModalContent()