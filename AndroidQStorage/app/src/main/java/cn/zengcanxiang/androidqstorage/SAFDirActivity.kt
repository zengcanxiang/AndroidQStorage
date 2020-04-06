package cn.zengcanxiang.androidqstorage

import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.OpenableColumns
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cn.zengcanxiang.androidqstorage.SAF.OpenSafModel
import cn.zengcanxiang.androidqstorage.SAF.OpenSafParams
import cn.zengcanxiang.androidqstorage.SAF.ResultCallBack
import cn.zengcanxiang.androidqstorage.SAF.SAFHelper
import cn.zengcanxiang.androidqstorage.databinding.SafDirBinding

class SAFDirActivity : AppCompatActivity() {

    private lateinit var bind: SafDirBinding

    private var saveUri: List<Uri?>? = null
    private val resultCallBack = object :
        ResultCallBack {

        override fun success(data: List<Uri?>) {
            saveUri = data
            bind.saveUriShow.text = "当前获取的uri是:$data"
            if (data.size == 1) {
                val temp = data[0] ?: return
                bind.saveUriShow.text = "当前获取的uri是:$data ,名称为：${
                AndroidQStorageQueryUtils.queryFileName4Uri(
                    this@SAFDirActivity, temp, OpenableColumns.DISPLAY_NAME
                )
                }"
            }
        }

        override fun fail() {
            Toast.makeText(this@SAFDirActivity, "创建失败", Toast.LENGTH_SHORT).show()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = SafDirBinding.inflate(layoutInflater)
        setContentView(bind.root)

        bind.openSAFFile.setOnClickListener {
            val mimeTypeString = bind.mimeType.text ?: null
            SAFHelper.openSafActivity(
                this,
                OpenSafParams.Builder().apply {
                    model(OpenSafModel.FILE)
                    multiple(bind.selectMultiple.isChecked)
                    mimeTypeString?.toString()?.let {
                        mimeType(it)
                    }
                }.build(),
                resultCallBack = resultCallBack
            )
        }
        bind.openSAFFileTree.setOnClickListener {
            SAFHelper.openSafActivity(
                this,
                OpenSafParams.Builder().apply {
                    model(OpenSafModel.FILE_TREE)
                    requestPersistablePermission(true)
                }.build(),
                resultCallBack = resultCallBack
            )
        }
        bind.createFileToSAF.setOnClickListener {
            SAFHelper.newFileToSAF(
                this,
                "test.txt",
                "text/plaint",
                resultCallBack
            )
        }

        bind.requestUriPermission.setOnClickListener {
            val inputText = bind.requestUri.text

            val uri = if (TextUtils.isEmpty(inputText)) {
                saveUri?.firstOrNull()
            } else {
                Uri.parse(inputText.toString())
            } ?: return@setOnClickListener
            SAFHelper.requestUriPersistablePermission(
                this, uri
            ).let {
                bind.requestPermissionResult.text = "校验的uri:$uri 结果是 $it"
            }
        }

        bind.createFileToSAF.setOnClickListener {
            val uri = saveUri?.firstOrNull() ?: return@setOnClickListener kotlin.run {
                Toast.makeText(this@SAFDirActivity, "当前没有选择uri", Toast.LENGTH_SHORT).show()
            }
            if (!DocumentsContract.isTreeUri(uri)) {
                Toast.makeText(this@SAFDirActivity, "当前选择的不是文件夹uri", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            SAFOperateActivity.newStart(this, uri)
        }
    }

}
