package ru.be_more.orange_forum.interactors

import io.reactivex.Observable
import io.reactivex.functions.BiFunction
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

    fun getBoard(boardId: String): Observable<List<BoardThread>> =
        Observable.zip(
            dbRepo.getThreadsOnBoard(boardId),
            apiRepo.getBoard(boardId),
            BiFunction { localThreads, webThreads ->
                localThreads.forEach { localThread ->
                    val webIndex = webThreads.indexOfFirst { it.num == localThread.num }

                    if (webIndex == -1){ //удаляем инфу об утонувших несохраненных тредах
                        if (!localThread.isDownloaded)
                            dbRepo.deleteThread(boardId, localThread.num)
                    }
                    else{
                        webThreads[webIndex].isFavorite = localThread.isFavorite
                        webThreads[webIndex].isHidden = localThread.isHidden
                        webThreads[webIndex].isDownloaded = localThread.isDownloaded
                    }
                }
                webThreads
            }
        )

    fun markThreadFavorite(boardId: String, threadNum: Int){
        dbRepo.markThreadFavorite(boardId, threadNum)
    }

    fun unmarkThreadFavorite(boardId: String, threadNum: Int){
        dbRepo.unmarkThreadFavorite(boardId, threadNum)
    }

    fun markThreadHidden(boardId: String, threadNum: Int){
        dbRepo.markThreadHidden(boardId, threadNum)
    }

    fun unmarkThreadHidden(boardId: String, threadNum: Int): Observable<Unit> =
        Observable.fromCallable {
            dbRepo.unmarkThreadHidden(boardId, threadNum)
        }

    fun deleteThread(boardId: String, threadNum: Int){

    }

    fun destroy(){
        dbRepo.destroy()
    }
}