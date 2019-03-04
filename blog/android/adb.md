# ADB指令

### 基本命令
* 安装 apk:
   `adb install -r ...apk`

* 清空应用数据：
  `adb shell pm clean com.package.name`

* 导出手机ANR信息

  `adb pull /data/anr/traces.txt ~/Desktop/`

* 

### 奇技淫巧

* 无线调试

  1. 连接设备 `adb connect 192.168.1.1:5555`

  2. 断开连接 `adb disconnect 192.168.1.1:5555`

