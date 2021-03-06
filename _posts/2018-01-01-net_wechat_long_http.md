---
layout: post
title: Android 微信智能心跳方案
catalog: true
tags:
    - Android
    - http
---

![](/img/posts/wechat-long-http/head.jpg)

>  查看微信[原文](https://mp.weixin.qq.com/s/ghnmC8709DvnhieQhkLJpA)

### 前言
*  在13年11月中旬时，因为基础组件组人手紧张，Leo安排我和春哥去广州轮岗支援。刚到广州的时候，Ray让我和春哥对Line和WhatsApp的心跳机制进行分析。我和春哥抓包测试了差不多两个多礼拜，在我们基本上摸清了Line和WhatsApp的心跳机制后，Ray才告诉我们真正的任务——对微信的固定心跳进行优化，并告诉我们这不是一件容易的事情。于是我和春哥开始构思第一个方案，我们开始想用统计的方法来解决问题，当我们拿着第一个方案和Ray讨论时，发现不能优雅应对Ray的所有提问：1、测试环境的准确性，失败到底是因为网络的特性导致还是因为用户当前的环境变化导致的暂时失败。2、临界值界定，如果方案选中的心跳值是临界值，我们该怎么办。Ray和组件组同事在网络方面有极其丰富的经验，虽然他没有给我们指出明确的方向，但提出的问题帮助我们更快的补齐需要面对的核心问题。这两个问题让我和春哥意识到如果能很好的解决，就可以给出一个比较好的心跳方案。第一个问题我和春哥开始就意识到，第二个问题我们确实在一开始时疏忽了。但直接解决这两个问题确实不容易，这着实让我和春哥迷茫了几天，有两三天在纺园我都没怎么睡着，因为想不到更好的方法。直到有一天思路发生了一些转变，既然最优解比较复杂，为什么不绕过去，使用有损服务理念找次优解呢。让复杂的事情简单化，好了，想到这里突然有一种拨开云雾的感觉。  

* 思路对了，方案就可以做到简单并且可靠，大家可以看到最终的方案是比较简单的，并且效果还挺好的。在方案描述之前大概讲一下减低问题复杂度的方法：
  **a）延迟心跳测试法：**这是测试结果准确的前提保障，我们认为长连接建立后连续三次成功的短心跳就可以很大程度的保证下一次心跳环境是正常的。
  **b）成功一次认定，失败连续累积认定**：成功是绝对的，连续失败多次才可能是失败。
  **c）临界值避免：**我们使用比计算出的心跳稍微小一点的值做为稳定心跳避免临界值。
  **d）动态调整：**即使在一次完整的智能心跳计算过程中，我们没有找到最好的值，我们还有机会来进行校正。

* 当我和春哥想出第二个简单易行的方案后，我们心里就很有底了，去找Ray讨论，Ray听完后一次通过，然后Ray约了Harvey，给Harvey讲完后，Harvey说听起来可以，可以试试。

* 然后就开始动手，分析竞品加确定方案花了差不多两个月。写心跳的主要代码，只花了一天时间，我记得那天是年会后的一天。回过头来再看这个方案花费的时间还是值得的，后来灰度的统计数据显示，70%用户都可以达到我们的心跳上限。
  搞完智能心跳后一段时间在广州没事干，我就跟Ray商量，Ray让我去测试下WebView的性能瓶颈。然后我跟周斯基一起来做这件事，搞完了安卓客户端WebView性能瓶颈测试后，因为怀孕的老婆一个人在深圳，领导就安排我先回深圳了。春哥坚守着把GCM部分完成后才回深圳。

* 等我们的心跳版本正式发布后，一年前我在公司km上分享了智能心跳方案，吸引不少做push的同事加入了讨论，感觉这方面的交流还是很有必要的。

  
---
好了，废话了很多，下面分享一下微信的智能心跳方案细节。
#### 主要目标
本方案的主要目标是，在尽量不影响用户收消息及时性的前提下，根据网络类型自适应的找出保活信令TCP连接的尽可能大的心跳间隔，从而达到减少安卓微信因心跳引起的空中信道资源消耗，减少心跳Server的负载，以及减少部分因心跳引起的耗电。
主要方法是参考WhatsApp和Line中有价值的做法，结合影响TCP连接寿命的因素，实现Android微信后台自适应心跳算法，同时使用GCM作为辅助通道增加新消息通知的可靠性。
##### WhatsApp、Line、微信的Push策略分析
1.  WhatsApp
在不支持GCM的设备上，采用和微信类似的长连接+心跳策略，WIFI和手机网络下的心跳间隔都为4分45秒，心跳5次后，主动断开连接再重连。
在支持GCM的设备上，主要靠GCM来激活WhatsApp，WhatsApp启动后，会建立一个与服务器的长连接，直接通过此长连接发送Push消息，这个长连接10分钟无消息就会主动断掉，且这十分钟内不做心跳，断掉后WhatsApp客户端和它的服务器不再有连接。当有消息时候，服务器发现没有长连接会发送GCM消息，手机收到GCM消息后，会重新建立长连接来收取消息，10分钟无消息会再断开，如此循环。
2. Line
从测试中发现Line在国内、台湾、美国使用了不同的策略。
 1.  *美国（使用GCM）：*
启动时，会保持7分钟心跳（CDMA2000网络）维持长连接半小时，之后主动断开长连接。当有消息时，服务器会发送GCM消息，Line客户端接收到GCM消息后，重新建立长连接，并再次用心跳维持半个小时。
  2. *国内（不使用GCM）：*
在国内，同样帐号在相同网络，不同的手机上测出了两种策略：
     1. 长连接+心跳策略*（在Galaxy S3上使用），心跳间隔WIFI下是3分20秒，手机网络是7分钟。
     2. 轮询策略（在红米和Nexus S上使用），如图2-1所示。与心跳策略的主要区别用红色标出，客户端在长连接建立后也会定时发送请求，Server会回复并且同时关闭长连接。客户端等待轮询间隔T1后再次建立TCP连接。Line会根据手机的活跃状态动态调整T1，调整范围是从最小1分到 最大到2小时半。而长连接存活时间T2比较固定，在WIFI下4分钟，手机网络7分钟。如果在T2时收到新消息会延长T2的时间。
![](/img/posts/wechat-long-http/lunxun.jpg)
图2-1 Line在国内的轮询策略
 3. *台湾（不使用GCM）：*
从IBG同事win和guang提供的测试数据中看到，台湾使用的策略跟国内的轮询策略类似。
4.  *微信*
微信没有使用GCM，自己维护TCP长连接，使用固定心跳。  


##### 心跳典型值

|                  | WhatsApp| Line    | GCM |
| --- |:---:|:---:|:---:|
| WIFI         | 4分45秒   |3分20秒     | 15分钟     |
| 手机网络  | 4分45秒    |   7分钟     | 28分钟     |


##### Line、WhatsApp、微信Push策略的优点

1. 微信：当前心跳间隔比竞品短，所以微信在新消息提醒上会最及时。
2. 使用GCM：Line和WhatsApp使用GCM策略的最大优点就是省电，以及减轻系统负荷（减少后台应用数目）。
3. Line：Line的轮询策略，优点是当Line处于活跃状态时，及时收消息。当Line处于不活跃状态时，省电。


##### Line、WhatsApp微信Push策略的不足
1. 微信当前心跳频率相对竞品较大，在耗电、耗流量，占用信令通道等方面有所影响。
2. Line的轮询策略，导致的问题是消息可能会延迟接收，测试发现最大延迟间隔到2.5小时。
3. WhatsApp和Line使用Push拉起一个定时长连接策略，缺点是要依赖Google的Push服务，如果Google的Push服务不稳定，消息也会延迟接收。
4. 在国内的移动和联通2G网络下，由于运营商的策略，GCM长连接频繁断连，WhatsApp的Push消息很不及时，体验非常差。


##### GCM研究
1. GCM特点
  *  Android2.2以下的手机不支持GCM，2.2到3.0需要安装Google Store并设置Google帐号，4.04及以上版本不需要设置帐号也能支持。
  *  GCM只传递数据（可以传递小于4kb的数据），对这些数据的处理可以全部由开发者控制。
  *  Android应用不需要运行就可以接收消息(通过Android广播)。
  *  GCM不保证发送的消息的顺序，也不保证消息一定能够推送到手机。
2. GCM心跳策略以及存在的问题
  *  用心跳保活长连接，心跳间隔为WIFI下15分钟，数据网络下28分钟。
  *  Google可以改变所有Android设备的心跳间隔值（目前还未改变过）。
  *  GCM由于心跳间隔固定，并且较长，所以在NAT aging-time设置较小的网络（如联通2G，或有些WIFI环境下）会导致TCP长连接在下一次心跳前被网关释放。造成Push延迟接收。
3. GCM的可用性及稳定性
目前测试发现GCM在国内可用性不高，原因有：
  *  Android很多被手机厂商定制化，厂商可能会去掉GCM服务。
  *  Android2.2到3.0之间需要安装Google Store并设置Google帐号。
  *  由于国内2G和移动3G的NAT超时时间都小于GCM心跳时间(28分钟)，TCP长连接必然无法保活，每次都要等28分钟心跳失败重连后才能收到Push。
  *  某些运营商可能限制了5228端口，移动3G/2G下，发现几乎无法连接上GCM服务器，也就无法获得GCM通知，WhatsApp放后台10分钟后，经常很长时间都收不到Push消息。
在美国3G网络下抓包的24小时，GCM的连接极其稳定，24小时内GCM长连接未曾断过，在台湾3G网络下抓包14个小时，GCM连接也只断过一次。WhatsApp用户在此类地区网络下客户端可以获得很及时的Push通知。
在中国电信3G下抓包，大部分时间GCM连接都比较稳定，只会因为偶尔的DHCP造成断连现象，由于频率很低(平均数小时才发生一次)，对Push体验的影响不大。
4. GCM Server类型
GCM提供两种Server模型：
  *  HTTP Server : 使用同步接口发送HTTP请求，一次请求可以发给最多1000个设备。
  *  XMPP Server :使用异步接口发送请求，只支持对单个设备（或同一个用户的多个关联设备发送），发送请求并发数须小于1000，支持设备到云端Server发送数据。需要Google将我们的发送Server加入白名单。


##### 微信可能的改进点探讨
微信Push的优化主要有几个优化点：
  *  公共Push通道
  *  使用GCM Push作为辅助通道
  *  自适应心跳间隔优化
1. 公共Push通道
由于GCM在国内的可靠性很低，现在国内Android上的Push基本上是各自为政，很多软件都自己实现Push。导致手机被经常性的唤醒，耗电耗流量严重。
市面上已经有很多第三方的公共推送服务，大家可以选择一个适合自己应用的推送服务。腾讯也有信鸽和维纳斯组件，大家在选择方案的时候可以对比下。
最终因为我们国内外使用一套方案，并且是辅助公道，所以我们选择使用GCM。
2. 使用GCM Push作为辅助通道
  ​     *  当前使用GCM的成本不大，可以使用GCM作为辅助通道来增加新消息的及时性。
  ​     *  使用GCM作为辅助通道，在支持GCM的设备上微信上传自己的注册GCM ID给微信Server。
  ​     *  微信Server在发现长连接失效的情况下，可以使用GCM 作为辅助通道通知客户端有新消息，客户端收到push通知后做一次sync。
  ​     *  只利用GCM来激活微信，不传递消息的具体数据，要控制给同一设备发送GCM通知的时间间隔(如五分钟)。
3. 自适应心跳间隔优化
      1. 影响TCP连接寿命的因素
在Android下，不管是GCM，还是微信，都是通过TCP长连接来进行Push消息的，TCP长连接存活，消息Push就及时，所以要对影响TCP连接寿命的因素进行研究。
          *  NAT超时
大部分移动无线网络运营商都在链路一段时间没有数据通讯时，会淘汰 NAT 表中的对应项，造成链路中断（*NAT**超时的更多描述见附录6.1*）。NAT超时是影响TCP连接寿命的一个重要因素(尤其是国内)，所以客户端自动测算NAT超时时间，来动态调整心跳间隔，是一个重要的优化点。
          *  DHCP的租期（lease time）
目前测试发现安卓系统对DHCP的处理有Bug，DHCP租期到了不会主动续约并且会继续使用过期IP，这个问题会造成TCP长连接偶然的断连。（*租期问题的具体描述见附录**6.2*）。
          *  网络状态变化
手机网络和WIFI网络切换、网络断开和连上等情况有网络状态的变化，也会使长连接变为无效连接，需要监听响应的网络状态变化事件，重新建立Push长连接。
      2. 心跳范围选择
          *  前后台区分处理：
为了保证微信收消息及时性的体验，当微信处于前台活跃状态时，使用固定心跳。
微信进入后台（或者前台关屏）时，先用几次最小心跳维持长链接。然后进入后台自适应心跳计算。这样做的目的是尽量选择用户不活跃的时间段，来减少心跳计算可能产生的消息不及时收取影响。
          *  后台自适应心跳选择区间：
可根据自身产品的特点选择合适的心跳范围。
      3. 状态转换图
![](/img/posts/wechat-long-http/zhuangtai.jpg)
      4. 自适应心跳算法描述
          *  按网络类型区分计算：
因为每个网络的NAT时间可能不一致。所以需要区分计算，数据网络按subType做关键字，WIFI按WIFI名做关键字。
对稳定的网络，因为NAT老化时间的存在，在自适应计算态的时候，暂设计以下步骤在当前心跳区间逼近出最大可用的心跳。
          *  变量说明：
[MinHeart，MaxHeart]——心跳可选区间。
successHeart——当前成功心跳，初始为MinHeart
curHeart——当前心跳初始值为successHeart
heartStep——心跳增加步长
successStep——稳定期后的探测步长
          *  最大值探测步骤：
![](/img/posts/wechat-long-http/xintiao.jpg)
图4-1 自适应心跳计算流程
自适应心跳计算流程如图4-1所示，经过该流程，会找到必然使心跳失败的curHeart（或者MaxHeart），为了保险起见，我们选择比前一个成功值稍微小一点的值作为后台稳定期的心跳间隔。
影响手机网络测试的因素太多，为了尽量保证测试结果的可靠性，我们使用延迟心跳测试法。在我们重新建立TCP连接后，先使用 短心跳连续成功三次，我们才认为网络相对稳定，可以使用curHeart进行一次心跳测试。图4-2显示了一次有效心跳测试过程。图4-3显示了在没有达到稳定网络环境时，我们会一直使用固定短心跳直到满足三次连续短心跳成功。
使用延迟心跳测试的好处是，可以剔除偶然失败，和网络变化较大的情况（如地铁），使测试结果相对可靠（五次延迟测试确定结论）。同时在网络波动较大的情况，使用短心跳，保证收取消息相对及时。
          *  运行时的动态调整策略(已经按测算心跳稳定值后)
NAT超时值算出来后，在维持心跳的过程中的策略
            *  无网络、网络时好时坏、偶然失败、NAT超时变小：在后台稳定期发生心跳发生失败后，我们使用延迟心跳测试法测试五次。如果有一次成功，则保持当前心跳值不变；如果五次测试全失败，重新计算合理心跳值。该过程如图4-4所示，有一点需要注意，每个新建的长连接需要先用短心跳成功维持3次后才用successHeart进行心跳。
![](/img/posts/wechat-long-http/xintiao2.jpg)
图4-2 后台稳定态动态调整心跳策略
            *  NAT超时变大：以周为周期，每周三将后台稳定态调至自适应计算态，使用心跳延迟法往后探测心跳间隔。
            *  successHeart是NAT超时临界值：因为我们现在选择的是一个比successHeart稍小的值作为稳定值，所以在计算过程中可以避开临界值。当运营商在我们后台稳定期将NAT超时调整为我们当前计算值，那么由于我们每周会去向下探索，所以下一周探测时也可以及时调整正确。
      5. 冗余Sync和心跳
在用户的一些主动操作以及联网状态改变时，增加冗余Sync和心跳，确保及时收到消息。
         * 当用户点亮屏幕的时候，做一次心跳。
         * 当微信切换到前台时，做一次Sync。
         * 联网时重建信令TCP，做一次Sync


##### 可能存在的风险及预防措施
1. DHCP租期因素
  * 问题：根据目前的测试结果显示，安卓不续约到期的IP Bug，会导致TCP连接在不确定的时间点失效，从而会导致一次心跳失败。
  * 预防：统计后台稳定期的心跳成功率，上报给后台。后台可以按地区分网络监控这个指标的波动，并且后台可以根据不同的波动，动态调整某区域特定网络下可选的心跳区间。
2. 其他影响TCP寿命的因素
  * 是否有遗漏的因素？欢迎各位联系我反馈。

##### 附录
1.  附录A——NAT超时介绍
  *  因为 IP v4 的 IP 量有限，运营商分配给手机终端的 IP 是运营商内网的 IP，手机要连接Internet，就需要通过运营商的网关做一个网络地址转换(Network Address Translation，NAT)。简单的说运营商的网关需要维护一个外网 IP、端口到内网 IP、端口的对应关系，以确保内网的手机可以跟 Internet 的服务器通讯。
![](/img/posts/wechat-long-http/ggsn.jpg)
NAT 功能由图中的 GGSN 模块实现
  *  大部分移动无线网络运营商都在链路一段时间没有数据通讯时，会淘汰 NAT 表中的对应项，造成链路中断。下表列出一些已测试过的网络的NAT超时时间(更多数据由于测试条件所限没有测到)：

    |  地区/网络     | NAT超时时间|
    | --- |:---:|
    |中国移动3G和2G| 5分钟 |
    |中国联通2G | 5分钟 |
    |中国电信3G|大于28分钟 |
    |美国3G|大于28分钟 |
    |台湾3G|大于28分钟 |

长连接心跳间隔必须要小于NAT超时时间(aging-time)，如果超过aging-time不做心跳，TCP长连接链路就会中断，Server就无法发送Push给手机，只能等到客户端下次心跳失败后，重建连接才能取到消息。

2.  附录B——安卓DHCP的租期（lease time）问题
目前测试发现安卓系统对DHCP的处理有Bug：
  *  DHCP租期到了不会主动续约并且会继续使用过期IP，详细描述见[传送门](http://www.net.princeton.edu/android/android-stops-renewing-lease-keeps-using-IP-address-11236.html)。这个问题导致的问题表象是，在超过租期的某个时间点（没有规律）会导致IP过期，老的TCP连接不能正常收发数据。并且系统没有网络变化事件，只有等应用判断主动建立新的TCP连接才引起安卓设备重新向DHCP Server申请IP租用。
  *  未到租期的一半时间，安卓设备重新向DHCP Server申请IP租用。从目前测试结果来看，这种现象恢复的比较快。
  *  移动2G/3G，联通2G没有抓到DHCP。
  *  美国3G下抓取24小时，没有抓到DHCP。