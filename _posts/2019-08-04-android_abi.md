---
layout: post
title: Android ABI
catalog: false
tags:
    - Android
---

### Android 系统是如何安装 so 文件的？

1. 查看手机支持的架构：

   ```shell
   ✗ adb shell getprop ro.product.cpu.abilist
   ➜ arm64-v8a,armeabi-v7a,armeabi
   ```

2. APK 在安装的时候，会按照上面的顺序查看 apk 中是否有对应的文件夹，找到的话就将该文件夹下的所有 so 文件拷贝到 /data/app/packageName/lib/ 目录下，然后马上就停止查找了。