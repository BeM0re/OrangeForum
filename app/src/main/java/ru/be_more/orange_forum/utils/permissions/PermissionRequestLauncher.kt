package ru.be_more.orange_forum.utils.permissions

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

/**
 * Лаунчер запросов разрешений.
 */
interface PermissionRequestLauncher {
    /**
     * Запросить/проверить разрешения по предоставленому запросу.
     */
    fun request(request: PermissionsRequest)

    /**
     * Калбек для обработки результата выполнения запроса.
     */
    fun interface Callback {

        /**
         * Калбек для обработки результата выполнения запроса.
         */
        fun onPermissionRequestResult(result: PermissionRequestResult)
    }
}

/**
 * Регистрация лаунчера пермишенов.
 */
fun ActivityResultCaller.registerPermissionsLauncher(
    handler: PermissionRequestLauncher.Callback,
): PermissionRequestLauncher =
    object : PermissionRequestLauncher {
        private var request: PermissionsRequest? = null

        // (polkovnik): внутренний механизм работы с калбеками запроса рзрешений.
        private val launcher = registerForActivityResult(RequestMultiplePermissions()) { map ->
            val request = request ?: return@registerForActivityResult

            if (map.isEmpty())
                Log.e(
                    "ActivityResultCaller.registerPermissionsLauncher",
                    "permission request with tag ${request.tag} has an empty map in result"
                )

            handler.onPermissionRequestResult(
                PermissionRequestResult(
                    request = request,
                    isGranted = map.isNotEmpty() && map.values.all { it },
                    shouldShowRequestPermissionRationale = false,
                )
            )
        }

        override fun request(request: PermissionsRequest) {
            val activity = when (this@registerPermissionsLauncher) {
                is AppCompatActivity -> this@registerPermissionsLauncher
                is Fragment -> this@registerPermissionsLauncher.requireActivity()
                is ComponentActivity -> this@registerPermissionsLauncher
                else -> throw UnsupportedOperationException("${this@registerPermissionsLauncher} not supported registration")
            }

            // Механизм работы:
            // 1 - узнаем получены ли разрешения
            if (activity.isPermissionsGranted(request.permissions)) {
                handler.onPermissionRequestResult(
                    PermissionRequestResult(
                        request = request,
                        isGranted = true,
                        shouldShowRequestPermissionRationale = false,
                    )
                )

                return
            }

            // 2 - Разрешения не получены. Узнаем нужно ли показывать Rationale для разрешений.
            // Можно скипнуть, если это повторный запрос после показа rationale или rationale не нужен.
            if (activity.shouldShowRequestPermissionRationale(request.permissions)
                && !request.skipShouldShowRequestPermissionRationale
            ) {
                handler.onPermissionRequestResult(
                    PermissionRequestResult(
                        request = request,
                        isGranted = false,
                        shouldShowRequestPermissionRationale = true,
                    )
                )

                return
            }

            this.request = request

            // 3 - Если не разрешены и скипнут/показан rationale запрашиваем разрешения.
            Log.d(
                "ActivityResultCaller.registerPermissionsLauncher",
                "Requesting permissions: ${request.permissions}"
            )
            launcher.launch(request.permissions.toTypedArray())
        }
    }
