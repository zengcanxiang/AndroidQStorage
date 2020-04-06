package cn.zengcanxiang.androidqstorage

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cn.zengcanxiang.androidqstorage.databinding.MainBinding
import java.io.File
import java.io.InputStream


@SuppressLint("SetTextI18n")
class MainActivity : AppCompatActivity() {
    var saveUri: Uri? = null
    lateinit var bind: MainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = MainBinding.inflate(layoutInflater)
        setContentView(bind.root)

        bind.scopedStoragePermission.text =
            "scoped-Storage权限：${AndroidQStorageUtils.externalMode()}"

        val spinnerItems = arrayOf<String>(
            Environment.DIRECTORY_DOWNLOADS,
            Environment.DIRECTORY_DOCUMENTS,
            Environment.DIRECTORY_PICTURES,
            Environment.DIRECTORY_DCIM,
            Environment.DIRECTORY_MOVIES,
            Environment.DIRECTORY_MUSIC
        )
        val spinnerAdapter = ArrayAdapter<String>(
            this,
            R.layout.public_spinner_item,
            R.id.spinnerItemText,
            spinnerItems
        )
        bind.mainInclude.publicSpinner.let {
            it.adapter = spinnerAdapter
            it.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    bind.mainInclude.saveFile.text = when (position) {
                        2, 3 -> {
                            "保存的文件是：appIcon"
                        }
                        0, 1 -> {
                            "保存的文件是：新建的txt文档"
                        }
                        4 -> {
                            "保存的文件是：mp4文件"
                        }
                        5 -> {
                            "保存的文件是：audio文件"
                        }
                        else -> {
                            "选择项没有处理"
                        }
                    }
                }
            }
        }

        bind.mainInclude.saveFileToPrivate.setOnClickListener {
            val cache = cacheDir
            val newFile = File(cache, "sample.txt")
            if (newFile.exists()) {
                Toast.makeText(this, newFile.absolutePath, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            newFile.createNewFile()
            if (newFile.exists()) {
                Toast.makeText(this, newFile.absolutePath, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
        }

        bind.mainInclude.saveFileToPublic.setOnClickListener {
            val selectMenu = bind.mainInclude.publicSpinner.selectedItem.toString()
            saveUri = when (selectMenu) {
                Environment.DIRECTORY_PICTURES, Environment.DIRECTORY_DCIM -> {
                    saveBitmap(selectMenu)
                }
                Environment.DIRECTORY_DOWNLOADS, Environment.DIRECTORY_DOCUMENTS -> {
                    saveTxt(selectMenu, resources.assets.open("test.txt"))
                }
                Environment.DIRECTORY_MOVIES -> {
                    saveVideo(selectMenu, resources.assets.open("test.mp4"))
                }
                Environment.DIRECTORY_MUSIC -> {
                    saveAudio(selectMenu, resources.assets.open("test.mp3"))
                }
                else -> {
                    null
                }
            }
            saveUri ?: return@setOnClickListener kotlin.run {
                Toast.makeText(this, "保存失败", Toast.LENGTH_SHORT).show()
            }

            bind.mainInclude.saveUriShow.text = "当前保存的uri是:$saveUri"
        }

        bind.mainInclude.validUri.setOnClickListener {
            val uri = saveUri ?: return@setOnClickListener kotlin.run {
                Toast.makeText(this, "当前saveUri为空", Toast.LENGTH_SHORT).show()
            }
            Toast.makeText(
                this,
                "文件保存的uri:$uri,文件有效性${AndroidQStorageUtils.fileUriIsExists(uri, this)}",
                Toast.LENGTH_SHORT
            ).show()
        }

        bind.mainInclude.showPublic.setOnClickListener {
            startActivity(
                Intent(this, PublicDirActivity::class.java)
            )
        }

        bind.mainInclude.showCustomSAF.setOnClickListener {
            startActivity(
                Intent(this, SAFDirActivity::class.java)
            )
        }

    }

    private fun saveBitmap(selectMenu: String): Uri? {
        return AndroidQStorageSaveUtils.saveBitmap2Public(
            this,
            BitmapFactory.decodeResource(resources, R.drawable.test),
            selectMenu
        )
    }

    private fun saveTxt(
        selectMenu: String,
        source: InputStream
    ): Uri? {
        return AndroidQStorageSaveUtils.saveFile2Public(
            this,
            source,
            selectMenu,
            "txt"
        )
    }

    private fun saveVideo(
        selectMenu: String,
        source: InputStream
    ): Uri? {
        return AndroidQStorageSaveUtils.saveVideo2Public(
            this,
            source,
            selectMenu,
            "mp4"
        )
    }

    private fun saveAudio(
        selectMenu: String,
        source: InputStream
    ): Uri? {
        return AndroidQStorageSaveUtils.saveAudio2Public(
            this,
            source,
            selectMenu,
            "mp3"
        )
    }
}
