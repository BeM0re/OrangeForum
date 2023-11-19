package ru.be_more.orange_forum.utils.permissions

import android.os.Parcelable
import androidx.annotation.StringRes
import kotlinx.parcelize.Parcelize

@Parcelize
data class PermissionsRequest(
    val tag: String,
    val permissions: Set<String>,
    val skipShouldShowRequestPermissionRationale: Boolean,
    @StringRes val rationaleTextResId: Int?,
) : Parcelable {
    constructor(
        permissionSet: PermissionSet,
        skipShouldShowRequestPermissionRationale: Boolean,
        rationaleTextResId: Int?,
    ) : this(
        tag = permissionSet.tag,
        permissions = permissionSet.permissions,
        skipShouldShowRequestPermissionRationale = skipShouldShowRequestPermissionRationale,
        rationaleTextResId = rationaleTextResId
    )
}