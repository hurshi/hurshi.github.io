---
layout: post
title: C++入门
catalog: true
subtitle:   "C++不完全入门指南"
tags:
    - cpp
---

## 名词解释

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

##### 编译器的工作原理

传统编译器的工作原理基本上都是三段式的，可以分为前端（Frontend）、优化器（Optimizer）、后端（Backend）。前端负责解析源代码，检查语法错误，并将其翻译为抽象的语法树（Abstract Syntax Tree）。优化器对这一中间代码进行优化，试图使代码更高效。后端则负责将优化器优化后的中间代码转换为目标机器的代码，这一过程后端会最大化的利用目标机器的特殊指令，以提高代码的性能。

| 编译器        | 前端（解析） | 后端（优化，生成机器码） |
| ------------- | ------------ | ------------------------ |
| GCC 4.2       | GCC          | GCC                      |
| LLVM-GCC      | GCC          | LLVM                     |
| LLVM Compiler | Clang        | LLVM                     |

##### 比较

1. GCC vs Clang：

   1. Apple 目前使用的是 LLVM 编译器，早已摒弃 GCC；
   2. Android NDK 从 r17 开始不再支持 GCC，而使用 Clang；

2. CMake vs ndk-build：

   功能相近，CMake 更具有普遍性，所以优先使用 CMake；

## 编译

##### 使用 Clang

```shell
clang --version
clang *.cpp -lstdc++;./a.out
```

##### `#ifndef` (`if not defined`)

仅当以前没有使用与处理器编译指令`#define`定义名称 `COORDIN_H_` 时，才处理 `#ifndef` 和 `#ifend` 之间的语句。

为防止多次 `#include 'coordin.h'`的时候，下面 “something” 被多次定义；

```cpp
// C++ Primer Plus # 318
// coordin.h
#ifndef COORDIN_H_
#define COORDIN_H_

// something

#endif
```

## 指针

##### 指针与引用

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
     2. 地址只能保存在 `*x` 这样的对象中，其他比如 `int c = &a`是<font color=red>不合法</font>的（由继承带来的强制转换除外）
     3. `*b`自己也有地址，可以通过 `&b` 获取；

  * `int &`：定义了一个引用类型

     1. `int &c = a` 表示 c 共用 a 的地址，c 的值当然也是和 a 是一致的；
     2. `int &c = a` 和 `int d = a`的区别在于：
        1. d 的地址和 a 的地址是不一样的，只是它们的值是一样的而已，相当于是个拷贝；
        2. 但 c 完全等价于 a；

##### 取值与取址

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

##### 经典用法

  1. 参数传递

     ```cpp
     // 👇 使用 &value，传递的是引用，而非拷贝；
     void swap(int &val1, int &val2)
     {
     	int temp = val1;
     	val1 = val2;
     	val2 = temp;
     }
     
     // 👇 新建一个 “int 指针”类型的 val1,并把需要的地址“拷贝到 val1 的值”中：
     void swap(int *val1, int *val2);
     ```

  2. 数组

     ```cpp
     // 👇 数组作为参数传递，传的是首地址，所有会丢失“size”；因此一般需要传 size;
     void exeIntArray(int *array, int size)
     {
     	int firstEle = array[0];
     	int firstEle2 = *array;
     	int secEle = array[1];
     	// 👇 地址先往后挪一个，然后取址；结果和 array[1] 是等价的（前提是地址的连续性，所以对 数组 和 vector 是可用的，对 list 不能这么干）：
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



## 变量

##### 静态持续变量

```cpp
...
// 👇 [外部链接]性[静态]持续性变量；类比于 java 中的 `public static`
// 👇 会被默认初始化
int global = 1000; 

// 👇 内部链接性，只有当前文件能访问；类比 java 中的 `private static`
// 👇 相比于上面的‘外部链接性静态变量’，static 限制了作用域；
// 👇 会被默认初始化
static int one_file = 50; 

// 👇 效果和上述 static 一样
const int one_file2 = 100;

