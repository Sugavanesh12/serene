package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.myapplication.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnStartReset.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), FocusSessionActivity.class);
            intent.putExtra("EXTRA_TITLE", "10-minute reset");
            intent.putExtra("EXTRA_SUBTITLE", "Reset session · 10 min");
            intent.putExtra("EXTRA_DURATION", 10 * 60 * 1000L);
            intent.putExtra("EXTRA_MODE", "Focus");
            startActivity(intent);
        });

        binding.ivProfile.setOnClickListener(v -> {
            BottomNavigationView nav = requireActivity().findViewById(R.id.bottomNavigationView);
            if (nav != null) {
                nav.setSelectedItemId(R.id.navigation_profile);
            }
        });

        // Mood check-ins
        setupMoodClick(binding.layoutCalm, "Calm", "Wonderful! Let me guide you through a 10-minute mindful pause.", "Mindful calm", "Meditation · 10 min", 10 * 60 * 1000L, "Focus");
        setupMoodClick(binding.layoutTired, "Tired", "Rest is productive. Let's ease into a soothing 20-minute sleep story.", "Sleep stories", "Sleep session · 20 min", 20 * 60 * 1000L, "Focus");
        setupMoodClick(binding.layoutStressed, "Stressed", "We hear you. Let's take a 3-minute breathing reset together.", "Breathing reset", "Breathe session · 3 min", 3 * 60 * 1000L, "Breathe");
        setupMoodClick(binding.layoutFocused, "Focused", "You're in the zone! Ready for a 25-minute deep focus session.", "Deep focus", "Focus session · 25 min", 25 * 60 * 1000L, "Focus");

        // Specialized Practice Cards -> Each opens its own unique, innovative activity!
        binding.cardMeditation.setOnClickListener(v -> 
            startActivity(new Intent(requireActivity(), MeditationActivity.class))
        );

        binding.cardSleep.setOnClickListener(v -> 
            startActivity(new Intent(requireActivity(), SleepActivity.class))
        );

        binding.cardBreathwork.setOnClickListener(v -> 
            startActivity(new Intent(requireActivity(), BreathworkActivity.class))
        );

        binding.cardFocus.setOnClickListener(v -> 
            startActivity(new Intent(requireActivity(), FocusSessionActivity.class))
        );

        binding.cardMovement.setOnClickListener(v -> 
            startActivity(new Intent(requireActivity(), MovementActivity.class))
        );

        binding.cardJournaling.setOnClickListener(v -> 
            startActivity(new Intent(requireActivity(), JournalActivity.class))
        );
    }

    private void setupMoodClick(View moodView, String moodName, String comfortingMessage, String targetTitle, String targetSubtitle, long targetDuration, String targetMode) {
        moodView.setOnClickListener(v -> {
            v.animate().scaleX(1.1f).scaleY(1.1f).setDuration(150).withEndAction(() ->
                v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(150).start()
            ).start();

            new AlertDialog.Builder(requireContext())
                    .setTitle("Mood Check-in: " + moodName)
                    .setMessage(comfortingMessage)
                    .setPositiveButton("Begin Recommended Practice", (d, w) -> {
                        Intent intent = new Intent(requireActivity(), FocusSessionActivity.class);
                        intent.putExtra("EXTRA_TITLE", targetTitle);
                        intent.putExtra("EXTRA_SUBTITLE", targetSubtitle);
                        intent.putExtra("EXTRA_DURATION", targetDuration);
                        intent.putExtra("EXTRA_MODE", targetMode);
                        startActivity(intent);
                    })
                    .setNegativeButton("Thank you", null)
                    .show();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
