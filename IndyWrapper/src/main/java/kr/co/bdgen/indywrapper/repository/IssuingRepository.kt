package kr.co.bdgen.indywrapper.repository

import kr.co.bdgen.indywrapper.AppExecutors
import kr.co.bdgen.indywrapper.api.IssuerApi
import kr.co.bdgen.indywrapper.config.RetrofitConfig
import kr.co.bdgen.indywrapper.data.argument.CredentialInfo
import kr.co.bdgen.indywrapper.data.argument.IssueArg
import kr.co.bdgen.indywrapper.data.payload.IssuePayload
import kr.co.bdgen.indywrapper.data.payload.OfferPayload
import org.hyperledger.indy.sdk.anoncreds.Anoncreds.proverCreateCredentialReq
import org.hyperledger.indy.sdk.anoncreds.Anoncreds.proverStoreCredential
import org.hyperledger.indy.sdk.wallet.Wallet
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class IssuingRepository() {
    private val issuerApi: IssuerApi = RetrofitConfig.createApi(
        RetrofitConfig.issuerBaseUrl, IssuerApi::class.java
    )

    /**
     * 1. Credential을 생성하기 위해 issuer에 offer
     */
    fun requestOffer(
        secret: String,
        onSuccess: (OfferPayload) -> Void,
        onFail: (Throwable) -> Void,
    ) {
        issuerApi
            .postOffer(secret)
            .enqueue(object : Callback<OfferPayload> {
                override fun onResponse(
                    call: Call<OfferPayload>,
                    response: Response<OfferPayload>
                ) {
                    val offerPayload = response.body()
                    if (offerPayload == null) {
                        onFail(Exception("response error"))
                        return
                    }
                    onSuccess(offerPayload)
                }

                override fun onFailure(call: Call<OfferPayload>, t: Throwable) {
                    t.printStackTrace()
                    onFail(t)
                }
            })
    }

    /**
     * 2. credential을 생성하기 위한 metadata와 요청 정보를 담은 객체를 생성하는 메소드
     */
    fun requestCredential(
        wallet: Wallet,
        myDid: String,
        myMasterSecret: String,
        secret: String,
        offerPayload: OfferPayload,
        onSuccess: (CredentialInfo, IssuePayload) -> Void,
        onFail: (Throwable) -> Void,
    ) {
        proverCreateCredentialReq(
            wallet,
            myDid,
            offerPayload.credOfferJson,
            offerPayload.credDefJson,
            myMasterSecret,
        )
            .thenApply {
                val credentialInfo = CredentialInfo(
                    offerPayload.credDefJson,
                    it.credentialRequestMetadataJson,
                    it.credentialRequestJson
                )
                val response = try {
                    issuerApi
                        .postIssue(
                            IssueArg(
                                secret,
                                offerPayload.credOfferJson,
                                it.credentialRequestJson
                            )
                        )
                        .execute()
                } catch (e: Throwable) {
                    e.printStackTrace()
                    onFail(e)
                    return@thenApply
                }
                val payload = response.body()
                if (response.isSuccessful && payload != null) {
                    onSuccess(credentialInfo, payload)
                    return@thenApply
                }
                onFail(Exception("fail"))
            }
    }

    /**
     * 3. 생성한 Credential을 저장함
     */
    fun storeCredential(
        wallet: Wallet,
        credentialInfo: CredentialInfo,
        issuePayload: IssuePayload,
        onSuccess: (credentialId: String) -> Void,
        onFail: (Throwable) -> Void
    ) {
        proverStoreCredential(
            wallet,
            null,
            credentialInfo.credReqMetadataJson,
            issuePayload.credentialJson,
            credentialInfo.credDefJson,
            null
        )
            .thenAcceptAsync(
                { onSuccess(it) },
                AppExecutors.getInstance().mainThread()
            )
            .exceptionally {
                onFail(it)
            }
    }
}