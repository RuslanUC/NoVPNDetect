package me.hoshino.novpndetect.hooks

import android.app.PendingIntent
import android.net.ConnectivityManager
import android.net.LinkProperties
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Handler
import android.util.Log
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import me.hoshino.novpndetect.TAG
import me.hoshino.novpndetect.XHook
import java.net.NetworkInterface
import kotlin.collections.iterator

class HookLinkProperties : XHook {

    override val targetKlass: String
        get() = "android.net.LinkProperties"

    override fun injectHook() {
        hookGetInterfaceName()
    }

    private fun hookGetInterfaceName() {
        XposedHelpers.findAndHookMethod(LinkProperties::class.java, "getInterfaceName", object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                Log.i(TAG, "$targetKlass.getInterfaceName () -> ${param.result}")
                if (param.result != null && param.result is String && (param.result as String).startsWith("tun")) {
                    val interfaces = NetworkInterface.getNetworkInterfaces()
                    if(interfaces != null) {
                        for (iface in interfaces) {
                            if (!iface.isUp || iface.isLoopback)
                                continue

                            if (
                                iface.name.contains("wlan")
                                || iface.name.contains("rmnet_data")
                                || iface.name.contains("eth")
                            ) {
                                param.result = iface.name
                                return
                            }
                        }
                    }

                    param.result = "wlan0"
                }
            }
        })
    }
}