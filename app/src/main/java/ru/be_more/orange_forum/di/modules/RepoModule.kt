package ru.be_more.orange_forum.di.modules

import dagger.Module
import dagger.Provides

@Module
class RepoModule {

    @Provides
    fun provideRepo() = RepoModule()
}