package ru.be_more.orange_forum.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations

/*
object ListItemRepository {

    private lateinit var listItemDao: ListItemDao
    private lateinit var db: AppDatabase

    fun initDatabase() : ListItemDao{
        db = App.getDatabase()
        listItemDao = db.listItemDao()
        return listItemDao
    }

    fun getItems() = domainToDto()

    suspend fun remove(id: Long) {
        listItemDao.setStatus(ListItemEntity.Status.LAST_REMOVED, ListItemEntity.Status.REMOVED)
        listItemDao.setStatus(id, ListItemEntity.Status.LAST_REMOVED)
    }

    suspend fun restore(){
        listItemDao.setStatus(ListItemEntity.Status.LAST_REMOVED, ListItemEntity.Status.ACTIVE)
    }

    suspend fun addItem(item: ListItem){
        listItemDao.insertItem(toEntity(item))
    }

    suspend fun editItem(id: Long, content: String, isChecked: Boolean){
        listItemDao.editItem(id, content, isChecked)
    }

    suspend fun changeChecked(itemId: Long, isChecked : Boolean){
        listItemDao.setCheckbox(itemId, isChecked)
    }

    private fun domainToDto() : LiveData<List<ListItem>>{

        return Transformations.map(listItemDao.getAll()){entityList ->
            return@map entityList.filter { it.status == ListItemEntity.Status.ACTIVE }
                .map { toListItem(it) }
                .sortedBy { it.id }
        }
    }

    private fun toListItem (entity : ListItemEntity) : ListItem = ListItem(
            id = entity.id!!,
            content = entity.content,
            isChecked = entity.isChecked
        )

    private fun toEntity (item : ListItem) : ListItemEntity = ListItemEntity(
            content = item.content,
            isChecked = item.isChecked,
            status = ListItemEntity.Status.ACTIVE)
}*/