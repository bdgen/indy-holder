package lec.baekseokuni.indyholder;

import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import kr.co.bdgen.indywrapper.data.payload.OfferPayload;
import kr.co.bdgen.indywrapper.repository.IssuingRepository;

public class MainActivity extends AppCompatActivity {
    private OfferPayload offer = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //deeplink 처리
        //deeplink = indy://holder?secret=blarblar
        // scheme = indy
        // host = holder
        // queryParameter = secret
        Uri data = getIntent().getData();
        if (data == null)
            return;
        String secret = data.getQueryParameter("secret");
        Log.d("[SUCCESS]", secret);

        //1. offer 받기
        IssuingRepository repository = new IssuingRepository();
        repository.reuqestOffer(
                secret,
                offerPayload -> {
                    Log.d("[SUCCESS]", offerPayload.getCredDefJson() + "\n" + offerPayload.getCredOfferJson());
                    offer = offerPayload;
                    return null;
                },
                error -> {
                    Log.e("[ERROR!]", error.getMessage(), error);
                    return null;
                }
        );

        /*
        terminal emulator 실행 code
        $
        $ cd $ADB_HOME
        $ adb shell am start
            -W -a android.intent.action.VIEW
            -d "indy://holder?secret=test1"
         */

        //2. request and issue credential
    }
}
