package com.example.app;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import org.hyperledger.indy.sdk.IndyException;
import org.hyperledger.indy.sdk.pool.Pool;
import org.hyperledger.indy.sdk.wallet.Wallet;

import kotlin.Pair;
import kr.co.bdgen.indywrapper.IndyWrapper;
import kr.co.bdgen.indywrapper.config.PoolConfig;
import kr.co.bdgen.indywrapper.config.WalletConfig;
import kr.co.bdgen.indywrapper.repository.IssuingRepository;

public class MyApplication extends Application {
    public static final String WALLET_PREFERENCE = "WALLET_PREFERENCE";
    public static final String PREF_KEY_DID = "PREF_KEY_DID";
    public static final String PREF_KEY_VER_KEY = "PREF_KEY_DID";
    public static final String PREF_KET_MASTER_SECRET = "PREF_KEY_DID";

    private static Wallet wallet;

    public static Wallet getWallet(){
        return wallet;
    }
    public static String getDid(Context context){
//        String did = null;
        SharedPreferences pref = context.getSharedPreferences(WALLET_PREFERENCE, MODE_PRIVATE);
        return pref.getString(PREF_KEY_DID, "");
    }
    public static String getMasterSecret(Context context){
//        String masterSecret = null;
        SharedPreferences pref = context.getSharedPreferences(WALLET_PREFERENCE, MODE_PRIVATE);
        String masterSecret = pref.getString(PREF_KET_MASTER_SECRET, "");
        return masterSecret;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        //1. indy library 초기화
        IndyWrapper.init(this);

        //2 ledgger 사용을 위한 pool config 생성
        String poolName = PoolConfig.getPoole(this);
        try {
            //3 ledger 환경 사용을 위해 pool ledger 열기
            Pool.openPoolLedger(poolName, "{}").get();

            //4 wallet 생성
            WalletConfig.createWallet(this).get();

            //5 wallet 열기
            wallet = WalletConfig.openWallet().get();

            //6 did, verKey 생성
            Pair<String, String> didAndVerKey = WalletConfig.createDid(wallet).get();

            //7 master secret 생성
            String masterSecret = WalletConfig.createMasterSecret(wallet, null);

            //8 생성한 did, verKey, masterSecretID 저장
            SharedPreferences prefs = getSharedPreferences(WALLET_PREFERENCE, MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(PREF_KEY_DID, didAndVerKey.getFirst());
            editor.putString(PREF_KEY_VER_KEY, didAndVerKey.getSecond());
            editor.putString(PREF_KET_MASTER_SECRET, masterSecret);
            editor.apply();

            Toast.makeText(this, "wallet success", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("[INDY ERROR", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
