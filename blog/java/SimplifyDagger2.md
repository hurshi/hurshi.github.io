# 简化 Dagger2 注入

### 麻烦问题

1. 每个Activity 都要写 Component , 这还是9012年应该的么

### 指导思想

1. 新建 @AutoComponent 类注解
2. 对需要注入的 Class 类上添加 @AutoComponent 注解，自动生成 Component 类
3. 分析被注入的类是 Activity/Fragment/Application, 拦截 onCreate方法，实现自动注入
4. 拦截 onCreate 可以使用 Transform API 实现（应该可以吧）或者 APT

### 更进一步

1. 对 Dagger Android 也进行类似处理
2. 

