package ru.be_more.orange_forum.services

import ru.be_more.orange_forum.services.BashQuoteApi
import ru.be_more.orange_forum.services.RetrofitFactory

const val DVACH_ROOT_URL = "https://2ch.hk/"

object ApiFactory{

    val dvachApi : BashQuoteApi = RetrofitFactory.retrofit(DVACH_ROOT_URL)
        .create(BashQuoteApi::class.java)
}