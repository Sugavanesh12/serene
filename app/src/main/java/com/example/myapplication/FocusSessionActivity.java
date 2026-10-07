package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.databinding.ActivityFocusSessionBinding;

import java.util.Locale;

public class FocusSessionActivity extends AppCompatActivity {

    private ActivityFocusSessionBinding binding;
    private boolean isPlaying = true;
    private CountDownTimer countDownTimer;
    private long initialDuration = 25 * 60 * 1000L; // Full initial duration
    private long timeLeftInMillis = 25 * 60 * 1000L; // Current remaining time
    private String currentMode = "Focus";
    private boolean isFavorite = false;
    private MediaPlayer mediaPlayer;
    private ActivityResultLauncher<Intent> musicPickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFocusSessionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get intent extras if passed from HomeFragment
        Intent intent = getIntent();
        String customTitle = intent.getStringExtra("EXTRA_TITLE");
        String customSubtitle = intent.getStringExtra("EXTRA_SUBTITLE");
        long customDuration = intent.getLongExtra("EXTRA_DURATION", 25 * 60 * 1000L);
        String initialMode = intent.getStringExtra("EXTRA_MODE");

        if (customTitle != null) {
            binding.tvSessionTitle.setText(customTitle);
        }
        if (customSubtitle != null) {
            binding.tvSessionSubtitle.setText(customSubtitle);
        }
        if (customDuration > 0) {
            initialDuration = customDuration;
            timeLeftInMillis = customDuration;
        }
        if (initialMode == null) {
            initialMode = "Focus";
        }

        // Register local music picker launcher
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

        // Interactive Favorite button toggle
        binding.btnFavorite.setOnClickListener(v -> {
            isFavorite = !isFavorite;
            if (isFavorite) {
                binding.btnFavorite.setImageResource(android.R.drawable.btn_star_big_on);
                Toast.makeText(this, "Added to favorites ❤️", Toast.LENGTH_SHORT).show();
            } else {
                binding.btnFavorite.setImageResource(android.R.drawable.btn_star);
                Toast.makeText(this, "Removed from favorites", Toast.LENGTH_SHORT).show();
            }
        });

        // Interactive Session Options Dialog
        binding.btnMore.setOnClickListener(v -> {
            String[] options = {"🔄 Reset Timer", "⏱ Change Duration", "🛑 End Session"};
            new AlertDialog.Builder(this)
                    .setTitle("Session Options")
                    .setItems(options, (dialog, which) -> {
                        if (which == 0) {
                            // Reset timer back to full initial duration
                            timeLeftInMillis = initialDuration;
                            int minutes = (int) (initialDuration / 1000) / 60;
                            int seconds = (int) (initialDuration / 1000) % 60;
                            binding.tvTimer.setText(String.format(Locale.US, "%02d:%02d", minutes, seconds));
                            
                            if (countDownTimer != null) {
                                countDownTimer.cancel();
                            }
                            if (isPlaying) {
                                startTimer(initialDuration);
                            } else {
                                binding.btnPlayPause.setImageResource(android.R.drawable.ic_media_play);
                            }
                            Toast.makeText(this, "Timer reset successfully", Toast.LENGTH_SHORT).show();
                        } else if (which == 1) {
                            showDurationPicker();
                        } else if (which == 2) {
                            finish();
                        }
                    })
                    .show();
        });

        // Tab switching (Focus, Breathe, Move)
        binding.btnTabFocus.setOnClickListener(v -> switchMode("Focus", 25 * 60 * 1000L, "Deep focus", "Focus session · 25 min"));
        binding.btnTabBreathe.setOnClickListener(v -> switchMode("Breathe", 5 * 60 * 1000L, "Box breathing", "Breathing session · 5 min"));
        binding.btnTabMove.setOnClickListener(v -> switchMode("Move", 10 * 60 * 1000L, "Mindful movement", "Movement session · 10 min"));

