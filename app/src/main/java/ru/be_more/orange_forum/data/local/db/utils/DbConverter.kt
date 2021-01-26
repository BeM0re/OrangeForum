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
        fun toModelBoard(board: StoredBoard, threads: List<BoardThread>): Board = Board(
            name = board.name,
            id = board.id,
            threads = threads,
            isFavorite = board.isFavorite
        )

        fun toStoredThread(thread: BoardThread, boardId: String): StoredThread = StoredThread(
            num = thread.num,
            title = thread.title,
            boardId = boardId,
            isHidden = thread.isHidden,
            isDownloaded = thread.isDownloaded,
            isFavorite = thread.isFavorite,
            isQueued = thread.isQueued
        )

        fun downloadedToStoredThread(thread: BoardThread, boardId: String): StoredThread = StoredThread(
            num = thread.num,
            title = thread.title,
            boardId = boardId,
            isHidden = thread.isHidden,
            isDownloaded = true,
            isFavorite = thread.isFavorite
        )

        fun favoriteToStoredThread(thread: BoardThread, boardId: String): StoredThread = StoredThread(
            num = thread.num,
            title = thread.title,
            boardId = boardId,
            isHidden = thread.isHidden,
            isDownloaded = thread.isDownloaded,
            isFavorite = true
        )

        fun toModelThread(thread: StoredThread, posts: List<Post>): BoardThread = BoardThread(
            num = thread.num,
            title = thread.title,
            isHidden = thread.isHidden,
            isDownloaded = thread.isDownloaded,
            isFavorite = thread.isFavorite,
            isQueued = thread.isQueued,
            posts = posts
        )

        fun toModelThreads(threads: List<StoredThread>): List<BoardThread> =
            threads.map { thread ->
                BoardThread(
                    num = thread.num,
                    title = thread.title,
                    isHidden = thread.isHidden,
                    isDownloaded = thread.isDownloaded,
                    isFavorite = thread.isFavorite,
                    isQueued = thread.isQueued
                )

            }

        fun toStoredPost(post: Post, threadNum: Int, boardId: String): StoredPost = StoredPost(
            boardId = boardId,
            num = post.num,
            threadNum = threadNum,
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
            replies = post.replies
        )

        fun toStoredFile(
            file: AttachFile,
            postNum: Int,
            boardId: String,
            threadNum: Int,
            localPath: String,
            localThumbnail: String
        ): StoredFile = StoredFile(
            boardId = boardId,
            postNum = postNum,
            displayName = file.displayName,
            height = file.height,
            width = file.width,
            tn_height = file.tn_height,
            tn_width = file.tn_width,
            webPath = file.path,
//            localPath = if (file.duration == "") fileStorage.downloadImage(file.path).toString() else "",
            localPath = localPath,
            webThumbnail = file.thumbnail,
//            localThumbnail = fileStorage.downloadImage(file.thumbnail).toString(),
            localThumbnail = localThumbnail,
            duration = file.duration,
            isOpPostFile = postNum == threadNum,
            threadNum = threadNum
        )

        fun toStoredFile(
            file: AttachFile,
            postNum: Int,
            boardId: String,
            threadNum: Int
        ): StoredFile = StoredFile(
            boardId = boardId,
            postNum = postNum,
            displayName = file.displayName,
            height = file.height,
            width = file.width,
            tn_height = file.tn_height,
            tn_width = file.tn_width,
            webPath = file.path,
            localPath = file.localPath,
            webThumbnail = file.thumbnail,
            localThumbnail = file.localThumbnail,
            duration = file.duration,
            isOpPostFile = postNum == threadNum,
            threadNum = threadNum
        )

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


        fun toModelFile(file: StoredFile): AttachFile = AttachFile(
            path = file.webPath,
            thumbnail = file.webThumbnail,
            localPath = file.localPath,
            localThumbnail = file.localThumbnail,
            duration = file.duration
        )

    }
}