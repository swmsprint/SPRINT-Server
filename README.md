# server_dev_settings

## Homebrew install

```bash
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
```

# Java install

## openjdk version "11.0.11"

```bash
brew tap AdoptOpenJDK/openjdk
brew install —cask adoptopenjdk11
```

# IntelliJ IDEA Ultimate

```bash
brew install --cask intellij-idea
```

# Flutter

```bash
brew cask install visual-studio-code #vscode 설치
code --install-extension dart-code.flutter # Flutter 개발에 필요한 Extension 설치
# --------앱스토어에서 Xcode 설치하기------------
# Xcode 설정
sudo xcode-select --switch /Applications/Xcode.app/Contents/Developer
sudo xcodebuild -runFirstLaunch
sudo xcodebuild -license
sudo gem install cocoapods
# Android studio 설치
brew install --cask android-studio.
# --------Flutter SDK 설치-------------
mkdir ~/development
cd ~/development
git clone https://github.com/flutter/flutter.git -b stable
```

## Flutter SDK 경로 설정

```
code ~/.zshrc
```

파일의 제일 하단에 다음의 내용을 추가합니다.

```bash
...
export PATH=$HOME/development/flutter/bin:$PATH
```

## 의존성 설치

```bash
flutter doctor
```

## 다음 에러인 경우 Android Studio를 처음 실행할때 Andorid SDK components를 설치해야함.

```
[✗] Android toolchain - develop for Android devices
    ✗ Unable to locate Android SDK.
      Install Android Studio from: https://developer.android.com/studio/index.html
      On first launch it will assist you in installing the Android SDK components.
      (or visit https://flutter.dev/docs/get-started/install/macos#android-setup for detailed instructions).
      If the Android SDK has been installed to a custom location, please use
      `flutter config --android-sdk` to update to that location.
```

### 이후

```bash
flutter doctor --android-licenses
```

### 그리고 다시 다음을 실행

```bash
flutter doctor
```

### 기타 오류는 여기로..

https://while1.tistory.com/entry/Flutter-android-sdkmanager-not-found-%EC%97%90%EB%9F%AC-%ED%95%B4%EA%B2%B0%ED%95%98%EA%B8%B0

# MariaDB

# Spring

# docker
