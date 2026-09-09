# 🚀 APK Banana Bina Android Studio Ke (Cloud Build Methods)

Android Studio install kiye baghair APK banane ke 3 best tareeqay:

---

## ✅ Method 1: GitHub Actions (Sab Se Aasaan - FREE)

Yeh method sab se best hai kyunki yeh free hai aur automatic kaam karta hai.

### Step-by-Step Guide:

#### 1️⃣ GitHub Par Project Upload Karein
```bash
cd /workspace/CallRecorderApp
git init
git add .
git commit -m "Initial commit"
git branch -M main
git remote add origin https://github.com/YOUR_USERNAME/CallRecorderApp.git
git push -u origin main
```

#### 2️⃣ GitHub Actions Automatically Run Hoga
- Jab aap code push karenge, GitHub automatically:
  - JDK setup karega
  - Gradle build run karega
  - APK generate karega
  - Download link dega

#### 3️⃣ APK Download Karein
1. Apne repository par jayein
2. **Actions** tab par click karein
3. Latest workflow run select karein
4. Neeche scroll karke **Artifacts** section mein `call-recorder-app` download karein
5. ZIP extract karein → `app-debug.apk` milega

#### 4️⃣ Manual Trigger (Optional)
- **Actions** tab → "Android CI" workflow → **Run workflow** button dabayein

---

## ✅ Method 2: Command Line Tools (Local PC Par)

Agar aap apne computer par terminal use karna chahte hain:

### Requirements Install Karein:

#### Windows/Mac/Linux:
1. **JDK 17** install karein: https://adoptium.net/
2. **Android SDK Command Line Tools** download karein: https://developer.android.com/studio#command-tools

### Setup Commands:

```bash
# 1. Environment Variables Set Karein
export ANDROID_HOME=$HOME/Android/Sdk
export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin
export PATH=$PATH:$ANDROID_HOME/platform-tools

# 2. Required SDK Components Install Karein
sdkmanager "platform-tools"
sdkmanager "platforms;android-34"
sdkmanager "build-tools;34.0.0"

# 3. Project Directory Mein Jayein
cd CallRecorderApp

# 4. Gradle Wrapper Setup Karein
chmod +x gradlew

# 5. Debug APK Build Karein
./gradlew assembleDebug

# 6. Release APK (Signed) Build Karein
./gradlew assembleRelease
```

### APK Location:
- **Debug**: `app/build/outputs/apk/debug/app-debug.apk`
- **Release**: `app/build/outputs/apk/release/app-release-unsigned.apk`

---

## ✅ Method 3: Online Cloud Build Services

### A) Codemagic (Free Tier Available)
Website: https://codemagic.io/

1. GitHub account se login karein
2. Apna repository select karein
3. Auto-detect Android project
4. Build trigger karein
5. APK download karein

### B) Bitrise (Free for Open Source)
Website: https://www.bitrise.io/

1. Sign up karein
2. Connect GitHub repository
3. Workflow automate karein
4. Build & Download

### C) AppCenter (Microsoft - Free)
Website: https://appcenter.ms/

1. Microsoft account se login
2. New app create karein
3. GitHub connect karein
4. Build configure karein

---

## 📱 APK Install Kaise Karein Phone Mein?

1. APK file phone mein transfer karein (USB, Email, Google Drive)
2. Phone ki **Settings** → **Security** → Enable **"Unknown Sources"**
3. File Manager se APK par tap karein
4. Install par click karein

---

## ⚠️ Important Notes:

### Debug vs Release APK:
| Feature | Debug APK | Release APK |
|---------|-----------|-------------|
| Size | Bada | Chhota (optimized) |
| Speed | Slow | Fast |
| Signing | Auto | Manual (Keystore chahiye) |
| Testing | ✅ Best | ❌ Not recommended |
| Distribution | ❌ No | ✅ Yes |

### Release APK Sign Karne Ke Liye:
```bash
# 1. Keystore Generate Karein
keytool -genkey -v -keystore my-release-key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias my-alias

# 2. gradle.properties Mein Add Karein
STORE_FILE=my-release-key.jks
STORE_PASSWORD=your_password
KEY_ALIAS=my-alias
KEY_PASSWORD=your_key_password

# 3. Build Karein
./gradlew assembleRelease
```

---

## 🎯 Recommended Workflow (Mera Suggestion):

1. **Testing Ke Liye**: GitHub Actions use karein (Method 1)
   - Free hai
   - Automatic hai
   - Har commit par naya APK ban jata hai

2. **Final Release Ke Liye**: 
   - Pehle locally test karein
   - Phir signed release APK banayein
   - Play Store ya direct share karein

---

## 🔧 Troubleshooting:

### Error: `SDK not found`
```bash
export ANDROID_HOME=$HOME/Android/Sdk
export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin
```

### Error: `Permission denied`
```bash
chmod +x gradlew
```

### Error: `Java version mismatch`
```bash
java -version  # Check karein
# JDK 17 hona chahiye
```

---

## 📞 Need Help?

Agar koi step samajh na aaye toh batayein, main detail mein guide karunga!
