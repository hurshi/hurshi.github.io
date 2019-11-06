---
layout: post
title: 笔记 kotlin
catalog: true
tags:
	- notes
---

1. 内联函数：一般在比较小的方法上，以及频繁使用的方法上使用，而不会随意使用。

2. 访问修饰符：internal ==> 模块内可访问修饰符，但在 java 中，会被直接当作 public 使用的。

   黑科技：

   ```kotlin
   // 在kotlin中是合法的
   fun `1234`(){//是无法兼容 Java 的
       println("function 1234")
   }
   
   //在 kotlin 中可以调用
   fun main(args: Array<String>) {
       `1234`()
   }
   
   // 在Java中不能调用
   public void test() {
   	1234();//ERROR: 不合法调用
   	`1234`();//ERROR: 不合法调用
   }
   ```

3. 单例：除了 object 来声明单例外，还可以使用伴生对象来实现：

   ```kotlin
   class Single private constructor() {
   	companion object {
   		fun get(): Single {
   			return Holder.instance
   		}
   	}
   	private object Holder {
   		val instance = Single()
   	}
   }
   
   fun main(args: Array<String>) {
   	val single = Single.get()
   }
   ```

   伴生对象，可以理解为 匿名内部单例。

4. 动态代理：使用 by 关键字就可以实现，参考：[极客时间 -- Kotlin](https://time.geekbang.org/course/detail/105-10673)。 Kotlin的动态代理会在编译以后转换成静态代理去调用，所以比Java通过反射的方式效率要高。

   ❓究竟何为动态代理，如何使用Kotlin动态代理实现 Retrofit❓

5. ❗️❗️❗️密闭类完全可以代替枚举类，并且实现***更多的扩展***，比如

   ```kotlin
   sealed class SuperCommand {
       object UP : SuperCommand()
       object LEFT : SuperCommand()
       object RIGHT : SuperCommand()
       object DOWN : SuperCommand()
       class SPEED(var speed: Int) : SuperCommand()
   }
   
   fun exec(tank: Tank, superCommand: SuperCommand) {
       return when (superCommand) {
           SuperCommand.UP -> tank.turnUp()
           SuperCommand.LEFT -> tank.turnLeft()
           SuperCommand.RIGHT -> tank.turnRight()
           SuperCommand.DOWN -> tank.turnDown()
           is SuperCommand.SPEED -> tank.speed(superCommand.speed)
       }
   }
   ```

6. 解构：自动将一个对象拆解成若干个变量分别赋值

   ```kotlin
   class User(private val name: String, private val age: Int) {
       operator fun component1() = age
       operator fun component2() = name
   }
   
   fun main(args: Array<String>) {
       val user = User("Hurshi", 18)
       val (name, age) = user
       println("name = $name")
       println("age = $age")
   }
   
   //常用场景:
   fun main(args: Array<String>) {
       val map = mapOf<String,String>("name" to "Hurshi","age" to "18")
       for((key,value) in map){
           println("key = $key ,value = $value")
       }
   }
   ```

7. 循环：

   ```kotlin
   for(i in 1..10) { println(i) }
   for(i in 1 until 10) { println(i) }
   for(i in 10 downTo 1) { println(i) }
   for(i in 1..10 step 2) { println(i) }
   repeat(10) { println(it) }
   
   //循环 list with index
   list.forEachIndexed { index, value -> println("[$index,$value]") }
   for ((index, value) in list.withIndex()) {
     println("[$index,$value]")
   }
   ```

8. 作用域函数

   ```kotlin
   val letResult: String = user.let { it.name }
   val runResult: String = user.run { this.name }
   
   val alsoResult: User = user.also { it.name }
   val applyResult: User = user.apply { this.name }
   
   user.takeIf { it.name.isNotEmpty() }?.also { println(it.name) }
   
   with(user){
     this.name = "haha"
     this.age = 18
     this.phone = 111111
   }
   // with 可以替换为：
   // user.let { 
   //   it.name = "haha"
   //   it.age = 18
   //   it.phone = 111111
   // }
   ```

9. 中缀表达式（扩展函数）

   使用 `infix` 来扩展函数，比如自定义一个 vs 的中缀表达式：

   ```kotlin
   infix fun Int.vs(num:Int):Int{
       return if(this<num)-1 else if(this>num) 1 else 0
   }
   
   fun main(args: Array<String>) {
     println(100 vs 90)
   }
   ```

10. `javap [option] *.class` 命令，反编译一个 class 文件，比如：

  ```shell
  javap -c main.class
  ```

11. val 变量并不是常量，比如：

    ```kotlin
    class Person(val birthYear: Int) {
        var currentYear = 2019
        val age: Int
            get() = currentYear - birthYear
    }
    
    fun main(args: Array<String>) {
        val person = Person(1990)
        println(person.age)
        person.currentYear = 2020
        println(person.age)
    }
    ```

    如何真正的声明一个常量？ 

    ```kotlin
    const val a = 0 //const 变量的值必须在编译期间确定下来
    ```

12. 空安全：

    1. 局部变量可以通过上下文推断，来避免多次判空。

13. 内联函数

    1. 内联函数可以中断外部调用的。

       ```kotlin
       fun test() {
           inlineTest {
               println("hello1")
               return
             	println("hello2")
           }
           println("hello3")
       }
       
       private inline fun inlineTest(l: () -> Unit) {
           l.invoke()
           return
       }
       
       // 输出： 
       // hello1
       ```

    2. 使用 `crossinline`不允许 inline 的 Lambda 中断外部函数的执行。

       ```kotlin
       fun test() {
           inlineTest {
               println("hello1")
               return@inlineTest
               println("hello2")
           }
           println("hello3")
       }
       
       private inline fun inlineTest(crossinline l: () -> Unit) {
           l.invoke()
           return
       }
       //输出：
       //hello1
       //hello3
       ```

    3. 使用 `noinline` 拒绝内联。

14. 泛型：

    1. 可以指定多个约束条件，比如

       ```kotlin
       //要求 T 同时实现了 Callback 和 Runnable 接口
       class Test<T> where T  : Callback, T : Runnable {}
       ```

    2. 真泛型：

       ```java
       // java 的 fromJson:
       public <T> T fromJson(String json, Class<T> classOfT) throw Json...Exception{}
       ```

       ```kotlin
       // Kotlin 的 fromJson:
       inline fun <reified T> Gson.fromJson(json: String): T {
       	return fromJson(json, T::class.java)
       }
       ```
       
    3. `out / in`：相当于 java 中的 `<? extends Class>` / `<? super Class>`，理解为：

       1. out：只读不可写。
       2. in：只写不可读。

    4. 类型擦除/类型安全 ？？？

    5. 补充知识：在 Java 中，使用 `? extends`定义的比如`List<? extends ClassA> list`是不能被修改的，理解为只读。而 `? super`则是只能写入而不能读。

15. 协程

    1. 挂起函数 suspend ：非阻塞式的挂起。相当于到新线程去执行任务了。
    2. `runBlocking {}` 是 阻塞的
    3.  `withContext {}`可以切到指定线程去执行代码，结束后再切回来
    4. Channel：用于2个协程之间的通信。一般可以使用 produce 来使用。

16. BIO NIO：

    1. Blocking IO / Non-blocking IO
    2. Kotlin 在对'流'操作有语法糖 -- use, 使用者不用写那么多的 try catch，而且不用管 流的close。
    3. 对象缓存池：DefaultPool，缓存对象。

17. KTX 扩展库: https://developer.android.com/kotlin/ktx

    1. 对 LinearLayout  遍历子 View：`linearlayout.foreach { }`
    2. 判断字符串是否只包含数字：`"12343.22333.223".isDigistsOnly()`

18. 修改 Kotlin 类名：

    1. `@file:JvmName("name")`：指定类名。
    2. `@file:JvmMultifileClass`：当类名冲突的时候，会合并为一个 class。

19. 其他

