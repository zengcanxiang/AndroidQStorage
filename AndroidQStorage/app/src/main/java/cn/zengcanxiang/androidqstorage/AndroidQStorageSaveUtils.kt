package cn.zengcanxiang.androidqstorage

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import java.io.InputStream

object AndroidQStorageSaveUtils {
    /**
     * mediaStore接口保存
     */
    fun saveBitmap2Public(context: Context, bitmap: Bitmap, dirName: String): Uri? {
        //TODO 存在插入失败的情况，UNIQUE constraint failed: files._data。
        // 由此可见，这些属性不能重复。特别是 DISPLAY_NAME
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.TITLE, "title_1")
            put(MediaStore.Images.Media.DISPLAY_NAME, "sample_${System.currentTimeMillis()}.png")
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.RELATIVE_PATH, "$dirName/sample")
        }
        val contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        var result: Uri? = null
        context.contentResolver?.let { resolver ->
            resolver.insert(
                contentUri,
                values
            )?.let { insertUri ->
                result = insertUri
                resolver.openOutputStream(insertUri)?.use {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
                }
            }
        }
        return result
    }

    fun saveBitmap2Uir(context: Context, saveUri: Uri, bitmap: Bitmap) {
        context.contentResolver?.let { resolver ->
            resolver.openOutputStream(saveUri)?.use {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            }
        }
    }

    fun saveFile2Public(
        context: Context,
        source: InputStream,
        dirName: String,
        suffix: String
    ): Uri? {
        val values = if (TextUtils.equals(dirName, Environment.DIRECTORY_DOWNLOADS)) {
            buildDownloadContentValues(dirName, suffix)
        } else {
            buildDocumentsContentValues(dirName, suffix)
        }

        val contentUri = if (TextUtils.equals(dirName, Environment.DIRECTORY_DOWNLOADS)) {
            MediaStore.Downloads.EXTERNAL_CONTENT_URI
        } else {
            MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL)
        }

        var result: Uri? = null
        context.contentResolver?.let { resolver ->
            resolver.insert(
                contentUri,
                values
            )?.let { insertUri ->
                result = insertUri
                resolver.openOutputStream(insertUri)?.use { outPut ->
                    var read: Int = -1
                    val buffer = ByteArray(2048)
                    while (source.read(buffer).also { read = it } != -1) {
                        outPut.write(buffer, 0, read)
                    }
                }
            }
        }
        return result
    }


    fun saveFile2Uri(
        context: Context,
        saveUri: Uri,
        source: InputStream
    ) {
        context.contentResolver?.let { resolver ->
            resolver.openOutputStream(saveUri)?.use { outPut ->
                var read: Int = -1
                val buffer = ByteArray(2048)
                while (source.read(buffer).also { read = it } != -1) {
                    outPut.write(buffer, 0, read)
                }
            }
        }
    }

    fun saveVideo2Public(
        context: Context,
        source: InputStream,
        dirName: String,
        suffix: String
    ): Uri? {
        val values = ContentValues().apply {
            put(MediaStore.Video.Media.TITLE, "title_1")
            put(
                MediaStore.Video.Media.DISPLAY_NAME,
                "sample_${System.currentTimeMillis()}.${suffix}"
            )
            put(MediaStore.Video.Media.MIME_TYPE, "video/$suffix")
            put(MediaStore.Video.Media.RELATIVE_PATH, "$dirName/sample")
        }
        val contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        var result: Uri? = null
        context.contentResolver?.let { resolver ->
            resolver.insert(
                contentUri,
                values
            )?.let { insertUri ->
                result = insertUri
                resolver.openOutputStream(insertUri)?.use { outPut ->
                    var read: Int = -1
                    val buffer = ByteArray(2014)
                    while (source.read(buffer).also { read = it } != -1) {
                        outPut.write(buffer, 0, read)
                    }
                }
            }
        }
        return result
    }

    fun saveVideo2Uri(
        context: Context,
        saveUri: Uri,
        source: InputStream
    ) {
        context.contentResolver?.let { resolver ->
            resolver.openOutputStream(saveUri)?.use { outPut ->
                var read: Int = -1
                val buffer = ByteArray(2014)
                while (source.read(buffer).also { read = it } != -1) {
                    outPut.write(buffer, 0, read)
                }
            }
        }
    }

    fun saveAudio2Public(
        context: Context,
        source: InputStream,
        dirName: String,
        suffix: String
    ): Uri? {
        val values = ContentValues().apply {
            put(MediaStore.Audio.Media.TITLE, "title_1")
            put(
                MediaStore.Audio.Media.DISPLAY_NAME,
                "sample_${System.currentTimeMillis()}.${suffix}"
            )
            put(MediaStore.Audio.Media.MIME_TYPE, "audio/$suffix")
            put(MediaStore.Audio.Media.RELATIVE_PATH, "$dirName/sample")
        }
        val contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        var result: Uri? = null
        context.contentResolver?.let { resolver ->
            resolver.insert(
                contentUri,
                values
            )?.let { insertUri ->
                result = insertUri
                resolver.openOutputStream(insertUri)?.use { outPut ->
                    var read: Int = -1
                    val buffer = ByteArray(2048)
                    while (source.read(buffer).also { read = it } != -1) {
                        outPut.write(buffer, 0, read)
                    }
                }
            }
        }
        return result
    }

    fun saveAudio2Uri(
        context: Context,
        saveUri: Uri,
        source: InputStream
    ) {
        context.contentResolver?.let { resolver ->
            resolver.openOutputStream(saveUri)?.use { outPut ->
                var read: Int = -1
                val buffer = ByteArray(2048)
                while (source.read(buffer).also { read = it } != -1) {
                    outPut.write(buffer, 0, read)
                }
            }
        }
    }

    private fun buildDownloadContentValues(dirName: String, suffix: String): ContentValues {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "title_1")
        values.put(
            MediaStore.Downloads.DISPLAY_NAME,
            "sample_${System.currentTimeMillis()}.$suffix"
        )
        values.put(
            MediaStore.Downloads.RELATIVE_PATH,
            "$dirName/sample"
        )
        return values
    }

    private fun buildDocumentsContentValues(dirName: String, suffix: String): ContentValues {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "title_1")
        values.put(
            MediaStore.Downloads.DISPLAY_NAME,
            "sample_${System.currentTimeMillis()}.$suffix"
        )
        values.put(
            MediaStore.Downloads.RELATIVE_PATH,
            "$dirName/sample"
        )
        return values
    }

}
