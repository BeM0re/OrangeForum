package ru.be_more.orange_forum.worker

import android.Manifest
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
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import io.reactivex.Completable

class CheckFavoriteUpdateWorker(
    private val appContext: Context,
    workerParams: WorkerParameters
): RxWorker(appContext, workerParams) {


    private val threadInteractor: InteractorContract.ThreadInteractor by inject(InteractorContract.ThreadInteractor::class.java)
    private val favoriteInteractor: InteractorContract.FavoriteInteractor by inject(InteractorContract.FavoriteInteractor::class.java)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun createWork(): Single<Result> {
        return Single.just(Result.success())
/*        return favoriteInteractor.getFavoritesOnly()
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
            .flatMapCompletable { (board, thread) ->
//                threadInteractor.updateNewMessages(board, thread)
                Completable.complete()
            }
            .andThen(favoriteInteractor.getFavoritesOnly())
            .doOnSuccess { boardList ->
                boardList.map { board ->
                    board.threads.forEach { thread ->
                        if (thread.newMessageAmount > 0)
                            showPush(thread.num, thread.newMessageAmount, thread.title.substring(0, 20))
                    }
                }
            }
            .map { Result.success() }
            .onErrorReturn { Result.failure() }*/
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showPush(notificationId: Int, newPosts: Int, threadTitle: String){
/*        val name: CharSequence = "2ch New Messages"
        val importance = NotificationManager.IMPORTANCE_LOW

        val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
            .apply {
                this.description = threadTitle
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

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        NotificationManagerCompat
            .from(appContext)
            .notify(notificationId, notification)*/
    }
}