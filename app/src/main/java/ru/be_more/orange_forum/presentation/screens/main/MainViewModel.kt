package ru.be_more.orange_forum.presentation.screens.main

import android.os.Build
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import ru.be_more.orange_forum.domain.contracts.InteractorContract
import ru.be_more.orange_forum.presentation.screens.base.BaseViewModel
import ru.be_more.orange_forum.utils.permissions.PermissionSet
import ru.be_more.orange_forum.utils.permissions.PermissionsRequest
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val favoriteInteractor: InteractorContract.FavoriteInteractor,
) : BaseViewModel() {

    private var favoriteUpdateSubscription = mutableListOf<Job>()

    private val requestPermissionsChannel = Channel<PermissionsRequest>()
    val requestPermissionsFlow = requestPermissionsChannel.receiveAsFlow()

    private val hasFavoriteNewMessage = MutableStateFlow(false)
    val hasFavoriteNewMessageFlow = hasFavoriteNewMessage.asStateFlow()

    init {
        requestNotificationPermission()
    }

    private fun requestNotificationPermission() {
        viewModelScope.launch {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                requestPermissionsChannel.send(
                    PermissionsRequest(
                        permissionSet = PermissionSet.Notifications,
                        skipShouldShowRequestPermissionRationale = false,
                        rationaleTextResId = PermissionSet.Notifications.rationaleTextId,
                    )
                )
            }
        }
    }

    private fun subscribeToFavoriteNewMessages() =
        runCoroutine("subscribeToFavoriteNewMessages") {
            favoriteInteractor
                .getBoardFlow()
                .collect {
                    hasFavoriteNewMessage.value = it
                }
        }.also { favoriteUpdateSubscription.add(it)  }


    private fun subscribeToFavoriteUpdates() =
        runCoroutine("subscribeToFavoriteNewMessages") {
            favoriteInteractor
                .updatingFavoritesSubscription()
        }.also { favoriteUpdateSubscription.add(it)  }

    fun onPause() {
        favoriteUpdateSubscription.forEach { it.cancel() }
    }

    fun onResume() {
        subscribeToFavoriteNewMessages()
        subscribeToFavoriteUpdates()
    }
}