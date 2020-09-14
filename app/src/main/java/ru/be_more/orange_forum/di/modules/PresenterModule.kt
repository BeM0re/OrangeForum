package ru.be_more.orange_forum.di.modules

import dagger.Module
import dagger.Provides
import ru.be_more.orange_forum.domain.InteractorContract
import ru.be_more.orange_forum.ui.main.MainPresenter


@Module
class PresenterModule {
    @Provides
    fun provideMain(boardInteractor : InteractorContract.BoardInteractor,
                    threadInteractor : InteractorContract.ThreadInteractor,
                    postInteractor : InteractorContract.PostInteractor
    ): MainPresenter = MainPresenter(boardInteractor, threadInteractor, postInteractor)

}