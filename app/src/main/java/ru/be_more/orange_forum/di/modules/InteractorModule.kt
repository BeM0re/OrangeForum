package ru.be_more.orange_forum.di.modules

import dagger.Module
import dagger.Provides
import ru.be_more.orange_forum.data.local.DbContract
import ru.be_more.orange_forum.data.local.storage.StorageContract
import ru.be_more.orange_forum.data.remote.RemoteContract
import ru.be_more.orange_forum.domain.InteractorContract
import ru.be_more.orange_forum.domain.interactors.*

@Module
class InteractorModule {
    @Provides
    fun provideCategory( repository: RemoteContract.CategoryRepository):
            InteractorContract.CategoryInteractor = CategoryInteractorImpl(repository)

    @Provides
    fun provideBoard(
        apiRepository: RemoteContract.BoardRepository,
        dbBoardRepository: DbContract.BoardRepository,
        dbThreadRepository: DbContract.ThreadRepository
    ): InteractorContract.BoardInteractor = BoardInteractorImpl(
        apiRepository, dbBoardRepository, dbThreadRepository)

    @Provides
    fun provideThread(
        apiRepository: RemoteContract.ThreadRepository,
        dbBoardRepository: DbContract.BoardRepository,
        dbThreadRepository: DbContract.ThreadRepository,
        dbPostRepository: DbContract.PostRepository,
        dbFileRepository: DbContract.FileRepository,
        fileRepository: StorageContract.FileRepository
    ): InteractorContract.ThreadInteractor = ThreadInteractorImpl(
        apiRepository,
        dbBoardRepository,
        dbThreadRepository,
        dbPostRepository,
        dbFileRepository,
        fileRepository
    )

    @Provides
    fun providePost(
        dbPostRepository: DbContract.PostRepository,
        dbFileRepository: DbContract.FileRepository
    ): InteractorContract.PostInteractor = PostInteractorImpl(dbPostRepository, dbFileRepository)

    @Provides
    fun provideResponse(
        responseRepository: RemoteContract.ResponseRepository
    ): InteractorContract.ResponseInteractor = ResponseInteractorImpl(responseRepository)

    @Provides
    fun provideDownload(
        dbBoardRepository: DbContract.BoardRepository,
        dbThreadRepository: DbContract.ThreadRepository,
        dbPostRepository: DbContract.PostRepository,
        dbFileRepository: DbContract.FileRepository
    ): InteractorContract.DownloadInteractor = DownloadInteractorImpl(
        dbBoardRepository, dbThreadRepository, dbPostRepository, dbFileRepository)

    @Provides
    fun provideFavorite(
        favoriteRepository: DbContract.FavoriteRepository
    ): InteractorContract.FavoriteInteractor = FavoriteInteractorImpl(favoriteRepository)


}

