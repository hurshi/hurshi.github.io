# 屏幕适配 dp, px, dpi ...

### 概念理理清

* **px**

  * 全称：pixel / 像素

* **dot**

  * 全称：墨点（物理点）

* **ppi**

  * 全称：pixle per inch （图像分辨率）
  * 解释：每英寸包含像素(px)的个数

* **dpi**

  * 全称：dots per inch（打印分辨率）
  * 解释：每英寸包含物理点(dot)的个数

* **dppx**

  * 全称：dot per pixel
  * 解释：每像素包含的像素(px)个数

* **density independence**

  * 全称：独立密度
  * 解释：这是Android中定义的一个值，计算方式为：屏幕像素宽度(px)/最小宽度(dp)，其中“最小宽度”可以在手机的开发者选项中设置。

* **dp**

  * 全称：density-independent pixel (设备独立像素)

  * 解释：在Android中使用的单位，计算方式为：px = dp * density_indenpendence；    

    由此可见,其实dp类似于分辨率(ppi)，都是描述每个单位中包含多少个像素的，只不过ppi是固定的，而dp依据设备动态变化的。



### 混淆区分开

* DPI - PPI 都是啥鬼
  1. DPI面向的是印刷受体，PPI面向的是荧屏
  2. 这篇文章讲的非常好，强烈推荐：[十分钟快速理解DPI和PPI，不再傻傻分不清！](https://www.jianshu.com/p/aaa9fafdbc20)
  3. 在Android中，不必过多计较DPI/PPI,毕竟官网也没有描述清楚，如下所示，既然是`pixels per inch`那不应该是PPI么？
       ```java
       /**
         * The exact physical pixels per inch of the screen in the X dimension.
         */
       public float xdpi;
       ```



### 图片放哪个目录？

* Android官方**并没有说** “320 * 480分辨率的图片要放到mdpi中”这样的话，而是说 “160dpi 的图片要放到mdpi的文件夹中”，见[官网文档](https://developer.android.com/training/multiscreen/screendensities)。

* 但是视觉一般只提供分辨率，不提供给我门dpi,我要怎么放呢？那就需要视觉提供视觉稿的英寸数值inch，然后自己计算dpi，具体计算方法如下，计算结果往上匹配（比如算出来是300dpi,那就放到320dpi对应的xdpi目录中）
  ```dpi = Math.sqrt( widthInPixel * widthInPixel + heightInPixel * heightInPixel) / inch```

* 下面表格为**5.0英寸的设计稿**匹配的

    | 目录     | 倍数   | dpi          | 分辨率        | 描述         |
    | -------- | ------ | ------------ | ------------- | ------------ |
    | ldpi     |        | ~  120dpi    |               |              |
    | **mdpi** | **1x** | **~ 160dpi** | **320 * 480** |              |
    | hdpi     | 1.5x   | ~ 240dpi     | 480 * 800     |              |
    | xdpi     | 2x     | ~ 320dpi     | 720 * 1280    | 主流设计尺寸 |
    | xxdpi    | 3x     | ~ 480dpi     | 1080 * 1920   | 主流设计尺寸 |
    | xxxdpi   | 4x     | ~ 640dpi     |               |              |
    | nodpi    |        |              |               | 不缩放图片   |


### Android 屏幕适配方案

1. Google官方推荐方案，先看看在Android中`DP`这个单位的魔法：

   ```
   // 源码位于：TypedValue#applyDimension
   /**
    * Converts an unpacked complex data value holding a dimension to its final floating
    * point value. The two parameters <var>unit</var> and <var>value</var>
    * are as in {@link #TYPE_DIMENSION}.
    *
    * @param unit    The unit to convert from.
    * @param value   The value to apply the unit to.
    * @param metrics Current display metrics to use in the conversion --
    *                supplies display density and scaling information.
    * @return The complex floating point value multiplied by the appropriate
    * metrics depending on its unit.
    */
   public static float applyDimension(int unit, float value, DisplayMetrics metrics) {
       switch (unit) {
           case COMPLEX_UNIT_PX:
               return value;
           case COMPLEX_UNIT_DIP:
               return value * metrics.density;
           case COMPLEX_UNIT_SP:
               return value * metrics.scaledDensity;
           case COMPLEX_UNIT_PT:
               return value * metrics.xdpi * (1.0f / 72);
           case COMPLEX_UNIT_IN:
               return value * metrics.xdpi;
           case COMPLEX_UNIT_MM:
               return value * metrics.xdpi * (1.0f / 25.4f);
       }
       return 0;
   }
   ```
    这里`applyDimension`传入的是DP，返回的是Px，因为在Android中所有的绘制都是用Px作为单位的，DP就是在这里被转化为DP的，转换的方式也一目了然。

   Google推荐使用`"wrap_content"` 和`"match_parent"`等**相对尺寸**来布局，这样无论在什么屏幕，以及横屏竖屏情况下，都有一个良好的体验。Google新出的`ConstraintLayout`也是这样的设计。

   为适配不同大小的屏幕，Google还有[SmallestWidth适配](https://developer.android.com/training/multiscreen/screensizes#TaskUseSWQuali), 中文也有较详细的介绍[@拉丁吴](https://www.jianshu.com/p/a4b8e4c5d9b0)

2. [今日头条提出了个方案](https://mp.weixin.qq.com/s/d9QCoBP6kV9VSWvVldVVwA),通过修改`Resources#getDisplayMetrics#density`来适配屏幕，利用了上述源码中dp -> px的转换，更改density值，来达到自己控制屏幕适配。看起来很美好，但这种方案其实是和Google的设计相违背的，比如

    |                         Google 方案                          | 今日头条方案 [Github](https://github.com/JessYanCoding/AndroidAutoSize) |
    | :----------------------------------------------------------: | :----------------------------------------------------------: |
    | ![](https://developer.android.com/images/training/layout-hvga.png \| width=300) |                ![](jrtt_layout.jpgwidth=300)                 |

    看出问题来了吧，虽然可以通过其他方式解决问题，但是这始终非官方推荐的设计方式。

3. 今日头条只提出了方案，并没有开源，所以就有大佬依据头条的方案写了开源库[AndroidAutoSize](https://github.com/JessYanCoding/AndroidAutoSize)

4. 头条的方案仍然存在缺陷，就是更改了`Resources#getDisplayMetrics#density`值并不能一劳永逸，系统会时不时地将`density`值复原，比如`activity`从后台恢复到前台时……。于是，又有大佬站出来了，并在其开源库[AndroidUtilCode](https://github.com/Blankj/AndroidUtilCode/issues/758)中实现了，相比于 AndroidAutoSize，主要有2点进步：

   1. 使用`PT`而不是`DP`,完美兼容了老项目。
   2. 将更改`DisplayMetrics`的时机设在`getResource()`，因为基本上所有的调用`DisplayMetrics`的地方都是通过`getResource().getDisplayMetrics`调用的，重写了`getResource()`方法并在其中更改`DisplayMetrics`中的值，保证了每次调用`metrics`之前，都已经更改了里面的值。




##### 参考资料

1. [[无线手册-4] dp、sp、px傻傻分不清楚[完整]](https://zhuanlan.zhihu.com/p/19565895)
2. [十分钟快速理解DPI和PPI，不再傻傻分不清！](https://www.jianshu.com/p/aaa9fafdbc20)
3. [一种极低成本的Android屏幕适配方式@今日头条](https://mp.weixin.qq.com/s/d9QCoBP6kV9VSWvVldVVwA)
4. [今日头条屏幕适配方案终极版，一个极低成本的 Android 屏幕适配方案.@JessYanCoding](https://github.com/JessYanCoding/AndroidAutoSize)
5. [Android 屏幕适配终结者@Blankj](https://blankj.com/2018/12/18/android-adapt-screen-killer/)
