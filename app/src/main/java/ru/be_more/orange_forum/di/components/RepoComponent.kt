package ru.be_more.orange_forum.di.components

import dagger.Component
import ru.be_more.orange_forum.di.modules.RepoModule
import ru.be_more.orange_forum.repositories.DvachApiRepository
import ru.be_more.orange_forum.ui.board.BoardPresenter
import ru.be_more.orange_forum.ui.category.CategoryPresenter
import ru.be_more.orange_forum.ui.thread.ThreadPresenter
import javax.inject.Singleton

@Singleton
@Component(modules = [RepoModule::class])
interface RepoComponent {
    fun inject(presenter: CategoryPresenter)

    fun inject(presenter: BoardPresenter)

    fun inject(presenter: ThreadPresenter)

    fun getApiRepo(): DvachApiRepository

}