        // Sound selection picker including Local Music Upload & Silent Mode
        binding.tvSoundTitle.setText("Silent Mindfulness");
        binding.cardAudioPlayer.setOnClickListener(v -> {
            String[] sounds = {
                    "🤫 Silent Mindfulness (Recommended)", 
                    "📁 Upload Your Favorite Relaxing Music (.mp3/.wav)..."
            };
            new AlertDialog.Builder(this)
                    .setTitle("Select Audio or Upload Music")
                    .setItems(sounds, (dialog, which) -> {
                        if (which == 1) {
                            Intent pickerIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                            pickerIntent.addCategory(Intent.CATEGORY_OPENABLE);
                            pickerIntent.setType("audio/*");
                            musicPickerLauncher.launch(pickerIntent);
                        } else {
                            stopMusic();
                            binding.tvSoundTitle.setText("Silent Mindfulness");
                            Toast.makeText(this, "Silent mindfulness selected.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .show();
        });

        // Initialize with correct mode selected
        switchMode(initialMode, initialDuration, binding.tvSessionTitle.getText().toString(), binding.tvSessionSubtitle.getText().toString());

        binding.btnPlayPause.setOnClickListener(v -> {
            isPlaying = !isPlaying;
            if (isPlaying) {
                binding.btnPlayPause.setImageResource(android.R.drawable.ic_media_pause);
                startTimer(timeLeftInMillis);
                if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
                    try { mediaPlayer.start(); } catch (Exception ignored) {}
                }
                Toast.makeText(this, "Session resumed", Toast.LENGTH_SHORT).show();
            } else {
                binding.btnPlayPause.setImageResource(android.R.drawable.ic_media_play);
                if (countDownTimer != null) {
                    countDownTimer.cancel();
                }
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    try { mediaPlayer.pause(); } catch (Exception ignored) {}
                }
                Toast.makeText(this, "Session paused", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void stopMusic() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop();
                mediaPlayer.release();
            } catch (Exception ignored) {}
            mediaPlayer = null;
        }
    }

    private void showDurationPicker() {
        String[] durations = {"3 minutes (Quick Reset)", "5 minutes (Breathe)", "10 minutes (Reset)", "25 minutes (Deep Focus)", "45 minutes (Deep Flow)"};
        long[] millisList = {3 * 60 * 1000L, 5 * 60 * 1000L, 10 * 60 * 1000L, 25 * 60 * 1000L, 45 * 60 * 1000L};
        new AlertDialog.Builder(this)
                .setTitle("Select Session Duration")
                .setItems(durations, (dialog, which) -> {
                    initialDuration = millisList[which];
                    timeLeftInMillis = initialDuration;
                    int minutes = (int) (initialDuration / 1000) / 60;
                    int seconds = (int) (initialDuration / 1000) % 60;
                    binding.tvTimer.setText(String.format(Locale.US, "%02d:%02d", minutes, seconds));
                    
                    if (isPlaying) {
                        startTimer(timeLeftInMillis);
                    }
                    Toast.makeText(this, "Duration set to " + durations[which], Toast.LENGTH_SHORT).show();
                })
                .show();
    }

    private void switchMode(String mode, long durationMillis, String title, String subtitle) {
        currentMode = mode;
        if (intentDoesNotOverrideDuration()) {
            initialDuration = durationMillis;
            timeLeftInMillis = durationMillis;
            binding.tvSessionTitle.setText(title);
            binding.tvSessionSubtitle.setText(subtitle);
        }

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        int activeBg = getResources().getColor(R.color.serene_primary, getTheme());
        int inactiveBg = Color.TRANSPARENT;
        int activeText = Color.WHITE;
        int inactiveText = getResources().getColor(R.color.serene_text_secondary, getTheme());

        boolean isFocus = mode.equals("Focus");
        boolean isBreathe = mode.equals("Breathe");
        boolean isMove = mode.equals("Move");

        binding.btnTabFocus.setBackgroundColor(isFocus ? activeBg : inactiveBg);
        binding.btnTabFocus.setTextColor(isFocus ? activeText : inactiveText);
        binding.btnTabFocus.setAlpha(isFocus ? 1.0f : 0.7f);

        binding.btnTabBreathe.setBackgroundColor(isBreathe ? activeBg : inactiveBg);
        binding.btnTabBreathe.setTextColor(isBreathe ? activeText : inactiveText);
        binding.btnTabBreathe.setAlpha(isBreathe ? 1.0f : 0.7f);

        binding.btnTabMove.setBackgroundColor(isMove ? activeBg : inactiveBg);
        binding.btnTabMove.setTextColor(isMove ? activeText : inactiveText);
        binding.btnTabMove.setAlpha(isMove ? 1.0f : 0.7f);

        if (isPlaying) {
            startTimer(timeLeftInMillis);
        } else {
            int minutes = (int) (timeLeftInMillis / 1000) / 60;
            int seconds = (int) (timeLeftInMillis / 1000) % 60;
            binding.tvTimer.setText(String.format(Locale.US, "%02d:%02d", minutes, seconds));
        }
    }

    private boolean intentDoesNotOverrideDuration() {
        return getIntent().getStringExtra("EXTRA_TITLE") == null;
    }

    private void playLocalMusic(Uri uri) {
        try {
            stopMusic();
            mediaPlayer = MediaPlayer.create(this, uri);
            if (mediaPlayer != null) {
                mediaPlayer.setLooping(true);
                if (isPlaying) {
                    mediaPlayer.start();
                }
                binding.tvSoundTitle.setText("Custom Local Track");
                Toast.makeText(this, "Playing your relaxation music successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Could not open selected audio file", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error playing audio: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void startTimer(long millisInFuture) {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        countDownTimer = new CountDownTimer(millisInFuture, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                int minutes = (int) (timeLeftInMillis / 1000) / 60;
                int seconds = (int) (timeLeftInMillis / 1000) % 60;
                String timeFormatted = String.format(Locale.US, "%02d:%02d", minutes, seconds);
                binding.tvTimer.setText(timeFormatted);
            }

            @Override
            public void onFinish() {
                binding.tvTimer.setText("00:00");
                Toast.makeText(FocusSessionActivity.this, currentMode + " session completed!", Toast.LENGTH_LONG).show();
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        stopMusic();
    }
}
