package cn.zengcanxiang.androidqstorage.SAF

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity


object SAFHelper : SafInterface {

    private const val FRAGMENT_TAG = "saf_fragment"

    private fun getSafFragment(activity: AppCompatActivity): SAFFragment? {
        return findFragment4Tag(
            activity
        ) ?: kotlin.run {
            val temp = SAFFragment()
            val fragmentManager = activity.supportFragmentManager
            fragmentManager
                .beginTransaction()
                .add(temp,
                    FRAGMENT_TAG
                )
                .commitAllowingStateLoss()
            fragmentManager.executePendingTransactions()
            temp
        }
    }

    private fun findFragment4Tag(activity: AppCompatActivity): SAFFragment? {
        return activity
            .supportFragmentManager
            .findFragmentByTag(FRAGMENT_TAG) as? SAFFragment
    }

    override fun openSafActivity(
        activity: AppCompatActivity,
        params: OpenSafParams,
        resultCallBack: ResultCallBack?
    ) {
        getSafFragment(activity)?.openSafActivity(
            activity,
            params,
            resultCallBack
        )
    }

    override fun newFileToSAF(
        activity: AppCompatActivity,
        name: String,
        type: String,
        resultCallBack: ResultCallBack?
    ) {
        getSafFragment(activity)
            ?.newFileToSAF(activity, name, type, resultCallBack)
    }

    /**
     * 持久化权限校验
     */
    override fun requestUriPersistablePermission(
        activity: AppCompatActivity,
        uri: Uri
    ): Boolean {
        return getSafFragment(
            activity
        )?.requestUriPersistablePermission(
            activity,
            uri
        ) ?: false
    }

}

internal interface SafInterface {
    fun openSafActivity(
        activity: AppCompatActivity,
        params: OpenSafParams,
        resultCallBack: ResultCallBack? = null
    )

    fun newFileToSAF(
        activity: AppCompatActivity,
        name: String,
        type: String,
        resultCallBack: ResultCallBack? = null
    )

    fun requestUriPersistablePermission(
        activity: AppCompatActivity,
        uri: Uri
    ): Boolean
}
