package ru.be_more.orange_forum.ui.category.feature

import com.badoo.mvicore.element.Reducer
import com.badoo.mvicore.feature.ReducerFeature
import ru.be_more.orange_forum.model.Category
import ru.be_more.orange_forum.ui.category.feature.CategoryFeature.*

class CategoryFeature : ReducerFeature<Wish, State, Nothing>(
    initialState = State(),
    reducer = ReducerImpl()
){
    data class State(
        val categories: List<Category> = listOf()
    )

    sealed class Wish{
        object CategorySelected : Wish()
    }

    class ReducerImpl : Reducer <State, Wish>{
        override fun invoke(state: State, effect: Wish): State = when(effect){
            Wish.CategorySelected -> state.copy()
        }
    }
}