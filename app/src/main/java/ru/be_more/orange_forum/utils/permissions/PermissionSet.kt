package ru.be_more.orange_forum.utils.permissions

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import ru.be_more.orange_forum.R

enum class PermissionSet(
    val tag: String,
    val permissions: Set<String>,
    @StringRes val rationaleTextId: Int,
) {
    Camera(
        tag = "cameraPermissionTag",
        permissions = setOf(Manifest.permission.CAMERA),
        rationaleTextId = R.string.permission_camera
    ),
    Media(
        tag = "mediaPermissionTag",
        permissions =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            setOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO,
            )
        } else {
            setOf(
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        },
        rationaleTextId = R.string.permission_media
    ),
    @RequiresApi(31)
    Notifications(
        tag = "notificationPermissionTag",
        permissions = setOf(Manifest.permission.POST_NOTIFICATIONS),
        rationaleTextId = R.string.permission_notifications
    )
}