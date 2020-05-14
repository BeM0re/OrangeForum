package ru.be_more.orange_forum.interactors

import android.util.Log
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function3
import ru.be_more.orange_forum.App
import ru.be_more.orange_forum.model.BoardThread
import ru.be_more.orange_forum.repositories.DvachApiRepository
import ru.be_more.orange_forum.repositories.DvachDbRepository
import javax.inject.Inject

class ThreadInteractor @Inject constructor() {

    @Inject
    lateinit var apiRepo : DvachApiRepository

    @Inject
    lateinit var dbRepo : DvachDbRepository

    init {
        App.getComponent().inject(this)
    }

    fun getThread(boardId: String, threadNum: Int): Observable<BoardThread> =
        Observable.zip(
            dbRepo.getThreadOrEmpty(boardId, threadNum),
            apiRepo.getThread(boardId, threadNum),
            BiFunction { localThread, webThread ->
                return@BiFunction if (localThread.num == 0) // в базе вообще нет данных о треде
                    webThread
                else{
                    if (localThread.posts.isNotEmpty()) // тред полностью скачан
                        localThread
                    else{ // о треде есть заметки (избранное, скрытое)
                        webThread.copy(
                            isHidden = localThread.isHidden,
                            isFavorite = localThread.isFavorite
                        )
                    }
                }
            }
        )
}