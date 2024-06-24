package ru.be_more.orange_forum.presentation.screens.base

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.be_more.orange_forum.presentation.model.ContentState
import ru.be_more.orange_forum.presentation.model.NavigationState
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

abstract class BaseViewModel : ViewModel() {

    private val navMutableState = MutableSharedFlow<NavigationState>()
    open val navState = navMutableState.asSharedFlow()

    private val contentStateMutableState = MutableStateFlow<ContentState>(ContentState.Loading)
    open val contentState = contentStateMutableState.asStateFlow()

    protected fun exceptionHandler(methodName: String = ""): CoroutineExceptionHandler =
        object : AbstractCoroutineContextElement(CoroutineExceptionHandler), CoroutineExceptionHandler {
            override fun handleException(context: CoroutineContext, exception: Throwable) {
                Log.e(
                    this@BaseViewModel::class.java.name,
                    this@BaseViewModel::class.java.name
                            + ("methodName: $methodName \n".takeIf { methodName.isNotEmpty() } ?: "")
                            + exception.message
                )
            }
        }

    protected fun runCoroutine(
        methodName: String = "",
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        scope: CoroutineScope = viewModelScope,
        block: suspend CoroutineScope.() -> Unit
    ): Job =
        scope.launch(
            context = dispatcher + exceptionHandler(methodName),
            block = block
        )


    protected fun navigateToBoard(boardId: String) {
        viewModelScope.launch {
            navMutableState.emit(
                NavigationState.NavigateToBoard(boardId)
            )
        }
    }

    protected fun navigateToThread(boardId: String, threadNum: Int) {
        viewModelScope.launch {
            navMutableState.emit(
                NavigationState.NavigateToThread(boardId, threadNum)
            )
        }
    }

    protected fun navigateToReply(boardId: String, threadNum: Int, additionalString: String) {
        viewModelScope.launch {
            navMutableState.emit(
                NavigationState.NavigateToReply(boardId, threadNum, additionalString)
            )
        }
    }

    protected fun navigateToThreadCreating(boardId: String) {
        viewModelScope.launch {
            navMutableState.emit(
                NavigationState.NavigateToThreadCreating(boardId)
            )
        }
    }

    protected fun showContent() {
        viewModelScope.launch {
            contentStateMutableState.emit(
                ContentState.Content
            )
        }
    }

    protected fun showLoading() {
        viewModelScope.launch {
            contentStateMutableState.emit(
                ContentState.Loading
            )
        }
    }

    protected fun showError(message: String) {
        viewModelScope.launch {
            contentStateMutableState.emit(
                ContentState.Error(message)
            )
        }
    }

}