void func()
{
  // 👇 作用域为局部，无链接性；
  // 👇 会被初始化为默认值，函数执行完毕不会自动释放内存；
  // 👇 相比之下，static 改变了内存空间，count 被存储在‘静态存储区’
  static int count; 
  
  // 👇 内存空间在‘栈’中
  int llama = 0;
}
...
```

##### 初始化

1. 自定义类型，需要确保<font color=red>每一个</font>构造函数都将对象的<font color=red>每一个</font>成员初始化，<font color=red>即使是没有初始值</font>，这是一个好习惯；

   ```cpp
   class Point {
     int x, y; // 👈 有时候会被初始化（为 0），有时候不会。
   };
   ...
   Point p;
   ```

2. 在构造函数中初始化：

   ```cpp
   class MyClass {
   private:
     std::string name;
   }
   
   // 👎 方法1: 先初始化 name 为默认值, 然后把 _name 赋值给 name;
   MyClass::MyClass(std::string _name) {
     name = _name;
   }
   // 👍👍 方法2（推荐）:效率比上面的高 
   MyClass::MyClass(std::string _name) : name(_name) {}
   ```

   



## 关键字

##### const

1. ~~指针常量 & 常量指针~~；<font color=red>把这破名字忘了吧，它只会扰乱你</font>

   ```cpp
   int intValue = 100;
   int other = 10086;
   
   // 👇 *b = xxx 都不被允许
   const int *b = &intValue;
   // 👇 c = xxx 都不被允许
   int *const c = &intValue;
   
   *b = other; // 👈 Error
   b = &other;
   
   *c = other;
   c = &other; // 👈 Error
   ```

2. const 函数

   ```cpp
   class C {
   public:
       void func1();
       void func2() const;
   };
   
   int main() {
       const C c;
       c.func1(); // 👈 Error
       c.func2();
   }
   ```

   

## Cpp 类

##### 潜规则

1. 在类声明中定义的变量，函数默认都是 `private` 的;
2. 在类声明中定义的函数，默认为内联 `inline`函数；
2. 在构造函数中使用了 `new` ，一般来说都需要**显式**定义析构函数，复制构造函数，赋值运算符。
4. 在继承场景中，调用派生类的函数：
   1. 如果是析构函数，会自动调用基类；
   2. 如果是构造函数，如果没有指定则调用默认无参构造函数；

##### 代码示例

```cpp
// C++ Primer Plus # 370
// stock00.h
...
class Stock
{
private: // 👈 默认为 private，所以这个可以省略
    string _company;// 👈 这里是声明，不会初始化
    double _share_val;
    void set_tot()
    {
        ...
    }
public:
    // 👇 构造函数，可以添加默认参数，如果没有显式定义，会有默认无参构造函数
    Stock(const string &company = "Bob", long share_val = 20);
    Stock(Stock &stock);// 👈 ‘复制’构造函数
    Stock &operator=(const Stock &stock);// 👈 ‘赋值’构造函数
    void update(double price);
    ~Stock();// 👈 析构函数
};

// stock00.cpp
// 👇 实现构造函数
Stock::Stock(const string &company, long share_val)
{
    ...
}

void Stock::update(double price)
{
    ...
}

// 👇 实现析构函数
Stock::~Stock()
{
    cout << "bye, stock" << endl;
}
```

```cpp
int main()
{
    // 👇 和 Java 不一样，这样写就已经调用构造函数初始化了；
    Stock kate; // 调用无参构造函数（如果有的话），或者全部使用默认值
    kate.show();

    Stock s = Stock{"CompanyName"};
  
    Stock *s2 = new Stock;

    return 0;
}
```

## 虚方法

##### 经验

1. 如果要在派生类中重新定义基类的方法，通常应将基类方法声明为虚的。这样，程序将根据对象类型（而不是引用或指针的类型）来选择方法版本；
2. 为基类声明一个虚**析构函数**也是一种惯例；如果基类的析构函数是虚的，调用派生类的析构函数后会自动调用基类的析构函数；

##### 虚方法在派生类中的应用

```cpp
class Animal
{
public:
    virtual void bark();
}

class Dog
{
public:
    virtual void bark();
}

int main()
{
    Dog dog;
    dog.bark(); // 调用 Dog 类下的方法（没啥毛病）
  
    Animal & dog2 = dog;
    // 👇 如果 bark 不是虚方法，这里会调用 Animal 中的 bark 方法；
    dog2.bark(); 
}
```

##### 重载会隐藏基类中的方法

```cpp
class Parent
{
public:
    virtual void say(string msg);
    virtual void say(int i);
}

class Child
{
public:
    // 👇 会把 Parent 中的所有 say 方法隐藏掉；
    virtual void say();
}
```

##### 纯虚函数

包含“纯虚函数”的类称为“抽象类”，它不能被初始化；

```cpp
// 👇 结尾处为 ‘=0’
virtual void say() = 0;
```












### 参考

* [LLVM 与 GCC @知乎用户](https://www.zhihu.com/question/20039402/answer/67652398)
* C++ Primer Plus 
* Effective C++