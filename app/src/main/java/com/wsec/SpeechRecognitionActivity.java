/* ====================================================================
 * Copyright (c) 2014 Alpha Cephei Inc.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY ALPHA CEPHEI INC. ``AS IS'' AND
 * ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL CARNEGIE MELLON UNIVERSITY
 * NOR ITS EMPLOYEES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * ====================================================================
 */

package com.wsec;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;
import edu.cmu.pocketsphinx.SpeechRecognizerSetup;

import static android.widget.Toast.makeText;

public class SpeechRecognitionActivity extends Activity implements
        RecognitionListener {

    int counter = 0;
    /* Named searches allow to quickly reconfigure the decoder */
    private static final String KWS_SEARCH_HELP = "help";
    private static final String KEYPHRASE_HELP = "help me";

    private static final String KWS_STOP_SIREN = "STOP_SIREN";
    private static final String KEY_PHRASE_STOP_SIREN = "stop";


    /* Used to handle permission request */
    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;
    @BindView(R.id.btn_start_stop)
    FloatingActionButton btnStartStop;

    private SpeechRecognizer recognizer;

    MediaPlayer player;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

        player = MediaPlayer.create(this, R.raw.siren);
        player.setLooping(true);
        // Prepare the data for UI
        setContentView(R.layout.activity_speech_recognizer);
        ButterKnife.bind(this);

        int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSIONS_REQUEST_RECORD_AUDIO);
            return;
        }

        btnStartStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(player.isPlaying())
               {
                   pauseSiren();
               }
               else{
                   playSiren();
               }
            }
        });

        new SetupTask(this).execute();
    }

    private static class SetupTask extends AsyncTask<Void, Void, Exception> {
        WeakReference<SpeechRecognitionActivity> activityReference;

        SetupTask(SpeechRecognitionActivity activity) {
            this.activityReference = new WeakReference<>(activity);
        }

        @Override
        protected Exception doInBackground(Void... params) {
            try {
                Assets assets = new Assets(activityReference.get());
                File assetDir = assets.syncAssets();
                activityReference.get().setupRecognizer(assetDir);
            } catch (IOException e) {
                return e;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Exception result) {
            if (result != null) {
                ((TextView) activityReference.get().findViewById(R.id.caption_text))
                        .setText("Failed to init recognizer " + result);
            } else {

                activityReference.get().recognizer.startListening(KWS_SEARCH_HELP);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSIONS_REQUEST_RECORD_AUDIO) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Recognizer initialization is a time-consuming and it involves IO,
                // so we execute it in async task
                new SetupTask(this).execute();
            } else {
                finish();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (recognizer != null) {
            recognizer.cancel();
            recognizer.shutdown();
        }
    }

    /**
     * In partial result we get quick updates about current hypothesis. In
     * keyword spotting mode we can react here, in other modes we need to wait
     * for final result in onResult.
     */
    @Override
    public void onPartialResult(Hypothesis hypothesis) {
        if (hypothesis == null)
            return;

        String text = hypothesis.getHypstr();

        Log.v("SPX", text);

    }

    /**
     * This callback is called when we stop the recognizer.
     */
    @Override
    public void onResult(Hypothesis hypothesis) {
        if (hypothesis != null) {
            String text = hypothesis.getHypstr().trim();
            makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
            if (text.contains("help") && text.contains("me")) {
                playSiren();
                recognizer.startListening(KWS_STOP_SIREN);
            }
            if (text.contains("stop")) {
                pauseSiren();
                recognizer.startListening(KWS_SEARCH_HELP);
            }
        }

        if (player.isPlaying()) {
            recognizer.startListening(KWS_STOP_SIREN);
        } else {
            recognizer.startListening(KWS_SEARCH_HELP);
        }
    }

    @Override
    public void onBeginningOfSpeech() {
    }

    @Override
    public void onEndOfSpeech() {
        recognizer.stop();
    }


    private void setupRecognizer(File assetsDir) throws IOException {

        recognizer = SpeechRecognizerSetup.defaultSetup()
                .setAcousticModel(new File(assetsDir, "en-us-ptm"))
                .setDictionary(new File(assetsDir, "cmudict-en-us.dict"))
                .setRawLogDir(assetsDir) // To disable logging of raw audio comment out this call (takes a lot of space on the device)
                .getRecognizer();

        recognizer.addListener(this);
        recognizer.addKeyphraseSearch(KWS_SEARCH_HELP, KEYPHRASE_HELP);
        recognizer.addKeyphraseSearch(KWS_STOP_SIREN, KEY_PHRASE_STOP_SIREN);

    }

    @Override
    public void onError(Exception error) {
        ((TextView) findViewById(R.id.caption_text)).setText(error.getMessage());
    }

    @Override
    public void onTimeout() {
//        recognizer.startListening(KWS_SEARCH);
    }

    private void playSiren() {
        btnStartStop.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.holo_red_dark)));
        btnStartStop.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_black_24dp));
        player.start();
    }

    private void pauseSiren() {
        btnStartStop.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
        btnStartStop.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_arrow_24dp));
        player.pause();
    }
}