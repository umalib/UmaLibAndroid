package cn.umafan.lib.android.util

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.content.SharedPreferences
import android.database.Cursor
import android.net.Uri
import android.os.Handler
import android.provider.DocumentsContract
import android.provider.MediaStore
import androidx.core.net.toUri
import cn.umafan.lib.android.R
import cn.umafan.lib.android.model.MyApplication
import java.io.*

object SettingUtil {
    private const val fileName = "setting"
    const val INDEX_BG = "index_bg"
    const val APP_BAR_BG = "app_bar_bg"
    const val SAVE_IMAGE_SUCCESS = 0
    const val SAVE_IMAGE_FAIL = 1

    private var sharedPreferences: SharedPreferences =
        MyApplication.context.getSharedPreferences(fileName, 0)

    fun saveImageBackground(handler: Handler, type: String, uri: Uri): Boolean {
        return try {
            SaveImageThread(handler, type, uri).start()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun getImageBackground(type: String): Uri? {
        return try {
            val uriStr = sharedPreferences.getString(type, "")
            if (null != uriStr && uriStr.isNotBlank()) {
                return uriStr.toUri()
            }
            return null
        } catch (e: Exception) {
            null
        }
    }

    fun clearImageBackground(type: String): Boolean {
        return try {
            val editor = sharedPreferences.edit()
            editor.remove(type)
            editor.apply()
            true
        } catch (e: Exception) {
            false
        }
    }

    fun saveTheme(id: Int): Boolean {
        return try {
            sharedPreferences.edit().putInt("theme", id).apply()
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getTheme(): Int {
        return try {
            return sharedPreferences.getInt("theme", R.style.Theme_UmaLibrary_NGA)
        } catch (e: Exception) {
            R.style.Theme_UmaLibrary
        }
    }

    private fun getDataColumn(
        context: Context,
        uri: Uri,
        selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        var path: String? = null
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        var cursor: Cursor? = null
        try {
            cursor =
                context.contentResolver.query(uri, projection, selection, selectionArgs, null)
            if (cursor != null && cursor.moveToFirst()) {
                val columnIndex: Int = cursor.getColumnIndexOrThrow(projection[0])
                path = cursor.getString(columnIndex)
            }
        } catch (e: java.lang.Exception) {
            cursor?.close()
        }
        return path
    }


    /**
     * 适配api19及以上,根据uri获取图片的绝对路径
     * @param context 上下文对象
     * @param uri     图片的Uri
     * @return 如果Uri对应的图片存在, 那么返回该图片的绝对路径, 否则返回null
     */
    @SuppressLint("NewApi")
    fun getRealPathFromUriAboveApi19(context: Context, uri: Uri): String? {
        var filePath: String? = null
        if (DocumentsContract.isDocumentUri(context, uri)) {
            // 如果是document类型的 uri, 则通过document id来进行处理
            val documentId = DocumentsContract.getDocumentId(uri)
            if (isMediaDocument(uri)) { // MediaProvider
                // 使用':'分割
                val id = documentId.split(":").toTypedArray()[1]
                val selection = MediaStore.Images.Media._ID + "=?"
                val selectionArgs = arrayOf(id)
                filePath = getDataColumn(
                    context,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    selection,
                    selectionArgs
                )
            } else if (isDownloadsDocument(uri)) { // DownloadsProvider
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"),
                    java.lang.Long.valueOf(documentId)
                )
                filePath = getDataColumn(context, contentUri, null, null)
            }
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {
            // 如果是 content 类型的 Uri
            filePath = getDataColumn(context, uri, null, null)
        } else if ("file" == uri.scheme) {
            // 如果是 file 类型的 Uri,直接获取图片对应的路径
            filePath = uri.path
        }
        return filePath
    }

    /**
     * @param uri the Uri to check
     * @return Whether the Uri authority is MediaProvider
     */
    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    /**
     * @param uri the Uri to check
     * @return Whether the Uri authority is DownloadsProvider
     */
    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    private class SaveImageThread(
        val handler: Handler,
        val type: String,
        val uri: Uri
    ) : Thread() {
        override fun run() {
            try {
                val context = MyApplication.context
                val file = File(getRealPathFromUriAboveApi19(context, uri)!!)
                val myInput: InputStream = FileInputStream(file)
                val outFile: File = context.getDatabasePath("$type.jpg")

                outFile.parentFile?.mkdirs()
                val myOutput: OutputStream = FileOutputStream(outFile)
                val buffer = ByteArray(5)
                var length: Int = myInput.read(buffer)
                var count = 0

                while (length > 0) {
                    myOutput.write(buffer, 0, length)
                    length = myInput.read(buffer)
                    count++
                }
                myOutput.flush()
                myOutput.close()
                myInput.close()
                sharedPreferences.edit().putString(type, outFile.path).apply()
                handler.sendEmptyMessage(SAVE_IMAGE_SUCCESS)
            } catch (e: Exception) {
                e.printStackTrace()
                handler.sendEmptyMessage(SAVE_IMAGE_FAIL)
            }
        }
    }
}