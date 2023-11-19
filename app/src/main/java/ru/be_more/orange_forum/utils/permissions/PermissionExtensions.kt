package ru.be_more.orange_forum.utils.permissions

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker

fun Activity.isPermissionGranted(permission: String): Boolean =
    ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
            && PermissionChecker.checkSelfPermission(this, permission) == PermissionChecker.PERMISSION_GRANTED

fun Activity.isPermissionsGranted(permissions: Set<String>): Boolean =
    permissions.all { isPermissionGranted(it) }

fun Activity.shouldShowRequestPermissionRationale(permissions: Set<String>): Boolean {
    permissions.forEach {
        if (!isPermissionGranted(it) && !shouldShowRequestPermissionRationale(it)) {
            return false
        }
    }

    return true
}