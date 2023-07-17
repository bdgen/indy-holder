package kr.co.bdgen.indywrapper

import android.content.Context
import android.content.SharedPreferences
import android.system.Os
import kr.co.bdgen.indywrapper.config.PoolConfig
import kr.co.bdgen.indywrapper.config.WalletConfig
import org.hyperledger.indy.sdk.pool.Pool
import org.hyperledger.indy.sdk.wallet.Wallet

object IndyWrapper {
    @JvmStatic
    fun init(context: Context) {
        //지갑 설정 과정
        //1. load indy library
        Os.setenv("EXTERNAL_STORAGE", context.dataDir.absolutePath, true)
        System.loadLibrary("indy")
    }
}