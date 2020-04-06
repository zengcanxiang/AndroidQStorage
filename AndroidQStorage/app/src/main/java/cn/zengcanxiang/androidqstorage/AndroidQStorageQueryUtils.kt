package cn.zengcanxiang.androidqstorage

import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Size
import java.io.FileDescriptor
import java.io.FileNotFoundException

object AndroidQStorageQueryUtils {

    fun queryAllMediaData(
        context: Context,
        queryUri: Uri,
        idKey: String
    ): List<Uri> {
        // 查询条件，null为查询uri所有的
        val selection = null
        // 查询条件的具体值。规范的查询，应该是在selection中写条件。例如name = ?, id = ?.
        // 具体的值使用args数组来传递。系统会与？的顺序一一对应
        val args = arrayOf<String>()
        // 需要返回的数据,null返回所有
        val projection = arrayOf<String>()
        // 排序条件，null为默认排序。写法为 "xxx DESC" 降序 , "xxx ASC" 升序
        val sort = MediaStore.Images.ImageColumns.DATE_MODIFIED + "  desc"
        val result = mutableListOf<Uri>()
        context.contentResolver?.query(queryUri, projection, selection, args, sort)?.use {
            while (it.moveToNext()) {
                val idIndex = it.getColumnIndex(idKey)
                if (idIndex == -1) {
                    continue
                }
                val id = it.getLong(idIndex)
                ContentUris.withAppendedId(
                    queryUri,
                    id
                ).let { uri ->
                    result.add(uri)
                }
            }
        }
        return result
    }

    /**
     * 只能获取到文件名称
     */
    fun queryFileName4Uri(context: Context, uri: Uri, nameKey: String): String? {
        if (DocumentsContract.isTreeUri(uri)) {
            val path = uri.path ?: return null
            return path.substring(path.lastIndexOf(":") + 1)
        }
        context.contentResolver?.query(uri, null, null, null, null)?.use {
            val isHas = it.moveToFirst()
            if (isHas) {
                val nameIndex = it.getColumnIndex(nameKey)
                if (nameIndex != -1) {
                    return it.getString(nameIndex)
                }
            }
        }
        return null
    }

    fun queryFilePath4Uri(context: Context, uri: Uri, pathKey: String): String? {
        context.contentResolver?.query(uri, null, null, null, null)?.use {
            val isHas = it.moveToFirst()
            if (isHas) {
                val dataIndex = it.getColumnIndex(pathKey)
                if (dataIndex != -1) {
                    val data = it.getString(dataIndex)
                    if (!TextUtils.isEmpty(data)) {
                        return data
                    }
                }
            }
        }
        return uri.path
    }

    /**
     * 加载出来的是源文件
     * 如果是图片，转换成bitmap，是原图尺寸
     */
    fun queryFile4Uri(context: Context, uri: Uri): FileDescriptor? {
        return try {
            context.contentResolver?.openFileDescriptor(
                uri,
                AndroidQStorageUtils.OPEN_FILE_DESCRIPTOR_MODE_READ
            )?.fileDescriptor
        } catch (e: FileNotFoundException) {
            null
        }
    }

    /**
     *  透明背景的图片加载出来是黑色的。
     */
    fun loadThumbnail4Uri(context: Context, uri: Uri): Bitmap? {
        return try {
            context.contentResolver.loadThumbnail(
                uri,
                Size(100, 100),
                null
            )
        } catch (e: FileNotFoundException) {
            null
        }

    }
}
