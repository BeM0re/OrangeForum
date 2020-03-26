package ru.be_more.orange_forum.model

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup
import ru.be_more.orange_forum.model.Board

class Category(title: String?, items: List<Board?>?) :
    ExpandableGroup<Board?>(title, items)
