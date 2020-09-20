package ru.be_more.orange_forum.di.modules

import org.koin.dsl.module
import ru.be_more.orange_forum.ui.board.BoardPresenter
import ru.be_more.orange_forum.ui.category.CategoryPresenter
import ru.be_more.orange_forum.ui.category.CategoryViewModel
import ru.be_more.orange_forum.ui.download.DownloadPresenter
import ru.be_more.orange_forum.ui.favorire.FavoritePresenter
import ru.be_more.orange_forum.ui.main.MainPresenter
import ru.be_more.orange_forum.ui.post.PostPresenter
import ru.be_more.orange_forum.ui.response.ResponsePresenter
import ru.be_more.orange_forum.ui.thread.ThreadPresenter


@JvmField
val presenterModule = module {
    factory { params -> MainPresenter(get(), get(), get(), params[0]) }
    single { params -> CategoryPresenter(get(), params[0]) }
    single { CategoryViewModel(get()) }
    factory { params -> BoardPresenter(get(), get(), get(), params[0]) }
    factory { params -> ThreadPresenter(get(), get(), params[0]) }
    factory { params -> PostPresenter(params[0]) }
    factory { params -> ResponsePresenter(get(), params[0]) }
    single { params -> FavoritePresenter(get(), get(), params[0]) }
    single { params -> DownloadPresenter(get(), get(), get(), params[0]) }
 }
