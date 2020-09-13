package ru.be_more.orange_forum.di.modules

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.be_more.orange_forum.data.local.storage.StorageContract
import ru.be_more.orange_forum.data.local.storage.StorageContractImpl

@Module
class StorageModule {
    @Provides
    fun provideCategory(context: Context): StorageContract.FileRepository =
        StorageContractImpl(context)

}