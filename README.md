# 🌿 Serene — Wellbeing by Design (v1.1.0)

> *A personal digital sanctuary for calm, focus, rest, and growth. Built natively for Android using Java and Material 3.*

[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com)
[![Java](https://img.shields.io/badge/Language-Java_17-orange.svg)](https://www.oracle.com/java/)
[![Material 3](https://img.shields.io/badge/Design-Material_3-blue.svg)](https://m3.material.io)

---

## 📖 About Serene

**Serene** is a modern, holistic mindfulness and physical wellbeing application designed to bring balance, clarity, and peace to daily life. Rather than generic timers, Serene provides **six specialized, purpose-built interactive experiences** covering physical activity tracking, deep focus, breathwork, sleep wind-down, and gratitude journaling.

---

## ✨ Key Features & Pillars

### 🚶 1. Mindful Walking & Real-Time Step Tracker (`MovementActivity`)
- **Hardware Pedometer Integration**: Connects directly to Android's `Sensor.TYPE_STEP_COUNTER` and `ACTIVITY_RECOGNITION` runtime permission for 24/7 background step tracking on physical devices.
- **Live Walking Metrics**: Real-time calculation of distance (`km`), active calories burned (`kcal`), and pace (`min/km`).
- **Goal Achievement System**: Dynamic status banners transitioning from **In Progress 🚶** to **Goal Achieved (SUCCESS 🎉)** upon hitting daily targets.
- **Analytics Dashboard**: Switchable **Daily** (Mon–Sun), **Weekly** (W1–W5), and **Monthly** (Jan–Jul) animated step analytics charts.

### 🫁 2. Interactive Box Breathwork (`BreathworkActivity`)
- **Animated Pacer**: Expanding/contracting visualizer guiding users through *Inhale (4s) → Hold (4s) → Exhale (4s) → Rest (4s)* cycles to actively reduce stress and slow heart rate.

### 🌙 3. Sleep Sanctuary (`SleepActivity`)
- **Bedtime Wind-Down**: Night sky aesthetic (`#121824`) with bedtime stories, gentle sleep countdown timers, and local audio player support.

### 📝 4. Gratitude & Reflection Diary (`JournalActivity`)
- **Daily Prompts**: Guided reflection prompts (*"What is one small thing that brought you calm today?"*).
- **Persistent History**: View and manage saved daily reflections with timestamps.

### 🧘 5. Guided Meditation (`MeditationActivity`)
- **Glowing Lotus Bloom**: Animated meditation visualizer paired with soothing chime audio cues and local music support.

### 🎯 6. Pomodoro Focus Companion (`FocusSessionActivity`)
- **Custom Timer**: Flexible 3m, 5m, 10m, 25m, or 45m focus sessions.
- **Local Audio Upload**: Play your own `.mp3` / `.wav` relaxation tracks straight from your device via the file picker.
- **Session Controls**: Pause, resume, reset, or change duration on the fly without interrupting playback.

### 🔐 7. Secure Authentication & Session Persistence
- **Email & Password Authentication**: Full Sign In & Sign Up validation with duplicate checking and password update options.
- **Session Persistence**: Powered by `AuthManager` (`SharedPreferences`), ensuring users stay logged in across app restarts until explicit logout.
- **Dynamic New User Baselines**: New users start with **0 steps**, **0 mins**, **0 sessions**, and **0 day streak**, which accumulate dynamically as activities are completed.

---

## 🎨 Design System

Serene uses a calming, nature-inspired Material 3 color palette:
* 🟢 **Sage** (`#A2B29F` / `#4A5D4E`) — Primary Calm & Focus Accent
* 🔵 **Mist** (`#C5D3E8`) — Tranquil Sleep & Relaxation Accent
* 🟠 **Sand** (`#EADBC8`) — Warmth & Energy Accent
* 🟣 **Lavender** (`#DAC0DE`) — Reflection & Growth Accent

---

## 🛠️ Tech Stack & Architecture

* **Language**: Java 17
* **Minimum SDK**: API 24 (Android 7.0)
* **Target SDK**: API 35 (Android 15)
* **UI Framework**: Native Android XML Views, Material 3 Components, ViewBinding, ValueAnimator
* **Sensors**: Android `SensorManager`, `Sensor.TYPE_STEP_COUNTER`, `Sensor.TYPE_ACCELEROMETER`
* **Permissions**: `android.permission.ACTIVITY_RECOGNITION`
* **Media**: Android `MediaPlayer` & System Storage Picker (`Intent.ACTION_OPEN_DOCUMENT`)
* **Local Persistence**: `SharedPreferences` (`StepManager`, `AuthManager`)

---

## 🚀 Getting Started

### Prerequisites
* Android Studio Jellyfish / Ladybug or newer
* JDK 17
* Android SDK 35

### Installation
1. **Clone the repository**:
   ```bash
   git clone https://github.com/Sugavanesh12/serene.git
   ```
2. **Open in Android Studio**:
   Open Android Studio and select **Open an existing project**, then select the cloned directory.
3. **Build the project**:
   ```bash
   ./gradlew assembleDebug
   ```
4. **Run on Emulator or Device**:
   Connect an Android device or launch an AVD emulator and click **Run (▶)**.

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
