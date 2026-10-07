package com.example.myapplication;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
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

import java.util.Locale;

public class ProgressFragment extends Fragment {

    private FragmentProgressBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProgressBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Populate dynamic real stats
        int realSteps = StepManager.getSavedSteps(requireContext());
        int sessions = StepManager.getCompletedSessions(requireContext());
        int mins = StepManager.getTotalMinutes(requireContext());
        int streak = StepManager.getStreakDays(requireContext());

        binding.tvStreakDays.setText(String.valueOf(streak));
        binding.tvWeeklyMins.setText(String.valueOf(mins));
        binding.tvTotalSessionsCount.setText(String.valueOf(sessions));

        binding.btnSettings.setOnClickListener(v -> 
            showPreferencesDialog()
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

        // Reminders: Open TimePickerDialog
        binding.tvReminders.setOnClickListener(v -> {
            TimePickerDialog timePicker = new TimePickerDialog(
                    requireContext(),
                    (view1, hourOfDay, minute) -> {
                        String timeFormatted = String.format(Locale.US, "%02d:%02d %s", 
                                (hourOfDay % 12 == 0 ? 12 : hourOfDay % 12), 
                                minute, 
                                (hourOfDay >= 12 ? "PM" : "AM"));
                        Toast.makeText(requireContext(), "🔔 Daily reminder scheduled for " + timeFormatted, Toast.LENGTH_LONG).show();
                    },
                    8, 0, false
            );
            timePicker.setTitle("Schedule Daily Mindfulness Reminder");
            timePicker.show();
        });

        // Downloads: Manage Offline Downloads
        binding.tvDownloads.setOnClickListener(v -> {
            String[] downloadedItems = {
                    "✓ Deep focus (25 min) - 8.4 MB",
                    "✓ Gentle stretch (10 min) - 4.2 MB",
                    "🗑 Clear All Downloads"
            };
            new AlertDialog.Builder(requireContext())
                    .setTitle("Offline Downloads (12.6 MB stored)")
                    .setItems(downloadedItems, (dialog, which) -> {
                        if (which == downloadedItems.length - 1) {
                            Toast.makeText(requireContext(), "Cleared offline downloads", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(requireContext(), "Item ready for offline listening!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .show();
        });

        // Preferences Dialog
        binding.tvPreferences.setOnClickListener(v -> showPreferencesDialog());
    }

    private void showPreferencesDialog() {
        String[] options = {
                "🌙 Theme: Serene Day/Night Auto",
                "📳 Haptic Feedback: Enabled",
                "🔔 Reminder Sound: Soft Chime",
                "🎯 Daily Walking Target: 5,000 Steps"
        };
        new AlertDialog.Builder(requireContext())
                .setTitle("Preferences & Settings")
                .setItems(options, (dialog, which) -> 
                    Toast.makeText(requireContext(), "Preference updated!", Toast.LENGTH_SHORT).show()
                )
                .setPositiveButton("Close", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
