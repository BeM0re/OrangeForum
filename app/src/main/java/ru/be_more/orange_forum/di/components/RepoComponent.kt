package ru.be_more.orange_forum.di.components

import dagger.Component
import ru.be_more.orange_forum.di.modules.RepoModule
import ru.be_more.orange_forum.interactors.ThreadInteractor
import ru.be_more.orange_forum.repositories.DvachApiRepository
import ru.be_more.orange_forum.ui.board.BoardPresenter
import ru.be_more.orange_forum.ui.category.CategoryPresenter
import ru.be_more.orange_forum.ui.download.DownloadPresenter
import ru.be_more.orange_forum.ui.favorire.FavoritePresenter
import ru.be_more.orange_forum.ui.main.MainPresenter
import ru.be_more.orange_forum.ui.post.PostPresenter
import ru.be_more.orange_forum.ui.response.ResponsePresenter
import ru.be_more.orange_forum.ui.thread.ThreadPresenter
import javax.inject.Singleton

@Singleton
@Component(modules = [RepoModule::class])
interface RepoComponent {
    fun inject(presenter: CategoryPresenter)

    fun inject(presenter: BoardPresenter)

    fun inject(presenter: ThreadPresenter)

    fun inject(presenter: PostPresenter)

    fun inject(presenter: MainPresenter)

    fun inject(presenter: DownloadPresenter)

    fun inject(presenter: FavoritePresenter)

    fun inject(presenter: ResponsePresenter)

    fun inject(interactor: ThreadInteractor)

    fun getApiRepo(): DvachApiRepository

}