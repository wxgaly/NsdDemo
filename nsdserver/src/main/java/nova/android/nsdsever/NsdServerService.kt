package nova.android.nsdsever

import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.os.Binder
import android.os.IBinder
import android.util.Log
import java.net.ServerSocket

/**
 *  nova.android.nsdsever.
 *
 * @author Created by WXG on 2018/6/22 022 14:16.
 * @version V1.0
 */
class NsdServerService : Service() {

    private val TAG = "NSD_SERVER"
    private val serviceName = "NSD_Test_Program"
    private val serviceType = "_http._tcp"

    private var registrationListener: NsdManager.RegistrationListener? = null

    override fun onCreate() {
        super.onCreate()
        registerNDSService()
    }

    private fun registerNDSService() {

        // 注意：注册网络服务时不要对端口进行硬编码，通过如下这种方式为你的网络服务获取
        // 一个可用的端口号.
        var port = 0
        try {
            val sock = ServerSocket(0)
            port = sock.localPort
            sock.close()
        } catch (e: Exception) {
            Log.e(TAG, e.message)
        }


        // 注册网络服务的名称、类型、端口
        val nsdServiceInfo = NsdServiceInfo()
        nsdServiceInfo.serviceName = serviceName
        nsdServiceInfo.serviceType = serviceType
        nsdServiceInfo.port = port
        Log.d(TAG, "port: $port")

        registrationListener = object : NsdManager.RegistrationListener {

            override fun onUnregistrationFailed(serviceInfo: NsdServiceInfo?, errorCode: Int) {
                Log.d(TAG, "onUnregistrationFailed ---- errorCode: $errorCode serviceInfo: ${serviceInfo.toString()}")
            }

            override fun onServiceUnregistered(serviceInfo: NsdServiceInfo?) {
                Log.d(TAG, "onServiceUnregistered ---- serviceInfo: ${serviceInfo.toString()}")
            }

            override fun onRegistrationFailed(serviceInfo: NsdServiceInfo?, errorCode: Int) {
                Log.d(TAG, "onRegistrationFailed ---- errorCode: $errorCode serviceInfo: ${serviceInfo.toString()}")
            }

            override fun onServiceRegistered(serviceInfo: NsdServiceInfo?) {
                Log.d(TAG, "onServiceRegistered ---- serviceInfo: ${serviceInfo.toString()}")
            }

        }

        // 获取系统网络服务管理器，准备之后进行注册
        val nsdManager = applicationContext.getSystemService(Context.NSD_SERVICE) as NsdManager
        nsdManager.registerService(nsdServiceInfo, NsdManager.PROTOCOL_DNS_SD, registrationListener)

    }

    private fun unregisterNDSService() {
        try {
            val nsdManager = applicationContext.getSystemService(Context.NSD_SERVICE) as NsdManager
            nsdManager.unregisterService(registrationListener)    // 注销网络服务
        } catch (e: Exception) {
            Log.e(TAG, e.message)
        }

    }

    override fun onBind(intent: Intent?): IBinder = Binder()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        super.onDestroy()
        unregisterNDSService()
    }

}