---
layout: post
title: C++入门
catalog: true
tags:
    - cpp
---

## 名词解释：

| 名称                                         | 全称                      | 说明                                                         |
| -------------------------------------------- | ------------------------- | ------------------------------------------------------------ |
| GNU                                          | *GNU*'s Not Unix          | GNU 指的是**自由软件**，自由软件意味着使用者有运行、复制、发布、研究、修改和改进该软件的自由。 |
| GCC                                          | GNU Compiler Collection   | 即 GNU 编译器集合，可以编译C，C++，Java等语言；              |
| gcc                                          |                           | GCC 中的 GNU C Compiler，特指C编译器                         |
| g++                                          |                           | GCC 中的 GNU C++ Compiler，特指 C++ 编译器                   |
| LLVM                                         | Low Level Virtual Machine | LLVM是编译器基础架构（负责优化，），可以作为多种语言编译器的后台来使用； |
| Clang                                        |                           | 支持C，C++和Objective-C的编译器，是基于 LLVM 开发的（后台使用 LLVM） |
| [CMake](https://www.hahack.com/codes/cmake/) |                           | 高级编译配置工具，用来定制整个编译流程<br>1. 编写 CMake 配置文件 CMakeLists.txt <br>2. 执行 CMakeLists.txt 生成 makefile（平台相关）<br>3. 使用 make 命令，配合 makefile 编译代码 |
| ndk-build                                    |                           | 与 CMake 类似，也是系统构建工具；                            |

> 1. 传统编译器的工作原理：
>
>    传统编译器的工作原理基本上都是三段式的，可以分为前端（Frontend）、优化器（Optimizer）、后端（Backend）。前端负责解析源代码，检查语法错误，并将其翻译为抽象的语法树（Abstract Syntax Tree）。优化器对这一中间代码进行优化，试图使代码更高效。后端则负责将优化器优化后的中间代码转换为目标机器的代码，这一过程后端会最大化的利用目标机器的特殊指令，以提高代码的性能。
>
>    | 编译器        | 前端（解析） | 后端（优化，生成机器码） |
>    | ------------- | ------------ | ------------------------ |
>    | GCC 4.2       | GCC          | GCC                      |
>    | LLVM-GCC      | GCC          | LLVM                     |
>    | LLVM Compiler | Clang        | LLVM                     |
>
> 1. 现状：
>
>       1. Apple 目前使用的是 LLVM 编译器，早已摒弃 GCC；
>   1. Android NDK 从 r17 开始不再支持 GCC，而使用 Clang；
>    
>

## 编译

1. 使用 Clang 编译运行：

   ```shell
   clang --version
   clang *.cpp -lstdc++;./a.out
   ```

   







### 参考

[LLVM 与 GCC @知乎用户](https://www.zhihu.com/question/20039402/answer/67652398)