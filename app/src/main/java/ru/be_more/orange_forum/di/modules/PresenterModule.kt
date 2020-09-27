package ru.be_more.orange_forum.di.modules

import org.koin.dsl.module
import ru.be_more.orange_forum.ui.PresentationContract
import ru.be_more.orange_forum.ui.board.BoardViewModelImpl
import ru.be_more.orange_forum.ui.category.CategoryViewModelImpl
import ru.be_more.orange_forum.ui.download.DownloadViewModelImpl
import ru.be_more.orange_forum.ui.favorire.FavoriteViewModelImpl
import ru.be_more.orange_forum.ui.response.ResponseViewModelImpl
import ru.be_more.orange_forum.ui.thread.ThreadViewModelImpl

@JvmField
val presenterModule = module {
    single <PresentationContract.CategoryViewModel> { CategoryViewModelImpl(get()) }
    single <PresentationContract.BoardViewModel> { BoardViewModelImpl(get(), get(), get()) }
    single <PresentationContract.ThreadViewModel> { ThreadViewModelImpl(get(), get()) }
    single <PresentationContract.ResponseViewModel> { ResponseViewModelImpl(get()) }
    single <PresentationContract.FavoriteViewModel> { FavoriteViewModelImpl(get(), get()) }
    single <PresentationContract.DownloadViewModel> { DownloadViewModelImpl(get(), get(), get()) }
 }
