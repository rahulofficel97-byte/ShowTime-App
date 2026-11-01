# ShowTime+ Android 🎬

Modern, 100% Hack-Free & Ad-Free native Android streaming application built with **Kotlin**, **Jetpack Compose (Material 3)**, and **AndroidX Media3 (ExoPlayer)**.

---

## 🛡️ Hack-Free & Anti-Tamper Architecture

| Security Feature | How It Protects The App |
| :--- | :--- |
| **SHA-256 Signature Verification** | App verifies its own cryptographic signing hash at startup. If modified and re-signed by an attacker, the app automatically exits. |
| **Dynamic XOR Obfuscation** | MovieBox API secret bytes and HMAC signing keys are scrambled at compile-time and dynamically de-masked in memory. |
| **Hooking & Frida Shield** | Detects unauthorized runtime memory instrumentation (Frida, Xposed, Substrate). |
| **Zero Trackers / Zero Ad Networks** | No AdMob, no telemetry, no tracking. Only clean direct streaming. |
| **Hardware Keystore Storage** | User tokens and membership status encrypted using AES256-GCM via `EncryptedSharedPreferences`. |

---

## ⚡ Key Features

1. **Segmented Chunked Streaming:**
   - Powered by **AndroidX Media3 ExoPlayer**.
   - Progressive HTTP Byte-Range chunking and HLS `.m3u8` segmentation.
   - Low startup delay (begins playback in under 1.5 seconds) without wasting mobile data.
2. **User Authentication & Membership Plans:**
   - Login, Register, and One-Tap Guest Access.
   - Tiered membership model (`Free`, `VIP Gold`, `Ultra 4K VIP`).
   - VIP-gated 4K Ultra HD & HDR streams.
3. **Cinema AMOLED Dark UI:**
   - 120Hz smooth scrolling with Jetpack Compose.
   - Hero trending carousel banner.
   - Categorized horizontal rows (Trending, Blockbusters, Anime & TV Shows).
   - Live instant search with poster cards.
4. **Hardware Video Player:**
   - Fullscreen landscape mode with touch gestures.
   - Picture-in-Picture (PiP) background playback.

---

## 📦 How to Build the APK

### Method 1: GitHub Actions (Automatic Free Cloud Build)
Push the code to your GitHub repository. The `.github/workflows/build-apk.yml` workflow will automatically compile and produce the installable `.apk` file under your repo's **Actions** tab!

### Method 2: Android Studio
1. Open **Android Studio**.
2. Select **Open** and choose the `moviebox-android` folder.
3. Click **Build** > **Build Bundle(s) / APK(s)** > **Build APK(s)**.
4. Transfer the generated `.apk` to your phone and install!
