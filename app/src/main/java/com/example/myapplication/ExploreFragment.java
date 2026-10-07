package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.myapplication.databinding.FragmentExploreBinding;

import java.util.ArrayList;
import java.util.List;

public class ExploreFragment extends Fragment {

    private FragmentExploreBinding binding;
    private List<PracticeItem> allPractices;
    private PracticeAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentExploreBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        allPractices = new ArrayList<>();
        allPractices.add(new PracticeItem("1", "Sleep stories", "Drift into peaceful places.", "20 min", "Sleep", true));
        allPractices.add(new PracticeItem("2", "Deep focus", "Clear your mind. Do what matters.", "25 min", "Focus", true));
        allPractices.add(new PracticeItem("3", "Box breathing", "Find calm in four counts.", "5 min", "Breathwork", true));
        allPractices.add(new PracticeItem("4", "Gentle stretch", "Move mindfully, feel better.", "10 min", "Movement", true));
        allPractices.add(new PracticeItem("5", "Rain sounds", "A soothing soundscape for rest or focus.", "30 min", "Sounds", true));

        List<PracticeItem> displayList = new ArrayList<>(allPractices);
        adapter = new PracticeAdapter(displayList, item -> {
            Intent intent = new Intent(requireActivity(), FocusSessionActivity.class);
            startActivity(intent);
        });

        binding.recyclerViewPractices.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerViewPractices.setAdapter(adapter);

        binding.btnFilterAll.setOnClickListener(v -> filterCategory("All", displayList));
        binding.btnFilterSleep.setOnClickListener(v -> filterCategory("Sleep", displayList));
        binding.btnFilterFocus.setOnClickListener(v -> filterCategory("Focus", displayList));
        binding.btnFilterBreathwork.setOnClickListener(v -> filterCategory("Breathwork", displayList));
        binding.btnFilterMovement.setOnClickListener(v -> filterCategory("Movement", displayList));
        binding.btnFilterSounds.setOnClickListener(v -> filterCategory("Sounds", displayList));
    }

    private void filterCategory(String category, List<PracticeItem> displayList) {
        displayList.clear();
        if (category.equals("All")) {
            displayList.addAll(allPractices);
        } else {
            for (PracticeItem item : allPractices) {
                if (item.getCategory().equalsIgnoreCase(category)) {
                    displayList.add(item);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
