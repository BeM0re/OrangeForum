package ru.be_more.orange_forum.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.be_more.orange_forum.presentation.screens.board.BoardViewModel
import ru.be_more.orange_forum.presentation.screens.category.CategoryViewModel
import ru.be_more.orange_forum.presentation.screens.favorite.FavoriteViewModel
import ru.be_more.orange_forum.presentation.screens.main.MainViewModel
import ru.be_more.orange_forum.presentation.screens.posting.PostingViewModel
import ru.be_more.orange_forum.presentation.screens.queue.QueueViewModel
import ru.be_more.orange_forum.presentation.screens.thread.ThreadViewModel
import ru.be_more.orange_forum.utils.ViewModelProvider

@JvmField
val viewModelModule = module {
    single { ViewModelProvider() }
    viewModel { MainViewModel(get()) }
    viewModel { CategoryViewModel(get(), get()) }
    viewModel { BoardViewModel(get(), get(), get(), get(), get(), get()) }
    viewModel { ThreadViewModel(get(), get(), get(), get(), get(), get(), get()) }
    viewModel { PostingViewModel(get(), get(), get(), get(), get()) }
    viewModel { QueueViewModel(get(), get(), get()) }
    viewModel { FavoriteViewModel(get(), get(), get()) }
 }
