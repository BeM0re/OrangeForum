package ru.be_more.orange_forum.repositories

import android.util.Log
import androidx.lifecycle.MutableLiveData
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import ru.be_more.orange_forum.data.*
import ru.be_more.orange_forum.model.*
import ru.be_more.orange_forum.services.ApiFactory
import ru.be_more.orange_forum.utils.ParseHtml
import java.io.File
import java.util.*
import javax.inject.Inject


const val cookie = "usercode_auth=54e8a3b3c8d5c3d6cffb841e9bf7da63; _ga=GA1.2.57010468.1498700728; ageallow=1; _gid=GA1.2.1910512907.1585793763; _gat=1"
const val SECRET = "6Ler0ukUAAAAAFZD0uzKYrkK4ne8jVJn6B52x43z"
const val OPEN_KEY = "6Ler0ukUAAAAAA0GXsEhYa-rgoA6HojFJmn2aTTC"

class DvachApiRepository @Inject constructor(){

    private val dvachApi = ApiFactory.dvachApi
    private val googleCaptchaApi = ApiFactory.googleCaptcha
    private var isLoading : Observable<Boolean> = Observable.just(false)


    fun getCaptchaTypes(): Observable<CaptchaType>? =
        dvachApi.getDvachCaptchaTypesRx("b", cookie) //TODO убрать захардкоженное
            .flatMap { Observable.fromIterable(it.types) }

    fun getCaptchaId(captchaType: String): Observable<GetCaptchaResponse>  =
        dvachApi.getDvachCaptchaIdRx(captchaType, cookie) //TODO убрать захардкоженное

    fun getMobileCaptcha(): Observable<ResponseBody>  =
        dvachApi.getMobileCaptchaRx()

    fun getCategories(): Observable<List<Category>>  =
        dvachApi.getDvachCategoriesRx("get_boards")
            .subscribeOn(Schedulers.io())
            .doOnError { throwable -> Log.d("M_DvachApiRepository", "Getting category error = $throwable") }
            .map { entity -> toCategories(entity) }

    fun getThreads(boardId: String): Observable<List<BoardThread>> =
        dvachApi.getDvachThreadsRx(boardId)
            .subscribeOn(Schedulers.io())
            .doOnError { throwable -> Log.d("M_DvachApiRepository", "Getting thread error = $throwable") }
            .map { entity -> toBoard(entity) }

    fun getThread(boardId: String, threadNum: Int): Observable<BoardThread> =
        dvachApi.getDvachPostsRx(boardId, threadNum, cookie)
            .subscribeOn(Schedulers.io())
            .doOnError { throwable -> Log.d("M_DvachApiRepository", "get thread via api error = $throwable") }
            .onErrorReturn { DvachThread() }
            .map { entity -> toThread(entity, threadNum)}
            .map { entity -> findResponses(entity)}



    fun getPost(boardId: String, postNum: Int): Observable<Post> =
        dvachApi.getDvachPostRx("get_post", boardId, postNum, cookie)
            .subscribeOn(Schedulers.io())
            .doOnError { throwable -> Log.d("M_DvachApiRepository", "Getting post error = $throwable") }
            .map { entity -> toPost(entity[0]) }

