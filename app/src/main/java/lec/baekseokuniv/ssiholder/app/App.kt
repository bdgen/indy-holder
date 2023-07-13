package lec.baekseokuniv.ssiholder.app

import android.app.Application
import android.content.Context
import android.system.Os
import android.util.Log
import android.widget.Toast
import lec.baekseokuniv.ssiholder.config.PoolConfig
import lec.baekseokuniv.ssiholder.config.WalletConfig
import org.hyperledger.indy.sdk.pool.Pool
import org.hyperledger.indy.sdk.wallet.Wallet

class App : Application() {
    private val PREFERENCE_FILE_MY_WALLET = "PREFERENCE_FILE_MY_WALLET"
    private val PREF_MASTER_SECRET = "PREF_MASTER_SECRET_ID"
    private val PREF_KEY_DID = "PREF_KEY_DID"
    private val PREF_KEY_VERKEY = "PREF_KEY_VERKEY"

    lateinit var wallet: Wallet

    override fun onCreate() {
        super.onCreate()

        //지갑 설정 과정
        //1. load indy library
        //dataDir.absolutePath = /data/user/0/lec.baekseokuniv.ssiholder
        Os.setenv("EXTERNAL_STORAGE", dataDir.absolutePath, true)
        System.loadLibrary("indy")

        //2. create and then open pool
        Pool.openPoolLedger(PoolConfig.getPoole(this), "{}").get()
        Log.d(App::class.simpleName, "success open pool ledger")

        //3. create and then open wallet
        WalletConfig.createWallet(this).get()
        wallet = WalletConfig.openWallet().get()
        Log.d(App::class.simpleName, "success open wallet")

        //4. create DID
        if (getDid().isNullOrEmpty() || getVerKey().isNullOrEmpty()) {
            WalletConfig.createDid(wallet)
                .thenAccept { didAndVerKey ->
                    getSharedPreferences(PREFERENCE_FILE_MY_WALLET, Context.MODE_PRIVATE)
                        .also {
                            with(it.edit()) {
                                putString(PREF_KEY_DID, didAndVerKey.first)
                                putString(PREF_KEY_VERKEY, didAndVerKey.second)
                                apply()
                                Log.d(App::class.simpleName, "success create did")
                            }
                        }
                }.exceptionally {
                    it.printStackTrace()
                    return@exceptionally null
                }
        }
        //5. create secret
        if (!getMasterSecret().isNullOrEmpty())
            return
        WalletConfig.createMasterSecret(wallet, getMasterSecret())
            .apply {
                getSharedPreferences(PREFERENCE_FILE_MY_WALLET, Context.MODE_PRIVATE)
                    .also {
                        with(it.edit()) {
                            putString(PREF_MASTER_SECRET, this@apply)
                            apply()
                            Log.d(App::class.simpleName, "success create master secret")
                        }
                    }
            }
    }

    fun getMasterSecret(): String? {
        return getSharedPreferences(PREFERENCE_FILE_MY_WALLET, Context.MODE_PRIVATE)
            .getString(PREF_MASTER_SECRET, null)
    }

    fun getDid(): String? {
        return getSharedPreferences(PREFERENCE_FILE_MY_WALLET, Context.MODE_PRIVATE)
            .getString(PREF_KEY_DID, "")
    }

    fun getVerKey(): String? {
        return getSharedPreferences(PREFERENCE_FILE_MY_WALLET, Context.MODE_PRIVATE)
            .getString(PREF_KEY_VERKEY, "")
    }
}