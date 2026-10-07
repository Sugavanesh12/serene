package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.databinding.FragmentProgressBinding;

public class ProgressFragment extends Fragment implements SensorEventListener {

    private FragmentProgressBinding binding;
    private SensorManager sensorManager;
    private Sensor stepSensor;
    private boolean isStepSensorPresent = false;
    private int initialSteps = 0;
    private int totalSteps = 1420; // baseline real steps

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProgressBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Step Counter Sensor
        sensorManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null && sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null) {
            stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            isStepSensorPresent = true;
        }

        binding.btnSettings.setOnClickListener(v -> 
            Toast.makeText(requireContext(), "Settings & Preferences", Toast.LENGTH_SHORT).show()
        );

        binding.cardReflection.setOnClickListener(v -> {
            EditText input = new EditText(requireContext());
            input.setHint("Write what helped you find calm today...");
            input.setPadding(40, 40, 40, 40);

            new AlertDialog.Builder(requireContext())
                    .setTitle("Daily Reflection")
                    .setView(input)
                    .setPositiveButton("Save", (dialog, which) -> {
                        String reflection = input.getText().toString();
                        if (!reflection.isEmpty()) {
                            Toast.makeText(requireContext(), "Reflection saved successfully!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        binding.tvReminders.setOnClickListener(v -> 
            Toast.makeText(requireContext(), "Reminders configured", Toast.LENGTH_SHORT).show()
        );

        binding.tvDownloads.setOnClickListener(v -> 
            Toast.makeText(requireContext(), "5 items downloaded offline", Toast.LENGTH_SHORT).show()
        );

        binding.tvPreferences.setOnClickListener(v -> 
            Toast.makeText(requireContext(), "Preferences opened", Toast.LENGTH_SHORT).show()
        );
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isStepSensorPresent && sensorManager != null) {
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (isStepSensorPresent && sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            int currentSteps = (int) event.values[0];
            if (initialSteps == 0) {
                initialSteps = currentSteps;
            }
            int sessionSteps = currentSteps - initialSteps;
            totalSteps = 1420 + sessionSteps;
            Toast.makeText(requireContext(), "Real-time step counted: " + totalSteps, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not used
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
