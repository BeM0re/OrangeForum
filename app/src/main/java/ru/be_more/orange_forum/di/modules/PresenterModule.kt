package ru.be_more.orange_forum.di.modules

//import dagger.Binds
//import dagger.Module
//import dagger.Provides
//import dagger.hilt.InstallIn
//import dagger.hilt.android.components.ApplicationComponent
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import org.koin.dsl.module
import ru.be_more.orange_forum.domain.InteractorContract
import ru.be_more.orange_forum.ui.board.BoardPresenter
import ru.be_more.orange_forum.ui.category.CategoryPresenter
import ru.be_more.orange_forum.ui.download.DownloadPresenter
import ru.be_more.orange_forum.ui.favorire.FavoritePresenter
import ru.be_more.orange_forum.ui.main.MainActivity
import ru.be_more.orange_forum.ui.main.MainPresenter
import ru.be_more.orange_forum.ui.main.MainView
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
    single { params -> MainPresenter(get(), get(), get(), params[0]) }
    single { params -> CategoryPresenter(get(), params[0]) }
    single { params -> BoardPresenter(get(), get(), get(), params[0]) }
    single { params -> ThreadPresenter(get(), get(), params[0]) }
    single { params -> PostPresenter(params[0]) }
    single { params -> ResponsePresenter(get(), params[0]) }
    single { params -> FavoritePresenter(get(), get(), params[0]) }
    single { params -> DownloadPresenter(get(), get(), params[0]) }
 }
