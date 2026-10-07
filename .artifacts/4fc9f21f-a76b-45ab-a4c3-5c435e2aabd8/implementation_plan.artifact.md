# Serene — 6 Specialized Practice Experiences Implementation Plan

We will transform **Serene** from a single generic timer page into 6 distinct, highly innovative, specialized interactive experiences for each practice pillar.

## Specialized Practice Activities

### 1. 🚶 Mindful Movement (`MovementActivity.java` & `activity_movement.xml`)
- **Live Hardware Step Tracker**: Reads real-time device steps via `Sensor.TYPE_STEP_COUNTER`.
- **Animated Progress Ring**: Real-time step counter (0 - 5,000 steps), distance in km, active calories, and live step updates.

### 2. 🫁 Box Breathwork (`BreathworkActivity.java` & `activity_breathwork.xml`)
- **Interactive Breathing Visualizer**: Smooth pulsing circle animation expanding on *Inhale (4s)*, staying still on *Hold (4s)*, and contracting on *Exhale (4s)*.

### 3. 🌙 Sleep Sanctuary (`SleepActivity.java` & `activity_sleep.xml`)
- **Night Sky Wind-down**: Dark soothing night backdrop, sleep story player, ambient night soundscapes, and sleep quality tracker.

### 4. 📝 Mindful Journal (`JournalActivity.java` & `activity_journal.xml`)
- **Gratitude & Reflection Diary**: Daily prompt (*"What made you smile today?"*), mood tagging, and saved journal entry history.

### 5. 🧘 Guided Meditation (`MeditationActivity.java` & `activity_meditation.xml`)
- **Mindful Lotus Bloom**: Glowing visualizer with ambient meditation bell cues and guided pause.

### 6. 🎯 Pomodoro Focus (`FocusSessionActivity.java`)
- **Task Companion**: Add focus goal (*"Writing design doc"*), distraction-free timer, break cycles, and session logs.

## Verification Plan
- Build and run app to test that each card opens its own unique, specialized screen.
- Verify live step counting in `MovementActivity`.
- Verify breathing animation in `BreathworkActivity`.
