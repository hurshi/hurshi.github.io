---
layout: post
title: Android 图片优化
catalog: true
tags:
    - Android
---

### Bitmap 

1. 如何计算一张 Bitmap 在内存中的大小呢？

   ```java
   bitmap.getByteCount();
   ```

2. 一张图片在占用的内存和什么有关？

   1. 图片本身的像素尺寸
   2. 加载的格式，ARGB8888 每像素占4 Byte, 比 RGB565 大一倍。
   3. 所放文件夹和手机屏幕dpi的比例，比如放在 mdpi 中的图片，加载在480dpi的设备上，内存占用要乘以480/160，内存占用整整大了3倍，所以这个是**非常重要**的。关于 [dpi 的资料可以点我](https://hurshi.github.io/2018/01/01/android_screen_adapt/)。

3. 











#### 参考

1. [Android 开发绕不过的坑：你的 Bitmap 究竟占多大内存？](https://mp.weixin.qq.com/s?__biz=MzA3NTYzODYzMg==&mid=403263974&idx=1&sn=b0315addbc47f3c38e65d9c633a12cd6&scene=0&key=41ecb04b05111003d79189315d2ebdda9a5dc312d579a616c9358c3994f94eaf700ba910fb56c37d348fbe317cbce872&ascene=0&uin=NTMyODkxMDE1&devicetype=iMac+MacBookPro12%2C1+OSX+OSX+10.11.2+build(15C50)&version=11020201&pass_ticket=uq%2BZUPewIgxSiSrWWGqLMnd8%2Fy8eclx6vr92bs5s8Q9YVusWCl2cgRirA7iVDRu%2B)
2. [Android性能优化典范 - 第6季](https://mp.weixin.qq.com/s?__biz=MzA3NTYzODYzMg==&mid=2653578016&idx=1&sn=d997d1142bac09e3764c075392468ae5&chksm=84b3b127b3c4383197c7d1cf15ecec44d66a1119b033ae383f9e2126bb1be0abc93416622dc0&scene=21#wechat_redirect)
3. [Android 内存优化总结&实践](https://mp.weixin.qq.com/s/2MsEAR9pQfMr1Sfs7cPdWQ)