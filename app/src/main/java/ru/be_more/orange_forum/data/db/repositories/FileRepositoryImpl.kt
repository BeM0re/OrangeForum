package ru.be_more.orange_forum.data.db.repositories

import io.reactivex.Completable
import io.reactivex.Single
import ru.be_more.orange_forum.data.db.DbContract
import ru.be_more.orange_forum.data.db.db.dao.DvachDao
import ru.be_more.orange_forum.data.db.db.utils.DbConverter.Companion.toModelFile
import ru.be_more.orange_forum.data.db.db.utils.DbConverter.Companion.toStoredFile
import ru.be_more.orange_forum.domain.model.AttachFile
import ru.be_more.orange_forum.extentions.processSingle
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FileRepositoryImpl @Inject constructor(
    private val dao: DvachDao
) : DbContract.FileRepository{

    override fun saveFile(file: AttachFile, postNum: Int, threadNum: Int, boardId: String): Completable =
        Completable.fromCallable {
            dao.insertFile(toStoredFile(file, postNum, boardId, threadNum))
        }

/*    override fun getFile(boardId: String, postNum: Int): Single<AttachFile> =
        dao.getPostFiles(postNum, boardId)*/

    override fun getPostFiles(boardId: String, postNum: Int): Single<List<AttachFile>> =
        dao.getPostFiles(postNum, boardId)
            .map { files ->
                files.map { file ->
                    toModelFile(file)
                }
            }
            .processSingle()

    override fun getThreadFiles(boardId: String, threadNum: Int): Single<List<AttachFile>> =
        dao.getThreadFiles(boardId, threadNum)
            .map { files ->
                files.map { file ->
                    toModelFile(file)
                }
            }
            .processSingle()

    override fun deleteThreadFiles(boardId: String, threadNum: Int): Completable =
        Completable.fromCallable {
            dao.deleteThreadFiles(boardId, threadNum)
        }

    override fun deletePostFiles(boardId: String, postNum: Int): Completable =
        Completable.fromCallable {
            dao.deletePostFiles(boardId, postNum)
        }


}