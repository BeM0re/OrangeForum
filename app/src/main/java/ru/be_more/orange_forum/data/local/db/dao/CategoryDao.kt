package ru.be_more.orange_forum.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import ru.be_more.orange_forum.data.local.db.entities.StoredBoard
import ru.be_more.orange_forum.data.local.db.entities.StoredCategory
import ru.be_more.orange_forum.data.local.db.entities.StoredFile
import ru.be_more.orange_forum.data.local.db.entities.StoredPost
import ru.be_more.orange_forum.data.local.db.entities.StoredThread
import ru.be_more.orange_forum.domain.model.Category

@Dao
interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(categories: List<StoredCategory>): Completable

    @Query("SELECT * FROM categories")
    fun observeCategories(): Observable<List<StoredCategory>>

    @Query("SELECT * FROM categories WHERE name = :name")
    fun getCategory(name: String): Single<StoredCategory>

    @Query("UPDATE categories SET isExpanded = :isExpanded WHERE name = :name")
    fun setIsExpanded(name: String, isExpanded: Boolean): Completable
}