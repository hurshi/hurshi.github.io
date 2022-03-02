---
layout: post
title: 树莓派耍耍
catalog: true
subtitle: 初始化树莓派
tags:
    - 操作系统
    - shell
    - rasperry
---

1. 找镜像：

   除了官方在 [raspberry.org](https://www.raspberrypi.org/software/operating-systems/) 中找，还可以在 [ubuntu](https://ubuntu.com/download/raspberry-pi) 中去找镜像，以及 [Ubuntu 全部镜像版本](http://cdimage.ubuntu.com/releases/20.10/release/)

2. [烧录](https://www.raspberrypi.org/documentation/installation/installing-images/mac.md)：

   ```shell
   diskutil list
   diskutil unmountDisk /dev/rdiskN
   sudo dd bs=1m if=path_of_your_image.img of=/dev/rdiskN; sync
   sudo diskutil eject /dev/rdiskN
   ```

3. 连接服务器并初始化：

   ```shell
   ssh ubuntu@192.168.3.188 # default password is 'ubuntu'
   sh -c "$(wget https://raw.github.com/ohmyzsh/ohmyzsh/master/tools/install.sh -O -)"
   ```

4. 固定 IP：

   ```shell
   sudo vi /etc/netplan/50-cloud-init.yaml
   # addresses: [192.168.3.188/24]
   sudo netplan apply
   ```

   