package com.github.skiedrowski.flavourit

fun main(args: Array<String>) {
    val singleFork = SingleFork()
    singleFork.delegator1()
    
    val multipleForks = MultipleForks()
    println("calling delegator1")
    multipleForks.delegator1()
    println("calling delegator2")
    multipleForks.delegator2()
}