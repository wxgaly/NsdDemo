package nova.android.nsdclient

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val TAG = "NSD_CLIENT"
    private val mServiceName = "NSD_Test_Program"
    private val serviceType = "_http._tcp"

    private var discoveryListener: NsdManager.DiscoveryListener? = null
    private var resolveListener: NsdManager.ResolveListener? = null
    private lateinit var nsdManager: NsdManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        initData()
    }

    private fun initData() {
        nsdManager = applicationContext.getSystemService(Context.NSD_SERVICE) as NsdManager
        initResolveListener()
        discoverService()
    }

    private fun initResolveListener() {
        resolveListener = object : NsdManager.ResolveListener {
            override fun onResolveFailed(serviceInfo: NsdServiceInfo?, errorCode: Int) {
                Log.d(TAG, "onResolveFailed ---- errorCode: $errorCode")
            }

            override fun onServiceResolved(serviceInfo: NsdServiceInfo?) {
                Log.d(TAG, "onServiceResolved ---- serviceInfo: ${serviceInfo.toString()}")
                unregisterNDSService()
            }
        }
    }

    private fun discoverService() {
        discoveryListener = object : NsdManager.DiscoveryListener {
            override fun onServiceFound(serviceInfo: NsdServiceInfo?) {
                // 发现网络服务时就会触发该事件
                // 可以通过switch或if获取那些你真正关心的服务
                Log.d(TAG, "onServiceFound ---- serviceInfo: ${serviceInfo.toString()}")
                serviceInfo?.apply {
                    if (serviceName.contains(mServiceName)) {
                        nsdManager.resolveService(serviceInfo, resolveListener)
                    }
                }
            }

            override fun onStopDiscoveryFailed(serviceType: String?, errorCode: Int) {
                Log.d(TAG, "onStopDiscoveryFailed ---- errorCode: $errorCode")
            }

            override fun onStartDiscoveryFailed(serviceType: String?, errorCode: Int) {
                Log.d(TAG, "onStartDiscoveryFailed ---- errorCode: $errorCode")
            }

            override fun onDiscoveryStarted(serviceType: String?) {
                Log.d(TAG, "onDiscoveryStarted ----  serviceType: $serviceType")
            }

            override fun onDiscoveryStopped(serviceType: String?) {
                Log.d(TAG, "onDiscoveryStopped ---- serviceType: $serviceType")
            }

            override fun onServiceLost(serviceInfo: NsdServiceInfo?) {
                Log.d(TAG, "onServiceLost ---- serviceInfo: ${serviceInfo.toString()}")
            }

        }

        nsdManager.discoverServices(serviceType, NsdManager.PROTOCOL_DNS_SD, discoveryListener)
    }

    private fun unregisterNDSService() {
        try {
            nsdManager.stopServiceDiscovery(discoveryListener) // 关闭网络发现
        } catch (e: Exception) {
            Log.e(TAG, e.message)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterNDSService()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
