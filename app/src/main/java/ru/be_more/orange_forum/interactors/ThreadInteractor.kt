package ru.be_more.orange_forum.interactors

import android.util.Log
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import ru.be_more.orange_forum.App
import ru.be_more.orange_forum.model.Board
import ru.be_more.orange_forum.model.BoardThread
import ru.be_more.orange_forum.model.Post
import ru.be_more.orange_forum.repositories.DvachDbRepository
import java.util.*
import javax.inject.Inject

//TODO разбить на несколько интеракторов
class ThreadInteractor @Inject constructor() {

    @Inject
    lateinit var dbRepo : DvachDbRepository

    private val disposables = LinkedList<Disposable>() //TODO сделать отдельные диспы, а не массив

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
                    if (localThread.isDownloaded) // тред полностью скачан
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

    fun getBoard(boardId: String): Observable<Board> =
        Observable.zip(
            dbRepo.getBoard(boardId),
            apiRepo.getThreads(boardId),
            BiFunction { localBoard, webThreads ->

                localBoard.threads.forEach { localThread ->
                    val webIndex = webThreads.indexOfFirst { it.num == localThread.num }

                    if (webIndex == -1){ //удаляем инфу об утонувших несохраненных тредах //TODO не работает
                        if (!localThread.isDownloaded)
                            disposables.add( dbRepo.deleteThread(boardId, localThread.num) )
                    }
                    else{
                        webThreads[webIndex].isFavorite = localThread.isFavorite
                        webThreads[webIndex].isHidden = localThread.isHidden
                        webThreads[webIndex].isDownloaded = localThread.isDownloaded
                    }
                }
                return@BiFunction localBoard.copy(threads = webThreads)
            }
        )

    fun markThreadFavorite(boardId: String, threadNum: Int, boardTitle: String){
        disposables.add(
            apiRepo.getPost(boardId, threadNum)
                .observeOn(Schedulers.io())
                .subscribe(
                    { post ->
                        val thread = BoardThread(
                            num = threadNum,
                            posts = listOf(post),
                            title = post.subject
                        )
                        dbRepo.saveThread(thread,
                            boardId,
                            boardTitle,
                            DvachDbRepository.Purpose.FAVORITE)
                    },
                    {
                        App.showToast("Ошибка")
                        Log.d("M_ThreadInteractor", "Interactor add to fav error = $it")
                    }
                )
        )
    }

    fun unmarkThreadFavorite(boardId: String, threadNum: Int) =
        Observable.fromCallable {
            dbRepo.unmarkThreadFavorite(boardId, threadNum)
        }

    fun markBoardFavorite(boardId: String, boardTitle: String){
        dbRepo.markBoardFavorite(boardId, boardTitle)
    }

    fun unmarkBoardFavorite(boardId: String) =
        Observable.fromCallable {
            dbRepo.unmarkBoardFavorite(boardId)
        }

    fun markThreadHidden(boardId: String, threadNum: Int){
        dbRepo.markThreadHidden(boardId, threadNum)
    }

    fun unmarkThreadHidden(boardId: String, threadNum: Int): Observable<Unit> =
        Observable.fromCallable {
            dbRepo.unmarkThreadHidden(boardId, threadNum)
        }

    fun deleteThread(boardId: String, threadNum: Int) =
        dbRepo.deleteThread(boardId, threadNum)



    fun getFavorites(): Observable<List<Board>> =
        dbRepo.getFavorites()


    fun getPost(boardId: String, postNum: Int): Observable<Post> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun destroy(){
        dbRepo.destroy()
        disposables.forEach { it.dispose() }
    }
}