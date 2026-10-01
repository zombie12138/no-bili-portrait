<p align="center">
  <img src="docs/assets/logo.svg" width="144" height="144" alt="No Bilibili Portrait logo">
</p>

<h1 align="center">No Bilibili Portrait</h1>

<p align="center">
  <a href="README.md">简体中文</a> | <a href="README_EN.md">English</a>
</p>

<p align="center"><strong>Open Bilibili portrait videos in the classic video player.</strong></p>

Instead of sending portrait videos to the vertical Story feed, this module opens them like regular videos in Bilibili's classic video player.

## What it does

- Redirects portrait video links from the `story` route to the classic video player.
- Keeps the portrait indicator on home-feed cards while opening the video in the classic player.
- Works automatically once enabled, with no separate configuration.

## Installation and compatibility

Before installing, make sure your Android device is rooted and has a framework compatible with the legacy Xposed API. LSPosed 2.x is recommended ([download](https://lsposed.zip)).

Bilibili version compatibility:

- Standard app `tv.danmaku.bili`: tested with 8.60.0, 8.97.0 and 9.13.0.
- Other versions: may work, but have not been tested individually.
- International app `com.bilibili.app.in`: included in the default scope, but not yet tested.

Installation steps:

1. Download and install the latest APK from [Releases](https://github.com/zombie12138/no-bili-portrait/releases).
2. Enable No Bilibili Portrait in your Xposed framework manager.
3. Set the module scope. Select `tv.danmaku.bili` for the standard app or `com.bilibili.app.in` for the international app.
4. Force stop Bilibili, then open it again.
5. Tap a portrait video and confirm that it opens in the classic video player.

## How it works

Bilibili uses internal links to decide which player should open a video:

- `bilibili://story/...` opens the vertical Story feed.
- `bilibili://video/...` opens the classic video player.

The module intercepts `story` links while routing requests are created and rewrites them as `video` links.

## Troubleshooting and feedback

If the module does not work, first check that your Xposed framework is running, the module is enabled, and the correct scope is selected. Force stop Bilibili and open it again. If necessary, reboot your device.

If the problem persists, open an [issue](https://github.com/zombie12138/no-bili-portrait/issues) and include the module version, Bilibili version, Android version, Xposed framework version, and relevant logs.

## Credits

This project is based on [WankkoRee/Portrait2Landscape](https://github.com/WankkoRee/Portrait2Landscape). Thanks to the original author for the implementation that made this project possible.

## License

This project is licensed under the [GPL-3.0](LICENSE).
