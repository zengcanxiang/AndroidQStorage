package cn.zengcanxiang.androidqstorage

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.OpenableColumns
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.documentfile.provider.DocumentFile
import cn.zengcanxiang.androidqstorage.SAF.OpenSafModel
import cn.zengcanxiang.androidqstorage.SAF.OpenSafParams
import cn.zengcanxiang.androidqstorage.SAF.ResultCallBack
import cn.zengcanxiang.androidqstorage.SAF.SAFHelper
import cn.zengcanxiang.androidqstorage.databinding.SafOperateBinding
import java.net.URLDecoder

class SAFOperateActivity : AppCompatActivity() {

    private lateinit var bind: SafOperateBinding

    private var parentUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = SafOperateBinding.inflate(layoutInflater)
        setContentView(bind.root)

        parentUri = intent.getParcelableExtra<Uri>(uri_key)

        val temp = parentUri ?: return
        bind.parentUri.text =
            "当前的父文件夹uri为：$parentUri \n ,文件夹名称为：${AndroidQStorageQueryUtils.queryFileName4Uri(
                this, temp, OpenableColumns.DISPLAY_NAME
            )}"

        bind.parentUriPermission.text = "检测对uri的操作权限结果:${SAFHelper.requestUriPersistablePermission(
            this, temp
        )}"

        bind.createFileTree.setOnClickListener {
            var name = bind.createFIleTreeName.text?.toString()
            if (TextUtils.isEmpty(name)) {
                name = "sample"
            }
            name ?: return@setOnClickListener

            DocumentFile.fromTreeUri(this, temp)?.createDirectory(name)
        }


        bind.queryFileForParent.setOnClickListener {
            val name = bind.queryFileName.text?.toString() ?: return@setOnClickListener
            if (TextUtils.isEmpty(name)) {
                Toast.makeText(this, "名称不能为空", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val result =
                DocumentFile.fromTreeUri(this, temp)?.findFile(name)
                    ?: return@setOnClickListener kotlin.run {
                        bind.queryFileResult.text = "寻找的结果：无"
                    }
            bind.queryFileResult.text = "寻找的结果：uri为：${result.uri} 是否是目录：${result.isDirectory}"
        }

        bind.createFileForImage.setOnClickListener {
            val parentFile = DocumentFile.fromTreeUri(this, temp) ?: return@setOnClickListener
            parentFile.createFile(
                "image/jpeg", "test.jpg"
            )?.let {
                it.uri
                AndroidQStorageSaveUtils.saveBitmap2Uir(
                    this,
                    it.uri,
                    BitmapFactory.decodeResource(
                        resources,
                        R.drawable.test
                    )
                )
            }
        }

        bind.createFileForTxt.setOnClickListener {
            val parentFile = DocumentFile.fromTreeUri(this, temp) ?: return@setOnClickListener
            parentFile.createFile(
                "text/plain", "test.txt"
            )?.let {
                it.uri
                AndroidQStorageSaveUtils.saveFile2Uri(
                    this,
                    it.uri,
                    resources.assets.open("test.txt")
                )
            }
        }

        bind.createFileForVideo.setOnClickListener {
            val parentFile = DocumentFile.fromTreeUri(this, temp) ?: return@setOnClickListener
            parentFile.createFile(
                "video/mp4", "test.mp4"
            )?.let {
                it.uri
                AndroidQStorageSaveUtils.saveVideo2Uri(
                    this,
                    it.uri,
                    resources.assets.open("test.mp4")
                )
            }
        }

        bind.createFileForAudio.setOnClickListener {
            val parentFile = DocumentFile.fromTreeUri(this, temp) ?: return@setOnClickListener
            parentFile.createFile(
                "audio/mp3", "test.mp3"
            )?.let {
                it.uri
                AndroidQStorageSaveUtils.saveAudio2Uri(
                    this,
                    it.uri,
                    resources.assets.open("test.mp3")
                )
            }
        }

        bind.createFileForApk.setOnClickListener {
            val parentFile = DocumentFile.fromTreeUri(this, temp) ?: return@setOnClickListener
            parentFile.createFile(
                "application/vnd.android.package-archive", "test.apk"
            )?.let {
                it.uri
                AndroidQStorageSaveUtils.saveAudio2Uri(
                    this,
                    it.uri,
                    resources.assets.open("test.apk")
                )
            }
        }

        bind.selectFileDelete.setOnClickListener {
            SAFHelper.openSafActivity(
                this, OpenSafParams.Builder().apply {
                    model(OpenSafModel.FILE)
                    multiple(true)
                }.build(), object : ResultCallBack {
                    override fun success(data: List<Uri?>) {
                        data.forEach {
                            it?.let {
                                if (DocumentFile.isDocumentUri(this@SAFOperateActivity, it)) {
                                    DocumentFile.fromSingleUri(this@SAFOperateActivity, it)
                                        ?.delete()
                                }
                            }
                        }
                    }

                    override fun fail() {

                    }
                }
            )
        }
    }

    companion object {

        private const val uri_key = ""

        fun newStart(
            activity: AppCompatActivity,
            uri: Uri
        ) {
            val intent = Intent(activity, SAFOperateActivity::class.java)
            intent.putExtra(uri_key, uri)
            activity.startActivity(intent)
        }
    }

}
