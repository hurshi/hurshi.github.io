# Rxjava 捡漏

### 操作符

1. **Interval**: 按固定时间间隔发射整数序列，相当于定时器
2. **range**: 发射指定范围的整数序列 比如`Observable.range(10,2) 结果 10，11`
3. **concatMap**: 在 map 基础上保持数据的顺序
4. **buffer**: 将指定个数的数据包装成数据列表一次性发射
5. **groupBy**: 将数据分组排序后依次发射
6. **distinct**: 去重，只允许没有发射过的数据通过
7. **throttleFirst**: 在指定时间窗口里，只允许第一条数据通过
8. **merge/concat**: 就像多车道汇合一样，只是 merge 不保证顺序，concat 保证顺序
9. **zip**: 就像拉链一样，2个 Observable 的元素依次结合，返回新的元素。
10. **Do**:
    1. doOnEach
    2. doOnNext
    3. doOnSubscribe
    4. doOnUnsubscribe
    5. doOnCompleted
    6. doOnError
    7. doOnTerminate: 当 Observable 终止（无论是正常还是异常终止）***之前***会被调用
    8. finallyDo: 当 Observable 终止（无论是正常还是异常终止）***之后***会被调用
11. **timeout**: 超时后 onError 或使用备用的 Observable
12. **catch**: onErrorReturn...
13. **retry/retryWhen**: 遇到 Error 重试
14. **条件判断**
    1. all: 对所有数据进行判断，全部满足返回 true, 否则 false
    2. contains: 类似于 `list.contains()`
15. **toMap**: 类似于 toList



### 线程

1. **IO**: 内部实现为 无数量上限的线程池，可重用线程