package ru.be_more.orange_forum.di

import dagger.Binds
import dagger.Module
import ru.be_more.orange_forum.data.local.repositories.BoardRepositoryImpl
import ru.be_more.orange_forum.data.local.repositories.CategoryRepositoryImpl
import ru.be_more.orange_forum.data.local.repositories.PostRepositoryImpl
import ru.be_more.orange_forum.data.local.repositories.ThreadRepositoryImpl
import ru.be_more.orange_forum.domain.contracts.DbContract

@Module
interface RepoModule {

    @Binds
    fun bindCategoryRepository(categoryRepositoryImpl: CategoryRepositoryImpl): DbContract.CategoryRepository

    @Binds
    fun bindBoardRepository(boardRepositoryImpl: BoardRepositoryImpl): DbContract.BoardRepository

    @Binds
    fun bindThreadRepository(threadRepositoryImpl: ThreadRepositoryImpl): DbContract.ThreadRepository

    @Binds
    fun bindPostRepository(postRepositoryImpl: PostRepositoryImpl): DbContract.PostRepository
}