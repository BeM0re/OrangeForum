package ru.be_more.orange_forum.di

import dagger.Binds
import dagger.Module
import ru.be_more.orange_forum.domain.contracts.InteractorContract
import ru.be_more.orange_forum.domain.interactors.BoardInteractorImpl
import ru.be_more.orange_forum.domain.interactors.CategoryInteractorImpl
import ru.be_more.orange_forum.domain.interactors.FavoriteInteractorImpl
import ru.be_more.orange_forum.domain.interactors.PostInteractorImpl
import ru.be_more.orange_forum.domain.interactors.QueueInteractorImpl
import ru.be_more.orange_forum.domain.interactors.ReplyInteractorImpl
import ru.be_more.orange_forum.domain.interactors.ThreadInteractorImpl

@Module
interface InteractorModule {

    @Binds
    fun bindCategoryInteractor(categoryInteractor: CategoryInteractorImpl): InteractorContract.CategoryInteractor

    @Binds
    fun bindBoardInteractor(boardInteractor: BoardInteractorImpl): InteractorContract.BoardInteractor

    @Binds
    fun bindThreadInteractor(categoryInteractor: ThreadInteractorImpl): InteractorContract.ThreadInteractor

    @Binds
    fun bindPostInteractor(postInteractor: PostInteractorImpl): InteractorContract.PostInteractor

    @Binds
    fun bindReplyInteractor(replyInteractor: ReplyInteractorImpl): InteractorContract.ReplyInteractor

    @Binds
    fun bindQueueInteractor(queueInteractor: QueueInteractorImpl): InteractorContract.QueueInteractor

    @Binds
    fun bindFavoriteInteractor(favoriteInteractor: FavoriteInteractorImpl): InteractorContract.FavoriteInteractor
}