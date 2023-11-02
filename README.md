# AnyLauncher
Any Launcher is OK for MIUI!

## Description

Gesture navigation is not available when using third-party launchers on MIUI. This module uses Xposed to allow you to use the system's native gestures without using any third-party gesture navigation applications.

There are actually two gesture navigation systems on MIUI / HyperOS! One is the official implementation of Android and exists in SystemUI, and the other is the implementation of MIUI and exists in MIUI desktop.

The inability to use gestures on third-party desktops is **purely an artificial limitation!** It's actually very simple to make it support gestures, and I'm not sure why the arrogant Xiaomi developers ignored it.

Known defects: There is no animation for backing to the desktop and recent tasks, so it is stiff to use.

To Xiaomi:

Hopefully you can fix this yourself, there are at least two benefits:

1. Reduce resource usage; two sets of gestures run in the system, isn’t it bloated?
2. Compatible with third-party desktops; many domestic ROMs also support it. Why so arrogant?

## 说明

在 MIUI 上使用第三方桌面时，无法使用手势导航。这个模块借助 Xposed 使你可以不借助任何第三方的手势导航应用来使用系统原生的手势。

MIUI / HyperOS 上实际上存在两套手势导航系统！一套是 Android 的官方实现，存在于 SystemUI 中，一套是 MIUI 的实现，存在于 MIUI 桌面中。

第三方桌面无法使用手势**纯粹是人为限制！**实际上要让它支持手势非常简单，我不清楚为何傲慢的小米开发人员置之不理。

已知缺陷：返回桌面和最近任务没有动画，因此使用起来较为生硬。

致小米：

希望你们能够自己修复这个问题，至少有两个好处：

1. 减少资源占用；两套手势运行在系统里，不臃肿吗？
2. 兼容第三方桌面；很多国产 ROM 也都支持，就你傲慢？
