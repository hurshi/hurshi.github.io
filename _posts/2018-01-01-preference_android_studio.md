---
layout: post
title: 偏好设置 — AndroidStudio
catalog: true
tags:
    - Android
    - tools
---


### 快捷键
`Preferences -> keymap -> `

| 命令                     | name                     | 描述             |
| ------------------------ | ------------------------ | ---------------- |
| `command + d`            | `delete line`            | 删除一行         |
| `command + shift + F`    | `reformat code`          | 格式化代码       |
| `command + shift + R`    | `File...`                | 切换代码文件     |
| `control + R`            | `Replace in Path...`     | 全局替换         |
| `command + shift + o`    | `Optimize Imports...`    | 删除无用`import` |
| `alt + command + 方向下` | `duplicate entire lines` | 复制选中行到下面 |
| `command + shift + g`    | `Find Usages`            | 搜索引用         |
|`option + enter`||快速提示|
|`command + F12`||查看当前代码文件大纲|
|`F3`||添加/删除 书签|
|`command + F3`||查看所有书签|
|`command + y`||预览光标所在的方法|
|`command + option + T`||`surround with`|

### 代码模版
添加模版 `Preferences -> Live Templates ->`
1. `tmb : Timber.e(">>> $name$"+$value$);`
2. `loge : android.util.Log.e(" >>> ", "$METHOD_NAME$: $content$" + $exception$);`

### 字体
1. 更改字体
`Preferences -> Appearance`
2. 字体推荐
[`inconsolata`](https://github.com/google/fonts/blob/master/ofl/inconsolata/Inconsolata-Regular.ttf)

### 常用设置
1. **设置`command + 左键`和`control + 左键`同样功能**
  `Preferences -> Keymap -> Search for "Declaration" -> double click on the search result under the  'Navigate' category ->
     select "add mouse shortcut" -> and press the shortcut buttons (eg. CMD+left click) and save it`

2. **提示不区分大小写，因此我们要改成大小写不敏感**
  `Preferences -> code completion -> case sensitive completion: none;`

3. **自动`import`**
   `Preference -> auto import`

4. **代码提示(alt + /)**
   `Preference -> Keymap -> Completion -> Basic`

5. **鼠标悬浮文档提示**

   `Editor -> General -> Show quick documention on mouse move`

6. **自定义VM Options**

   ```shell
   # custom Android Studio VM options, see https://developer.android.com/studio/intro/studio-config.html
   -Xmx2g
   -Xms1g
   -XX:MaxPermSize=2g
   -XX:ReservedCodeCacheSize=1g
   ```

7. **显示内存信息**

   `Preference -> Appearance -> Show memory indicator`


### 断点

1. 异常断点

   `Run -> View breakpoints` 

2. 日志断点

   `添加一个普通断点 -> suspend设为false -> Log evaluated expression`

### 代码分析

1. `Analyze -> Inspect Code...`
2. `Analyze -> Code Cleanup...`
