package ru.be_more.orange_forum.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.be_more.orange_forum.presentation.screens.board.BoardViewModel
import ru.be_more.orange_forum.presentation.screens.category.CategoryViewModel
import ru.be_more.orange_forum.presentation.screens.favorite.FavoriteViewModel
import ru.be_more.orange_forum.presentation.screens.posting.PostingViewModel
import ru.be_more.orange_forum.presentation.screens.queue.QueueViewModel
import ru.be_more.orange_forum.presentation.screens.thread.ThreadViewModel
import ru.be_more.orange_forum.utils.DaggerViewModelFactory
import javax.inject.Singleton

@Module
interface VmBindModule {

    @Binds
    @IntoMap
    @Singleton
    @ViewModelKey(CategoryViewModel::class)
    fun categoryViewModel(viewModel: CategoryViewModel): ViewModel

    @Binds
    @IntoMap
    @Singleton
    @ViewModelKey(QueueViewModel::class)
    fun queueViewModel(viewModel: QueueViewModel): ViewModel

    @Binds
    @IntoMap
    @Singleton
    @ViewModelKey(FavoriteViewModel::class)
    fun favoriteViewModel(viewModel: FavoriteViewModel): ViewModel

    @Binds
    @Singleton
    fun bindsViewModelFactory(factory: DaggerViewModelFactory): ViewModelProvider.Factory
}