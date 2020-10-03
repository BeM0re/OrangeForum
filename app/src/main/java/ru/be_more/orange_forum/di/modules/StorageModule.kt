package ru.be_more.orange_forum.di.modules

import org.koin.dsl.module
import ru.be_more.orange_forum.domain.contracts.StorageContract
import ru.be_more.orange_forum.data.local.storage.StorageContractImpl

@JvmField
val storageModule = module {
    single<StorageContract.FileRepository> { StorageContractImpl(get()) }
}