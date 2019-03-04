# Shell 技巧


| 命令  | 描述  | 备注  |
| --- | --- | --- |
| 官网 | [Ubuntu 官方命令行指南](https://wiki.ubuntu.com.cn/%E5%91%BD%E4%BB%A4%E8%A1%8C%E6%8C%87%E5%8D%97) |  |
| `alt` + 鼠标左键  | 光标快速定位  |  |
| `Find`  | ` find [path][options][expression]`  |    `find . -name '*.txt'` |
| `kill pid`  | 杀掉进程 | `kill -9 pid:`强制杀掉进程<br/>`killall name:`杀掉所有name的进程 |
| `netstat`  | 查看网络状况 | `netstat -a:`查看已经链接的服务端口<br/>`netstat -ap:`查看所有的服务端口 |
| `cat /etc/issue`  | 查看`Ubuntu`版本  | `lsb_release -a:`查看更加详细的内容 |
| `scp -r local_dir username@servername:remote_dir` | 上传本地目录到服务器  |  |
|`scp -r username@servername:remote_dir local_dir`|下载服务器目录到本地||
| `ps -aux`  | 进程监控 | `-a:`显示所有用户的所有进程 <br> `-u:`按照用户名和启动事件顺序显示进程<br>`-x:`显示无控制终端的进程<br>`-l:`长格式输入<br>`-ww:`避免详细参数被截断<br>`-r:`显示运行中的进程<br>`-f:`用树形格式来显示进程<br>`-j:`用任务格式来显示进程 |
|`>`|将输出内容保存到其他|eg  `curl baidu.com > 123.txt`|


