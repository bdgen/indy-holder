package com.example.app;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import org.hyperledger.indy.sdk.pool.Pool;
import org.hyperledger.indy.sdk.wallet.Wallet;

import kotlin.Pair;
import kr.co.bdgen.indywrapper.IndyWrapper;
import kr.co.bdgen.indywrapper.config.PoolConfig;
import kr.co.bdgen.indywrapper.config.WalletConfig;

public class MyApplication extends Application {
    public static final String WALLET_PREFERENCE = "WALLET_PREFERENCES";
    public static final String PREF_KEY_DID = "PREF_KEY_DID";
    public static final String PREF_KEY_VER_KEY = "PREF_KEY_VER_KEY";
    public static final String PREF_KEY_MASTER_SECRET = "PREF_KEY_MASTER_SECRET";
    @Override
    public void onCreate() {
        super.onCreate();
        IndyWrapper.init(this);
        String poolName = PoolConfig.getPoole(this);
        try {
            Pool.openPoolLedger(poolName, "{}").get();

            WalletConfig.createWallet(this).get();
            Wallet wallet = WalletConfig.openWallet().get();

            Pair<String, String> didAndVerKey = WalletConfig.createDid(wallet).get();
            String masterSecret = WalletConfig.createMasterSecret(wallet, null);

            SharedPreferences prefs = getSharedPreferences(WALLET_PREFERENCE, MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(PREF_KEY_DID, didAndVerKey.getFirst());
            editor.putString(PREF_KEY_VER_KEY, didAndVerKey.getSecond());
            editor.putString(PREF_KEY_MASTER_SECRET, masterSecret);
            editor.apply();

            Toast.makeText(this, "wallet success!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("[INDY ERROR!]", e.getMessage(), e);
        }


    }


}
