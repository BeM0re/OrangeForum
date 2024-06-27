package ru.be_more.orange_forum.di

import dagger.Binds
import dagger.Module
import ru.be_more.orange_forum.data.local.storage.LocalStorageImpl
import ru.be_more.orange_forum.domain.contracts.StorageContract

//todo move
@Module
interface StorageModule {
    @Binds
    fun bindLocalStorage(localStorage: LocalStorageImpl): StorageContract.LocalStorage
}