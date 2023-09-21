package ru.be_more.orange_forum.data.local.db.utils

import ru.be_more.orange_forum.data.local.db.entities.StoredBoard
import ru.be_more.orange_forum.data.local.db.entities.StoredFile
import ru.be_more.orange_forum.data.local.db.entities.StoredPost
import ru.be_more.orange_forum.data.local.db.entities.StoredThread
import ru.be_more.orange_forum.domain.model.AttachFile
import ru.be_more.orange_forum.domain.model.Board
import ru.be_more.orange_forum.domain.model.BoardThread
import ru.be_more.orange_forum.domain.model.Post

class DbConverter {
    companion object{
        @Deprecated("Stop using it")
        fun toModelBoard(board: StoredBoard, threads: List<BoardThread>): Board = Board(
            name = board.name,
            id = board.id,
            category = board.categoryId,
            threads = threads,
            isFavorite = board.isFavorite
        )

        fun toModelBoard(board: StoredBoard): Board = Board(
            name = board.name,
            id = board.id,
            category = board.categoryId,
            threads = board.threads.map { toModelThread(it) },
            isFavorite = board.isFavorite
        )

        fun toStoredThread(thread: BoardThread, boardId: String): StoredThread = StoredThread(
            num = thread.num,
            title = thread.title,
            boardId = boardId,
            lastPostNumber = thread.lastPostNumber,
            newMessageAmount = thread.newMessageAmount,
            posts = thread.posts.map { toStoredPost(it) },
            isHidden = thread.isHidden,
            isDownloaded = thread.isDownloaded,
            isFavorite = thread.isFavorite,
            isQueued = thread.isQueued
        )

        fun toModelThread(thread: StoredThread): BoardThread =
            BoardThread(
                num = thread.num,
                title = thread.title,
                lastPostNumber = thread.lastPostNumber,
                newMessageAmount = thread.newMessageAmount,
                posts = thread.posts.map { toModelPost(it) },
                isHidden = thread.isHidden,
                isDownloaded = thread.isDownloaded,
                isFavorite = thread.isFavorite,
                isQueued = thread.isQueued
            )

        fun toStoredPost(post: Post): StoredPost = StoredPost(
            num = post.num,
            name = post.name,
            comment = post.comment,
            date = post.date,
            email = post.email,
            files_count = post.files_count,
            op = post.op,
            posts_count = post.posts_count,
            subject = post.subject,
            timestamp = post.timestamp,
            number = post.number,
            replies = post.replies,
            files = post.files.map { toStoredFile(it, it.localPath, it.localThumbnail) }
        )

        fun toStoredFile(
            file: AttachFile,
            localPath: String,
            localThumbnail: String
        ): StoredFile = StoredFile(
            displayName = file.displayName,
            height = file.height,
            width = file.width,
            tn_height = file.tn_height,
            tn_width = file.tn_width,
            webPath = file.path,
            localPath = localPath,
            webThumbnail = file.thumbnail,
            localThumbnail = localThumbnail,
            duration = file.duration
        )

        @Deprecated("")
        fun toModelPost(post: StoredPost, files: List<StoredFile>): Post = Post(
            num = post.num,
            name = post.name,
            comment = post.comment,
            date = post.date,
            email = post.email,
            files_count = post.files_count,
            op = post.op,
            posts_count = post.posts_count,
            subject = post.subject,
            timestamp = post.timestamp,
            number = post.number,
            replies = post.replies,
            files = files.map { toModelFile(it) }
        )

        fun toModelPost(post: StoredPost): Post = Post(
            num = post.num,
            name = post.name,
            comment = post.comment,
            date = post.date,
            email = post.email,
            files_count = post.files_count,
            op = post.op,
            posts_count = post.posts_count,
            subject = post.subject,
            timestamp = post.timestamp,
            number = post.number,
            replies = post.replies,
            files = post.files.map { toModelFile(it) }
        )
        
        fun toModelFile(file: StoredFile): AttachFile = AttachFile(
            path = file.webPath,
            thumbnail = file.webThumbnail,
            localPath = file.localPath,
            localThumbnail = file.localThumbnail,
            duration = file.duration
        )

    }
}