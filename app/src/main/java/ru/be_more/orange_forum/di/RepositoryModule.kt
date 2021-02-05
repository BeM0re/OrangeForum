package ru.be_more.orange_forum.di

import org.koin.dsl.module
import ru.be_more.orange_forum.domain.contracts.DbContract
import ru.be_more.orange_forum.domain.contracts.RemoteContract
import ru.be_more.orange_forum.data.remote.repositories.BoardRepositoryImpl
import ru.be_more.orange_forum.data.remote.repositories.CategoryRepositoryImpl
import ru.be_more.orange_forum.data.remote.repositories.ResponseRepositoryImpl
import ru.be_more.orange_forum.data.remote.repositories.ThreadRepositoryImpl

@JvmField
val repositoryModule = module {
    single<RemoteContract.CategoryRepository> { CategoryRepositoryImpl(get()) }
    single<RemoteContract.BoardRepository> { BoardRepositoryImpl(get()) }
    single<RemoteContract.ThreadRepository> { ThreadRepositoryImpl(get()) }
    single<RemoteContract.ResponseRepository> { ResponseRepositoryImpl(get()) }

    single<DbContract.BoardRepository> {
        ru.be_more.orange_forum.data.local.repositories.BoardRepositoryImpl(get())
    }
    single<DbContract.ThreadRepository> {
        ru.be_more.orange_forum.data.local.repositories.ThreadRepositoryImpl(get(), get())
    }
    single<DbContract.PostRepository> {
        ru.be_more.orange_forum.data.local.repositories.PostRepositoryImpl(get())
    }
    single<DbContract.FileRepository> {
        ru.be_more.orange_forum.data.local.repositories.FileRepositoryImpl(get(), get())
    }
    single<DbContract.DownFavRepository> {
        ru.be_more.orange_forum.data.local.repositories.DownFavRepositoryImpl(get())
    }
    single<DbContract.QueueRepository> {
        ru.be_more.orange_forum.data.local.repositories.QueueRepositoryImpl(get())
    }

}