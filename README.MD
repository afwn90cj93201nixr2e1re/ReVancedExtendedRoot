# How to install.

## By using outputs:

The output was meant to be executed on rooted devices (I personally have only attempted to run it on magisk root) in order to install an aligned and unsigned patched apk.

1. Connect your device to your PC;
2. Run these commands:
- '`adb shell su`';
- In the shell you have to execute the following commands: '`rm -rf /data/adb/service.d`', '`rm -rf /data/adb/post-fs-data.d`', '`rm -rf /data/adb/revanced`', '`reboot`';
- It will reboot your device and unmount (even 'device or resource busy') folders and files (or you can skip this part and just use the unmount script, it is all up to you, since i have used these commands since my apk still was in use, even tho it was force stopped and unmounted (yeah, you've read it right, unmounted...));
3. Either uninstall or downgrade your YouTube application;
- You can simply go to Google Play and tap the Update or/and Install buttons on the YouTube application page if you have either 'revanced' or newer, unsupported versions installed;
- Or you can use the unmount script included in the output, still might require additional downgrade;
4. Install the non-dpi version from the folder with the patched, aligned and unsigned apk you've got built (you already can use '`adb push local dest`' at this point to transfer the file back to your phone, for example: '`adb push C:\Users\user\Desktop\com.google.android.youtube.apk /storage/self/primary/Download/`');
5. Open 2 instances of windows/unix terminals, one of which will be used just for the commands that start with 'adb', the other one should be used for the rest of the commands;
6. Execute commands;
7. Turn off auto-update in Google Play if you need.


An example of build script for building an aligned & unsigned apk:

`
java -jar revanced-cli-v2.21.1.jar -b revanced-patches-v2.173.4.jar -t revanced-cache --experimental -a com.google.android.youtube.apk -o revanced_unsigned.apk -m revanced-integrations-v0.107.4.apk -i bypass-ambient-mode-restrictions -i change-homepage -i custom-double-tap-length -i custom-seekbar-color -i custom-video-speed -i default-video-quality -i default-video-speed -i disable-haptic-feedback -i disable-hdr-video -i disable-quic-protocol -i disable-startup-shorts-player -i enable-external-browser -i enable-minimized-playback -i enable-old-quality-layout -i enable-open-links-directly -i enable-seekbar-tapping -i enable-tablet-miniplayer -i enable-tablet-navigation-bar -i enable-timestamps-speed -i enable-wide-searchbar -i force-vp9-codec -i header-switch -i hide-account-menu -i hide-auto-captions -i hide-auto-player-popup-panels -i hide-autoplay-button -i hide-autoplay-preview -i hide-breaking-news-shelf -i hide-button-container -i hide-captions-button -i hide-cast-button -i hide-category-bar -i hide-channel-avatar-section -i hide-channel-watermark -i hide-collapse-button -i hide-comment-component -i hide-crowdfunding-box -i hide-email-address -i hide-endscreen-cards -i hide-endscreen-overlay -i hide-filmstrip-overlay -i hide-floating-microphone -i hide-flyout-panel -i hide-fullscreen-panels -i hide-general-ads -i hide-info-cards -i hide-live-chat-button -i hide-load-more-button -i hide-mix-playlists -i hide-music-button -i hide-navigation-buttons -i hide-navigation-label -i hide-pip-notification -i hide-player-button-background -i hide-player-overlay-filter -i hide-previous-next-button -i hide-quick-actions -i hide-search-terms -i hide-seek-message -i hide-seekbar -i hide-shorts-component -i hide-shorts-navbar -i hide-snack-bar -i hide-stories -i hide-suggested-actions -i hide-time-stamp -i hide-tooltip-content -i hide-video-ads -i layout-switch -i optimize-resource -i overlay-buttons -i protobuf-spoof -i return-youtube-dislike -i settings -i sponsorblock -i spoof-app-version -i swipe-controls -i theme -i translations -e custom-branding-icon-afn-blue -e custom-branding-icon-afn-red -e custom-branding-icon-mmt -e custom-branding-icon-revancify-blue -e custom-branding-icon-revancify-red -e custom-branding-name -e disable-landscape-mode -e force-hide-player-button-background -e force-premium-heading -e hide-double-tap-overlay-filter -e materialyou -e custom-package-name -e microg-support --mount
`

## By using the attached jar file:

You must have non-dpi/or any other compatible versions before you start.

1. Copy the device name:
```bash
adb devices
```

2. Either uninstall or (and) unmount the existing application:
```bash
java -jar revanced-cli-2.21.2-dev.1-all.jar -d device-name --uninstall -a aligned_unsigned.apk
```

3. Install the fresh one:
```bash
java -jar revanced-cli-2.21.2-dev.1-all.jar -d device-name -a aligned_unsigned.apk --mount
```

