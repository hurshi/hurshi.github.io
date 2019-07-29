---
layout: post
title: Android RecyclerView 知识点补充
catalog: false
tags:
    - Android
    - 笔记
---



<div class="mermaid">
graph TD
A[RecyclerView] --> B(LayoutManager)
A --> C(Item Animator)
A --> D(Adapter)
</div>

#### ListView 相对 RecyclerView 的局限

1. 只有纵向列表一种布局（RecyclerView 通过自定义 LayoutManager 还可解锁更多姿势）
2. 没有支持动画的 API
3.  接口设计不够明确，比如 RecyclerView 的 onCreateViewHolder 和 onBindViewHolder 体现在 ListView 中只有 getView。
4. 并没有强制实现 ViewHolder
5. 性能不如 RecyclerView

#### ViewHolder 究竟是什么？

1. ViewHolder 和 itemView 是什么关系？一对一，一对多，多对多？

   > 一对一

2. ViewHolder 解决了什么问题？

   > 防止重复执行 findViewById，以提升效率

ViewHolder 和 **复用**没有必然关系，拿 ListView  来举例，即使不用 ViewHolder，只是如下代码，也已经起到了**复用**的效果
```java
public View getView(int position, View convertView, VireGroup parent){
	//只在 convertView 为 null时才创建，就是复用了View
	if(convertView == null){
		convertView = LayoutInflater.from...
	}
}
```



#### ListView 缓存机制

<div class="mermaid">
graph LR
A[ListView] --> B(RecycleBin)
A --> C(Create View)
B --> D(Active View)
D -.-> B
B --> E(Scrap View)
E -.-> B
</div>
* **ActiveView** 指的是当前显示区域的 Item 的缓存，为什么要缓存这些呢？是因为按照 Android   每16.6ms刷新一次，在此期间就会用到 ActiveView 的缓存。这里的数据是干净的，是不用重新绑定数据的。
* **Scrap View** 指的是不在当前显示区域的 Item 缓存，表示已经滑出去了的 Item，数据是 dirty 的。
* ListView 缓存的对象是 View 对象

#### RecyclerView缓存机制

<div class="mermaid">
graph LR
A[LayoutManager] --> B(Recycler)
B --> C(Scrap)
C -.-> B
B --> D(Cache)
D -.-> B
B --> E(ViewCacheExtension)
E -.-> B
B --> F(RecycledViewPool)
F -.-> B
B --> G(Create View)
G -.-> B
</div>
* **Scrap** 指的是当前显示区域内的 Item 缓存，类似于 ListView 的 ActiveView
* **Cache** 指的是**刚刚**被移出屏幕的 Item，一般默认缓存2个，数据干净的，能直接复用。用于用户在**回滑**的时候提高性能。
* **ViewCacheExtension**: 用户自定义的缓存策略。
* **RecycledViewPool**: 类比于 ListView 的 ScrapView，这里面的数据是 dirty 的，需要重新绑定数据。
* RecyclerView 缓存的对象是 RecyclerView.ViewHolder 对象

#### RecyclerView  的性能优化策略

1. Item ClickListener 在 `onCreateViewHolder()`中设定，避免在`onBindViewHolder()`中设定。

2. LinearLayoutManager.setInitialPrefetchItemCount() 初始化 RecyclerView 时候初始化 Item 个数。

3. `RecyclerView.setHasFixedSize()`如果Adapter 的数据变化不会导致 RecyclerView 的大小变化，可以使用`RecyclerView.setHasFixedSize(true)`

   ```java
   //伪代码
   void onContentsChanged() {
   	if(mHasFixedSize) {
   		layoutChildren();
   	} else {
   		requestLayout();
   	}
   }
   ```

4. **很简单很厉害：**多个 RecyclerView 共用 RecycledViewPool：

   ```java
   RecyclerView.RecycledViewPool recycledViewPool = new RecyclerView.RecycledViewPool();
   recyclerView1.setRecycledViewPool(recycledViewPool);
   recyclerView2.setRecycledViewPool(recycledViewPool);
   recyclerView3.setRecycledViewPool(recycledViewPool);
   ```

5. **DiffUtil：**适用于整个页面需要更新，但是有部分数据是相同的。

   ```kotlin
   // Step1: 非必须，只有当Item内部有属性更新的时候需要重写
   // Adapter中重写 onBindViewHolder(holder: Any, position: Int, payloads: MutableList<Any>)方法
   class Adapter() : RecyclerView.Adapter<Any>() {
       override fun onCreateViewHolder(p0: ViewGroup, p1: Int): Any {}
       override fun getItemCount(): Int {}
       override fun onBindViewHolder(p0: Any, p1: Int) {}
   
       //当Item内部有变化的时候，调用这个方法
       override fun onBindViewHolder(holder: Any, position: Int, payloads: MutableList<Any>) {
           if(null == payloads || payloads.isEmpty()) onBindViewHolder(holder,position)
           else{
               //解析 payloads,将数据更新到holder中去
               val bundle = payloads[0] as Bundle
               bundle.keySet().forEach {key -> 
                   when(key){
                       User.KEY_NAME -> holder.name.text = bundle.get(key)
                   }
               }
           }
       }
   }
   
   //Step2: 定义 DiffUtil.Callback
   class CustomDiffCallback() : DiffUtil.Callback() {
       override fun areItemsTheSame(p0: Int, p1: Int): Boolean {}
       override fun getOldListSize(): Int {}
       override fun getNewListSize(): Int {}
       override fun areContentsTheSame(p0: Int, p1: Int): Boolean {}
       //更新这个Item的其中一部分,非必须写的方法
       override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
           val oldUser = oldList[oldItemPosition]
           val newUser = newList[newItemPosition]
           val payload = Bundle()
           if (oldUser.id != newUser.id) {
               payload.putLong(User.KEY_ID, newUser.id)
           }
           if (oldUser.name != newUser.name) {
               payload.putString(User.KEY_NAME, newUser.name)
           }
           if (payload.size() <= 0) return null
           return payload
       }
   }
   
   //Step3: 更新 List
   //这里的calculateDiff方法的第二个参数，true表示考虑列表数据位置变化，复杂度会变为O(n²)
   val diffResult = DiffUtil.calculateDiff(CustomDiffCallback(newList, oldList), false)
   diffResult.dispatchUpdatesTo(mAdapter)
   ```

   * [AsyncListDiffer](https://developer.android.com/reference/androidx/recyclerview/widget/AsyncListDiffer): DiffUtil 后台执行方案
   * [RecyclerView.ListAdapter](https://developer.android.com/reference/androidx/recyclerview/widget/ListAdapter)

#### 其他

* `onViewAttachedToWindow():`Item显示到屏幕上的时候回调
* `onViewDetachedFromWindow():`Item 离开屏幕时候回调，比较适合资源回收，`setRecycleChildrenDetach(true)`确保页面退出的时候，调用`onViewDetachedFromWindow()`方法。
* [ItemDecoration深入解析与实战](https://www.jianshu.com/p/f41db270d5fe)
* 拓展：[github.com/h6ah4i/android-advancedrecyclerview](https://github.com/h6ah4i/android-advancedrecyclerview) (RecyclerView的各种教程，Demo，拖拽，滑动等等)

#### Thanks for

1. HenCoder Plus
2. jiaheng







