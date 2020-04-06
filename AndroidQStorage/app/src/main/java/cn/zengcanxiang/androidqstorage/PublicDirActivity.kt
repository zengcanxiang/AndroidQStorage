package cn.zengcanxiang.androidqstorage

import android.Manifest
import android.Manifest.permission
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import cn.zengcanxiang.androidqstorage.databinding.PublicDirBinding

class PublicDirActivity : AppCompatActivity() {

    var adapter = FileListAdapter(this, mutableListOf())
    lateinit var bind: PublicDirBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = PublicDirBinding.inflate(layoutInflater)
        setContentView(bind.root)

        bind.goToPermissionActivity.setOnClickListener {
            goIntentSetting()
        }

        bind.dataList.let {
            it.layoutManager = LinearLayoutManager(this)
            it.adapter = adapter
        }

        bind.getPublic.setOnClickListener {
            val imageResult = AndroidQStorageQueryUtils.queryAllMediaData(
                this,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                MediaStore.Images.Media._ID
            ).map {
                FileItem(it, "image")
            }

            val downloadResult = AndroidQStorageQueryUtils.queryAllMediaData(
                this,
                MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                MediaStore.Downloads._ID
            ).map {
                FileItem(it, "downloads")
            }

            val data = mutableListOf<FileItem>()
            data.addAll(imageResult)
            data.addAll(downloadResult)

            adapter.data = data
            adapter.notifyDataSetChanged()
        }

    }

    override fun onResume() {
        super.onResume()
        val isHasImagePermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        bind.hasPermission.text = "hasPermission:$isHasImagePermission"

    }

    private fun goIntentSetting() {
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
        }.let {
            startActivity(it)
        }

    }

}
