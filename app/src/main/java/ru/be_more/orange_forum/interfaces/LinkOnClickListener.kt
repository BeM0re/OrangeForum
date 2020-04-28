package ru.be_more.orange_forum.interfaces

interface LinkOnClickListener {

    fun onClick(chanLink: Triple<String, String, String>?)
    fun onClick(externalLink: String?)
}