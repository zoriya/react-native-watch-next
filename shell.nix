{pkgs ? import <nixpkgs> {}}: let
  buildToolsVersion = "35.0.0";
  android = pkgs.androidenv.composeAndroidPackages {
    buildToolsVersions = [buildToolsVersion "36.0.0"];
    platformVersions = ["34" "35" "36"];
    cmakeVersions = ["latest" "3.22.1"];
    includeNDK = true;
    ndkVersions = ["latest" "27.1.12297006"];
  };
  jdk = pkgs.jdk17_headless;
in
  pkgs.mkShell rec {
    packages = with pkgs; [
      bun
      nodejs
      biome
      jdk
      android.androidsdk
    ];

    ANDROID_HOME = "${android.androidsdk}/libexec/android-sdk";
    ANDROID_SDK_ROOT = "${android.androidsdk}/libexec/android-sdk";
    ANDROID_NDK_ROOT = "${ANDROID_SDK_ROOT}/ndk-bundle";
    GRADLE_OPTS = "-Dorg.gradle.project.android.aapt2FromMavenOverride=${ANDROID_SDK_ROOT}/build-tools/${buildToolsVersion}/aapt2";
    JAVA_HOME = jdk;
  }
