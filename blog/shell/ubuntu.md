### Ubuntu

#### 定时任务 Crontab

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

#### Ubuntu 18.04 启动服务/开机自启

* 参考：[How to Enable or Disable Services in Ubuntu Systemd/Upstart](https://linoxide.com/linux-how-to/enable-disable-services-ubuntu-systemd-upstart/)

* 
    | 描述     | 命令                           |
    | -------- | ------------------------------ |
    | 启动/关闭/重启 服务 | `systemctl start/stop/restart service-name` |
    |查看状态|`systemctl status service-name`|
    |开机启动| `systemctl enable service-name`             |
    |取消开机启动| `systemctl disable service-name`            |
    |开机启动状态|`systemctl is-enabled service-name`|

#### 不可描述

* [n_p_v](https://github.com/hwdsl2/setup-ipsec-vpn)
* [s_s_s](https://github.com/shadowsocks/shadowsocks-libev)

