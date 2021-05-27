package ru.be_more.orange_forum.domain.model

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup

data class Category(
    val name: String?,
    val boards: List<Board?>?
):ExpandableGroup<Board?>(name, boards)
