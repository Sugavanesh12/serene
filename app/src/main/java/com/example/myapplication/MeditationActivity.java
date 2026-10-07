package com.example.myapplication;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.databinding.ActivityMeditationBinding;

import java.util.Locale;

public class MeditationActivity extends AppCompatActivity {

    private ActivityMeditationBinding binding;
    private CountDownTimer timer;
    private boolean isPlaying = false;
    private MediaPlayer mediaPlayer;
    private long timeLeftInMillis = 15 * 60 * 1000L;
    private ActivityResultLauncher<Intent> musicPickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMeditationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        musicPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri audioUri = result.getData().getData();
                        if (audioUri != null) {
                            playLocalMusic(audioUri);
                        }
                    }
                }
        );

        binding.btnBack.setOnClickListener(v -> finish());

        binding.layoutLotusBloom.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("audio/*");
            musicPickerLauncher.launch(intent);
        });

        binding.btnStartMeditation.setOnClickListener(v -> {
            isPlaying = !isPlaying;
            if (isPlaying) {
                binding.btnStartMeditation.setText("⏸ Pause Meditation");
                startMeditationTimer();
                if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
                    try { mediaPlayer.start(); } catch (Exception ignored) {}
                }
            } else {
                binding.btnStartMeditation.setText("🧘 Begin Guided Meditation");
                pauseMeditationTimer();
            }
        });
    }

    private void playLocalMusic(Uri uri) {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }
            mediaPlayer = MediaPlayer.create(this, uri);
            if (mediaPlayer != null) {
                mediaPlayer.setLooping(true);
                if (isPlaying) {
                    mediaPlayer.start();
                }
                Toast.makeText(this, "Playing your custom meditation music!", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error playing audio: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void startMeditationTimer() {
        if (timer != null) timer.cancel();
        binding.layoutLotusBloom.animate().scaleX(1.15f).scaleY(1.15f).setDuration(2000).start();
        timer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                int minutes = (int) (timeLeftInMillis / 1000) / 60;
                int seconds = (int) (timeLeftInMillis / 1000) % 60;
                binding.tvMeditationTime.setText(String.format(Locale.US, "%02d:%02d", minutes, seconds));
            }

            @Override
            public void onFinish() {
                binding.tvMeditationTime.setText("00:00");
            }
        }.start();
    }

    private void pauseMeditationTimer() {
        if (timer != null) timer.cancel();
        binding.layoutLotusBloom.animate().scaleX(1.0f).scaleY(1.0f).setDuration(500).start();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            try { mediaPlayer.pause(); } catch (Exception ignored) {}
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pauseMeditationTimer();
        if (mediaPlayer != null) {
            try { mediaPlayer.release(); } catch (Exception ignored) {}
            mediaPlayer = null;
        }
    }
}
