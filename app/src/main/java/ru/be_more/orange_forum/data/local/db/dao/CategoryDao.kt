package ru.be_more.orange_forum.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.be_more.orange_forum.data.local.db.entities.StoredCategory

@Dao
interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(categories: List<StoredCategory>)


    @Query("SELECT * FROM categories")
    fun getCategoryListFlow(): Flow<List<StoredCategory>>

    @Query("SELECT * FROM categories WHERE name = :name")
    suspend fun getCategory(name: String): StoredCategory

    @Query("UPDATE categories SET isExpanded = :isExpanded WHERE name = :name")
    suspend fun setIsExpanded(name: String, isExpanded: Boolean)


    @Query("DELETE FROM categories")
    suspend fun delete()
}