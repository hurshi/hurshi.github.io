---
layout: post
title: Http 缓存
catalog: true
tags:
    - http
---

### Cache-Control
* ~~Expired：仅在 HTTP/1.0 使用；~~
* **max-age**：缓存的最长“新鲜”时长；
* s-maxage: 会覆盖 max-age 以及 Expires 数据（但仅对 shared/public 缓存有效）；
* **max-stale**: 缓存的最长保留时长，当缓存不在新鲜，会从网络获取新鲜数据，但是当获取数据发生异常，会获取还在 max-stale 时长内的数据，超过 max-stale 时长的数据会被删除抛弃；
* **no-store**：不使用缓存；
* **only-if-cached**：只使用缓存，若没有缓存则返回 503错误；


### Etag / If-None-Match 策略
<div class="mermaid">
sequenceDiagram
	rect rgba(100,200,100,.4)
		客户端->>+服务器: http 请求
		服务器-->>-客户端: ETag: W/"5886c231-8d9"
	end
	rect rgba(200,200,150,.3)
		客户端->>+服务器: If-None-Match:  W/"5886c231-8d9"
		participant 服务器
		Note right of 服务器: 通过 If-None-Match 判断客户端缓存是否过期
		alt 过期
		服务器->>客户端:返回200 + 新数据
		else 没过期
		服务器->>-客户端:返回 304.
		end
	end
</div>

### Last-Modified / If-Modified-Since 策略
<div class="mermaid">
sequenceDiagram
	rect rgba(100,200,100,.4)
		客户端->>+服务器: http 请求
		服务器-->>-客户端: Last-Modified: Tue, 24 Jan 2020 02:02:02 GMT
	end
	rect rgba(200,200,150,.3)
		客户端->>+服务器: If-Modified-Since: Tue, 24 Jan 2020 02:02:02 GMT
		participant 服务器
		Note right of 服务器: 通过 If-Modified-Since 判断客户端缓存是否过期
		alt 过期
		服务器->>客户端:返回200 + 新数据
		else 没过期
		服务器->>-客户端:返回 304.
		end
	end
</div>



