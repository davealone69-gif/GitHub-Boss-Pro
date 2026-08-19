# Termux commands for GitHub-Boss-Pro

Phone-first workflow. Run these in Termux on Android.

## 1. Clone

```bash
pkg update -y && pkg install -y git openjdk-17 wget unzip
git clone https://github.com/davealone69-gif/GitHub-Boss-Pro.git
cd GitHub-Boss-Pro
```

## 2. Fix corrupt gradle-wrapper.jar (if build says Invalid or corrupt jarfile)

```bash
pkg install -y wget unzip
cd /tmp
wget -q https://services.gradle.org/distributions/gradle-8.9-bin.zip
unzip -q gradle-8.9-bin.zip

mkdir -p /tmp/wrapfix && cd /tmp/wrapfix
echo 'rootProject.name="w"' > settings.gradle.kts
touch build.gradle.kts
echo 'org.gradle.jvmargs=-Xmx512m' > gradle.properties
/tmp/gradle-8.9/bin/gradle wrapper --gradle-version 8.9 --no-daemon

cp /tmp/wrapfix/gradle/wrapper/gradle-wrapper.jar ~/GitHub-Boss-Pro/gradle/wrapper/
cp /tmp/wrapfix/gradlew ~/GitHub-Boss-Pro/
chmod +x ~/GitHub-Boss-Pro/gradlew
cd ~/GitHub-Boss-Pro
./gradlew --version
```

Or from docs:

```bash
curl -fsSL -o gradle/wrapper/gradle-wrapper.jar \
  https://raw.githubusercontent.com/gradle/gradle/v8.9.0/gradle/wrapper/gradle-wrapper.jar
```

## 3. Build debug APK

```bash
chmod +x gradlew
./gradlew assembleDebug --no-daemon
```

APK path:
`app/build/outputs/apk/debug/app-debug.apk`

If SDK location not found:

```bash
echo "sdk.dir=$HOME/android-sdk" > local.properties
```

## 4. Everyday commands

```bash
./gradlew clean assembleDebug --no-daemon
./gradlew :app:compileDebugKotlin --no-daemon
./gradlew installDebug --no-daemon
./gradlew --stop
rm -rf ~/.gradle/caches/ .gradle/
./gradlew clean assembleDebug --no-daemon --refresh-dependencies
./gradlew detekt --no-daemon
```

## 5. Java check

```bash
java -version   # prefer 17
```

## 6. Copy APK to shared storage

```bash
cp app/build/outputs/apk/debug/app-debug.apk /sdcard/Download/GitHub-Boss-Pro-debug.apk
```

## 7. Release bundle (needs keystore secrets)

```bash
./gradlew bundleRelease --no-daemon
```

AAB: `app/build/outputs/bundle/release/app-release.aab`
