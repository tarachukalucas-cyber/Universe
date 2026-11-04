# Universe Launcher

Universe is an unofficial Android distribution based on the open-source Minecraft: Java Edition launcher runtime from [ZalithLauncher2](https://github.com/ZalithLauncher/ZalithLauncher2), which itself uses PojavLauncher as its launching core.

## Build status and architecture

The repository pins the runtime source as a Git submodule at `runtime/ZalithLauncher2`. The GitHub Actions workflow checks out that exact revision, applies the reproducible Universe branding patch in `scripts/patch_runtime.py`, and builds an ARM64 Android APK. This is the practical route to a real Java-on-Android runtime; the original lightweight Compose prototype in `app/` is retained as an early UI prototype and is not the executable game engine.

The build requires Java 21, Android SDK/NDK, and Rust/cargo-ndk because the upstream runtime includes native components and bundled Java runtimes. The workflow produces a debug APK artifact for ARM64 devices.

## Important

- This is an **unofficial modified version**. It is not affiliated with or endorsed by the upstream project.
- The upstream project's GPL-3.0 license and additional terms apply to this modified distribution. Its copyright and license notices are preserved in the submodule.
- The upstream project's additional terms require modified builds to use a distinct name and display a clear unofficial-modification notice. Universe does both.
- The upstream runtime source is pinned by the submodule commit. The branding changes are reproducibly applied by `scripts/patch_runtime.py`.
- Minecraft game files and Microsoft account authentication remain subject to the official Minecraft/Microsoft services and their terms. Universe does not bundle paid game assets or bypass account ownership.

## Build

Run the **Android launcher build** GitHub Actions workflow. Download the `universe-launcher-arm64-debug` artifact from the completed run and extract the APK.

The APK is built for ARM64 Android devices. It has not been validated on every phone; first launch may require downloading a Minecraft version and signing in with an account that owns Minecraft: Java Edition.
