package cn.zengcanxiang.androidqstorage.SAF

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import java.lang.Exception

class SAFFragment : Fragment(),
    SafInterface {

    var callback: ResultCallBack? = null

    override fun openSafActivity(
        activity: AppCompatActivity,
        params: OpenSafParams,
        resultCallBack: ResultCallBack?
    ) {
        if (resultCallBack != null) {
            callback = resultCallBack
        }
        Utils.openSafActivity(
            params
        ).let {
            val requestCode = if (params.model == OpenSafModel.FILE) {
                if (params.isMultiple) {
                    key_fragment_request_code_open_file_multiple
                } else {
                    key_fragment_request_code_open_file
                }
            } else {
                key_fragment_request_code_open_file_tree
            }
            startActivityForResult(it, requestCode)
        }
    }

    override fun newFileToSAF(
        activity: AppCompatActivity,
        name: String,
        type: String,
        resultCallBack: ResultCallBack?
    ) {
        if (resultCallBack != null) {
            callback = resultCallBack
        }
        Utils.newFileTOSAF(name, type).let {
            startActivityForResult(
                it,
                key_fragment_request_code_new_file
            )
        }
    }


    /**
     * 持久化权限校验
     */
    override fun requestUriPersistablePermission(
        activity: AppCompatActivity,
        uri: Uri
    ): Boolean {
        return Utils.requestUriPermission(
            activity,
            uri
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            callback?.fail()
            return
        }
        when (requestCode) {
            key_fragment_request_code_new_file -> {
                callback?.success(listOf(data?.data))
            }
            key_fragment_request_code_open_file, key_fragment_request_code_open_file_tree -> {
                callback?.success(listOf(data?.data))
            }
            key_fragment_request_code_open_file_multiple -> {
                // 多选，如果选择一条，可能不通过clipdata传递。需要再去获取一次data
                data?.clipData?.let { clipData ->
                    val count = clipData.itemCount
                    val list = mutableListOf<Uri?>()
                    for (i in 0 until count) {
                        list.add(clipData.getItemAt(i).uri)
                    }
                    callback?.success(list)
                } ?: kotlin.run {
                    callback?.success(listOf(data?.data))
                }
            }
        }
    }

    companion object {
        private const val key_fragment_request_code_new_file = 0x1234
        private const val key_fragment_request_code_open_file = 0x1235
        private const val key_fragment_request_code_open_file_multiple = 0x1236
        private const val key_fragment_request_code_open_file_tree = 0x1237
    }
}

internal object Utils : SAFSampleInterface {

    override fun openSafActivity(
        params: OpenSafParams
    ): Intent {
        val intent = if (params.model == OpenSafModel.FILE) {
            Intent.ACTION_OPEN_DOCUMENT
        } else {
            Intent.ACTION_OPEN_DOCUMENT_TREE
        }.let {
            Intent(it)
        }

        if (params.model == OpenSafModel.FILE) {
            val mimeType = if (TextUtils.isEmpty(params.mimeType)) {
                "*/*"
            } else {
                params.mimeType
            }
            intent.type = mimeType
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, params.isMultiple)
        }

        if (params.requestPersistablePermission) {
            val requestPermissionFlag =
                Intent.FLAG_GRANT_READ_URI_PERMISSION and Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            intent.flags = intent.flags and requestPermissionFlag
        }
        return intent
    }


    override fun newFileTOSAF(name: String, mimeType: String): Intent {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
        intent.type = mimeType
        intent.putExtra(Intent.EXTRA_TITLE, name)
        return intent
    }

    /**
     * 持久化权限校验,当文件发送变动。这个也是会不会在有这个持久化权限
     */
    override fun requestUriPermission(activity: AppCompatActivity, uri: Uri): Boolean {
        return try {
            activity.contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION and Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}

interface SAFSampleInterface {
    fun openSafActivity(
        params: OpenSafParams
    ): Intent

    fun newFileTOSAF(
        name: String,
        mimeType: String
    ): Intent

    fun requestUriPermission(
        activity: AppCompatActivity,
        uri: Uri
    ): Boolean
}

enum class OpenSafModel {
    FILE,
    FILE_TREE
}

class OpenSafParams private constructor(
    builder: Builder
) {
    internal var model = OpenSafModel.FILE
        private set
    internal var mimeType: String? = null
        private set
    /**
     * 选择多个文件。但是，好像不能默认进入多选模式。需要长按
     */
    internal var isMultiple = false
        private set
    internal var requestPersistablePermission = false
        private set

    init {
        model = builder.model
        mimeType = builder.mimeType
        isMultiple = builder.isMultiple
        requestPersistablePermission = builder.requestPersistablePermission
    }

    class Builder {
        internal var model = OpenSafModel.FILE
            private set
        internal var mimeType: String? = null
            private set
        internal var isMultiple = false
            private set
        internal var requestPersistablePermission = false
            private set

        fun model(model: OpenSafModel): Builder {
            this.model = model
            return this
        }

        fun mimeType(mimeType: String): Builder {
            this.mimeType = mimeType
            return this
        }

        fun multiple(isMultiple: Boolean): Builder {
            this.isMultiple = isMultiple
            return this
        }

        fun requestPersistablePermission(isRequest: Boolean): Builder {
            requestPersistablePermission = isRequest
            return this
        }

        fun build(): OpenSafParams {
            return OpenSafParams(this)
        }

    }

}

interface ResultCallBack {
    fun success(data: List<Uri?>)
    fun fail()
}