    fun postResponse (
        boardId: String,
        threadNum: Int,
        comment: String,
        captcha_type: String,
        g_recaptcha_response: String,
        chaptcha_id : String,
        files : List<File>): Observable<DvachPostResponse>  {

        val requestTask =
            RequestBody.create(MediaType.parse("multipart/form-data"), "post")
        val requestCookie =
            RequestBody.create(MediaType.parse("multipart/form-data"), cookie)
        val requestBoardId =
            RequestBody.create(MediaType.parse("multipart/form-data"), boardId)
        val requestThreadNum =
            RequestBody.create(MediaType.parse("multipart/form-data"), ""+threadNum)
        val requestComment =
            RequestBody.create(MediaType.parse("multipart/form-data"), comment)
        val requestCaptchaType =
            RequestBody.create(MediaType.parse("multipart/form-data"), captcha_type)
        val requestGRecaptchaResponse =
            RequestBody.create(MediaType.parse("multipart/form-data"), g_recaptcha_response)
        val requestChaptchaId =
            RequestBody.create(MediaType.parse("multipart/form-data"), chaptcha_id)

        val requestFiles: LinkedList<MultipartBody.Part> = LinkedList()

        val gCaptchaResponse: MutableLiveData<String> = MutableLiveData()

        files.forEach {file ->
            val requestFile: RequestBody =
                RequestBody.create(MediaType.parse("multipart/form-data"), file)

            requestFiles.add(
                MultipartBody.Part.createFormData("image", file.name, requestFile)
            )
        }




        //TODO вернуть после нормального API
       /* SafetyNet.getClient(App.applicationContext()).verifyWithRecaptcha(
            OPEN_KEY
//        chaptcha_id
        )
            .addOnSuccessListener { response ->



                if (response.tokenResult?.isNotEmpty() == true) {

                    Log.d("M_DvachApiRepository", response.tokenResult)
                    val token =
                        RequestBody.create(MediaType.parse("multipart/form-data"), response.tokenResult)

                    dvachApi.postThreadResponseRx(
                        cookie = requestCookie,
                        task = requestTask ,
                        board = requestBoardId,
                        thread = requestThreadNum,
                        op_mark = null,
                        usercode = null,
                        captcha_type = requestCaptchaType,
                        email = null,
                        subject = null,
                        comment = requestComment,
                        g_recaptcha_response = token,
                        chaptcha_id = requestChaptchaId,
                        files = requestFiles
                    ).subscribeOn(Schedulers.io())
                        .subscribe { Log.d("M_DvachApiRepository", "! response = $it") }

                    disposable = googleCaptchaApi.getGCaptchaResponse(SECRET, response.tokenResult+"1")
                        .subscribeOn(Schedulers.io())
                        .subscribe {
//                            Log.d("M_DvachApiRepository", "response = $it")
//                            requestGRecaptchaResponse -> Observable.just(

                         }
                }
            }
            .addOnFailureListener { e ->
                if (e is ApiException) {
                    Log.d("M_DvachApiRepository",
                        "Google captcha error: ${CommonStatusCodes.getStatusCodeString(e.statusCode)}")
                } else {
                    Log.d("M_DvachApiRepository", "Google captcha error: ${e.message}")
                }
            }*/


        return dvachApi.postThreadResponseRx(
            cookie = requestCookie,
            task = requestTask ,
            board = requestBoardId,
            thread = requestThreadNum,
            op_mark = null,
            usercode = null,
            captcha_type = requestCaptchaType,
            email = null,
            subject = null,
            comment = requestComment,
            g_recaptcha_response = requestGRecaptchaResponse,
            chaptcha_id = requestChaptchaId,
            files = requestFiles
        )
    }

    private fun toCategories (allCategories: Map<String, List<DvachBoardName>>) : List<Category> {
        Log.d("M_DvachApiRepository","$allCategories")




        val adult = Category(
            title = "Взрослым",
            items = getBoardNames(allCategories.adult)
        )
        val games = Category(
            title = "Игры",
            items = getBoardNames(allCategories.games)
        )
        val politics = Category(
            title = "Политика",
            items = getBoardNames(allCategories.politics)
        )
        val custom = Category(
            title = "Пользовательские",
            items = getBoardNames(allCategories.custom)
        )
        val other = Category(
            title = "Разное",
            items = getBoardNames(allCategories.other)
        )
        val art = Category(
            title = "Творчество",
            items = getBoardNames(allCategories.art)
        )
        val thematics = Category(
            title = "Тематика",
            items = getBoardNames(allCategories.thematics)
        )
        val tech = Category(
            title = "Техника и софт",
            items = getBoardNames(allCategories.tech)
        )
        val japan = Category(
            title = "Японская культура",
            items = getBoardNames(allCategories.japan)
        )

        return listOf(adult, games, politics, custom, other, art, thematics, tech, japan)
    }

    private fun getBoardNames(dvachBoards : List<DvachBoardName>) =
        dvachBoards.map { toBoard(it) }

    private fun toBoard(dvachBoard: DvachBoardName) = Board(
        name = dvachBoard.name,
        id = dvachBoard.id
    )

    private fun toBoard(dvachBoard: DvachBoard)
            = dvachBoard.threads.map { toThread(it) }

    private fun toThread(dvachOpPost: DvachPost) = BoardThread(
        num = dvachOpPost.num,
        posts = listOf(toPost(dvachOpPost)),
        title = dvachOpPost.subject
    )
    private fun toThread(dvachThread: DvachThread, threadNum: Int) = BoardThread(
        num = threadNum,
        posts = dvachThread.threads[0].posts.map { toPost(it) },
        title = dvachThread.title
    )

    private fun toPost(post: DvachPost) = Post(
        num = post.num,
        name = post.name,
        comment = post.comment,
        date = post.date,
        email = post.email,
        files = post.files.map { toFiles(it) },
        files_count = post.files_count,
        op = post.op,
        posts_count = post.posts_count,
        subject = post.subject,
        timestamp = post.timestamp,
        number = post.number
    )

    private fun toFiles (file: DvachFile) = AttachFile(
        path = file.path,
        thumbnail = file.thumbnail,
        duration = if(file.duration.isNullOrEmpty()) "" else file.duration
    )

    private fun findResponses(board: BoardThread): BoardThread {

        board.posts.forEach { post ->
            //replies - на какие посты ответы
            val replies = ParseHtml.findReply(post.comment)

            //пост с номером post.num отвечает на пост с номером reply
            //reply сохраняет, что на него ссылается post.num
            replies.forEach { reply ->
                board.posts.find { it.num == reply }
                    ?.replies?.add(post.num)
            }
        }

        return board
    }

}