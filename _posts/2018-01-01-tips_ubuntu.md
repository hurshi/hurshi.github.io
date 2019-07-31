---
layout: post
title: Ubuntu 小技巧
catalog: true
tags:
    - 操作系统
    - shell
---

#### 技巧1: 定时任务 Crontab

1. 打开：`sudo crontab -e`

2. 使用规则

    |  m   |     h      | dom  | mon  |         dow         |    command     |
    | :--: | :--------: | :--: | :--: | :-----------------: | :------------: |
    | 分钟 |    小时    |  日  |  月  | 星期(0~6,0表示周日) |      命令      |
    |  0   | 6,11,17,22 | */5  |  *   |          *          | sh autosend.sh |

    | 符号 | 意义                                             |
    | ---- | ------------------------------------------------ |
    | *    | 代表所有的取值范围内的数字；                     |
    | /    | 代表”每”（“*/5”，表示每5个单位）                 |
    | -    | 代表从某个数字到某个数字（“1-4”，表示1-4个单位） |
    | ,    | 分开几个离散的数字；                             |

#### 技巧2: Ubuntu 18.04 启动服务/开机自启

1. systemctl 命令：

    | 描述                | 命令                                        |
    | ------------------- | ------------------------------------------- |
    | 启动/关闭/重启 服务 | `systemctl start/stop/restart service-name` |
    | 查看状态            | `systemctl status service-name`             |
    | 开机启动            | `systemctl enable service-name`             |
    | 取消开机启动        | `systemctl disable service-name`            |
    | 开机启动状态        | `systemctl is-enabled service-name`         |
  
2. 参考自：[How to Enable or Disable Services in Ubuntu Systemd/Upstart](https://linoxide.com/linux-how-to/enable-disable-services-ubuntu-systemd-upstart/)


#### 技巧3: Ubuntu 18.04 启动执行 shell 脚本

1. 新建 shell 脚本，并赋予执行权限 `chmod u+x /path/to/shell/start.sh

2. 在 `/etc/systemd/system`文件夹下新建 `.service` 文件 `start.service`,格式如下：

   ```
   [Unit]
   Description=Auto Start service
   
   [Service]
   ExecStart=/path/to/shell/start.sh
   
   [Install]
   WantedBy=multi-user.target
   ```

3. 开机启动：`sudo systemctl enable first`

4. 参考自：[How do I run a single command at startup using systemd?](https://askubuntu.com/questions/919054/how-do-i-run-a-single-command-at-startup-using-systemd)

#### 技巧4: SSH 超时

  ```
# 客户端设置定时心跳，比如每60秒发一次心跳，失败3次就不再发送
# /etc/ssh/ssh_config
  
ServerAliveInterval 60
ServerAliveCountMax 3
  ```

#### 技巧5: 给 apt 设置代理

```
sudo vi /etc/apt/apt.conf.d/proxy.conf
# paste
Acquire {
  HTTP::proxy "http://127.0.0.1:8080";
  HTTPS::proxy "http://127.0.0.1:8080";
}
```





#### 其他

* [Upgrade Ubuntu 16.04 LTS To Ubuntu 18.04 LTS Server](https://websiteforstudents.com/upgrade-ubuntu-16-04-lts-to-ubuntu-18-04-lts-beta-server/)

* [V@P@N](https://github.com/hwdsl2/setup-ipsec-vpn)

* [S@S](https://github.com/shadowsocks/shadowsocks-libev)

  

