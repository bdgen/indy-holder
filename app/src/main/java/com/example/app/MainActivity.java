package com.example.app;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import kr.co.bdgen.indywrapper.data.payload.OfferPayload;
import kr.co.bdgen.indywrapper.repository.CredentialRepository;
import kr.co.bdgen.indywrapper.repository.IssuingRepository;

public class MainActivity extends AppCompatActivity {

    private OfferPayload offer = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // deeplink Url = indy://holder?secret=blarblar
        // scheme = indy
        // host = holder
        // queryParameter = secret
        Uri data = getIntent().getData();

        if (data == null)
            return;
        String secret = data.getQueryParameter("secret");
        Log.d("[SUCCESS]", secret);

        IssuingRepository repository = new IssuingRepository();
        repository.requestOffer(
                secret,
                offerPayload -> {
                    Log.d("[SUCCESS]", offerPayload.getCredDefJson() + "\n" + offerPayload.getCredDefJson());
                    offer = offerPayload;

                    repository.requestCredential(
                            MyApplication.getWallet(),
                            MyApplication.getDid(this),
                            MyApplication.getMasterSecret(this),
                            secret,
                            offer,
                            (credentialInfo, issuePayload) -> {
                                Log.d(
                                        "[SUCCESS]",
                                        credentialInfo.getCredReqMetadataJson() +
                                                "\n" +
                                                credentialInfo.getCredReqJson() +
                                                "\n" +
                                                credentialInfo.getCredDefJson() +
                                                "\n" +
                                                issuePayload.getCredentialJson()
                                );

                                repository.storeCredential(
                                        MyApplication.getWallet(),
                                        credentialInfo,
                                        issuePayload,
                                        cred -> {
                                            Log.i("[SUCCESS]", "credential = " + cred);
                                            return null;
                                        },
                                        error -> {
                                            Log.e("[ERROR]", error.getMessage(), error);
                                            return null;
                                        }
                                );
                                return null;
                            },
                            error -> {
                                Log.e("[ERROR!]", error.getMessage(), error);
                                return null;
                            }
                    );
                    return null;
                },
                error -> {
                    Log.e("ERROR!", error.getMessage(), error);
                    return null;
                }
        );

    }

    private String getCredentioal() {
        String credential;
        CredentialRepository credentialRepository = new CredentialRepository();
        credential = credentialRepository.getRawCredentials(
                MyApplication.getWallet(),
                "{}"
        );
        return credential;
    }
}