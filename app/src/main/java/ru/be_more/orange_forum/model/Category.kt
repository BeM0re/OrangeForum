package ru.be_more.orange_forum.model

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup

class Category(
    title: String?,
    items: List<Board?>?
):ExpandableGroup<Board?>(title, items)
