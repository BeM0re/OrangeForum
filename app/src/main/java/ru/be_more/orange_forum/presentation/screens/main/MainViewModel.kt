package ru.be_more.orange_forum.presentation.screens.main

import android.os.Build
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
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

    init {
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
}