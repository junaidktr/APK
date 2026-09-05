# Call Recorder Application - Project Structure

## Overview
This is a complete Android application structure for recording phone calls and WhatsApp calls. The application uses modern Android architecture with Kotlin, Room Database, and Foreground Services.

## ⚠️ Important Notes

### Legal Disclaimer
- **Call recording laws vary by country/region**. In many places, you need consent from all parties before recording calls.
- **WhatsApp calls are encrypted end-to-end**. This app uses accessibility services to detect when WhatsApp calls are active and records ambient audio through the microphone.
- **Android 10+ restrictions**: Google has restricted call recording capabilities. This app works within Android's limitations.

### Technical Limitations
1. **Regular Phone Calls**: Recording quality depends on device manufacturer and Android version
2. **WhatsApp Calls**: Uses accessibility service to detect call state and records through microphone (not direct audio capture)
3. **VoIP Calls**: Similar limitations as WhatsApp calls

## Project Structure

```
CallRecorderApp/
├── app/
│   ├── src/main/
│   │   ├── java/com/example/callrecorder/
│   │   │   ├── activities/          # UI Activities
│   │   │   │   ├── MainActivity.kt           # Main screen with permissions
│   │   │   │   ├── RecordingListActivity.kt  # List all recordings
│   │   │   │   ├── PlayerActivity.kt         # Play recordings
│   │   │   │   └── SettingsActivity.kt       # App settings
│   │   │   │
│   │   │   ├── database/            # Room Database
│   │   │   │   ├── AppDatabaseSingleton.kt   # Database instance
│   │   │   │   ├── DatabaseProvider.kt       # Database provider
│   │   │   │   └── RecordingDao.kt           # Data Access Object
│   │   │   │
│   │   │   ├── models/              # Data Models
│   │   │   │   └── Recording.kt              # Recording entity
│   │   │   │
│   │   │   ├── receivers/           # Broadcast Receivers
│   │   │   │   ├── PhoneStateReceiver.kt     # Detect call states
│   │   │   │   └── BootCompletedReceiver.kt  # Start on boot
│   │   │   │
│   │   │   ├── services/            # Background Services
│   │   │   │   ├── CallRecordingService.kt   # Main recording service
│   │   │   │   └── WhatsappAccessibilityService.kt  # WhatsApp detection
│   │   │   │
│   │   │   ├── utils/               # Utility Classes
│   │   │   │   ├── AudioRecorder.kt          # Audio recording logic
│   │   │   │   ├── PermissionUtils.kt        # Permission handling
│   │   │   │   └── RecordingConstants.kt     # Constants
│   │   │   │
│   │   │   └── CallRecorderApplication.kt    # Application class
│   │   │
│   │   ├── res/                     # Resources
│   │   │   ├── layout/              # XML Layouts
│   │   │   ├── values/              # Strings, Colors, Themes
│   │   │   ├── drawable/            # Icons
│   │   │   └── xml/                 # Accessibility config
│   │   │
│   │   └── AndroidManifest.xml      # App manifest
│   │
│   └── build.gradle                 # App-level build config
│
├── build.gradle                     # Project-level build config
├── settings.gradle                  # Project settings
└── README.md                        # This file
```

## Key Components

### 1. CallRecordingService
- Foreground service that handles audio recording
- Starts automatically when call is detected
- Saves recordings with metadata (phone number, duration, date)

### 2. PhoneStateReceiver
- Listens for incoming/outgoing call events
- Triggers recording service on call answer
- Stops recording on call end

### 3. WhatsappAccessibilityService
- **Requires manual enablement** in Accessibility Settings
- Monitors WhatsApp window states
- Detects call screens and buttons
- Automatically starts/stops recording

### 4. AudioRecorder
- Handles MediaRecorder initialization
- Manages file storage
- Supports multiple audio formats

### 5. Room Database
- Stores recording metadata
- Provides CRUD operations
- Enables search and filtering

## Required Permissions

```xml
- RECORD_AUDIO - Record audio during calls
- READ_PHONE_STATE - Detect incoming/outgoing calls
- READ_CALL_LOG - Get caller information
- POST_NOTIFICATIONS - Show recording notification (Android 13+)
- FOREGROUND_SERVICE - Run recording service
- BIND_ACCESSIBILITY_SERVICE - Detect WhatsApp calls
- READ_MEDIA_AUDIO / WRITE_EXTERNAL_STORAGE - Save recordings
```

## How to Build

### Prerequisites
- Android Studio Arctic Fox or later
- JDK 17
- Android SDK 34

### Steps
1. Open project in Android Studio
2. Sync Gradle files
3. Connect Android device or start emulator
4. Run the app

### Building APK
```bash
./gradlew assembleDebug
# or
./gradlew assembleRelease
```

## Usage Instructions

### First Time Setup
1. Launch the app
2. Grant all required permissions
3. Enable Accessibility Service for WhatsApp call detection:
   - Go to Settings > Accessibility
   - Find "Call Recorder" service
   - Enable it

### Recording Regular Calls
- Automatic - no action needed
- App detects call state and records automatically

### Recording WhatsApp Calls
- Ensure Accessibility Service is enabled
- Make/receive WhatsApp call as normal
- App will detect and record automatically

### Viewing Recordings
- Open app → View Recordings
- Play, delete, or share recordings
- Search by contact or date

## Features

✅ **Automatic Call Detection**
✅ **WhatsApp Call Recording** (via accessibility)
✅ **Background Recording**
✅ **Recording Management** (play, delete, share)
✅ **Contact Information Display**
✅ **Search and Filter**
✅ **Auto-start on Boot**
✅ **Modern Material Design UI**
✅ **Dark Theme Support**

## Architecture

- **MVVM Pattern** (Model-View-ViewModel)
- **Room Database** for local storage
- **Coroutines & Flow** for async operations
- **ViewBinding** for type-safe view access
- **Material Design 3** components

## Testing

Run unit tests:
```bash
./gradlew test
```

Run instrumented tests:
```bash
./gradlew connectedAndroidTest
```

## Troubleshooting

### Recording not working?
1. Check all permissions are granted
2. Ensure microphone permission is allowed
3. Some devices block call recording (Xiaomi, Huawei, etc.)

### WhatsApp calls not detected?
1. Enable Accessibility Service manually
2. Keep WhatsApp updated
3. Service may need time to learn UI patterns

### Poor audio quality?
- Try different audio source in settings
- Use speakerphone during calls
- Device hardware limitations

## License

This project is for educational purposes only. Users must comply with local laws regarding call recording.

## Support

For issues and feature requests, please check:
- Android documentation
- Device-specific limitations
- Local legal requirements

---

**Created**: 2024
**Target SDK**: Android 14 (API 34)
**Minimum SDK**: Android 7.0 (API 24)
**Language**: Kotlin
