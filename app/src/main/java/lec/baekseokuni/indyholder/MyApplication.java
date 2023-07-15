package lec.baekseokuni.indyholder;

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
    public static final String WALLET_PREFERENCES = "WALLET_PREFERENCES";
    public static final String PREF_KEY_DID = "PREF_KEY_DID";
    public static final String PREF_KEY_VER_KEY = "PREF_KEY_VER_KEY";
    public static final String PREF_KEY_MASTER_SECRET = "PREF_KEY_MASTER_SECRET";


    @Override
    public void onCreate() {
        super.onCreate();

        //1. indy library 초기화
        IndyWrapper.init(this);

        //2. ledger 사용을 위한 pool config 생성
        String poolName = PoolConfig.getPoole(this);
        try {
            //3. ledger 환경 사용을 위해 pool ledger 열기
            Pool.openPoolLedger(poolName, "{}").get();

            //4. wallet 생성
            WalletConfig.createWallet(this).get();

            //5. wallet 열기
            Wallet wallet = WalletConfig.openWallet().get();

            //6. did, verKey 생성
            Pair<String, String> didAndVerKey = WalletConfig.createDid(wallet).get();

            //7. master secret 생성
            String masterSecret = WalletConfig.createMasterSecret(wallet, null);

            //8. 생성한 did, verKey, masterSecretId 저장
            SharedPreferences prefs = getSharedPreferences(WALLET_PREFERENCES, MODE_PRIVATE);
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
