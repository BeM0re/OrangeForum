package ru.be_more.orange_forum.utils

import androidx.compose.runtime.Composable
import ru.be_more.orange_forum.presentation.screens.base.BaseViewModel
import ru.be_more.orange_forum.presentation.screens.board.BoardViewModel

class ViewModelProvider {
    val viewModelMap = HashMap<Class<out BaseViewModel>, BaseViewModel>()

/*    @Composable
    inline fun <reified T: BaseViewModel> getVM(vararg args: Any?, createNew: Boolean): T =
        when {
            !createNew ->
                viewModelMap
                    .getOrPut(T::class.java) {
                        BoardViewModel()
                    } as T

            args.isEmpty() ->
                viewModelMap
                    .getOrPut(T::class.java) {
                        BoardViewModel()
                    } as T
            else ->
                BoardViewModel()
                    .also {
                        viewModelMap.put(T::class.java, it)
                    } as T
        }*/
}
