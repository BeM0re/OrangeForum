package ru.be_more.orange_forum.di

import org.koin.dsl.module
import ru.be_more.orange_forum.domain.contracts.InteractorContract
import ru.be_more.orange_forum.domain.interactors.*

@JvmField
val interactorModule = module {
    single<InteractorContract.CategoryInteractor> { CategoryInteractorImpl(get(),get(),get()) }
    single<InteractorContract.BoardInteractor> { BoardInteractorImpl(get(),get(),get(),get()) }
    single<InteractorContract.ThreadInteractor> { ThreadInteractorImpl(get(),get(),get(),get()) }
    single<InteractorContract.PostInteractor> { PostInteractorImpl(get(),get()) }
    single<InteractorContract.ResponseInteractor> { ResponseInteractorImpl(get()) }
    single<InteractorContract.FavoriteInteractor> { FavoriteInteractorImpl(get(),get(),get()) }
    single<InteractorContract.QueueInteractor> { QueueInteractorImpl(get(),get(),get()) }
}
