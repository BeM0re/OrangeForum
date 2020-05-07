package ru.be_more.orange_forum.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import ru.be_more.orange_forum.App
import ru.be_more.orange_forum.data.AppDatabase
import ru.be_more.orange_forum.data.DvachDao


object DvachDbRepository {

    private lateinit var dvachDbDao: DvachDao
    private lateinit var db: AppDatabase

    fun initDatabase() : DvachDao{
        db = App.getDatabase()
        dvachDbDao = db.dvachDao()
        return dvachDbDao
    }

/*    fun getItems() = domainToDto()

    fun remove(id: Long) {
        listItemDao.setStatus(ListItemEntity.Status.LAST_REMOVED, ListItemEntity.Status.REMOVED)
        listItemDao.setStatus(id, ListItemEntity.Status.LAST_REMOVED)
    }

    fun restore(){
        listItemDao.setStatus(ListItemEntity.Status.LAST_REMOVED, ListItemEntity.Status.ACTIVE)
    }

    fun addItem(item: ListItem){
        listItemDao.insertItem(toEntity(item))
    }

    fun editItem(id: Long, content: String, isChecked: Boolean){
        listItemDao.editItem(id, content, isChecked)
    }

    fun changeChecked(itemId: Long, isChecked : Boolean){
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
            status = ListItemEntity.Status.ACTIVE)*/
}