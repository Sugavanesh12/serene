package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String userEmail = AuthManager.getPhoneNumber(requireContext());
        binding.tvProfileEmail.setText(userEmail + " • Premium Plan");

        binding.tvProfileName.setOnClickListener(v -> {
            EditText input = new EditText(requireContext());
            input.setText(binding.tvProfileName.getText().toString());
            input.setPadding(40, 40, 40, 40);

            new AlertDialog.Builder(requireContext())
                    .setTitle("Edit Profile Name")
                    .setView(input)
                    .setPositiveButton("Save", (dialog, which) -> {
                        String newName = input.getText().toString().trim();
                        if (!newName.isEmpty()) {
                            binding.tvProfileName.setText(newName);
                            Toast.makeText(requireContext(), "Profile updated to " + newName, Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        binding.layoutAvatarContainer.setOnClickListener(v -> 
            Toast.makeText(requireContext(), "Profile photo updated successfully", Toast.LENGTH_SHORT).show()
        );

        binding.tvAccountPlan.setOnClickListener(v -> 
            Toast.makeText(requireContext(), "Serene Premium active for " + userEmail, Toast.LENGTH_SHORT).show()
        );

        binding.tvDailyGoal.setOnClickListener(v -> {
            String[] goals = {"10 minutes / day", "15 minutes / day", "20 minutes / day", "30 minutes / day"};
            new AlertDialog.Builder(requireContext())
                    .setTitle("Set Daily Mindfulness Goal")
                    .setItems(goals, (dialog, which) -> {
                        binding.tvDailyGoal.setText("🎯 Set Daily Mindfulness Goal (" + goals[which].split(" ")[0] + " min)");
                        Toast.makeText(requireContext(), "Daily goal updated to " + goals[which], Toast.LENGTH_SHORT).show();
                    })
                    .show();
        });

        binding.tvNotifications.setOnClickListener(v -> 
            Toast.makeText(requireContext(), "Reminders & notifications configured", Toast.LENGTH_SHORT).show()
        );

        binding.tvLogout.setOnClickListener(v -> 
            new AlertDialog.Builder(requireContext())
                    .setTitle("Log Out")
                    .setMessage("Are you sure you want to log out of Serene?")
                    .setPositiveButton("Log Out", (dialog, which) -> {
                        AuthManager.logout(requireContext());
                        Intent intent = new Intent(requireActivity(), LoginActivity.class);
                        startActivity(intent);
                        requireActivity().finish();
                    })
                    .setNegativeButton("Cancel", null)
                    .show()
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
