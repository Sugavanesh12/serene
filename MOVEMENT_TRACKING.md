# 🚶 Mindful Walking & Step Tracking Architecture

This document provides a technical overview of how **Serene** tracks real-time physical walking steps, calculates distance and calories, and manages persistent step metrics across app sessions.

---

## 🏗️ Architecture Overview

Step tracking in Serene is designed to be **accurate, low-power, and background-resilient**, leveraging hardware-level sensors built into modern Android smartphones.

```
┌────────────────────────────────────────────────────────┐
  Android Smartphone Hardware Sensors                   
  • Primary: Sensor.TYPE_STEP_COUNTER (Pedometer)       
  • Backup:  Sensor.TYPE_ACCELEROMETER (Motion Vector)   
└──────────────────────────┬─────────────────────────────┘
                           │
                           ▼
┌────────────────────────────────────────────────────────┐
  Runtime Permission Layer                                
  • android.permission.ACTIVITY_RECOGNITION (API 29+)    
└──────────────────────────┬─────────────────────────────┘
                           │
                           ▼
┌────────────────────────────────────────────────────────┐
  Movement Tracker Activity (MovementActivity.java)      
  • SensorEventListener (onSensorChanged)                
  • Real-time UI updates & ValueAnimator interpolations  
└──────────────────────────┬─────────────────────────────┘
                           │
                           ▼
┌────────────────────────────────────────────────────────┐
  Persistent Storage Layer (StepManager.java)             
  • SharedPreferences ("SereneStepsDB")                  
  • Stores totalRealSteps & initialHardwareSteps         
└────────────────────────────────────────────────────────┘
```

---

## 📡 1. Sensor Integration & Fallback Mechanism

Serene uses a dual-sensor strategy to ensure step tracking works reliably across different devices and emulators:

### Primary Sensor: `Sensor.TYPE_STEP_COUNTER`
- **Type**: Hardware Pedometer
- **Behavior**: Returns the total accumulated step count since the device was last booted.
- **Power Efficiency**: Extremely low power consumption because processing happens on dedicated sensor hub microcontrollers.
- **Delta Calculation**:
  ```java
  int hardwareSteps = (int) event.values[0];
  int initial = StepManager.getInitialHardwareSteps(context);
  if (initial == 0) {
      initial = hardwareSteps;
      StepManager.saveInitialHardwareSteps(context, initial);
  }
  int deltaSteps = Math.max(0, hardwareSteps - initial);
  ```

### Backup Sensor: `Sensor.TYPE_ACCELEROMETER`
- **Type**: 3-Axis Accelerometer (`x`, `y`, `z`)
- **Behavior**: Measures physical acceleration magnitude vector:
  $$\text{Magnitude} = \sqrt{x^2 + y^2 + z^2}$$
- **Step Detection Threshold**: Detects step impacts when the magnitude delta exceeds `12.0 m/s²`, filtering out slight phone tilts or screen launches:
  ```java
  double delta = Math.abs(magnitude - lastAccelMagnitude);
  if (delta > 12.0) {
      currentSteps++;
      StepManager.saveSteps(context, currentSteps);
  }
  ```

---

## 🔒 2. Runtime Permissions (`ACTIVITY_RECOGNITION`)

Android 10 (API level 29) introduced mandatory privacy protection for physical activity recognition. Serene declares and requests this permission at runtime:

### AndroidManifest.xml Declaration:
```xml
<uses-permission android:name="android.permission.ACTIVITY_RECOGNITION" />
<uses-permission android:name="android.permission.BODY_SENSORS" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
```

### Runtime Request (`MovementActivity.java`):
```java
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
```

---

## 📐 3. Mathematical Formulas & Metrics

Serene converts step counts into meaningful physical activity metrics in real time:

| Metric | Formula | Example (1,000 Steps) |
| :--- | :--- | :--- |
| **Distance** | $\text{Steps} \times 0.00075\text{ km}$ (0.75m average stride length) | **0.75 km** |
| **Active Calories** | $\text{Steps} \times 0.04\text{ kcal}$ | **40 kcal** |
| **Pace** | Calculated dynamically in $\text{min/km}$ | **11'20" / km** |

---

## 🏆 4. Daily Goal & Achievement Engine

Serene features an interactive daily goal system with dynamic visual feedback:

- **Target Options**: 3,000 steps (Light Walk), 5,000 steps (Daily Target), 8,000 steps (Active Goal), or 10,000 steps (Mindful Master).
- **Goal Status States**:
  - **In Progress (`🚶`)**: Displays remaining steps needed to achieve daily target (`dailyTarget - currentSteps`).
  - **Goal Achieved (`🎉 SUCCESS`)**: Dynamically transforms the banner into a green celebratory badge (*"Daily Walking Goal Achieved! 🏆"*) when $\text{Steps} \ge \text{Target}$.

---

## 💾 5. Persistent Local Database (`StepManager.java`)

To prevent step counts from resetting to zero or hardcoded defaults upon closing the app, Serene stores all step metrics in Android's `SharedPreferences` (`SereneStepsDB`):

- **New Users**: Start at **0 steps**, **0 sessions**, **0 mins**, and **0 day streak**.
- **Data Persistence**: Accumulated steps persist across app launches, phone restarts, and screen changes.

---

## 📊 6. Analytics Charting

`MovementActivity` includes interactive multi-timeframe analytics charts:
- **Daily View**: Shows walking step totals across days of the week (**Mon–Sun**).
- **Weekly View**: Tracks walking volume across weeks (**W1–W5**).
- **Monthly View**: Tracks long-term walking trends month-by-month (**Jan–Jul**).
