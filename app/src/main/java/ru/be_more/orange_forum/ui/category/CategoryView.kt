package ru.be_more.orange_forum.ui.category

import ru.be_more.orange_forum.domain.model.Category

interface CategoryView {
    fun loadCategories(categories: List<Category>)
    fun expandCategories()
    fun restoreState(expandedItems: List<Int>, savedQuery: String)
}