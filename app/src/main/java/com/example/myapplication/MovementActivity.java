package com.example.myapplication;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.myapplication.databinding.ActivityMovementBinding;

import java.util.Locale;

public class MovementActivity extends AppCompatActivity implements SensorEventListener {

    private ActivityMovementBinding binding;
    private SensorManager sensorManager;
    private Sensor stepSensor;
    private Sensor accelSensor;
    private boolean isSensorPresent = false;
    private int currentSteps = 0;
    private int dailyTargetSteps = 5000;
    private static final int PERMISSION_REQUEST_CODE = 1001;
    private double lastAccelMagnitude = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMovementBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> finish());

        // Load saved steps from persistent database (starts at 0 for new users)
        currentSteps = StepManager.getSavedSteps(this);

        // Request Activity Recognition Runtime Permission for physical devices
        checkAndRequestPermissions();

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

        // Initialize Sensors
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null) {
                stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
                isSensorPresent = true;
            } else if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
                accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            }
        }

        // Display current step count without auto-incrementing
        updateUI(currentSteps);

        // Analytics Tabs
        switchAnalyticsView("Daily");
        binding.tabDaily.setOnClickListener(v -> switchAnalyticsView("Daily"));
        binding.tabWeekly.setOnClickListener(v -> switchAnalyticsView("Weekly"));
        binding.tabMonthly.setOnClickListener(v -> switchAnalyticsView("Monthly"));
    }

    private void checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.ACTIVITY_RECOGNITION},
                        PERMISSION_REQUEST_CODE
                );
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Physical Step Sensor Permission Granted!", Toast.LENGTH_SHORT).show();
                registerSensors();
            } else {
                Toast.makeText(this, "Step permission denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void switchAnalyticsView(String viewType) {
        int primaryColor = getResources().getColor(R.color.serene_primary, getTheme());
        int textColorSecondary = getResources().getColor(R.color.serene_text_secondary, getTheme());

        // Reset all tabs
        binding.tabDaily.setBackgroundColor(Color.TRANSPARENT);
        binding.tabDaily.setTextColor(textColorSecondary);

        binding.tabWeekly.setBackgroundColor(Color.TRANSPARENT);
        binding.tabWeekly.setTextColor(textColorSecondary);

        binding.tabMonthly.setBackgroundColor(Color.TRANSPARENT);
        binding.tabMonthly.setTextColor(textColorSecondary);

        // Highlight selected tab with primary dark green background and white text
        if (viewType.equals("Daily")) {
            binding.tabDaily.setBackgroundColor(primaryColor);
            binding.tabDaily.setTextColor(Color.WHITE);
            binding.tvChartTitle.setText("Daily Walking Analytics (Mon-Sun)");
            setChartLabels("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun");
        } else if (viewType.equals("Weekly")) {
            binding.tabWeekly.setBackgroundColor(primaryColor);
            binding.tabWeekly.setTextColor(Color.WHITE);
            binding.tvChartTitle.setText("Weekly Walking Analytics (W1-W5)");
            setChartLabels("W1", "W2", "W3", "W4", "W5", "--", "--");
        } else if (viewType.equals("Monthly")) {
            binding.tabMonthly.setBackgroundColor(primaryColor);
            binding.tabMonthly.setTextColor(Color.WHITE);
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

    private void updateUI(int steps) {
        binding.tvLiveSteps.setText(String.format(Locale.US, "%,d", steps));
        binding.tvGoalSubtext.setText(String.format(Locale.US, "steps / %,d target", dailyTargetSteps));

        double distanceKm = (steps * 0.00075); // approx 0.75m per step
        double calories = (steps * 0.04);     // approx 0.04 kcal per step

        binding.tvDistance.setText(String.format(Locale.US, "%.2f km", distanceKm));
        binding.tvCalories.setText(String.format(Locale.US, "%.0f kcal", calories));

        // Check Goal Status: SUCCESS vs IN PROGRESS
        if (steps >= dailyTargetSteps && steps > 0) {
            binding.tvStatusEmoji.setText("🎉");
            binding.tvStatusTitle.setText("Daily Walking Goal Achieved! 🏆");
            binding.tvStatusTitle.setTextColor(Color.parseColor("#2E7D32"));
            binding.tvStatusDesc.setText("Incredible job! You reached " + String.format(Locale.US, "%,d", steps) + " steps today.");
            binding.cardStatusBanner.setCardBackgroundColor(Color.parseColor("#E8F5E9"));
        } else {
            int remaining = Math.max(0, dailyTargetSteps - steps);
            binding.tvStatusEmoji.setText("🚶");
            binding.tvStatusTitle.setText("Walking Goal In Progress");
            binding.tvStatusTitle.setTextColor(getResources().getColor(R.color.serene_text_primary, getTheme()));
            binding.tvStatusDesc.setText(String.format(Locale.US, "%,d steps remaining to achieve daily target", remaining));
            binding.cardStatusBanner.setCardBackgroundColor(Color.parseColor("#E6ECE7"));
        }
    }

    private void registerSensors() {
        if (sensorManager != null) {
            if (isSensorPresent && stepSensor != null) {
                sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI);
            } else if (accelSensor != null) {
                sensorManager.registerListener(this, accelSensor, SensorManager.SENSOR_DELAY_UI);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerSensors();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
        lastAccelMagnitude = 0; // Reset baseline on pause
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            int hardwareSteps = (int) event.values[0];
            int initial = StepManager.getInitialHardwareSteps(this);
            if (initial == 0) {
                initial = hardwareSteps;
                StepManager.saveInitialHardwareSteps(this, initial);
            }
            int addedSteps = Math.max(0, hardwareSteps - initial);
            if (addedSteps > currentSteps) {
                currentSteps = addedSteps;
                StepManager.saveSteps(this, currentSteps);
                updateUI(currentSteps);
            }
        } else if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            // Physical motion detector backup
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            double magnitude = Math.sqrt(x * x + y * y + z * z);
            
            // Ignore initial reading when opening screen to prevent false +1
            if (lastAccelMagnitude == 0) {
                lastAccelMagnitude = magnitude;
                return;
            }
            
            double delta = Math.abs(magnitude - lastAccelMagnitude);
            lastAccelMagnitude = magnitude;

            if (delta > 12.0) { // High threshold corresponding to real physical walking steps
                currentSteps++;
                StepManager.saveSteps(this, currentSteps);
                updateUI(currentSteps);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}
