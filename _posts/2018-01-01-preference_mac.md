---
layout: post
title: 偏好设置 — Mac
catalog: true
tags:
    - tools
    - 操作系统
---


* **BetterTouchTool**

  * 网站：https://folivora.ai/
  * 介绍：解锁鼠标键盘各种姿势
  * 我的配置：[偏好设置文件](/assets/bettertouchtool_20181224.json ':ignore')

* **Alfred**
  * 网站：https://www.alfredapp.com/
  * 介绍：提高效率的黑科技
  * 我的配置：
    1. Advanced --> Force Keyboard --> ABC
    2. Workflows: [help ++, shorten_url, youdao_translate](https://github.com/hurshi/AlfredWorkflow)

* **iTerm2**

  * 网站：https://www.iterm2.com/

* **Homebrew**

  * 网站： https://brew.sh/

  * 介绍：安装在`/usr/local/bin/`目录下

  * 我的安装：

    ```
    brew install git
    brew install autojump
    brew install pidcat
    ```

* **Homebrew Cask**
  * 安装：

    ```shell
    brew install caskroom/cask/brew-cask
    ```
    
  * 介绍：安装 `.dmg/.pkg` 应用，解压在`/opt/homebrew-cask/Caskroom`中

  * 我的应用：

    ```shell
    brew cask install BetterTouchTool
    brew cask install iterm2
    brew cask install Alfred
    brew cask install AppCleaner
    brew cask install Archiver
    brew cask install sublime
    brew cask install typora
    
    brew cask install android-studio
    brew cask install postman
    brew cask install intellij-idea
    brew cask install navicat-premium
    brew cask install proxifier
    brew cask install sketch
    
    brew cask install neteasemusic
    brew cask install vox
    brew cask install wechat
    brew cask install QQ
    brew cask install evernote
    ```

* **shell**

  * oh-my-zsh: https://ohmyz.sh/

  * .gitconfig

    ```
    [alias]
        st = status
        co = checkout
        pl = pull
        ps = push
        br = branch
        cm = commit -m
        lg = log --color --graph --pretty=format:'%Cred%h%Creset -%C(yellow)%d%Creset %s %Cgreen(%cr) %C(bold blue)<%an>%Creset' --abbrev-commit
    
    [user]
        name = Hurshi
        email = ihsruh@gmail.com
    
    [color]
    	ui = auto
    
    ```

  * .zshrc

    ```
    alias gc="./gradlew clean"
    alias ga="./gradlew assembleDebug --stacktrace"
    alias gb="./gradlew build --stacktrace"
    alias gca="gc && ga"
    alias gcb="gc && gb"
    alias apk2jar="sh /Users/hurshi/Documents/dev/dex2jar-2.0/d2j-dex2jar.sh  -f "
    alias adl="adb devices -l"
    alias py="python3"
    alias ls="ls -a"
    alias fq="export all_proxy=socks5://127.0.0.1:8889;export http_proxy=http://127.0.0.1:8888;export https_proxy=http://127.0.0.1:8888;"
    alias bfq="unset ALL_PROXY"
    alias brew="fq;brew"
    ```

* 反编译工具
  * apktool
  * dextojar
  * jadx : https://github.com/skylot/jadx

* Chrome

  * Adblock plus 自定义过滤列表（高级 --> 我的过滤列表）

    ```
    zixun.jia.com##.box_shade
    zixun.jia.com##.zxbj-img
    zixun.jia.com##.bottom-click
    blog.csdn.net##.pulllog-box
    blog.csdn.net##.adblock
    dict.youdao.com###ads
    dict.youdao.com##.ads
    baidu.com###content_right
    baidu.com##.cr-offset
    baidu.com##.rrecom-container.rrecom_content_s
    baidu.com##.result-op.xpath-log
    baidu.com##.rrecom-btn.rrecom-btn-hover
    juejin.im##.link
    juejin.im##.extension
    blog.csdn.net###csdn-toolbar
    blog.csdn.net##.csdn-toolbar.tb_disnone
    blog.csdn.net##.recommend-box
    juejin.im##.request-health-alert
    juejin.im##.recommended-area
    juejin.im##.article-banner
    juejin.im##.app-link
    juejin.im##.wechat-banner
    juejin.im##.index-book-collect
    juejin.im##.tag-list-title
    juejin.im##.share-title
    juejin.im##.weibo-btn
    juejin.im##.qq-btn
    juejin.im##.wechat-btn
    juejin.im##.alert.error
    juejin.im##.box
    ||gitee.com/assets/weixin/button-banner.png
    ||gitee.com/assets/weixin/close.png
    ```

  * OneTab: https://www.one-tab.com/

  * SourceGraph: https://sourcegraph.com

  * Octotree: https://github.com/ovity/octotree

  * Vimium:[https://chrome.google.com](https://chrome.google.com/webstore/detail/vimium/dbepggeogbaibhgnhhndojpepiihcmeb?hl=en)

* 截图

  * [xnip](https://xnipapp.com/)：支持滚动截图

* 下载

  * [Motrix](https://github.com/agalwood/Motrix)

* 音乐

  * [Listen1](https://github.com/listen1/listen1)

    