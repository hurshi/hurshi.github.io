---
layout: post
title: 偏好设置 — Mac
catalog: true
tags:
    - tools
    - 操作系统
---

### Preferences

1. 关闭自动纠错

   系统偏好设置 ==> 键盘 ==> 文本 ==> 关闭自动纠正拼写

2. Active Screen Corners

   | Put Display to Sleep |      Launchpad      |
   | :------------------: | :-----------------: |
   |     **Desktop**      | **Mission Control** |

### Softwares

* **BetterTouchTool**

  * 网站：https://folivora.ai/
  * 介绍：解锁鼠标键盘各种姿势
  * 我的配置：[偏好设置文件](/assets/bettertouchtool_20201103.json ':ignore')

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
        pl = pull --rebase
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
    # git alias:
    alias grst="git reset --hard"
    alias gpl="git pull"
    alias gps="git push"
    alias gpls="gpl && gps"
    
    #gradle alias:
    alias gs="./gradlew --stop"
    alias gc="./gradlew clean"
    alias ga="./gradlew assembleDebug --stacktrace"
    alias gfa="./gradlew assembleFullDebug --stacktrace"
    alias gca="gc && ga && gs"
    alias gcfa="gc && gfa && gs"
    
    # repo alias:
    alias rc="repo forall -c"
    alias rcrst="rc git reset --hard"
    alias rcpl="rc git pull"
    alias rcps="rc git push"
    alias rcst="rc git status"
    alias rcstpl="rcrst;rcpl"
    
    # other alias:
    alias ls="ls -a"
    alias op="open ./"
    alias py="python3"
    alias ft="flutter"
    alias adl="adb devices -l"
    alias fg="fastddsgen"
    alias apk2jar="sh /Users/hurshi/Documents/dev/dex2jar-2.0/d2j-dex2jar.sh  -f "
    
    export LSCOLORS="GxFxCxDxBxegedabagaced"
    export CLICOLOR=1
    alias ls='ls -G'
    ```

* 反编译工具
  * apktool
  * dextojar
  * jadx : https://github.com/skylot/jadx

* Chrome

  * Adblock plus 自定义过滤列表（高级 --> 我的过滤列表）

    ```
    ||gitee.com/assets/weixin/close.png
    ||gitee.com/assets/weixin/button-banner.png
    zixun.jia.com##.zxbj-img
    zixun.jia.com##.box_shade
    zixun.jia.com##.bottom-click
    juejin.im##.weibo-btn
    juejin.im##.wechat-btn
    juejin.im##.wechat-banner
    juejin.im##.tag-list-title
    juejin.im##.share-title
    juejin.im##.request-health-alert
    juejin.im##.recommended-area
    juejin.im##.qq-btn
    juejin.im##.link
    juejin.im##.index-book-collect
    juejin.im##.extension
    juejin.im##.box
    juejin.im##.article-banner
    juejin.im##.app-link
    juejin.im##.alert.error
    dict.youdao.com##.ads
    dict.youdao.com###ads
    blog.csdn.net##.recommend-box
    blog.csdn.net##.pulllog-box
    blog.csdn.net##.csdn-toolbar.tb_disnone
    blog.csdn.net##.csdn-side-toolbar
    blog.csdn.net##.adblock
    blog.csdn.net###csdn-toolbar
    baidu.com##.rrecom-container.rrecom_content_s
    baidu.com##.rrecom-btn.rrecom-btn-hover
    baidu.com##.result-op.xpath-log
    baidu.com##.cr-offset
    baidu.com###content_right
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

  


  
