package com.example.myapplication;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.databinding.ActivityBreathworkBinding;

import java.util.Locale;

public class BreathworkActivity extends AppCompatActivity {

    private ActivityBreathworkBinding binding;
    private CountDownTimer breathTimer;
    private boolean isRunning = false;
    private int phase = 0; // 0: Inhale, 1: Hold, 2: Exhale, 3: Rest

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBreathworkBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> finish());

        binding.btnStartBreathe.setOnClickListener(v -> {
            if (!isRunning) {
                startBreathingCycle();
                binding.btnStartBreathe.setText("Pause Breathing Guide");
            } else {
                stopBreathingCycle();
                binding.btnStartBreathe.setText("🫁 Resume Breathing Guide");
            }
        });
    }

    private void startBreathingCycle() {
        isRunning = true;
        runPhase();
    }

    private void runPhase() {
        if (!isRunning) return;

        String stateText;
        float targetScale;

        switch (phase % 4) {
            case 0:
                stateText = "Inhale...";
                targetScale = 1.25f;
                break;
            case 1:
                stateText = "Hold...";
                targetScale = 1.25f;
                break;
            case 2:
                stateText = "Exhale...";
                targetScale = 0.85f;
                break;
            default:
                stateText = "Rest...";
                targetScale = 1.0f;
                break;
        }

        binding.tvBreathState.setText(stateText);
        binding.layoutBreathCircle.animate()
                .scaleX(targetScale)
                .scaleY(targetScale)
                .setDuration(4000)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();

        breathTimer = new CountDownTimer(4000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int seconds = (int) (millisUntilFinished / 1000) + 1;
                binding.tvBreathCountdown.setText(String.format(Locale.US, "%d seconds", seconds));
            }

            @Override
            public void onFinish() {
                phase++;
                runPhase();
            }
        }.start();
    }

    private void stopBreathingCycle() {
        isRunning = false;
        if (breathTimer != null) {
            breathTimer.cancel();
        }
        binding.layoutBreathCircle.animate().scaleX(1.0f).scaleY(1.0f).setDuration(300).start();
        binding.tvBreathState.setText("Paused");
        binding.tvBreathCountdown.setText("Tap resume when ready");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopBreathingCycle();
    }
}
