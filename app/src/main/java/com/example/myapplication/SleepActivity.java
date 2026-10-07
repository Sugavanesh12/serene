package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.databinding.ActivitySleepBinding;

import java.util.Locale;

public class SleepActivity extends AppCompatActivity {

    private ActivitySleepBinding binding;
    private CountDownTimer sleepTimer;
    private boolean isPlaying = false;
    private MediaPlayer mediaPlayer;
    private long timeLeftInMillis = 20 * 60 * 1000L;
    private ActivityResultLauncher<Intent> musicPickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySleepBinding.inflate(getLayoutInflater());
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

        // Dedicated Sleep Music Selector Card
        binding.cardSleepAudio.setOnClickListener(v -> {
            String[] options = {
                    "📁 Upload Local Sleep Music from Device (.mp3/.wav)",
                    "🤫 Silent Wind-down"
            };
            new AlertDialog.Builder(this)
                    .setTitle("Select Sleep Audio")
                    .setItems(options, (dialog, which) -> {
                        if (which == 0) {
                            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                            intent.addCategory(Intent.CATEGORY_OPENABLE);
                            intent.setType("audio/*");
                            musicPickerLauncher.launch(intent);
                        } else {
                            if (mediaPlayer != null) {
                                try { mediaPlayer.stop(); mediaPlayer.release(); } catch (Exception ignored) {}
                                mediaPlayer = null;
                            }
                            binding.tvSleepAudioTitle.setText("Silent Wind-down");
                            Toast.makeText(this, "Silent wind-down selected", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .show();
        });

        binding.btnPlaySleep.setOnClickListener(v -> {
            isPlaying = !isPlaying;
            if (isPlaying) {
                binding.btnPlaySleep.setText("⏸ Pause Sleep Story");
                startSleepTimer();
                if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
                    try { mediaPlayer.start(); } catch (Exception ignored) {}
                }
            } else {
                binding.btnPlaySleep.setText("🌙 Play Sleep Story");
                pauseSleepTimer();
            }
        });
    }

    private void playLocalMusic(Uri uri) {
        try {
            if (mediaPlayer != null) {
                try { mediaPlayer.stop(); mediaPlayer.release(); } catch (Exception ignored) {}
                mediaPlayer = null;
            }
            mediaPlayer = MediaPlayer.create(this, uri);
            if (mediaPlayer != null) {
                mediaPlayer.setLooping(true);
                if (isPlaying) {
                    mediaPlayer.start();
                }
                binding.tvSleepAudioTitle.setText("Custom Local Sleep Track");
                Toast.makeText(this, "Playing your local sleep music successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Could not open selected audio file", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error playing audio: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void startSleepTimer() {
        if (sleepTimer != null) sleepTimer.cancel();
        sleepTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                int minutes = (int) (timeLeftInMillis / 1000) / 60;
                int seconds = (int) (timeLeftInMillis / 1000) % 60;
                binding.tvSleepTimer.setText(String.format(Locale.US, "%02d:%02d", minutes, seconds));
            }

            @Override
            public void onFinish() {
                binding.tvSleepTimer.setText("00:00");
                Toast.makeText(SleepActivity.this, "Sweet dreams...", Toast.LENGTH_LONG).show();
            }
        }.start();
    }

    private void pauseSleepTimer() {
        if (sleepTimer != null) sleepTimer.cancel();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            try { mediaPlayer.pause(); } catch (Exception ignored) {}
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pauseSleepTimer();
        if (mediaPlayer != null) {
            try { mediaPlayer.release(); } catch (Exception ignored) {}
            mediaPlayer = null;
        }
    }
}
