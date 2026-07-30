package com.jackson.blebridge.data.provider

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import com.jackson.blebridge.domain.provider.AppInfoProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AndroidAppInfoProvider @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : AppInfoProvider {
    override val versionName: String by lazy {
        context.packageManager.appVersionName(context.packageName)
    }

    override val packageName: String
        get() = context.packageName
}

private fun PackageManager.appVersionName(packageName: String): String {
    val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0L))
    } else {
        @Suppress("DEPRECATION")
        getPackageInfo(packageName, 0)
    }
    return packageInfo.versionName.orEmpty()
}
