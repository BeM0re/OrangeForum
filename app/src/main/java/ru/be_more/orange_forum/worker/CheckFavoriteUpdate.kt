package ru.be_more.orange_forum.worker

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import io.reactivex.Observable
import io.reactivex.Single
import org.koin.java.KoinJavaComponent.inject
import ru.be_more.orange_forum.consts.CHANNEL_ID
import ru.be_more.orange_forum.domain.contracts.InteractorContract
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.annotation.RequiresApi

class CheckFavoriteUpdateWorker(
    private val appContext: Context,
    workerParams: WorkerParameters
): RxWorker(appContext, workerParams) {


    private val threadInteractor: InteractorContract.ThreadInteractor by inject(InteractorContract.ThreadInteractor::class.java)
    private val favoriteInteractor: InteractorContract.DownFavInteractor by inject(InteractorContract.DownFavInteractor::class.java)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun createWork(): Single<Result> {
        return favoriteInteractor
            .getFavoritesOnly()
            .map { boardList ->
                boardList.map { board ->
                    board.threads.map { thread ->
                        board.id to thread.num
                    }
                }.flatten()
            }
            .flatMapObservable{
                Observable.fromIterable(it)
            }
            .flatMapSingle { (board, thread) ->
                threadInteractor.getThread(board, thread, true)
                    .doOnSuccess { newThread ->
                        showPush(newThread.posts.size, newThread.num)
                    }
            }
            .ignoreElements()
            .toSingleDefault(Result.success())
            .onErrorReturn { Result.failure() }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showPush(newPosts: Int, notificationId: Int){
        val name: CharSequence = "2ch"
        val description: String = "New Messages"

        val importance = NotificationManager.IMPORTANCE_LOW

        val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
            .apply {
                this.description = description
                enableLights(true)
                enableVibration(true)
                vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            }

        NotificationManagerCompat
            .from(appContext)
            .createNotificationChannel(mChannel)

        val notification = NotificationCompat.Builder(appContext, CHANNEL_ID)
            .setSmallIcon(ru.be_more.orange_forum.R.drawable.ic_baseline_flash_on_24)
            .setContentTitle("2ch")
            .setContentText("$newPosts new messages")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        NotificationManagerCompat
            .from(appContext)
            .notify(notificationId, notification)
    }
}