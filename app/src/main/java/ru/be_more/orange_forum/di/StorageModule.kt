package ru.be_more.orange_forum.di

import org.koin.dsl.module
import ru.be_more.orange_forum.domain.contracts.StorageContract
import ru.be_more.orange_forum.data.local.storage.LocalStorageImpl

@JvmField
val storageModule = module {
    single<StorageContract.LocalStorage> { LocalStorageImpl(get()) }
}