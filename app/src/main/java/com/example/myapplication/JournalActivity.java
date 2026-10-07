package com.example.myapplication;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.databinding.ActivityJournalBinding;

public class JournalActivity extends AppCompatActivity {

    private ActivityJournalBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityJournalBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> finish());

        binding.btnSaveJournal.setOnClickListener(v -> {
            String content = binding.etJournalContent.getText() != null ? binding.etJournalContent.getText().toString().trim() : "";
            if (!content.isEmpty()) {
                String existing = binding.tvSavedEntries.getText().toString();
                binding.tvSavedEntries.setText("• '" + content + "' (Just now)\n" + existing);
                binding.etJournalContent.setText("");
                Toast.makeText(this, "Gratitude entry saved! 🌸", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Please write something before saving", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
