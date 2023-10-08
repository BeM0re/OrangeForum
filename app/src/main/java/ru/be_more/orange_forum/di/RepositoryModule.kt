package ru.be_more.orange_forum.di

import org.koin.dsl.module
import ru.be_more.orange_forum.domain.contracts.DbContract
import ru.be_more.orange_forum.domain.contracts.RemoteContract
import ru.be_more.orange_forum.data.remote.repositories.ApiRepositoryImpl

@JvmField
val repositoryModule = module {
    single<RemoteContract.ApiRepository> { ApiRepositoryImpl(get()) }

    single<DbContract.CategoryRepository> {
        ru.be_more.orange_forum.data.local.repositories.CategoryRepositoryImpl(get(), get())
    }
    single<DbContract.BoardRepository> {
        ru.be_more.orange_forum.data.local.repositories.BoardRepositoryImpl(get())
    }
    single<DbContract.ThreadRepository> {
        ru.be_more.orange_forum.data.local.repositories.ThreadRepositoryImpl(get(), get())
    }
    single<DbContract.PostRepository> {
        ru.be_more.orange_forum.data.local.repositories.PostRepositoryImpl(get(), get())
    }
}