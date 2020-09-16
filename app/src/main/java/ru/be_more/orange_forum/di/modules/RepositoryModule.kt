package ru.be_more.orange_forum.di.modules

//import dagger.Module
//import dagger.Provides
//import dagger.hilt.InstallIn
//import dagger.hilt.android.components.ApplicationComponent
import org.koin.dsl.module
import ru.be_more.orange_forum.data.local.DbContract
import ru.be_more.orange_forum.data.local.db.dao.DvachDao
import ru.be_more.orange_forum.data.remote.RemoteContract
import ru.be_more.orange_forum.data.remote.api.DvachApi
import ru.be_more.orange_forum.data.remote.repositories.BoardRepositoryImpl
import ru.be_more.orange_forum.data.remote.repositories.CategoryRepositoryImpl
import ru.be_more.orange_forum.data.remote.repositories.ResponseReposirotyImpl
import ru.be_more.orange_forum.data.remote.repositories.ThreadRepositoryImpl
import ru.be_more.orange_forum.ui.main.MainPresenter

/*
@Module
//@InstallIn(ApplicationComponent::class)
class RepositoryModule {
    @Provides
    fun provideCategory(dvachApi : DvachApi):
            RemoteContract.CategoryRepository = CategoryRepositoryImpl(dvachApi)

    @Provides
    fun provideBoard(dvachApi : DvachApi):
            RemoteContract.BoardRepository = BoardRepositoryImpl(dvachApi)

    @Provides
    fun provideThread(dvachApi : DvachApi):
            RemoteContract.ThreadRepository = ThreadRepositoryImpl(dvachApi)

    @Provides
    fun provideResponse(dvachApi : DvachApi):
            RemoteContract.ResponseRepository = ResponseReposirotyImpl(dvachApi)



    @Provides
    fun provideLocalBoard(dao: DvachDao): DbContract.BoardRepository =
        ru.be_more.orange_forum.data.local.repositories.BoardRepositoryImpl(dao)

    @Provides
    fun provideLocalThread(dao: DvachDao): DbContract.ThreadRepository =
        ru.be_more.orange_forum.data.local.repositories.ThreadRepositoryImpl(dao)

    @Provides
    fun provideLocalPost(dao: DvachDao): DbContract.PostRepository =
        ru.be_more.orange_forum.data.local.repositories.PostRepositoryImpl(dao)

    @Provides
    fun provideLocalFile(dao: DvachDao): DbContract.FileRepository =
        ru.be_more.orange_forum.data.local.repositories.FileRepositoryImpl(dao)

    @Provides
    fun provideLocalDownload(dao: DvachDao): DbContract.DownloadRepository =
        ru.be_more.orange_forum.data.local.repositories.DownloadRepositoryImpl(dao)

    @Provides
    fun provideLocalFavorite(dao: DvachDao): DbContract.FavoriteRepository =
        ru.be_more.orange_forum.data.local.repositories.FavoriteRepositoryImpl(dao)

}*/


@JvmField
val repositoryModule = module {
    single<RemoteContract.CategoryRepository> { CategoryRepositoryImpl(get()) }
    single<RemoteContract.BoardRepository> { BoardRepositoryImpl(get()) }
    single<RemoteContract.ThreadRepository> { ThreadRepositoryImpl(get()) }
    single<RemoteContract.ResponseRepository> { ResponseReposirotyImpl(get()) }

    single<DbContract.BoardRepository> {
        ru.be_more.orange_forum.data.local.repositories.BoardRepositoryImpl(get())
    }
    single<DbContract.ThreadRepository> {
        ru.be_more.orange_forum.data.local.repositories.ThreadRepositoryImpl(get())
    }
    single<DbContract.PostRepository> {
        ru.be_more.orange_forum.data.local.repositories.PostRepositoryImpl(get())
    }
    single<DbContract.FileRepository> {
        ru.be_more.orange_forum.data.local.repositories.FileRepositoryImpl(get())
    }
    single<DbContract.DownloadRepository> {
        ru.be_more.orange_forum.data.local.repositories.DownloadRepositoryImpl(get())
    }
    single<DbContract.FavoriteRepository> {
        ru.be_more.orange_forum.data.local.repositories.FavoriteRepositoryImpl(get())
    }

}