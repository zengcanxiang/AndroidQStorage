package cn.zengcanxiang.androidqstorage

import android.content.Context
import android.graphics.BitmapFactory
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cn.zengcanxiang.androidqstorage.databinding.ItemFileListBinding
import kotlinx.android.synthetic.main.item_file_list.view.*

class FileListAdapter(
    private val context: Context,
    var data: List<FileItem>
) : RecyclerView.Adapter<FileListAdapter.ItemViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val bind = ItemFileListBinding.inflate(LayoutInflater.from(context), parent, false)
        return ItemViewHolder(bind.root)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(context, data[position])
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(context: Context, fileItem: FileItem) {
            val fileName = AndroidQStorageQueryUtils.queryFileName4Uri(
                context,
                fileItem.uri,
                when (fileItem.type) {
                    "image" -> {
                        MediaStore.Images.Media.DISPLAY_NAME
                    }
                    "downloads" -> {
                        MediaStore.Downloads.DISPLAY_NAME
                    }
                    else -> {
                        ""
                    }
                }
            )
            val filePath = AndroidQStorageQueryUtils.queryFilePath4Uri(
                context,
                fileItem.uri,
                when (fileItem.type) {
                    "image" -> {
                        MediaStore.Images.Media.DATA
                    }
                    "downloads" -> {
                        MediaStore.Downloads.DATA
                    }
                    else -> {
                        ""
                    }
                }
            )
            itemView.fileName.text = fileName
            itemView.filePath.text = filePath

            if (fileItem.type == "image") {
//                AndroidQStorageQueryUtils.queryFile4Uri(context, fileItem.uri)?.let {
//                    itemView.thumbnail.setImageBitmap(BitmapFactory.decodeFileDescriptor(it))
//                }

                AndroidQStorageQueryUtils.loadThumbnail4Uri(context,fileItem.uri)?.let {
                    itemView.thumbnail.setImageBitmap(it)
                }
            }
        }
    }

}
