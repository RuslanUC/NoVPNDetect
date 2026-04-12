package me.hoshino.novpndetect

import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.callbacks.XC_LoadPackage
import me.hoshino.novpndetect.hooks.HookConnectivityManager
import me.hoshino.novpndetect.hooks.HookLinkProperties
import me.hoshino.novpndetect.hooks.HookNetworkCapabilities
import me.hoshino.novpndetect.hooks.HookNetworkInfo
import me.hoshino.novpndetect.hooks.HookNetworkInterface
import me.hoshino.novpndetect.hooks.HookNetworkRequestBuilder

const val TAG = "NoVPNDetect"

class XposedInit : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam?) {
        lpparam ?: return
        XposedBridge.log("[NVD] handleLoadPackage: ${lpparam.packageName}")

        val hooks =
            arrayOf(
                HookConnectivityManager(),
                HookNetworkInterface(),
                HookNetworkCapabilities(),
                HookNetworkInfo(),
                HookNetworkRequestBuilder(),
                HookLinkProperties(),
            )

        hooks.forEach {
            it.injectHook()
        }
    }
}