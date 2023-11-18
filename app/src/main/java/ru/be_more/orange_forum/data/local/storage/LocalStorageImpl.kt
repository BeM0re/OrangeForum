package ru.be_more.orange_forum.data.local.storage

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import ru.be_more.orange_forum.consts.DVACH_ROOT_URL
import ru.be_more.orange_forum.domain.contracts.StorageContract
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class LocalStorageImpl(
    private val context: Context
) : StorageContract.LocalStorage{

    override fun saveFile(url: String): Uri? {
        val glideUrl = GlideUrl(
            DVACH_ROOT_URL +url.substring(1), LazyHeaders.Builder()
                .addHeader("Cookie", "usercode_auth=54e8a3b3c8d5c3d6cffb841e9bf7da63; " +
                        "_ga=GA1.2.57010468.1498700728; " +
                        "ageallow=1; " +
                        "_gid=GA1.2.1910512907.1585793763; " +
                        "_gat=1")
                .build()
        )

        val bitmap = Glide.with(context)
            .asBitmap()
            .load(glideUrl)
            .submit()
            .get()

        return try {
            val photoFile: File = createImageFile()
            val fileName = FileProvider.getUriForFile(context,"ru.be_more.orange_forum.fileprovider", photoFile)
            val out = FileOutputStream(photoFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            out.flush()
            out.close()
            fileName
        } catch(e: Exception) {
            Log.e("M_FileStorage", "Image NOT saved. Error = $e")
            null
        }
    }

    override fun delete(path: String) {
        try {
            context.contentResolver.delete(Uri.parse(path), null, null)
        }
        catch(e: java.lang.Exception){
            Log.e("M_FileStorage", "on delete error = $e")
        }
    }

    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp = "${System.currentTimeMillis()}"
        val storageDir: File = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            ?: throw Throwable("\nLocalStorageImpl.createImageFile: Can get ExternalFilesDir for file saving")
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        )
    }

}