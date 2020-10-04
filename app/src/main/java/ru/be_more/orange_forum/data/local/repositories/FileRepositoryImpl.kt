package ru.be_more.orange_forum.data.local.repositories

import io.reactivex.Single
import ru.be_more.orange_forum.domain.contracts.DbContract
import ru.be_more.orange_forum.data.local.db.dao.DvachDao
import ru.be_more.orange_forum.data.local.db.utils.DbConverter.Companion.toModelFile
import ru.be_more.orange_forum.data.local.db.utils.DbConverter.Companion.toStoredFile
import ru.be_more.orange_forum.domain.contracts.StorageContract
import ru.be_more.orange_forum.domain.model.AttachFile

class FileRepositoryImpl (
    private val dao: DvachDao,
    private val storage: StorageContract.LocalStorage
) : DbContract.FileRepository{

    override fun saveFile(file: AttachFile, postNum: Int, threadNum: Int, boardId: String){
        dao.insertFile(toStoredFile(
            file,
            postNum,
            boardId,
            threadNum,
            storage.saveFile(file.path).toString(),
            storage.saveFile(file.thumbnail).toString()
        ))
    }

    override fun saveFiles(files: List<AttachFile>, postNum: Int, threadNum: Int, boardId: String) {
        files.forEach { file -> saveFile(file, postNum, threadNum, boardId) }
    }

    override fun getPostFiles(boardId: String, postNum: Int): Single<List<AttachFile>> =
        dao.getPostFiles(postNum, boardId)
            .map { files ->
                files.map { file -> toModelFile(file) }
            }

}