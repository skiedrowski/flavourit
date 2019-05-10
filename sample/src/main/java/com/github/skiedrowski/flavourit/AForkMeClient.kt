package com.github.skiedrowski.flavourit

fun main(args: Array<String>) {
    val singleFork = SingleFork()
    singleFork.delegator1()
    
    val multipleForks = MultipleForks()
    multipleForks.delegator1()
    multipleForks.delegator2()
}