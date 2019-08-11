---
layout: post
title: Android 图片内存优化
catalog: true
tags:
    - Android
---

### Bitmap 

1. 如何计算一张 Bitmap 在内存中的大小呢？

   ```java
   bitmap.getByteCount();
   ```

2. 一张图片占用的内存和什么有关？

   1. 图片本身的像素尺寸。
   2. 加载的格式，ARGB_8888 每像素占4 Byte, 比 RGB_565 大一倍。
   3. 所放文件夹和手机屏幕dpi的比例，比如放在 mdpi 中的图片，加载在480dpi的设备上，内存占用要乘以480/160，内存占用整整大了3倍，所以这个是**非常重要**的。查看更多有关 [DPI 的资料](https://hurshi.github.io/2018/01/01/android_screen_adapt/)。




### 图片使用最佳实践

1. 如果是单色的图片，优先考虑 IconFont。

2. 如果图片比较简单，考虑使用 Vector Drawable。

3. 图片尽可能复用：

   1. 使用旋转，缩放，动画来复用图片

      ```xml
      <!--一张图片明明旋转下就满足要求了，就不要再创建另一张了-->
      <?xml version="1.0" encoding="utf-8"?>
      <rotate xmlns:android="http://schemas.android.com/apk/res/android"
          android:drawable="@mipmap/ic_launcher"
          android:fromDegrees="90"
          android:toDegrees="90" />
      ```

   2. 使用 Tint 着色器：为 ImageView 添加属性即可改变 ImageView 上图片的显示颜色。

      ```xml
      andorid:tint="@color/colorAccent"
      ```

4. 选用合适的色彩模式，如果不用 alpha 通道，就用 RGB_565，否则用 ARGB_8888。

5. 大图小用用采样，计算合适的 inSampleSize 值，不浪费内存。比如

   > ImageView 的尺寸只有 100\*100，而图片是1080\*500 的，完全没有必要将原图加载进来，可以计算合适的 inSampleSize 来加载图片，不用花“冤枉内存”。

   **使用 Glide**，它已经把这些事情做好了，即使是加载 R.drawable / R.mipmap 中的图片，也推荐使用 Glide 来加载。

6. 小图大用用矩阵（Matrix）：加载小尺寸的图片，通过 Matrix 放大后给再显示。



#### 参考

1. [Android 开发绕不过的坑：你的 Bitmap 究竟占多大内存？](https://mp.weixin.qq.com/s?__biz=MzA3NTYzODYzMg==&mid=403263974&idx=1&sn=b0315addbc47f3c38e65d9c633a12cd6&scene=0&key=41ecb04b05111003d79189315d2ebdda9a5dc312d579a616c9358c3994f94eaf700ba910fb56c37d348fbe317cbce872&ascene=0&uin=NTMyODkxMDE1&devicetype=iMac+MacBookPro12%2C1+OSX+OSX+10.11.2+build(15C50)&version=11020201&pass_ticket=uq%2BZUPewIgxSiSrWWGqLMnd8%2Fy8eclx6vr92bs5s8Q9YVusWCl2cgRirA7iVDRu%2B)
2. [Android性能优化典范 - 第6季](https://mp.weixin.qq.com/s?__biz=MzA3NTYzODYzMg==&mid=2653578016&idx=1&sn=d997d1142bac09e3764c075392468ae5&chksm=84b3b127b3c4383197c7d1cf15ecec44d66a1119b033ae383f9e2126bb1be0abc93416622dc0&scene=21#wechat_redirect)
3. [Android 内存优化总结&实践](https://mp.weixin.qq.com/s/2MsEAR9pQfMr1Sfs7cPdWQ)