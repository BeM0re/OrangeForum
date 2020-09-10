package ru.be_more.orange_forum.data.db.storage

import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import ru.be_more.orange_forum.App
import ru.be_more.orange_forum.data.remote.utils.DVACH_ROOT_URL
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

class FileStorage @Inject constructor(){
    private fun downloadImage(url: String): Uri? {
//        val fulUrl = DVACH_ROOT_URL+url
        val glideUrl = GlideUrl(
            DVACH_ROOT_URL +url.substring(1), LazyHeaders.Builder()
                .addHeader("Cookie", "usercode_auth=54e8a3b3c8d5c3d6cffb841e9bf7da63; " +
                        "_ga=GA1.2.57010468.1498700728; " +
                        "ageallow=1; " +
                        "_gid=GA1.2.1910512907.1585793763; " +
                        "_gat=1")
                .build()
        )

        val bitmap = Glide.with(App.applicationContext())
            .asBitmap()
            .load(glideUrl)
            .submit()
            .get()

        try {
            val photoFile: File? = try {
                createImageFile()
            } catch (ex: IOException) {
                Log.d("M_FileStorage", "$ex")
                return null
            }
            var fileName: Uri? = null
            if(photoFile != null) {
                fileName = FileProvider.getUriForFile(
                    App.applicationContext(),"ru.be_more.orange_forum.fileprovider", photoFile)
                val out = FileOutputStream(photoFile)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
                out.flush()
                out.close()
            }
            return fileName
        } catch (e: Exception) {
            Log.e("M_FileStorage", "Image NOT saved. Error = $e")
            return null
        }
    }

    private fun deleteImage(path: String){
        try {
            App.applicationContext().contentResolver.delete(Uri.parse(path), null, null)
        }
        catch (e: java.lang.Exception){
            Log.e("M_FileStorage", "on delete error = $e")
        }

    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = "${System.currentTimeMillis()}"
        val storageDir: File = App.applicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        )
    }
}