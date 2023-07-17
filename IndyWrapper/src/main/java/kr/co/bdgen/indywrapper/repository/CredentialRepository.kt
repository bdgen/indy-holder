package kr.co.bdgen.indywrapper.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kr.co.bdgen.indywrapper.data.Credential
import kr.co.bdgen.indywrapper.data.CredentialFilter
import org.hyperledger.indy.sdk.anoncreds.Anoncreds
import org.hyperledger.indy.sdk.wallet.Wallet


class CredentialRepository() {
    fun getCredentialList(wallet: Wallet, filter: CredentialFilter?): List<Credential> {
        return Gson().fromJson(
            getRawCredentials(
                wallet,
                if (filter == null) "{}" else Gson().toJson(filter).toString()
            ),
            object : TypeToken<List<Credential>>() {}.type
        )
    }

    fun getRawCredentials(wallet: Wallet, filter: String): String {
        return Anoncreds.proverGetCredentials(wallet, filter).get()
    }
}