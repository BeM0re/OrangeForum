package ru.be_more.orange_forum.di.modules

import android.content.Context
import org.koin.dsl.module
//import dagger.Module
//import dagger.Provides
//import dagger.hilt.InstallIn
//import dagger.hilt.android.components.ApplicationComponent
import ru.be_more.orange_forum.data.local.storage.StorageContract
import ru.be_more.orange_forum.data.local.storage.StorageContractImpl
import ru.be_more.orange_forum.data.remote.RemoteContract
import ru.be_more.orange_forum.data.remote.repositories.CategoryRepositoryImpl

/*
@Module
//@InstallIn(ApplicationComponent::class)
class StorageModule {
    @Provides
    fun provideCategory(context: Context): StorageContract.FileRepository =
        StorageContractImpl(context)

}*/


@JvmField
val storageModule = module {
    single<StorageContract.FileRepository> { StorageContractImpl(get()) }

}