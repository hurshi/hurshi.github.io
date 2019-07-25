---
layout: post
title: Android 文字测量
catalog: false
tags:
    - Android
---

### 文字的对齐

1. 将文字的对齐，必须知道 paint 的属性，如下图所示：([查看示例地址,自己运行下就能搞清楚这些线条了](<https://github.com/hurshi/AndroidFontMetrics>)) ![](/img/posts/text_measure/text-measure.png){:width="50%"}

2. 上图中，除了`baseline`，其他的都是 paint 的成员变量，而`baseline`是由使用者自己定义的，其中 TextView 就有 定义 baseline 成员变量

3. 其中`top`,`bottom`,`ascent`,`descent`都是paint属性，它们的坐标永远都是相对 baseline 而言的（对于 paint 而言，baseline 永远都是0），所以我们在绘制文字(`canvas.drawText`)的时候，Y坐标是对准 baseline 的。想要正确显示文字，在Y坐标上肯定需要一定的偏移量。

4. `paint.getTextBounds()`获取到的是文字的绝对外框，文字的变动（比如`a->b`,`c->A`)都会导致 TextBounds 发生变化，因为`a`和`b`的高度不一样，所以`a`和`b`的 TextBounds 高度也是不一样的，宽度也一样。

5. 文字对齐点为：水平可以`Gravity`指定，默认`Left`; 垂直方向为`Baseline`。如下图红色点为默认对齐点：![](/img/posts/text_measure/text-measure_A.png){:width="30%"}

   **注意：**文字在垂直方向并不是底部对齐的，而是对准文字的`Baseline`的

### 绘制多行自动换行文字

* 使用`StaticLayout`就能解决问题啦，很智能的

* 使用 `paint.breakText(text...)`可以解锁更多自定义姿势，比如图文混排等效果。

* [DEMO](https://github.com/hurshi/Tests/tree/master/TextMeasure) 效果图：

  | ImageTextView                                | HollowTextView                               |
  | -------------------------------------------- | -------------------------------------------- |
  | ![](/img/posts/text_measure/screenshot2.jpg) | ![](/img/posts/text_measure/screenshot1.jpg) |





### 参考

* [HenCoder Android 开发进阶：自定义 View 1-3 drawText() 文字的绘制](https://hencoder.com/ui-1-3/)
* [stackoverflow](https://stackoverflow.com/a/27631737)
* [github: suragch/AndroidFontMetrics](https://github.com/suragch/AndroidFontMetrics)