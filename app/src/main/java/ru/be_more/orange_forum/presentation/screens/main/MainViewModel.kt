package ru.be_more.orange_forum.presentation.screens.main

import android.os.Build
import android.util.Log
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import ru.be_more.orange_forum.domain.contracts.InteractorContract
import ru.be_more.orange_forum.presentation.screens.base.BaseViewModel
import ru.be_more.orange_forum.utils.permissions.PermissionSet
import ru.be_more.orange_forum.utils.permissions.PermissionsRequest

class MainViewModel(
    private val favoriteInteractor: InteractorContract.FavoriteInteractor,
) : BaseViewModel() {

    private val requestPermissionsChannel = Channel<PermissionsRequest>()
    val requestPermissionsFlow = requestPermissionsChannel.receiveAsFlow()

    private val hasFavoriteNewMessage = MutableStateFlow(false)
    val hasFavoriteNewMessageFlow = hasFavoriteNewMessage.asStateFlow()

    init {
        requestNotificationPermission()
        subscribeToFavoriteNewMessages()
        subscribeToFavoriteUpdates()
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

    private fun subscribeToFavoriteNewMessages() {
        favoriteInteractor
            .observeNewMessages()
            .defaultThreads()
            .subscribe(
                { hasFavoriteNewMessage.value = it },
                { Log.e("MainViewModel", "MainViewModel.subscribeToFavoriteNewMessages: \n$it") }
            )
            .addToSubscribe()
    }

    private fun subscribeToFavoriteUpdates() {
        favoriteInteractor
            .updatingFavoritesSubscription()
            .defaultThreads()
            .subscribe(
                {},
                { Log.e("MainViewModel", "MainViewModel.subscribeToFavoriteNewMessages: \n$it") }
            )
            .addToSubscribe()
    }
}