---
layout: post
title: Android View 的绘制
catalog: false
tags:
    - Android
---

### View 的绘制流程

1. 


### onMeasure 流程

1. 父View 调用每个子View的 measure()  方法，让子 View 自我测量。
2. 父View 根据子 View 给出的尺寸，得出子 View 的位置，并把它们的位置和尺寸保存在 父View。
3. 根据子 View 的位置和尺寸，计算自己的尺寸，并调用 setMeasuredDimension 保存下来
4. `View.resolveSize(int size, int measureSpec)`可以修正尺寸，使之符合父 View 的限定规则。
5. 常规测量在 ViewGroup 中已经提供，比如 `measureChildren`,`measureChildWithMargins()`等，可以用它们快速测量子 View。
6. 有些 ViewGroup 对子 View 的测量可能需要多次，比如在 vertical 布局的宽度是 wrap_contnet的 Linearlayout 中，有一个子 View  的宽度是 match_parent，而 ViewGroup 的宽度需要将所有子 View 测完才能知道，所以 ViewGroup 会直接不测他或者测为了0，而在 ViewGroup 遍历一遍后，再回来测一遍之前无法确定宽度的子 View。

### Layout

1. onLayout 的传入参数注解： 表示当前 View 在父布局中的位置。

   ```java
   * @param changed This is a new size or position for this view
   * @param left Left position, relative to parent
   * @param top Top position, relative to parent
   * @param right Right position, relative to parent
   * @param bottom Bottom position, relative to parent
   */
   void onLayout(boolean changed, int left, int top, int right, int bottom){
   }
   ```

2. layout() 对当前 View 进行布局，onLayout()对子 View 布局，所以 onLayout  只有 ViewGroup才有重写的必要。

