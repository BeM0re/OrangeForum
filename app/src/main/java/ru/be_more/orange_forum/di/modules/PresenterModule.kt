package ru.be_more.orange_forum.di.modules

//import dagger.Binds
//import dagger.Module
//import dagger.Provides
//import dagger.hilt.InstallIn
//import dagger.hilt.android.components.ApplicationComponent
import org.koin.core.qualifier.named
import org.koin.dsl.module
import ru.be_more.orange_forum.domain.InteractorContract
import ru.be_more.orange_forum.ui.board.BoardPresenter
import ru.be_more.orange_forum.ui.category.CategoryPresenter
import ru.be_more.orange_forum.ui.download.DownloadPresenter
import ru.be_more.orange_forum.ui.favorire.FavoritePresenter
import ru.be_more.orange_forum.ui.main.MainPresenter
import ru.be_more.orange_forum.ui.post.PostPresenter
import ru.be_more.orange_forum.ui.response.ResponsePresenter
import ru.be_more.orange_forum.ui.thread.ThreadPresenter

//import javax.inject.Provider


/*
@Module
//@InstallIn(ApplicationComponent::class)
abstract class PresenterModule {
    @Binds
    abstract fun provideMain(): Provider<MainPresenter>

}*/

@JvmField
val presenterModule = module {
    single { MainPresenter(get(), get(), get()) }
    single { CategoryPresenter(get()) }
    single { BoardPresenter(get(), get(), get()) }
    single { ThreadPresenter(get(), get()) }
    single { PostPresenter() }
    single { ResponsePresenter(get()) }
    single { FavoritePresenter(get(), get()) }
    single { DownloadPresenter(get(), get()) }
 }
