package ru.be_more.orange_forum.di.modules

import org.koin.dsl.module
import ru.be_more.orange_forum.domain.contracts.DbContract
import ru.be_more.orange_forum.domain.contracts.RemoteContract
import ru.be_more.orange_forum.data.remote.repositories.BoardRepositoryImpl
import ru.be_more.orange_forum.data.remote.repositories.CategoryRepositoryImpl
import ru.be_more.orange_forum.data.remote.repositories.ResponseReposirotyImpl
import ru.be_more.orange_forum.data.remote.repositories.ThreadRepositoryImpl

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