package ru.be_more.orange_forum.domain.interactors

import io.reactivex.Single
import io.reactivex.functions.Function4
import ru.be_more.orange_forum.data.local.DbContract
import ru.be_more.orange_forum.domain.InteractorContract
import ru.be_more.orange_forum.domain.model.AttachFile
import ru.be_more.orange_forum.domain.model.Board
import ru.be_more.orange_forum.domain.model.BoardThread
import ru.be_more.orange_forum.domain.model.Post
import ru.be_more.orange_forum.extentions.processSingle

//import javax.inject.Inject

class DownloadInteractorImpl /*@Inject constructor*/(
    private val dbBoardRepository: DbContract.BoardRepository,
    private val dbThreadRepository: DbContract.ThreadRepository,
    private val dbPostRepository: DbContract.PostRepository,
    private val dbFileRepository: DbContract.FileRepository
): InteractorContract.DownloadInteractor, BaseInteractorImpl() {
    override fun getDownloads(): Single<List<Board>> =
        Single.zip(
            dbBoardRepository.getBoards(),
            dbThreadRepository.getDownloadedThreads(),
            dbPostRepository.getOpPosts(),
            dbFileRepository.getOpFiles(),
            Function4 <List<Board>,
                    List<Pair<BoardThread, String>>,
                    List<Pair<Post, Int>>,
                    List<Pair<AttachFile, Int>>,
                    List<Board>> { boards, threads, posts, files ->
                insertThreadsIntoBoards(
                    boards,
                    insertPostsIntoThreads(
                        threads,
                        insertFilesIntoPosts(posts, files)
                    )
                )
            }
        )
            .processSingle()

    //TODO вынести ссылку на родителя в модель? Чтобы избавиться от пар
    //Pair<AttachFile, Int> - Int это номер поста
    private fun insertFilesIntoPosts(postsPairs: List<Pair<Post, Int>>,
                                     filePairs: List<Pair<AttachFile, Int>>): List<Pair<Post, Int>> =
        postsPairs.map { postPair -> //Для каждой пары Пост-НомерТреда
            postPair.copy(first =  //Меняем пост на
                postPair.first.copy(files = filePairs //Такой же пост, но с файлами, у которых
                    .filter { it.second == postPair.first.num } //2е значение пары (номер поста) совпаадет с номером поста
                    .map { it.first })   //из пары файл-НомерПоста берем только файл
            )
        }

    //Pair<AttachFile, Int> - Int это номер треда
    private fun insertPostsIntoThreads(threadPairs: List<Pair<BoardThread, String>>,
                                       postPairs: List<Pair<Post, Int>>): List<Pair<BoardThread, String>> =
        threadPairs.map { threadPair ->
            threadPair.copy( first =
                threadPair.first.copy(posts = postPairs
                    .filter { it.second == threadPair.first.num }
                    .map { it.first })
            )
        }

    //Pair<AttachFile, String> - String это id борды
    private fun insertThreadsIntoBoards(boards: List<Board>,
                                        threads: List<Pair<BoardThread, String>>): List<Board> =
        boards.map { board ->
            board.copy(threads = threads
                .filter { it.second == board.id }
                .map { it.first })
        }



}