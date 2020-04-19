package com.wsec.services;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.vikramezhil.droidspeech.DroidSpeech;
import com.vikramezhil.droidspeech.OnDSListener;
import com.wsec.R;

import java.util.List;

public class SpeechRecognizerService2 extends Service {
    MediaPlayer player;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        player = MediaPlayer.create(this, R.raw.siren);
        player.setLooping(true);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        DroidSpeech droidSpeech = new DroidSpeech(this, null);
        droidSpeech.setOnDroidSpeechListener(new OnDSListener() {
            @Override
            public void onDroidSpeechSupportedLanguages(String currentSpeechLanguage, List<String> supportedSpeechLanguages) {

            }

            @Override
            public void onDroidSpeechRmsChanged(float rmsChangedValue) {

            }

            @Override
            public void onDroidSpeechLiveResult(String liveSpeechResult) {
                Log.v("Speech",liveSpeechResult);
                if(liveSpeechResult.contains("help"))
                {
                    playSiren();
                }
            }

            @Override
            public void onDroidSpeechFinalResult(String finalSpeechResult) {
                Log.v("Speech",finalSpeechResult);
            }

            @Override
            public void onDroidSpeechClosedByUser() {

            }

            @Override
            public void onDroidSpeechError(String errorMsg) {

            }
        });

        droidSpeech.startDroidSpeechRecognition();

        return START_STICKY;
    }

    private void playSiren(){
        player.start();
    }

    // Initiate playing the media player
    private void pauseSiren(){
        player.pause();


    }
}