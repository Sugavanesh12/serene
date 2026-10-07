package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;

public class StepManager {
    private static final String PREF_NAME = "SereneStepsDB";
    private static final String KEY_TOTAL_STEPS = "totalRealSteps";
    private static final String KEY_INITIAL_HARDWARE_STEPS = "initialHardwareSteps";
    private static final String KEY_COMPLETED_SESSIONS = "completedSessions";
    private static final String KEY_MINUTES_PRACTICED = "minutesPracticed";
    private static final String KEY_DAILY_GOAL = "dailyGoalMins";

    public static int getSavedSteps(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(KEY_TOTAL_STEPS, 0); // New users start at 0 steps
    }

    public static void saveSteps(Context context, int steps) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putInt(KEY_TOTAL_STEPS, steps).apply();
    }

    public static int getInitialHardwareSteps(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(KEY_INITIAL_HARDWARE_STEPS, 0);
    }

    public static void saveInitialHardwareSteps(Context context, int steps) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putInt(KEY_INITIAL_HARDWARE_STEPS, steps).apply();
    }

    public static int getCompletedSessions(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(KEY_COMPLETED_SESSIONS, 0); // New users start at 0 sessions
    }

    public static void incrementCompletedSessions(Context context) {
        int current = getCompletedSessions(context);
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putInt(KEY_COMPLETED_SESSIONS, current + 1).apply();
    }

    public static int getTotalMinutes(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(KEY_MINUTES_PRACTICED, 0); // New users start at 0 minutes
    }

    public static void addMindfulnessMinutes(Context context, int mins) {
        int current = getTotalMinutes(context);
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putInt(KEY_MINUTES_PRACTICED, current + mins).apply();
    }

    public static int getDailyGoal(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(KEY_DAILY_GOAL, 20); // Default daily goal: 20 mins
    }

    public static void saveDailyGoal(Context context, int goalMins) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putInt(KEY_DAILY_GOAL, goalMins).apply();
    }

    public static int getStreakDays(Context context) {
        int steps = getSavedSteps(context);
        int sessions = getCompletedSessions(context);
        if (steps > 0 || sessions > 0) {
            return Math.max(1, (steps / 1000) + sessions);
        }
        return 0; // New users start at 0 day streak
    }
}
