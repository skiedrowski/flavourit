package com.github.skiedrowski.flavourit

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
//@Repeatable -- does not seem to work with kapt
annotation class Fork(val from: String, val to: String, val ifActive: String)

annotation class Forks(val value : Array<Fork>)