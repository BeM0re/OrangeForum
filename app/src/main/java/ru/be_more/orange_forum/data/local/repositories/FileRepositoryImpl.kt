package ru.be_more.orange_forum.data.local.repositories

import io.reactivex.Single
import ru.be_more.orange_forum.domain.contracts.DbContract
import ru.be_more.orange_forum.data.local.db.dao.DvachDao
import ru.be_more.orange_forum.data.local.db.utils.DbConverter.Companion.toModelFile
import ru.be_more.orange_forum.data.local.db.utils.DbConverter.Companion.toStoredFile
import ru.be_more.orange_forum.domain.model.AttachFile

class FileRepositoryImpl (
    private val dao: DvachDao
) : DbContract.FileRepository{

    override fun saveFile(file: AttachFile, postNum: Int, threadNum: Int, boardId: String){
            dao.insertFile(toStoredFile(file, postNum, boardId, threadNum))
        }

    override fun saveFiles(files: List<AttachFile>, postNum: Int, threadNum: Int, boardId: String) {
        files.forEach { file ->
            dao.insertFile(toStoredFile(file, postNum, boardId, threadNum))
        }
    }

    override fun getPostFiles(boardId: String, postNum: Int): Single<List<AttachFile>> =
        dao.getPostFiles(postNum, boardId)
            .map { files ->
                files.map { file ->
                    toModelFile(file)
                }
            }

    override fun getThreadFiles(boardId: String, threadNum: Int): Single<List<Pair<AttachFile, Int>>> =
        dao.getThreadFiles(boardId, threadNum)
            .map { files ->
                files.map { file ->
                    Pair(toModelFile(file), file.postNum)
                }
            }

    override fun deleteThreadFiles(boardId: String, threadNum: Int){
            dao.deleteThreadFiles(boardId, threadNum)
        }

    override fun deletePostFiles(boardId: String, postNum: Int){
            dao.deletePostFiles(boardId, postNum)
        }

    override fun getOpFiles(): Single<List<Pair<AttachFile, Int>>> =
        dao.getOpFiles()
            .map { files ->
                files.map { file ->
                    Pair(toModelFile(file), file.postNum)
                }
            }
}