package ru.be_more.orange_forum.utils.permissions

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PermissionRequestResult(
    val request: PermissionsRequest,
    val isGranted: Boolean,
    val shouldShowRequestPermissionRationale: Boolean,
) : Parcelable