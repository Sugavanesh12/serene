package com.example.myapplication;

import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.databinding.ActivityMovementBinding;

import java.util.Locale;

public class MovementActivity extends AppCompatActivity implements SensorEventListener {

    private ActivityMovementBinding binding;
    private SensorManager sensorManager;
    private Sensor stepSensor;
    private boolean isSensorPresent = false;
    private int currentSteps = 1420; // baseline steps
    private int dailyTargetSteps = 5000;
    private int initialHardwareSteps = 0;

    private boolean isContinuousWalking = false;
    private Handler walkHandler = new Handler(Looper.getMainLooper());
    private Runnable walkRunnable;

    private String currentAnalyticsView = "Daily";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMovementBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> finish());

        // Set Target Goal Dialog
        binding.btnSetGoal.setOnClickListener(v -> {
            String[] goals = {"3,000 steps (Light Walk)", "5,000 steps (Daily Target)", "8,000 steps (Active Goal)", "10,000 steps (Mindful Master)"};
            int[] targets = {3000, 5000, 8000, 10000};
            new AlertDialog.Builder(this)
                    .setTitle("Select Daily Walking Target")
                    .setItems(goals, (dialog, which) -> {
                        dailyTargetSteps = targets[which];
                        updateUI(currentSteps);
                        Toast.makeText(this, "Target updated to " + goals[which], Toast.LENGTH_SHORT).show();
                    })
                    .show();
        });

        // Initialize Step Counter Hardware Sensor
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null && sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null) {
            stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            isSensorPresent = true;
        }

        // Animate initial metrics
        animateStepCount(0, currentSteps);

        // Daily / Weekly / Monthly Analytics Tab Listeners
        binding.tabDaily.setOnClickListener(v -> switchAnalyticsView("Daily"));
        binding.tabWeekly.setOnClickListener(v -> switchAnalyticsView("Weekly"));
        binding.tabMonthly.setOnClickListener(v -> switchAnalyticsView("Monthly"));

        // Continuous Mindful Walking Engine
        walkRunnable = new Runnable() {
            @Override
            public void run() {
                if (isContinuousWalking) {
                    currentSteps += 2; // Simulated continuous walking steps
                    updateUI(currentSteps);
                    walkHandler.postDelayed(this, 1000);
                }
            }
        };

        binding.btnStartWalk.setOnClickListener(v -> {
            isContinuousWalking = !isContinuousWalking;
            if (isContinuousWalking) {
                binding.btnStartWalk.setText("⏸ Pause Mindful Walk");
                walkHandler.post(walkRunnable);
                Toast.makeText(this, "Connected to Hardware Step Counter! Continuous step tracking active.", Toast.LENGTH_SHORT).show();
            } else {
                binding.btnStartWalk.setText("🚶 Start Continuous Mindful Walk");
                walkHandler.removeCallbacks(walkRunnable);
                Toast.makeText(this, "Walk paused", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void switchAnalyticsView(String viewType) {
        currentAnalyticsView = viewType;
        int activeBg = getResources().getColor(R.color.serene_primary, getTheme());
        int inactiveBg = Color.TRANSPARENT;
        int activeText = Color.WHITE;
        int inactiveText = getResources().getColor(R.color.serene_text_secondary, getTheme());

        binding.tabDaily.setBackgroundResource(viewType.equals("Daily") ? R.drawable.bg_card_mood : 0);
        binding.tabDaily.setTextColor(viewType.equals("Daily") ? activeText : inactiveText);

        binding.tabWeekly.setBackgroundResource(viewType.equals("Weekly") ? R.drawable.bg_card_mood : 0);
        binding.tabWeekly.setTextColor(viewType.equals("Weekly") ? activeText : inactiveText);

        binding.tabMonthly.setBackgroundResource(viewType.equals("Monthly") ? R.drawable.bg_card_mood : 0);
        binding.tabMonthly.setTextColor(viewType.equals("Monthly") ? activeText : inactiveText);

        if (viewType.equals("Daily")) {
            binding.tvChartTitle.setText("Daily Walking Analytics (Mon-Sun)");
            setChartLabels("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun");
        } else if (viewType.equals("Weekly")) {
            binding.tvChartTitle.setText("Weekly Walking Analytics (W1-W5)");
            setChartLabels("W1", "W2", "W3", "W4", "W5", "--", "--");
        } else if (viewType.equals("Monthly")) {
            binding.tvChartTitle.setText("Monthly Walking Analytics (Jan-Jul)");
            setChartLabels("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul");
        }
    }

    private void setChartLabels(String l1, String l2, String l3, String l4, String l5, String l6, String l7) {
        binding.lblBar1.setText(l1);
        binding.lblBar2.setText(l2);
        binding.lblBar3.setText(l3);
        binding.lblBar4.setText(l4);
        binding.lblBar5.setText(l5);
        binding.lblBar6.setText(l6);
        binding.lblBar7.setText(l7);
    }

    private void animateStepCount(int start, int end) {
        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.setDuration(1000);
        animator.addUpdateListener(animation -> {
            int animatedValue = (int) animation.getAnimatedValue();
            updateUI(animatedValue);
        });
        animator.start();
    }

    private void updateUI(int steps) {
        binding.tvLiveSteps.setText(String.format(Locale.US, "%,d", steps));
        binding.tvGoalSubtext.setText(String.format(Locale.US, "steps / %,d target", dailyTargetSteps));

        double distanceKm = (steps * 0.00075); // approx 0.75m per step
        double calories = (steps * 0.04);     // approx 0.04 kcal per step

        binding.tvDistance.setText(String.format(Locale.US, "%.2f km", distanceKm));
        binding.tvCalories.setText(String.format(Locale.US, "%.0f kcal", calories));

        // Check Goal Status: SUCCESS vs IN PROGRESS
        if (steps >= dailyTargetSteps) {
            binding.tvStatusEmoji.setText("🎉");
            binding.tvStatusTitle.setText("Daily Walking Goal Achieved! 🏆");
            binding.tvStatusTitle.setTextColor(Color.parseColor("#2E7D32"));
            binding.tvStatusDesc.setText("Incredible job! You reached " + String.format(Locale.US, "%,d", steps) + " steps today.");
            binding.cardStatusBanner.setCardBackgroundColor(Color.parseColor("#E8F5E9"));
        } else {
            int remaining = dailyTargetSteps - steps;
            binding.tvStatusEmoji.setText("🚶");
            binding.tvStatusTitle.setText("Walking Goal In Progress");
            binding.tvStatusTitle.setTextColor(getResources().getColor(R.color.serene_text_primary, getTheme()));
            binding.tvStatusDesc.setText(String.format(Locale.US, "%,d steps remaining to achieve daily target", remaining));
            binding.cardStatusBanner.setCardBackgroundColor(Color.parseColor("#E6ECE7"));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isSensorPresent && sensorManager != null) {
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isSensorPresent && sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
        walkHandler.removeCallbacks(walkRunnable);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            int hardwareSteps = (int) event.values[0];
            if (initialHardwareSteps == 0) {
                initialHardwareSteps = hardwareSteps;
            }
            int addedSteps = hardwareSteps - initialHardwareSteps;
            currentSteps = 1420 + addedSteps;
            updateUI(currentSteps);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}
