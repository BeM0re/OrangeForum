package ru.be_more.orange_forum.di

import org.koin.dsl.module
import ru.be_more.orange_forum.presentation.PresentationContract
import ru.be_more.orange_forum.presentation.screens.board.BoardViewModelImpl
import ru.be_more.orange_forum.presentation.screens.category.CategoryViewModelImpl
import ru.be_more.orange_forum.presentation.screens.download.DownloadViewModelImpl
import ru.be_more.orange_forum.presentation.screens.favorire.FavoriteViewModelImpl
import ru.be_more.orange_forum.presentation.screens.response.ResponseViewModelImpl
import ru.be_more.orange_forum.presentation.screens.thread.ThreadViewModelImpl

@JvmField
val presenterModule = module {
    single <PresentationContract.CategoryViewModel> { CategoryViewModelImpl(get()) }
    single <PresentationContract.BoardViewModel> { BoardViewModelImpl(get(), get(), get()) }
    single <PresentationContract.ThreadViewModel> { ThreadViewModelImpl(get(), get()) }
    single <PresentationContract.ResponseViewModel> { ResponseViewModelImpl(get()) }
    single <PresentationContract.FavoriteViewModel> { FavoriteViewModelImpl(get(), get()) }
    single <PresentationContract.DownloadViewModel> { DownloadViewModelImpl(get(), get(), get()) }
 }
