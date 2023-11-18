package ru.be_more.orange_forum.utils

import androidx.compose.runtime.Composable
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import ru.be_more.orange_forum.presentation.screens.base.BaseViewModel

class ViewModelProvider {
    val viewModelList = mutableListOf<BaseViewModel>()

    @Composable
    inline fun <reified T: BaseViewModel> getVM(vararg args: Any?, createNew: Boolean): T =
        when {
            !createNew ->
                viewModelList
                    .filterIsInstance<T>()
                    .firstOrNull() ?:
                koinViewModel<T>(parameters = { parametersOf(*args) })
                    .also { viewModelList.add(it) }
            args.isEmpty() ->
                viewModelList
                    .filterIsInstance<T>()
                    .firstOrNull() ?:
                koinViewModel<T>()
                    .also { viewModelList.add(it) }
            else ->
                koinViewModel<T>(parameters = { parametersOf(*args) })
                    .also { viewModelList.removeIf { it is T } }
                    .also { viewModelList.add(it) }
        }
}