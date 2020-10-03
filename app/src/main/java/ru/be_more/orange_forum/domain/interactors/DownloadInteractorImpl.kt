package ru.be_more.orange_forum.domain.interactors

import io.reactivex.Single
import ru.be_more.orange_forum.domain.contracts.DbContract
import ru.be_more.orange_forum.domain.contracts.InteractorContract
import ru.be_more.orange_forum.domain.model.Board
import ru.be_more.orange_forum.extentions.processSingle

class DownloadInteractorImpl(
    private val dbDownloadRepository: DbContract.DownloadRepository
): InteractorContract.DownloadInteractor, BaseInteractorImpl() {
    override fun getDownloads(): Single<List<Board>> =
        dbDownloadRepository.getDownloads()
            .processSingle()
}