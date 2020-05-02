package ru.be_more.orange_forum.interfaces

interface LinkOnClickListener {

    fun onLinkClick(chanLink: Triple<String, String, String>?)
    fun onLinkClick(externalLink: String?)
    fun onLinkClick(postNum: Int)
}