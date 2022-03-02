---
layout: post
title: C++入门
catalog: true
subtitle:   "C++不完全入门指南"
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
> 1. 比较：
>
>       1. GCC vs Clang：
>       
>          1. Apple 目前使用的是 LLVM 编译器，早已摒弃 GCC；
>          2. Android NDK 从 r17 开始不再支持 GCC，而使用 Clang；
>       
>       2. CMake vs ndk-build：
>       
>          功能相近，CMake 更具有普遍性，所以优先使用 CMake；
>

## 编译

1. 使用 Clang 编译运行：

   ```shell
   clang --version
   clang *.cpp -lstdc++;./a.out
   ```

## 指针

* 指针与引用

  ```cpp
  int a = 1;
  int *b = &a;
  int &c = a;
  int d = a;
  
  cout << "a:" << a << "," << &a << endl;
  cout << "b:" << b << "," << &b << endl;
  cout << "c:" << c << "," << &c << endl;
  cout << "d:" << d << "," << &d << endl;
  
  //输出
  a:1,0x7ffeee1a2378
  b:0x7ffeee1a2378,0x7ffeee1a2370
  c:1,0x7ffeee1a2378
  d:1,0x7ffeee1a2364
  ```

  * pointer & reference：

     ![](/img/posts/cpp_induction/pointer.png){:width="70%"}

  * `int *`: 定义一个指针类型：

     1. 它的值只能存地址，不能存其他的东西；
     2. 地址只能保存在 `*x` 这样的对象中，其他比如 `int c = &a`是<font color=red>不合法</font>的
     3. `*b`自己也有地址，可以通过 `&b` 获取；

  * `int &`：定义了一个引用类型

     1. `int &c = a` 表示 c 共用 a 的地址，c 的值当然也是和 a 是一致的；
     2. `int &c = a` 和 `int d = a`的区别在于：
        1. d 的地址和 a 的地址是不一样的，只是它们的值是一样的而已，相当于是个拷贝；
        2. 但 c 完全等价于 a；

* 取值与取址

  ```cpp
  int a = 10086;
  // 取址：
  int *b = &a;
  // 取值：
  int c = *b;
  
  cout << "a:" << a << endl;
  cout << "b:" << b << endl;
  cout << "c:" << c << endl;
  
  // 输出：
  a:10086
  b:0x7ffeef8b33d8
  c:10086
  ```

* 经典用法：

  1. 参数传递

     ```cpp
     // 使用 &value，传递的是引用，而非拷贝；
     void swap(int &val1, int &val2)
     {
     	int temp = val1;
     	val1 = val2;
     	val2 = temp;
     }
     
     // 新建一个 “int 指针”类型的 val1,并把需要的地址“拷贝到 val1 的值”中：
     void swap(int *val1, int *val2);
     ```

  2. 数组

     ```cpp
     // 数组作为参数传递，传的是首地址，所有会丢失“size”；因此一般需要传 size;
     void exeIntArray(int *array, int size)
     {
     	int firstEle = array[0];
     	int firstEle2 = *array;
     	int secEle = array[1];
       // 地址先往后挪一个，然后取址；结果和 array[1] 是等价的（前提是地址的连续性，所以对 数组 和 vector 是可用的，对 list 不能这么干）：
     	int secEle2 = *(array + 1); 
     
     	cout << "firstEle : " << firstEle << endl;
     	cout << "firstEle2: " << firstEle2 << endl;
     	cout << "secEle : " << secEle << endl;
     	cout << "secEle2: " << secEle2 << endl;
     }
     
     int main()
     {
     	int intArray[] = {1, 2, 3, 4};
     	exeIntArray(intArray, 4);
     }
     
     // 输出：
     firstEle : 1
     firstEle2: 1
     secEle : 2
     secEle2: 2
     ```

     

## 作用域

1. file scope & local scope：

   1. file scope ：会默认初始化；

   2. local scope：默认不会初始化；

      ```cpp
      #include <iostream>
      
      int fileScope; // 会默认初始化为0；
      int main()
      {
      	using namespace std;
      	int localScope; // 不会初始化，为野指针；
      	cout << fileScope << endl;
      	cout << localScope << endl;
      	return 0;
      }
      
      // 输出：
      0
      248037413
      ```

      




### 参考

[LLVM 与 GCC @知乎用户](https://www.zhihu.com/question/20039402/answer/67652398)